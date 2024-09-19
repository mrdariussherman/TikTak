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

public class Error extends Message {
    private int code;
    private String message;

    /**
     * Error constructor
     * @param code Error code
     * @param message Error message
     * @throws ValidationException Thrown when error is invalid
     * @throws NullPointerException Thrown whn message or code is null
     */
    public Error(int code, String message) throws ValidationException,
            NullPointerException {
        this.setCode(code);
        this.setMessage(message);

        this.setOperation("ERROR");
    }

    /**
     * Parser for error code
     * @param rawMessage Message from input stream
     * @return Error code integer
     */
    public static int parseCode(String rawMessage) {
        return Integer.parseInt(rawMessage.split("\\s")[1]);
    }

    /**
     * Parser for error message
     * @param rawMessage Message from input stream
     * @return Error message
     */
    public static String parseMessage(String rawMessage) {
        return rawMessage.substring(10);
    }

    /**
     * Error toString
     * @return String representation of error
     */
    public String toString() {
        return ("Error: code=" + code + " message=" + message);
    }

    /**
     * Getter for error code
     * @return error code
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter for error code
     * @param code new code
     * @return Object with new code
     * @throws ValidationException thrown when code is not valid
     */
    public Error setCode(int code) throws ValidationException {
        this.code = this.validateCode(code);
        return this;
    }

    /**
     * Getter for error message
     * @return error message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Setter for error message
     * @param message new message
     * @return Object with new message
     * @throws NullPointerException Thrown when message is null
     * @throws ValidationException Thrown when message is invalid
     */
    public Error setMessage(String message) throws NullPointerException,
            ValidationException {
        this.message = this.validateMessage(message);
        return this;
    }

    /**
     * Message encoder
     * @param out Output stream
     * @throws IOException Thrown when stream is not writable
     * @throws NullPointerException Thrown when stream is null
     */
    @Override
    public void encode(MessageOutput out) throws IOException,
            NullPointerException {
        Objects.requireNonNull(out, "Message Output cannot be null!");
        out.Write(("ERROR " + code + " " + message +
                "\r\n").getBytes(ENC));
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

    /**
     * Error code validator
     * @param cde code to validate
     * @return Valid code
     * @throws ValidationException Thrown when code is invalid
     */
    private int validateCode(int cde) throws ValidationException {
        if ( cde >= 100 && cde < 1000 ) {
            return cde;
        }

        throw new ValidationException("-1", "Invalid error code received!");
    }

    /**
     * Error message validator
     * @param msg Error message
     * @return Valid error message
     * @throws ValidationException Thrown when message is invalid
     */
    private String validateMessage(String msg) throws ValidationException {
        if (msg == null) {
            throw new ValidationException("145", "Error message cannot be " +
                    "null!");
        }

        if (msg.matches("[0-9a-zA-Z\\s]+") ) {
            return msg;
        }

        throw new ValidationException("-1", "Invalid error message! Does not " +
                "match required format.");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return getCode() == error.getCode() &&
                getMessage().equals(error.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getMessage());
    }
}