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
import tiktak.serialization.Credentials;
import tiktak.serialization.Message;
import tiktak.serialization.MessageInput;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {

    //Max TCP message length
    protected final int MAXMESSAGELENGTH = 16384;
    //TikTak character encoding
    protected final String ENC = "ISO8859-1";

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
    @DisplayName("[Invalid] Test Message Decode: Empty")
    public void testDecodeEmpty() {
        Assertions.assertThrows(EOFException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream("".getBytes(ENC)))));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Too Long")
    public void testDecodeLong() {
        byte[] test = new byte[MAXMESSAGELENGTH + 1];
        Arrays.fill(test, (byte) '1');
        test[0] = 'I';
        test[1] = 'D';
        test[2] = ' ';
        test[MAXMESSAGELENGTH - 1] = '\r';
        test[MAXMESSAGELENGTH] = '\n';
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                new String(test).getBytes(ENC)))));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: No Delimeter")
    public void testDecodeNoDelimeter() {
        Assertions.assertThrows(EOFException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "TIKTAK 1.0".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Bad Delimeter")
    public void testDecodeBadDelimeter() {
        Assertions.assertThrows(EOFException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "TIKTAK 1.0\r\r".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Bad Operation")
    public void testDecodeBadOperation() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "TIKTOK 1.0\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Long ID Message")
    public void testDecodeValidLongIDMessage()
            throws ValidationException, IOException {
        byte[] bytes = new byte[MAXMESSAGELENGTH];
        Arrays.fill(bytes, (byte) 'a');

        bytes[0] = (byte) 'I';
        bytes[1] = (byte) 'D';
        bytes[2] = (byte) ' ';

        bytes[MAXMESSAGELENGTH - 2] = (byte) '\r';
        bytes[MAXMESSAGELENGTH - 1] = (byte) '\n';

        Message m = Message.decode(new MessageInput(new ByteArrayInputStream(
                new String(bytes).getBytes(ENC)
        )));

        byte[] test = new byte[MAXMESSAGELENGTH - 5];
        Arrays.fill(test, (byte) 'a');
        assertEquals("ID: id=" + new String(test),
                m.toString());
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Long Challenge Message")
    public void testDecodeValidLongChallengeMessage()
            throws ValidationException, IOException {
        byte[] bytes = new byte[MAXMESSAGELENGTH];
        Arrays.fill(bytes, (byte) '1');

        bytes[0] = (byte) 'C';
        bytes[1] = (byte) 'L';
        bytes[2] = (byte) 'N';
        bytes[3] = (byte) 'G';
        bytes[4] = (byte) ' ';

        bytes[MAXMESSAGELENGTH - 2] = (byte) '\r';
        bytes[MAXMESSAGELENGTH - 1] = (byte) '\n';

        Message m = Message.decode(new MessageInput(new ByteArrayInputStream(
                new String(bytes).getBytes(ENC)
        )));

        byte[] test = new byte[MAXMESSAGELENGTH - 7];
        Arrays.fill(test, (byte) '1');
        assertEquals("Challenge: nonce=" + new String(test),
                m.toString());
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Long Challenge Message " +
            "(String)")
    public void testDecodeValidLongChallengeMessageString()
            throws ValidationException, IOException {
        byte[] bytes = new byte[MAXMESSAGELENGTH];
        Arrays.fill(bytes, (byte) '1');

        bytes[0] = (byte) 'C';
        bytes[1] = (byte) 'L';
        bytes[2] = (byte) 'N';
        bytes[3] = (byte) 'G';
        bytes[4] = (byte) ' ';

        bytes[MAXMESSAGELENGTH - 2] = (byte) '\r';
        bytes[MAXMESSAGELENGTH - 1] = (byte) '\n';

        Message m = Message.decode(new String(bytes));

        byte[] test = new byte[MAXMESSAGELENGTH - 7];
        Arrays.fill(test, (byte) '1');
        assertEquals("Challenge: nonce=" + new String(test),
                m.toString());
    }


    @Test
    @DisplayName("[Valid] Test Message Decode: Valid TIKTAK Message")
    public void testDecodeValidTIKTAKOperation()
            throws IOException, ValidationException {
        Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "TIKTAK 1.0\r\n".getBytes(ENC)
                )));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Invalid TIKTAK Message")
    public void testDecodeValidBadTIKTAKOperation() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "TIKTAK 2.0\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid ID Message")
    public void testDecodeValidIDOperation()
            throws IOException, ValidationException {
        Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "ID 12345abcdef\r\n".getBytes(ENC)
                )));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Invalid ID Message")
    public void testDecodeInvalidIDOperation() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "ID 1823j&hd\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Challenge Message")
    public void testDecodeValidChallengeOperation()
            throws IOException, ValidationException {
        Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "CLNG 123456789\r\n".getBytes(ENC)
                )));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Invalid Challenge Message")
    public void testDecodeInvalidChallengeOperation() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "CLNG 123456789a\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: " +
            "Invalid Leading Data Version")
    public void testDecodeInvalidLeadingDataVersion() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "000000000TIKTAK 1.0\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Trailing Data Version")
    public void testDecodeValidTrailingDataVersion()
            throws IOException, ValidationException {
        Message m = Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "TIKTAK 1.0\r\n000000000".getBytes(ENC)
                )));
        assertEquals("TikTak", m.toString());
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: Invalid Leading Data ID")
    public void testDecodeInvalidLeadingDataID() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "000000000ID 12345\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Trailing Data ID")
    public void testDecodeValidTrailingDataID()
            throws IOException, ValidationException {
        Message m = Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "ID 12345\r\n000000000".getBytes(ENC)
                )));
        assertEquals("ID: id=12345", m.toString());
    }

    @Test
    @DisplayName("[Invalid] Test Message Decode: " +
            "Invalid Leading Data Challenge")
    public void testDecodeInvalidLeadingDataChallenge() {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "000000000CLNG 12345\r\n".getBytes(ENC)
                        ))));
    }

    @Test
    @DisplayName("[Valid] Test Message Decode: Valid Trailing Data Challenge")
    public void testDecodeValidTrailingDataChallenge()
            throws IOException, ValidationException {
        Message m = Message.decode(new MessageInput(
                new ByteArrayInputStream(
                        "CLNG 12345\r\n000000000".getBytes(ENC)
                )));
        assertEquals("Challenge: nonce=12345", m.toString());
    }

    @Test
    @DisplayName("Credentials - MessageInput")
    public void testCredentialsMessageInput() throws IOException, ValidationException {
        assertEquals("Credentials: hash=" + BASICHASH,
                (Message.decode(new MessageInput(new ByteArrayInputStream(BASICHASHENCODING)))).toString());
    }

    @Test
    @DisplayName("No delimeter String")
    public void testNoDelimeterString() throws IOException,
            ValidationException {
        Assertions.assertThrows(ValidationException.class,
                () -> Message.decode("ID TEST"));
    }

    @Test
    @DisplayName("No delimeter MessageInput")
    public void testNoDelimeterMessageInput() {
        Assertions.assertThrows(EOFException.class,
                () -> Message.decode(new MessageInput(new ByteArrayInputStream("ID TEST".getBytes(ENC)))));
    }

    @Test
    @DisplayName("Credentials - String")
    public void testCredentialsString() throws IOException,
            ValidationException {
        assertEquals("Credentials: hash=" + BASICHASH,
                (Message.decode(new String(BASICHASHENCODING)).toString()));
    }
}
