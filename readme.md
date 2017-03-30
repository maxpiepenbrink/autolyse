== Overview
This is a minimal somewhat blind implementation of the TFTP RFC outlined here: https://www.ietf.org/rfc/rfc1350.txt

The goal of this project is to provide a minimal library & daemon for sending/receiving data through the constructs outlined in RFC1350.

What will be implemented is as follows (this also serves as my checklist):

 - [ ] A unit tested serializer/deserializer package which will handle the 5 primary packet types which are:
   - [ ] 0x01 RRQ	(read request)
   - [ ] 0x02 WRQ	(write request) (OPTIONAL SCOPE, we'll see how I'm feeling)
   - [ ] 0x03 DATA	(data)
   - [ ] 0x04 ACK	(acknowledgement)
   - [ ] 0x05 ERROR (you guessed it)
 - [ ] A simple daemon which listens on UDP port 69 for TFTP packets
 - [ ] A dead simple client which initiates TFTP conversations against the aforementioned daemon

