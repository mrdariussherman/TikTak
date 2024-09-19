package tiktak.serialization;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

public class NIODeframer {
    private ArrayList<String> messages;

    public NIODeframer(){
        messages = new ArrayList<>();
    }

    public String getMessage(ByteBuffer buff)
            throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(buff, "Buffer cannot be null");
        int cutLength = 0;

        try {
            String DELIMITER = "\r\n";
            String ENC = "ISO8859-1";
            if ( new String(buff.array(), ENC).contains(DELIMITER) ){
                String[] message = new String(buff.array(), ENC).split(DELIMITER);

                for ( String x : message ){
                    if ( x.matches("[a-zA-Z0-9\\s]+" ) ){
                        try {
                            Message test = Message.decode(x + DELIMITER);
                            messages.add(x + DELIMITER);
                        } catch ( ValidationException ignored ){ }
                        cutLength += (x + DELIMITER).getBytes(ENC).length;
                    }
                }

                byte[] trash = new byte[cutLength];
                buff.get(trash);
            }
        } catch ( UnsupportedEncodingException ignored ){}

        if ( messages.size() > 0 ){
            return messages.remove(0);
        }  else {
            return null;
        }
    }
}
