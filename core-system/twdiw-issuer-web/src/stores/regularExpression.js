import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { getToken } from "src/boot/auth.js";
import { useI18n } from "vue-i18n";

const jwtToken = getToken();

export const useRegularExpressionStore = defineStore(
  "regularExpression",
  () => {
    const loading = ref(false);
    const regularExpressionList = ref([]);
    const totalCount = ref("");
    const $n = useNotify();
    const { t } = useI18n();
    const dialogLoading = ref(false);
    const deleteLoading = ref(false);

    const pagination = ref({
      sortBy: "id",
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
      rowsPerPageOptions: [10, 20, 50, 0]
    });

    const defaultFormState = {
      name: "",
      description: "",
      type: null
    };

    // 2. 搜尋相關的狀態
    const filter = ref({ ...defaultFormState });

    // 3. 搜尋相關方法
    function applySearch(searchFormData) {
      filter.value = {
        ...searchFormData,
        type: searchFormData.type?.value
      };
    }

    function resetSearchForm() {
      filter.value = { ...defaultFormState };
    }

    async function getRegularExpressionList(
      page = 1,
      size = 10,
      sortBy = "id",
      descending = true,
      options = {}
    ) {
      const defaultOptions = {
        size,
        sortBy,
        descending,
        description: options.description,
        name: options.name,
        type: options.type
      };

      const finalOptions = { ...defaultOptions };

      loading.value = true;

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

      if (finalOptions.name) {
        params["name.contains"] = finalOptions.name;
      }
      if (finalOptions.description) {
        params["description.contains"] = finalOptions.description;
      }

      if (finalOptions.type) {
        params["type.equals"] = finalOptions.type;
      }

      try {
        const response = await api.get("/api/regular-expressions", {
          params,
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });

        regularExpressionList.value = response.data;
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

    // 新增 & 修改
    async function createEditRegularExpression(type, obj) {
      dialogLoading.value = true;

      const url =
        type === "edit"
          ? `/api/regular-expressions/${obj.id}`
          : "/api/regular-expressions";
      try {
        const method = type === "edit" ? api.put : api.post;
        await method(url, obj, {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });

        const page = type === "edit" ? pagination.value.page : 1;

        await getRegularExpressionList(
          page,
          pagination.value.rowsPerPage,
          pagination.value.sortBy,
          pagination.value.descending,
          filter.value
        );
        const successMessage =
          type === "edit" ? t("success.edit") : t("success.add");
        $n.success(successMessage);
        return true;
      } catch (error) {
        handleError(error);
        return false; // 失敗時返回 false
      } finally {
        dialogLoading.value = false;
      }
    }

    // 刪除
    async function deleteRegularExpression(row) {
      deleteLoading.value = true;
      try {
        await api.delete(`/api/regular-expressions/${row.id}`, {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });
        $n.success(t("regularExpression.success.delete", { name: row.name }));

        await getRegularExpressionList(
          pagination.value.page,
          pagination.value.rowsPerPage,
          pagination.value.sortBy,
          pagination.value.descending,
          filter.value
        );

        return true;
      } catch (error) {
        handleError(error);
      } finally {
        deleteLoading.value = false;
      }
    }

    function resetRegularExpressionList() {
      regularExpressionList.value = [];
    }

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

    return {
      loading,
      regularExpressionList,
      totalCount,
      pagination,
      dialogLoading,
      getRegularExpressionList,
      deleteRegularExpression,
      createEditRegularExpression,
      resetPagination,
      deleteLoading,
      resetRegularExpressionList,
      filter,
      applySearch,
      resetSearchForm
    };
  }
);
