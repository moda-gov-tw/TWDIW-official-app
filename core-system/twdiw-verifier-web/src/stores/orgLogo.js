import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { ref } from "vue";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { HttpStatusCode } from "axios";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useOrgLogoStore = defineStore("orgLogo", () => {
  const $n = useNotify();
  const { t } = useI18n();
  const loading = ref(false);
  const verifierDID = ref(false);
  const orgInfo = ref("");
  const rectangleSampleLogo = ref("");

  // [API] 組織 LOGO
  async function searchOrg() {
    loading.value = true;
    try {
      const response = await api.get("/api/modadw102w/uploadOrgLogo", {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      loading.value = false;
      verifierDID.value = response.data.data.verifierDID;
      orgInfo.value = response.data.data;
    } catch (error) {
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    } finally {
      loading.value = false;
    }
  }

  // [API] 組織 LOGO 上傳
  async function uploadOrgLogo(org, callBackSuccess) {
    loading.value = true;

    try {
      const response = await api.post("/api/modadw102w/uploadOrgLogo", org, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      if (response && response.data && response.data.code === "0") {
        loading.value = false;
        $n.success(t("orgLogo.uplpoadSuccess"));
        callBackSuccess();
      } else {
        if (response.data.msg) {
          $n.error(response.data.msg);
        }
        loading.value = false;
      }
    } catch (error) {
      if (error.status === HttpStatusCode.Forbidden) {
        $n.error(t("orgLogo.error.noEditPermission"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
      loading.value = false;
    }
  }

  // [API] 還原預設長方形 LOGO
  async function getDefaultLogo(orgName) {
    loading.value = true;
    try {
      const response = await api.get(
        "/api/modadw102w/uploadOrgLogo/reductionDefault",
        {
          params: { orgName },
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      if (response.data.code === "0") {
        loading.value = false;
        rectangleSampleLogo.value = response.data.data.defaultRectangleLogo;
      } else {
        if (response.data.msg) {
          $n.error(response.data.msg);
        }
        loading.value = false;
      }
    } catch (error) {
      console.error("getDefaultLogo error:", error);
    } finally {
      loading.value = false;
    }
  }

  return {
    loading,
    verifierDID,
    orgInfo,
    rectangleSampleLogo,
    searchOrg,
    uploadOrgLogo,
    getDefaultLogo
  };
});
