package gov.moda.dw.issuer.oidvci.web.rest.errors;

public class HTTPConnectException extends RuntimeException{

	private int error_code;
    private String error_message;
    
    public HTTPConnectException(int error_code, String error_message) {
    	super(error_message);
    	this.error_code = error_code;
    	this.error_message = error_message;
    }
    
    public HTTPConnectException(int error_code, String error_message, Throwable cause) {
        super(error_message, cause);
        this.error_code = error_code;
        this.error_message = error_message;
    }

	public int getError_code() {
		return error_code;
	}

	public String getError_message() {
		return error_message;
	}
}
