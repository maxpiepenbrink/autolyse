package com.piepenbrink.tftp.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Exercise the deserialization behavior on this object
 */
public class ReadRequestTest
{
    /**
     * This simulates a transmission and reception by serializing and deserializing the packet. They should be the
     * exact same in this case.
     */
    @Test
    public void serializeAndDeserialize() throws Exception
    {
        ReadRequest outgoing = new ReadRequest();

        outgoing.setState( "hire-me.txt", "octet" );
        byte[] data = outgoing.serialize();

        ReadRequest incoming = new ReadRequest();
        incoming.deserialize( data, data.length );

        assertEquals( outgoing.targetFile, incoming.targetFile );
        assertEquals( outgoing.fileMode, incoming.fileMode );
    }

}