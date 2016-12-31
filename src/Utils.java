import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.MissingFormatArgumentException;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
final class Utils {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Static class
     */
    private Utils() {
    }

    /**
     * Validates the file exists
     *
     * @param file the file
     * @throws FileNotFoundException in case file does not exist
     */
    static void ensureFileExists(String file) throws FileNotFoundException {
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
    static void ensureParamDefinition(String param, Map<String, String> programParams) {
        if (!programParams.containsKey(param)) {
            throw new MissingFormatArgumentException(String.format("Missing argument: %s", param));
        }
    }

    /**
     * Parses the arguments received in the main method.
     *
     * @param params the params
     */
    static void parseParams(String[] params, Map<String, String> programParams, List<String> switches) {
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
                programParams.put(param.substring(1), paramValue);
            }
        }
    }

    static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
