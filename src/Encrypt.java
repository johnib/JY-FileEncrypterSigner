import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.MissingFormatArgumentException;

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
            parseParams(args);

            programParams.forEach((param, value) -> System.out.println(String.format("%s: %s", param, value)));

            ensureParamDefinition("password");
            ensureParamDefinition("file");
            ensureFileExists(programParams.get("file"));

        } catch (Exception e) {
            System.out.println(guide_message);
        }
    }

    /**
     * Validates the file exists
     *
     * @param file the file
     * @throws FileNotFoundException in case file does not exist
     */
    private static void ensureFileExists(String file) throws FileNotFoundException {
        File f = new File(file);

        if (!f.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file));
        }
    }

    /**
     * Validates the required param is defined
     *
     * @param param the param
     */
    private static void ensureParamDefinition(String param) {
        if (!programParams.containsKey(param)) {
            throw new MissingFormatArgumentException(String.format("Missing argument: %s", param));
        }
    }

    /**
     * Parses the arguments received in the main method.
     *
     * @param params the params
     */
    private static void parseParams(String[] params) {
        for (int i = 0; i < params.length; i++) {
            String param = params[i];

            if (!param.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Param: %s should start with '-'", param));
            }

            if (switches.contains(param)) {
                programParams.put(param, "true");

            } else {
                i++;

                String paramValue = params[i];
                programParams.put(param, paramValue);
            }
        }
    }
}
