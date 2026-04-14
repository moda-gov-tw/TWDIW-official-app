import { defineStore } from "pinia";
import { apiWithInterceptors as api } from "boot/axios";
import { getToken } from "src/boot/auth";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";

export const useLoginStore = defineStore("loginStore", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = useI18n();

  //state
  const jwtToken = getToken();

  // 確認目前密碼是否未過期
  async function isPasswordNotExpired(userName, token) {
    const url = `/api/ams303w/isPasswordNotExpired`;
    const data = { login: userName };
    try {
      // 發送 POST 請求到後端
      const response = await api.post(url, data, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        }
      });

      // 檢查回應狀態
      if (response && response.code && response.msg) {
        return { code: response.code, msg: response.msg };
      } else {
        throw new Error(t("login.error.pwdExpiredException"));
      }
    } catch (error) {
      // 捕獲錯誤並記錄，回傳false表示發生錯誤。
      console.error(t("login.error.pwdExpired"), error.message || error);
      return { code: "1", msg: error.message || error || t("unknownError") };
    }
  }

  return { isPasswordNotExpired };
});
