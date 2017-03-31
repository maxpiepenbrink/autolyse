package com.piepenbrink.tftp;


import com.piepenbrink.tftp.protocol.ReadRequest;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

/**
 * This thread's job is to listen for RRQ packets and then spawn a reader thread that will perform the
 * loop of reading the file and waiting for acknowledgements.
 */
public class ReadRequestListenerThread implements Runnable
{
    private static final Logger logger = Logger.getLogger( "Main listener thread" );
    public static final int MAX_DATAGRAM_BUFFER_PER_THREAD = 1024;

    private final String listeningIp;
    private final int listeningPort;
    private final File servingDirectory;
    private DatagramSocket socket;

    public ReadRequestListenerThread(String listeningIp, int listeningPort, File servingDirectory) throws RuntimeException
    {
        this.listeningIp = listeningIp;
        this.listeningPort = listeningPort;
        this.servingDirectory = servingDirectory;

        try
        {
            socket = new DatagramSocket( listeningPort, InetAddress.getByName( listeningIp ) );
        } catch (UnknownHostException | SocketException e)
        {
            throw new RuntimeException( "Network exception when creating listening thread!", e );
        }
    }

    @Override
    public void run()
    {
        logger.info( "Main TFTP listener thread starting.\n"
                + " - Listening on " + listeningIp + ":" + listeningPort + "\n"
                + " - Serving directory " + servingDirectory.getAbsolutePath() );
        boolean keepRunning = true;
        while (keepRunning)
        {
            try
            {
                byte[] buffer = new byte[MAX_DATAGRAM_BUFFER_PER_THREAD];
                DatagramPacket packet = new DatagramPacket( buffer, buffer.length );

                // block on this receive, waiting for anything
                socket.receive( packet );

                // we're expecting RRQs only right now so we simply check if it is one
                byte[] incomingData = packet.getData();

                // if we have data then pull the first byte out and get the PacketType of that byte
                if (incomingData.length > 0)
                {
                    // attempt to derive what kind of packet it is
                    PacketType packetType = PacketType.fromOpCode( incomingData[0] );

                    // we got a RRQ packet!
                    if (PacketType.RRQ.equals( packetType ))
                    {
                        logger.info( "Received RRQ message from " + packet.getAddress() );
                        ReadRequest readRequest = new ReadRequest();
                        readRequest.deserialize( incomingData, packet.getLength() );
                    } else if (PacketType.ERROR.equals( packetType ))
                    {
                        logger.warning( "Received ERROR packet from " + packet.getAddress() );
                    }
                }

            } catch (IOException e)
            {
                // send ERR packet but keep looping
            }
        }
    }
}
