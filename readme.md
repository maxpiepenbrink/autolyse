Overview
=======

This is a minimal somewhat blind implementation of the TFTP RFC outlined here: https://www.ietf.org/rfc/rfc1350.txt

The goal of this project is to provide a minimal library & daemon for sending/receiving data through the constructs outlined in RFC1350.

What will be implemented is as follows (this also serves as my checklist):

 - [√] A somewhat unit tested serializer/deserializer package which will handle the 5 primary packet types which are:
   - [√] 0x01 RRQ	(read request)
   - [ ] 0x02 WRQ	(write request) (OPTIONAL SCOPE, we'll see how I'm feeling)
   - [√] 0x03 DATA	(data)
   - [√] 0x04 ACK	(acknowledgement)
   - [√] 0x05 ERROR (you guessed it)
 - [√] A simple daemon which listens on UDP port 69 for TFTP packets
 - [√] A dead simple client which initiates TFTP conversations against the aforementioned daemon

What is definitely NOT implemented:
 - Any form of TFTP protocol level error handling, currently the primitive/terminal support for ERROR is there but both the client and server implementation are pretty slapdash to get the end-to-end proven.
 - WRQ is not in

Usage
====

Client:
`bin/java -Djava.util.logging.config.file=logging.properties -Dfile.encoding=UTF-8 com.piepenbrink.Main --client --ip=127.0.0.1 --file=extremely-important-document.mp4 --directory=downloads/`

Server:
`bin/java -Djava.util.logging.config.file=logging.properties -Dfile.encoding=UTF-8 com.piepenbrink.Main --server --ip=127.0.0.1 --directory=samples/`
