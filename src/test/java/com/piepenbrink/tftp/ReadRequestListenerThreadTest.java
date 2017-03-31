package com.piepenbrink.tftp;

import org.junit.Test;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Exercise the request listener thread
 */
public class ReadRequestListenerThreadTest
{
    @Test
    public void runRequestListenerTestRrqMessage() throws Exception
    {
        Runnable runnable = new ReadRequestListenerThread( "0.0.0.0", 8080, new File( "./" ) );
        runnable.run();

        // send just the RRQ packet to exercise it
        DatagramSocket testSocket = new DatagramSocket( 8080, InetAddress.getByName( "127.0.0.1" ) );
    }
}