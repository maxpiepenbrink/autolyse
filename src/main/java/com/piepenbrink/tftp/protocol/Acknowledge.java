package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.*;

/**
 * This is the ACK or Acknowledge TFTP packet from RFC 1350
 * <pre>
 *  2 bytes     2 bytes
 * ---------------------
 * | Opcode |   Block #  |
 * ---------------------
 *
 * Figure 5-3: ACK packet
 * </pre>
 */
public class Acknowledge implements TftpDatagram
{
    private int blockNumber = -1;
    private boolean isValid = false;

    public void setBlockNumber(int blockNumber)
    {
        this.blockNumber = blockNumber;
        isValid = true;
    }

    public int getBlockNumber()
    {
        if (!isValid())
            throw new IllegalStateException( "Attempt to get block number on invalid Acknowledge object!" );
        return blockNumber;
    }

    @Override
    public byte[] serialize() throws RuntimeException, IOException
    {
        int totalSize = 2 + 2; // 2 for opcode, 2 for the block #
        // set up some buffers to make this easier on ourselves
        ByteArrayOutputStream buffer = new ByteArrayOutputStream( totalSize );
        DataOutputStream outputStream = new DataOutputStream( buffer );

        outputStream.writeShort( PacketType.ACK.opCode );
        outputStream.writeShort( blockNumber );

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
        if (opcode != PacketType.ACK.opCode)
            throw new RuntimeException( "Attempt to deserialize an Acknowledge (ACK) packet with data that doesn't have a ACK opcode" );

        blockNumber = inputStream.readShort();

        isValid = true;
    }

    @Override
    public PacketType getTftpPacketType()
    {
        return PacketType.ACK;
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }
}
