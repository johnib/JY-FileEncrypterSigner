import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;

public class FileEncrypt implements IFileEncrypt {

    private final Cipher cipher;

    public FileEncrypt(Cipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Encrypts a file
     *
     * @param sourceFilePath the path to the file to be encrypted
     * @param destFilePath   the path to the encrypted file
     * @param key            the encryption key to be used
     * @throws IOException         in case files are not accessible or read / write issues
     * @throws InvalidKeyException in case of key issues
     */
    @Override
    public void encrypt(Path sourceFilePath, Path destFilePath, Key key) throws IOException, InvalidKeyException {
        Utils.ensurePathReadable(sourceFilePath);
        Utils.ensurePathWritable(destFilePath);

        cipher.init(Cipher.ENCRYPT_MODE, key);

        FileInputStream sourceStream = new FileInputStream(sourceFilePath.toFile());
        CipherOutputStream encryptStream = new CipherOutputStream(new FileOutputStream(destFilePath.toFile()), cipher);

        Utils.pipeStreams(sourceStream, encryptStream);
        sourceStream.close();
        encryptStream.close();
    }


    /**
     * Gets the initialization vector used for encryption
     *
     * @return byte array containing the IV
     */
    @Override
    public byte[] getIV() {
        return cipher.getIV();
    }
}
