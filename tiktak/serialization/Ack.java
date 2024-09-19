/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 0
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

import java.io.IOException;
import java.util.Objects;

public class Ack extends Message {
    private final int hash = 771;

    /**
     * Act constructor
     */
    public Ack() {
        this.setOperation("ACK");
    }

    /**
     * Ack encoder
     * @param out Output stream
     * @throws IOException Thrown when stream is not writable
     * @throws NullPointerException Thrown when stream is null
     */
    public void encode(MessageOutput out) throws IOException,
            NullPointerException {
        Objects.requireNonNull(out, "Message Output cannot be null!");
        out.Write("ACK\r\n".getBytes(ENC));
    }

    /**
     * Getter for operation
     * @return Current value of parent operation
     */
    @Override
    public String getOperation() {
        return "ACK";
    }

    /**
     * Setter for operation
     * @param op New operation
     */
    @Override
    public void setOperation(String op) {
        operation = op;
    }

    /**
     * Ack toString
     * @return Ack string representation
     */
    public String toString() {
        return "Ack";
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null ){
            return false;
        }

        return o.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return hash;
    }
}