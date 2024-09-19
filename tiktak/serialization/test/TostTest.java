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
import tiktak.serialization.MessageOutput;
import tiktak.serialization.Tost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class TostTest {
    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Valid] Test Tost toString")
    public void testTostToString() {
        Assertions.assertEquals("Tost", new Tost().toString());
    }

    @Test
    @DisplayName("[Valid] Test Tost encode")
    public void testTostEncode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Tost().encode(new MessageOutput(out));
        assertArrayEquals("TOST\r\n".getBytes(ENC), out.toByteArray());
    }

    @Test
    @DisplayName("[Valid] Test Tost getOperation")
    public void testTostGetOperation() {
        assertEquals("TOST", new Tost().getOperation());
    }

    @Test
    @DisplayName("Tost hash test")
    public void tostHashTest(){
        assertEquals(new Tost().hashCode(), new Tost().hashCode());
    }

    @Test
    @DisplayName("Tost equal test")
    public void tostEqualTest(){
        assertEquals(new Tost(), new Tost());
    }

    @Test
    @DisplayName("Tost !equal test")
    public void tostNotEqualTest(){
        assertNotEquals(new Tost(), null);
    }
}
