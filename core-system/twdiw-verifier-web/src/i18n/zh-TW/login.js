export default {
  login: {
    title: "驗證端模組系統",
    email: "帳號(電子郵件)",
    pwd: "密碼",
    verificationCode: "驗證碼",
    placeholder: {
      account: "登入帳號為申請之電子郵件"
    },
    show: {
      true: "隱藏帳號",
      false: "顯示帳號"
    },
    button: {
      forget: "忘記密碼",
      login: "登入",
      operationManual: "操作手冊下載",
      verified: "已驗證",
      verify: "驗 證",
      resendOtp: "重新發送驗證碼"
    },
    dialog: {
      title: "【數位憑證皮夾】驗證端模組系統_忘記密碼",
      verifyOtpTitle: "驗證電子郵件",
      tel: "手機號碼",
      placeholder: {
        account: "帳號為申請之電子郵件。",
        tel: "請輸入申請時的手機號碼。"
      },
      valid: {
        lessThan: "不得小於10碼",
        greaterThan: "不得大於20碼"
      },
      success: {
        send: "重設密碼信已寄出"
      },
      message: {
        graphicError: "圖形驗證碼錯誤",
        graphicSuccess: "圖形驗證通過"
      },
      content: {
        send: "已寄送驗證碼至",
        otp1: "請輸入驗證碼完成驗證（5 分鐘內有效）。",
        otp2: "如果您未收到驗證碼，請點擊下方「重新發送驗證碼」。",
        resendOtp: "秒後可重新發送驗證碼"
      }
    },
    success: {
      login: "登入成功",
      sendOtp: "驗證碼發送成功",
      verifyOtp: "電子郵件驗證成功"
    },
    error: {
      token: "發生錯誤，進行登入後未能取得token",
      accountAndPasswordMistake: "帳號密碼錯誤",
      login: "登入失敗",
      mistake: "錯誤",
      expired: "登入已過期，請重新登入。",
      email: "請先輸入電子郵件",
      sendOtp: "驗證碼發送失敗",
      verifyOtp: "電子郵件驗證失敗",
      pwdExpiredException: "查詢登入密碼是否到期發生異常錯誤",
      pwdExpired: "查詢登入密碼是否到期發生錯誤，錯誤原因："
    }
  }
};
