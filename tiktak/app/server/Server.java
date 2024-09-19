/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 3
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.app.server;

import tiktak.app.client.Client;
import tiktak.app.utility.Yipper;
import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    protected static String ENC = "ISO8859-1"; // Character Encoding
    private static int MAX_SOCKET_COUNT; // Max sockets open

    private final static String START_ERROR = "Unable to start: "; // Start
    // of a start error message
    private final static String COMS_ERROR = "Unable to communicate: "; // Start
    // of a communication error message
    private final static int ARG_COUNT = 3; // The acceptable number of arguments

    /**
     * Client - Main
     * @param args Command-line Arguments
     */
    public static void main(String[] args) throws IOException {
        final int TIMEOUT = 20000; // Socket timeout
        ServerSocket listen = null; // Listening socket

        String LOGNAME = "connections.log"; // Logger file name
        Logger log = Logger.getLogger(LOGNAME); // Logger
        FileHandler handler = new FileHandler(LOGNAME, true); // Log file
        // handler

        Map< String,Integer> tostCount = new HashMap<>(); // Keep tost count
        List<String> TIKTAKS = new ArrayList<>(); // Initialize Toks!
        Map<String, String> creds = new HashMap<>(); // Init credentials


        Semaphore lock = new Semaphore(1);
        Yipper yip = new Yipper("yipper.html"); // Yipper interface

        // Setup logger
        try {
            log.addHandler(handler);
            handler.setFormatter(new SimpleFormatter());
            log.setUseParentHandlers(false);
        } catch (SecurityException e) {
            log.log(Level.WARNING,
                    START_ERROR + "error starting log. " + e.toString());
        }

        validateArguments(args, log, creds);

        // Open a socket
        try {
            listen = new ServerSocket(Integer.parseInt(args[0]));
        } catch (IOException exc){
            log.log(Level.SEVERE, START_ERROR + "error binding to port. \n " +
                    exc.toString());
            System.exit(1);
        }

        if( !listen.isBound() ){
            log.log(Level.SEVERE, START_ERROR + "server did not successfully " +
                    "bind to port! Exiting.");
            System.exit(1);
        }

        log.log(Level.INFO, "Server successfully started on port: " +
                listen.getLocalPort());

        // Start thread pool
        ExecutorService pool = Executors.newFixedThreadPool(MAX_SOCKET_COUNT);
        int i = 1000; // Used for thread ID

        // Start the Topics Server
        Thread topicServer = new Thread(new topic.app.server.Server(TIKTAKS,
                Integer.parseInt(args[0])));
        topicServer.start();

        //noinspection InfiniteLoopStatement
        while (true){
            Socket client = null;
            try {
                client = listen.accept();
                client.setSoTimeout(TIMEOUT);
                Runnable task = new ClientHandler(client, i, tostCount, log,
                        creds, yip, TIKTAKS, lock);
                pool.execute(task);
            } catch (IOException e) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
                assert client != null;
                client.close();
            }
            i++;
        }
    }

    /**
     * Server - Validate commandline arguments
     * @param args Arguments from the commandline
     * @throws IOException thrown when Scanner fails to access PW file
     */
    private static void validateArguments(String[] args, Logger log,
                                          Map<String, String> credentials) throws IOException {
        // Validate all necessary startup items
        if ( args.length != ARG_COUNT ){
            log.log(Level.SEVERE, START_ERROR + "invalid parameter count!" +
                    "Expected: <PORT> <THREAD COUNT> <PASSWORD FILE>");
            System.exit(1);
        }

        try {
            // Check port number
            if (Integer.parseInt(args[0]) <= 0 || Integer.parseInt(args[0]) > 65535) {
                log.log(Level.SEVERE, START_ERROR + "invalid port number. " +
                        "Must be greater than 0 and less than 65535");
                System.exit(1);
            }
        } catch ( NumberFormatException exc ){
            // handle error
            log.log(Level.SEVERE, START_ERROR + "invalid port number (NaN). " +
                    "Expected: <PORT> <THREAD COUNT> <PASSWORD " +
                    "FILE>");
            System.exit(1);
        }

        // Check thread count
        try {
            if (Integer.parseInt(args[1]) <= 0 && Integer.parseInt(args[1]) < 32072) {
                log.log(Level.SEVERE, START_ERROR + "invalid thread count. " +
                        "Must be greater than 0 and less than 32072.");
                System.exit(1);
            }
        } catch (NumberFormatException exc){ // Make sure to gracefully
            // handle error
            log.log(Level.SEVERE, START_ERROR + "invalid thread count (NaN). " +
                    "Expected: <PORT> <THREAD COUNT> <PASSWORD " +
                    "FILE>");
            System.exit(1);
        }

        MAX_SOCKET_COUNT = Integer.parseInt(args[1]);

        // Ensure password file existence
        if ( !Files.exists(Paths.get(args[2])) ){
            log.log(Level.SEVERE, START_ERROR +
                    "password file (" + args[2] + ") does not exist");
            System.exit(1);
        }

        // Checking password file
        Scanner scan = new Scanner(new File(args[2]), StandardCharsets.ISO_8859_1);
        scan.useDelimiter("\r\n");

        if ( !scan.hasNextLine() ){
            log.log(Level.SEVERE, START_ERROR + "password file does not " +
                    "meet expected format.");
            System.exit(1);
        }

        while( scan.hasNextLine() ){
            String line = scan.nextLine();
            String PW_REGEX = "[a-zA-Z0-9]+:[a-zA-Z0-9]*";
            if ( !line.matches(PW_REGEX) ){
                log.log(Level.SEVERE, START_ERROR + "password file does not " +
                        "meet expected format. Expected: <USER ID>:<PASSWORD>");
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
}

class ClientHandler implements Runnable {
    private Socket client; // Client connection
    private int threadID; // Thread ID for logging
    private Map<String, String> credentials; // Store credentials
    private Map<String, Integer> tostCount; // Tost count for Topics server
    private Logger log; // Logger for ALL event logging
    private Yipper yip; // Yipper for posting
    private List<String> TIKTAKS; // All tiktaks for Topics server
    private Semaphore lock; // Lock to ensure integrity of Topics server supporting
    // structures

    /**
     * Server - Constructor
     * @param client Socket connected to the servers
     * @param id Thread id
     * @param count Map holding TOST counts
     */
    ClientHandler( Socket client, int id, Map<String, Integer> count,
                  Logger logger, Map<String, String> creds,
                   Yipper yipper,
                   List<String> tiks, Semaphore lock) {
        this.client = client;
        this.threadID = id;
        this.tostCount = count;
        this.log = logger;
        this.credentials = creds;
        this.yip = yipper;
        this.TIKTAKS = tiks;
        this.lock = lock;
    }

    /**
     * Server - Thread Main
     */
    public void run() {
        MessageInput in; // Input stream
        MessageOutput out; // Output stream
        Message message; // Temp message variable

        String COMS_ERROR = "Unable to communicate: "; // Start
        // of a communication error message
        String NO_USER = "No such user "; // Start
        // of a non-existent user error message
        String AUTH_ERROR = "Unable to authenticate: "; // Start
        // of a invalid password error message

        log.info("Handling client " + client.getInetAddress()
                + "-" + client.getPort() + " with thread id " + threadID);

        // Get I/O
        try {
            in = new MessageInput(client.getInputStream());
            out = new MessageOutput(client.getOutputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            return;
        }

        // Send version
        try {
            new Version().encode(out);
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            try {
                client.close();
            } catch (IOException exc) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
            }

            return;
        }

        // Get ID
        message = getMessage(in, out, client, log);
        if (message == null) return;

        // Validate ID Class
        if ( message.getClass() != ID.class ){
            log.log(Level.WARNING, "Invalid message: Expected ID, but got " +
                    message.toString() );
            try {
                client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
            }

            return;
        }
        log.info("Valid ID Message Received");

        // Check if ID exists
        try {
            if( !validID(((ID) message).getID()) ){
                log.log(Level.SEVERE, NO_USER + ((ID)message).getID());
                new Error(352, NO_USER + ((ID)message).getID()).encode(out);
                client.close();
                return;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            return;
        } catch ( ValidationException e ){
            log.log(Level.INFO, "Invalid Error message! " + e.toString());
        }
        String id = ((ID) message).getID();
        log.info("User ID Validated");

        // Send CLNG
        Random random = new Random();
        int nonce = Math.abs(random.nextInt() + 1);
        try {
            new Challenge(Integer.toString(nonce)).encode(out);
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            try {
                client.close();
            } catch (IOException exc) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
            }

            return;
        } catch (ValidationException e) {
            log.log(Level.WARNING, "Invalid nonce! " + e.toString());
        }
        log.info("Sending Challenge Message");

        // Get CRED
        message = getMessage(in, out, client, log);
        if (message == null) return;
        log.info("Valid Credentials Message Received");

        // Validate CRED Class
        if ( message.getClass() != Credentials.class ){
            log.log(Level.WARNING, "Invalid message: Expected Credentials, " +
                    "but got " +
                    message.toString() );
            try {
                client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
            }

            return;
        }

        // Validate pw
        try {
            if ( !validPassword(id, Integer.toString(nonce), message) ){
                log.log(Level.SEVERE, AUTH_ERROR);
                new Error(145, AUTH_ERROR).encode(out);
                client.close();
                return;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            return;
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.WARNING, "Could not find MD5 hash algorithm");
        } catch (ValidationException e) {
            log.log(Level.WARNING, "Invalid Error message! " + e.toString() );
        }
        log.info("Validated User Password");

        try {
            new Ack().encode(out);
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            return;
        }
        log.info("Sent Ack Message");

        message = getMessage(in, out, client, log);
        if (message == null) return;
        log.info("Received operation message");

        if ( message.getClass() != Tost.class && message.getClass() != LtsRL.class ){
            log.log(Level.WARNING, "Invalid message: Expected LtsRL/Tost, " +
                    "but got " +
                    message.toString() );
            try {
                client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, COMS_ERROR + e.toString());
            }

            return;
        }

        if ( message.getClass() == Tost.class ){
            log.info("Received Tost Message");
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
            TIKTAKS.add(id + ": TOST " + tostCount.get(id));
        }

        if ( message.getClass() == LtsRL.class ){
            log.info("Received LtsRL Message");
            try {
                yip.updateWithImage(id + ": LtsRL #" +
                        ((LtsRL)message).getCategory(), ((LtsRL)message).getImage());
            } catch (IOException e) {
                log.warning("Error posting to yipper!");
            }
            log.info("Sent LtsRL to Yipper");
            TIKTAKS.add(id + ": LTSRL #" + ((LtsRL)message).getCategory());
        }

        try {
            new Ack().encode(out);
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
            return;
        }
        log.info("Sent Ack");

        log.info("Closing connection");
        try {
            client.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, COMS_ERROR + e.toString());
        }
    }


    /**
     * Server - Get Message with generic error handling
     * @param in MessageInput for server
     * @param client Socket connected to the server
     */
    private Message getMessage(MessageInput in, MessageOutput out,
                                      Socket client, Logger log) {
        Message message = null;

        try {
            message = Message.decode(in);
        } catch ( ValidationException exc ){
            log.log(Level.SEVERE, "Invalid message: " + exc.toString());
            // Reply with an error and close connection
            try {
                new Error(982, "Invalid message received!").encode(out);
            } catch (IOException | ValidationException ec ){
                log.log(Level.SEVERE, "Error validating/receiving message! " +
                        "Closing connection");
                try {
                    client.close();
                } catch ( IOException e ){
                    log.log(Level.SEVERE, "Error closing connection!");
                }
                return null;
            }
        } catch ( IOException exc ){
            log.log(Level.SEVERE,
                    "Error getting message! " + exc.toString() + " Closing connection");
            try {
                client.close();
            } catch ( IOException e ){
                log.log(Level.SEVERE, "Error closing connection! " + e.toString());
                return null;
            }
        }

        if ( message != null && message.getClass() == Error.class ){
            handleError(message, log);
        }

        return message;
    }

    /**
     * Server - Will handle error messages before closing connection
     * @param message Message from server
     */
    private void handleError(Message message, Logger log){
        log.log(Level.SEVERE, "Received error: " + message.toString());
    }

    /**
     * Server - Validate ID in PW File
     * @param id ID to search for
     */
    private Boolean validID(String id) {
        return credentials.containsKey(id);
    }

    /**
     * Server - Validate password in PW File
     * @param id ID to search for a password
     * @param nonce Server nonce to test against
     * @param cred Credential message to compare against
     */
    private Boolean validPassword(String id, String nonce,
                                         Message cred ) throws NoSuchAlgorithmException {
        nonce += credentials.get(id);
        String hash = Client.convertToHex(MessageDigest.getInstance("MD5")
                .digest(nonce.getBytes(StandardCharsets.ISO_8859_1)));

        return hash.equals(((Credentials)cred).getHash());
    }
}
