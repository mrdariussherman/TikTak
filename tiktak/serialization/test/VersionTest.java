/************************************************
 *
 * Author: Michael Prescott and Darius Sherman
 * Assignment: Program 0 Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.MessageOutput;
import tiktak.serialization.ValidationException;
import tiktak.serialization.Version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VersionTest {

    //TikTak character encoding
    protected static String ENC = "ISO8859-1";

    @Test
    @DisplayName("[Valid] Test Version Value: Valid Version")
    public void testVersionVal() {
        assertEquals("1.0", new Version().getVersion());
    }

    @Test
    @DisplayName("[Valid] Test Version Operation: Valid Operation")
    public void testVersionOperation() {
        assertEquals("TIKTAK", new Version().getOperation());
    }

    @Test
    @DisplayName("[Valid] Test Version toString: Same toString")
    public void testVersionToString() {
        assertEquals("TikTak", new Version().toString());
    }

    @Test
    @DisplayName("[Valid] Test Version Encode: Same Output")
    public void testVersionEncode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new Version().encode(new MessageOutput(out));
        assertEquals("TIKTAK 1.0\r\n", new String(out.toByteArray(), ENC));
    }

    @Test
    @DisplayName("Test setVersion()")
    public void versionSetTest() throws ValidationException {
        Version ver = new Version();
        ver.setVersion("TIKTAK 1.0");
        assertEquals(ver.getVersion(), "TIKTAK 1.0");
    }

    @Test
    @DisplayName("Test Version Equals")
    public void versionEqualTest() throws ValidationException {
        Version ver = new Version();
        Version ver2 = new Version();
        assertEquals(ver, ver2);
    }

    @Test
    @DisplayName("Test Version !Equals")
    public void versionNotEqualTest() {
        Version ver = new Version();
        assertNotEquals(ver, null);
    }

    @Test
    @DisplayName("Test Version Hash")
    public void versionHashCodeTest() {
        assertEquals(new Version().hashCode(), new Version().hashCode());
    }

}
