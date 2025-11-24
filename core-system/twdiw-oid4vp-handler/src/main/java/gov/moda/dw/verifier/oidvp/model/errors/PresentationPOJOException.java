package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class PresentationPOJOException extends OidvpException{

    public PresentationPOJOException(OidvpError error) {
        super(error);
    }

    public PresentationPOJOException(OidvpError error, String detailMessage) {
        super(error, detailMessage);
    }
}
