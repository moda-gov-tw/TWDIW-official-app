export default {
  remove: {
    title: "VC 資料狀態管理",
    orgId: "組織",
    qrcodeStartTime: "產製時間(起)",
    qrcodeEndTime: "產製時間(迄)",
    issuanceDate: "VC 發行時間",
    expiredDate: "到期日",
    noExpiredDate: "未設定到期日",
    content: "資料",
    vcCidMask: "VC 卡片序號",
    logDatetime: "異動時間",
    detail: "詳細資訊",
    history: "歷程記錄",
    selected: "已選擇 {count} 列",
    selectedVC: "已選取 VC 資料",
    btn: {
      notice: "注意事項",
      vcStatusChange: "VC 資料狀態管理"
    },
    status: {
      label: "狀態",
      normal: "正常",
      inactive: "停用",
      revoked: "撤銷",
      expired: "過期",
      reuse: "復用"
    },
    notice: {
      label: "注意事項",
      statusChange: "狀態異動通知",
      normalDesc: "可調整為「停用」、「撤銷」",
      inactiveDesc:
        "可調整為「復用」即為「正常」，APP 上卡片顯示停用，無法進行 VP 驗證",
      inactiveToRevokedDesc: "可調整為「撤銷」",
      revokedDesc: "為最終狀態無法再調整",
      expiredDesc: "APP 上卡片顯示失效，無法進行 VP 驗證",
      removeAlertOwnVc: "▼ 只能撤銷自己建立的 VC 模板所產生的 VC 資料",
      removeAlertOrgVc: "▼ 只能撤銷自己組織建立的 VC 模板所產生的 VC 資料",
      executableListTitle: "可執行 VC 資料狀態變更",
      nonExecutableListTitle: "不可執行 VC 資料狀態變更"
    },
    rule: {
      whenStatus: "僅狀態",
      disable: "時，可以{action}"
    },
    vcStatus: "資料狀態",
    expiredStatus: "過期狀態",
    dataTag: "資料標註",
    transactionId: "唯一交易序號",
    dialog: {
      button: {
        cancelStep: {
          true: "上一步",
          false: "取消"
        }
      }
    },
    error: {
      noSelectVc: "請選擇要異動的資料",
      init: "初始化失敗",
      fail: "資料狀態異動失敗",
      failDetail: "資料狀態異動失敗，請檢查以下資料：\n{list}",
      noUpdatableData: "無可更改狀態的 VC 資料"
    },
    success: {
      statusChange: "資料狀態異動成功",
      successDetail:
        "該次異動已調整 {count} 筆的狀態，請到 VC 資料狀態管理確認狀態是否已異動。"
    }
  }
};
