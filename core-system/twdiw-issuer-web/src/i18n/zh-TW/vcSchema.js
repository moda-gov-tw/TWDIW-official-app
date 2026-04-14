export default {
  vcSchema: {
    title: "VC 管理",
    createVC: "VC 建立管理",
    add: "建立 VC 模板",
    edit: "編輯 VC 資料來源",
    removeVC: "VC 資料狀態管理",
    ScheduledDataCleanup: "定時清除資料排程",
    orgTwName: "組織中文名稱",
    show: "顯示",
    disabled: "隱藏",
    startDate: "建立時間(起)",
    endDate: "建立時間(迄)",
    temporaryStorage: "暫存模板",
    uploaded: "已上傳卡面",
    notes: "請點選注意事項查看",
    validityPeriod: "有效期間",
    defaultValidityPeriod: "預設有效期間",
    expirationDate: "到期日",
    expirationDateNotice:
      "非必填，如填寫該張 VC 以此時間為到期日，如未填寫該張 VC 以發行時間加上預設有效期間為到期日。",
    createTitle: "VC 建立管理",
    credentialType: "證件類型",
    issuerUrl: "發行端網址",
    ial: "IAL 等級",
    noIalChoice: "IAL 無選擇",
    revocation: "撤銷",
    stopIssuing: "停止發行",
    tempTag: "（暫存）",
    inactiveTag: "（停止發行）",
    table: {
      serialNo: "VC 模板代碼",
      name: "VC 模板名稱",
      org: "組織",
      crDatetime: "建立時間",
      owner: "我的建立",
      information: "詳細資訊",
      build: "產生 VC 資料",
      description: "限定資料格式",
      deleteStopIssuing: "刪除/停止發行"
    },
    notice: {
      first:
        "請先於「Issuer 功能/註冊 DID/建立發行端 DID 身分」才可『＋建立 VC 模板』。",
      delete: "請確認是否要刪除 VC 模板「 {field} 」？",
      stopIssuing:
        "請確認是否要停止發行 VC 模板「 {field} 」？\n如執行停止發行動作，此模板將不可再發行卡片．但現有已發行卡片仍有效。",
      ial: "▲ 此選項非必選，供發行端決定該 VC 之身分保證等級 (IAL)，IAL3 為最高等級。",
      verifyNotice:
        "▲ 此選項預設非勾選，如勾選後，使用者申請 VC 時，需進行驗證碼驗證才可以完成流程。",
      issuerServiceUrlNotice: "▲ 請填寫使用者申請該 VC 的外部網站 URL。",
      webView: "▲ WebView：嵌入 APP 中開啟網頁加入憑證。",
      openPage:
        "▲ 外開網頁：跳轉至其他瀏覽器 APP 開啟網頁後再回到APP加入憑證。",
      picNotice:
        "▲ 建議圖片尺寸為長度 320px，寬度 200px，限制長寬比為1.6：1，長度需小於等於 2048px，檔案大小應介於 40kb 至500kb 之間，檔案類型為 JPG、PNG。",
      picNoticeCustom:
        "▲ 第一個 VC 欄位資料為必填欄位，將顯示在卡面左下角，即『 自訂欄位文字區塊 』。",
      showExpireTimeInfo:
        "▲ VC 有效時間自掃描 VC 資料的 QR Code 後開始計算，超過有效期間或被撤銷即為失效。",
      temporaryStorage: "▲ 請注意!!! VC 模板代碼於暫存後無法再行調整。"
    },
    select: {
      DAY: "日",
      MONTH: "月",
      YEAR: "年",
      default: "請選擇",
      basic: "基本欄位",
      normal: "常用欄位",
      custom: "自定義欄位",
      1: "外開網頁",
      2: "WebView"
    },
    error: {
      repeat: "欄位名稱(英)重複{field}，請修改！",
      delete: "無法進行刪除",
      edit: "無法進行修改",
      test: "無法進行測試"
    },
    valid: {
      pic: "請上傳有效的圖片檔案（JPG, PNG）",
      size: "檔案大小應介於 40kb 至 500kb 之間",
      lengthLessThan: "長度需小於等於 2048px",
      limit: "限制長寬比為 1.6 : 1",
      readerError: "讀取圖片失敗",
      commonFields: "請先建立常用欄位",
      checkVerifyCode: "請填寫完整驗證碼"
    },
    dialog: {
      title: "建立 VC 模板",
      editVCServiceUrlTitle: "編輯靜態 QR Code 功能",
      buildVcDataTitle: "產生 VC 資料",
      serialNo: {
        placeholder: "請輸入小寫英文、數字或_",
        rules: {
          input: "請輸入 VC 模板代碼",
          format: "只能輸入小寫英文、數字和_"
        }
      },
      name: {
        placeholder: "即為發行卡面的名稱，請輸入中文、英文或數字",
        rules: {
          input: "請輸入 VC 模板名稱",
          length: "字數不能超過18個全形",
          format: "只能輸入中文、英文或數字"
        }
      },
      validityPeriod: {
        placeholder: "請輸入數字",
        rules: {
          input: "請輸入有效期間",
          greaterThanZero: "數字需要大於0",
          integer: "請輸入整數"
        }
      },
      ial: {
        firstOption: "請選擇",
        noChoice: "IAL 無選擇"
      },
      isVerify: {
        label: "驗證碼模式"
      },
      qrCode: {
        label: "靜態 QR Code",
        type: "模式",
        issuerServiceUrl: "組織業務系統 URL",
        false: "否",
        1: "外開網頁",
        2: "WebView"
      },
      cardUpload: {
        label: "卡面上傳",
        placeholder: "請點選注意事項查看"
      },
      button: {
        showMore: "進階功能（未上線）",
        notice: "注意事項",
        imageVector: "圖片向量檔下載",
        imageSpecification: "圖片規範下載",
        submit: "確認",
        step: {
          true: "上一步",
          false: "下一步"
        },
        export: "匯出靜態 QR Code",
        show: "展示靜態 QR Code",
        regenerateQRCode: "重新產生 QR Code",
        resetOtpCode: "重新輸入驗證碼",
        openWithMobile: "使用手機開啟"
      },
      imagePreview: "圖片預覽",
      table: {
        addAndDelete: "增刪",
        sort: "排序",
        format: "只能輸入中英文數字和_",
        length: "字數不能超過18個字",
        ename: {
          format: "只能輸入英文數字和_(不可輸入 id)",
          placeholder: "請輸入英文、數字或_"
        },
        placeholder: "請輸入中文、英文、數字或_"
      },
      true: "是",
      false: "否"
    },
    rules: {
      unit: "請選擇單位",
      check: "請選擇是否對外顯示",
      required: "必填項目",
      length: "字數不能超過 {max} 個字",
      field: "請輸入{field}",
      selectField: "請選擇{field}",
      url: "請輸入正確的 URL 格式"
    },
    tooltip: {
      deleteVcSchema: "刪除 VC 模板",
      disableVcSchema: "停止發行 VC 資料"
    },
    info: {
      id: "序號",
      serialNo: "VC 模板代碼",
      name: "VC 模板名稱(發行卡面的名稱)",
      org: "組織",
      crDatetime: "建立時間",
      credentialType: "證件類型 (credentialType) ",
      isVerify: "是否啟用驗證碼模式",
      isStaticQrcode: "是否啟用靜態 QR Code 模式",
      issuerServiceUrl: "組織業務系統 URL"
    },
    vcData: {
      inputNotice:
        "▼ 第一個 VC 欄位資料，將顯示在卡面左下角，即『自訂欄位文字區塊』。",
      ruleTypeDeny: "此欄位的規則為：符合規則時不允許輸入",
      expirationDateError: "到期日 格式錯誤，最大日期為 9999-12-31",
      expirationDateInvalid: "到期日不可以早於今日或建立 VC 模板日期",
      qrCodeTitle: "請使用 數位憑證皮夾 APP 掃描 QR Code",
      qrCodeNotice:
        "請注意 QR Code 只能使用一次，如發生 QR Code 失效或倒數結束，請重新至 VC 管理頁面操作『產生 VC 資料』以完成流程。",
      otpModeNotice:
        "請在 APP 下方『掃描』中開啟『顯示驗證碼』確認 2 位驗證碼後，按下『確認』，於畫面下方輸入此 2 位驗證碼點選『下一步』，再使用 APP 掃描 QR Code 完成流程。",
      countdown: "驗證倒數",
      pollError: "輪循錯誤",
      result: {
        success: "發行成功"
      }
    },
    vcDataSource: {
      header: "Header",
      headerKey: "key 值",
      headerValue: "value 值",
      inputHeaderKey: "Key：請輸入",
      inputHeaderValue: "Value：請輸入",
      inputUrl: "URL：請輸入",
      header: {
        duplicate: "Header 的 Key 重複：{keys}"
      },
      error: {
        header: "解析 headers 時發生錯誤"
      }
    },
    success: {
      delete: "成功刪除 「{name}」 VC 模板",
      edit: "成功編輯 「{name}」 VC 模板",
      stopIssuing: "成功停止發行 VC 模板「{name}」 "
    }
  }
};
