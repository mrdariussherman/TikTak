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
import tiktak.serialization.Ack;
import tiktak.serialization.MessageOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AckTest {
    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Valid] Test Ack toString")
    public void testAckToString() {
        Assertions.assertEquals("Ack", new Ack().toString());
    }

    @Test
    @DisplayName("[Valid] Test Ack encode")
    public void testAckEncode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Ack().encode(new MessageOutput(out));
        assertArrayEquals("ACK\r\n".getBytes(ENC), out.toByteArray());
    }

    @Test
    @DisplayName("[Valid] Test Ack getOperation")
    public void testAckGetOperation() {
        assertEquals("ACK", new Ack().getOperation());
    }

    @Test
    @DisplayName("Test Ack Equals")
    public void testAckEquals() {
        Ack base = new Ack();
        Ack copy = new Ack();
        assertEquals(base, copy);
    }

    @Test
    @DisplayName("Test Ack !Equals")
    public void testAckNotEquals() {
        Ack base = new Ack();
        assertNotEquals(base, null);
    }

    @Test
    @DisplayName("Test Ack Hash")
    public void testAckHash() {
        Ack base = new Ack();
        Ack copy = new Ack();
        assertEquals(base.hashCode(), copy.hashCode());
    }
}
