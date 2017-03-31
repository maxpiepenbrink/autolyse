package com.piepenbrink.tftp;

/**
 * This enum contains a collection of supported packet types in the TFTP protocol as defined in <a href="https://www.ietf.org/rfc/rfc1350.txt">RFC1350</a>.
 */
public enum PacketType
{
    RRQ( 0x01 ),
    WRQ( 0x02 ),
    DATA( 0x03 ),
    ACK( 0x04 ),
    ERROR( 0x05 ),
    UNKNOWN( 0xFF );

    public final byte opCode;

    PacketType(int opCode)
    {
        this.opCode = (byte) opCode;
    }

    public static PacketType fromOpCode(byte opCode)
    {
        //TODO: enums might not be best here but I like maintaining them a lot more in general for this sort of exercise
        if (opCode == RRQ.opCode)
            return RRQ;
        else if (opCode == DATA.opCode)
            return DATA;
        else if (opCode == ACK.opCode)
            return ACK;
        else if (opCode == WRQ.opCode)
            return WRQ;
        else if (opCode == ERROR.opCode)
            return ERROR;

        // always return special dummy by default
        return UNKNOWN;
    }
}
