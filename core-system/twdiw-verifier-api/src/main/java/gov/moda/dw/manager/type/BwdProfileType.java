package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BwdProfileType {
  DEFAULT("default");

  @Getter
  private String code;

  public static BwdProfileType byCode(String code) {
    for (BwdProfileType tmp : BwdProfileType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }
}
