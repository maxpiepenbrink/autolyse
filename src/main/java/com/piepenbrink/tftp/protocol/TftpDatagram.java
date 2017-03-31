package com.piepenbrink.tftp.protocol;

import com.piepenbrink.tftp.PacketType;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * A basic interface that outlines the core behavior options of the TFTP packets.
 * <p>
 * Namely this interface outlines a (probably TOO basic) basic contract:
 * <p>
 * <ul>
 * <li>* Each implementation must send it's current self to a remote address</li>
 * <li>* Each implementation must be able to digest a  </li>
 * </ul>
 */
public interface TftpDatagram
{
    /** Use this to serialize/deserialize strings */
    String TFTP_CHARSET_STRING = "US-ASCII";
    Charset TFTP_CHARSET = Charset.forName( TFTP_CHARSET_STRING );

    /**
     * Create the data to send over the medium
     */
    byte[] serialize() throws RuntimeException, IOException;

    /**
     * Take data from the medium and build state from that
     */
    void deserialize(final byte[] data, final int length) throws RuntimeException, IOException;

    /**
     * Returns the {@link PacketType} enum representing this datagram.
     */
    PacketType getTftpPacketType();

    /**
     * @return Whether or not this datagram looks good
     */
    boolean isValid();
}
