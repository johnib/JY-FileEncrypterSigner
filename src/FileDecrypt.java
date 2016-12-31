import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public class FileDecrypt implements IFileDecrypt {

    private final Cipher cipher;

    @SuppressWarnings("WeakerAccess")
    public FileDecrypt(Cipher cipher) {
        this.cipher = cipher;
    }

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
    @Override
    public void decrypt(Path sourceFilePath, Path destFilePath, SecretKeySpec key, byte[] iv) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        Utils.ensurePathReadable(sourceFilePath);
        Utils.ensurePathWritable(destFilePath);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        CipherInputStream decryptStream = new CipherInputStream(new FileInputStream(sourceFilePath.toFile()), cipher);
        FileOutputStream destStream = new FileOutputStream(destFilePath.toFile());

        Utils.pipeStreams(decryptStream, destStream);
        decryptStream.close();
        destStream.close();
    }
}
