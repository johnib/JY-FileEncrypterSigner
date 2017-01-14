import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
public class EncryptTool {

    // true / false params
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<String> switches = new LinkedList<String>() {{
        add("encrypt");
        add("decrypt");
    }};

    private static final Map<String, String> programParams = new HashMap<>();

    private static final String ENCRYPT_SWITCH_NAME = "encrypt";
    private static final String DECRYPT_SWITCH_NAME = "decrypt";
    private static final String KEYSTORE_PARAM_NAME = "keystore";
    private static final String KEYSTORE_PASSWORD_PARAM_NAME = "password";
    private static final String MY_ALIAS_PARAM_NAME = "myAlias".toLowerCase();
    private static final String MY_ALIAS_PASSWORD_PARAM_NAME = "myAliasPassword".toLowerCase();
    private static final String RECIPIENT_ALIAS_PARAM_NAME = "recAlias".toLowerCase();
    private static final String FILE_PARAM_PATH = "file";

    @SuppressWarnings("FieldCanBeLocal")
    private static String guide_message = "FileEncrypt tool\n" +
            "\n" +
            "Run:\n" +
            "java EncryptTool -encrypt -keystore <keystore file> -password <pass> -myAlias <your keystore alias> -myAliasPassword <your alias password> -file <file path>\n" +
            "java EncryptTool -decrypt -keystore <keystore file> -password <pass> -myAlias <your keystore alias> -myAliasPassword <your alias password> -file <file path>\n" +
            "\n" +
            "Options:\n" +
            "    -encrypt            Encrypts the file and creates a signature\n" +
            "    -decrypt            Encrypts the file validates signature\n" +
            "    -keystore           Key Store file path\n" +
            "    -password           Key Store password\n" +
            "    -myAlias            Key Store alias for my cert with private key\n" +
            "    -myAliasPassword    alias password for my cert with private key (if not defined, using keystore password)\n" +
            "    -recAlias           Key Store alias for recipient cert\n" +
            "    -file               File to encrypt and sign";

    public static void main(String[] args) throws Exception {
        validateInput(args);

        final KeyStore keystore = loadKeystore("JKS", "SUN");

        System.out.print("Initializing cryptography instances ...");

        //
        // Support for padding is in order as the encrypted content size is not limited.
        // For file encryption - used AES CBC so no patterns can be visible on the encrypted output.
        // For secret encryption and delivery in insecure channel - used RSA.
        // For message digest used SHA-256 which is the best available in terms of collisions.
        // Same for signature.
        //
        // For SecureRandom, used by the AES key generator, I haven't specified the algorithm but let the runtime choose
        // the best available for me.
        //
        // Symmetric key length is 128 bits just for the demo - it can be 256 for AES - but requires additional
        // packages to be installed.
        //
        final Cipher symmetricCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final Cipher asymmetricCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        final Signature signature = Signature.getInstance("SHA256withRSA");
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        final int symmetricKeyLength = 128; // bits

        System.out.println("DONE");

        if (programParams.containsKey(ENCRYPT_SWITCH_NAME)) {

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

        } else if (programParams.containsKey(DECRYPT_SWITCH_NAME)) {

            final Decrypter decrypter = new Decrypter(
                    keystore,
                    symmetricCipher,
                    asymmetricCipher,
                    messageDigest,
                    signature);

            decrypter.initialize(programParams.get(MY_ALIAS_PARAM_NAME),
                    programParams.containsKey(MY_ALIAS_PASSWORD_PARAM_NAME) ?
                            programParams.get(MY_ALIAS_PASSWORD_PARAM_NAME) :
                            programParams.get(KEYSTORE_PASSWORD_PARAM_NAME),
                    programParams.get(RECIPIENT_ALIAS_PARAM_NAME));

            final Path encryptedSourceFile = Paths.get(programParams.get(FILE_PARAM_PATH));
            final Path decryptedFile = Paths.get(String.format("%s/decrypted.txt", encryptedSourceFile.getParent().toAbsolutePath()));
            final Path configFilePath = Paths.get(String.format("%s-config.json", encryptedSourceFile.toAbsolutePath()));

            try {
                decrypter.decryptAndValidate(encryptedSourceFile, configFilePath, decryptedFile);

            } catch (SignatureException e) {
                // file's content was tampered with
                System.err.println(e.getMessage());

                // requested by instructor to overwrite the decrypted file's content with the error message
                FileOutputStream outputStream = new FileOutputStream(decryptedFile.toFile(), false);
                outputStream.write(e.getMessage().getBytes());
                outputStream.close();
            }
        }

        System.exit(1);
    }

    private static KeyStore loadKeystore(String type, String provider) throws Exception {
        System.out.print("Loading keystore ...");
        final KeyStore keystore = KeyStore.getInstance(type, provider);
        final Path keystoreFilePath = Paths.get(programParams.get(KEYSTORE_PARAM_NAME));
        final String keystorePassword = programParams.get(KEYSTORE_PASSWORD_PARAM_NAME);

        FileInputStream fip = new FileInputStream(keystoreFilePath.toFile());
        keystore.load(fip, keystorePassword.toCharArray());

        System.out.println("DONE");

        return keystore;
    }

    private static void validateInput(String[] args) {
        System.out.print("Validating input arguments ...");

        try {
            Utils.parseParams(args, programParams, switches);

            Utils.ensureParamDefinition(KEYSTORE_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(KEYSTORE_PASSWORD_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(MY_ALIAS_PASSWORD_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(RECIPIENT_ALIAS_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(FILE_PARAM_PATH, programParams);

            if ((programParams.containsKey(ENCRYPT_SWITCH_NAME) && programParams.containsKey(DECRYPT_SWITCH_NAME))
                    || !programParams.containsKey(ENCRYPT_SWITCH_NAME) && !programParams.containsKey(DECRYPT_SWITCH_NAME)) {

                throw new MissingFormatArgumentException("Either -encrypt or -decrypt should be used, but not both");
            }

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

        System.out.println("DONE");
    }
}
