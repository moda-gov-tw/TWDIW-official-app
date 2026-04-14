import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { i18n } from "src/boot/i18n";
import { getToken } from "src/boot/auth.js";

const jwtToken = getToken();

export const useScheduleStore = defineStore("schedule", () => {
  const $n = useNotify();
  const { t } = i18n.global;

  // state
  const loading = ref(false);
  const detailLoading = ref(false);
  const scheduleList = ref([]);
  const scheduleDetail = ref({});
  const scheduleVCItemDataTable = ref([]);

  const pagination = ref({
    sortBy: "id",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const detailPagination = ref({
    sortBy: "id",
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

  // 列表
  async function getScheduleList(
    page = 1,
    size = 10,
    sortBy = "id",
    descending = true
  ) {
    loading.value = true;
    try {
      const response = await api.get("/api/schedule", {
        params: {
          page: page - 1, // API 通常使用 0 為起始頁碼
          size: size,
          sort: `${sortBy},${descending ? "desc" : "asc"}`
        },
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      scheduleList.value = response.data;
      pagination.value.rowsNumber =
        parseInt(response.headers["x-total-count"]) || 0;
      pagination.value.page = page;
      pagination.value.rowsPerPage = size;
    } catch (error) {
      console.error(
        "Error fetching schedule list:",
        error.response.data.message
      );
      const errorMessage = error.response.data.message;
      $n.error(errorMessage);
    } finally {
      loading.value = false;
    }
  }

  // 詳情(上方)
  async function getListClearDateInfo(id) {
    scheduleDetail.value = {};
    try {
      const response = await api.get(`/api/schedule/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      scheduleDetail.value = response.data;
    } catch (error) {
      console.error(
        "Error fetching schedule list:",
        error.response.data.message
      );
      const errorMessage = error.response.data.message;
      $n.error(errorMessage);
    }
  }

  // 詳細(下方)
  async function getScheduleVCItemDataTable(
    id,
    page = 1,
    options = { descending: true }
  ) {
    detailLoading.value = true;
    try {
      const response = await api.get(`/api/schedule/${id}/vcItemData`, {
        params: {
          page: page - 1,
          size: options.size !== undefined ? options.size : 10,
          sort: `${options.sortBy || "id"},${
            options.descending ? "desc" : "asc"
          }`
        },
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      scheduleVCItemDataTable.value = response.data;
      detailPagination.value = {
        ...detailPagination.value,
        page: page,
        rowsPerPage: options.size === 0 ? 0 : options.size || 10,
        rowsNumber: parseInt(response.headers["x-total-count"]) || 0,
        sortBy: options.sortBy || "id",
        descending: options.descending || false
      };
    } catch (error) {
      console.error(
        "Error fetching schedule list:",
        error.response.data.message
      );
      const errorMessage = error.response.data.message;
      $n.error(errorMessage);
    } finally {
      detailLoading.value = false;
    }
  }

  // 新增
  async function createSchedule(data) {
    loading.value = true;
    try {
      const response = await api.post(`/api/schedule`, data, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      $n.success(t("success.add"));
      getScheduleList(1);
    } catch (error) {
      console.error(
        "Error fetching schedule list:",
        error.response.data.message
      );
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    }
  }

  // 刪除
  async function deleteSchedule(id) {
    loading.value = true;
    try {
      await api.delete(`/api/schedule/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      $n.success(t("success.delete"));

      getScheduleList(
        pagination.value.page,
        pagination.value.rowsPerPage,
        pagination.value.sortBy,
        pagination.value.descending
      );
    } catch (error) {
      console.error(
        "Error fetching schedule list:",
        error.response.data.message
      );
      const errorMessage = error.response.data.message;
      $n.error(t(errorMessage));
    }
  }

  function reset() {
    scheduleDetail.value = {};
    scheduleVCItemDataTable.value = [];
  }

  function resetScheduleList() {
    scheduleList.value = [];
  }

  return {
    loading,
    pagination,
    currentFilter,
    scheduleDetail,
    scheduleList,
    scheduleVCItemDataTable,
    detailLoading,
    detailPagination,
    getListClearDateInfo,
    getScheduleList,
    getScheduleVCItemDataTable,
    reset,
    createSchedule,
    deleteSchedule,
    resetScheduleList
  };
});
