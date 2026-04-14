package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LogType {
  DEL("delete", "刪除"),

  ADD("add", "新增"),

  MOD("modify", "修改");

  @Getter
  private String code;

  @Getter
  private String name;

  public static LogType toLogType(String code) {
    for (LogType tmp : LogType.values()) {
      if (tmp.getCode().equals(code)) {
        return tmp;
      }
    }
    return null;
  }

  public static LogType toLogTypeName(String name) {
    for (LogType tmp : LogType.values()) {
      if (tmp.getName().equals(name)) {
        return tmp;
      }
    }
    return null;
  }
}
