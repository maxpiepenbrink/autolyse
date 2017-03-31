package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.*;

/**
 * The Error or ERROR packet from RFC 1350
 * <pre>
 *  2 bytes     2 bytes      string    1 byte
 * -----------------------------------------
 * | Opcode |  ErrorCode |   ErrMsg   |   0  |
 * -----------------------------------------
 *
 * Figure 5-4: ERROR packet
 * </pre>
 */
public class Error implements TftpDatagram
{
    private int errorCode = -1;
    private String errorMessage = null;
    private boolean isValid = false;

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setError(int errorCode, String message)
    {
        this.errorCode = errorCode;
        this.errorMessage = message;

        if (errorMessage == null)
            errorMessage = "";

        if (errorCode == -1)
            throw new IllegalStateException( "Error code cannot be -1! This is a dummy value." );
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    @Override
    public byte[] serialize() throws RuntimeException, IOException
    {
        byte[] errorMessageBytes = errorMessage.getBytes( TFTP_CHARSET );
        int totalSize = 2 + 2 + errorMessageBytes.length; // +2 for opcode, +2 for error code, then error message

        // set up some buffers to make this easier on ourselves
        ByteArrayOutputStream buffer = new ByteArrayOutputStream( totalSize );
        DataOutputStream outputStream = new DataOutputStream( buffer );

        outputStream.writeShort( PacketType.ERROR.opCode );
        outputStream.writeShort( errorCode );
        outputStream.write( errorMessageBytes );

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
        // validate that the opcode is correct before anything
        if (opcode != PacketType.ERROR.opCode)
            throw new RuntimeException( "Attempt to deserialize a Error (ERROR) packet with data that doesn't have a ERROR opcode" );

        errorCode = inputStream.readShort();

        byte[] errorMessageBytes = new byte[inputStream.available()];
        inputStream.read( errorMessageBytes );
        errorMessage = new String( errorMessageBytes, TFTP_CHARSET );

        isValid = true;
    }

    @Override
    public PacketType getTftpPacketType()
    {
        return PacketType.ERROR;
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }
}
