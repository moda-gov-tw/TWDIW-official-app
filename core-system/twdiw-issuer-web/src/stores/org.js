import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { ref } from "vue";
import { getToken } from "src/boot/auth.js";
import { format } from "src/utils/dateFormat";
import { HttpStatusCode } from "axios";
import { i18n } from "src/boot/i18n";

const jwtToken = getToken();

export const useOrgStore = defineStore("org", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = i18n.global;
  const { yyyyMMddHHmmss } = format();

  const orgList = ref([]);
  const dialogLoading = ref(false);
  const loading = ref(false);
  const totalCount = ref("");
  const orgDetail = ref([]);
  const orgCreate = ref([
    { name: "orgId", value: "" },
    { name: "orgTwName", value: "" },
    { name: "orgEnName", value: "" }
  ]);
  const orgEdit = ref([
    { name: "orgId", value: "" },
    { name: "orgTwName", value: "" },
    { name: "orgEnName", value: "" },
    { name: "vcDataSource", value: "" }
  ]);

  const pagination = ref({
    sortBy: "createTime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    orgId: "",
    orgTwName: "",
    orgEnName: "",
    startDate: "",
    endDate: ""
  });

  async function getOrgList(
    page = 1,
    options = {
      size: 10,
      sortBy: "createTime",
      descending: true,
      orgId: "",
      orgTwName: "",
      orgEnName: "",
      startDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;

    const finalOptions = {
      ...options,
      orgId: options.orgId || currentFilter.value.orgId,
      orgTwName: options.orgTwName || currentFilter.value.orgTwName,
      orgEnName: options.orgEnName || currentFilter.value.orgEnName,
      startDate: options.startDate || currentFilter.value.startDate,
      endDate: options.endDate || currentFilter.value.endDate
    };

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "createTime,desc";

    // 將參數組裝成 URL 查詢參數
    const queryParams = new URLSearchParams({
      orgId: finalOptions.orgId || "",
      orgTwName: finalOptions.orgTwName || "",
      orgEnName: finalOptions.orgEnName || "",
      createTimeFrom: finalOptions.startDate || "",
      createTimeTo: finalOptions.endDate || "",
      page: page - 1,
      size: finalOptions.size || 2000,
      sort: sortStr
    });
    try {
      const response = await api.get(
        `/api/modadworg/search?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      const responseData = response.data.data || [];
      orgList.value = responseData.map((item) => {
        return {
          ...item,
          createTime: yyyyMMddHHmmss(item.createTime),
          updateTime: yyyyMMddHHmmss(item.updateTime)
        };
      });
      totalCount.value =
        response.headers["x-total-count"] || responseData.length;

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: parseInt(response.headers["x-total-count"] || 0),
        sortBy: options.sortBy || "orgId",
        descending: options.descending || false,
        rowsPerPage: options.size
      };

      return response.data.data;
    } catch (error) {
      if (error.status === HttpStatusCode.Forbidden) {
        $n.error(t("org.error.permissionDenied"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
    } finally {
      loading.value = false;
    }
  }

  async function getOrgDetail(orgId) {
    dialogLoading.value = true;
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

      const responseOrg = response.data.data?.[0];
      if (!responseOrg) {
        $n.error(t("noResult"));
        return null;
      }

      const fieldMapping = {
        orgId: t("org.table.orgId"),
        orgTwName: t("org.table.orgTwName"),
        orgEnName: t("org.table.orgEnName"),
        createTime: t("org.table.createTime"),
        updateTime: t("org.table.updateTime"),
        metadata: "metadata",
        vcDataSource: t("org.table.vcDataSource"),
        logo: t("org.logo"),
        logoUrl: t("org.logoUrl")
      };
      const fieldOrder = [
        "orgId",
        "orgTwName",
        "orgEnName",
        "createTime",
        "updateTime",
        "vcDataSource",
        "metadata",
        "logo",
        "logoUrl"
      ];
      orgDetail.value = Object.entries(responseOrg)
        .filter(([key]) => fieldMapping[key])
        .sort(
          ([keyA], [keyB]) =>
            fieldOrder.indexOf(keyA) - fieldOrder.indexOf(keyB)
        )
        .map(([key, value]) => {
          let formattedValue;

          if (key === "createTime" || key === "updateTime") {
            formattedValue = yyyyMMddHHmmss(value);
          } else if (key === "vcDataSource") {
            if (value === 501 || value === 901) {
              formattedValue = t(`org.select.${value}`);
            }
          } else {
            formattedValue = value;
          }

          return {
            name: fieldMapping[key] || key,
            value: formattedValue
          };
        });

      return responseOrg;
    } catch (err) {
      if (err.status === HttpStatusCode.Forbidden) {
        $n.error(t("org.error.permissionDenied"));
      } else {
        const errorMessage = err.response?.data?.message || t("error.unknown");
        $n.error(errorMessage);
      }
      return null;
    } finally {
      dialogLoading.value = false;
    }
  }

  function resetOrgDetail() {
    orgDetail.value = [];
  }

  function resetOrgCreate() {
    orgCreate.value = [
      { name: "orgId", value: "" },
      { name: "orgTwName", value: "" },
      { name: "orgEnName", value: "" }
    ];
  }

  //新增組織
  async function createOrg(org) {
    loading.value = true;
    const requestData = {
      orgId: org.orgId,
      orgTwName: org.orgTwName,
      orgEnName: org.orgEnName
    };

    try {
      const response = await api.post("/api/modadworg/create", requestData, {
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      if (response && response.data && response.data.code === "0") {
        $n.success(t("org.success.add", { input: org.orgTwName }));
        await getOrgList();
        return { isCreateOK: true };
      } else {
        if (response.data.msg) {
          $n.error(response.data.msg);
        }
        loading.value = false;
        return { isCreateOK: false };
      }
    } catch (error) {
      if (error.status === HttpStatusCode.Forbidden) {
        $n.error(t("org.error.permissionDenied"));
      } else {
        const errorMessage = error.response?.data?.message;
        $n.error(errorMessage);
      }
      loading.value = false;
      return { isCreateOK: false };
    }
  }

  function resetOrgEdit(orgId, orgTwName, orgEnName, vcDataSource) {
    orgEdit.value = [
      { name: "orgId", value: orgId },
      { name: "orgTwName", value: orgTwName },
      { name: "orgEnName", value: orgEnName },
      { name: "vcDataSource", value: vcDataSource }
    ];
  }

  //編輯組織
  async function editOrg(org) {
    loading.value = true;
    const requestData = {
      id: org.id,
      orgId: org.orgId,
      orgTwName: org.orgTwName,
      orgEnName: org.orgEnName,
      vcDataSource: org.vcDataSource
    };

    if (requestData.orgId === "default") {
      $n.error(t("org.notice.defaultCanNotEdit"));
      loading.value = false;
      return { isEditOK: false };
    } else {
      try {
        const response = await api.post("/api/modadworg/update", requestData, {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });

        if (response && response.data && response.data.code === "0") {
          $n.success(t("org.success.edit", { input: org.orgTwName }));
          await getOrgList();
          return { isEditOK: true };
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          loading.value = false;
          return { isEditOK: false };
        }
      } catch (error) {
        if (error.status === HttpStatusCode.Forbidden) {
          $n.error(t("org.error.permissionDenied"));
        } else {
          const errorMessage = error.response?.data?.message;
          $n.error(errorMessage);
        }
        loading.value = false;
        return { isEditOK: false };
      }
    }
  }

  //刪除組織
  async function deleteOrg(org) {
    loading.value = true;
    const requestData = {
      orgId: org.orgId
    };

    if (requestData.orgId === "default") {
      $n.error(t("org.notice.defaultCanNotDelete"));
      loading.value = false;
      return { isDeleteOK: false };
    } else {
      try {
        const response = await api.post("/api/modadworg/delete", requestData, {
          headers: {
            Authorization: `Bearer ${jwtToken}`
          }
        });

        if (response && response.data && response.data.code === "0") {
          await getOrgList();
          return { isDeleteOK: true };
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          loading.value = false;
          return { isDeleteOK: false };
        }
      } catch (error) {
        if (error.status === HttpStatusCode.Forbidden) {
          $n.error(t("org.error.permissionDenied"));
        } else {
          const errorMessage = error.response?.data?.message;
          $n.error(errorMessage);
        }
        loading.value = false;
        return { isDeleteOK: false };
      }
    }
  }

  return {
    editOrg,
    deleteOrg,
    pagination,
    currentFilter,
    getOrgList,
    dialogLoading,
    loading,
    totalCount,
    orgList,
    orgDetail,
    getOrgDetail,
    resetOrgDetail,
    createOrg,
    orgCreate,
    resetOrgCreate,
    orgEdit,
    resetOrgEdit
  };
});
