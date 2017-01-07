import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public class StreamDigester implements IStreamDigester {

    private static final int bufferSize = 1024; // bytes
    private MessageDigest messageDigest;

    @SuppressWarnings("WeakerAccess")
    public StreamDigester(MessageDigest md) {
        messageDigest = md;
    }

    /**
     * Digests a data read from a stream until reaching end of stream
     *
     * @param stream the stream to read
     * @return the digested byte array
     * @throws IOException for stream IO issues
     */
    @Override
    public byte[] digestStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[bufferSize];
        messageDigest.reset();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) > 0) {
            messageDigest.update(buffer, 0, bytesRead);
        }

        return messageDigest.digest();
    }
}
