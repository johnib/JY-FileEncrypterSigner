import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.MissingFormatArgumentException;

/**
 * Created by Jonathan Yaniv and Arnon Nir on 31/12/2016.
 */
@SuppressWarnings("WeakerAccess")
public final class Utils {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static final int BUFFER_SIZE = 1024; // bytes

    /**
     * Static class
     */
    private Utils() {
    }

    /**
     * Validates the required param is defined
     *
     * @param param the param
     */
    public static void ensureParamDefinition(String param, Map<String, String> programParams) {
        if (!programParams.containsKey(param)) {
            throw new MissingFormatArgumentException(String.format("Missing argument: %s", param));
        }
    }

    /**
     * Parses the arguments received in the main method.
     *
     * @param params the params
     */
    public static void parseParams(String[] params, Map<String, String> programParams, List<String> switches) {
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

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Pipes data from one stream to another
     *
     * @param in  the source stream
     * @param out the destination stream
     * @throws IOException in case of read / write issues
     */
    public static void pipeStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Validates source file exists
     *
     * @param sourceFilePath the path
     * @throws IOException in case file does not exist or cannot be read¬
     */
    public static void ensurePathReadable(Path sourceFilePath) throws IOException {
        File sourceFile = sourceFilePath.toFile();

        if (!sourceFile.exists()) {
            throw new FileNotFoundException(String.format("Source file not found %s", sourceFile.getAbsolutePath()));
        }

        if (!sourceFile.canRead()) {
            throw new FileNotFoundException(String.format("Source cannot be read %s", sourceFile.getAbsolutePath()));
        }
    }

    /**
     * Validates the destination path can be assigned to a new file.
     *
     * @param destFilePath the path
     * @throws IOException in case path already used by existing file or is not permitted to create new one
     */
    public static void ensurePathWritable(Path destFilePath) throws IOException {
        File destFile = destFilePath.toFile();

        if (destFile.exists()) {
            throw new IllegalArgumentException(String.format("File already exists in path %s", destFilePath.toAbsolutePath()));
        }

        if (!destFile.createNewFile()) {
            throw new FileNotFoundException(String.format("File cannot be created %s", destFilePath.toAbsolutePath()));
        }
    }
}
