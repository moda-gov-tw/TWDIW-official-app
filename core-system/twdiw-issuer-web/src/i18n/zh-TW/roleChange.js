export default {
  roleChange: {
    title: "角色異動清單查詢",
    detail: "角色異動詳情",
    rangeTime: "時間範圍",
    table: {
      id: "資料 ID",
      actor: "操作者",
      code: "角色代號",
      name: "名稱",
      desc: "描述",
      state: "啟用狀態",
      logType: "異動",
      logTime: "紀錄時間",
      createTime: "建立時間",
      authChangeTime: "上次變更權限時間"
    },
    search: {
      beginDate: "紀錄時間(起)",
      endDate: "紀錄時間(迄)"
    },
    logType: {
      add: "異動為新增",
      modify: "異動為修改",
      delete: "異動為刪除"
    },
    error: {
      noPermission: "您沒有查詢角色清單的權限"
    }
  }
};
