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
import tiktak.serialization.MessageOutput;

import java.io.ByteArrayOutputStream;

public class MessageOutputTest {

    @Test
    @DisplayName("[Invalid] Test MessageOutput Constructor: Null")
    public void testMessageOutputConstructorNull() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new MessageOutput(null));
    }

    @Test
    @DisplayName("[Invalid] Test MessageOutput Write: Null")
    public void testMessageOutputWriteNull() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new MessageOutput(
                        new ByteArrayOutputStream()).Write(null));
    }
}
