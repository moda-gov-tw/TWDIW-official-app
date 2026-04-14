export default {
  accountChange: {
    title: "帳號異動清單查詢",
    table: {
      id: "資料 ID",
      userId: "帳號",
      name: "名稱",
      mail: "信箱",
      tel: "電話",
      level: "類型",
      enabled: "啟用狀態",
      actionType: "異動",
      logTime: "紀錄時間",
      createTime: "建立時間",
      authChangeTime: "上次變更權限時間",
      pwdResetTime: "上次重置密碼時間"
    },
    actionTypeAdd: "異動為新增",
    actionTypeModify: "異動",
    search: {
      beginDate: "紀錄時間(起)",
      endDate: "紀錄時間(迄)"
    },
    detail: "帳號異動詳情",
    error: {
      permissionDenied: '您沒有"查詢帳號異動清單"的權限'
    }
  }
};
