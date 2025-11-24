package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public abstract class BaseOidvpException extends Exception implements OidvpErrorProperty {

    private final OidvpError oidvpError;
    private final String message;

    public BaseOidvpException(OidvpError oidvpError, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.oidvpError = oidvpError;
        this.message = errorMessage;
    }

    public BaseOidvpException(OidvpError oidvpError, String errorMessage) {
        super(errorMessage);
        this.oidvpError = oidvpError;
        this.message = errorMessage;
    }

    public BaseOidvpException(OidvpError oidvpError, Throwable cause) {
        super(oidvpError.getMsg(), cause);
        this.oidvpError = oidvpError;
        this.message = oidvpError.getMsg();
    }

    public BaseOidvpException(OidvpError oidvpError) {
        super(oidvpError.getMsg());
        this.oidvpError = oidvpError;
        this.message = oidvpError.getMsg();
    }

    @Override
    public OidvpError getOidvpError() {
        return oidvpError;
    }

    @Override
    public String getOidvpErrorMessage() {
        return message;
    }
}
