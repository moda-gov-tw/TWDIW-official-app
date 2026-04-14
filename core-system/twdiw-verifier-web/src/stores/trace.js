import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { ref } from "vue";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { format } from "src/utils/dateFormat";
import { HttpStatusCode } from "axios";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useTraceStore = defineStore("trace", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = useI18n();
  const { yyyyMMddHHmmss } = format();

  const isOpenDetail = ref(false);
  const loading = ref(false);
  const traces = ref([]);
  const selected = ref(null);

  const pagination = ref({
    sortBy: "timestamp",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    source: "",
    jhiFrom: "",
    uri: "",
    statusCode: "",
    beginDate: "",
    endDate: ""
  });

  async function getTraces(
    page = 1,
    options = {
      size: 10,
      sortBy: "timestamp",
      descending: true,
      source: "",
      jhiFrom: "",
      uri: "",
      statusCode: "",
      beginDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;
    const finalOptions = {
      ...options,
      source: options.source || currentFilter.value.source,
      jhiFrom: options.jhiFrom || currentFilter.value.jhiFrom,
      uri: options.uri || currentFilter.value.uri,
      statusCode: options.statusCode || currentFilter.value.statusCode,
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
      source: finalOptions.source || "",
      jhiFrom: finalOptions.jhiFrom || "",
      uri: finalOptions.uri || "",
      statusCode: finalOptions.statusCode || "",
      beginDate: finalOptions.beginDate || "",
      endDate: finalOptions.endDate || "",
      page: page - 1,
      size: finalOptions.size || 2000,
      sort: sortStr
    });

    try {
      const response = await api.get(
        `/api/modadw341w?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );
      const responseData = response.data.data || [];
      traces.value = responseData.map((item) => {
        return {
          ...item,
          timestamp: yyyyMMddHHmmss(item.timestamp)
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
        $n.error(t("trace.error.permissionDenied"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
    } finally {
      loading.value = false;
    }
  }

  function selectTrace(row) {
    selected.value = row;
  }

  return {
    traces,
    getTraces,
    selectTrace,
    selected,
    pagination,
    currentFilter,
    isOpenDetail,
    loading
  };
});
