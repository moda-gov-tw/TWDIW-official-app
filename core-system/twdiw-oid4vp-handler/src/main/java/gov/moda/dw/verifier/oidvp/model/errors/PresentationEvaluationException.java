package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class PresentationEvaluationException extends OidvpException{

    public PresentationEvaluationException(OidvpError error) {
        super(error);
    }

    public PresentationEvaluationException(OidvpError error, String detailMessage) {
        super(error, detailMessage);
    }
}
