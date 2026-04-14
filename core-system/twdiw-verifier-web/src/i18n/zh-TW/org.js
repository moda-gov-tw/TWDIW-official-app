export default {
  org: {
    id: "資料 ID",
    orgId: {
      label: "組織代號",
      pattern: "只能輸入數字"
    },
    orgTwName: {
      label: "組織中文名稱",
      detailLabel: "中文名稱",
      pattern: "只能輸入中文、英文或數字跟『.-』"
    },
    orgEnName: {
      label: "組織英文名稱",
      detailLabel: "英文名稱",
      pattern: "只能輸入英文數字",
      inputPattern: "只能輸入英文數字及 .,&- 特殊符號"
    },
    metadata: "資料",
    rules: {},
    title: {
      detail: "組織詳情",
      create: "新增組織",
      edit: "編輯組織"
    },
    btn: {
      createOrg: "新增組織"
    },
    row: "{start}-{end}列，共{total}列",
    fieldName: "欄位名稱",
    fieldValue: "欄位資料",
    createNotice: "▲ 請務必確認發行端、驗證端系統皆新增一致的組織資訊。",
    cannotEditAfterConfirm: "(確認後將無法編輯)",
    inputPlaceholder: "請輸入資料",
    confirmDelete: "請確認是否要刪除「{name}」？",
    deleteSuccess: "已刪除，「{name}」。",
    undefined: "未定義欄位",
    error: {
      cannotEditDefault: "預設組織不可編輯",
      cannotDeleteDefault: "預設組織不可刪除",
      noQueryPermission: "您沒有「查詢組織」的權限",
      noCreatePermission: "您沒有「新增組織」的權限",
      noEditPermission: "您沒有「編輯組織」的權限",
      noDeletePermission: "您沒有「刪除組織」的權限",
      notFound: "查無資料",
      unknown: "發生未知錯誤"
    },
    success: {
      create:
        "新增組織『{name}』完成，請務必確認發行端、驗證端系統皆新增一致的組織資訊。",
      edit: "已編輯，「{name}」。"
    }
  }
};
