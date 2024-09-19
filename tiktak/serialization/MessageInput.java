/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 0
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MessageInput {
    protected final String ENC = "ISO8859-1";
    protected final String DELIMITER = "\r\n";
    protected final int MAXMESSAGELENGTH = 16384;

    private InputStream in;

    /**
     * MessageInput Constructor
     *
     * @param in Input stream
     * @throws NullPointerException Exception thrown when InputStream is null
     */
    public MessageInput(InputStream in) throws NullPointerException {
        Objects.requireNonNull(in, "InputStream cannot be null!");
        this.in = new DataInputStream(in);
    }

    /**
     * Gets bytes from a stream
     *
     * @return Byte array with data from the stream
     * @throws IOException         Exception thrown when stream can not be
     *                             read from
     * @throws ValidationException Exception thrown when the data fails to
     *                             meet encoding standard
     */
    public byte[] ReadStream() throws IOException,
            ValidationException {
        boolean done = false;
        byte[] buffer = new byte[1];

        String dataString = "";

        while (!done) {
            int read = in.read(buffer);

            if (read == -1 && !dataString.contains(DELIMITER)) {
                throw new EOFException("EOS reached! The delimiter was not " +
                        "found before the stream ended.");
            }

            dataString = dataString.concat(new String(buffer, ENC));

            if (dataString.contains(DELIMITER)) {
                done = true;
            }
        }

        if (dataString.length() > MAXMESSAGELENGTH) {
            throw new ValidationException("1425", "Message length too long");
        }

        return dataString.getBytes(ENC);
    }
}
