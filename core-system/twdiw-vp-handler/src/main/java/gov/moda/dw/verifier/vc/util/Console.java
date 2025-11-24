package gov.moda.dw.verifier.vc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Console {

    private static final Logger LOGGER = LoggerFactory.getLogger(Console.class);

    /**
     * show byte array
     *
     * @param bytes byte array
     */
    public static void showBytes(byte[] bytes) {
        showBytes(bytes, null);
    }

    /**
     * show byte array
     *
     * @param bytes byte array
     * @param title title will be shown
     */
    public static void showBytes(byte[] bytes, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "bytes";
        if (bytes == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + bytes.length + ") =\n");
            for (int i = 0; i < bytes.length; i++) {

                if (i % 16 == 0) {
                    result = result.concat(String.format("%04X", i) + ": ");
                }

                result = result.concat(("" + "0123456789ABCDEF".charAt(0x0F & bytes[i] >> 4) + "0123456789ABCDEF".charAt(bytes[i] & 0x0F)) + " ");

                if (i % 16 == 15 || i == bytes.length - 1) {
                    result = result.concat("\n");
                }
            }
        }

        LOGGER.info(result);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param <T> type of element in list
     */
    public static <T> void showList(List<T> inputList) {
        showList(inputList, null);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param title title will be shown
     * @param <T> type of element in list
     */
    public static <T> void showList(List<T> inputList, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "list";
        if (inputList == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + inputList.size() + ") = [\n");
            for (int i = 0; i < inputList.size(); i++) {
                String separator = (i != inputList.size() - 1) ? "," : "";
                result = result.concat("\t" + inputList.get(i).toString() + separator + "\n");
            }
            result = result.concat("]\n");
        }

        LOGGER.info(result);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param title title will be shown
     * @param unitLength unit hex length (byte: 1, short: 2, int: 4)
     * @param <T> type of element in list
     */
    public static <T extends Number> void showListByHex(List<T> inputList, String title, int unitLength) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "list";
        if (inputList == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + inputList.size() + ") = [\n");
            for (int i = 0; i < inputList.size(); i++) {
                String separator = (i != inputList.size() - 1) ? "," : "";
                String element;
                switch (unitLength) {
                    case Byte.BYTES -> element = String.format("%02X", inputList.get(i).byteValue());
                    case Short.BYTES -> element = String.format("%04X", inputList.get(i).shortValue());
                    case Integer.BYTES -> element = String.format("%08X", inputList.get(i).intValue());
                    default -> element = String.valueOf(inputList.get(i));
                }
                result = result.concat("\t" + element + separator + "\n");
            }
            result = result.concat("]\n");
        }

        LOGGER.info(result);
    }

    /**
     * show json
     *
     * @param json json
     */
    public static void showJson(String json) {
        showJson(json, null);
    }

    /**
     * show json
     *
     * @param json json
     * @param title title will be shown
     */
    public static void showJson(String json, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "json";
        if (json == null) {
            result = result.concat(title + " -> null");
        } else {
            result = result.concat(title + " = ");
            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
//                .setPrettyPrinting()
                .create();
            String prettyJson = gson.toJson(JsonParser.parseString(json));
            result = result.concat(prettyJson);
        }

        LOGGER.info(result);
    }
}
