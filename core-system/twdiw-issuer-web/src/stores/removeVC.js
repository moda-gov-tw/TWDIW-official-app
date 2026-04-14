import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useRemoveVCStore = defineStore("removeVC", () => {
  const loading = ref(false);
  const removeLoading = ref(false);
  const totalCount = ref("");
  const vcDataList = ref([]);
  const { t } = useI18n();
  const $n = useNotify();
  const removeDetailList = ref({
    content: [],
    statusLogList: []
  });
  const selected = ref([]);
  const detailLoading = ref(false);
  const orgList = ref([]);
  const orgVcItemList = ref([]);
  const loadingState = ref(false);

  const tableRows = computed(() => {
    return removeDetailList.value?.content || [];
  });

  const historyTableRows = computed(() => {
    return removeDetailList.value?.statusLogList || [];
  });

  const historyPagination = ref({
    sortBy: "logDatetime",
    descending: true,
    page: 1,
    rowsPerPage: 10
  });

  const pagination = ref({
    sortBy: "issuanceDate",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const defaultFormState = {
    orgId: "",
    vcSerialNo: "",
    startDate: "",
    endDate: "",
    state: null,
    dataTag: "",
    transactionId: ""
  };

  const filter = ref({ ...defaultFormState });

  function applySearch(searchFormData) {
    filter.value = {
      ...searchFormData,
      orgId: searchFormData.orgId?.orgId,
      state: searchFormData.state?.value,
      vcSerialNo:
        searchFormData.vcSerialNo?.vcItemSerialNo || searchFormData.vcSerialNo,
      dataTag: searchFormData.dataTag,
      transactionId: searchFormData.transactionId
    };
  }

  function resetSearchForm() {
    filter.value = { ...defaultFormState };
    orgVcItemList.value = [];
    getOrgList();
  }

  // 獲取列表
  async function getVCDataList(
    page = 1,
    options = {
      size: 10,
      sortBy: "",
      descending: true,
      vcSerialNo: "",
      name: "",
      id: "",
      startDate: "",
      endDate: "",
      state: "",
      content: "",
      expiredDate: "",
      dataTag: "",
      transactionId: ""
    }
  ) {
    loading.value = true;

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortDirection = options.descending ? "desc" : "asc";

    const params = {
      sortType: sortDirection,
      page: page - 1,
      size: options.size || 2000
    };

    if (options.vcSerialNo) {
      params["vcSerialNo"] = `${options.vcSerialNo}`;
    }

    if (options.orgId) {
      params["orgId"] = options.orgId;
    }

    // 獲取當前時區偏移量（格式化為 +HH:00 或 -HH:00）
    const tzOffset = new Date().getTimezoneOffset();
    const tzString = `${tzOffset <= 0 ? "+" : "-"}${String(
      Math.abs(tzOffset / 60)
    ).padStart(2, "0")}:00`;

    if (options.startDate) {
      params["issuanceDateStart"] = `${options.startDate}T00:00:00${tzString}`;
    }

    if (options.endDate) {
      params["issuanceDateEnd"] = `${options.endDate}T23:59:59${tzString}`;
    }

    if (options.state === 0 || options.state === 1 || options.state === 2) {
      params["credentialStatus"] = options.state;
    }

    if (options.dataTag) {
      params["dataTag"] = options.dataTag;
    }

    if (options.transactionId) {
      params["transactionId"] = options.transactionId;
    }

    try {
      const response = await api.get("/api/vc-item-data", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const startIndex = (page - 1) * options.size;
      vcDataList.value = response.data.content.map((item, index) => ({
        ...item,
        originalIndex: startIndex + index + 1 // 添加固定的序号
      }));
      const pageInfo = response.data.page;

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: pageInfo.totalElements || 0,
        sortBy: options.sortBy || "id",
        descending: options.descending,
        rowsPerPage: options.size === 2000 ? "全部" : options.size,
        totalPages: pageInfo.totalPages
      };

      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
      vcDataList.value = [];
      return [];
    } finally {
      loading.value = false;
    }
  }

  async function removeVC(params) {
    removeLoading.value = true;

    try {
      const response = await api.patch("/api/vc-item-data", params, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // 只有當 response.data 是空陣列時才重新獲取列表
      if (Array.isArray(response.data) && response.data.length === 0) {
        selected.value = [];

        const { page, rowsPerPage, sortBy, descending } = pagination.value;
        const { vcSerialNo, name, startDate, endDate, valid } = filter.value;

        await getVCDataList(page, {
          size: rowsPerPage,
          sortBy,
          descending,
          vcSerialNo,
          name,
          startDate,
          endDate,
          valid
        });
      }

      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
      return [];
    } finally {
      removeLoading.value = false;
    }
  }

  function resetList() {
    vcDataList.value = [];
  }

  // 有detail 的  撤銷VC的詳細資料
  async function removeVCDetail(id) {
    resetRemoveDetailList();
    detailLoading.value = true;
    try {
      const response = await api.get(`/api/vc-item-data/detail/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      removeDetailList.value = response.data;

      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      detailLoading.value = false;
    }
  }

  // 沒有detail 的  輪循 VC 的api
  async function checkVCDetail(id) {
    detailLoading.value = true;
    try {
      const response = await api.get(`/api/vc-item-data/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      return response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);

      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      detailLoading.value = false;
    }
  }

  function resetRemoveDetailList() {
    removeDetailList.value = {};
  }

  // 獲取組織列表
  async function getOrgList() {
    try {
      const response = await api.get("/api/modadworg/list", {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      orgList.value = response.data.data;
    } catch (error) {
      console.error("Error fetching field list:", error);
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
      return [];
    }
  }

  // 獲取 vcItemsList 列表 getOrgVcItemsList
  async function getOrgVcItemsList(orgId) {
    loadingState.value = true;
    try {
      const response = await api.get(`/api/vc-items/list/${orgId}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      orgVcItemList.value = response.data;
    } catch (error) {
      console.error("Error fetching field list:", error);
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
      return [];
    } finally {
      loadingState.value = false;
    }
  }

  const getStatusColor = (status) => {
    if (status === "0") return "accent";
    if (status === "1") return "orange-7";
    if (status === "2") return "grey-3";
    if (status === "3") return "primary";
    return "transparent";
  };

  const getStatusLabel = (status) => {
    if (status === "0") return t("remove.status.normal");
    if (status === "1") return t("remove.status.inactive");
    if (status === "2") return t("remove.status.revoked");
    return "";
  };

  return {
    getVCDataList,
    vcDataList,
    totalCount,
    loading,
    pagination,
    removeVC,
    removeLoading,
    removeVCDetail,
    removeDetailList,
    resetRemoveDetailList,
    detailLoading,
    checkVCDetail,
    getOrgList,
    orgList,
    getOrgVcItemsList,
    orgVcItemList,
    tableRows,
    historyTableRows,
    historyPagination,
    loadingState,
    resetList,
    filter,
    applySearch,
    resetSearchForm,
    getStatusColor,
    getStatusLabel
  };
});
