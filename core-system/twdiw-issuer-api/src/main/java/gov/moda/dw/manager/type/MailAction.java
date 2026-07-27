package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailAction {
  ACCOUNT_CREATE("A01", "帳號管理_帳號啟用信", "user"),
  ACCOUNT_RESET_BWD("A02", "帳號管理_重置密碼信", "user"),
  FORGOT_PASSWORD_RESET_BWD("B01", "登入頁面_忘記密碼_重置密碼信", "user"),
  ACCOUNT_NOT_ACTIVATED_DELETE("D01", "帳號管理_未激活帳號_激活時間到期刪除", "user"),
  ACCOUNT_RESET_ACCESS_TOKEN("R01", "個人資料_換發 Access Token", "user"),
  ACCOUNT_VERIFY_OTP("O01", "登入頁面_驗證電子郵件", "user");

  private final String mailType;

  private final String name;

  private final String recipient_role;

  public static MailAction toMailType(String code) {
    for (MailAction tmp : MailAction.values()) {
      if (tmp.getMailType().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
