import { boot } from "quasar/wrappers";
import { useLinksStore } from "src/stores/links";

const tokenKey = "jwt-user-object";

export default boot(({ router, urlPath, redirect }) => {
  const { isAuthorizedLink, isPublicLink } = useLinksStore();
  router.beforeEach((to, from) => {
    const path = to.path;
    const isAuthorized = getToken() && isAuthorizedLink(path);

    const homepath = process.env.VITE_BASE_PATH || "/";

    if (!isPublicLink(path) && !isAuthorized && urlPath !== homepath) {
      removeToken();
      closeConnect();

      // 踢回登入頁面
      console.log("kick", path);
      redirect({ path: "/" });
    }
  });
});

export function getToken() {
  return sessionStorage.getItem(tokenKey);
}

export function setToken(token) {
  return sessionStorage.setItem(tokenKey, token);
}

export function removeToken() {
  sessionStorage.clear();
  localStorage.clear();
  deleteCookie("finRefreshToken");
}

function deleteCookie(cookieName) {
  document.cookie =
    cookieName + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}

export function closeConnect() {
  try {
    if (websocket.readyState === websocket.OPEN) {
      websocket.send("bye");
    }
  } catch (e) {}
}
