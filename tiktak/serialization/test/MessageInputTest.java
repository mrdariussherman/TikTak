/************************************************
 *
 * Author: Michael Prescott and Darius Sherman
 * Assignment: Program 1 Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MessageInputTest {

    private static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Invalid] Test MessageInput Constructor: Null")
    public void testMessageInputConstructor() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new MessageInput(null));
    }

    @Test
    @DisplayName("[Invalid] Test MessageInput Read: Unexpected End")
    public void testMessageInputReadBad() {
        Assertions.assertThrows(IOException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                "TIKTAK 1.0\r".getBytes(ENC)
                        )
                )));
    }

    @Test
    @DisplayName("[Valid] Test MessageInput Read: Good Message")
    public void testMessageInputReadGood() throws IOException,
            ValidationException {
        assertEquals(Version.class, Message.decode(new MessageInput(
                new ByteArrayInputStream("TIKTAK 1.0\r\n".getBytes(ENC)))).getClass());
    }

    @Test
    @DisplayName("[Valid] Test MessageInput Read: Multi Message")
    public void testMessageInputMultiMessage() throws IOException,
            ValidationException {
        MessageInput in = new MessageInput(
                new DataInputStream(
                        new ByteArrayInputStream(
                                "ACK\r\nTOST\r\nID 12345abc\r\nTIKTAK 1.0\r\n"
                                        .getBytes(ENC))));

        assertEquals(Ack.class, Message.decode(in).getClass());
        assertEquals(Tost.class, Message.decode(in).getClass());
        assertEquals(new ID("12345abc"), Message.decode(in));
        assertEquals(Version.class, Message.decode(in).getClass());
    }
}
