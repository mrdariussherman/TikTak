/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 1
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization;

public class ValidationException extends Exception {
    private static final long serialVersionUID = 2048L;
    String message;
    String token;
    Throwable cause = null;

    /**
     * 2 Param ValidationException constructor
     * @param str Exception message
     * @param badToken Exception token
     */
    public ValidationException(String str, String badToken) {
        super(str);
        token = badToken;
        message = str;
    }

    /**
     * 3 Param ValidationException constructor
     * @param str Exception message
     * @param why Exception cause/throwable
     * @param tok Exception token
     */
    public ValidationException(String str, Throwable why, String tok) {
        super(str, why);
        message = str;
        cause = why;
        token = tok;
    }

    /**
     * Getter for token
     * @return token
     */
    public String getBadToken() {
        return token;
    }

}
