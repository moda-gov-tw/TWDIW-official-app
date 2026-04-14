import { date } from "quasar";

export const format = () => {
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

  return { yyyyMMddSlash, yyyyMMdd, changeLocalDate, yyyyMMddHHmmss };
};
