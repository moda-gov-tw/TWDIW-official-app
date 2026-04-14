export default {
  role: {
    title: "角色管理",
    detailTitle: "角色詳情",
    create: "新增角色",
    edit: "編輯角色",
    resName: "功能名稱",
    resId: "功能代碼",
    table: {
      id: "資料 ID",
      code: "角色代號",
      name: "角色名稱",
      desc: "角色描述",
      state: "啟用狀態",
      createTime: "建立時間",
      updateTime: "更新時間"
    },
    state: {
      enabled: "啟用角色",
      disabled: "停用角色"
    },
    actions: {
      giveResource: "授權功能"
    },
    notice: {
      action: "請確認是否要{action}「 {field} 」?",
      actionSuccess: "已{action}，「 {field} 」。",
      defaultRoleState: "預設角色及admin不可停用",
      defaultRoleDelete: "預設角色及admin不可刪除"
    },
    detail: {
      funcDetail: "功能資訊",
      search: "請輸入欲查詢的功能名稱",
      noData: "角色尚未賦予任何功能"
    },
    resource: {
      title: "功能授權設定",
      selectedList: "已選功能清單",
      noSelectedList: "尚未選取任何功能"
    },
    error: {
      unknown: "發生未預期錯誤，請洽系統管理員",
      roleList: "取得角色列表時發生錯誤",
      delete: "刪除角色時發生錯誤"
    }
  }
};
