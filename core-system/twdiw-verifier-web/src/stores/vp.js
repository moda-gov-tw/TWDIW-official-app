import { defineStore } from "pinia";
import { getToken } from "src/boot/auth";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const $n = useNotify();
const jwtToken = getToken();

export const useVpStore = defineStore("vpStore", () => {
  const { t } = useI18n();

  // state
  const vpList = ref([]);
  const vpDetailList = ref([]);
  const vpItemData = ref();
  const loading = ref(false);
  const detailLoading = ref(false);
  const qrCodeDetail = ref({});
  const categoriesLoading = ref(false);
  const categoriesVDRList = ref([]);
  const categoriesSpecialListLoading = ref(false);
  const cateoriesSpecialList = ref([]);
  const cateoriesVCItemFieldLoading = ref(false);
  const cateoriesVCItemFieldsList = ref([]);
  const createVPLoading = ref(false);
  const qrcodeLoading = ref(false);
  const checkAllFieldByStepLoading = ref(false);
  const saveTermsLoading = ref(false);
  const verifierDID = ref(false);
  // 模式下拉選單
  const modelTypeList = ref([]);

  const pagination = ref({
    sortBy: "upDatetime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    serialNo: "",
    name: "",
    startDate: "",
    endDate: ""
  });

  // VP列表(Page)
  async function getVpList(
    page = 1,
    options = {
      size: 10,
      sortBy: "upDatetime",
      descending: true,
      serialNo: "",
      name: "",
      startDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = options.sortBy
      ? `${options.sortBy},${options.descending ? "desc" : "asc"}`
      : "id,asc";

    const params = {
      sort: sortStr,
      page: page - 1,
      size: options.size === 0 ? 2000 : options.size,
      serialNo: options.serialNo,
      name: options.name,
      startDate: options.startDate,
      endDate: options.endDate
    };

    if (options.serialNo) {
      params["serialNo"] = options.serialNo;
    }
    if (options.name) {
      params["name"] = options.name;
    }

    //獲取當前時區偏移量（格式化為 +HH:00 或 -HH:00）
    const tzOffset = new Date().getTimezoneOffset();
    const tzString = `${tzOffset <= 0 ? "+" : "-"}${String(
      Math.abs(tzOffset / 60)
    ).padStart(2, "0")}:00`;

    if (options.startDate) {
      params["startDate"] = `${options.startDate}T00:00:00${tzString}`;
    }

    if (options.endDate) {
      params["endDate"] = `${options.endDate}T23:59:59${tzString}`;
    }

    try {
      const response = await api.get("/api/vp-item", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      vpList.value = response.data.data;

      verifierDID.value = response.data.verifierDID;

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: options.sortBy || "id",
        descending: options.descending || false,
        rowsPerPage: options.size
      };
      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      loading.value = false;
    }
  }

  // 查詳情
  async function getVpDetail(id) {
    detailLoading.value = true;
    try {
      const response = await api.get(`/api/vp-item/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      // vpDetailList.value = response.data;
      vpItemData.value = {
        ...response.data,
        model: modelTypeList.value.find(
          (data) => data.value === response.data.model
        )
      };
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      detailLoading.value = false;
    }
  }

  function resetDetail() {
    vpItemData.value = null;
    qrCodeDetail.value = {};
  }

  // 取得模式下拉選單
  async function getModelTypeSelect() {
    try {
      const response = await api.get(`/api/vp-item/getModelTypeSelect`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      modelTypeList.value = response.data.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    }
  }

  // 刪除VP
  async function deleteVp(row) {
    const id = row.id;

    await api
      .delete(`/api/vp-item/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then(async (res) => {
        const { page, rowsPerPage, sortBy, descending } = pagination.value;
        const { serialNo, name, startDate, endDate } = currentFilter.value;

        await getVpList(page, {
          size: rowsPerPage,
          sortBy,
          descending,
          serialNo,
          name,
          startDate,
          endDate
        });
        $n.success(t("vp.success.delete", { name: row.serialNo }));
      })
      .catch((err) => {
        const errorMessage = err.response?.data?.message || t("unknownError");
        $n.error(t(errorMessage));
        return null;
      })
      .finally(() => {
        loading.value = false;
      });
  }

  // 取得 QR Code
  async function getQrcode(id) {
    qrcodeLoading.value = true;
    try {
      const response = await api.get(`/api/vp-item/${id}/qrcode`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      qrCodeDetail.value = response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);
      const errorMessage = error.response?.data?.message || t("unknownError");
      $n.error(t(errorMessage));
      return null;
    } finally {
      qrcodeLoading.value = false;
    }
  }

  // 取得 QR Code 驗證結果
  async function getQrcodeValidate(transaction_id) {
    try {
      const response = await api.get(
        `/api/vp-item/verifyResult/${transaction_id}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );
      return response.data;
    } catch (error) {
      console.log(error);
      return null;
    }
  }

  // 第四階段 取得所有類別-VDR

  async function getCategoriesVDR() {
    categoriesLoading.value = true;
    try {
      const response = await api.get("/api/categories", {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      categoriesVDRList.value = response.data;
      // name 要加ename在後面
      categoriesVDRList.value.forEach((item) => {
        item.name = item.name + "（" + item.nameEn + "）";
      });
    } catch (error) {
      console.error("Error:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      categoriesLoading.value = false;
    }
  }

  async function getCateoriesSpecialList(obj) {
    categoriesSpecialListLoading.value = true;

    const { taxId } = obj;

    const params = {
      taxId
    };

    try {
      const response = await api.post(`/api/vc-items/specialOrder`, params, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      cateoriesSpecialList.value = response.data;
      cateoriesSpecialList.value.forEach((item) => {
        item.businessName = obj.name;
      });
    } catch (error) {
      console.error("Error:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
      cateoriesSpecialList.value = [];
    } finally {
      categoriesSpecialListLoading.value = false;
    }
  }

  async function getCateoriesVCItemFields(params) {
    cateoriesVCItemFieldLoading.value = true;
    try {
      const response = await api.post(
        `/api/vc-item-fields?page=0&size=2000`,
        params,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );
      cateoriesVCItemFieldsList.value = response.data;
    } catch (error) {
      console.error("Error:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      cateoriesVCItemFieldLoading.value = false;
    }
  }

  async function createVP(obj) {
    createVPLoading.value = true;
    try {
      const response = await api.post("/api/vp-item", obj, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // 創建成功後返回創建的 VP 數據
      if (response.data) {
        $n.success(t("vp.success.create"));

        // 立即獲取詳細資訊
        await getVpDetail(response.data.id);

        // 獲取 QR Code
        if (vpDetailList.value?.length > 0) {
          await getQrcode(vpDetailList.value[0].vpItemId);
        }

        return response.data;
      }
    } catch (error) {
      console.error("Error:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      createVPLoading.value = false;
    }
  }

  // [API] 編輯 VP
  async function editVp(data) {
    createVPLoading.value = true;

    try {
      const response = await api.put("/api/vp-item/" + data.id, data, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // 編輯成功後返回編輯的 VP 數據
      if (response.data) {
        $n.success(t("vp.success.edit"));

        // 立即獲取詳細資訊
        await getVpDetail(response.data.id);

        // 獲取 QR Code
        if (vpDetailList.value?.length > 0) {
          await getQrcode(vpDetailList.value[0].vpItemId);
        }

        return response.data;
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      createVPLoading.value = false;
    }
  }

  // 儲存VP授權條款
  async function saveTerms(obj) {
    saveTermsLoading.value = true;

    const response = await api
      .post(`/api/vp-item/saveTerms`, obj, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then(async (res) => {
        const { page, rowsPerPage, sortBy, descending } = pagination.value;
        const { serialNo, name, startDate, endDate } = currentFilter.value;

        await getVpList(page, {
          size: rowsPerPage,
          sortBy,
          descending,
          serialNo,
          name,
          startDate,
          endDate
        });
        $n.success(t("vp.success.delete", { name: obj.serialNo }));
      })
      .catch((err) => {
        const errorMessage = err.response?.data?.message || t("unknownError");
        $n.error(t(errorMessage));
        return null;
      })
      .finally(() => {
        saveTermsLoading.value = false;
      });
  }

  // 取得VP模板的授權條款
  async function getVpTerms(serialNo, businessId) {
    try {
      const response = await api.get(
        `/api/vp-item/getTerms/${serialNo}/${businessId}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      return response.data;
    } catch (error) {
      console.error("Error:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    }
  }

  // 建立 VP 時的步驟檢核
  async function checkAllFieldByStep(obj) {
    checkAllFieldByStepLoading.value = true;
    try {
      const response = await api.post("/api/vp-item/valid", obj, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // By step 檢核成功
      return true;
    } catch (error) {
      console.error("Error:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
      return false;
    } finally {
      checkAllFieldByStepLoading.value = false;
    }
  }

  // [API] 取得靜態 QR Code
  async function getStaticQRCode(credentialType) {
    try {
      const response = await api.get(`/api/vp-item/get/staticQRCode`, {
        params: { credentialType },
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      return response.data.data;
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("unknownError");
      $n.error(t(errorMessage));

      return null;
    }
  }

  // [API] 匯出靜態 QR Code
  async function downloadStaticQRCode(base64) {
    try {
      const response = await api.post(
        "/api/vp-item/download",
        { base64 },
        {
          responseType: "blob",
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      // 建立 Blob
      const blob = new Blob([response.data], { type: "image/png" });
      const url = window.URL.createObjectURL(blob);

      // 建立下載連結
      const link = document.createElement("a");
      link.href = url;
      link.download = "qrcode.png";
      document.body.appendChild(link);
      link.click();

      // 清理資源
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("unknownError");
      $n.error(t(errorMessage));

      return null;
    }
  }

  function resetPagination() {
    pagination.value = {
      sortBy: "upDatetime",
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
      rowsPerPageOptions: [10, 20, 50, 0]
    };
  }

  return {
    getVpList,
    getVpDetail,
    getModelTypeSelect,
    resetDetail,
    deleteVp,
    getQrcode,
    getQrcodeValidate,
    getCategoriesVDR,
    getCateoriesSpecialList,
    getCateoriesVCItemFields,
    createVP,
    editVp,
    resetPagination,
    saveTerms,
    getVpTerms,
    checkAllFieldByStep,
    getStaticQRCode,
    downloadStaticQRCode,
    vpList,
    verifierDID,
    loading,
    pagination,
    vpDetailList,
    detailLoading,
    modelTypeList,
    qrCodeDetail,
    categoriesLoading,
    categoriesVDRList,
    categoriesSpecialListLoading,
    cateoriesSpecialList,
    cateoriesVCItemFieldLoading,
    cateoriesVCItemFieldsList,
    createVPLoading,
    currentFilter,
    qrcodeLoading,
    saveTermsLoading,
    vpItemData,
    checkAllFieldByStepLoading
  };
});
