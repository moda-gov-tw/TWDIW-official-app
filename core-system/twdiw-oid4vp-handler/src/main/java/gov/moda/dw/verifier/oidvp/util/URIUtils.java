package gov.moda.dw.verifier.oidvp.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class URIUtils {

    public static boolean isValidURI(String uriString) {
        if (uriString == null) {
            return false;
        }

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            return false;
        }
        if (uri.getScheme() == null || !Arrays.asList("http", "https").contains(uri.getScheme().toLowerCase())) {
            return false;
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            return false;
        }

        return true;
    }
}
