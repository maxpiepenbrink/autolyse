package com.piepenbrink.tftp.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AcknowledgeTest
{

    @Test
    public void serializeDeserialize() throws Exception
    {
        Acknowledge outgoing = new Acknowledge();
        outgoing.setBlockNumber( 219 );

        byte[] serializedData = outgoing.serialize();

        Acknowledge incoming = new Acknowledge();
        incoming.deserialize( serializedData, serializedData.length );

        assertEquals( outgoing.getBlockNumber(), incoming.getBlockNumber() );
    }
}