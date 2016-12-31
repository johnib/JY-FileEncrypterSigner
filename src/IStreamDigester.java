import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public interface IStreamDigester {
    byte[] digestStream(InputStream stream) throws IOException;
}
