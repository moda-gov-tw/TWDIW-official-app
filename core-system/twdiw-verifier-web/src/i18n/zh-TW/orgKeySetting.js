export default {
  orgKeySetting: {
    title: "組織金鑰管理",
    didNotRegistered:
      "請先於「Verifier 功能/註冊 DID/建立驗證端 DID 身分」才可『新增金鑰』。",
    isActive: "使用中金鑰",
    activateNotice: "▲ 如需啟用請至組織金鑰管理頁面調整勾選『使用中金鑰』。",
    isEnabled: "是否啟用",
    enabled: "啟用",
    disabled: "未啟用",
    detail: "金鑰詳情",
    addKey: "新增金鑰",
    generateKey: "產生金鑰",
    keyId: {
      label: "金鑰代碼",
      required: "請輸入金鑰代碼",
      maxLength: "字數不能超過 50 個字",
      pattern: "只能輸入小寫英文、數字和_",
      placeholder: "請輸入小寫英文、數字或_"
    },
    description: {
      label: "金鑰備註",
      maxLength: "字數不能超過 18 個字",
      pattern: "只能輸入中英文數字",
      placeholder: "請輸入中文、英文或數字"
    },
    publicKey: {
      label: "Curve25519 公鑰",
      required: "請輸入 Curve25519 公鑰",
      pattern: "只能輸入數字、英文大小寫與 {allowed}",
      placeholder: "請輸入合法金鑰，只能包含數字、英文大小寫與 {allowed}"
    },
    privateKey: {
      label: "Curve25519 私鑰",
      pattern: "只能輸入數字、英文大小寫與 {allowed}",
      placeholder: "請輸入合法金鑰，只能包含數字、英文大小寫與 {allowed}"
    },
    totpKey: {
      label: "TOTP 金鑰",
      required: "請輸入 TOTP 金鑰",
      notice: "採用 HMAC-SHA256 演算法，金鑰長度 256 位元",
      pattern: "只能輸入數字、英文大小寫與 {allowed}",
      placeholder: "請輸入合法金鑰，只能包含數字、英文大小寫與 {allowed}"
    },
    hmacKey: {
      label: "HMAC 金鑰",
      required: "請輸入 HMAC 金鑰",
      notice: "採用 HMAC-SHA256 演算法，金鑰長度 256 位元",
      pattern: "只能輸入數字、英文大小寫與 {allowed}",
      placeholder: "請輸入合法金鑰，只能包含數字、英文大小寫與 {allowed}"
    },
    dialog: {
      title: {
        notice: "金鑰啟用注意事項",
        activateKey: "金鑰啟用確認"
      },
      noticeMessage: "▲ 如需啟用請至組織金鑰管理頁面調整勾選『使用中金鑰』。",
      deleteMessage: "請確認是否要刪除 金鑰「{keyId}」?",
      activate: {
        message: "請確認是否要啟用此金鑰「{keyId}」?",
        alert: "注意！若按「確認」，此金鑰將選定為該組織金鑰。"
      }
    },
    error: {
      generatedFail: "建立失敗"
    },
    success: {
      createKey: "新增成功",
      generateKey: "產生金鑰成功",
      activateKey: "成功啟用 金鑰「{keyId}」",
      deleteKey: " 成功刪除 金鑰「{keyId}」"
    }
  }
};
