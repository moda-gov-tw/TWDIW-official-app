export default {
  trace: {
    title: "使用者操作軌跡管理",
    table: {
      id: "資料 ID",
      uuid: "uuid",
      requestIp: "來源 IP",
      funcType: "功能類型",
      uri: "URI",
      url: "URL",
      statusCode: "回傳狀態碼",
      RTT: "耗時(ms)",
      requestMethod: "請求方法",
      requestParam: "請求參數",
      requestHeader: "請求標頭",
      requestBody: "請求內容",
      responseHeader: "回應標頭",
      responseBody: "回應內容",
      token: "AccessToken",
      form: "帳號",
      to: "接收端",
      createTime: "建立時間"
    },
    detail: "使用者操作軌跡詳情",
    statusSuccess: "狀態為成功",
    statusFail: "狀態為失敗",
    error: {
      permissionDenied: "您沒有'查詢使用者查詢軌跡'的權限"
    }
  }
};
