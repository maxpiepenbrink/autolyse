package com.piepenbrink.tftp;

import org.junit.Test;

/**
 *
 */
public class BasicUdpTftpServerTest
{
    /**
     * Smoke test basic instantiation with default params
     */
    @Test
    public void testCreation() throws Exception
    {
        BasicUdpTftpServer server = new BasicUdpTftpServer();
    }

    /**
     * Make sure the server wont spawn with a clearly invalid file.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidFileCreation() throws Exception
    {
        BasicUdpTftpServer server = new BasicUdpTftpServer( "777.777.888.888", -1, "./notrealfile.txt" );
    }

}