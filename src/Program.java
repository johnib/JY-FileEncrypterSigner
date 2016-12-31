import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String PASSWORD_PARAM_NAME = "password";
    private static final String FILE_PARAM_NAME = "file";

    @SuppressWarnings("FieldCanBeLocal")
    private static String guide_message = "Encrypt class\n" +
            "\n" +
            "Run:\n" +
            "java Encrypt -password <pass> -file <file path>\n" +
            "\n" +
            "Options:\n" +
            "    -password           Used as a private key\n" +
            "    -file               File to encrypt";

    public static void main(String[] args) throws Exception {
        /* validate input */

        try {
            Utils.parseParams(args, programParams, switches);

            // TODO: remove before submitting
            programParams.forEach((param, value) -> System.out.println(String.format("%s: %s", param, value)));

            Utils.ensureParamDefinition(PASSWORD_PARAM_NAME, programParams);
            Utils.ensureParamDefinition(FILE_PARAM_NAME, programParams);

        } catch (Exception e) {
            System.out.println(guide_message);
            System.exit(-1);
        }

        try {
            Utils.ensureFileExists(programParams.get("file"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        /* end of validate input */
    }

    // TODO: remove
    static void testStreamHash() {
        try {
            StreamDigester sd = new StreamDigester(MessageDigest.getInstance("MD5"));
            InputStream fip = new FileInputStream(programParams.get(FILE_PARAM_NAME));

            byte[] hash = sd.digestStream(fip);

            System.out.println(Utils.bytesToHex(hash));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: remove
    static void testDataSigner() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("DSA", "SUN");
            KeyPair pair = gen.generateKeyPair();

            String data = "Jonathan is awesome";

            DataSigner ds = new DataSigner(Signature.getInstance("SHA1withDSA"));
            byte[] signature = ds.sign(data, pair.getPrivate());

            System.out.println(ds.verify(data, signature, pair.getPublic()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
