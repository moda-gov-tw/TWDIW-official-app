package gov.moda.dw.manager.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import gov.moda.dw.manager.service.dto.NonceDTO;

/**
 * View Model object for storing a user's credentials.
 */
public class LoginVM {

  @NotNull
  private String username;

  @NotNull
  private String password;

  private Boolean rememberMe;

  private NonceDTO nonce;

  private String captcha;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(Boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  public NonceDTO getNonce() {
    return nonce;
  }

  public void setNonce(NonceDTO nonce) {
    this.nonce = nonce;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  @Override
  public String toString() {
    return (
      "LoginVM{" +
      "username='" +
      username +
      '\'' +
      ", password='" +
      password +
      '\'' +
      ", rememberMe='" +
      rememberMe +
      '\'' +
      ", nonce='" +
      nonce +
      '\'' +
      ", captcha='" +
      captcha +
      '\'' +
      '}'
    );
  }
}
