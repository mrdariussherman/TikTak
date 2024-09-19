package tiktak.app.client;

import java.io.BufferedReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TestClient {
    private static final String CHARENC = "ISO8859-1";
    private static final String EOLN = "\r\n";
    private static final String TOST_OP = "TOST";
    private static final String LTSRL_OP = "LTSRL";
    private static final String ACK_OP = "ACK";
    private static final int EXTRATHREADS = 2;

    public static void main(String[] args) {
        if (args.length != 5 && args.length != 7) {
            throw new IllegalArgumentException(
                    "Parameter(s): <server> <port> <username> <password> (TOST|LTSRL <Category> <Image File>)");
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        String password = args[3];
        String operation = args[4];
        String category = (args.length == 7) ? args[5] : null;
        String imageFile = (args.length == 7) ? args[6] : null;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < EXTRATHREADS; i++) {
            Thread t = new Thread(() -> go(server, port, username, password, operation, category, imageFile));
            t.start();
            threads.add(t);
        }

        go(server, port, username, password, operation, category, imageFile);

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void go(String server, int port, String username, String password, String operation, String category,
                          String imageFile) {

        try (Socket s = new Socket(server, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), CHARENC));
            Writer out = new OutputStreamWriter(makeSlowOutputStream(s.getOutputStream()), CHARENC);

            // Consume version
            in.readLine();

            // Send ID
            out.write("ID " + username + EOLN);
            out.flush();

            // Get Challenge
            String line = in.readLine();
            String chlg = line.split(" ")[1];

            // Send response
            out.write("CRED " + computeHash(chlg + password) + EOLN);
            out.flush();

            // Get ACK
            if (!ACK_OP.equals(in.readLine())) {
                fail("Expected " + ACK_OP);
            }

            switch (operation) {
                case TOST_OP:
                    out.write(TOST_OP + EOLN);
                    out.flush();
                    break;
                case LTSRL_OP:
                    Base64.Encoder e = Base64.getEncoder().withoutPadding();
                    String image = e.encodeToString(Files.readAllBytes(Paths.get(imageFile)));
                    out.write(LTSRL_OP + " " + category + " " + image + EOLN);
                    out.flush();
                    break;
                default:
                    fail("Bad operation");
            }


            // Get ACK
            if (!ACK_OP.equals(in.readLine())) {
                fail("Expected " + ACK_OP);
            }else{
                System.out.println("Got Ack");
            }

        } catch (IOException e) {
            System.err.println("Problem creating socket: " + e.getMessage());
        }
    }

    public static void fail(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static String computeHash(String msg) throws UnsupportedEncodingException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buf = md5.digest(msg.getBytes(CHARENC));
            return hashToString(buf);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to get MD5", e);
        }
    }

    public static String hashToString(byte[] bytes) {
        StringBuilder hexHash = new StringBuilder();
        for (byte b : bytes) {
            String v = Integer.toHexString(b & 0xff);
            if (v.length() == 1)
                v = "0" + v;
            hexHash.append(v.toUpperCase());
        }

        return hexHash.toString();
    }

    public static OutputStream makeSlowOutputStream(OutputStream out) {
        return new FilterOutputStream(out) {
            private final static long DELAY = 5;

            @Override
            public void write(int b) throws IOException {
                super.write(b);
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public synchronized void write(byte[] b, int off, int len) throws IOException {
                for (int i = off; i < len; i++) {
                    write(b[i]);
                }
            }
        };
    }
}