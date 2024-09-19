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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response extends Message {
    public List<String> toks = new ArrayList<>(); // the posts to be sent/were
    // received
    private ErrorCode code = ErrorCode.NOERROR;    // Error code
    private short post_count; // count of posts to be used by decode

    /**
     * Response constructor with queryID and requestedPosts
     * @param queryID QueryID
     * @param errorCode error code to send in response
     * @param posts posts to be encoded
     * @throws IllegalArgumentException One of the provided arguments is not
     * valid
     */
    public Response(long queryID, ErrorCode errorCode, List<String> posts)
            throws IllegalArgumentException {
        if ( posts == null || errorCode == null ){
            throw new IllegalArgumentException("Missing/null parameters! " +
                    "Expected <QUERY ID>, <ErrorCode>, <TikTaks>");
        }

        setErrorCode(errorCode);
        setQueryID(queryID);
        setPosts(posts);
        post_count = (short)posts.size();
    }

    /**
     * Response Constructor with queryID and requestedPosts
     * @param data data to be processed
     * @throws TopicException Error detected within data
     */
    public Response(byte[] data) throws TopicException {
        if ( data == null ){
            throw new TopicException(ErrorCode.VALIDATIONERROR);
        }
        if ( data.length < 6 ){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }

        // Check QR Bit
        // 1 = Response
        if ( ((data[0] >> 3) & 1) != 1 ){
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }

        // Check Reserved Bits
        checkReservedBits(data);

        // Get QueryID Bytes 2-5
        setQueryID(parseQueryID(data));

        // Check Flags
        code = checkFlags(data);
        if ( code == ErrorCode.UNEXPECTEDERRORCODE ||
                code == ErrorCode.PACKETTOOLONG ||
                code == ErrorCode.PACKETTOOSHORT ) {
            throw new TopicException(code);
        }

        // Check version
        checkVersion(data);

        // Get total toks 6-7
        ByteArrayOutputStream wR = new ByteArrayOutputStream();
        wR.write(data, 6, 2);
        ByteBuffer wR_to_int = ByteBuffer.wrap(wR.toByteArray());
        wR_to_int.position(0);
        post_count = wR_to_int.getShort();

        // read in all toks
        short postLength;
        StringBuilder post;
        toks.clear();

        ByteBuffer stream = null;
        if ( post_count > 0 ){
            stream = ByteBuffer.wrap(data);
            stream.position(8);

            for (int i = 0; i < post_count; i++) {
                if ( stream.remaining() >= 2 ){
                    postLength = stream.getShort();
                } else {
                    throw new TopicException(ErrorCode.PACKETTOOSHORT);
                }

                int count = postLength;
                count &= 0xFFFF;
                if ( count > MAX_POSTS ) { // This is for redundancy --
                    // should never be possible.
                    throw new TopicException(ErrorCode.VALIDATIONERROR);
                }

                post = new StringBuilder();
                if ( stream.remaining() < postLength ){
                    throw new TopicException(ErrorCode.PACKETTOOSHORT);
                }

                for (int j = 0; j < count; j++) {
                    post.append((char) stream.get());
                }

                toks.add(validatePost(post.toString()));
            }
        }

        if ( stream != null ){
            if ( stream.hasRemaining() ){
                throw new TopicException(ErrorCode.PACKETTOOLONG);
            }
        }
    }

    /**
     * Encode the Query message for transport
     * @return byte array of Query
     */
    @Override
    public byte[] encode() {
        // Create stream for output
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            // Static values
            result.write(0x28);
            byte err = (byte) code.getErrorCodeValue();
            result.write(err);
            // Query ID
            ByteBuffer qID = ByteBuffer.allocate(Integer.BYTES);
            qID.putInt( (int)getQueryID() );
            qID.position(0);
            result.write(qID.array());
            // Post Count
            ByteBuffer rP = ByteBuffer.allocate(Short.BYTES);
            rP.putShort( post_count );
            rP.position(0);
            result.write(rP.array());

            // Write toks
            for( String post : toks ){
                ByteBuffer pL = ByteBuffer.allocate(Short.BYTES);
                pL.putShort( (short)post.length() );
                pL.position(0);
                result.write( pL.array() );
                result.write( post.getBytes(ENC) );
            }
        } catch (Exception ignored){}

        // Return bytes
        return result.toByteArray();
    }

    /**
     * Return a string representation of Query
     * @return String'd Query
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Response: QueryID=");
        result.append(getQueryID());
        result.append(" Posts=");
        if (toks.size() > 0 ){
            for( String post : toks ){
                result.append(post);
                result.append(", ");
            }
        } else {
            result.append("0");
        }
        return result.toString();
    }

    public String validatePost(String post) throws TopicException {
        if ( post.length() > 65535 ){
            throw new TopicException( ErrorCode.VALIDATIONERROR );
        }

        Pattern reg = Pattern.compile("^[\\x20-\\x7E]*$");
        Matcher match = reg.matcher(post);
        if (  !match.matches() ) {
            throw new TopicException( ErrorCode.VALIDATIONERROR );
        }

        return post;
    }

    /**
     * Sets the List of Top Tics
     */
    public final Response setPosts(List<String> posts)
            throws IllegalArgumentException {
        if (posts == null){
            throw new IllegalArgumentException("Posts cannot be null!");
        }
        if (posts.size() > MAX_POSTS ){
            throw new IllegalArgumentException("Too many posts provided!");
        }

        for ( String x : posts ){
            if ( x.length() > 65535 ){
                throw new IllegalArgumentException("Invalid post, too long! " +
                        "Length is: " + x.length());
            }

            Pattern reg = Pattern.compile("^[\\x20-\\x7E]*$");
            Matcher match = reg.matcher(x);
            if (  !match.matches() ) {
                throw new IllegalArgumentException("Invalid post, character " +
                        "invalid!" );
            }
        }

        toks = posts;

        return this;
    }

    /**
     * Return a string representation of Query
     * @return String'd Query
     */
    public List<String> getPosts(){
        return toks;
    }

    /**
     * Returns the ErrorCode associated with Response
     * @return an ErrorCode object
     */
    public ErrorCode getErrorCode(){
        return code;
    }

    /**
     * Sets the ErrorCode of the response
     * @return a response object
     */
    public Response setErrorCode(ErrorCode err){
        if ( err == null ){
            throw new IllegalArgumentException("ErrorCode cannot be null");
        }
        code = err;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        if (getClass() != o.getClass()) return false;
        Response response = (Response) o;
        if ( !getPosts().equals(((Response) o).getPosts())){
            return false;
        }

        return post_count == response.post_count &&
                toks.equals(response.toks) &&
                code == response.code &&
                this.getQueryID() == response.getQueryID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toks, code, post_count, getQueryID());
    }
}
