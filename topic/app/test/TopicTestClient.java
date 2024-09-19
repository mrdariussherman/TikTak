package topic.app.test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TopicTestClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Parameter(s):   ");
        }

        int MAX_MESSAGE_LEN = 65507;

        InetAddress server = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        byte[] HEX_TO_SEND = new BigInteger(args[2], 16).toByteArray();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(server, port);
            socket.setSoTimeout(2000);
            socket.send(new DatagramPacket(HEX_TO_SEND, HEX_TO_SEND.length));

            DatagramPacket incoming =
                    new DatagramPacket(new byte[MAX_MESSAGE_LEN], MAX_MESSAGE_LEN);
            socket.receive(incoming);

            System.out.println("Response (hex):");
            System.out.println(new BigInteger(incoming.getData(), 0,
                    incoming.getLength()).toString(16).toUpperCase());

            System.out.println("Response (chars):");
            for (int i = 0; i < incoming.getLength(); i++) {
                char buff = (char)incoming.getData()[i];
                if (buff >= 32 && buff < 127) {
                    System.out.print(buff);
                }
            }
        }
    }
}