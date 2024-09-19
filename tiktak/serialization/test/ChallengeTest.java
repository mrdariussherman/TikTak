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
import tiktak.serialization.Challenge;
import tiktak.serialization.MessageOutput;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ChallengeTest {

    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Invalid] Test Challenge Constructor: Null")
    public void testChallengeConstructorNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge(null));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Constructor: Empty")
    public void testChallengeConstructorEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge(""));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Constructor: NonNumeric")
    public void testChallengeConstructorNonN() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge("123+5"));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Setter: Null")
    public void testChallengeSetterNull() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge("12345").setNonce(null));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Setter: Empty")
    public void testChallengeSetterEmpty() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge("12345").setNonce(""));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Setter: NonNumeric")
    public void testChallengeSetterNonN() {
        Assertions.assertThrows(ValidationException.class,
                () -> new Challenge("12345").setNonce("123+5"));
    }

    @Test
    @DisplayName("[Invalid] Test Challenge Getter: Valid Nonce")
    public void testChallengeGetNonce() throws ValidationException {
        Challenge ch = new Challenge("12345");
        assertEquals("12345", ch.getNonce());
    }

    @Test
    @DisplayName("[Valid] Test Challenge Setter: Valid Nonce")
    public void testChallengeSetNonce() throws ValidationException {
        assertEquals("67890", new Challenge("12345")
                .setNonce("67890").getNonce());
    }

    @Test
    @DisplayName("[Invalid] Test Challenge toString: Same toString")
    public void testChallengeToString() throws ValidationException {
        Challenge ch = new Challenge("12345");
        assertEquals("Challenge: nonce=12345", ch.toString());
    }

    @Test
    @DisplayName("[Valid] Test Challenge getOperation: Valid Operation")
    public void testIDOperation() throws ValidationException {
        assertEquals("CLNG", new Challenge("12345").getOperation());
    }

    @Test
    @DisplayName("[Valid] Test Challenge Encode: Same Encoding")
    public void testChallengeEncode() throws ValidationException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Challenge("12345").encode(new MessageOutput(out));
        assertEquals("CLNG 12345\r\n", new String(out.toByteArray(), ENC));
    }

    @Test
    @DisplayName("Test Challenge Equals")
    public void testChallengeEquals() throws ValidationException {
        Challenge base = new Challenge("12345");
        Challenge copy = base;
        assertEquals(base, copy);
    }

    @Test
    @DisplayName("Test Challenge !Equals")
    public void testChallengeNotEquals() throws ValidationException {
        Challenge base = new Challenge("12345");
        assertNotEquals(base, null);
    }

    @Test
    @DisplayName("Test Challenge Hash")
    public void testChallengeHash() throws ValidationException {
        Challenge base = new Challenge("12345");
        Challenge yikes = new Challenge("4514");
        assertNotEquals(base.hashCode(), yikes.hashCode());
    }

    @Test
    public void equalsTest() throws ValidationException {
        String nonce = "1111111111111111";
        assertEquals(new Challenge(nonce), new Challenge(
                nonce));
    }

}
