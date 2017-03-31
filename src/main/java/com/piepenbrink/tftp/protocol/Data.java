package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.*;
import java.util.Arrays;

/**
 * The DATA packet from RFC1350
 * <pre>
 *  2 bytes     2 bytes      n bytes
 * ----------------------------------
 * | Opcode |   Block #  |   Data     |
 * ----------------------------------
 *
 * Figure 5-2: DATA packet
 * </pre>
 */
public class Data implements TftpDatagram
{
    public static final int TFTP_MTU = 512;
    private boolean isValid = false;
    private int blockNumber = -1;

    private byte[] payload;

    public void setPayload(byte[] newPayload, int blockNumber)
    {
        if (newPayload.length > TFTP_MTU || newPayload.length == 0)
            throw new IllegalStateException( "Invalid payload size, must be smaller than 512 and greater than 0" );

        payload = newPayload;
        this.blockNumber = blockNumber;

        // set our valid to true in this case
        isValid = true;
    }

    public int getBlockNumber()
    {
        return blockNumber;
    }

    public byte[] getPayload()
    {
        if (!isValid())
            throw new IllegalStateException( "getPayload() called on invalid Data object!" );
        return payload;
    }

    public int getPayloadLength()
    {
        if (!isValid())
            throw new IllegalStateException( "getPayloadLength() called on invalid Data object!" );
        return payload.length;
    }

    public boolean isFinalPacket()
    {
        if (!isValid())
            throw new RuntimeException( "isFinalPacket() called on Data object that isn't valid." );
        // the spec calls for treating the packet as the final one if it's length is less than the MTU in this protocol
        return payload.length < TFTP_MTU;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        if (isValid != data.isValid) return false;
        if (blockNumber != data.blockNumber) return false;
        return Arrays.equals( payload, data.payload );
    }

    @Override
    public int hashCode()
    {
        int result = ( isValid ? 1 : 0 );
        result = 31 * result + blockNumber;
        result = 31 * result + Arrays.hashCode( payload );
        return result;
    }

    @Override
    public byte[] serialize() throws RuntimeException, IOException
    {
        // the compiler optimizes basic algebra like this and I think it's expressive and somewhat clear.
        // this is how we calculate the total size the output stream needs to buffer
        int padding = 2 + 2; // +2 for opcode, +2 for block #
        int totalSize = TFTP_MTU + padding;

        // set up some buffers to make this easier on ourselves
        ByteArrayOutputStream buffer = new ByteArrayOutputStream( totalSize );
        DataOutputStream outputStream = new DataOutputStream( buffer );

        // write our fields to the buffer
        outputStream.writeShort( PacketType.DATA.opCode );  // write the opcode
        outputStream.writeShort( blockNumber );             // write the block #
        outputStream.write( payload, 0, payload.length );   // write the payload data

        outputStream.close();
        return buffer.toByteArray();
    }

    @Override
    public void deserialize(byte[] data, int length) throws RuntimeException, IOException
    {
        // set up a stream reader
        DataInputStream inputStream = new DataInputStream( new ByteArrayInputStream( data, 0, length ) );

        // unpack the first two bytes and make sure we have a good opcode before we do anything
        int opcode = inputStream.readShort();

        // validate that the opcode is the correct opcode before anything
        if (opcode != PacketType.DATA.opCode)
            throw new RuntimeException( "Attempt to deserialize a Data (DATA) packet with data that doesn't have a DATA opcode" );

        // read the block #
        blockNumber = inputStream.readShort();

        // read the payload
        payload = new byte[inputStream.available()]; // a nice little shortcut since there's no more data theoretically
        int bytesRead = inputStream.read( payload );
        if (bytesRead > TFTP_MTU)
            throw new IllegalStateException( "Read a TFTP DATA packet that had > 512 bytes of payload data" );

        isValid = true;
    }

    @Override
    public PacketType getTftpPacketType()
    {
        return PacketType.DATA;
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }
}
