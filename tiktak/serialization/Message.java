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

public abstract class Message {
    protected static String ENC = "ISO8859-1";
    protected static String operation;
    protected static String ACKCONST = "ACK";
    protected static String VERSIONCONST = "TIKTAK ";
    protected static String CREDCONST = "CRED ";
    protected static String CLNGCONST = "CLNG ";
    protected static String LTSRLCONST = "LTSRL ";
    protected static String IDCONST = "ID ";
    protected static String TOSTCONST = "TOST";
    protected static String ERRORCONST = "ERROR ";

    /**
     * Will decode a MessageInput stream and return the Message subclass that
     * it belongs to.
     *
     * @param in Byte input stream
     * @return A subclass of Message based on the input
     * @throws NullPointerException Exception thrown when null input provided
     * @throws ValidationException  Exception thrown when a valid format is
     *                              not received
     * @throws java.io.IOException  Exception thrown when the input scream is
     *                              not readable
     */
    public static Message decode(MessageInput in)
            throws NullPointerException, ValidationException,
            IOException {
        Objects.requireNonNull(in, "Message input cannot be null");

        String message = new String(in.ReadStream(), ENC);

        Objects.requireNonNull(message, "Data must be read from the byte " +
                "stream");

        message = message.split("\r\n")[0];
        return MessageTree(message);
    }

    /**
     * Will decode a MessageInput stream and return the Message subclass that
     * it belongs to.
     *
     * @param message String input from AIO
     * @return A subclass of Message based on the input
     * @throws NullPointerException Exception thrown when null input provided
     * @throws ValidationException  Exception thrown when a valid format is
     *                              not received
     */
    public static Message decode(String message)
            throws NullPointerException, ValidationException {
        Objects.requireNonNull(message, "Message input cannot be null");

        if (!message.contains("\r\n")) {
            throw new ValidationException("145145", "No delimiter in message!");
        }

        message = message.split("\r\n")[0];

        return MessageTree(message);
    }

    public static Message MessageTree(String message) throws ValidationException {
        Message decoded;

        if (message.startsWith(VERSIONCONST)) {
            Version.parseString(message);
            decoded = new Version();
        } else if (message.startsWith(IDCONST)) {
            decoded = new ID(ID.parse(message));
        } else if (message.startsWith(CLNGCONST)) {
            decoded = new Challenge(Challenge.parse(message));
        } else if (message.startsWith(CREDCONST)) {
            decoded = new Credentials(Credentials.parseHash(message));
        } else if (message.startsWith(LTSRLCONST)) {
            decoded = new LtsRL( LtsRL.parseCategory(message),
                    LtsRL.parseImage(message));
        } else if (TOSTCONST.equals(message)) {
            decoded = new Tost();
        } else if (ACKCONST.equals(message)) {
            decoded = new Ack();
        } else if (message.startsWith(ERRORCONST)) {
            decoded = new Error(Error.parseCode(message),
                    Error.parseMessage(message));
        } else {
            throw new ValidationException("56789", "Error decoding the " +
                    "message, no matching class found");
        }

        return decoded;
    }

    /**
     * Abstract function for encoding message types
     *
     * @param out Output stream
     * @throws IOException Exception thrown when output stream is not writable
     */
    public abstract void encode(MessageOutput out) throws IOException;

    /**
     * Abstract function for getting operation
     * Getter for operation
     *
     * @return String representation of operation
     */
    public abstract String getOperation();

    /**
     * Abstract setter for operation
     *
     * @param op New operation
     */
    public abstract void setOperation(String op);
}
