import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
import { getToken } from "src/boot/auth.js";

const jwtToken = getToken();

export const useVcSchemaStore = defineStore("vcSchema", () => {
  // state
  const loading = ref(false);
  const dialogLoading = ref(false);
  const vcSchemaList = ref([]);
  const totalCount = ref("");
  const basicFieldList = ref([]);
  const normalFieldList = ref([]);
  const VCSchemaDetail = ref([]);
  const { t } = useI18n();
  const $n = useNotify();
  const createDialogLoading = ref(false);
  const vcDataDialogLoading = ref(false);
  const tempDialogLoading = ref(false);
  const coverLoading = ref(false);
  const vcCoverInfo = ref([]);
  const fieldLoading = ref(false);
  const settingLoading = ref(false);
  const checkAPILoading = ref(false);
  const vcDataSource = ref("");
  const deleteLoading = ref(false);
  const stopIssuingLoading = ref(false);
  const vcSourceType = ref(0);
  const issuerDID = ref(false);
  const regularError = ref({});
  const ialTypeAllList = ref([]);
  const editServiceUrlLoading = ref(false);
  const currentEnv = ref("");

  const pagination = ref({
    sortBy: "crDatetime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const defaultFormState = {
    orgTwName: "",
    serialNo: "",
    name: "",
    startDate: "",
    endDate: ""
  };

  const filter = ref({ ...defaultFormState });

  function applySearch(searchFormData) {
    filter.value = { ...searchFormData };
  }

  function resetSearchForm() {
    filter.value = { ...defaultFormState };
  }

  async function getVCSchemaList(
    page = 1,
    options = {
      size: 10,
      sortBy: "crDatetime",
      descending: true,
      orgTwName: "",
      serialNo: "",
      name: "",
      startDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;

    const finalOptions = {
      ...options,
      orgTwName: options.orgTwName || filter.value.orgTwName,
      serialNo: options.serialNo || filter.value.serialNo,
      name: options.name || filter.value.name,
      startDate: options.startDate || filter.value.startDate,
      endDate: options.endDate || filter.value.endDate
    };

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "id,asc";

    const params = {
      sort: sortStr,
      page: page - 1,
      size: finalOptions.size || 2000
    };

    if (finalOptions.orgTwName) {
      params["businessId.contains"] = finalOptions.orgTwName;
    }
    if (finalOptions.serialNo) {
      params["serialNo.contains"] = finalOptions.serialNo;
    }
    if (finalOptions.name) {
      params["name.contains"] = finalOptions.name;
    }
    // 獲取當前時區偏移量（格式化為 +HH:00 或 -HH:00）
    const tzOffset = new Date().getTimezoneOffset();
    const tzString = `${tzOffset <= 0 ? "+" : "-"}${String(
      Math.abs(tzOffset / 60)
    ).padStart(2, "0")}:00`;

    if (finalOptions.startDate) {
      params[
        "crDatetime.greaterThan"
      ] = `${finalOptions.startDate}T00:00:00${tzString}`;
    }

    if (finalOptions.endDate) {
      params[
        "crDatetime.lessThan"
      ] = `${finalOptions.endDate}T23:59:59${tzString}`;
    }

    try {
      const response = await api.get("/api/vc-items", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      vcSourceType.value = response.data.vcSourceType;
      vcSchemaList.value = response.data.data;
      issuerDID.value = response.data.issuerDID;
      totalCount.value = response.headers["x-total-count"];

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: options.sortBy || "id",
        descending: options.descending || false,
        rowsPerPage: options.size
      };
    } catch (error) {
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      loading.value = false;
    }
  }

  // 獲取欄位列表
  async function getFieldList(type, visible = true, size = 2000) {
    fieldLoading.value = true;

    try {
      const response = await api.get(
        `/api/fields?type.equals=${type}&sort=id,desc&visible.equals=${visible}&size=${size}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      if (type === "BASIC") {
        basicFieldList.value = response.data;
      } else if (type === "NORMAL") {
        normalFieldList.value = response.data;
      }

      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
      return [];
    } finally {
      fieldLoading.value = false;
    }
  }

  // 創建VC Schema
  async function createVCSchema(data) {
    regularError.value = {};
    createDialogLoading.value = true;

    try {
      await api.post("/api/vc-items", data, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      createDialogLoading.value = false;
      $n.success(t("createSuccess"));

      return true;
    } catch (err) {
      console.log(err);

      const errorMessage = err.response?.data?.message || t("error.unknown");
      const prefix =
        errorMessage === "error.exists" ? t("vcSchema.table.serialNo") : "";
      $n.error(prefix + t(errorMessage));
    } finally {
      createDialogLoading.value = false;
    }
  }

  // 暫存VC Schema
  async function tempVCSchema(data) {
    regularError.value = {};
    tempDialogLoading.value = true;

    try {
      await api.post("/api/vc-items/temp", data, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      tempDialogLoading.value = false;
      $n.success(t("tempSuccess"));

      return true;
    } catch (err) {
      console.log(err);

      const errorMessage = err.response?.data?.message || t("error.unknown");
      const prefix =
        errorMessage === "error.exists" || errorMessage === "error.change"
          ? t("vcSchema.table.serialNo")
          : "";
      $n.error(prefix + t(errorMessage));
    } finally {
      tempDialogLoading.value = false;
    }
  }

  // 獲取 VCSchema 明細
  async function getVCSchemaDetail(id) {
    resetVCSchemaDetail();
    dialogLoading.value = true;
    try {
      const response = await api.get(
        `/api/vc-item-fields?vcItemId.equals=${id}&page=0&size=2000`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      VCSchemaDetail.value = response.data;

      return response.data;
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("error.unknown");
      $n.error(t(errorMessage));
      return null;
    } finally {
      dialogLoading.value = false;
    }
  }

  // 新增 VCData
  async function createVCData(params, type) {
    vcDataDialogLoading.value = true;

    try {
      const response = await api.post("/api/vc-item-data", params, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // new , regenerateQRCode
      if (type === "new") {
        $n.success(t("createSuccess"));
      } else if (type === "regenerateQRCode") {
        $n.success(t("regenerateQRCodeSuccess"));
      }

      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message;

      if (errorMessage) {
        $n.error(errorMessage);
        return;
      }

      const allErrorMsgList = [];
      if (error.response?.data?.detail) {
        for (const fieldsItem of params.fields) {
          regularError.value[fieldsItem.ename] = "";
        }

        const errorDetail = JSON.parse(error.response?.data?.detail);
        for (const item of errorDetail) {
          const invalidMsgList = [];
          item.invalid.forEach((invalidItem, index) => {
            if (allErrorMsgList.length === 0) {
              // 組建errorMsg, 重複的提示不出現
              allErrorMsgList.push(invalidItem);
            } else if (!allErrorMsgList.includes(invalidItem)) {
              allErrorMsgList.push(`, ${invalidItem}`);
            }

            // 若此欄位有2個以上的錯誤
            if (index != 0) {
              // 重複的提示不出現
              if (!invalidMsgList.includes(invalidItem)) {
                invalidMsgList.push(`, ${invalidItem}`);
              }
            } else {
              invalidMsgList.push(invalidItem);
            }
          });
          regularError.value[item.ename] = invalidMsgList.join("");
        }
      }
      $n.error(allErrorMsgList.join(""));
    } finally {
      vcDataDialogLoading.value = false;
    }
  }

  function resetVCSchemaDetail() {
    VCSchemaDetail.value = [];
  }

  async function removeVCSchema(row) {
    deleteLoading.value = true;

    const { id } = row;

    try {
      await api.delete(`/api/vc-items/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      $n.success(t("vcSchema.success.delete", { name: row.name }));
    } catch (error) {
      const errorMessage = error.response.data.message;
      if (errorMessage === "error.NonOwners") {
        $n.error(t(errorMessage) + t("vcSchema.error.delete"));
      } else {
        $n.error(t(errorMessage));
      }
    } finally {
      deleteLoading.value = false;
    }
  }

  async function getVCCover(id) {
    coverLoading.value = true;
    try {
      const response = await api.get(`/api/vc-items/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      vcCoverInfo.value = response.data;
    } catch (error) {
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    } finally {
      coverLoading.value = false;
    }
  }

  const settingUrl = async (props) => {
    settingLoading.value = true;
    try {
      await api.post(`/api/vc-items/setting`, props, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const { page, rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      $n.success(t("success.edit"));

      return true;
    } catch (error) {
      const errorMessage = error.response.data.message;

      if (errorMessage === "error.NonOwners") {
        $n.error(t(errorMessage) + t("vcSchema.error.edit"));
      } else {
        $n.error(t(errorMessage));
      }
    } finally {
      settingLoading.value = false;
    }
  };

  const checkAPI = async (params) => {
    checkAPILoading.value = true;

    try {
      const response = await api.post(`/api/vc-items/test/901i`, params, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message;
      if (errorMessage === "error.NonOwners") {
        $n.error(t(errorMessage) + t("vcSchema.error.test"));
      } else {
        $n.error(t(errorMessage));
      }
    } finally {
      checkAPILoading.value = false;
    }
  };

  const checkSerialNumber = async (vcNumber) => {
    try {
      const response = await api.post(`/api/vc-items/check/serial`, vcNumber, {
        headers: {
          "Content-Type": "text/plain",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      return response.data;
    } catch (err) {
      console.log(err);

      const errorMessage = err.response?.data?.message || t("error.unknown");
      const prefix =
        errorMessage === "error.exists" ? t("vcSchema.table.serialNo") : "";
      $n.error(prefix + t(errorMessage));
    }
  };

  const searchOrgInfo = async (orgId) => {
    try {
      const queryParams = new URLSearchParams({
        orgId: orgId,
        page: 0,
        size: 1
      });

      const response = await api.get(
        `/api/modadworg/search?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      vcDataSource.value = response.data.data[0].vcDataSource;
    } catch (error) {
      console.log(error);
    }
  };

  const editServiceUrl = async (row) => {
    editServiceUrlLoading.value = true;

    const { id, type, issuerServiceUrl } = row;

    try {
      await api.post(
        "/api/vc-items/issuerServiceUrl",
        {
          id: id,
          type: type,
          issuerServiceUrl: issuerServiceUrl
        },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const { rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      $n.success(t("vcSchema.success.edit", { name: row.name }));
      return true;
    } catch (error) {
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
      return null;
    } finally {
      editServiceUrlLoading.value = false;
    }
  };

  const getIALTypeIdOptionsUrl = `/api/vc-items/list/ial`;
  // 取得IAL等級列表
  async function getIALTypeList() {
    const requestData = {
      state: "1"
    };

    try {
      const response = await api.get(getIALTypeIdOptionsUrl, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const filteredData = response.data.data;
      ialTypeAllList.value = filteredData.map((item) => ({
        label: item.name,
        value: item.code
      }));
    } catch (error) {
      console.error("error:", error);
    }
  }

  // [API] 產製靜態QR Code
  async function getStaticQRCode(credentialType) {
    dialogLoading.value = true;

    try {
      const response = await api.get(`/api/vc-items/qrCode`, {
        params: { credentialType },
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      return response.data.data;
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("error.unknown");
      $n.error(t(errorMessage));

      return null;
    } finally {
      dialogLoading.value = false;
    }
  }

  // [API] 匯出靜態QR Code
  async function downloadStaticQRCode(base64) {
    dialogLoading.value = true;

    try {
      const response = await api.post(
        "/api/vc-items/download",
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
      const errorMessage = err.response?.data?.message || t("error.unknown");
      $n.error(t(errorMessage));

      return null;
    } finally {
      dialogLoading.value = false;
    }
  }

  // 停止發行
  async function stopIssuingVCSchema(row) {
    stopIssuingLoading.value = true;

    const { id } = row;

    try {
      await api.post(
        `/api/vc-items/stopIssuing`,
        { id: id },
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const { rowsPerPage, sortBy, descending } = pagination.value;
      const { orgTwName, serialNo, name, startDate, endDate } = filter.value;

      await getVCSchemaList(1, {
        size: rowsPerPage,
        sortBy,
        descending,
        orgTwName,
        serialNo,
        name,
        startDate,
        endDate
      });

      $n.success(t("vcSchema.success.stopIssuing", { name: row.name }));
    } catch (error) {
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    } finally {
      stopIssuingLoading.value = false;
    }
  }

  async function getVersionInfo() {
    try {
      const response = await api.get("/api/info/version");
      if (response.data && Object.keys(response.data).length > 0) {
        const responseData = response.data.data;
        currentEnv.value = responseData.env || "";
      }
    } catch (error) {
      console.error("Failed to load management info: ", error);
    } finally {
    }
  }

  function resetVCcover() {
    vcCoverInfo.value = [];
  }

  function resetVcShemaList() {
    vcSchemaList.value = [];
  }

  function resetPagination() {
    pagination.value = {
      sortBy: "crDatetime",
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
      rowsPerPageOptions: [10, 20, 50, 0]
    };
  }

  // 最後返回所有方法和狀態
  return {
    getVCSchemaList,
    vcSchemaList,
    pagination,
    loading,
    getFieldList,
    basicFieldList,
    normalFieldList,
    createVCSchema,
    tempVCSchema,
    getVCSchemaDetail,
    VCSchemaDetail,
    createVCData,
    dialogLoading,
    resetVCSchemaDetail,
    createDialogLoading,
    vcDataDialogLoading,
    tempDialogLoading,
    removeVCSchema,
    stopIssuingVCSchema,
    getVCCover,
    coverLoading,
    vcCoverInfo,
    resetVCcover,
    resetPagination,
    settingUrl,
    settingLoading,
    checkAPI,
    checkAPILoading,
    vcDataSource,
    deleteLoading,
    stopIssuingLoading,
    resetVcShemaList,
    filter,
    applySearch,
    resetSearchForm,
    defaultFormState,
    searchOrgInfo,
    vcSourceType,
    issuerDID,
    regularError,
    ialTypeAllList,
    getIALTypeList,
    getStaticQRCode,
    downloadStaticQRCode,
    checkSerialNumber,
    editServiceUrl,
    editServiceUrlLoading,
    getVersionInfo,
    currentEnv
  };
});
