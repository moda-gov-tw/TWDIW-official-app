package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class BadOidvpParamException extends BaseOidvpRuntimeException {

    private static final OidvpError DEFAULT_ERROR = OidvpError.BAD_OIDVP_PARAM;

    public BadOidvpParamException(String detailMessage) {
        super(DEFAULT_ERROR, detailMessage);
    }

    public BadOidvpParamException(OidvpError error, String detailMessage) {
        super(error, detailMessage);
    }
}
