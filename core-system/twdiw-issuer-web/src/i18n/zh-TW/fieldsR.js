export default {
  basicFields: {
    title: "基本欄位",
    column: {
      type: "欄位類別",
      cname: "欄位對外名稱",
      ename: "欄位名稱(英)",
      visible: "是否顯示"
    },
    show: {
      true: "顯示",
      false: "隱藏"
    },
    message: {
      visible: "是否要{show}「 {field} 」欄位？"
    }
  },
  normalFields: {
    title: "常用欄位",
    add: "新增常用欄位",
    column: {
      information: "詳細資訊"
    },
    dialog: {
      addAndDelete: "增刪",
      cname: "欄位對外名稱",
      ename: "欄位名稱(英)",
      regularExpression: "限定資料格式",
      error: {
        repeat: "以下欄位名稱(英)重複：{input}"
      }
    },
    information: {
      id: " id",
      type: "類別",
      name: "名稱",
      regular: "正規表達式",
      errorMsg: "錯誤訊息",
      ruleType: {
        allow: "符合規則時允許輸入",
        deny: "符合規則時不允許輸入"
      },
      rule: "規則"
    },
    visible: {
      confirmShow: "是否要顯示「{name}」欄位？",
      confirmHide: "是否要隱藏「{name}」欄位？"
    },
    delete: {
      confirm: "請確認是否要刪除欄位「{name}」？"
    },
    error: {
      visible: "隱藏/顯示失败",
      delete: "刪除失败"
    },
    success: {
      delete: "成功刪除 「{name}」 欄位",
      show: "已顯示「{name}」欄位",
      hide: "已隱藏「{name}」欄位"
    }
  }
};
