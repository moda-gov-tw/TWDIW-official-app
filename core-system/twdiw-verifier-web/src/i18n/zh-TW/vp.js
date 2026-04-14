export default {
  vp: {
    detail: "詳細資料",
    createVPDisabled:
      "請先於「Verifier 功能/註冊 DID/建立驗證端 DID 身分」才可『建立VP』。",
    confirmData: "請確認資料是否正確",
    detailTitle: "VP 模板資料",
    vpRef: "驗證服務代碼 (ref)",
    attributeData: "屬性資料",
    vcName: "VC 模板名稱",
    generalTab: "一般模式",
    staticQRCodeTab: "靜態 QR Code 模式",
    serialNo: {
      label: "VP 代碼",
      pattern: "只能輸入小寫英文、數字和_",
      placeholder: "請輸入小寫英文、數字或_"
    },
    name: {
      label: "VP 名稱",
      pattern: "只能輸入中文、英文或數字",
      placeholder: "請輸入中文、英文或數字"
    },
    purpose: {
      label: "VP 授權目的",
      pattern: "只能輸入中文、英文或數字",
      placeholder: "請輸入中文、英文或數字"
    },
    terms: {
      label: "VP 授權條款",
      status: {
        edited: "已編輯",
        notEdited: "未編輯"
      },
      notice: "▲ 使用者在進行 VP 授權時，需同意該授權條款才可完成流程。"
    },
    model: {
      label: "模式",
      staticNotice:
        "▲ 靜態 QR Code 模式：驗證單位可匯出或列印靜態 QR Code，供使用者掃描驗證使用。",
      offlineNotice:
        "▲ APP 出示憑證模式：使用者點選 APP 中『出示憑證』功能顯示 QR Code，供驗證單位掃描驗證使用。",
      noChoose: "無"
    },
    serviceUrl: {
      label: "組織業務系統 URL",
      pattrrn: "請輸入正確的 URL 格式",
      notice: "▲ 填寫 VP QR Code 或 deeplink 的 URL"
    },
    callBackUrl: {
      label: "CallBack URL",
      pattrrn: "請輸入正確的 URL 格式",
      notice: "▲ 填寫需接收使用者提供驗證資料的 URL。"
    },
    customFields: {
      name: "欄位",
      label: "使用者自定義資料",
      notice:
        "▲ 限定資料格式：此欄位必填，使用者在 APP 中輸入資料內容前的說明，建議可填寫『請輸入(欄位中文名稱)』，如果資料內容已有填寫(預設值)將不顯示。\n▲ 資料內容：此欄位非必填，驗證時使用者在 APP 會顯示預設的資料內容，如果不填寫將顯示限定資料格式。",
      fields: {
        description: {
          label: "限定資料格式"
        },
        cname: {
          label: "欄位對外名稱",
          pattern: "只能輸入中英文數字和_"
        },
        ename: {
          label: "欄位名稱(英)",
          pattern: "只能輸入英文數字和_(不可輸入id)"
        },
        regex: {
          label: "正規表達式",
          pattern: "請使用正規表達式語法（如：^[0-9]+$、\\d+）",
          notVaild: "請使用正規表達式語法",
          errorMessage: "請輸入有效的正規表達式"
        },
        value: {
          label: "資料內容",
          regexBlank: "請先填寫正規表達式",
          notVaild: "不符合正規表達式",
          errorMessage: "正規表達式格式錯誤"
        }
      }
    },
    encryptEnabled: {
      label: "模組加密",
      notice:
        "▲ 如勾選將透過模組進行加解密，如不勾選資料將無加密提供，如果資料超過一筆將以 JSON 格式提供。",
      yes: "是",
      no: "否"
    },
    tag: {
      label: "TAG",
      notice:
        "▲ 請填寫英文或數字代碼，供驗證單位接收資料時判別情境使用，例如:使用者點選 APP 中『出示憑證』功能顯示 QR Code，給機器掃描做超商取貨的代號。"
    },
    btn: {
      createVP: "建立 VP",
      editTerms: "管理 VP 授權條款",
      advancedFeature: "進階功能（未上線）",
      importVcData: "選擇 VC 資料匯入",
      groupRule: "選取規則",
      selectAll: "全選",
      cancelAll: "取消全選",
      regenerateQrCode: "重新產生 QR Code",
      openWithMobile: "使用手機開啟",
      exportQrCode: "匯出 QR Code"
    },
    groups: {
      notice: "▲ 選取規則建議設定為『最多可選取張數』",
      rules: {
        title: "選取規則",
        content:
          " ▲ 選取規則建議設定為『最多可選取張數』，即使用者最多可選擇輸入的張數送出\n▲ 如點選『必須全選』，即使用者必須選擇全部的 VC 才可以送出\n▲ 使用者每次提供資料 VP 時，最少必須選擇一張 VC 才可以送出\n▲ 1 個 VP 最多可以建立 10 個群組，1 個群組可以建立 10 張 VC"
      },
      addRemove: "增刪",
      name: "群組名稱",
      pickRule: {
        label: "使用者選取規則",
        pick: "最多可選取",
        pcs: "張",
        all: "必須全選",
        notBlank: "請輸入最多可選取筆數",
        minOne: "數字需要大於 0",
        maxTen: "數字不可大於 10",
        pattern: "請輸入整數"
      },
      importVcData: "VC 資料匯入",
      index: "排序",
      error: {
        maxGroups: "最多只能新增 10 個群組",
        groupVcLimit: "群組 {groups} 不能選擇超過 10 筆 VC 資料",
        duplicateNames: "群組名稱 重複：{names}，請修改！",
        emptyGroup: "群組 {groups} 至少匯入一筆 VC 資料",
        pickCountFail: "群組 {groups} 最多可選取筆數不可大於已選擇 VC 模板數"
      },
      ruleAllMsg:
        "使用者在 APP 中必需『選擇全部』 {count} 個 VC 模板才可授權驗證",
      rulePickMsg: "使用者在 APP 中『最多可選擇』 {max} 個 VC 模板進行授權驗證",
      noRule: "未命名"
    },
    importVc: {
      selectVcTemplate: "請選擇 VC 模板",
      orgName: "組織名稱",
      selectedVcTemplates: "已選 {count} 個 VC 模板（最多可選 10 個）",
      noSelectedVc: "尚未選取任何 VC 模板",
      vcName: {
        label: "VC 模板名稱",
        pattern: "只能輸入中文、英文或數字"
      },
      vcSerialNo: {
        label: "VC 模板代碼",
        pattern: "只能輸入小寫英文、數字和_"
      },
      filterCondition: "篩選條件",
      reset: "重設",
      filter: "篩選",
      maxSelectVc: "最多只能選擇 10 個 VC 模板"
    },
    selectFields: {
      filter: "請輸入關鍵字",
      title: "請選擇欄位資料",
      noResultData: "無相符搜尋結果",
      selected: "已選擇的欄位資料",
      customFieldsNotice: "可調整接收 VP 的欄位名稱",
      customFieldName: {
        placeholder: "請輸入自定義欄位名稱(英)",
        pattern: "只能輸入英文數字和_(不可輸入id)",
        duplicateHint: "{name} 已重複，相同的欄位名稱只會擇一做 VP 資料提供"
      },
      error: {
        pickAtLeastMax: "群組「{groupName}」至少勾選 {max} 個 VC 模板",
        pickAtLeastOne: "群組「{groupName}」至少勾選 1 個 VC 模板"
      }
    },
    qrCode: {
      title: "請使用 數位憑證皮夾 APP 掃描 QR Code",
      notice:
        "請注意 QR Code 只能使用一次，如發生 QR Code 失效或倒數結束，請點選『重新產生 QR CODE』，再掃描 QR Code 完成流程。",

      countdown: "驗證倒數",
      result: {
        success: "驗證結果 => 成功",
        fail: "驗證結果 => 失敗"
      },
      error: {
        poll: "輪詢驗證結果失敗",
        regenerate: "重新產生 QR Code 失敗",
        missingRequiredParam: "缺少必要參數"
      }
    },
    dialog: {
      title: {
        confirm: "確認",
        createVp: "建立 VP",
        editVp: "編輯 VP",
        terms: "授權條款",
        detail: "VP 詳細資料"
      },
      deleteVP: "請確認是否要刪除 VP 模板「{serialNo}」?",
      alertMessage: "▲ 請注意!!! VP 代碼確認後無法再編輯調整。",
      terms: {
        contentTitle: "授權條款內容",
        importExampleTermsBtn: "匯入 VP 授權條款範例",
        basicNotice: "數字及英文單字前建議加空格以利 APP 排版顯示。",
        exampleNotice: "請務必確認【】內的文字（共 10 個）是否已調整完成!!!",
        placeholder: "請匯入或編輯授權條款內容",
        comfirmTitle: "確認匯入",
        comfirmContent: "原有內容將會被覆蓋，是否要匯入條款範例？",
        error: "讀取錯誤"
      }
    },
    stepHeader: {
      step1: "步驟一",
      step2: "步驟二",
      step3: "步驟三",
      step4: "步驟四"
    },
    stepCaption: {
      step1: "基本資料",
      step2: "新增群組",
      step3: "選擇欄位",
      step4: "確認資料"
    },
    success: {
      create: "建立成功",
      edit: "編輯成功",
      saveTerms: "成功儲存 「{name}」VP 授權條款 ",
      delete: "成功刪除 「{name}」VP 模板 "
    }
  }
};
