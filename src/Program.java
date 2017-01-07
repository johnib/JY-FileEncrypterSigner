import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public class Program {

    // true / false params
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final LinkedList<String> switches = new LinkedList<>();
    private static final HashMap<String, String> programParams = new HashMap<>();

    private static final String KEYSTORE_PARAM_NAME = "keystore";
    private static final String KEYSTORE_PASSWORD_PARAM_NAME = "password";
    private static final String MY_ALIAS_PARAM_NAME = "myAlias".toLowerCase();
    private static final String MY_ALIAS_PASSWORD_PARAM_NAME = "myAliasPassword".toLowerCase();
    private static final String RECIPIENT_ALIAS_PARAM_NAME = "recAlias".toLowerCase();
    private static final String FILE_PARAM_PATH = "file";

    @SuppressWarnings("FieldCanBeLocal")
    private static String guide_message = "FileEncrypt class\n" +
            "\n" +
            "Run:\n" +
            "java FileEncrypt -keystore <keystore file> -password <pass> -myAlias <your keystore alias> -myAliasPassword <your alias password> -file <file path>\n" +
            "\n" +
            "Options:\n" +
            "    -keystore           Key Store file path\n" +
            "    -password           Key Store password\n" +
            "    -myAlias            Key Store alias for my cert with private key\n" +
            "    -myAliasPassword    alias password for my cert with private key (if not defined, using keystore password)\n" +
            "    -recAlias           Key Store alias for recipient cert\n" +
            "    -file               File to encrypt and sign";

    public static void main(String[] args) throws Exception {
        validateInput(args);

        final KeyStore keystore = loadKeystore("JKS", "SUN");
        final Cipher symmetricCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final Cipher asymmetricCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final Signature signature = Signature.getInstance("SHA1withRSA");
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        final int symmetricKeyLength = 128; // bits

        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        }

        final Encrypter encrypter = new Encrypter(
                keystore,
                symmetricCipher,
                asymmetricCipher,
                messageDigest,
                signature,
                keyGenerator,
                secureRandom,
                symmetricKeyLength);

        encrypter.initialize(
                programParams.get(MY_ALIAS_PARAM_NAME),
                programParams.containsKey(MY_ALIAS_PASSWORD_PARAM_NAME) ?
                        programParams.get(MY_ALIAS_PASSWORD_PARAM_NAME) :
                        programParams.get(KEYSTORE_PASSWORD_PARAM_NAME),
                programParams.get(RECIPIENT_ALIAS_PARAM_NAME));

        final Path sourceFilePath = Paths.get(programParams.get(FILE_PARAM_PATH));
        final Path encryptedFilePath = Paths.get(String.format("%s-encrypted", sourceFilePath.toAbsolutePath()));
        encrypter.encryptAndSign(sourceFilePath, encryptedFilePath);
    }

    private static KeyStore loadKeystore(String type, String provider) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(type, provider);
        final Path keystoreFilePath = Paths.get(programParams.get(KEYSTORE_PARAM_NAME));
        final String keystorePassword = programParams.get(KEYSTORE_PASSWORD_PARAM_NAME);

        FileInputStream fip = new FileInputStream(keystoreFilePath.toFile());
        keystore.load(fip, keystorePassword.toCharArray());

        return keystore;
    }

    private static void validateInput(String[] args) {
        try {
            Utils.parseParams(args, programParams, switches);

            Utils.ensureParamDefinition(KEYSTORE_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(KEYSTORE_PASSWORD_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(MY_ALIAS_PASSWORD_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(RECIPIENT_ALIAS_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(FILE_PARAM_PATH, programParams);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(guide_message);
            System.exit(-1);
        }

        try {
            Utils.ensurePathReadable(Paths.get(programParams.get("file")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
