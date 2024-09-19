/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 2
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.app.client;

import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Client {
    public static String TOST = "TOST"; // TOST message string
    public static String LTSRL = "LTSRL"; // LtsRL message string
    protected static String ENC = "ISO8859-1"; // String encoding
    private static boolean PROTOCOL_MATCH = false; // Tracks progression

    /**
     * Client - Main
     * Connects to server and executes protocol as defined.
     * @param args Command-line Argument
     */
    public static void main(String[] args) {
        final int TOST_PARAM_COUNT = 5; // Parameter count for TOST
        final int LTSRL_PARAM_COUNT = 7; // Parameter count for LTSRL
        final int MAX_ATTEMPTS = 3; // Wait for this count of incorrect
        final String INVALID_PARAMETERS = "Invalid parameters provided. " +
                "Expected: <SERVER> <PORT> <USERID> <PASSWORD> <OPERATION>";
        String PROTOCOL_ERROR = "The protocol was not followed - ";

        MessageInput in; // Serialization Input Stream
        MessageOutput out; // Serialization Output Stream
        Message message = null; // Temp message variable
        Socket server; // Client connection

        // Ensure that minimum parameters are present before doing work.
        if (args.length < TOST_PARAM_COUNT) {
            System.err.println(INVALID_PARAMETERS);
            System.exit(1);
        }

        // Determine if the operation has the correct arguments
        if ( args[4].equals(TOST) && args.length != TOST_PARAM_COUNT ) {
            System.err.println(INVALID_PARAMETERS);
            System.exit(1);
        } else if (args[4].equals(LTSRL) && args.length != LTSRL_PARAM_COUNT ) {
            System.err.println(INVALID_PARAMETERS);
            System.exit(1);

        }

        if ( args[4].equals(LTSRL) ) {
            if (!Files.exists(Paths.get(args[6]))) {
                System.err.println("LTSRL Image (" + args[6] + ") does not " +
                        "exist!\n\n");
                System.exit(1);
            }
        }


        // Connect to server
        try {
            server = new Socket(args[0], Integer.parseInt(args[1]));
            if (!server.isConnected()) {
                System.err.println("Error connecting to " + args[0] + ":"
                        + args[1] + "\n\n");
                System.exit(1);
            }

            in = new MessageInput(server.getInputStream());
            out = new MessageOutput(server.getOutputStream());

            // Begin protocol, Receive version message
            for ( int i = 0; i < MAX_ATTEMPTS; i++ ){
                message = getMessage(in, message, server);
                if (message.getClass() != Version.class) {
                    System.err.println("Sever did not respond with TIKTAK! " +
                            "Attempting to continue.");
                } else {
                    PROTOCOL_MATCH = true;
                    break;
                }
            }

            // Ensure that correct message was received.
            // Error message constant
            if ( !PROTOCOL_MATCH ){
                System.err.println(PROTOCOL_ERROR + "Missing server identity " +
                        "(TIKTAK)");
                closeConnection(server);
            } else {
                // Reset protocol match
                PROTOCOL_MATCH = false;
            }

            // Send id message
            try {
                new ID(args[2]).encode(out);
            } catch ( ValidationException exc ){
                System.err.println("Invalid ID provided! - " + exc.toString());
                closeConnection(server);
            }

            // Wait for challenge message
            for ( int i = 0; i < MAX_ATTEMPTS; i++ ) {
                message = getMessage(in, message, server);
                if (message.getClass() != Challenge.class) {
                    System.err.println("Server did not respond with CLNG! Got "
                            + message.toString() + " " + "Attempting " +
                            "to continue.");
                } else {
                    PROTOCOL_MATCH = true;
                    break;
                }
            }

            // Ensure that correct message was received.
            if ( !PROTOCOL_MATCH ){
                System.err.println(PROTOCOL_ERROR + "Missing challenge " +
                        "message");
                closeConnection(server);
            } else {
                // Reset protocol match
                PROTOCOL_MATCH = false;
            }

            // Parse message for nonce
            String nonce = ((Challenge) message).getNonce();
            nonce += args[3];

            try {
                new Credentials(convertToHex(MessageDigest.getInstance("MD5")
                        .digest(nonce.getBytes(ENC)))).encode(out);
            } catch ( ValidationException exc ){
                System.err.println("Error serializing credentials message!");
                closeConnection(server);
            } catch ( NoSuchAlgorithmException ignored ){}

            // Wait for an ack of authorization
            for ( int i = 0; i < MAX_ATTEMPTS; i++ ) {
                message = getMessage(in, message, server);
                if (message.getClass() != Ack.class) {
                    System.err.println("Server did not respond with ACK! Got "
                            + message.toString() + " " + "Attempting " +
                            "to continue.");
                } else {
                    PROTOCOL_MATCH = true;
                    break;
                }
            }

            // Ensure that correct message was received.
            if ( !PROTOCOL_MATCH ){
                System.err.println(PROTOCOL_ERROR + "Missing authorization " +
                        "ack");
                closeConnection(server);
            } else {
                // Reset protocol match
                PROTOCOL_MATCH = false;
            }

            // Send post
            if (args[4].equals(TOST)) {
                new Tost().encode(out);
            } else if (args[4].equals(LTSRL)) {
                byte[] image = Files.readAllBytes(Paths.get(args[6]));
                try {
                    new LtsRL(args[5],
                            Base64.getEncoder().withoutPadding().encode(image))
                            .encode(out);
                } catch ( ValidationException exc ){
                    System.err.println("LtsRL image is invalid - " + exc.toString());
                    closeConnection(server);
                }
            }

            // Wait for ack
            for ( int i = 0; i < MAX_ATTEMPTS; i++ ) {
                message = getMessage(in, message, server);
                if (message.getClass() != Ack.class) {
                    System.err.println("Server did not respond with ACK! Got "
                            + message.toString() + " " + "Attempting " +
                            "to continue.");
                } else {
                    PROTOCOL_MATCH = true;
                    break;
                }
            }

            // Ensure that correct message was received.
            if ( !PROTOCOL_MATCH ){
                System.err.println(PROTOCOL_ERROR + "Missing post " +
                        "acknowledgement");
                closeConnection(server);
            }

            server.close();
        } catch (IOException exc) {
            System.err.println("Unable to communicate: " + exc.toString());
            System.exit(1);
        }
    }

    /**
     * Client - Get Message with error class handling
     * @param in      MessageInput for server
     * @param message Message object to process response
     * @param server  Socket connected to the server
     */
    private static Message getMessage(MessageInput in, Message message,
                                      Socket server) throws IOException {
        try {
            message = Message.decode(in);
        } catch (ValidationException exc) {
            System.err.println("Invalid message: " + exc.toString());
            System.exit(1);
        }
        if (message.getClass() == Error.class) {
            System.err.println("Server ERROR! Ending session. Message:");
            System.err.println(message.toString() + "\n\n");
            server.close();
            System.exit(1);
        }
        System.out.println(message.toString());
        return message;
    }

    /**
     * Client - Convert MD5 to Hex
     * Converts
     * @param cred MD5 hash of credentials
     */
    public static String convertToHex(byte[] cred) {
        StringBuilder bob = new StringBuilder();

        for (byte b : cred) {
            String hexString = String.format("%02x", b);
            if (hexString.length() == 1) {
                bob.append("0");
            }
            bob.append(hexString);
        }

        return bob.toString().toUpperCase();
    }

    /**
     * Client - closeConnection will attempt to close the connection and
     * handel IOException potentially thrown
     * @param server Socket connected to the server
     */
    public static void closeConnection( Socket server ){
        try {
            server.close();
        } catch ( IOException exc ){
            System.err.println("Error closing connection!");
            System.exit(-1);
        }

        System.exit(1);
    }
}
