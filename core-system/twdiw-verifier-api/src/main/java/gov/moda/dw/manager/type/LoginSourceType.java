package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoginSourceType {
  WEB("web"),
  API("api");

  @Getter
  private String code;

  public static LoginSourceType toLoginType(String code) {
    for (LoginSourceType tmp : LoginSourceType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
