/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/
package topic.serialization;

public class TopicException extends Exception {
    private static final long serialVersionUID = 20148L;
    ErrorCode code;
    String secretMessage = null;

    /**
     * 1 Param TopicException constructor
     * @param errorCode Exception enum with code and message
     */
    public TopicException(ErrorCode errorCode){
        super(errorCode.getErrorMessage());
        code = errorCode;
    }

    /**
     * 2 Param TopicException constructor
     * @param errorCode Exception enum with code and message
     * @param message Used for tracking Non-zero message
     */
    public TopicException(ErrorCode errorCode, String message){
        super(errorCode.getErrorMessage());
        code = errorCode;
        secretMessage = message;
    }

    /**
     * 2 Param TopicException constructor
     * @param errorCode Exception enum with code and message
     * @param cause Throwable that cause the exception to be thrown
     */
    public TopicException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getErrorMessage(), cause);
        code = errorCode;
    }

    /**
     * Getter for error code
     * @return code
     */
    public ErrorCode getErrorCode(){
        return code;
    }

    /**
     * Getter for secretMessage code
     * @return secretMessage
     */
    public String getSecretMessage(){
        return secretMessage;
    }
}
