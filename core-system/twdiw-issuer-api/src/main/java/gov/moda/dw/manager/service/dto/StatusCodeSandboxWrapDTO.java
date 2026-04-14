package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.type.StatusCodeSandbox;

public class StatusCodeSandboxWrapDTO {

    StatusCodeSandbox statusCode;

    public StatusCodeSandbox getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodeSandbox statusCode) {
        this.statusCode = statusCode;
    }
}
