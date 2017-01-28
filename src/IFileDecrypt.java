import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;

@SuppressWarnings("WeakerAccess")
public interface IFileDecrypt {

    /**
     * Decrypts a file
     *
     * @param sourceFilePath the path to the encrypted file to be decrypted
     * @param destFilePath   the path to the decrypted file
     * @param key            the decryption key to be used
     * @param iv             the initialization vector used for encryption
     * @throws IOException         in case files are not accessible or read / write issues
     * @throws InvalidKeyException in case of key issues
     */
    void decrypt(Path sourceFilePath, Path destFilePath, Key key, byte[] iv) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException;
}
