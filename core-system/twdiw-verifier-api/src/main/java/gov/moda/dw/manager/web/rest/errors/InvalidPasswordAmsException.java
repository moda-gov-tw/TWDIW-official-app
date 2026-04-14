package gov.moda.dw.manager.web.rest.errors;

public class InvalidPasswordAmsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String errorKey;

  public InvalidPasswordAmsException() {
    super("Incorrect password");
  }

  public InvalidPasswordAmsException(String msg) {
    super(msg);
  }

  public InvalidPasswordAmsException(String msg, String errorKey) {
    super(msg);
    this.errorKey = errorKey;
  }

  public String getErrorKey() {
    return errorKey;
  }
}
