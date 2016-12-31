import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */

public class Encrypt {

    @SuppressWarnings("FieldCanBeLocal")
    private static String guide_message = "Encrypt class\n" +
            "\n" +
            "Run:\n" +
            "java Encrypt -password <pass> -file <file path>\n" +
            "\n" +
            "Options:\n" +
            "    -password           Used as a private key\n" +
            "    -file               File to encrypt";

    // true / false params
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static LinkedList<String> switches = new LinkedList<>();

    private static HashMap<String, String> programParams = new HashMap<>();

    public static void main(String[] args) {
        try {
            Utils.parseParams(args, programParams, switches);

            programParams.forEach((param, value) -> System.out.println(String.format("%s: %s", param, value)));

            Utils.ensureParamDefinition("password", programParams);
            Utils.ensureParamDefinition("file", programParams);
            Utils.ensureFileExists(programParams.get("file"));

        } catch (Exception e) {
            System.out.println(guide_message);
        }
    }
}
