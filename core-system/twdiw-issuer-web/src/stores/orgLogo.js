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
  const issuerDID = ref(false);
  const orgInfo = ref("");
  const rectangleSampleLogo = ref("");

  async function searchOrg() {
    loading.value = true;
    try {
      const response = await api.get("/api/modadw102w/uploadOrgLogo", {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      loading.value = false;
      issuerDID.value = response.data.data.issuerDID;
      orgInfo.value = response.data.data;
    } catch (error) {
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    } finally {
      loading.value = false;
    }
  }

  // 組織 LOGO 上傳
  async function uploadOrgLogo(org, callBackSuccess) {
    loading.value = true;
    const requestData = {
      orgId: org.orgId,
      logoSquare: org.logoSquare,
      logoRectangle: org.logoRectangle
    };

    if (requestData.orgId === "default") {
      $n.error(t("orgLogo.notice.default"));
      loading.value = false;
      return { isUploadOK: false };
    } else {
      try {
        const response = await api.post(
          "/api/modadw102w/uploadOrgLogo",
          requestData,
          {
            headers: {
              Authorization: `Bearer ${jwtToken}`
            }
          }
        );

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
  }

  // 還原預設長方形 LOGO
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
    issuerDID,
    orgInfo,
    loading,
    rectangleSampleLogo,
    searchOrg,
    uploadOrgLogo,
    getDefaultLogo
  };
});
