package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.IOException;

/**
 * Created by maxpiepenbrink on 3/30/17.
 */
public class Acknowledge implements TftpDatagram
{
    private int blockNumber;

    public void setBlockNumber(int blockNumber)
    {
        this.blockNumber = blockNumber;
    }

    public int getBlockNumber()
    {
        return blockNumber;
    }

    @Override
    public byte[] serialize() throws RuntimeException, IOException
    {
        return new byte[0];
    }

    @Override
    public void deserialize(byte[] data, int length) throws RuntimeException, IOException
    {

    }

    @Override
    public PacketType getTftpPacketType()
    {
        return null;
    }

    @Override
    public boolean isValid()
    {
        return false;
    }
}
