export default {
  regularExpression: {
    title: "正規表示法",
    add: "新增正規表示法",
    edit: "編輯正規表示法",
    regular: "正規表達式",
    errorMsg: "錯誤訊息",
    ruleType: "規則類型",
    rule: "規則",
    table: {
      type: "類別",
      name: "名稱",
      description: "限定資料格式"
    },
    notice: {
      delete: "請確認是否要刪除正則表示法「 {input} 」？"
    },
    select: {
      custom: "自定義",
      specified: "基本",
      allow: "符合規則時允許輸入",
      deny: "符合規則時不允許輸入"
    },
    dialog: {
      id: "id",
      example: "（例：手機號碼）",
      exampleRegular: String.raw`（例：手機號碼 {regular}）`,
      exampleDescription: "（例：09 開頭共 10 碼數字）",
      errorMessagesDescription: " (例：請輸入 09 開頭共 10 碼數字) ",
      valid: {
        lessThan: "長度不能超過{max}個字",
        regular: "請輸入有效的正規表達式"
      }
    },
    success: {
      delete: "成功刪除「{name}」正規表示法"
    }
  }
};
