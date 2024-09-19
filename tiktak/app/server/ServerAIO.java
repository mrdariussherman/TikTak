/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 7
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.app.server;

import tiktak.app.client.Client;
import tiktak.app.utility.Yipper;
import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerAIO {
    private final static int ARG_COUNT = 2; // The acceptable number of args

    private final static String LOGNAME = "connections.log"; // Logger file name
    private static Logger log = Logger.getLogger(LOGNAME); // Logger

    private static final int BUFSIZE = 16384; // Max buffer size
    private static NIODeframer deframer; // deframer object

    private static String id; // id received
    private static Map<String, String> credentials = null; // Store all
    // credentials

    private static Semaphore lock = new Semaphore(1); // lock for TOST
    private static Semaphore NIODeframer_lock = new Semaphore(1); // lock for
    // deframer

    private static Yipper yip = null; // yip object for posting
    private static Map<String, Integer> tostCount; // Count of tosts

    private final static String START_ERROR = "Unable to start: "; // Start
    // of a start error message
    private final static String NO_USER = "No such user "; // Start
    // of a non-existent user error message
    private final static String AUTH_ERROR = "Unable to authenticate"; // Start
    // of a invalid password error message

    private static Boolean error = false; // Used to determine if an error
    // has occured

    /**
     * Main
     * @param args Commandline Args
     */
    public static void main(String[] args) throws IOException {
        // Init
        credentials = new HashMap<>();
        deframer = new NIODeframer();
        tostCount = new HashMap<>();
        yip = new Yipper("yipper.html"); // Yipper interface
        FileHandler handler; // Log file handler

        // Setup logger
        try {
            handler = new FileHandler(LOGNAME, false);
            log.addHandler(handler);
            handler.setFormatter(new SimpleFormatter());
            log.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            log.log(Level.WARNING, START_ERROR + "error starting log");
        }

        validateArguments(args);

        AsynchronousServerSocketChannel listenChannel =
                AsynchronousServerSocketChannel.open();

        // Ensure continuation and reset

        try {
            // Bind local port
            listenChannel.bind(new InetSocketAddress(Integer.parseInt(args[0])));

            // Create accept handler using final listen channel
            AsynchronousServerSocketChannel finalListenChannel =
                    listenChannel;
            listenChannel.accept(null,
                    new CompletionHandler<AsynchronousSocketChannel, Void>() {

                @Override
                public void completed(AsynchronousSocketChannel channel, Void attachment) {
                    finalListenChannel.accept(null, this);
                    handleAccept(channel);
                }

                @Override
                public void failed(Throwable e, Void attachment) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            });
            // Block until current thread dies
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Server Interrupted, resetting " +
                    "connection. " + e.toString(), e);
            listenChannel.close();
            AsynchronousServerSocketChannel.open();
        } catch ( Exception e ){
            log.log(Level.WARNING, "Error communicating, resetting " +
                    "connection. " + e.toString(), e);
            listenChannel.close();
            AsynchronousServerSocketChannel.open();
        }
    }

    /**
     * Called after each accept completion, will send Version to client
     * @param channel channel of new client
     */
    public static void handleAccept(final AsynchronousSocketChannel channel) {
        error = false;
        log.log(Level.INFO, "Sending Version message!");
        sendMessage(channel, new Version());
    }

    /**
     * Called after each read completion
     *
     * @param channel channel of new client
     * @param buf byte buffer used in read
     * @throws IOException if I/O problem
     */
    public static void handleRead(final AsynchronousSocketChannel channel, ByteBuffer buf, int bytesRead)
            throws IOException {
        if (bytesRead == -1) { // Did the other end close
            channel.close();
        } else if (bytesRead > 0) {
            channel.read(buf, buf, new CompletionHandler<>() {
                public void completed(Integer bytesRead, ByteBuffer buf) {
                    try {
                        try {
                            NIODeframer_lock.acquire();
                        } catch (InterruptedException e) {
                            log.log(Level.SEVERE,
                                    "Could not lock deframer. " + e.toString());
                        }
                        String message = deframer.getMessage(buf);
                        NIODeframer_lock.release();

                        if ( message != null ) {
                            Message received;
                            try {
                                received = Message.decode(message);
                                log.log(Level.INFO, "Reveived: " + received.toString());
                                decisionTree(received, channel);
                            } catch (ValidationException e) {
                                log.log(Level.SEVERE,
                                        "Error receiving message: " + e.toString());
                            }
                        } else {
                            handleRead(channel, buf, bytesRead);
                        }
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Handle Read Failed " + e.toString(), e);
                    }

                }

                public void failed(Throwable ex, ByteBuffer v) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed " + e.toString(), e);
                    }
                }
            });
        }
    }

    /**
     * Called after each write
     *
     * @param channel channel of new client
     * @param buf byte buffer used in write
     */
    public static void handleWrite(final AsynchronousSocketChannel channel, ByteBuffer buf) {
        if (buf.hasRemaining()) { // More to write
            channel.write(buf, buf, new CompletionHandler<>() {
                public void completed(Integer bytesWritten, ByteBuffer buf) {
                    handleWrite(channel, buf);
                }

                public void failed(Throwable ex, ByteBuffer buf) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed " + e.toString(), e);
                    }
                }
            });
        } else { // Back to reading
            if ( error ){
                try{
                    channel.close();
                } catch ( IOException failed ){
                    log.log(Level.WARNING, "Close Failed " + failed.toString(), failed);
                }
                return;
            }

            ByteBuffer receiving = ByteBuffer.allocate(BUFSIZE);
            channel.read(receiving, receiving, new CompletionHandler<>() {
                public void completed(Integer bytesRead, ByteBuffer buf) {
                    try {
                        handleRead(channel, buf, bytesRead);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Handle Read Failed " + e.toString(), e);
                    }
                }

                public void failed(Throwable ex, ByteBuffer v) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed " + e.toString(), e);
                    }
                }
            });
        }
    }

    /**
     * Server - Validate commandline arguments
     * @param args Arguments from the commandline
     * @throws IOException thrown when Scanner fails to access PW file
     */
    private static void validateArguments(String[] args) throws IOException {
        // Validate all necessary startup items
        if ( args.length != ARG_COUNT ){
            log.log(Level.SEVERE, START_ERROR + "invalid parameter count." +
                    " Expected: <PORT> <PASSWORD FILE>");
            System.exit(1);
        }

        if ( Integer.parseInt(args[0]) <= 0 ){
            log.log(Level.SEVERE, START_ERROR + "invalid port number. " +
                    "needs to be greater than 0.");
            System.exit(1);
        }


        if ( !Files.exists(Paths.get(args[1])) ){
            log.log(Level.SEVERE,
                    START_ERROR + "password file (" + args[1] + ") does not " +
                    "exist");
            System.exit(1);
        }

        // Checking password file
        Scanner scan = new Scanner(new File(args[1]),
                StandardCharsets.ISO_8859_1);
        scan.useDelimiter("\r\n");

        if ( !scan.hasNextLine() ){
            log.log(Level.SEVERE, START_ERROR + "password file (" + args[1]  +
                    ") is corrupt. Expected format: <USER " +
                    "ID>:<PASSWORD>\\r\\n");
            System.exit(1);
        }

        while( scan.hasNextLine() ){
            String line = scan.nextLine();
            String PW_REGEX = "[a-zA-Z0-9]+:[a-zA-Z0-9]*";
            if ( !line.matches(PW_REGEX) ){
                log.log(Level.SEVERE, START_ERROR + "password file (" + args[1]  +
                        ") is corrupt! Expected format: <USER " +
                        "ID>:<PASSWORD>\\r\\n");
                System.exit(1);
            } else {
                String[] split = line.split(":");
                if ( split.length > 1 ){
                    credentials.put(split[0], split[1]);
                } else {
                    credentials.put(split[0], "");
                }
            }
        }
        scan.close();

        log.log(Level.INFO, "All parameters valid, attempting to start " +
                "server!");
    }

    /**
     * Server - Validate ID in PW File
     * @param id ID to search for
     */
    private static Boolean validID(String id) {
        return credentials.containsKey(id);
    }

    /**
     * Server - Validate password in PW File
     * @param id ID to search for a password
     * @param nonce Server nonce to test against
     * @param cred Credential message to compare against
     */
    private static Boolean validPassword(String id, String nonce,
                                         Message cred ) throws NoSuchAlgorithmException {
        nonce += credentials.get(id);
        String hash = Client.convertToHex(MessageDigest.getInstance("MD5")
                .digest(nonce.getBytes(StandardCharsets.ISO_8859_1)));

        return hash.equals(((Credentials)cred).getHash());
    }

    /**
     * DecisionTree - decide what happens upon message received
     * @param message received message
     * @param channel Client channel for IO
     */
    private static void decisionTree(Message message,
                                     AsynchronousSocketChannel channel) {
        if ( ID.class.equals(message.getClass()) || Credentials.class.equals(message.getClass()) ) {
            auth(message, channel);
        } else if ( Tost.class.equals(message.getClass()) ) {
            try {
                lock.acquire();
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "Could not acquire lock! " + e.toString());
            }

            if ( !tostCount.containsKey(id) ){
                tostCount.put(id, 1);
            }

            try {
                yip.update(id + ": " + Client.TOST + " " + tostCount.get(id));
                tostCount.replace(id, tostCount.get(id) + 1 );
            } catch (IOException e) {
                log.warning("Error posting to yipper! " + e.toString());
            }
            lock.release();
            log.info("Sent TOST to Yipper");
            sendMessage(channel, new Ack());
        } else if ( LtsRL.class.equals(message.getClass()) ) {
            log.info("Received LtsRL Message");
            try {
                yip.updateWithImage(id + ": LtsRL #" +
                        ((LtsRL)message).getCategory(), ((LtsRL)message).getImage());
            } catch (IOException e) {
                log.warning("Error posting to yipper!");
            }
            log.info("Sent LtsRL to Yipper");
            sendMessage(channel, new Ack());
        } else if (Error.class.equals(message.getClass()) ) {
            log.log(Level.SEVERE, "Received error: " + message.toString());
        } else {
            log.log(Level.SEVERE,
                    "Received unexpected message: " + message.toString());
        }
    }

    /**
     * auth - handles all auth related messages
     * @param message received message
     * @param channel Client channel for IO
     */
    private static void auth(Message message,
                             AsynchronousSocketChannel channel){
        // nonce used in challenge
        final int nonce = 245828166;
        if ( ID.class.equals(message.getClass()) ){ // ID
            if ( validID(((ID) message).getID()) ){ // Validation (Valid)
                id = ((ID) message).getID();
                try {
                    sendMessage(channel,
                            new Challenge(Integer.toString(nonce)));
                } catch ( ValidationException exc ){
                    log.log(Level.SEVERE, "Error sending challenge! " + exc.toString());
                }
            } else { // Validation (Invalid)
                log.log(Level.INFO, NO_USER + ((ID)message).getID());
                try {
                    error = true;
                    sendMessage(channel, new Error(352, NO_USER + ((ID) message).getID()));
                } catch ( ValidationException exc ){
                    log.log(Level.SEVERE,
                            "Error sending error message! " + exc.toString());
                }
            }
        } else if ( Credentials.class.equals(message.getClass()) ){ // Creds
            try {
                if (validPassword(id, Integer.toString(nonce), message)) {
                    sendMessage(channel, new Ack());
                } else {
                    error = true;
                    log.log(Level.INFO, "Invalid credential received!");
                    sendMessage(channel, new Error(145, AUTH_ERROR));
                }
            } catch ( NoSuchAlgorithmException | ValidationException exc ){
                log.log(Level.WARNING,
                        "Error validating credentials! " + exc.toString());
            }
        }
    }

    /**
     * sendMessages - fully sends message and enters back into read state
     * @param message received message
     * @param channel Client channel for IO
     */
    private static void sendMessage(AsynchronousSocketChannel channel,
                                    Message message){
        log.log(Level.INFO, "Attempting to send: " + message.toString());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageOutput output = new MessageOutput(out);

        if ( Error.class.equals(message.getClass()) ){
            error = true;
        }

        try {
            message.encode(output);
            handleWrite(channel, ByteBuffer.wrap(out.toByteArray()));
        } catch ( IOException exc ){
            log.log(Level.SEVERE, "Error sending error message. " + exc.toString());
        }
    }
}
