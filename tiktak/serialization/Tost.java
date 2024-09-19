/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 1
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

import java.io.IOException;
import java.util.Objects;

public class Tost extends Message {
    private final int hash = 721;

    /**
     * Tost constructor
     */
    public Tost() {
        setOperation("TOST");
    }

    /**
     * Tost toString
     * @return Tost string representation
     */
    public String toString() {
        return "Tost";
    }

    /**
     * Tost encoder
     * @param out Output stream
     * @throws IOException Thrown when stream is not writable
     * @throws NullPointerException Thrown when stream is null
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException,
            IOException {
        Objects.requireNonNull(out, "Message Output cannot be null!");
        out.Write(("TOST\r\n").getBytes(ENC));
    }

    /**
     * Getter for operation
     * @return Current value of parent operation
     */
    @Override
    public String getOperation() {
        return operation;
    }

    /**
     * Setter for operation
     * @param op New operation
     */
    @Override
    public void setOperation(String op) {
        operation = op;
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
