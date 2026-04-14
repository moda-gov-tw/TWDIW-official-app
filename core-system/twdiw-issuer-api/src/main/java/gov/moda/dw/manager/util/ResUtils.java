package gov.moda.dw.manager.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResUtils {

    public static String extractResIdFromUri(String uri) {
        try {
            log.debug("[ResUtils.extractResIdFromUri] 解析的uri是:{}", uri);
            if (uri.startsWith("/")) uri = uri.substring(1);
            if (uri.endsWith("/")) uri = uri.substring(0, uri.length() - 1);

            String result = null;
            String[] tokens = uri.split("/");

            if (tokens[0].equals("api")) {
                if (tokens[1].equals("mock") || tokens[1].equals("ext")) {
                    result = tokens[2];
                } else {
                    result = tokens[1];
                }
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
