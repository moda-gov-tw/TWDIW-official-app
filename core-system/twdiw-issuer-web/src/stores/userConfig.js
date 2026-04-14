import { defineStore } from "pinia";
import { LocalStorage } from "quasar";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { useRouter } from "vue-router";
import { getToken, removeToken, closeConnect } from "src/boot/auth.js";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useUserConfigStore = defineStore("userConfig", () => {
  //plugin settings
  const $n = useNotify();
  const $r = useRouter();
  const { t } = useI18n();

  const defaultConfig = {
    freeze: true
  };

  const config = LocalStorage.getItem("config") || { ...defaultConfig };

  const save = () => {
    try {
      LocalStorage.setItem("config", config);
    } catch (error) {
      console.error("error setting userconfig in localStorage");
    }
  };
  //state
  const freeze = ref(config.freeze);
  const userDetails = ref({});
  const fromAPIorgId = ref("");

  //methods
  function toggleFreeze() {
    freeze.value = !freeze.value;
    config.freeze = freeze.value;
    save();
  }

  function setDefaultFreeze() {
    freeze.value = defaultConfig.freeze;
  }

  // 取得個人資料
  async function getUser() {
    const url = "/api/modadw311w/getUserDetails";
    const requestData = {};

    return await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((res) => {
        if (!res.data) {
          $n.error(t("account.error.emptyResponse"));
        }

        if (!res.data.code) {
          $n.error(t("account.error.noCode"));
        }

        if (res.data.code !== "0") {
          $n.error(res.data.msg || t("account.error.backendError"));
        }

        const fromAPI = res.data.data;
        if (fromAPI) {
          fromAPIorgId.value = fromAPI.orgId;
          userDetails.value = fromAPI;
          userDetails.value.orgId =
            userDetails.value.orgId + " " + userDetails.value.orgTwName;
        }
      })
      .catch((err) => {
        console.error(`${t("error.notice")}：${err}`);
      });
  }

  // 更新個人資料
  async function updateCurrentUser(updateData) {
    const url = "/api/modadw311w/updateUserDetails";
    const requestData = {
      userId: updateData?.userId,
      userName: updateData?.userName,
      tel: updateData?.tel
    };
    return await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((res) => {
        if (!res.data) {
          $n.error(t("account.error.emptyResponse"));
          return false;
        }

        if (!res.data.code) {
          $n.error(t("account.error.noCode"));
          return false;
        }

        if (res.data.code !== "0") {
          $n.error(res.data.msg || t("account.error.backendError"));
          return false;
        }

        const fromAPI = res.data.data;
        if (fromAPI) {
          userDetails.value = fromAPI;
          $n.success(t("account.success.personalInfo"));
          return true;
        }

        return false;
      })
      .catch((err) => {
        console.error(`${t("error.notice")}：${err}`);
      });
  }

  // 登出
  function onLogout() {
    console.debug(t("logout"));
    api
      .post(
        "/api/modadw303w/logout",
        {},
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      )
      .then(() => {
        removeToken();
        closeConnect();
        setDefaultFreeze();
        $n.success(t("logoutSuccess"));
      })
      .catch((error) => {
        if (
          error.response &&
          error.response.data &&
          error.response.data.detail
        ) {
          $d.alert(t("account.confirm"), error.response.data.detail);
        }
      })
      .finally(() => {
        $r.replace("/");
      });
  }

  return {
    freeze,
    toggleFreeze,
    onLogout,
    userDetails,
    getUser,
    updateCurrentUser,
    setDefaultFreeze,
    fromAPIorgId
  };
});
