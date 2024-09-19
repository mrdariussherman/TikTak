package topic.serialization.test;

import org.junit.jupiter.api.Test;
import topic.serialization.Query;
import topic.serialization.TopicException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Donatest {
    // Query with errorcode = NOERROR, id = 5, and requested posts of 2
    private byte[] encodedQuery = new byte[] {0x20, 0, 0, 0, 0, 5, 0, 2};
    private long queryid = 5;
    private int reqpost = 2;

    @Test
    void testEncode() throws TopicException {
        assertArrayEquals(encodedQuery, new Query(queryid, reqpost).encode());
    }

    @Test
    void testDecode() throws TopicException {
        Query q = new Query(encodedQuery);
        assertEquals(queryid, q.getQueryID());
        assertEquals(reqpost, q.getRequestedPosts());
    }
}