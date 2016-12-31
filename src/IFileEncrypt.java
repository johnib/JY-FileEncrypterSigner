import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public interface IFileEncrypt {

    /**
     * Encrypts a file
     *
     * @param sourceFilePath the path to the file to be encrypted
     * @param destFilePath   the path to the encrypted file
     * @param key            the encryption key to be used
     * @throws IOException         in case files are not accessible or read / write issues
     * @throws InvalidKeyException in case of key issues
     */
    void encrypt(Path sourceFilePath, Path destFilePath, SecretKeySpec key) throws IOException, InvalidKeyException;

    /**
     * Gets the initialization vector used for encryption
     *
     * @return byte array containing the IV
     */
    byte[] getIV();
}
