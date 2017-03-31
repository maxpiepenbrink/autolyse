package com.piepenbrink.tftp.protocol;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises the DATA packet type
 */
public class DataTest
{
    @Test
    public void serializeDeserialize() throws Exception
    {
        File importantDocument = new File( "samples/extremely-important-document.mp4" );

        FileInputStream testFile = new FileInputStream( importantDocument );

        // load a dummy payload and construct a DATA object with it
        byte[] testPayload = new byte[512];
        int bytesRead = testFile.read( testPayload, 0, 512 );
        testFile.close();
        assertEquals( 512, bytesRead );
        assertEquals( 512, testPayload.length );

        Data outgoing = new Data();
        outgoing.setPayload( testPayload, 1 );

        byte[] serializedData = outgoing.serialize();

        Data incoming = new Data();
        incoming.deserialize( serializedData, serializedData.length );

        byte[] dataA = outgoing.getPayload();
        byte[] dataB = incoming.getPayload();

        assertEquals( dataA.length, dataB.length );
        for (int i = 0; i < dataA.length; i++)
        {
            assertEquals( dataA[i], dataB[i] );
        }
    }

    @Test
    public void isFinalPacket() throws Exception
    {
        // almost exactly the same test as bove but we intentinoaly read 1 less byte
        File importantDocument = new File( "samples/extremely-important-document.mp4" );

        FileInputStream testFile = new FileInputStream( importantDocument );

        // load a dummy payload and construct a DATA object with it
        byte[] testPayload = new byte[511];
        int bytesRead = testFile.read( testPayload, 0, 511 );
        testFile.close();
        assertEquals( 511, bytesRead );
        assertEquals( 511, testPayload.length );

        Data outgoing = new Data();
        outgoing.setPayload( testPayload, 1 );

        byte[] serializedData = outgoing.serialize();

        Data incoming = new Data();
        incoming.deserialize( serializedData, serializedData.length );

        byte[] dataA = outgoing.getPayload();
        byte[] dataB = incoming.getPayload();

        assertEquals( dataA.length, dataB.length );
        for (int i = 0; i < dataA.length; i++)
        {
            assertEquals( dataA[i], dataB[i] );
        }

        assertTrue( outgoing.isFinalPacket() );
        assertTrue( incoming.isFinalPacket() );
    }
}