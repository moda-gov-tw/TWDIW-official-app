package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountActionType {
  CREATE("01"),
  UPDATE("02"),
  RESET_PWD("03"),
  CHANGE_ACTIVATED("04"),
  DELETE_ACCOUNT("05"),
  CHANGE_ROLE("06"),
  RESET_ACCESS_TOKEN("07"),
  VERIFY_OTP("08");

  private final String code;

  public static AccountActionType toAccessTokenState(String code) {
    for (AccountActionType tmp : AccountActionType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
