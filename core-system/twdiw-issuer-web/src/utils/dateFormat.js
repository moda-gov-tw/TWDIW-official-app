import { date } from "quasar";
import { useI18n } from "vue-i18n";

export const format = () => {
  const { t } = useI18n();

  // 格式化時間成 yyyy/MM/dd
  const yyyyMMddSlash = (dateStr) => {
    const dateTime = new Date(dateStr);

    // 格式化日期
    const formattedDate = `${dateTime.getFullYear()}/${(dateTime.getMonth() + 1)
      .toString()
      .padStart(2, "0")}/${dateTime.getDate().toString().padStart(2, "0")}`;

    return formattedDate;
  };

  // 格式化時間成 yyyy/MM/dd
  const yyyyMMdd = (dateStr) => {
    const dateTime = new Date(dateStr);

    // 格式化日期
    const formattedDate = `${dateTime.getFullYear()}${(dateTime.getMonth() + 1)
      .toString()
      .padStart(2, "0")}${dateTime.getDate().toString().padStart(2, "0")}`;

    return formattedDate;
  };

  // 將UTC時間轉成Local時間
  const changeLocalDate = (dateStr) => {
    const dateObj = new Date(dateStr);
    const formatedString = date.formatDate(dateObj, "YYYY-MM-DDTHH:mm:ss.SSSZ");
    return formatedString;
  };

  const yyyyMMddHHmmss = (dateStr) => {
    return date.formatDate(new Date(dateStr), "YYYY/MM/DD HH:mm:ss");
  };

  const type = (typeName) => {
    switch (typeName) {
      case "Daily":
        return t("schedule.select.Daily");
      case "Weekly":
        return t("schedule.select.Weekly");
      case "Monthly":
        return t("schedule.select.Monthly");
      case "Quarterly":
        return t("schedule.select.Quarterly");
    }
  };

  const formatScheduleInfo = (row) => {
    switch (row.type) {
      case "Daily":
        return t("schedule.select.Daily");
      case "Weekly":
        if (row.week) {
          return `${t("schedule.select.weekday." + row.week)}`;
        }
        return "";
      case "Monthly":
        if (row.date) {
          return `${row.date}${t("schedule.select.day")}`;
        }
        return "";
      case "Quarterly":
        if (row.month && row.date) {
          const months = row.month.split(",");
          const formattedDates = months.map((month) => `${month}/${row.date}`);
          return formattedDates.join("、");
        }
        return "";
      default:
        return "";
    }
  };

  // 處理 time 欄位的轉換
  const convertTimeToLocal = (time, timezone) => {
    // 獲取時區偏移量（小時）
    const getTimezoneOffset = (tz) => {
      try {
        // 創建一個特定時區的日期對象
        const date = new Date();
        const formatter = new Intl.DateTimeFormat("en-US", {
          timeZone: tz,
          timeZoneName: "shortOffset" // 獲取短格式的時區偏移
        });

        // 獲取時區偏移字符串 (例如: "GMT+9" 或 "GMT-8")
        const tzOffset = formatter.format(date).split(" ")[1];

        // 提取數字部分
        const offset = parseInt(tzOffset.replace("GMT", ""));

        return offset;
      } catch (error) {
        console.warn(
          `Error parsing timezone ${tz}, using default GMT+8:`,
          error
        );
        return 8; // 默認使用台北時區
      }
    };

    // 處理時間
    const [hours, minutes] = time.split(":");
    let hour = parseInt(hours);

    // 獲取時區差值
    const tzOffset = getTimezoneOffset(timezone);

    // 計算與台北時區（GMT+8）的差異
    const hourDiff = 8 - tzOffset;

    // 調整時間
    hour = hour + hourDiff;

    // 處理跨日的情況
    if (hour < 0) {
      hour += 24;
    } else if (hour >= 24) {
      hour -= 24;
    }
    return `${hour.toString().padStart(2, "0")}:${minutes}`;
  };

  return {
    yyyyMMddSlash,
    yyyyMMdd,
    changeLocalDate,
    yyyyMMddHHmmss,
    type,
    formatScheduleInfo,
    convertTimeToLocal
  };
};
