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
import tiktak.app.utility.Yipper;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


public class LtsRLTest {

    private static String ENC = "ISO8859-1";
    private static byte[] TESTBASE64 =
            Base64.getEncoder().withoutPadding().encode("TEST".getBytes());
    private static byte[] test4 = {76, 84, 83, 82, 76, 32, 109, 111, 118, 105, 101, 32, 83, 71, 107, 103, 100, 50, 57, 121, 98, 71, 81, 13, 10};

    private String string = "dGVzdFN0cmluzw";
    private byte[] image = Base64.getDecoder().decode(string);

    @Test
    public void testDecode() throws IOException, ValidationException, InterruptedException {
        System.out.println(new String(test4));
        Message m = Message.decode(new MessageInput(new ByteArrayInputStream(test4)));
        LtsRL ltsRL = (LtsRL)m;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ltsRL.encode(new MessageOutput(out));

        System.out.println(out.toByteArray().length);
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: All Null")
    public void testLtsRLConstructorAllNull(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL(null, null));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: Category Null")
    public void testLtsRLConstructorCategoryNull(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL(null, TESTBASE64));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: Image Null")
    public void testLtsRLConstructorImageNull(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("12345abcdef", null));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: All Empty")
    public void testLtsRLConstructorAllEmpty(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("", TESTBASE64));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: Category Empty")
    public void testLtsRLConstructorCategoryEmpty(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("", TESTBASE64));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Constructor: Image Empty")
    public void testLtsRLConstructorImageEmpty(){
        /*Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("12345abcdef", new byte[1]));*/
    }

    @Test
    @DisplayName("[Valid] Test LtsRL toString: Correct conversion")
    public void testLtsRLToString() throws ValidationException {
        /*Assertions.assertEquals("LtsRL: category=movie image=8 bytes",
                new LtsRL("movie", TESTBASE64).toString());*/
    }

    @Test
    @DisplayName("[Valid] Test LtsRL getCategory: Correct value")
    public void testLtsRLGetCategory() throws ValidationException {
        Assertions.assertEquals("movie",
                new LtsRL("movie", TESTBASE64).getCategory());
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL setCategory: Null")
    public void testLtsRLSetCategoryNull(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("test", TESTBASE64).setCategory(null));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL setCategory: Empty")
    public void testLtsRLSetCategoryEmpty(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("test", TESTBASE64).setCategory(""));
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL setCategory: Bad value")
    public void testLtsRLSetCategoryBadCategory(){
        Assertions.assertThrows(ValidationException.class,
                () -> new LtsRL("test", TESTBASE64).setCategory("1234^5"));
    }

    @Test
    @DisplayName("[Valid] Test LtsRL setCategory: Good value")
    public void testLtsRLSetCategoryGood() throws ValidationException {
        assertEquals("movie",
                new LtsRL("test", TESTBASE64)
                        .setCategory("movie").getCategory());
    }

    @Test
    @DisplayName("[Valid] Test LtsRL getImage: Same value")
    public void testLtsRLGetImage() throws ValidationException {
        /*Assertions.assertArrayEquals("TEST".getBytes(),
                new LtsRL("test", TESTBASE64).getImage());*/
    }

    @Test
    @DisplayName("[Valid] Test LtsRL setImage: Same value")
    public void testLtsRLSetImage() throws ValidationException {
        /*Assertions.assertArrayEquals("TEST".getBytes(),
                new LtsRL("test",
                        TESTBASE64).setImage(TESTBASE64).getImage());*/
    }

    @Test
    @DisplayName("[Valid] Test LtsRL Test Encode: Correct Output")
    public void testLtsRlEncode() throws ValidationException, IOException {
        /*ByteArrayOutputStream out = new ByteArrayOutputStream();
        new LtsRL("test", TESTBASE64).encode(new MessageOutput(out));
        assertArrayEquals(("LTSRL test " +
                        new String(TESTBASE64) + "\r\n")
                            .getBytes(ENC), out.toByteArray());*/
    }

    @Test
    @DisplayName("[Valid] Test Message Decode LtsRL: Same Values")
    public void testMessageDecodeLtsRL()
            throws ValidationException, IOException, InterruptedException {
       /* assertEquals(new LtsRL("test", TESTBASE64),
                Message.decode(new MessageInput(
                        new ByteArrayInputStream(("LTSRL test "
                        + new String(TESTBASE64) + "\r\n").getBytes(ENC)))));*/
    }

    @Test
    @DisplayName("[Invalid] Test LtsRL Equals Different Types")
    public void testLtsRLEqualsDifferent() throws ValidationException {
        assertEquals(false,
                new LtsRL("test", TESTBASE64).equals(
                        new LtsRL("test1", TESTBASE64)
                ));
    }

    @Test
    @DisplayName("[Valid] Test LtsRL Constructor Base64")
    public void testLtsRLConstructorUploadSite() throws IOException{
        System.out.println(new String(Base64.getMimeDecoder().decode(
                new String(new byte[]{83, 71, 107, 103, 100, 50, 57, 121,
                        98, 71, 81, 13, 10}).getBytes(ENC))));
    }

    @Test
    public void testLtsRLRealFile() throws Exception{
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        Yipper yipper = new Yipper("yipper.html");

        yipper.updateWithImage("haha", bytes);
        System.out.println(bytes.length);
        System.out.println(encoded.length);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new LtsRL("haha", encoded).encode(new MessageOutput(out));

        Message m = Message.decode(new MessageInput(new ByteArrayInputStream(out.toByteArray())));

        assertArrayEquals(encoded, ((LtsRL)m).getImage());
    }

    @Test
    @DisplayName("Hash code different")
    public void hashCodeTestDifferent() throws IOException,
            ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        assertNotEquals( new LtsRL("test", encoded).hashCode(), new LtsRL(
                "test1", encoded).hashCode() );
    }

    @Test
    @DisplayName("Hash code equals")
    public void hashCodeTest() throws IOException, ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        assertEquals( new LtsRL("test", encoded).hashCode(), new LtsRL(
                "test", encoded).hashCode() );
    }

    @Test
    @DisplayName("Equals")
    public void equalsTest() throws IOException, ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        assertEquals( new LtsRL("test", encoded), new LtsRL(
                "test", encoded) );
    }

    @Test
    @DisplayName("To String")
    public void toStringTest() throws IOException, ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        String result = "LtsRL: category=test image=2239 bytes";
        assertEquals( new LtsRL("test", encoded).toString(), result );
    }

    @Test
    @DisplayName("get Operations")
    public void getOperationTest() throws IOException, ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        assertEquals( new LtsRL("test", encoded).getOperation(), "LTSRL" );
    }

    @Test
    public void setImageTest() throws IOException, ValidationException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/BU.png"));
        byte[] encoded = Base64.getEncoder().withoutPadding().encode(bytes);
        LtsRL img = new LtsRL("test", encoded);
        img = img.setImage(image);
        assertEquals("LtsRL: category=test image=14 bytes", img.toString());
    }

    @Test
    public void parseBadImage(){
        assertNull(LtsRL.parseImage("*****"));
    }
}
