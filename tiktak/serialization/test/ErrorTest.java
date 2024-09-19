/************************************************
 *
 * Author: Michael Prescott and Darius Sherman
 * Assignment: Program 1 Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ErrorTest {
    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Invalid] Test Error Constructor: Null Message")
    public void testErrorConstructorNullMessage() throws NullPointerException {
        Assertions.assertThrows(ValidationException.class,
                () -> new Error(123, null));
    }

    @Test
    @DisplayName("[Inalid] Test Error Constructor: Code too short")
    public void testErrorConstructorInvalidCodeShort() throws ValidationException {
        Assertions.assertThrows(ValidationException.class,
                () -> new Error(12, "Hello World"));
    }

    @Test
    @DisplayName("[Inalid] Test Error Constructor: Code too long")
    public void testErrorConstructorInvalidCodeLong() throws ValidationException {
        Assertions.assertThrows(ValidationException.class,
                () -> new Error(1122, "Hello World"));
    }


    @Test
    @DisplayName("[Valid] Test Error toString: Same toString")
    public void testErrorToString() throws ValidationException {
        Error er = new Error(123, "Test error message");
        assertEquals("Error: code=123 message=Test error message",
                er.toString());
    }

    @Test
    @DisplayName("[Valid] Test Error getOperation")
    public void testErrorGetOperation() throws ValidationException {
        assertEquals("ERROR",
                new Error(112, "Test").getOperation());
    }

    @Test
    @DisplayName("[Valid] Test Error getMessage Constructor")
    public void testErrorGetMessageConstructor() throws ValidationException {
        assertEquals("Test message",
                new Error(112, "Test message").getMessage());
    }

    @Test
    @DisplayName("[Valid] Test Error getCode Constructor")
    public void testErrorGetCodeConstructor() throws ValidationException {
        assertEquals(112,
                new Error(112, "Test message").getCode());
    }

    @Test
    @DisplayName("[Valid] Test Error getCode Setter")
    public void testErrorGetCodeSetter() throws ValidationException {
        assertEquals(234,
                new Error(112, "Test message").setCode(234).getCode());
    }

    @Test
    @DisplayName("[Valid] Test Error getMessage Setter")
    public void testErrorGetMessageSetter() throws ValidationException {
        Error er = new Error(254, "Test message");
        assertEquals("Test", er.setMessage("Test").getMessage());
    }

    @Test
    @DisplayName("[Invalid] Test Error setCode: Code too short")
    public void testErrorSetCodeShort() throws ValidationException {
        Error er = new Error(112, "Hello World");
        Assertions.assertThrows(ValidationException.class,
                () -> er.setCode(12));
    }

    @Test
    @DisplayName("[Invalid] Test Error setCode: Code too long")
    public void testErrorSetCodeLong() throws ValidationException {
        Assertions.assertThrows(ValidationException.class,
                () -> new Error(112, "Hello World").setCode(12342));
    }

    @Test
    @DisplayName("[Valid] Test Version Encode: Same Output")
    public void testErrorEncode() throws IOException, ValidationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Error(124, "Test encode").encode(new MessageOutput(out));
        assertEquals("ERROR 124 Test encode\r\n", new String(out.toByteArray(),
                ENC));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Error")
    public void testMessageDecodeError() throws ValidationException,
            IOException {
        assertEquals(new Error(123, "test"),
                Message.decode(
                        new MessageInput(
                                new ByteArrayInputStream(
                                        "ERROR 123 test\r\n".getBytes(ENC)
                                ))));
    }

    @Test
    public void invalidMessage(){
        Assertions.assertThrows(ValidationException.class,
                () -> new Error(201, "*&%#"));
    }

    @Test
    public void hashTest() throws ValidationException {
        assertNotEquals(new Error(200, "hello").hashCode(), new Error(201,
                "hello").hashCode());
    }

}
