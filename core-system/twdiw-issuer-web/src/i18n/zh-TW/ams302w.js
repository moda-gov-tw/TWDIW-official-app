export default {
  ams302w: {
    title: "【數位憑證皮夾】發行端模組系統_重設您的密碼",
    inputPWD: "您的新密碼",
    recheck: "再次輸入您的新密碼",
    resetPassword: "重設密碼",
    fields: {
      newPwd: "新密碼",
      confirmPassword: "新密碼確認"
    },
    valid: {
      include:
        "*密碼必須包含一個大小寫英文字母及特殊符號{symbols}, 且至少 12 碼",
      notGreaterThan: "不得大於99碼",
      inconsistency: "與新密碼不一致",
      length: "輸入內容的長度不能超過{max}個字元！"
    },
    button: {
      true: "隱藏密碼",
      false: "顯示密碼"
    },
    success: {
      valid: "連結 驗證完畢",
      reset: "已重設密碼"
    },
    warn: {
      invalid: "無效連結"
    },
    error: {
      repeat: "密碼不可重複"
    },
    message: {
      resetPassword:
        "無效連結，請注意，密碼重設要求的效期只有 24 小時！<br/>確認後將導回首頁。",
      repeat: "新密碼不能與最近的 {input} 次密碼相同。"
    },
    confirm: "請確認"
  }
};
