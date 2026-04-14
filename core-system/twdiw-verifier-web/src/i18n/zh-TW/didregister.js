export default {
  didregister: {
    title: {
      true: "註冊 DID (已註冊)",
      false: "註冊 DID"
    },
    notice: {
      title: "注意事項"
    },
    noticeList: {
      didOnce: "請注意!!! 一組組織代號僅能進行一次 DID 註冊。",
      checkMoDev:
        "請先確認表格中『數位憑證皮夾前台服務』及『發證端/驗證端模組申請系統』是否成功。",
      govCert:
        "需以臺灣政府所發行『工商憑證、組織及團體憑證、或政府機關憑證』進行註冊 DID，請下載最新版本的 HiCOS 卡片管理工具。",
      pleaseFor: "請由",
      title: "MOICA 內政部憑證管理中心-檔案下載(另開視窗)",
      this: "這裡",
      downloadHicos:
        "下載最新版本的 HiCOS 卡片管理工具，以避免卡片 PIN 碼被鎖或是無法使用之情事發生，可參考安裝手冊。",
      success: "DID 註冊成功!! 請重新登入",
      logout: "十秒後將自動登出",
      BackToLogin: "回登入畫面"
    },
    select: {
      MOEACA: "使用工商憑證註冊 (MOEACA)",
      XCA: "使用組織及團體憑證註冊 (XCA)",
      GCA: "使用政府機關單位憑證註冊 (GCA)"
    },
    input: {
      info: "請輸入{input}資訊"
    },
    cardTypeName: {
      MOICA: "自然人憑證",
      MOEACA: "工商憑證",
      XCA: "組織及團體憑證",
      GCA: "政府機關憑證"
    },
    labelName: {
      PID: "身分證字號",
      orgID: "統一編號",
      PIN: "卡片 PIN 碼",
      nameTw: "組織中文名稱",
      nameEn: "組織英文名稱",
      info: "組織網址 (官網)",
      taxId: "組織統一編號",
      serviceBaseURL: "驗證端模組基礎網址",
      vcDataSource: "組織 VC 資料來源",
      token: "Access-Token"
    },
    valid: {
      PID: "請輸入格式正確的身分證號碼",
      orgID: "請輸入有效的 8 碼統一編號",
      token: "請輸入申請模組時取得的 access token",
      fix: "請修正所有錯誤欄位後再送出",
      inputAll: "請輸入所有欄位資料"
    },
    success: {
      reader: "已讀取到讀卡機與卡片",
      validSuccess: "已驗證成功!!"
    },
    error: {
      reader: "未偵測到讀卡機或憑證 IC 卡，請插卡後再重新勾選。",
      selectFirst:
        "請先選擇憑證類型，再插入工商憑證、組織管理憑證、政府機關憑證",
      noData: "模組註冊服務查無組織英文名稱",
      code: "錯誤代碼",
      unknown: "DID 檢驗服務未知錯誤，請聯絡系統管理員",
      verifierEndChain: "驗證端上鍊資訊查詢錯誤",
      vc: "查無組織對應的 vc 資料來源",
      orgName: "查無對應的組織名稱",
      init: "DID 註冊頁面初始化未知錯誤，請聯絡系統管理員",
      environmentFrontend:
        "環境檢查未通過，請檢查模組是否能連線至數位憑證皮夾前台服務",
      environmentIvpas:
        "環境檢查未通過，請檢查模組是否能連線至發證端/驗證端模組申請系統",
      environmentApi: "環境檢測 API 錯誤，請聯絡系統管理員",
      verifyService: "驗證服務未知錯誤，請聯絡系統管理員",
      didApi: "DID 註冊 API 未知錯誤，請聯絡系統管理員"
    },
    status: {
      0: "待審核",
      1: "有效",
      2: "已註銷",
      3: "審核未通過",
      4: "已審核上鏈中",
      5: "註銷中",
      unknown: "未知狀態"
    },
    testing: "檢測中",
    credentialsUsed: "使用的憑證",
    useCredentials: "使用憑證",
    detection: "偵測憑證",
    didInspection: "DID 檢驗",
    didState: "DID 狀態",
    createTime: "註冊時間",
    didRelated: "DID 相關",
    createDidIdentity: "建立驗證端 DID 身分",
    card: "卡片",
    vcData: "VC 資料來源模式",
    testItems: "檢測項目",
    result: "結果",
    frontendService: "數位憑證皮夾前台服務",
    ivpasService: "發證端/驗證端模組申請系統",
    fieldName: "欄位名稱",
    fieldData: "欄位資料",
    undefined: "未定義欄位",
    data: "資料",
    no: "無"
  }
};
