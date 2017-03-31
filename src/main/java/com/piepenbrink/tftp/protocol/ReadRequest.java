package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.*;

/**
 * <p>The RRQ / ReadRequest packet.</p>
 * <p>From the TFTP RFC:</p>
 * <pre>
 *
 * 2 bytes     string    1 byte     string   1 byte
 * ------------------------------------------------
 * | Opcode |  Filename  |   0  |    Mode    |   0  |
 * ------------------------------------------------
 *
 * Figure 5-1: RRQ/WRQ packet
 * </pre>
 */
public class ReadRequest implements TftpDatagram
{
    public static final byte TFTP_STRING_TERMINATION = 0x00;
    String targetFile;
    String fileMode;
    private boolean isValid = false;

    @Override
    public byte[] serialize() throws RuntimeException, IOException
    {
        byte[] targetFileBytes = targetFile.getBytes( TFTP_CHARSET );
        byte[] fileModeBytes = fileMode.getBytes( TFTP_CHARSET );

        int totalPadding = 2 + 1 + 1; // +2 for opcode bytes, +1 for first string padding, +1 for second
        // figure out our final packet length
        int totalSize = targetFileBytes.length + fileModeBytes.length + totalPadding;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream( totalSize );
        DataOutputStream outputStream = new DataOutputStream( buffer );
        outputStream.writeShort( PacketType.RRQ.opCode );   // first 2 bytes
        outputStream.write( targetFileBytes );              // first string
        outputStream.writeByte( TFTP_STRING_TERMINATION );  // terminating 0
        outputStream.write( fileModeBytes );                // file mode
        outputStream.writeByte( TFTP_STRING_TERMINATION );  // terminating 0

        // flush and return
        outputStream.flush();
        byte[] retval = buffer.toByteArray();
        outputStream.close();
        return retval;
    }

    public void setState(String targetFile, String fileMode)
    {
        this.targetFile = targetFile;
        this.fileMode = fileMode;
    }

    //TODO: the interface contract here is weak, there's nothing stopping a naive API user from trying to be clever
    // and re-using datagram objects when in reality this should be prevented and a better contract decided on here
    // for now this + documentation is fine.
    @Override
    public void deserialize(final byte[] data, final int length) throws RuntimeException, IOException
    {
        // set up a stream reader
        DataInputStream inputStream = new DataInputStream( new ByteArrayInputStream( data, 0, length ) );

        // unpack the first two bytes and make sure we have a good opcode before we do anything
        int opcode = inputStream.readShort();
        // validate that the opcode is the RRQ opcode before anything
        if (opcode != PacketType.RRQ.opCode)
            throw new RuntimeException( "Attempt to deserialize a ReadRequest (RRQ) packet with data that doesn't have a RRQ opcode" );

        //TODO: it wouldnt surprise if there's a better way to look for 0-padded strings in Java but I'm not gonna look right now

        {
            // we need to find our first \0 or null char value in our payload which will indicate
            // the start and end of the first string
            byte[] stringSearchBuffer = new byte[inputStream.available()]; // this array goes a little over but that's "ok", I'll sleep tonight somehow

            int stringLength = 0;
            for (int i = 0; i < stringSearchBuffer.length; i++)
            {
                stringSearchBuffer[i] = (byte) inputStream.read();
                if (stringSearchBuffer[i] == TFTP_STRING_TERMINATION)
                {
                    stringLength = i;
                    break;
                }
            }
            targetFile = new String( stringSearchBuffer, 0, stringLength, TFTP_CHARSET );
        }

        {
            byte[] stringSearchBuffer = new byte[inputStream.available()]; // this array goes a little over but that's "ok", I'll sleep tonight somehow
            int stringLength = 0;
            // now we need to get the mode string
            for (int i = 0; i < stringSearchBuffer.length; i++)
            {
                stringSearchBuffer[i] = (byte) inputStream.read();
                if (stringSearchBuffer[i] == TFTP_STRING_TERMINATION)
                {
                    stringLength = i;
                    break;
                }
            }
            fileMode = new String( stringSearchBuffer, 0, stringLength, TFTP_CHARSET );
        }

        // we're done!
        inputStream.close();
        isValid = true;
    }

    @Override
    public PacketType getTftpPacketType()
    {
        return PacketType.RRQ;
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }
}
