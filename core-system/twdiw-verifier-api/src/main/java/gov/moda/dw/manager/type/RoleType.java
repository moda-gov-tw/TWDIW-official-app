package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoleType {
  // 發行端角色
  ISSUER_ADMIN("issuer_admin", "發行端沙盒系統最高權限管理者"),
  ISSUER_ORG_MANAGER("issuer_org_manager", "某組織最高權限管理者"),
  ISSUER_ORG_USER("issuer_org_user", "某組織使用者"),
  ISSUER_ORG_APUSER("issuer_org_apuser", "某組織系統呼叫端"),

  // 驗證端角色
  VERIFY_ADMIN("verify_admin", "驗證端沙盒系統最高權限管理者"),
  VERIFY_ORG_MANAGER("verify_org_manager", "某組織最高權限管理者"),
  VERIFY_ORG_USER("verify_org_user", "某組織使用者"),
  VERIFY_ORG_APUSER("verify_org_apuser", "某組織系統呼叫端"),

  // 預設角色
  DEFAULT_ROLE("default_role", "預設角色");

  @Getter
  private String code;

  @Getter
  private String name;

  public static RoleType toRoleType(String code) {
    for (RoleType tmp : RoleType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
