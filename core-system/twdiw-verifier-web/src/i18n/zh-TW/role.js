export default {
  role: {
    id: "資料 ID",
    code: "角色代號",
    name: "角色名稱",
    desc: "角色描述",
    enabled: "啟用狀態",
    createTime: "建立時間",
    updateTime: "更新時間",
    btn: {
      creatRole: "新增角色"
    },
    funcDetail: "功能資訊",
    fillter: "請輸入欲查詢的功能名稱",
    noFunc: "角色尚未賦予任何功能",
    resName: "功能名稱",
    resId: "功能代碼",
    enabledRoles: "啟用角色",
    disabledRoles: "停用角色",
    actions: {
      giveResource: "授權功能"
    },
    doAction: "已{action}",
    confirmAction: "請確認是否要{action}「{name}」?",
    noData: "沒有相符資料",
    noResultData: "無相符搜尋結果",
    title: {
      giveResource: "功能授權設定",
      detail: "角色詳情",
      edit: "編輯角色",
      create: "新增角色"
    },
    selectedList: "已選功能清單",
    noSelected: "尚未選取任何功能",
    success: {
      giveResource: "授權功能完成",
      delete: "已刪除，「{name}」。",
      edit: "已編輯，「{name}」。"
    },
    error: {
      cannotDisable: "預設角色及 admin 不可停用",
      cannotDelete: "預設角色及 admin 不可刪除",
      unknownError: "發生未知錯誤，請稍後再試",
      fetchRoleListError: "取得角色列表時發生錯誤",
      deleteRoleError: "刪除角色時發生錯誤"
    }
  }
};
