package gov.moda.dw.issuer.oidvci.security.accessToken;

import lombok.Data;

/**
 * 這是客製化的AccessToken物件
 */
@Data
public class AccessTokenUserObject {

  private String accessToken;

  private String accessTokenName;

  private String owner;

  private String ownerName;

  private String orgId;

  private String orgName;

  private String loginIp;
}
