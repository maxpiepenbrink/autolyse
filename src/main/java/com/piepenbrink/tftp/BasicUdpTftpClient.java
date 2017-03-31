package com.piepenbrink.tftp;

import com.piepenbrink.tftp.protocol.Acknowledge;
import com.piepenbrink.tftp.protocol.Data;
import com.piepenbrink.tftp.protocol.ReadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Basic client that fetches a file from a remote
 */
public class BasicUdpTftpClient
{
    public static final int MAX_DATAGRAM_BUFFER = 1024;
    public static final int SOCKET_TIMEOUT = 0 /* temporarily wait forever */; // 2 second timeout

    private static final Logger logger = Logger.getLogger( "UDP TFTP Client" );

    private String remoteFile;
    private InetAddress targetAddress;
    private int targetPort;
    private boolean hasUpdatedPort = false;
    private File downloadDirectory;

    public BasicUdpTftpClient(String remoteFile, String targetAddress, int targetPort, String downloadDirectory) throws UnknownHostException
    {
        this( remoteFile, targetAddress, targetPort, new File( downloadDirectory ) );
    }

    public BasicUdpTftpClient(String remoteFile, String targetAddress, int targetPort, File downloadDirectory) throws UnknownHostException
    {
        this.remoteFile = remoteFile;
        this.targetAddress = InetAddress.getByName( targetAddress );
        this.targetPort = targetPort;
        this.downloadDirectory = downloadDirectory;

        if (!downloadDirectory.exists() && !downloadDirectory.mkdirs())
            throw new IllegalStateException( "Couldn't create or access download folder!" );

        if (!downloadDirectory.isDirectory())
            throw new IllegalStateException( "File directory must be a directory" );
    }

    public void start()
    {
        //TODO: this method could be factored into a multitude of components but reads ok flat like this just barely
        DatagramSocket socket = null;
        int nextExpectedBlock = 1;
        try
        {
            logger.info( "Connecting to " + targetAddress + ":" + targetPort + " to get file: " + remoteFile );
            // get a handle to our output directory
            File outputFile = new File( downloadDirectory, remoteFile );
            FileOutputStream fileWriter = new FileOutputStream( outputFile, false );

            // configure our main socket
            socket = new DatagramSocket(); // listen on a random port
            socket.setSoTimeout( SOCKET_TIMEOUT );

            // configure the read request
            ReadRequest readRequest = new ReadRequest();
            readRequest.setState( remoteFile, "octet" );

            {
                // serialize the request and send it
                byte[] data = readRequest.serialize();
                DatagramPacket requestPacket = new DatagramPacket( data, data.length, targetAddress, targetPort );
                logger.info( "Sent file request packet to " + requestPacket.getAddress().toString() );
                socket.send( requestPacket );
            }

            // have the socket loop and wait
            byte[] buffer = new byte[MAX_DATAGRAM_BUFFER];
            while (true)
            {
                // create a new packet
                DatagramPacket packet = new DatagramPacket( buffer, buffer.length );

                // block on the server's response
                socket.receive( packet );

                // we're expecting RRQs only right now so we simply check if it is one
                byte[] incomingData = packet.getData();

                PacketType type = PacketType.fromOpCode( ByteUtils.getUnsignedShortFromBuffer( incomingData ) );

                if (PacketType.DATA.equals( type ))
                {
                    // deserialize the packet into a Data object
                    Data deserializedData = new Data();
                    deserializedData.deserialize( incomingData, packet.getLength() );

                    // if it's not valid we want to abort without doing anything extra
                    if (!deserializedData.isValid())
                    {
                        logger.severe( "Closing socket and quitting due to unknown error in data packet" );
                        break;
                    }

                    if (deserializedData.getBlockNumber() != nextExpectedBlock)
                    {
                        //TODO: this should send an error back to whoever sent it but I'm out of time, for now it will abort
                        logger.severe( "Error behavior not implemented, quitting server instead" );
                        break;
                    }

                    // if we got to this point we have valid sounding data and the block ordering is good so lets
                    // write to the buffered writer and then send our ACK
                    fileWriter.write( deserializedData.getPayload() );
                    fileWriter.flush();

                    // if the packet appears to be the final one we should quit
                    if (deserializedData.isFinalPacket())
                    {
                        logger.info( "Done receiving data. Quitting client." );
                        break;
                    }

                    // now that we have a fully valid context for this session we can update the port we're
                    // going to use to finish this communication with the other thread on the server.
                    if (!hasUpdatedPort)
                        targetPort = packet.getPort();

                    byte[] sendData = createAcknowledgePacket( nextExpectedBlock );
                    socket.send( new DatagramPacket( sendData, sendData.length, targetAddress, targetPort ) );

                    nextExpectedBlock++; // increment the next block we expect
                } else
                {
                    logger.severe( "Closing socket and quitting client request due to unexpected packet type: " + type.toString() );
                    break;
                }
            }
        } catch (IOException e)
        {
            logger.severe( "Closing prematurely due to IOException." );
            e.printStackTrace();
            return; // do nothing!
        } finally
        {
            if (socket != null)
                socket.close();
        }
    }

    //TODO: these kinds of utilities could live in the concrete impls themselves
    private byte[] createAcknowledgePacket(int blockNumber) throws IOException
    {
        Acknowledge ack = new Acknowledge();
        ack.setBlockNumber( blockNumber );

        return ack.serialize();
    }
}
