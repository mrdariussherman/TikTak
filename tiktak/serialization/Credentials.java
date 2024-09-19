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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Credentials extends Message {
    String hash;

    /**
     * Credential constructor
     * @param creds Hash string
     * @throws ValidationException Thrown when hash is invalid
     */
    public Credentials(String creds) throws ValidationException {
        setHash(creds);
        setOperation("CRED");
    }

    /**
     * Credential message parser
     * @param message raw message from input stream
     * @return Valid hash
     * @throws NullPointerException Thrown when message is null
     */
    public static String parseHash(String message) throws NullPointerException {
        Objects.requireNonNull(message, "Hash cannot be null!");
        if ( message.contains(" ") ){
            return message.split(" ")[1];
        } else {
            return message;
        }
    }

    /**
     * Credential toString
     * @return Credential string representation
     */
    public String toString() {
        return "Credentials: hash=" + getHash();
    }

    /**
     * Hash Getter
     * @return Hash string
     */
    public String getHash() {
        return hash;
    }

    /**
     * Hash Setter
     * @param str Hash string
     * @return Credential with updated hash
     * @throws ValidationException Thrown when new hash is not valid
     * @throws NullPointerException Thrown when hash is null
     */
    public Credentials setHash(String str) throws ValidationException,
            NullPointerException {
        this.hash = validateHash(str);
        return this;
    }

    /**
     * Hash validator
     * @param hash Hex hash
     * @return Hex hash if valid
     * @throws ValidationException Thrown when hash (in hex) is not valid
     */
    private String validateHash(String hash) throws ValidationException {
        if (hash == null) {
            throw new ValidationException("-1234", "Credential cannot be null");
        }

        Pattern reg = Pattern.compile("^[A-F0-9]{32}$");
        Matcher match = reg.matcher(hash);

        if ( match.matches() ) {
            return hash;
        }

        throw new ValidationException("1", "Invalid credentials. No matching " +
                "user/password combination.");
    }

    /**
     * Message encoder
     * @param out Output stream
     * @throws IOException Thrown when stream is not writable
     * @throws NullPointerException Thrown when stream is null
     */
    @Override
    public void encode(MessageOutput out) throws IOException, NullPointerException {
        Objects.requireNonNull(out, "Message Output cannot be null!");
        out.Write(("CRED " + getHash() + "\r\n").getBytes(ENC));
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
        if ( o == null ) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return getHash().equals(that.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
