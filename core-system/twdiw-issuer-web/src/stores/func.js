import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { ref } from "vue";
import { getToken } from "src/boot/auth.js";
import { format } from "src/utils/dateFormat";
import { HttpStatusCode } from "axios";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useFuncStore = defineStore("func", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = useI18n();
  const { yyyyMMddHHmmss } = format();

  const isOpenDetail = ref(false);
  const funcs = ref([]);
  const loading = ref(false);
  const selected = ref(null);

  const pagination = ref({
    sortBy: "resId",
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    resId: "",
    resName: "",
    typeId: "",
    state: ""
  });

  //載入所有資料
  async function getFuncs(
    page = 1,
    options = {
      size: 10,
      sortBy: "resId",
      descending: false,
      resId: "",
      resName: "",
      typeId: "",
      state: ""
    }
  ) {
    loading.value = true;

    const finalOptions = {
      ...options,
      resId: options.resId || currentFilter.value.resId,
      resName: options.resName || currentFilter.value.resName,
      typeId: options.typeId || currentFilter.value.typeId,
      state: options.state || currentFilter.value.state
    };

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "createTime,desc";

    // 將參數組裝成 URL 查詢參數
    const queryParams = new URLSearchParams({
      resId: finalOptions.resId || "",
      resName: finalOptions.resName || "",
      typeId: finalOptions.typeId || "",
      state: finalOptions.state || "",
      page: page - 1,
      size: finalOptions.size || 2000,
      sort: sortStr
    });

    try {
      const response = await api.get(
        `/api/modadw331w/search?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const responseData = response.data.data || [];
      funcs.value = responseData.map((item) => {
        return {
          ...item,
          createTime: yyyyMMddHHmmss(item.createTime)
        };
      });

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: options.sortBy || "createTime",
        descending: options.descending || false,
        rowsPerPage: options.size
      };

      return response.data.data;
    } catch (error) {
      if (error.status === HttpStatusCode.Forbidden) {
        $n.error(t("func.error.permissionDenied.search"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
    } finally {
      loading.value = false;
    }
  }

  //當下選擇的功能詳情資料
  function selectFunc(row) {
    selected.value = row;
  }

  //更新啟停狀態
  async function updateState(row) {
    loading.value = true;
    try {
      const url = "/api/modadw331w/updateState";
      const requestData = {
        resId: row.resId
      };

      const response = await api.post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      if (
        response &&
        response.data &&
        response.data.code === "0" &&
        response.data.data.resDTO
      ) {
        await getFuncs();
        return { isUpdateStateOK: true };
      } else {
        if (response.data.msg) {
          $n.error(response.data.msg);
        }
        loading.value = false;
        return { isUpdateStateOK: false };
      }
    } catch (error) {
      loading.value = false;
      console.error("error:", error);
      return { isUpdateStateOK: false };
    }
  }

  return {
    funcs,
    getFuncs,
    selectFunc,
    updateState,
    selected,
    pagination,
    currentFilter,
    isOpenDetail,
    loading
  };
});
