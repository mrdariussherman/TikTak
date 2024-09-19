/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 0
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MessageOutput {
    private final OutputStream out;

    /**
     * MessageOutput constructor
     *
     * @param out OutputStream to create DataStream
     * @throws java.lang.NullPointerException Exception thrown when
     *                                        OutputStream is null
     */
    public MessageOutput(OutputStream out)
            throws java.lang.NullPointerException {
        Objects.requireNonNull(out, "OutputStream cannot be null!");
        this.out = new DataOutputStream(out);
    }

    /**
     * Write to data stream
     *
     * @param data Bytes to be written to stream
     * @throws IOException Exception thrown when stream cannot be written to
     */
    public void Write(byte[] data) throws IOException {
        out.write(data);
    }
}
