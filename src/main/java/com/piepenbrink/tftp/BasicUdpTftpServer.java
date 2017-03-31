package com.piepenbrink.tftp;

import java.io.File;
import java.util.logging.Logger;

/**
 * Basic UDP TFTP file server, listens on the specified port and serves files stemming from the directory specified.
 */
public class BasicUdpTftpServer
{
    private static final Logger logger = Logger.getLogger( "UDP TFTP Server" );
    public static final int DEFAULT_LISTEN_PORT = 8080/*69*/; // default should be 69 but MacOS by design makes non-root processes need special permissions
    public static final String DEFAULT_LISTEN_IP = "127.0.0.1";
    Runnable primaryListenerThread = null;

    public BasicUdpTftpServer()
    {
        this( DEFAULT_LISTEN_IP, DEFAULT_LISTEN_PORT, new File( "./" ) );
    }

    public BasicUdpTftpServer(final String localIp, final int localPort, final String servingDirectory)
    {
        this( localIp, localPort, new File( servingDirectory ) );
    }

    public BasicUdpTftpServer(final String localIp, final int localPort, final File servingDirectory)
    {
        if (!servingDirectory.isDirectory())
            throw new RuntimeException( "Can only serve directories! Was given a non-directory file argument." );

        // spawn the thread that will listen for RRQs
        primaryListenerThread = new ReadRequestListenerThread( localIp, localPort, servingDirectory );
    }

    /**
     * Launches the primary listener thread and busy waits & sleeps until an exit or kill command is given
     */
    public void startServer()
    {
        logger.info( "Starting listening thread." );
        primaryListenerThread.run();
    }
}
