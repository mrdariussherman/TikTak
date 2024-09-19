/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/
package topic.app.client;

import topic.serialization.Query;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class Client {
    private static DatagramSocket socket = null; // UDP Socket
    private static final int TIMEOUT = 3000; // Packet timeout
    private static final int ATTEMPTS = 3; // Retransmission attempts
    private static final long MAX_ID = 4294967295L; // Max ID

    /**
     * Client main
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        validateArgs(args);

        // Setup for UDP Connection
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
            InetAddress address = InetAddress.getByName(args[0]);
            socket.connect(address, Integer.parseInt(args[1]));
        } catch (SocketException | UnknownHostException exc){
            System.err.println("Error creating socket connection! " + exc.toString());
            System.exit(4);
        }

        // Do Query and get response
        Response response;
        response = sendQuery(args);

        System.out.println("Top Tics:");
        for ( String x : response.getPosts() ){
            System.out.println(x);
        }
    }

    /**
     * Client message sender
     * @param args
     */
    private static Response sendQuery(String[] args){
        Random rand = new Random(); // Random number generator

        long queryID = Math.abs( (rand.nextLong() + 1000) % MAX_ID);
        Query query;

        int MESSAGE_LENGTH = 65535;
        byte[] resp = new byte[MESSAGE_LENGTH];
        DatagramPacket response = new DatagramPacket(resp, MESSAGE_LENGTH);
        try {
            query = new Query( queryID, Integer.parseInt(args[2]) );
            byte[] data = query.encode();
            for (int i = 0; i < Client.ATTEMPTS; i++ ){
                socket.send(new DatagramPacket(data, data.length,
                        InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
                socket.receive(response);
                if ( response.getAddress().equals(InetAddress.getByName(args[0])) ){
                    break;
                }
            }
        } catch (IOException exc){
            System.err.println("Error sending query! " + exc.toString());
            System.exit(1);
        }

        try {
            return new Response(Arrays.copyOfRange(response.getData(), 0 ,
                    response.getLength()));
        } catch (TopicException exc){
            String error = "";
            switch ( exc.getErrorCode() ){
                case VALIDATIONERROR: error =
                        "Validation error: " + exc.getMessage(); break;
                case NETWORKERROR:
                    if ( exc.getSecretMessage() != null ){
                        error = exc.getSecretMessage();
                    } else {
                        error = exc.getErrorCode().toString();
                    }
                    break;
                default: error = exc.getErrorCode().toString(); break;
            }

            System.err.println(error);
            System.exit(6);
        }

        return null;
    }

    private static void validateArgs(String[] args){
        int posts; // Post count provided by user
        int port; // Port to connect on server
        String server; // Server Name/IP

        // Parameter Check
        // Parameter Count
        if ( args.length != 3 ){
            System.err.println("Invalid parameter count! Expected: <SERVER> " +
                    "<PORT> <REQUESTED POSTS>");
            System.exit(0);
        }

        // Server Name/IP
        server = args[0];
        if ( !server.matches("[a-zA-Z0-9.]+") ){
            System.err.println("Invalid Server Name/IP (" + server + ").");
            System.exit(1);
        }

        // Port
        port = Integer.parseInt(args[1]);
        if ( port <= 0 ){
            System.err.println("Invalid Port Number (" + port + "). Must be " +
                    "greater than " +
                    "0.");
            System.exit(2);
        }

        // Post Count
        posts = Integer.parseInt(args[2]);
        if ( posts < 0 || posts > 65535 ){
            System.err.println("Invalid Post count (" + posts + "). Must me " +
                    "greater than 0" +
                    " " +
                    "and less than 65536.");
            System.exit(3);
        }
    }
}
