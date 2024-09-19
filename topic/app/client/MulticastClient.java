/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 8
 * Class: CSI 4321
 *
 ************************************************/
package topic.app.client;

import topic.serialization.ErrorCode;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Scanner;

public class MulticastClient {
    /**
     * Multicast Client Main
     * @param args Commandline Arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Multicast Addr> <Port>");
        }

        InetAddress address = null; // Multicast address
        int MAX_UDP = 65535; // MAX UDP length
        MulticastSocket sock = null; // Multicast Socket

        try {
            address = InetAddress.getByName(args[0]); // Multicast address
            if (!address.isMulticastAddress()) { // Test if multicast address
                throw new IllegalArgumentException("Not a valid multicast " +
                        "address");
            }

            int port = Integer.parseInt(args[1]); // Multicast port
            sock = new MulticastSocket(port); // for receiving
            sock.joinGroup(address); // Join the multicast group
        } catch (IOException exc){
            System.err.print("Invalid Multicast Address. Quitting! " + exc.toString());
            System.exit(1);
        }

        // Anonymous thread to monitor console for quit
        MulticastSocket finalSock = sock;
        InetAddress finalAddress = address;
        (new Thread(() -> {
            Scanner scan = new Scanner(System.in);
            if ( scan.hasNextLine() ){
                if ( scan.nextLine().equals("quit") ){
                    try {
                        finalSock.leaveGroup(finalAddress);
                        finalSock.close();
                    } catch (IOException e) {
                        System.err.println("Error leaving multicast group! " + e.toString());
                        System.exit(1);
                    }
                    System.exit(0);
                }
            }
        })).start();

        // Main loop to receive multicast messages and output them
        while ( true ) {
            // Receive a datagram
            DatagramPacket packet = new DatagramPacket(new byte[MAX_UDP],
                    MAX_UDP);
            try {
                sock.receive(packet);
            } catch ( IOException exc ){
                System.err.println("Error receiving packet. " + exc.toString());
            }

            Response response = null;

            try {
                response =
                        new Response((Arrays.copyOfRange(packet.getData(), 0, packet
                                .getLength())));
            } catch (TopicException exc) {
                System.err.println(exc.toString());
            }

            if ( response != null && response.getErrorCode().equals(ErrorCode.NOERROR) ) {
                System.out.println(response.toString());
            } else if ( response != null ){
                System.out.println(response.getErrorCode().getErrorMessage());
            }
        }
    }
}