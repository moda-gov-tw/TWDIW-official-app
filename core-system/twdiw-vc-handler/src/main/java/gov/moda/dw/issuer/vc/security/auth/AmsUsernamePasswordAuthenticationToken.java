package gov.moda.dw.issuer.vc.security.auth;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AmsUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

  @Getter
  private String nonceId;

  public AmsUsernamePasswordAuthenticationToken(Object principal, Object credentials, String nonceId) {
    super(principal, credentials);
    this.nonceId = nonceId;
  }
}
