package gov.moda.dw.issuer.oidvci.security.jwt;

import lombok.Getter;

@Getter
public class JwtUserObject {

  //登入帳號
  private String userId;

  //所屬組織
  private String orgId;

  public JwtUserObject() {}

  public JwtUserObject(String userId, String orgId) {
    this.userId = userId;
    this.orgId = orgId;
  }
}
