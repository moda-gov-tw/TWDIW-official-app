package gov.moda.dw.manager.web.rest.errors;

import org.springframework.security.authentication.BadCredentialsException;

public class AmsBadCredentialsException extends BadCredentialsException {

  private String errorKey;

  public AmsBadCredentialsException(String errorKey, String msg) {
    super(msg);
    this.errorKey = errorKey;
  }

  public String getErrorKey() {
    return errorKey;
  }
}
