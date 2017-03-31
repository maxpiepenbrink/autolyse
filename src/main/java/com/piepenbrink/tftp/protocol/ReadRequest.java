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
    private String targetFile;
    private String fileMode;
    private boolean isValid = false;

    public String getTargetFile()
    {
        return targetFile;
    }

    public void setTargetFile(String targetFile)
    {
        this.targetFile = targetFile;
    }

    public String getFileMode()
    {
        return fileMode;
    }

    public void setFileMode(String fileMode)
    {
        this.fileMode = fileMode;
    }

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
        outputStream.close();
        return buffer.toByteArray();
    }

    public void setState(String targetFile, String fileMode)
    {
        this.targetFile = targetFile;
        this.fileMode = fileMode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadRequest that = (ReadRequest) o;

        if (isValid != that.isValid) return false;
        if (targetFile != null ? !targetFile.equals( that.targetFile ) : that.targetFile != null) return false;
        return fileMode != null ? fileMode.equals( that.fileMode ) : that.fileMode == null;
    }

    @Override
    public int hashCode()
    {
        int result = targetFile != null ? targetFile.hashCode() : 0;
        result = 31 * result + ( fileMode != null ? fileMode.hashCode() : 0 );
        result = 31 * result + ( isValid ? 1 : 0 );
        return result;
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
