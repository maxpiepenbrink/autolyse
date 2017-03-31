package com.piepenbrink;

import java.util.logging.Logger;

/**
 * entrypoint
 */
public class Main
{
    private static final Logger logger = Logger.getLogger( "main" );

    private static final String usageString =
            "By default this launches a TFTP file server in the current directory listening on UDP port 69 on 0.0.0.0\n"
                    + "\nUsage:\n"
                    + "  autolyse --help\n"
                    + "  autolyse --server [--ip=<local ip>] [--port=<local port>] [--directory=<directory to serve files from>]\n"
                    + "  autolyse --client [--ip=<remote ip>] [--port=<remote port>] \n";

    public static void main(String[] args)
    {
        Configuration runtimeConfig = Configuration.getConfigurationFrom( args );
    }

    /**
     * Small object to help us contain/validate/present the command line arguments & basic runtime configuration,
     * normally I'd consider using something like the apache commons Options parser or creating something with similar
     * niceties, for this exercise I just wanted something basic and straight forward.
     */
    private static class Configuration
    {
        public static final Mode DEFAULT_MODE = Mode.SERVER;
        public static final String DEFAULT_IP = "0.0.0.0";
        public static final int DEFAULT_LISTEN_PORT = 69;
        public static final String DEFAULT_DIR = "./";

        public final Mode mode;
        public final int targetPort;
        public final String targetIp;
        public final String servingDirectory;
        public final String targetFile;

        /**
         * Handles the argument searching and maps any relevant fields to the above variables
         */
        public static Configuration getConfigurationFrom(final String[] args) throws IllegalStateException
        {
            Mode mode = Mode.SERVER;
            int targetPort = DEFAULT_LISTEN_PORT;
            String targetIp = DEFAULT_IP;
            String targetDir = DEFAULT_DIR;
            String targetFile = "";

            for (String arg : args)
            {
                // search for the -server and -client args
                // the null check will ensure we only set this field once
                if ("--server".equals( arg ))
                    mode = Mode.SERVER;
                else if ("--client".equals( arg ))
                    mode = Mode.CLIENT;
                else if (arg.startsWith( "--port=" ))
                {
                    try
                    {
                        targetPort = Integer.parseUnsignedInt( arg.split( "=" )[1] );
                    } catch (NumberFormatException e)
                    {
                        throw new IllegalStateException( "Couldn't parse -port= command line option", e );
                    }
                } else if (arg.startsWith( "--ip=" ))
                {
                    targetIp = arg.split( "=" )[1];
                } else if (arg.startsWith( "--directory=" ))
                {
                    targetDir = arg.split( "=" )[1];
                } else if (arg.startsWith( "--file=" ))
                {
                    targetFile = arg.split( "=" )[1];
                } else if (arg.equals( "--h" ) || arg.equals( "--help" ))
                {
                    logger.info( usageString );
                    System.exit( 0 );
                }

            }

            return new Configuration( mode, targetPort, targetIp, targetDir, targetFile );
        }

        public Configuration(Mode mode, int targetPort, String targetIp, String servingDirectory, String targetFile)
        {
            this.mode = mode;
            this.targetPort = targetPort;
            this.targetIp = targetIp;
            this.servingDirectory = servingDirectory;
            this.targetFile = targetFile;
        }

        public enum Mode
        {
            CLIENT,
            SERVER
        }
    }
}
