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

public class ID extends Message {
    private final String REG = "[a-zA-Z0-9]+";
    private final String DELIMITER = "\r\n";
    private String id;

    /**
     * ID Constructor
     *
     * @param str ID to be set
     * @throws ValidationException Exception thrown when variable does not
     *                             meet ID requirements
     */
    public ID(String str) throws ValidationException {
        this.setID(str);
        this.setOperation("ID");
    }

    /**
     * Parses raw data to become ID
     *
     * @param str Raw data to be parsed
     * @return Potential ID as a String
     */
    public static String parse(String str) throws ValidationException {
        if (!str.matches("ID\\s[a-zA-Z0-9]+")) {
            throw new ValidationException("179830", "Invalid ID message! Does" +
                    " not meet ID requirements.");
        }
        return str.split(" ")[1];
    }

    /**
     * String representation of ID
     *
     * @return String object with the ID data
     */
    public String toString() {
        return ("ID: id=" + id);
    }

    /**
     * Getter for ID
     *
     * @return ID String
     */
    public String getID() {
        return id;
    }

    /**
     * Setter for ID
     *
     * @param str Potential ID
     * @return ID String if valid
     * @throws ValidationException Exception thrown when ID String does not
     *                             meet the requirements
     */
    public ID setID(String str) throws ValidationException,
            NullPointerException {
        this.id = this.validateID(str);
        return this;
    }

    /**
     * Encodes ID into bytes and writes them
     *
     * @param out Output stream
     * @throws IOException Exception thrown when stream is not writable
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "Output cannot be null");
        out.Write(("ID " + id + DELIMITER).getBytes(ENC));
    }

    /**
     * Getter for operation
     *
     * @return String operation
     */
    @Override
    public String getOperation() {
        return operation;
    }

    /**
     * Setter for Operation
     *
     * @param op New operation
     */
    @Override
    public void setOperation(String op) {
        operation = op;
    }

    /**
     * @param str String to be validated
     * @return Validated string
     * @throws ValidationException Exception thrown if string does not fit
     *                             the required pattern
     */
    private String validateID(String str) throws ValidationException {
        if (str == null) {
            throw new ValidationException("145", "ID cannot be null!");
        }

        if (!str.matches(REG)) {
            throw new ValidationException("435", "The ID did not meet the " +
                    "required " +
                    "pattern");
        }

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id1 = (ID) o;
        return id.equals(id1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
