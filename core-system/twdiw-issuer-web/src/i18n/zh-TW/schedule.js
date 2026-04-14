export default {
  schedule: {
    title: "定時清除資料排程",
    add: "新增定時清除資料排程",
    delete: "刪除 VC 清除資料排程",
    lastDelete: "最近一次 VC 清除資料",
    valid: "有效",
    revocation: "撤銷廢止",
    table: {
      type: "頻率",
      scheduleInfo: "排程資訊",
      time: "時間",
      lastRunDatetime: "最近一次執行時間",
      lastClearDateInfo: "最近一次 VC 清除資料"
    },
    dialog: {
      addTitle: "建立 VC 清除資料排程",
      frequency: "排程頻率",
      executionTime: "執行時間",
      executionWeek: "執行週期",
      date: "日"
    },
    select: {
      Daily: "每日",
      Weekly: "每週",
      Monthly: "每月",
      Quarterly: "每季",
      weekday: {
        1: "星期一",
        2: "星期二",
        3: "星期三",
        4: "星期四",
        5: "星期五",
        6: "星期六",
        7: "星期日"
      },
      quarter: {
        1: "一月",
        2: "二月",
        3: "三月",
        4: "四月",
        5: "五月",
        6: "六月",
        7: "七月",
        8: "八月",
        9: "九月",
        10: "十月",
        11: "十一月",
        12: "十二月"
      },
      day: "日"
    },
    notice: {
      recover: "撤銷後無法復原！！！！",
      searchRecover:
        "請確認是否要刪除該次排程，刪除後無法查詢最近一次執行時間及復原!!!"
    },
    column: {
      serialNo: "VC 模板代碼",
      vcItemName: "VC 模板名稱",
      vcCid: "VC 卡片序號",
      crDatetime: "產生 VC 資料時間",
      valid: "狀態"
    },
    success: {
      delete: "刪除成功"
    }
  }
};
