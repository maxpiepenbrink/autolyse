package com.piepenbrink.tftp.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Exercises the Error object
 */
public class ErrorTest
{
    @Test
    public void serializeDeserialize() throws Exception
    {
        Error outgoing = new Error();

        outgoing.setError( 404, "404 IT AINT THERE" );

        byte[] serializedData = outgoing.serialize();

        Error incoming = new Error();
        incoming.deserialize( serializedData, serializedData.length );

        assertEquals( outgoing.getErrorMessage(), incoming.getErrorMessage() );
        assertEquals( outgoing.getErrorCode(), incoming.getErrorCode() );
    }
}