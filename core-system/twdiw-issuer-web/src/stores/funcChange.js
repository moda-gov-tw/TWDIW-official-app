import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { format } from "src/utils/dateFormat";
import { HttpStatusCode } from "axios";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useFuncChangeStore = defineStore("funcChange", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = useI18n();
  const { yyyyMMddHHmmss } = format();

  const isOpenDetail = ref(false);
  const loading = ref(false);
  const funcChanges = ref([]);
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
    resId: "",
    beginDate: "",
    endDate: ""
  });

  async function getFuncChanges(
    page = 1,
    options = {
      size: 10,
      sortBy: "logTime",
      descending: true,
      resId: "",
      beginDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;
    const finalOptions = {
      ...options,
      resId: options.resId || currentFilter.value.resId,
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
      resId: finalOptions.resId || "",
      beginDate: finalOptions.beginDate || "",
      endDate: finalOptions.endDate || "",
      page: page - 1,
      size: finalOptions.size || 2000,
      sort: sortStr
    });

    try {
      const response = await api.get(
        `/api/modadw332w/search?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const responseData = response.data.data || [];
      funcChanges.value = responseData.map((item) => {
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
        $n.error(t("func.error.permissionDenied.changeHistory"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
    } finally {
      loading.value = false;
    }
  }

  function selectFuncChange(row) {
    selected.value = row;
  }

  return {
    funcChanges,
    getFuncChanges,
    selectFuncChange,
    selected,
    pagination,
    currentFilter,
    isOpenDetail,
    loading
  };
});
