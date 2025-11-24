package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public interface OidvpErrorProperty {

    OidvpError getOidvpError();

    String getOidvpErrorMessage();
}
