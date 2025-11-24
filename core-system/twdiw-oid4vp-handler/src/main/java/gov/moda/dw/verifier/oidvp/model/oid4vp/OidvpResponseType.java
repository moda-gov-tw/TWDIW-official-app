package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.oauth2.sdk.ResponseType;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OidvpResponseType {

    public static final ResponseType VPTOKEN = new ResponseType("vp_token");

    public static final ResponseType VPTOKEN_IDTOKEN = new ResponseType("vp_token", "id_token");

    // other support ResponseType...

    private static final Map<ResponseType, Integer> indexMap = new HashMap<>();

    static {
        indexMap.put(VPTOKEN, 1);
        indexMap.put(VPTOKEN_IDTOKEN, 2);
    }

    public static int getResponseTypeIndex(ResponseType responseType) {
        if (responseType == null) {
            throw new BadOidvpParamException("ResponseType must not be null");
        }
        Integer index = indexMap.get(responseType);
        if (index == null) {
            throw new BadOidvpParamException("not supported ResponseType");
        }
        return index;
    }

    public static ResponseType getResponseTypeByIndex(int index) {
        for (Entry<ResponseType, Integer> rmEntry : indexMap.entrySet()) {
            if (rmEntry.getValue().equals(index)) {
                return rmEntry.getKey();
            }
        }
        throw new BadOidvpParamException("not supported ResponseType index");
    }
}
