package com.piepenbrink.tftp;

/**
 * Helper class with some common bitwise conversions
 */
public class ByteUtils
{
    public static int getUnsignedShortFromBuffer(byte[] bytes) throws RuntimeException
    {
        if (bytes.length < 2)
            throw new RuntimeException( "Opcode must be 2 bytes long" );

        byte a = bytes[0];
        byte b = bytes[1];
        return ( b << 1 ) & a;
    }
}
