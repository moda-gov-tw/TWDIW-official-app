import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { format } from "src/utils/dateFormat";
import { HttpStatusCode } from "axios";
import { i18n } from "src/boot/i18n";

const jwtToken = getToken();

export const useRoleChangeStore = defineStore("roleChange", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = i18n.global;
  const { yyyyMMddHHmmss } = format();

  const isOpenDetail = ref(false);
  const loading = ref(false);
  const roleChanges = ref([]);
  const selected = ref(null);

  const pagination = ref({
    sortBy: "logTime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    roleId: "",
    logType: "",
    beginDate: "",
    endDate: ""
  });

  async function getRoleChanges(
    page = 1,
    options = {
      size: 10,
      sortBy: "logTime",
      descending: true,
      userId: "",
      logType: "",
      beginDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;
    const finalOptions = {
      ...options,
      roleId: options.roleId || currentFilter.value.roleId,
      actionType: options.logType || currentFilter.value.logType,
      beginDate: options.beginDate || currentFilter.value.beginDate,
      endDate: options.endDate || currentFilter.value.endDate
    };

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "logTime,desc";

    // 將參數組裝成 URL 查詢參數
    const queryParams = new URLSearchParams({
      roleId: finalOptions.roleId || "",
      logType: finalOptions.logType || "",
      beginDate: finalOptions.beginDate || "",
      endDate: finalOptions.endDate || "",
      page: page - 1,
      size: finalOptions.size || 2000,
      sort: sortStr
    });

    try {
      const response = await api.get(
        `/api/modadw322w/search?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const responseData = response.data.data || [];
      roleChanges.value = responseData.map((item) => {
        return {
          ...item,
          logTime: yyyyMMddHHmmss(item.logTime)
        };
      });

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: options.sortBy || "logTime",
        descending: options.descending || false,
        rowsPerPage: options.size
      };

      return response.data.data;
    } catch (error) {
      if (error.status === HttpStatusCode.Forbidden) {
        $n.error(t("roleChange.error.noPermission"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
    } finally {
      loading.value = false;
    }
  }

  function selectRoleChange(row) {
    selected.value = row;
  }

  return {
    roleChanges,
    getRoleChanges,
    selectRoleChange,
    selected,
    pagination,
    currentFilter,
    isOpenDetail,
    loading
  };
});
