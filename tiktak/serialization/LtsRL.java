/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 1
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LtsRL extends Message {
    private String category;
    private byte[] image;

    /**
     * LtsRL Constructor
     * @param str Image category
     * @param img Encoded image
     * @throws ValidationException thrown when category or image is invalid
     */
    public LtsRL(String str, byte[] img)
            throws ValidationException {
        if ( str == null ){
            throw new ValidationException("193742",
                    "Category cannot be null!");
        }

        category = validateCategory(str);
        image = validateImage(img);

        setOperation("LTSRL");
    }

    public static String parseCategory(String message){
        return message.split(" ")[1];
    }

    public static byte[] parseImage(String message){
        try {
            return Base64.getDecoder().decode(message.split(" ")[2].getBytes(ENC));
        } catch ( Exception ignored ){ }
        return null;
    }

    /**
     * LrsRL toString
     * @return String representation of LtsRL
     */
    public String toString() {
        return "LtsRL: category=" + category + " image=" + Base64.getEncoder().withoutPadding().encode(image).length +
                " bytes";
    }

    /**
     * Getter for category
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setter for category
     * @param str new category
     * @return Object with new category
     * @throws ValidationException Thrown when category is invalid
     */
    public LtsRL setCategory(String str) throws ValidationException {
        category = validateCategory(str);
        return this;
    }

    /**
     * Getter for image
     * @return image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Setter for image
     * @param img new image
     * @return Object with new image
     * @throws ValidationException Thrown when image is invalid
     */
    public LtsRL setImage(byte[] img)
            throws ValidationException {
        image = validateImage(img);
        return this;
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
        out.Write( ("LTSRL " + this.getCategory() + " " +
                new String(Base64.getEncoder().withoutPadding().encode(image), ENC) + "\r\n").getBytes(ENC) );
    }

    public String validateCategory(String str) throws ValidationException {
        if ( str == null ){
            throw new ValidationException("1949", "Category cannot be null!");
        }

        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]*$");
        Matcher matcher = pattern.matcher(str);

        if ( matcher.matches() && !(str.isBlank() || str.isEmpty() || str.contains(" ")) ) {
            return str;
        }

        throw new ValidationException("24543", "Category does not meet " +
                "required pattern");
    }

    public byte[] validateImage(byte[] img) throws ValidationException {
        if ( img == null ){
            throw new ValidationException("13711", "Image bytes cannot be " +
                    "null!");
        }

        return img;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LtsRL ltsRL = (LtsRL) o;
        return getCategory().equals(ltsRL.getCategory()) &&
                Arrays.equals(getImage(), ltsRL.getImage());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getCategory());
        result = 31 * result + Arrays.hashCode(getImage());
        return result;
    }
}
