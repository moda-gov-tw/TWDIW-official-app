import { defineStore } from "pinia";
import { ref } from "vue";

export const useAlertStore = defineStore("alert", () => {
  const alert = ref(false);
  const alertSuccess = ref(false);
  const alertText = ref("");
  const cardType = ref("");
  const usingMode = ref("");
  const certLoading = ref(false);

  // 顯示錯誤提示框
  const showError = (message) => {
    alertText.value = message;
    alert.value = true;
    loadingHide();
  };

  // 顯示成功提示框
  const showSuccess = (message) => {
    alertText.value = message;
    alertSuccess.value = true;
    loadingHide();
  };

  function createLoadingDiv() {
    const loadingDiv = document.createElement("div");
    loadingDiv.id = "loadingDiv";
    loadingDiv.className = "loading-hidden";

    const overlay = document.createElement("div");
    overlay.className = "loading-overlay";

    const spinner = document.createElement("div");
    spinner.className = "spinner";

    overlay.appendChild(spinner);
    loadingDiv.appendChild(overlay);
    document.body.appendChild(loadingDiv);
  }
  createLoadingDiv();

  /**
   * 顯示 loading 的 modal
   */
  const loadingShow = () => {
    document.getElementById("loadingDiv").style.display = "block";
    certLoading.value = true;
  };

  /**
   * 隱藏 loading 的 modal
   */
  const loadingHide = () => {
    document.getElementById("loadingDiv").style.display = "none";
    certLoading.value = false;
  };

  return {
    alert,
    alertSuccess,
    alertText,
    cardType,
    usingMode,
    showError,
    showSuccess,
    loadingShow,
    loadingHide,
    certLoading
  };
});
