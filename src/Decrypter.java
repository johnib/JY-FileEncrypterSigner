import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Map;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 07/01/2017.
 */
@SuppressWarnings("WeakerAccess")
public class Decrypter {

    private static final java.util.Base64.Decoder Base64 = java.util.Base64.getDecoder();

    private final KeyStore keystore;
    private final Cipher asymmetricCipher;
    private final IFileDecrypt fileDecrypt;
    private final IStreamDigester streamDigester;
    private final IDataSigner dataSigner;
    private final Gson gson;

    private Key myPrivateKey;
    private Certificate senderCertificate;

    /**
     * Initializes a new instance of Decrypter
     *
     * @param keystore         a loaded keystore
     * @param symmetricCipher  a Cipher instance
     * @param asymmetricCipher a Cipher instance
     * @param messageDigest    a MessageDigest instance
     * @param signature        a Signature instance
     */
    public Decrypter(KeyStore keystore,
                     Cipher symmetricCipher,
                     Cipher asymmetricCipher,
                     MessageDigest messageDigest,
                     Signature signature) {

        this.keystore = keystore;
        this.asymmetricCipher = asymmetricCipher;
        this.fileDecrypt = new FileDecrypt(symmetricCipher);
        this.streamDigester = new StreamDigester(messageDigest);
        this.dataSigner = new DataSigner(signature);
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
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
        System.out.print("Initializing decrypter: ...");

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

        senderCertificate = keystore.getCertificate(recipientCertificateAlias);

        System.out.println("DONE");
    }

    /**
     * Decrypts the file and validates its completeness
     *
     * @param encryptedFile the path to the encrypted file
     * @param configFile    the path to the config file
     */
    public void decryptAndValidate(Path encryptedFile, Path configFile, Path output) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        System.out.println("Decrypting and verifying signature of file: " + encryptedFile.getFileName());

        final Map<String, String> config = deserializeConfigFile(configFile);

        // decrypt symmetric key and IV
        asymmetricCipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
        byte[] iv = asymmetricCipher.doFinal(Base64.decode(config.get("iv")));
        byte[] decryptedSecretKey = asymmetricCipher.doFinal(Base64.decode(config.get("key")));
        Key key = new SecretKeySpec(decryptedSecretKey, "AES");

        // decrypt file
        fileDecrypt.decrypt(encryptedFile, output, key, iv);

        // validate file signature
        byte[] fileDigest = streamDigester.digestStream(output);
        byte[] providedSignature = Base64.decode(config.get("sig"));
        boolean fileDigestValid = dataSigner.verify(fileDigest, providedSignature, senderCertificate.getPublicKey());

        if (!fileDigestValid) {
            throw new SignatureException("File's content signature is invalid");
        }

        System.out.println("File " + encryptedFile.getFileName() + " decrypted and validated successfully");
    }

    /**
     * Deserializes the config file provided to a HashMap
     *
     * @param configFile the path to the config file
     * @return a HashMap corresponding to the config file
     */
    private Map<String, String> deserializeConfigFile(Path configFile) throws IOException {
        final Type deserializationType = new TypeToken<Map<String, String>>() {
        }.getType();

        final String configJson = new String(Files.readAllBytes(configFile));

        return gson.fromJson(configJson, deserializationType);
    }
}
