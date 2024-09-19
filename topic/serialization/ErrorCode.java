/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/
package topic.serialization;

import java.util.Arrays;
import java.util.Objects;

public enum ErrorCode {
    NOERROR(0, "No error"),    // There is no error
    BADVERSION(1, "Bad version"), // The version number is not valid
    UNEXPECTEDERRORCODE(2, "Unexpected error code"), // Invalid Error Code
    UNEXPECTEDPACKETTYPE(3, "Unexpected packet type"), // Invalid packet
    PACKETTOOLONG(4, "Packet too long"), // Packet size is too long
    PACKETTOOSHORT(5, "Packet too short"), // Packet size is too short
    NETWORKERROR(7, "Network error"), // Network error ErrorCode
    VALIDATIONERROR(8, "Validation error"); // Validation error ErrorCode

    private int ordinal;
    private String name;

    ErrorCode(int i, String name) {
        Objects.requireNonNull(name, "Error message cannot be null!");

        if ( i == 6 || i < 0 || i > 8 ){
            throw new IllegalArgumentException("ErrorCode ordinal number is " +
                    "not within the specified range.");
        }

        ordinal = i;
        this.name = name;
    }

    /**
     * getErrorCodeValue() - Returns the ordinal number of the ErrorCode Enum
     */
    public int getErrorCodeValue(){
        return ordinal;
    }

    /**
     * getErrorMessage() - Returns the string name of the ErrorCode Enum
     */
    public String getErrorMessage(){
        return name;
    }

    /**
     * getErrorCode() - Returns an ErrorCode based on an ordinal value
     */
    public static ErrorCode getErrorCode(int errorCodeValue)
            throws IllegalArgumentException {
        if ( errorCodeValue == 6 || errorCodeValue < 0 || errorCodeValue > 8){
            throw new IllegalArgumentException("Invalid error code value " +
                    "provided (" + errorCodeValue + ")!");
        }

        return Arrays.stream(values()).filter( i -> i.ordinal == errorCodeValue).findFirst().get();
    }

    /**
     * getErrorCodeResponse() - Returns an ErrorCode based on an ordinal value
     */
    public static ErrorCode getErrorCodeResponse(int errorCodeValue) {
        if ( errorCodeValue == 0 ){
            return NOERROR;
        } else if ( errorCodeValue == 1 ){
            return BADVERSION;
        } else if ( errorCodeValue == 2 ){
            return UNEXPECTEDERRORCODE;
        } else if ( errorCodeValue == 3 ){
            return UNEXPECTEDPACKETTYPE;
        } else if ( errorCodeValue == 4 ){
            return PACKETTOOLONG;
        } else if ( errorCodeValue == 5 ){
            return PACKETTOOSHORT;
        } else if ( errorCodeValue == 7 ){
            return NETWORKERROR;
        } else if ( errorCodeValue == 8 ){
            return VALIDATIONERROR;
        } else {
            return UNEXPECTEDERRORCODE;
        }
    }
}
