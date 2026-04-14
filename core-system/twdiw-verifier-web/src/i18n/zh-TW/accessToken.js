export default {
  accessToken: {
    title: "AccessToken 管理",
    create: "新增 AccessToken",
    detail: "Token 詳情",
    edit: "編輯 AccessToken",
    table: {
      id: "資料 ID",
      token: "AccessToken",
      name: "AccessToken 名稱",
      ownerId: "擁有者 ID",
      orgId: "組織 ID",
      orgName: "組織名稱",
      ownerName: "擁有者",
      tokenType: "類型",
      stock: "存量",
      expireAt: "期限",
      enabled: "啟用狀態",
      createTime: "建立時間",
      updateTime: "更新時間"
    },
    noPermissionBound: "尚未綁定任何功能權限",
    button: {
      edit: "編輯 Token"
    },
    message: {
      confirmChangeState: "請問確定(Token名稱:{name})要{action}？",
      confirmDelete: "請問確定要刪除以下AccessToken嗎？"
    },
    resList: "賦予權限清單",
    refresh: {
      message:
        "系統將會寄送新的 Access Token 至您的電子郵件，同時註銷舊的 Access Token。",
      success: "換發 Access Token 完成"
    },
    success: {
      authorized: "已完成 AccessToken 授權。",
      create: "新增 AccessToken 成功。",
      edit: "AccessToken 資料修改完成。",
      delete: "AccessToken 已刪除。",
      changeState: "AccessToken 啟停狀態變更完成。"
    },
    error: {
      title: "錯誤",
      permissionDenied: "您沒有權限執行此操作",
      accountState:
        "帳號啟停狀態變更失敗，請檢查您的操作或確認您是否具有此權限。",
      emptyResponse: "response 為空",
      noCode: "後端未回傳狀態碼",
      backendError: "發生錯誤，但後端並未回傳錯誤訊息",
      wrongBwd: "輸入的密碼不正確，請重試。",
      unknown: "發生了一個未知的錯誤。",
      noResponse: "無法連接到伺服器或伺服器無回應。",
      repeatPrefix: "新密碼不能與最近的 {count} 次密碼相同。"
    }
  }
};
