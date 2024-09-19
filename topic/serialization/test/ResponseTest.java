/************************************************
 *
 * Author: Darius Sherman
 * Class: CSI 4321
 *
 ************************************************/
package topic.serialization.test;

import org.junit.jupiter.api.Test;
import topic.serialization.ErrorCode;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {
    protected static String ENC = "ISO8859-1";

    @Test
    public void decodeTestA() throws TopicException {
        byte[] encoded =
                new BigInteger("2800000001010000", 16).toByteArray();
        Response r = new Response(encoded);
        assertEquals(r.getErrorCode().ordinal(), 0);
    }

    @Test
    public void decodeTestBInvalid() {
        byte[] encoded =
                new BigInteger("2806FFFFFFFFF", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encoded));
    }

    @Test
    public void decodeTestInvalid() {
        byte[] encoded =
                new BigInteger("FFFFFFFFF", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encoded));
    }

    @Test
    public void decodeTestB() throws TopicException {
        byte[] encoded =
                new BigInteger("2800000001010000", 16).toByteArray();
        Response r = new Response(encoded);
        assertEquals(r.getPosts().size(), 0);
    }

    @Test
    public void decodeTestCToString() throws TopicException {
        byte[] encoded =
                new BigInteger("2800000001010000", 16).toByteArray();
        Response r = new Response(encoded);
        assertEquals(r.toString(), "Response: QueryID=257 Posts=0");
    }

    @Test
    public void constructorTestA() {
        Response r = new Response(257, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        assertEquals(r.toString(), "Response: QueryID=257 Posts=0");
    }

    @Test
    public void constructorTestB() {
        assertThrows(IllegalArgumentException.class, () -> new Response(257, null,
                new ArrayList<>() {
                }));
    }

    @Test
    public void constructorTestC() {
        assertThrows(IllegalArgumentException.class, () -> new Response(257, ErrorCode.NOERROR,
                null));
    }

    @Test
    public void constructorTestEncode() throws UnsupportedEncodingException {
        byte[] encoded =
                new BigInteger("2800000001010000", 16).toByteArray();
        Response r = new Response(257, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        assertEquals(new String(r.encode(), ENC), new String(encoded, ENC));
    }

    @Test
    public void constructorTestEncodeWPosts() {
        ArrayList<String> posts = new ArrayList<>();
        posts.add("Test: TOST");
        posts.add("Hello: TOST");
        Response r = new Response(257, ErrorCode.NOERROR,
                posts);
        assertEquals(r.getPosts().toString(), "[Test: TOST, Hello: TOST]");
    }

    @Test
    public void encodeTest(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("TOST");
        byte[] encodedResponse =
                new BigInteger("28000000000100010004544F5354", 16).toByteArray();
        Response r = new Response(001, ErrorCode.NOERROR,
                posts);
        assertArrayEquals(encodedResponse, r.encode());
    }

    @Test
    public void constructorTestDecodeWPosts() {
        ArrayList<String> posts = new ArrayList<>();
        posts.add("TOST");
        posts.add("LOL");
        Response r = new Response(200, ErrorCode.NOERROR, posts);
    }

    @Test
    public void equalsTestSame() {
        Response r = new Response(257, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        Response c = r;
        assertEquals(r, c);
    }

    @Test
    public void equalsTestDifferent() {
        Response r = new Response(257, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        Response c = null;
        assertNotSame(r, c);
    }

    @Test
    public void hashTest() {
        Response r = new Response(257, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        Response c = new Response(258, ErrorCode.NOERROR,
                new ArrayList<>() {
                });
        assertNotEquals(r.hashCode(), c.hashCode());
    }

    @Test
    public void getErrorCodeTest0(){
        ErrorCode code = ErrorCode.getErrorCode(0);
        assertEquals(ErrorCode.NOERROR, code);
    }

    @Test
    public void getErrorCodeTest1(){
        ErrorCode code = ErrorCode.getErrorCode(1);
        assertEquals(ErrorCode.BADVERSION, code);
    }

    @Test
    public void getErrorCodeTest2(){
        ErrorCode code = ErrorCode.getErrorCode(2);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeTest3(){
        ErrorCode code = ErrorCode.getErrorCode(3);
        assertEquals(ErrorCode.UNEXPECTEDPACKETTYPE, code);
    }

    @Test
    public void getErrorCodeTest4(){
        ErrorCode code = ErrorCode.getErrorCode(4);
        assertEquals(ErrorCode.PACKETTOOLONG, code);
    }

    @Test
    public void getErrorCodeTest5(){
        ErrorCode code = ErrorCode.getErrorCode(5);
        assertEquals(ErrorCode.PACKETTOOSHORT, code);
    }

    @Test
    public void getErrorCodeTest6(){
        assertThrows(IllegalArgumentException.class, () -> ErrorCode.getErrorCode(6));
    }

    @Test
    public void getErrorCodeTest7(){
        ErrorCode code = ErrorCode.getErrorCode(7);
        assertEquals(ErrorCode.NETWORKERROR, code);
    }

    @Test
    public void getErrorCodeTest8(){
        ErrorCode code = ErrorCode.getErrorCode(8);
        assertEquals(ErrorCode.VALIDATIONERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest0(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(0);
        assertEquals(ErrorCode.NOERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest1(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(1);
        assertEquals(ErrorCode.BADVERSION, code);
    }

    @Test
    public void getErrorCodeResponseTest2(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(2);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeResponseTest3(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(3);
        assertEquals(ErrorCode.UNEXPECTEDPACKETTYPE, code);
    }

    @Test
    public void getErrorCodeResponseTest4(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(4);
        assertEquals(ErrorCode.PACKETTOOLONG, code);
    }

    @Test
    public void getErrorCodeResponseTest5(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(5);
        assertEquals(ErrorCode.PACKETTOOSHORT, code);
    }

    @Test
    public void getErrorCodeResponseTest6(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(6);
        assertEquals(ErrorCode.UNEXPECTEDERRORCODE, code);
    }

    @Test
    public void getErrorCodeResponseTest7(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(7);
        assertEquals(ErrorCode.NETWORKERROR, code);
    }

    @Test
    public void getErrorCodeResponseTest8(){
        ErrorCode code = ErrorCode.getErrorCodeResponse(8);
        assertEquals(ErrorCode.VALIDATIONERROR, code);
    }

    @Test
    public void toStringManyPosts(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        assertEquals("Response: QueryID=123 Posts=HELLO, WORLD, SIC, EM, ",
                r.toString());
    }

    @Test
    public void equalsSame(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        Response p = r;
        assertEquals(p, r);
    }

    @Test
    public void equalsSimilar(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        Response p = new Response(123, ErrorCode.NOERROR, posts);
        assertEquals(p, r);
    }

    @Test
    public void equalsNull(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        Response p = null;
        assertNotEquals(p, r);
    }

    @Test
    public void hashCodeTest(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        Response p = new Response(112, ErrorCode.NOERROR, new ArrayList<>());
        assertNotEquals(r.hashCode(), p.hashCode());
    }

    @Test
    public void responseDecode() throws TopicException {
        byte[] encoded =
                new BigInteger("2800FFFFFFFF00010004746F7374", 16).toByteArray();

        Response response = new Response(encoded);
        assertEquals(response.toString(), "Response: QueryID=4294967295 Posts=tost, ");
    }

    @Test
    public void wrongMessageFormat(){
        byte[] encoded =
                new BigInteger("200000000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encoded));
    }

    @Test
    public void nullArray(){
        assertThrows(TopicException.class, () -> new Response(null));
    }

    @Test
    public void invalidPosts(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO.");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM\n");

        assertThrows(IllegalArgumentException.class, () -> new Response(123,
                ErrorCode.NOERROR, posts));
    }

    @Test
    public void postTooLong(){
        StringBuilder bob = new StringBuilder();
        for ( int i = 0; i < 655555; i++ ){
            bob.append('x');
        }

        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        posts.add(bob.toString());

        assertThrows(IllegalArgumentException.class, () -> new Response(123,
                ErrorCode.NOERROR, posts));
    }

    @Test
    public void tooManyPosts(){
        ArrayList<String> posts = new ArrayList<>();
        for ( int i = 0; i < 6543345; i++ ){
            posts.add("x");
        }

        assertThrows(IllegalArgumentException.class, () -> new Response(123,
                ErrorCode.NOERROR, posts));
    }

    @Test
    public void setInvlaidQueryID(){
        assertThrows(IllegalArgumentException.class,
                () -> new Response(4294967296L, ErrorCode.NOERROR, new ArrayList<>()));
    }

    @Test
    public void nonZeroReserve(){
        byte[] encodedQuery =
                new BigInteger("2F0000000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encodedQuery));
    }

    @Test
    public void nullPosts(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        assertThrows(IllegalArgumentException.class, () -> r.setPosts(null));
    }

    @Test
    public void nullErrorCode(){
        ArrayList<String> posts = new ArrayList<>();
        posts.add("HELLO");
        posts.add("WORLD");
        posts.add("SIC");
        posts.add("EM");
        Response r = new Response(123, ErrorCode.NOERROR, posts);
        assertThrows(IllegalArgumentException.class, () -> r.setErrorCode(null));
    }

    @Test
    public void unexpectedErrorCode(){
        byte[] encodedResponse =
                new BigInteger("28FF00000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encodedResponse) );
    }

    @Test
    public void validatePostTooLong(){
        StringBuilder bob = new StringBuilder();
        for ( int i = 0; i < 655555; i++ ) {
            bob.append('x');
        }

        assertThrows(TopicException.class, () -> new Response(123,
                ErrorCode.NOERROR, new ArrayList<>()).validatePost(bob.toString()));
    }

    @Test
    public void validatePostInvalid(){
        StringBuilder bob = new StringBuilder();
        for ( int i = 0; i < 899; i++ ) {
            bob.append('\n');
        }

        assertThrows(TopicException.class, () -> new Response(123,
                ErrorCode.NOERROR, new ArrayList<>()).validatePost(bob.toString()));
    }

    @Test
    public void notEnoughCharacters(){
        byte[] encodedResponse =
                new BigInteger("2800001111020320", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encodedResponse));
    }

    @Test
    public void tooManyCharacters(){
        byte[] encodedResponse =
                new BigInteger("2800010000010001000120202020",
                        16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encodedResponse));
    }

    @Test
    public void notEnoughForPost(){
        byte[] encodedResponse =
                new BigInteger("28000100000100010001",
                        16).toByteArray();
        assertThrows(TopicException.class, () -> new Response(encodedResponse));
    }
}
