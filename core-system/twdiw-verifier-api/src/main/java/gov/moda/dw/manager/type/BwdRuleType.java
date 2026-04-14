/*
 * 版權宣告: CHTTL all rights reserved.
 */
package gov.moda.dw.manager.type;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/** 密碼規則類型 */
public enum BwdRuleType implements Serializable {
  /**
   * N代密碼不可重複<br>
   * 若值設定為0，代表完全可以重複。 最大支援到20代的記錄
   */
  Remember("1", "N代密碼不可重複", false, Integer.class),

  /** 首次登入強迫更改密碼 */
  ForceToChangeBwd("2", "首次登入強迫更改密碼", false, null),

  /** 密碼最長有效期(分鐘) */
  BwdMaximumAge("3", "密碼最長有效期(分鐘)", false, Integer.class),

  /** 密碼錯誤n次鎖定 */
  BwdLocked("4", "密碼錯誤N次鎖定", false, Integer.class),

  /** n分鐘後自動解鎖 */
  BwdLocked_AutoUnlock("5", "N分鐘後自動解鎖", false, Integer.class),

  /** 修改密碼時，帳號或密碼錯誤 */
  CredentialsError("32", "帳號或密碼錯誤", false, Integer.class),

  /** 帳號被管理者鎖定 */
  // Account_Lock_ByAdmin("6", "帳號被管理者鎖定", true, null),

  /** 組織IP白名單過濾 */
  // IP_Filter_Org("7", "組織IP白名單過濾", true, null),

  /** 帳號IP白名單過濾 */
  // IP_Filter_User("8", "帳號IP白名單過濾", true, null),

  /** 整個平台IP白名單過濾 */
  // IP_Filter_Platform("9", "整個平台IP白名單過濾", true, null),

  /** 密碼模式 */
  // Bwd_Authentication("11", "密碼模式", false, BwdRule_EncryptionType.class),

  /** 密碼最短有效期(分鐘) */
  BwdMinimumAge("12", "密碼最短有效期(N分鐘)", false, Integer.class),
  /** 密碼語法類型 AD & Custom */
  // BwdGrammarType("13", "密碼語法類型", false, BwdRule_GrammarType.class),

  /** 是否忽略大小寫 true = 忽略 false= 不忽略 */
  // BwdGrammar_Letter_IgnoreCase("14", "是否忽略大小寫,密碼模式為hash時不支援", false, Boolean.class),

  /** 密碼最少包含N個大寫字母 */
  BwdGrammar_Letter_IncludeMimUpCase("15", "密碼最少包含N個大寫字母", false, Integer.class),

  /** 密碼最多包含N個大寫字母 */
  // BwdGrammar_Letter_IncludeMaxUpCase("16", "密碼最多包含N個大寫字母", false, Integer.class),

  /** 密碼最少包含N個小寫字母 */
  BwdGrammar_Letter_IncludeMimLowCase("17", "密碼最少包含N個小寫字母", false, Integer.class),

  /** 密碼最多包含N個小寫字母 */
  // BwdGrammar_Letter_IncludeMaxLowCase("18", "密碼最多包含N個小寫字母", false, Integer.class),

  /**
   * 是否允許密碼包含數字<br>
   * true=允許包含<br>
   * false＝不允許包含<br>
   */
  // BwdGrammar_Number_isHasNumber("19", "允許密碼包含數字", false, Boolean.class),

  /**
   * 是否允許密碼以數字開頭<br>
   * true=允許<br>
   * false＝不允許<br>
   */
  // BwdGrammar_Number_isStartWithNumber("20", "是否允許密碼以數字開頭", false, Boolean.class),

  /**
   * 是否允許密碼以數字結尾<br>
   * true=允許<br>
   * false＝不允許<br>
   */
  // BwdGrammar_Number_isEndWithNumber("21", "是否允許密碼以數字結尾", false, Boolean.class),

  /** 密碼最少包含N個數字 */
  // BwdGrammar_Number_IncludeMimNumber("22", "密碼最少包含N個數字", false, Integer.class),

  /** 密碼最多包含N個數字 */
  // BwdGrammar_Number_IncludeMaxNumber("23", "密碼最多包含N個數字", false, Integer.class),

  /**
   * 是否允許密碼包含特殊符號<br>
   * true = 允許， false＝ 不允許
   */
  // BwdGrammar_Symbol_isHasSymbol("24", "是否允許密碼包含特殊符號", false, Boolean.class),

  /**
   * 是否允許密碼以特殊符號開頭<br>
   * true = 允許， false＝ 不允許
   */
  // BwdGrammar_Symbol_isStartWithSymbol("25", "是否允許密碼以特殊符號開頭", false, Boolean.class),

  /**
   * 是否允許密碼以特殊符號結尾<br>
   * true = 允許， false＝ 不允許
   */
  // BwdGrammar_Symbol_isEndWithSymbol("26", "是否允許密碼以特殊符號結尾", false, Boolean.class),

  /** 密碼最少包含N個符號 */
   BwdGrammar_Symbol_IncludeMimSymbol("27", "密碼最少包含N個符號", false, Integer.class),

  /** 密碼最多包含N個符號 */
  // BwdGrammar_Symbol_IncludeMaxSymbol("28", "密碼最多包含N個符號", false, Integer.class),

  /** 密碼最少N個字元 */
  BwdGrammar_Symbol_MinLength("29", "密碼最少N個字元", false, Integer.class),

  /** 密碼最多N個字元 */
  BwdGrammar_Symbol_MaxLength("30", "密碼最多N個字元", false, Integer.class)
  /** 指定的弱密碼清單 */
  // BwdBlockList("31", "弱密碼清單", false, String.class),
  ;

  private String ruleId;
  private String ruleName;
  private boolean addBwdRuleData;
  private Class<?> valueClass;

  @Getter
  private String msg;

  BwdRuleType(String ruleId, String ruleName, boolean addBwdRuleData, Class<?> valueClass) {
    this.ruleId = ruleId;
    this.ruleName = ruleName;
    this.valueClass = valueClass;
    this.addBwdRuleData = addBwdRuleData;
  }

  /** @return the ruleId */
  public String getRuleId() {
    return ruleId;
  }

  /** @return the ruleName */
  public String getRuleName() {
    return ruleName;
  }

  public static BwdRuleType toBwdRuleType(String ruleId) {
    BwdRuleType[] types = BwdRuleType.values();
    for (BwdRuleType tmp : types) {
      if (tmp.getRuleId().equals(ruleId)) {
        return tmp;
      }
    }
    return null;
  }

  /** @return the valueClass */
  public Class<?> getValueClass() {
    return valueClass;
  }

  public BwdRuleType setMsg(String msg) {
    this.msg = msg;
    return this;
  }

  public boolean isAddBwdRuleData() {
    return addBwdRuleData;
  }
}
