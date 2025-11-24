package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class VcValidatedException extends OidvpException {

    public VcValidatedException(OidvpError error) {
        super(error);
    }

    public VcValidatedException(OidvpError error, String detailMessage) {
        super(error, detailMessage);
    }
}
