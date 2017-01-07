import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 07/01/2017.
 */
@SuppressWarnings("WeakerAccess")
public class Encrypter {

    private static final String CONFIG_FILE_TEMPLATE = "%s-config.json";

    private final KeyStore keystore;
    private final Cipher asymmetricCipher;
    private final IFileEncrypt fileEncrypt;
    private final IStreamDigester streamDigester;
    private final IDataSigner dataSigner;
    private final KeyGenerator keyGenerator;
    private final SecureRandom secureRandom;
    private final int symmetricKeyLength;
    private final Gson gson;

    private Key myPrivateKey;
    private Certificate recipientCertificate;

    /**
     * Initializes a new instance of Encrypter
     *
     * @param keystore               a loaded keystore
     * @param symmetricCipher        a Cipher instance
     * @param asymmetricCipher       a Cipher instance
     * @param messageDigest          a MessageDigest instance
     * @param signature              a Signature instance
     * @param keyGenerator           a KeyGenerator instance
     * @param secureRandom           a SecureRandom instance
     * @param symmetricKeyLengthBits length in bits of the symmetric key
     */
    public Encrypter(KeyStore keystore,
                     Cipher symmetricCipher,
                     Cipher asymmetricCipher,
                     MessageDigest messageDigest,
                     Signature signature,
                     KeyGenerator keyGenerator,
                     SecureRandom secureRandom,
                     int symmetricKeyLengthBits) {

        this.keystore = keystore;
        this.asymmetricCipher = asymmetricCipher;
        this.fileEncrypt = new FileEncrypt(symmetricCipher);
        this.streamDigester = new StreamDigester(messageDigest);
        this.dataSigner = new DataSigner(signature);
        this.keyGenerator = keyGenerator;
        this.secureRandom = secureRandom;
        this.symmetricKeyLength = symmetricKeyLengthBits;
        this.gson = new GsonBuilder().create();
    }

    /**
     * Initializes the Encrypter instance with the relevant private key and public certificate.
     * Also - initializes the key generator
     *
     * @param privateKeyAlias           the alias for the private key
     * @param privateKeyPassword        the password for the private key
     * @param recipientCertificateAlias the alias for the public certificate
     * @throws KeyStoreException for bad private key password
     */
    public void initialize(String privateKeyAlias, String privateKeyPassword, String recipientCertificateAlias) throws KeyStoreException {
        if (!keystore.isKeyEntry(privateKeyAlias)) {
            throw new KeyStoreException("private key alias not found in keystore");
        }

        if (!keystore.isCertificateEntry(recipientCertificateAlias)) {
            throw new KeyStoreException("recipient's certificate alias not found in keystore");
        }

        try {
            myPrivateKey = keystore.getKey(privateKeyAlias, privateKeyPassword.toCharArray());

        } catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new KeyStoreException("Bad private key password");
        }

        recipientCertificate = keystore.getCertificate(recipientCertificateAlias);
        keyGenerator.init(symmetricKeyLength, secureRandom);
    }

    /**
     * Encrypts and signs the content of the given file
     *
     * @param filePath the path of the file to encrypt and sign
     * @param output   the output encrypted file path
     * @throws IOException         for IO issues
     * @throws InvalidKeyException for symmetric key issues
     * @throws SignatureException  for signature issues
     */
    public void encryptAndSign(Path filePath, Path output) throws IOException, InvalidKeyException, SignatureException, BadPaddingException, IllegalBlockSizeException {
        final HashMap<String, String> config = new HashMap<>();

        // sign content
        byte[] fileDigest = streamDigester.digestStream(filePath);
        byte[] fileSignature = dataSigner.sign(fileDigest, (PrivateKey) myPrivateKey);
        config.put("sig", Base64.getEncoder().encodeToString(fileSignature));

        // generate symmetric key
        Key symmetricKey = keyGenerator.generateKey();

        // encrypt symmetric key with recipient's public key
        asymmetricCipher.init(Cipher.ENCRYPT_MODE, recipientCertificate.getPublicKey());
        byte[] symmetricKeyEncrypted = asymmetricCipher.doFinal(symmetricKey.getEncoded());
        config.put("key", Base64.getEncoder().encodeToString(symmetricKeyEncrypted));

        // encrypt file
        fileEncrypt.encrypt(filePath, output, symmetricKey);

        // write config file
        createConfigFileFor(filePath, config);
    }

    /**
     * Serializes the config and writes it to disk
     *
     * @param filePath the original file for which the config is relevant
     * @param config   the config map
     * @throws IOException for IO issues
     */
    private void createConfigFileFor(Path filePath, HashMap<String, String> config) throws IOException {
        final Path configFile = Paths.get(String.format(CONFIG_FILE_TEMPLATE, filePath.toAbsolutePath()));
        final FileOutputStream outputStream = new FileOutputStream(configFile.toFile());

        final String configJson = gson.toJson(config);

        outputStream.write(configJson.getBytes());
        outputStream.close();
    }
}