export default {
  //header
  accountSettings: "個人資料設定",
  personEdit: "個人資料設定",
  bwdEdit: "修改密碼",
  resetAccessToken: "換發 Access Token",
  logout: "登出",
  logoutSuccess: "成功登出",

  //帳號權限
  accountAuth: "帳號權限",
  accountManagement: {
    delete: "刪除帳號",
    add: "新增帳號",
    edit: "帳號編輯",
    view: "帳號檢視"
  },
  account: {
    title: "帳號管理",
    visible: {
      true: "隱藏帳號",
      false: "顯示帳號"
    },
    table: {
      id: "資料 ID",
      orgId: "組織",
      org: "組織",
      orgTwName: "組織名稱",
      userTypeId: "帳號類型",
      onboardDate: "到職日",
      leftDate: "離職日",
      userId: "帳號(電子郵件)",
      name: "暱稱",
      mail: "信箱",
      phone: "手機",
      tel: "手機號碼",
      level: "類型",
      enabled: "啟用狀態",
      createdBy: "建立者",
      updatedBy: "最後更新者",
      createTime: "建立時間",
      updateTime: "最後更新時間",
      employeeId: "證件編號",
      employeeTypeId: "員工類型",
      resendActivationEmail: "重寄啟用信",
      row: "{start}-{end}列，共{total}列"
    },
    detail: "詳情",
    edit: "編輯",
    view: "檢視",
    userRoles: "已授權角色清單",
    reset: "重置密碼",
    roleConfiguration: "授權角色",
    accountRoleConfiguration: "帳號授權角色",
    dataPermissions: "資料權設定",
    toggleStatus: "帳號狀態",
    unverified: "未驗證",
    userAccount: "使用者帳號(暱稱)",
    setRol: "已設定角色",
    selected: "已選",
    accountRole: "帳號角色",
    resend: "重寄啟用信",
    accountInfo: "請確定是否要{action}帳號「 {input} 」?",
    resetBwd: "請確定是否要重置密碼「 {input} 」?",
    deleteAccount: "請確定是否要刪除帳號「 {input} 」?",
    resendInfo: "請確定是否要重新寄送啟用信「 {input} 」?",
    valid: {
      notLessThan: "不得少於10碼",
      notGreaterThan: "不得大於20碼",
      notEnterChinese: "請勿輸入中文字",
      onlyCan: "只能輸入數字",
      length: "輸入內容的長度不能超過{max}個字元！",
      format: "{input}格式錯誤"
    },
    state: {
      enabled: "啟用帳號",
      disabled: "停用帳號"
    },
    confirm: "請確認",
    success: {
      edit: "{input}修改完成",
      accountState: "已{action}，「 {input} 」。",
      create: "帳號建立完成，已寄出啟動帳號信件至信箱。",
      resendPwdMail: "重置密碼完成，已寄出重置密碼信件至信箱。",
      activateMail: "帳號啟用信已重新寄發。",
      personalInfo: "個人資料 更新成功",
      editBwd: "修改密碼完成"
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
    },
    query: {
      enabled: "狀態為啟用",
      disabled: "狀態為停用",
      range: "時間範圍"
    },
    bwd: {
      nowBwd: "密碼",
      enterNowBwd: "輸入現在的密碼",
      newBwd: "新密碼",
      enterNewBwd: "輸入新的密碼",
      confirmBwd: "確認新密碼",
      reCheck: "再次輸入新密碼",
      notBlank: "不可為空",
      hide: {
        true: "隱藏密碼",
        false: "顯示密碼"
      },
      valid: {
        include:
          "*密碼必須包含一個大小寫英文字母及特殊符號{symbols}, 且至少 12 碼",
        notGreaterThan: "不得大於 99 碼",
        inconsistency: "與新密碼不一致"
      }
    }
  },
  activate: {
    title: "【數位憑證皮夾】發行端模組系統_重設您的密碼",
    newPwdTip: "請輸入您的新密碼",
    newPwd: "新密碼",
    confirmPassword: "新密碼確認",
    reCheck: "再次輸入您的新密碼",
    hide: {
      true: "隱藏密碼",
      false: "顯示密碼"
    },
    valid: {
      include:
        "*密碼必須包含一個大小寫英文字母及特殊符號{symbols}, 且至少 12 碼",
      notGreaterThan: "不得大於 99 碼",
      inconsistency: "與新密碼不一致"
    },
    success: {
      finish: "帳號啟用及重設密碼完成，即將導回首頁。",
      valid: "連結 驗證完畢"
    },
    error: {
      invalid: "無效連結",
      reset: "重設密碼",
      info: "無效連結，請注意，密碼重設要求的效期只有24小時。"
    }
  }
};
