package com.piepenbrink.tftp;

import com.piepenbrink.tftp.protocol.Acknowledge;
import com.piepenbrink.tftp.protocol.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * This thread serves a file until completion or error
 * TODO: if I were doing this right I'd probably be implementing the entire Thread class and using an ExecutorPool or some scheme therein
 */
public class OctetFileRequestThread implements Runnable
{
    public static final int MAX_DATAGRAM_BUFFER_PER_THREAD = 1024;
    public static final int MAX_TIMEOUT_MS = 5000; // 5s
    private Logger logger;
    public final static int MAX_TFTP_MTU = 512;
    public final static int PACKET_BUFF = 1024;
    private File targetFile;
    InetAddress remoteHost;
    int remotePort;
    int currentBlock = 1;
    DatagramSocket socket;

    public OctetFileRequestThread(File file, InetAddress remoteHost, int remotePort) throws IOException
    {
        // spawn new socket on random port
        socket = new DatagramSocket();
        socket.setSoTimeout( MAX_TIMEOUT_MS );
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.targetFile = file;

        logger = Logger.getLogger( "File request thread for " + remoteHost.toString() + " : " + remotePort );
    }

    @Override
    public void run()
    {
        try
        {
            logger.info( "Spawning file request thread" );
            InputStream inputStream = new FileInputStream( targetFile );
            byte[] buffer = new byte[MAX_DATAGRAM_BUFFER_PER_THREAD];

            while (inputStream.available() > 0)
            {
                // read 512 bytes from the file
                int bytesRead = inputStream.read( buffer, 0, Math.min( MAX_TFTP_MTU, inputStream.available() ) );

                // build our Data object to send
                Data sendData = new Data();
                sendData.setPayload( buffer, bytesRead, currentBlock );
                byte[] packetData = sendData.serialize();
                socket.send( new DatagramPacket( packetData, packetData.length, remoteHost, remotePort ) );

                // quit if we're done
                if (sendData.isFinalPacket())
                {
                    logger.info( "Sent final packet, finishing." );
                    break;
                }

                // we're not done so we should wait for the ACK from that last block we sent
                DatagramPacket receivePacket = new DatagramPacket( buffer, MAX_DATAGRAM_BUFFER_PER_THREAD );
                socket.receive( receivePacket );

                packetData = receivePacket.getData();
                PacketType type = PacketType.fromOpCode( ByteUtils.getUnsignedShortFromBuffer( packetData ) );
                if (PacketType.ACK.equals( type ))
                {
                    // we got an ACK let's make sure it's the right block
                    Acknowledge ack = new Acknowledge();
                    ack.deserialize( packetData, receivePacket.getLength() );
                    if (ack.getBlockNumber() != currentBlock)
                    {
                        logger.warning( "Got out of sequence block ACK, doing nothing instead of sending errors like the spec asks for" );
                    }
                } //TODO: error/bad state handling here

                currentBlock++;
            }
            inputStream.close();
        } catch (IOException e)
        {

        }
    }
}
