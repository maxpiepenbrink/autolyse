package com.piepenbrink.tftp.protocol;

/**
 * <p> The valid file modes described in RFC 1350 </p>
 * <p> From the spec:</p>
 * <pre>
 * The mode field contains the
 * string "netascii", "octet", or "mail" (or any combination of upper
 * and lower case, such as "NETASCII", NetAscii", etc.) in netascii
 * indicating the three modes defined in the protocol.
 * </pre>
 */
public enum FileModes
{
    NETASCII,
    OCTET,
    MAIL;
}
