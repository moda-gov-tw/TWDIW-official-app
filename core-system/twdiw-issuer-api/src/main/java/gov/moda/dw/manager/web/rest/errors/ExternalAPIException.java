package gov.moda.dw.manager.web.rest.errors;

public class ExternalAPIException extends RuntimeException {

    private String externalApiCode;
    private String externalApiMessage;

    public ExternalAPIException(String externalApiCode, String externalApiMessage) {
        super(externalApiCode + ": " + externalApiMessage);
        this.externalApiCode = externalApiCode;
        this.externalApiMessage = externalApiMessage;
    }

    public String getExternalApiCode() {
        return externalApiCode;
    }

    public String getExternalApiMessage() {
        return externalApiMessage;
    }
}
