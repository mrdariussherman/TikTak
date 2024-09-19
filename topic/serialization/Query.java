/************************************************
 *
 * Author: Darius Sherman
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/
package topic.serialization;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Query extends Message {
    private int posts = -1; // Number of posts requested

    /**
     * Query Contructor with queryID and requestedPosts
     * @param queryID QueryID
     * @param requestedPosts count of posts requested
     * @throws IllegalArgumentException One of the provided arguments is not
     * valid
     */
    public Query(long queryID, int requestedPosts)
            throws IllegalArgumentException {
        setRequestedPosts(requestedPosts);
        setQueryID(queryID);
    }

    /**
     * Query Constructor with queryID and requestedPosts
     * @param data Data from wire
     * @throws TopicException Error detected within data
     */
    public Query(byte[] data) throws TopicException {
        if ( data == null ){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }
        if ( data.length < 8 ){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }
        if ( data.length > 8 ){
            throw new TopicException(ErrorCode.PACKETTOOLONG);
        }

        // Check QR Bit
        // 0 for Query
        if ( ((data[0] >> 3) & 1) != 0 ){
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }

        // Check Flags
        ErrorCode code = checkFlags(data);
        if ( code != ErrorCode.NOERROR ) {
            throw new TopicException(ErrorCode.UNEXPECTEDERRORCODE);
        }

        // Check version
        checkVersion(data);

        // Check Reserved Bits
        checkReservedBits(data);

        // Get QueryID Bytes 2-5
        setQueryID(parseQueryID(data));

        // Get wanted responses 6-7
        ByteArrayOutputStream wR = new ByteArrayOutputStream();
        wR.write(data, 6, 2);
        ByteBuffer wR_to_int = ByteBuffer.wrap(wR.toByteArray());
        wR_to_int.position(0);
        int count = wR_to_int.getShort();
        count &= 0xFFFF;
        setRequestedPosts( count );
    }

    /**
     * Returns the amount of posts requested
     */
    public int getRequestedPosts(){
        return posts;
    }

    /**
     * Set number of requested posts
     * @param requestedPosts Count of posts
     * @throws IllegalArgumentException thrown when requestedPort is invalid
     */
    public Query setRequestedPosts(int requestedPosts)
            throws IllegalArgumentException {
        if ( requestedPosts > MAX_POSTS || requestedPosts < 0 ){
            throw new IllegalArgumentException("Requested posts not in margin" +
                    " - Posts: " + requestedPosts);
        }

        posts = requestedPosts;
        return this;
    }

    /**
     * Encode the Query message for transport
     * @return byte array of Query
     */
    @Override
    public byte[] encode() {
        // Result stream
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            // Write constants
            byte[] first = { 0x20, 0x00};
            result.write( first );
            // Write queryID
            ByteBuffer qID = ByteBuffer.allocate(Integer.BYTES);
            qID.putInt( (int)getQueryID());
            qID.position(0);
            result.write(qID.array());
            // Write requested posts
            ByteBuffer rP = ByteBuffer.allocate(Short.BYTES);
            rP.putShort( (short)getRequestedPosts() );
            rP.position(0);
            result.write(rP.array());
        } catch (Exception ignored){}

        // Return stream in byte[] form
        return result.toByteArray();
    }

    /**
     * Return a string representation of Query
     * @return String'd Query
     */
    @Override
    public String toString() {
        return "Query: QueryID=" + getQueryID() + " ReqPosts=" + getRequestedPosts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return posts == query.posts && getQueryID() == query.getQueryID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(posts, getQueryID());
    }
}
