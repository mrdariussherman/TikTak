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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Challenge extends Message {
    private final String REG = "[0-9]+";
    private final String DELIMETER = "\r\n";
    private String nonce;

    /**
     * Challenge constructor
     *
     * @param str String to be used as the new Version
     * @throws ValidationException Exception thrown when the version is not 1.0
     */
    public Challenge(String str) throws ValidationException {
        setNonce(validateNonce(str));
        this.setOperation("CLNG");
    }

    /**
     * Parses a string to ensure that it contains the information needed for
     * Challenge
     *
     * @param data Raw String to be parsed
     * @return The nonce will be returned
     * @throws ValidationException Exception thrown when the raw String does
     *                             not contain the required information
     */
    public static String parse(String data) throws ValidationException {
        if (!data.matches("CLNG\\s[0-9]+")) {
            throw new ValidationException("4628", "Challenge message does not" +
                    " match required format. Expecting <CLNG [0-9]+>");
        }
        return data.split(" ")[1];
    }

    /**
     * Getter for nonce
     *
     * @return String representation of nonce
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * Setter for nonce
     *
     * @param str String to set nonce too
     * @return Challenge class object
     * @throws ValidationException Exception thrown when the String str does
     *                             not meet nonce requirements
     */
    public Challenge setNonce(String str) throws ValidationException {
        nonce = this.validateNonce(str);
        return this;
    }

    /**
     * Create String representation of Challenge
     *
     * @return String representation of Challenge
     */
    public String toString() {
        return ("Challenge: nonce=" + nonce);
    }

    /**
     * Encodes Challenge and outputs to stream
     *
     * @param out Byte stream to be used for output
     * @throws IOException Exception thrown when stream is not able to be
     *                     written to
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "Output cannot be null");
        out.Write(("CLNG " + getNonce() + DELIMETER).getBytes(ENC));
    }

    /**
     * Getter for operation
     *
     * @return String representation of operation
     */
    @Override
    public String getOperation() {
        return operation;
    }

    /**
     * Setter for operation
     *
     * @param op Proposed new operation String
     */
    @Override
    public void setOperation(String op) {
        operation = op;
    }

    /**
     * Validator for Nonce
     *
     * @param str Proposed nonce to be validated
     * @return Will return the valid nonce String
     * @throws ValidationException Exception thrown when str does not meet
     */
    public String validateNonce(String str) throws ValidationException {
        if (str == null) {
            throw new ValidationException("13245", "Nonce cannot be null!");
        }

        Pattern pattern = Pattern.compile(REG);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            throw new ValidationException("3435",
                    "The nonce did not meet validation " +
                            "standards. Expected: <[0-9]+>");
        }

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return getNonce().equals(challenge.getNonce());
    }

    @Override
    public int hashCode() {
        return Objects.hash(REG, getNonce());
    }
}
