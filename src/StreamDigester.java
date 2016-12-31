import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
class StreamDigester {

    private static final int bufferSize = 1024; // bytes
    private MessageDigest messageDigest;

    StreamDigester(MessageDigest md) {
        messageDigest = md;
    }

    byte[] digestStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[bufferSize];
        messageDigest.reset();

        while (stream.read(buffer) > 0) {
            messageDigest.update(buffer);
        }

        return messageDigest.digest();
    }
}
