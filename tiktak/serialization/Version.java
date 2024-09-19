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

public class Version extends Message {
    private String version = "1.0";

    /**
     * Version class constructor. Will set the operation value.
     */
    public Version() {
        this.setOperation("TIKTAK");
    }

    /**
     * Determines if String is a valid Version
     *
     * @param str String to be used as the new Version
     * @throws ValidationException Exception thrown when the String is not a
     *                             valid version
     */
    public static void parseString(String str) throws ValidationException {
        Objects.requireNonNull(str, "Invalid Version!");
        if (!str.equals("TIKTAK 1.0")) {
            throw new ValidationException("5678", "Version was not valid");
        }
    }

    /**
     * Converts version class to a String representation.
     *
     * @return the String representation of Version
     */
    public String toString() {
        return "TikTak";
    }

    /**
     * Gets the version associated with the class.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Attempts to set the version, if it is valid.
     *
     * @param str String to be used as the new Version
     * @throws ValidationException Exception thrown when the version is not 1.0
     */
    public void setVersion(String str) throws ValidationException {
        Objects.requireNonNull(str, "String cannot be null");
        Version.parseString(str);
        version = str;
    }

    /**
     * This will update the operation String.
     *
     * @return A String representation of operation.
     */
    @Override
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the operation String
     *
     * @param op String to be used as new operation
     */
    public void setOperation(String op) {
        Objects.requireNonNull(op, "Operation cannot be null");
        operation = op;
    }

    /**
     * This encodes a Version into ISO8859-1 bytes.
     *
     * @param out A stream to write the bytes to.
     * @throws IOException Exception thrown when stream is not writable
     */
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "Output cannot be null");
        String DELIMETER = "\r\n";
        out.Write((operation + " " + version + DELIMETER).getBytes(ENC));
    }

    @Override
    public boolean equals(Object o) {
        try {
            Objects.requireNonNull(o, "Object cannot be null");
        } catch (NullPointerException n) {
            return false;
        }
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Version version1 = (Version) o;
        return getVersion().equals(version1.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion());
    }
}