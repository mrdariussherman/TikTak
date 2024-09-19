/************************************************
 *
 * Author: Darius Sherman
 * Assignment: ServerAIO Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.NIODeframer;

import java.nio.ByteBuffer;

public class NIODeframerTest {
    @Test
    @DisplayName("NIODeframer Test Invalid")
    public void NIOTestInvalid(){
        NIODeframer nio = new NIODeframer();
        Assertions.assertNull(nio.getMessage(ByteBuffer.wrap("CRED ABCD".getBytes())));
    }

    @Test
    @DisplayName("NIODeframer Test Valid")
    public void NIOTestValid(){
        NIODeframer nio = new NIODeframer();
        Assertions.assertNotNull(nio.getMessage(ByteBuffer.wrap("ID ABCD\r\n".getBytes())));
    }
}
