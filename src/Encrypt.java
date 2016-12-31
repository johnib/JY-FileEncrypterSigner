import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */

public class Encrypt {

    // true / false params
    private static LinkedList<String> switches = new LinkedList<>();

    private static HashMap<String, String> programParams = new HashMap<>();

    public static void main(String[] args) {
        parseParams(args);

        programParams.forEach((param, value) -> System.out.println(String.format("%s: %s", param, value)));
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
