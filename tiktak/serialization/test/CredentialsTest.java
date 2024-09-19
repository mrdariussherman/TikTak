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
import tiktak.serialization.Credentials;
import tiktak.serialization.MessageOutput;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class CredentialsTest {
    //TikTak character encoding
    protected static String ENC = "ISO8859-1";
    private static String BASICHASH = "000102030405060708090A0B0C0D0E0F";
    private static String OTHERHASH = "1B524BD309198661801192E02F1AA7CF";
    private static byte[] BASICHASHBYTES =
            {'C', 'R', 'E', 'D', ' ', 0, 1, 2, 3, 4, 5, 6, 7,
                    8, 9, 10, 11, 12, 13, 14, 15, '\r', '\n'};
    private static byte[] BASICHASHENCODING = {67, 82, 69, 68, 32, 48, 48, 48
        , 49, 48, 50, 48, 51, 48, 52, 48, 53, 48, 54, 48, 55, 48, 56, 48, 57,
            48, 65, 48, 66, 48, 67, 48, 68, 48, 69, 48, 70, 13, 10};
    private static byte[] OTHERHASHBYTES =
            {'C', 'R', 'E', 'D', ' ', 27, 82, 75, -45, 9,
                    25, -122, 97, -128, 17, -110, -32, 47, 26,
                    -89, -49, '\r', '\n'};

    @Test
    @DisplayName("[Invalid] Test Credentials Constructor: Null")
    public void testCredentialsConstructorNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(null));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Constructor: Empty")
    public void testCredentialsConstructorEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(""));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Constructor: Invalid Hash String")
    public void testCredentialsConstructorBadHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(
                        "0102030405060708090A0B0C0D0E0G"
                ));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Constructor: Short Hash String")
    public void testCredentialsConstructorShortHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(
                        "0102030405060708090A0B0C0D0E"
                ));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Constructor: Long Hash String")
    public void testCredentialsConstructorLongHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(
                        "000102030405060708090A0B0C0D0E0F0F"
                ));
    }

    @Test
    @DisplayName("[Valid] Test Credentials Constructor: Good Hash String")
    public void testCredentialsConstructorGoodHashString() throws ValidationException {
        assertEquals(BASICHASH, new Credentials(BASICHASH).getHash());
    }

    @Test
    @DisplayName("[Valid] Test Credentials toString: Matching Hashes")
    public void testCredentialsToString() throws ValidationException {
        assertEquals("Credentials: hash=" + BASICHASH,
                new Credentials(BASICHASH).toString());
    }

    @Test
    @DisplayName("[Valid] Test Credentials Getter: Same Hash")
    public void testCredentialsGetHash() throws ValidationException {
        assertEquals(BASICHASH,
                new Credentials(BASICHASH).getHash());
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Setter: Null")
    public void testCredentialsSetHashNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(BASICHASH).setHash(null));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Setter: Empty")
    public void testCredentialsSetHashEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(BASICHASH).setHash(""));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Setter: Invalid Hash String")
    public void testCredentialsSetBadHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(null)
                        .setHash("0102030405060708090A0B0C0D0E0G"));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Setter: Short Hash String")
    public void testCredentialsSetHashShortHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(BASICHASH)
                        .setHash("0102030405060708090A0B0C0D0E"));
    }

    @Test
    @DisplayName("[Invalid] Test Credentials Setter: Long Hash String")
    public void testCredentialsSetHashLongHashString() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Credentials(BASICHASH)
                        .setHash("000102030405060708090A0B0C0D0E0F0F"));
    }

    @Test
    @DisplayName("[Valid] Test Credentials Setter: Good Hash String")
    public void testCredentialsSetHAshGoodHashString() throws ValidationException {
        assertEquals(BASICHASH,
                new Credentials(BASICHASH).setHash(BASICHASH).getHash());
    }

    @Test
    @DisplayName("[Valid] Test Credentials Operation Getter: Same Operation")
    public void testCredentialsGetOperation() throws ValidationException {
        assertEquals("CRED", new Credentials(BASICHASH).getOperation());
    }

    @Test
    @DisplayName("Test Credentials Equals")
    public void testCredentialsEqual() throws ValidationException {
        Credentials base = new Credentials(BASICHASH);
        Credentials copy = new Credentials(BASICHASH);
        assertEquals(base, copy);
    }

    @Test
    @DisplayName("Test Credentials Hash")
    public void testCredentialsHash() throws ValidationException {
        assertNotEquals(new Credentials(BASICHASH).hashCode(),
                new Credentials(OTHERHASH).hashCode());
    }

    @Test
    @DisplayName("Test Credentials Encode")
    public void testCredentialsEncode() throws ValidationException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Credentials(BASICHASH).encode(new MessageOutput(out));
        System.out.println(Arrays.toString(out.toByteArray()));
        assertArrayEquals(BASICHASHENCODING, out.toByteArray());
    }

    @Test
    @DisplayName("Test Credentials Parse")
    public void testCredentialsParse() {
        String parsed = Credentials.parseHash(BASICHASH);
        assertEquals(parsed, BASICHASH);
    }

}
