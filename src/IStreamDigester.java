import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface IStreamDigester {

    /**
     * Digests a data read from a stream until reaching end of stream
     *
     * @param stream the stream to read
     * @return the digested byte array
     * @throws IOException for stream IO issues
     */
    byte[] digestStream(InputStream stream) throws IOException;

    /**
     * Digests a data read from a file
     *
     * @param filePath the file to digest
     * @return the digested byte array
     * @throws IOException for stream IO issues
     */
    byte[] digestStream(Path filePath) throws IOException;
}
