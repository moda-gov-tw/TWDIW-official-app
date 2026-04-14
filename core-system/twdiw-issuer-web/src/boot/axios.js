import { boot } from "quasar/wrappers";
import axios from "axios";
import { useNotify } from "src/utils/plugin";
import { setToken, getToken } from "boot/auth";

// for use inside Vue files (Options API) through this.$axios and this.$api
const tokenKey = "jwt-user-object";

//plugin settings
const $n = useNotify();

const baseURL = process.env.VITE_BASE_PATH
  ? `${window.location.origin}${process.env.VITE_BASE_PATH}`
  : undefined;

// 主要的 API 客戶端，使用動態 baseURL
const api = axios.create(baseURL ? { baseURL } : {});

// 新版 API 客戶端，具有完整的攔截器支援
const apiWithInterceptors = axios.create();

export default boot(({ app, redirect }) => {
  const addToken = (config) => {
    const token = sessionStorage.getItem(tokenKey);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  };

  const handleRequestError = (err) => {
    console.log(err);
    return Promise.reject(err);
  };

  const handleResponse = (response) => {
    let res = response;
    let token = response.headers.authorization;

    if (token) {
      const startToken = token.substring(0, 7);
      if (startToken === "Bearer ") {
        token = token.substring(7);
      }

      const oldToken = getToken();
      if (oldToken !== token) {
        setToken(token);
      }
    }

    return res;
  };

  const handleResponseError = (error) => {
    console.log("error=", error);
    if (error.response) {
      const errorData = error.response.data;
      const status = error.response.status;
      const path = window.location.pathname;
      const homepath = process.env.VITE_BASE_PATH || "/";
      switch (status) {
        case 403:
          // $n.warn("無此權限");
          break;
        case 401:
          if (errorData === "UNAUTHORIZED" && path !== homepath) {
            localStorage.setItem("sessionExpired", "true");
            redirect("/");
          }
      }
    } else {
      console.warn("後端回傳 error.response 為空");
      $n.error("發生不可預期之錯誤");
    }

    return Promise.reject(error);
  };

  // 設定請求攔截器：在每個請求前添加 JWT token
  api.interceptors.request.use(addToken, handleRequestError);
  apiWithInterceptors.interceptors.request.use(addToken, handleRequestError);

  // 設定回應攔截器：處理 token 更新與錯誤處理
  api.interceptors.response.use(handleResponse, handleResponseError);
  apiWithInterceptors.interceptors.response.use(
    handleResponse,
    handleResponseError
  );

  // 將 axios 掛載到 Vue 全域屬性，方便在元件中使用
  app.config.globalProperties.$axios = axios;

  // 將 api 實例掛載到 Vue 全域屬性，方便在元件中使用
  app.config.globalProperties.$api = api;
});

// 匯出 API 客戶端供其他模組使用
export { api, apiWithInterceptors };
