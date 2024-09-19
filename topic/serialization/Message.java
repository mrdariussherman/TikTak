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

public abstract class Message {
    protected long queryID;     // queryID
    protected int MIN_POSTS = 0; // Min number of posts
    protected int MAX_POSTS = 65535; // Max number of posts
    protected long MAX_ID = 4294967295L; // Max ID
    protected long MIN_ID = 0L; // Min ID
    protected static String ENC = "ISO8859-1";

    /**
     * Static function for setting message type and Message factory
     *
     * @param ID Long with all the data from the wire
     * @throws IllegalArgumentException Exception thrown when ID is empty
     */
    public final Message setQueryID(long ID)
            throws IllegalArgumentException {
        // Make sure the the ID has data
        if ( ID > MAX_ID || ID < MIN_ID ) {
            throw new IllegalArgumentException("ID invalid, not within " +
                    "bounds!");
        }
        this.queryID = ID;
        return this;
    }

    /**
     * Abstract function to get QueryID
     * @return ID in long format
     */
    public long getQueryID(){ return this.queryID; }

    /**
     * Abstract function to encode Message to wire
     * @return byte[] to be sent across the wire
     */
    public abstract byte[] encode();

    /**
     * Abstract function to convert Message to string
     * @return String representation of Message
     */
    public abstract String toString();

    /**
     * Parse queryID from data
     * @return queryID
     */
    public long parseQueryID(byte[] data){
        ByteArrayOutputStream qID = new ByteArrayOutputStream();
        qID.write(data, 2, 4);
        ByteBuffer qID_to_int = ByteBuffer.wrap(qID.toByteArray());
        qID_to_int.position(0);
        int val = qID_to_int.getInt();
        return Integer.toUnsignedLong(val);
    }

    /**
     * Check flags
     */
    public ErrorCode checkFlags(byte[] data) {
        short flag = data[1];
        flag &= 0x00FF;
        return ErrorCode.getErrorCodeResponse(flag);
    }

    /**
     * Check reserved bit
     * @throws TopicException Thrown when the reserved bits are not 0x0
     */
    public void checkReservedBits(byte[] data) throws TopicException {
        byte reserved = data[0];
        reserved &= 0x07;
        if ( reserved != 0x00 ){
            throw new TopicException(ErrorCode.NETWORKERROR, "Non-zero " +
                    "reserve");
        }
    }

    /**
     * Check version
     * @throws TopicException Thrown when an version is not equal to 2
     */
    public void checkVersion(byte[] data) throws TopicException {
        if ( ((data[0] >> 4) & 0xF) != 0x02 ){
            throw new TopicException(ErrorCode.BADVERSION);
        }
    }
}
