package gov.moda.dw.issuer.oidvci.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RelType {
  USERTOROLE("1", "User", "Role"),
  ROLETORES("2", "Role", "Res"),
  ACCESSTOKENTORES("3", "AccessToken", "Res");

  @Getter
  private String code;

  @Getter
  private String leftTbl;

  @Getter
  private String rightTbl;

  public static RelType toAdminType(String code) {
    for (RelType tmp : RelType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
