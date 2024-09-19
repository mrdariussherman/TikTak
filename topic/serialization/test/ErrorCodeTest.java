/************************************************
 *
 * Author: Darius Sherman
 * Class: CSI 4321
 *
 ************************************************/

package topic.serialization.test;

import org.junit.jupiter.api.Test;
import tiktak.serialization.ValidationException;
import topic.serialization.ErrorCode;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorCodeTest {
    @Test
    public void getErrorCodeTest0(){
        ErrorCode code = ErrorCode.getErrorCode(0);
        assertEquals(ErrorCode.NOERROR, code);
    }

    @Test
    public void getErrorCodeTest1(){
        ErrorCode code = ErrorCode.getErrorCode(1);
        assertEquals(ErrorCode.BADVERSION, code);
    }

    @Test
    public void getErrorCodeTest2(){
        ErrorCode code = ErrorCode.getErrorCode(2);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeTest3(){
        ErrorCode code = ErrorCode.getErrorCode(3);
        assertEquals(ErrorCode.UNEXPECTEDPACKETTYPE, code);
    }

    @Test
    public void getErrorCodeTest4(){
        ErrorCode code = ErrorCode.getErrorCode(4);
        assertEquals(ErrorCode.PACKETTOOLONG, code);
    }

    @Test
    public void getErrorCodeTest5(){
        ErrorCode code = ErrorCode.getErrorCode(5);
        assertEquals(ErrorCode.PACKETTOOSHORT, code);
    }

    @Test
    public void getErrorCodeTest6(){
        assertThrows(IllegalArgumentException.class, () -> ErrorCode.getErrorCode(6));
    }

    @Test
    public void getErrorCodeTest7(){
        ErrorCode code = ErrorCode.getErrorCode(7);
        assertEquals(ErrorCode.NETWORKERROR, code);
    }

    @Test
    public void getErrorCodeTest8(){
        ErrorCode code = ErrorCode.getErrorCode(8);
        assertEquals(ErrorCode.VALIDATIONERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest0(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(0);
        assertEquals(ErrorCode.NOERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest1(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(1);
        assertEquals(ErrorCode.BADVERSION, code);
    }

    @Test
    public void getErrorCodeResponseTest2(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(2);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeResponseTest3(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(3);
        assertEquals(ErrorCode.UNEXPECTEDPACKETTYPE, code);
    }

    @Test
    public void getErrorCodeResponseTest4(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(4);
        assertEquals(ErrorCode.PACKETTOOLONG, code);
    }

    @Test
    public void getErrorCodeResponseTest5(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(5);
        assertEquals(ErrorCode.PACKETTOOSHORT, code);
    }

    @Test
    public void getErrorCodeResponseTest6(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(6);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeResponseTest7(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(7);
        assertEquals(ErrorCode.NETWORKERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest8(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(8);
        assertEquals(ErrorCode.VALIDATIONERROR, code);
    }

    @Test
    public void correctErrorMessageTest(){
        byte[] encodedQuery =
                new BigInteger("2F0000000101000A", 16).toByteArray();
        try{
            new Response(encodedQuery);
        } catch ( TopicException e ){
            assertEquals(e.getSecretMessage(), "Non-zero reserve");
        }
    }

    @Test
    public void topicGetCodeTest(){
        byte[] encodedQuery =
                new BigInteger("2F0000000101000A", 16).toByteArray();
        try{
            new Response(encodedQuery);
        } catch ( TopicException e ){
            assertEquals(e.getErrorCode(), ErrorCode.NETWORKERROR);
        }
    }

    @Test
    public void topic2ParamWCause(){
        try {
            throw new TopicException(ErrorCode.NOERROR,
                    new NullPointerException());
        } catch ( TopicException exc ){
            assertEquals(exc.getCause().getClass(), NullPointerException.class);
        }
    }

    public void helloWorld(){
        System.out.println("Hello World");
    }
}
