package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class VpValidatedException extends OidvpException {

    public VpValidatedException(OidvpError error) {
        super(error);
    }

    public VpValidatedException(OidvpError oidvpError, Throwable cause) {
        super(oidvpError, cause);
    }

    public VpValidatedException(OidvpError error, String detailMessage) {
        super(error, detailMessage);
    }

    public VpValidatedException(OidvpError oidvpError, String detailMessage, Throwable cause) {
        super(oidvpError, detailMessage, cause);
    }
}
