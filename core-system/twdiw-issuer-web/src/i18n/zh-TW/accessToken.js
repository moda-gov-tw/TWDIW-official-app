export default {
  accessToken: {
    title: "AccessToken 管理",
    table: {
      id: "資料 ID",
      token: "AccessToken",
      name: "Token 名稱",
      ownerId: "擁有者 ID",
      orgId: "組織",
      orgName: "組織名稱",
      ownerName: "擁有者",
      tokenType: "類型",
      stock: "存量",
      expireAt: "期限",
      enabled: "啟用狀態",
      createTime: "建立時間",
      updateTime: "更新時間"
    },
    add: "新增 AccessToken",
    detail: "Token 詳情",
    edit: "編輯 AccessToken",
    resList: "賦予權限清單",
    editToken: "編輯 Token",
    finish: "AccessToken 資料修改完成",
    noPermission: "尚未綁定任何功能權限",
    enabled: "請問確定({input})要啟用？",
    disabled: "請問確定({input})要停用？",
    delete: "請問確定要刪除以下{input}嗎？",
    deleteSuccess: "{input}已刪除",
    updateSuccess: "{input}變更完成",
    success: "已完成 AccessToken 授權。",
    viewAndEdit: "檢視及編輯",
    refresh: {
      message:
        "系統將會寄送新的 Access Token 至您的電子郵件，同時註銷舊的 Access Token。",
      success: "換發 Access Token 完成"
    }
  }
};
