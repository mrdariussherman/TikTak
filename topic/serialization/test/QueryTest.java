/************************************************
 *
 * Author: Darius Sherman
 * Class: CSI 4321
 *
 ************************************************/
package topic.serialization.test;

import org.junit.jupiter.api.Test;
import topic.serialization.Query;
import topic.serialization.TopicException;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {
    @Test
    void testDecodeReqPost() throws TopicException {
        byte[] encodedQuery =
                new BigInteger("200000000101000A", 16).toByteArray();
        Query q = new Query(encodedQuery);
        assertEquals(q.getRequestedPosts(), 10);
    }

    @Test
    void testDecodeQueryID() throws TopicException {
        byte[] encodedQuery =
                new BigInteger("200000000101000A", 16).toByteArray();
        Query q = new Query(encodedQuery);
        assertEquals(q.getQueryID(), 257);
    }

    @Test
    void testDecodeInvalidA(){
        byte[] encodedQuery =
                new BigInteger("300000000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Query(encodedQuery));
    }

    @Test
    void testDecodeInvalidB(){
        byte[] encodedQuery =
                new BigInteger("20000000000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Query(encodedQuery));
    }

    @Test
    void testDecodeNull(){
        assertThrows(TopicException.class, ()-> new Query(null));
    }

    @Test
    void testDecodeInvalidD(){
        byte[] encodedQuery =
                new BigInteger("20FFFFFFFF", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Query(encodedQuery));
    }

    @Test
    void testEqualsSame() throws TopicException {
        Query q = new Query(new BigInteger("200000000101000A", 16).toByteArray());
        Query a = q;
        assertEquals(q, a);
    }

    @Test
    void testEqualsSimilar() throws TopicException {
        Query q = new Query(new BigInteger("200000000101000A", 16).toByteArray());
        Query a = new Query(new BigInteger("200000000101000A", 16).toByteArray());
        assertEquals(q, a);
    }

    @Test
    void testEqualsDifferent() throws TopicException {
        Query q = new Query(new BigInteger("200000000101000A", 16).toByteArray());
        Query a =
                new Query(new BigInteger("200000000201000A", 16).toByteArray());
        assertNotEquals(q, a);
    }

    @Test
    void testHashCode() throws TopicException {
        Query q = new Query(new BigInteger("200000000101000A", 16).toByteArray());
        Query a = new Query(new BigInteger("200000000201000A", 16).toByteArray());
        assertNotEquals(q.hashCode(), a.hashCode());
    }

    @Test
    void testToString() throws TopicException {
        byte[] encodedQuery =
                new BigInteger("200000000101000A", 16).toByteArray();
        Query q = new Query(encodedQuery);
        assertEquals(q.toString(), "Query: QueryID=257 ReqPosts=10");
    }

    @Test
    public void setInvlaidQueryID(){
        assertThrows(IllegalArgumentException.class,
                () -> new Query(4294967296L, 100));
    }

    @Test
    public void nonZeroReserve(){
        byte[] encodedQuery =
                new BigInteger("2F0000000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Query(encodedQuery));
    }

    @Test
    public void errorCodeHasError(){
        byte[] encodedQuery =
                new BigInteger("200200000101000A", 16).toByteArray();
        assertThrows(TopicException.class, () -> new Query(encodedQuery));
    }

    @Test
    public void setPostsTooMany(){
        assertThrows(IllegalArgumentException.class, () -> new Query(1245, 9999999));
    }
}
