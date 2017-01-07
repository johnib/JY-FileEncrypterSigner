import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
@SuppressWarnings("WeakerAccess")
public class StreamDigester implements IStreamDigester {

    private static final int bufferSize = 1024; // bytes
    private MessageDigest messageDigest;

    /**
     * Initializes a new StreamDigester
     *
     * @param md the given MessageDigest object
     */
    public StreamDigester(MessageDigest md) {
        messageDigest = md;
    }

    /**
     * Initializes a new StreamDigester
     *
     * @param algorithm the requested MessageDigest algorithm
     * @throws NoSuchAlgorithmException in case algorithm not found
     */
    @SuppressWarnings("unused")
    public StreamDigester(String algorithm) throws NoSuchAlgorithmException {
        this(MessageDigest.getInstance(algorithm));
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

    /**
     * Digests a data read from a file
     *
     * @param filePath the file to digest
     * @return the digested byte array
     * @throws IOException for stream IO issues
     */
    public byte[] digestStream(Path filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath.toFile());

        return digestStream(inputStream);
    }
}
