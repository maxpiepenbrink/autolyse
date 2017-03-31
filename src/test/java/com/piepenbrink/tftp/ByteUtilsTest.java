package com.piepenbrink.tftp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Exercise the bitwise utils
 */
public class ByteUtilsTest
{
    @Test
    public void getShortFromBuffer() throws Exception
    {
        int valueTest = 40;
        // least significant byte first in the array
        byte[] test = new byte[]{(byte) ( valueTest >> 1 ), (byte) valueTest};

        assertEquals( valueTest, ByteUtils.getUnsignedShortFromBuffer( test ) );
    }

}