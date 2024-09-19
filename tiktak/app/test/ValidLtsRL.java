/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 3
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.app.test;

import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ValidLtsRL {
    protected static String ENC = "ISO8859-1";

    /**
     * Client - Main
     *
     * @param args Command-line Argument
     * @throws ValidationException      Thrown when message received from Server
     *                                  is not valid
     * @throws NoSuchAlgorithmException Thrown when MD5 hash not found
     */
    public static void main(String[] args) throws ValidationException,
            NoSuchAlgorithmException {
        MessageInput in; // Serialization Input Stream
        MessageOutput out; // Serialization Output Stream
        Message message = null; // Temp message variable
        Socket server; // Client connection

        try {
            server = new Socket("127.0.0.1", 5000);
            if (!server.isConnected()) {
                System.exit(1);
            }

            in = new MessageInput(server.getInputStream());
            out = new MessageOutput(server.getOutputStream());

            message = getMessage(in, message, server);

            new ID("sherman").encode(out);

            message = getMessage(in, message, server);


            String nonce = ((Challenge) message).getNonce();
            nonce += "test";

            new Credentials(convertToHex(MessageDigest.getInstance("MD5")
                    .digest(nonce.getBytes(ENC)))).encode(out);

            message = getMessage(in, message, server);

            if (!Files.exists(Paths.get("/Users/Darius/Desktop/Networking/TikTak/src/BU.png"))) {
                System.err.println("LTSRL Image does not exist!\n\n");
                System.exit(1);
            }
            byte[] image = Files.readAllBytes(Paths.get("/Users/Darius/Desktop/Networking/TikTak/src/BU.png"));
            new LtsRL("Baylor",
                    Base64.getEncoder().withoutPadding().encode(image))
                    .encode(out);

            message = getMessage(in, message, server);

            if (message.getClass() != Ack.class) {
                server.close();
                System.exit(1);
            }
            server.close();

            System.out.println("Successful LtsRL");
        } catch (IOException exc) {
            System.err.println("Unable to communicate: " + exc.toString());
            System.exit(1);
        }
    }

    /**
     * Client - Get Message with generic error handling
     *
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
     *
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
}
