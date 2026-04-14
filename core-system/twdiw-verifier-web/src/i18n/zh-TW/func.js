export default {
  func: {
    title: "功能管理",
    table: {
      id: "資料 ID",
      res_group: "群組",
      code: "代號",
      description: "描述",
      name: "名稱",
      api_uri: "api_uri",
      web_url: "web_url",
      funcType: "類型",
      enabled: "啟用狀態",
      createTime: "建立時間",
      updateTime: "更新時間"
    },
    detail: "功能詳情",
    enabled: "啟用功能",
    disabled: "停用功能",
    message: "請確認是否要{state}「 {field} 」?",
    success: "已{state}，「 {field} 」。",
    error: {
      permissionDenied: {
        search: '您沒有"查詢功能"的權限',
        changeHistory: '您沒有"查詢功能異動清單"的權限',
        state: '您沒有"功能啟停"的權限'
      }
    }
  }
};
