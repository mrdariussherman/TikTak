package tiktak.app.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MakePasswordFile {
    public static void main(String[] args){
        try (FileOutputStream fos = new FileOutputStream("/Users/Darius/Desktop/Networking/TikTak/src/passwords")) {
            fos.write("sherman:test\r\n".getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
