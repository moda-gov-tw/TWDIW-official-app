package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.oauth2.sdk.ResponseMode;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OidvpResponseMode {

    public static final ResponseMode DIRECT_POST = new ResponseMode("direct_post");

    public static final ResponseMode DIRECT_POST_JWT = new ResponseMode("direct_post.jwt");

    // other support ResponseMode...

    private static final Map<ResponseMode, Integer> indexMap = new HashMap<>();

    static {
        indexMap.put(ResponseMode.FRAGMENT, 0);
        indexMap.put(DIRECT_POST, 1);
        indexMap.put(DIRECT_POST_JWT, 2);
    }

    public static int getResponseModeIndex(ResponseMode responseMode) {
        if (responseMode == null) {
            throw new BadOidvpParamException("responseMode must not be null");
        }
        Integer index = indexMap.get(responseMode);
        if (index == null) {
            throw new BadOidvpParamException("not supported ResponseMode");
        }
        return index;
    }

    public static ResponseMode getResponseModeByIndex(int index) {
        for (Entry<ResponseMode, Integer> rmEntry : indexMap.entrySet()) {
            if (rmEntry.getValue().equals(index)) {
                return rmEntry.getKey();
            }
        }
        throw new BadOidvpParamException("not supported ResponseMode index");
    }
}
