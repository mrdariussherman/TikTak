/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/
package topic.app.server;

import topic.serialization.ErrorCode;
import topic.serialization.Query;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server implements Runnable{
    private final static String LOGNAME= "topic.log"; // Logger file name
    private static Logger log = Logger.getLogger(LOGNAME); // Logger
    private List<String> toks; // List of all VALID tiktaks
    private DatagramSocket socket = null; // Socket for UDP connection
    private int port; // port to connect on

    /**
     * Topic Server constructor
     * @param tiks ArrayList of all current tiktaks
     * @param pt Port to connect the socket on
     */
    public Server(List<String> tiks, int pt){
        // Setup logger
        FileHandler handler = null;
        try {
            handler = new FileHandler(LOGNAME, true); // Log file
        } catch ( IOException ignored ){ }
        assert handler != null;
        log.addHandler(handler);
        handler.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);

        // Validate port number
        if ( pt <= 0 ||  pt > 65535 ){
            log.log(Level.SEVERE, "Invalid Port Number (" + pt + ") Expected " +
                    "range 1-65535 (depending on machine)");
            System.exit(2);
        }
        port = pt;

        // Ensure that the tiks are not null
        if ( tiks == null ){
            log.log(Level.SEVERE, "Null array of TikTaks provided to server.");
            System.exit(3);
        }
        toks = tiks;
    }

    /**
     * Topic Server "main"
     * This will run the server and log all connections
     */
    @Override
    public void run() {
        // Setup for UDP Connection
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException exc) {
            System.err.println("Error binding to port - " + exc.getMessage());
            System.exit(4);
        }

        // Setup for receiving
        int MAX_LENGTH = 65507;
        byte[] data = new byte[MAX_LENGTH];
        DatagramPacket received;
        Response response = null;

        // Main loop!
        while (true) {
            // Get message
            received = new DatagramPacket(data, MAX_LENGTH);

            try {
                socket.receive(received);
            } catch (IOException exc) {
                log.log(Level.SEVERE,
                        "Error receiving message! " + exc.getMessage());
            }

            // Trim result
            byte[] queryBytes = Arrays.copyOfRange(received.getData(), 0,
                    received.getLength());

            // Deserialize and form response
            Query query = null;
            try {
                // Deserialize
                query = new Query(queryBytes);

                // Generate requested tiks
                List<String> responseTiks = new ArrayList<>();
                if (query.getRequestedPosts() > 0) {
                    int totalBytes = 0;
                    for (int i = 0; i < query.getRequestedPosts(); i++) {
                        String ENC = "ISO8859-1";
                        if ( i >= toks.size() ){
                            break;
                        }
                        if ((totalBytes + toks.get(i).getBytes(ENC).length + 2) < MAX_LENGTH) {
                            responseTiks.add(toks.get(i));
                            totalBytes += toks.get(i).getBytes(ENC).length + 2;
                        } else {
                            break;
                        }
                    }
                }

                // Form response
                response = new Response(query.getQueryID(), ErrorCode.NOERROR
                        , responseTiks);
            } catch (TopicException exc) {
                log.log(Level.SEVERE, "Error validating deserialized " +
                        "message/forming response" +
                        "!" +
                        " + " + exc.getMessage());
                response = new Response(0, exc.getErrorCode(),
                        new ArrayList<>());
            } catch (UnsupportedEncodingException ignored) { }

            // Send response
            try {
                assert response != null;
                DatagramPacket responsePacket =
                        new DatagramPacket(response.encode(),
                                response.encode().length, received.getAddress(),
                                received.getPort());

                sendMessage(socket, responsePacket);
            } catch (NullPointerException ignored) { }
        }
    }

    public void sendMessage( DatagramSocket socket, DatagramPacket packet ){
        try {
            socket.send(packet);
        } catch (IOException exc) {
            log.log(Level.SEVERE,
                    "Unable to send response! " + exc.getMessage());
        }
    }
}
