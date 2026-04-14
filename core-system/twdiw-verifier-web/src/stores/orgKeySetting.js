import { defineStore } from "pinia";
import { getToken } from "src/boot/auth";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const $n = useNotify();
const jwtToken = getToken();

export const useOrgKeySettingStore = defineStore("orgKeySettingStore", () => {
  const { t } = useI18n();

  // state
  const keyList = ref([]);
  const verifierDID = ref(false);
  const keyDetailData = ref();
  const loading = ref(false);
  const detailLoading = ref(false);
  const createKeyLoading = ref(false);
  const generateKeyLoading = ref(false);
  const deleteLoading = ref(false);
  const setKeyActiveLoading = ref(false);

  const pagination = ref({
    sortBy: "upDatetime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    keyId: "",
    description: "",
    startDate: "",
    endDate: ""
  });

  // 金鑰列表(Page)
  async function getKeyList(
    page = 1,
    options = {
      size: 10,
      sortBy: "upDatetime",
      descending: true,
      keyId: "",
      description: "",
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
      size: options.size === 0 ? 2000 : options.size
    };

    if (options.keyId) {
      params["keyId.contains"] = options.keyId;
    }
    if (options.description) {
      params["description.contains"] = options.description;
    }

    // 獲取當前時區偏移量（格式化為 +HH:00 或 -HH:00）
    const tzOffset = new Date().getTimezoneOffset();
    const tzString = `${tzOffset <= 0 ? "+" : "-"}${String(
      Math.abs(tzOffset / 60)
    ).padStart(2, "0")}:00`;

    if (options.startDate) {
      params[
        "upDatetime.greaterThan"
      ] = `${options.startDate}T00:00:00${tzString}`;
    }

    if (options.endDate) {
      params["upDatetime.lessThan"] = `${options.endDate}T23:59:59${tzString}`;
    }
    try {
      const response = await api.get("/api/modadw103/orgKeySetting", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      keyList.value = response.data.data;
      verifierDID.value = response.data.verifyDID;

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

  // 查金鑰詳情
  async function getKeyDetail(id) {
    resetDetailList();
    detailLoading.value = true;
    try {
      const response = await api.get(`/api/modadw103/orgKeySetting/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      keyDetailData.value = response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      detailLoading.value = false;
    }
  }

  // 刪除金鑰
  async function deleteKey(row) {
    const id = row.id;
    deleteLoading.value = true;
    try {
      await api.delete(`/api/modadw103/orgKeySetting/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { keyId, description, startDate, endDate } = currentFilter.value;

      await getKeyList(page, {
        size: rowsPerPage,
        sortBy,
        descending,
        keyId,
        description,
        startDate,
        endDate
      });
      $n.success(t("orgKeySetting.success.deleteKey", { keyId: row.keyId }));
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("unknownError");
      $n.error(t(errorMessage));
    } finally {
      deleteLoading.value = false;
    }
  }

  // 新增金鑰
  async function createKey(obj) {
    createKeyLoading.value = true;
    try {
      const response = await api.post("/api/modadw103/orgKeySetting", obj, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      if (response.data) {
        $n.success(t("orgKeySetting.success.createKey"));
        return response.data;
      }

      return null;
    } catch (error) {
      console.error("Error:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(errorMessage);
    } finally {
      createKeyLoading.value = false;
    }
  }

  // 產生金鑰
  async function generateKey() {
    generateKeyLoading.value = true;
    try {
      const response = await api.get(
        "/api/modadw103/orgKeySetting/generateKey",
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      // 產生成功後回傳 key 數據
      if (response.data) {
        $n.success(t("orgKeySetting.success.generateKey"));

        // 回傳值內容包含:
        const generateKey = {
          publicKey: response.data.publicKey,
          privateKey: response.data.privateKey,
          totpKey: response.data.totpKey,
          hmacKey: response.data.hmacKey
        };

        return generateKey;
      }
    } catch (error) {
      console.error("Error:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      generateKeyLoading.value = false;
    }
  }

  const setKeyActive = async (row) => {
    const id = row.id;
    setKeyActiveLoading.value = true;

    try {
      await api.post(`/api/modadw103/orgKeySetting/setKeyActive/${id}`, null, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { keyId, description, startDate, endDate } = currentFilter.value;

      await getKeyList(page, {
        size: rowsPerPage,
        sortBy,
        descending,
        keyId,
        description,
        startDate,
        endDate
      });

      $n.success(t("orgKeySetting.success.activateKey", { keyId: row.keyId }));
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("unknownError");
      $n.error(t(errorMessage));
    } finally {
      setKeyActiveLoading.value = false;
    }
  };

  function resetDetailList() {
    keyDetailData.value = {};
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
    getKeyList,
    keyList,
    pagination,
    currentFilter,
    verifierDID,
    createKey,
    createKeyLoading,
    getKeyDetail,
    keyDetailData,
    resetDetailList,
    generateKey,
    generateKeyLoading,
    deleteKey,
    deleteLoading,
    loading,
    resetPagination,
    setKeyActive,
    setKeyActiveLoading
  };
});
