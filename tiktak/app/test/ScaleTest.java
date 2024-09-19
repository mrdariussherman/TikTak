package tiktak.app.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ScaleTest {
    private static final int NOCLIENTS = 10000;
    private static final Charset CHARENC = StandardCharsets.ISO_8859_1;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
        }

        for (int i=0; i < NOCLIENTS; i++) {
            System.out.println(i);
            Socket s = new Socket(args[0], Integer.parseInt(args[1]));
            write(s.getOutputStream(), "ID".getBytes(CHARENC));
            byte[] buf = new byte[500];
            int rv = s.getInputStream().read(buf);
            if (rv == -1) {
                System.err.println("Premature EOS");
            }
        }
        System.out.println("Done");
        System.in.read();
    }

    private static void write(OutputStream out, byte[] buf) throws IOException {
        for (byte b : buf) {
            out.write(b);
        }
    }
}
