export default {
  roleChange: {
    title: "角色異動清單查詢",
    table: {
      id: "資料 ID",
      actor: "操作者",
      code: "角色代號",
      name: "名稱",
      desc: "描述",
      enabled: "啟用狀態",
      actionType: "異動",
      logTime: "紀錄時間",
      createTime: "建立時間",
      authChangeTime: "上次變更權限時間"
    },
    search: {
      beginDate: "紀錄時間(起)",
      endDate: "紀錄時間(迄)"
    },
    detail: "角色異動詳情",
    actionTypeAdd: "異動為新增",
    actionTypeModify: "異動為修改",
    actionTypeDelete: "異動為刪除",
    error: {
      noPermission: "您沒有查詢角色異動清單的權限"
    }
  }
};
