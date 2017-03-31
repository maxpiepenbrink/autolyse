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
        ReadRequest request = new ReadRequest();

        request.setState( "a", "octet" );
        byte[] data = request.serialize();

        ReadRequest newRequest = new ReadRequest();
        newRequest.deserialize( data, data.length );

        assertEquals( request.targetFile, newRequest.targetFile );
        assertEquals( request.fileMode, newRequest.fileMode );
    }

}