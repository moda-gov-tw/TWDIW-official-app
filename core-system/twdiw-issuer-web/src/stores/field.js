import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useFieldStore = defineStore("field", () => {
  // State 狀態管理
  const loading = ref(false);
  const fieldList = ref([]);
  const totalCount = ref("");
  const { t } = useI18n();
  const $n = useNotify();
  const dialogLoading = ref(false);
  const fieldUsedMessage = ref("");
  const regularExpressionsList = ref([]);
  const changeLoading = ref(false);
  const deleteLoading = ref(false);

  const defaultFormState = {
    cname: "",
    ename: "",
    visible: null
  };

  const pagination = ref({
    sortBy: "id",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  // 搜尋相關的狀態
  const filter = ref({ ...defaultFormState });

  // 搜尋
  function applySearch(searchFormData) {
    filter.value = {
      ...searchFormData,
      visible: searchFormData.visible?.value ?? searchFormData.visible
    };
  }

  function resetSearchForm() {
    filter.value = { ...defaultFormState };
  }

  // 獲取基本/常用欄位清單
  async function getFieldList(type, page = 1, options = {}) {
    const defaultOptions = {
      size: 10,
      sortBy: "id",
      descending: false,
      cname: "",
      ename: "",
      visible: null
    };

    const finalOptions = { ...defaultOptions, ...options };

    loading.value = true;

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "id,asc";

    const params = {
      "type.equals": type,
      sort: sortStr,
      page: page - 1,
      size: finalOptions.size || 2000
    };

    if (finalOptions.cname) {
      params["cname.contains"] = finalOptions.cname;
    }
    if (finalOptions.ename) {
      params["ename.contains"] = finalOptions.ename;
    }

    if (finalOptions.visible !== null && finalOptions.visible !== undefined) {
      params["visible.equals"] = finalOptions.visible;
    }

    try {
      const response = await api.get("/api/fields", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      fieldList.value = response.data;
      totalCount.value = response.headers["x-total-count"];

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: finalOptions.sortBy || "id",
        descending: finalOptions.descending || false,
        rowsPerPage: finalOptions.size
      };

      return response.data;
    } catch (error) {
      handleError(error);
      return null;
    } finally {
      loading.value = false;
    }
  }

  // 修改顯示狀態

  async function changeState(row, visible, type) {
    changeLoading.value = true;

    const requestData = {
      ...row,
      visible
    };

    try {
      await api.put(`/api/fields/${row.id}`, requestData, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
          "Content-Type": "application/json"
        }
      });

      await getFieldList(type, pagination.value.page, {
        size: pagination.value.rowsPerPage,
        sortBy: pagination.value.sortBy,
        descending: pagination.value.descending, // 加入搜尋條件

        cname: filter.value.cname,
        ename: filter.value.ename,
        visible: filter.value.visible
      });

      $n.success(
        row.visible
          ? t("normalFields.success.show", { name: row.cname })
          : t("normalFields.success.hide", { name: row.cname })
      );
      // return res.data; // 返回成功訊息
    } catch (error) {
      handleError(error);
      return null;
    } finally {
      changeLoading.value = false;
    }
  }

  // 刪除欄位

  async function deleteField(row) {
    deleteLoading.value = true;
    const id = row.id;

    try {
      await api.delete(`/api/fields/${id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      $n.success(t("normalFields.success.delete", { name: row.cname }));

      await getFieldList("NORMAL", pagination.value.page, {
        size: pagination.value.rowsPerPage,
        sortBy: pagination.value.sortBy,
        descending: pagination.value.descending,
        cname: filter.value.cname,
        ename: filter.value.ename,
        visible: filter.value.visible
      });
      return true;
    } catch (error) {
      handleError(error);
    } finally {
      deleteLoading.value = false;
    }
  }

  // 增加欄位
  async function addField(reqArray) {
    const requestData = reqArray.map((item) => ({
      ...item,
      type: "NORMAL",
      visible: true
    }));

    dialogLoading.value = true;

    try {
      await api.post("/api/fields", requestData, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
          "Content-Type": "application/json"
        }
      });

      await getFieldList("NORMAL", 1, {
        size: pagination.value.rowsPerPage,
        sortBy: pagination.value.sortBy,
        descending: pagination.value.descending,
        cname: filter.value.cname,
        ename: filter.value.ename,
        visible: filter.value.visible
      });

      $n.success(t("createSuccess"));
      return true; // 成功時返回 true
    } catch (err) {
      const errorMessage = err.response?.data?.message || t("error.unknown");
      const prefix = errorMessage === "error.exists" ? t("fields.ename") : "";
      $n.error(prefix + t(errorMessage));
      return false;
    } finally {
      dialogLoading.value = false;
    }
  }

  // 正則表

  async function regularExpression() {
    try {
      const response = await api.get(
        `/api/regular-expressions?sort=id,desc&size=2000`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json"
          }
        }
      );

      regularExpressionsList.value = response.data;
    } catch (error) {
      handleError(error);
      return null;
    }
  }

  // 重置分頁設定
  function resetPagination() {
    pagination.value = {
      sortBy: "id",
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
      rowsPerPageOptions: [10, 20, 50, 0]
    };
  }

  function handleError(error) {
    const errorMessage = error.response?.data?.message || t("error.unknown");
    $n.error(t(errorMessage));
  }

  // 重置欄位列表
  function resetFieldList() {
    fieldList.value = [];
  }

  async function redirect() {
    try {
      // 使用 axios 原始功能避免自動重定向
      const res = await api.post(`/api/vc-item-data/redirect`);
      return false;
    } catch (error) {
      // 檢查錯誤回應是否包含重定向信息
      if (error.response) {
        if (error.response.headers && error.response.headers.location) {
          const redirectUrl = error.response.headers.location;
          console.log(t("redirect.fromHeader"), redirectUrl);
          window.open(redirectUrl, "_blank", "noopener,noreferrer");
          return true;
        } else if (error.response.data && error.response.data.redirectUrl) {
          const redirectUrl = error.response.data.redirectUrl;
          console.log(t("redirect.fromBody"), redirectUrl);
          window.open(redirectUrl, "_blank", "noopener,noreferrer");
          return true;
        }
      }

      console.error(`${t("redirect.failed")}：${error}`);
      return false;
    }
  }

  return {
    fieldList,
    getFieldList,
    loading,
    changeState,
    deleteField,
    addField,
    pagination,
    dialogLoading,
    fieldUsedMessage,
    regularExpression,
    regularExpressionsList,
    resetPagination,
    changeLoading,
    resetFieldList,
    deleteLoading,
    defaultFormState,
    filter,
    applySearch,
    resetSearchForm,
    redirect
  };
});
