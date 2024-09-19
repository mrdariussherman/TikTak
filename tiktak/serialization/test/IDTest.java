/************************************************
 *
 * Author: Michael Prescott and Darius Sherman
 * Assignment: Program 0 Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class IDTest {

    //Message used in Donahoo's tests
    private static final byte[] IDENC =
            "ID user\r\n".getBytes(StandardCharsets.ISO_8859_1);
    //ID value used in Donahoo's tests
    private static final String USER = "user";
    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Invalid] Test ID Constructor: Null")
    public void testIDConstructorNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID(null));
    }

    @Test
    @DisplayName("[Invalid] Test ID Constructor: Empty")
    public void testIDConstructorEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID(""));
    }

    @Test
    @DisplayName("[Invalid] Test ID Constructor: NonAlphaNumeric")
    public void testIDConstructorNonAN() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID("1i32n+"));
    }

    @Test
    @DisplayName("[Invalid] Test ID Setter: Null")
    public void testIDSetterNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID("12345").setID(null));
    }

    @Test
    @DisplayName("[Invalid] Test ID Setter: Empty")
    public void testIDSetterEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID("12345").setID(""));
    }

    @Test
    @DisplayName("[Invalid] Test ID Setter: NonAlphaNumeric")
    public void testIDSetterNonAN() {
        Assertions.assertThrows(ValidationException.class,
                () -> new ID("12345").setID("1i32n+"));
    }

    @Test
    @DisplayName("[Valid] Test ID Getter: Valid ID")
    public void testIDGetID() throws ValidationException {
        ID id = new ID("12345");
        assertEquals("12345", id.getID());
    }

    @Test
    @DisplayName("[Valid] Test ID Setter: Valid ID")
    public void testIDSetID() throws ValidationException {
        assertEquals("67890", new ID("12345")
                .setID("67890").getID());
    }

    @Test
    @DisplayName("[Valid] Test ID toString: Same toString")
    public void testIDToString() throws ValidationException {
        assertEquals("ID: id=12345", new ID("12345").toString());
    }

    @Test
    @DisplayName("[Valid] Test ID getOperation: Valid Operation")
    public void testIDOperation() throws ValidationException {
        assertEquals("ID", new ID("12345").getOperation());
    }

    @Test
    @DisplayName("[Valid] Test ID Encode: Same Output")
    public void testIDEncode() throws ValidationException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ID("12345").encode(new MessageOutput(out));
        assertEquals("ID 12345\r\n", new String(out.toByteArray(), ENC));
    }

    @Test
    public void testEncode()
            throws NullPointerException, IOException, ValidationException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        new ID(USER).encode(new MessageOutput(bout));
        assertArrayEquals(IDENC, bout.toByteArray());
    }

    @Test
    public void testDecode()
            throws NullPointerException, IOException, ValidationException {
        ID id = (ID) Message.decode(
                new MessageInput(new ByteArrayInputStream(IDENC))
        );
        assertEquals(USER, id.getID());
        assertEquals("ID", id.getOperation());
    }

    @Test
    public void hashTest() throws ValidationException {
        assertNotEquals(new ID("Darius").hashCode(), new ID("Sherman").hashCode());
    }
}
