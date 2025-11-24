package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class InvalidParameterException extends OidvpRuntimeException {

    private static final OidvpError oidvpError = OidvpError.INVALID_PARAMETERS;

    public InvalidParameterException(String detailMessage) {
        super(oidvpError, detailMessage);
    }
}
