import { defineStore } from "pinia";
import { ref } from "vue";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
import { getToken } from "src/boot/auth";

const jwtToken = getToken();
const $n = useNotify();

export const useAccountStore = defineStore("accountStore", () => {
  //state
  const { t } = useI18n();
  const loading = ref(false);
  const isOpenCreate = ref(false);
  const isOpenDetail = ref(false);
  const hasAdminRole = ref(false);
  const isOpenEdit = ref(false);
  const dialogLoading = ref(false);
  const createDialogLoading = ref(false);
  const accountDataDialogLoading = ref(false);
  const totalCount = ref("");
  const accountList = ref([]);
  const accountDetail = ref({});
  const selected = ref(null);
  const selectedByRole = ref([]);
  const selectedByRoleMap = ref([]);
  const orgIdOptions = ref([]);
  const userTypeIdOptions = ref([]);
  const roleList = ref([]);
  const roleListLength = ref(0);

  //roleView control
  const isOpenRoleView = ref(false);

  const pagination = ref({
    sortBy: "createTime",
    descending: true,
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const currentFilter = ref({
    userId: "",
    userName: "",
    state: "",
    startDate: "",
    endDate: ""
  });

  // 帳號管理頁面，建立帳號。
  async function createUser(data) {
    createDialogLoading.value = true;
    const url = `/api/modadw311w/create`;
    const requestData = { ...data };
    requestData.orgId = requestData.orgId.value;
    requestData.userTypeId = requestData.userTypeId.value;
    try {
      const response = await api.post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // 檢查回應狀態。
      if (response && response.data && response.data.code === "0") {
        $n.success(t("account.success.create"));
        await getAccountList(1, {
          size: pagination.value.rowsPerPage,
          sortBy: pagination.value.sortBy,
          descending: pagination.value.descending,
          filter: currentFilter.value
        });
        formatClean();
        isOpenCreate.value = false;
        createDialogLoading.value = false;
        return Promise.resolve(true);
      } else {
        if (response.data.msg) {
          $n.error(response.data.msg);
        }
        createDialogLoading.value = false;
        return Promise.resolve(false);
      }
    } catch (error) {
      // 捕獲錯誤並記錄。
      if (error.response && error.response.status === 403) {
        $n.error(t("account.error.permissionDenied"));
      }
      console.error("error:", error);
      createDialogLoading.value = false;
      return Promise.reject(error);
    }
  }

  // 帳號管理頁面，更新帳號。
  async function updateUser(data) {
    loading.value = true;
    const url = `/api/modadw311w/update`;
    const login = data.login;
    let isOk = false;

    const requestData = { ...data };
    requestData.userTypeId = requestData.userTypeId.value;

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then(async (response) => {
        if (response && response.data && response.data.code === "0") {
          await getAccountList(1, {
            size: pagination.value.rowsPerPage,
            sortBy: pagination.value.sortBy,
            descending: pagination.value.descending,
            filter: currentFilter.value
          });
          $n.success(
            t("account.success.accountState", {
              action: t("edit"),
              input: login
            })
          );
          loading.value = false;
          isOk = true;
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          loading.value = false;
        }
      })
      .catch((error) => {
        if (error.response && error.response.status === 403) {
          $n.error(t("account.error.permissionDenied"));
        }
        console.error("error:", error);
        loading.value = false;
      });
    return isOk;
  }

  // 搜尋帳號管理頁面資料
  async function getAccountList(
    page = 1,
    options = {
      size: 10,
      sortBy: "createTime",
      descending: true,
      userId: "",
      userName: "",
      state: "",
      startDate: "",
      endDate: ""
    }
  ) {
    loading.value = true;

    const finalOptions = {
      ...options,
      userId: options.userId || currentFilter.value.userId,
      userName: options.userName || currentFilter.value.userName,
      state: options.state || currentFilter.value.state,
      startDate: options.startDate || currentFilter.value.startDate,
      endDate: options.endDate || currentFilter.value.endDate
    };

    // 修改排序邏輯
    // 將 sortBy 和 descending 組合成正確的格式
    // 例如: id,asc 或 id,desc
    if (finalOptions.sortBy === "orgTwName") {
      finalOptions.sortBy = "orgId";
    }
    const sortStr = finalOptions.sortBy
      ? `${finalOptions.sortBy},${finalOptions.descending ? "desc" : "asc"}`
      : "createTime,dasc";

    const params = {
      sort: sortStr,
      page: page - 1,
      size: finalOptions.size || 2000
    };

    if (finalOptions.userId) {
      params["login"] = finalOptions.userId;
    }
    if (finalOptions.userName) {
      params["userName"] = finalOptions.userName;
    }
    if (finalOptions.state) {
      params["state"] = finalOptions.state;
    }
    // 獲取當前時區偏移量（格式化為 +HH:00 或 -HH:00）
    const tzOffset = new Date().getTimezoneOffset();
    const tzString = `${tzOffset <= 0 ? "+" : "-"}${String(
      Math.abs(tzOffset / 60)
    ).padStart(2, "0")}:00`;

    if (finalOptions.startDate && finalOptions.endDate) {
      params["beginDate"] = `${finalOptions.startDate}`;
      params["endDate"] = `${finalOptions.endDate}`;
    } else if (!finalOptions.startDate && finalOptions.endDate) {
      params["beginDate"] = "1970-01-01";
      params["endDate"] = `${finalOptions.endDate}`;
    } else if (finalOptions.startDate && !finalOptions.endDate) {
      params["beginDate"] = `${finalOptions.startDate}`;
      const today = new Date();
      const formattedToday = today.toISOString().split("T")[0];
      params["endDate"] = `${formattedToday}`;
    }

    try {
      const response = await api.get("/api/modadw311w/queryAll", {
        params,
        headers: {
          Authorization: `Bearer ${jwtToken}`
        }
      });

      response.data.content.map((item) => {
        item.orgId = item.orgId + " " + item.orgTwName;
        return item;
      });

      accountList.value = response.data.content;
      totalCount.value = response.headers["x-total-count"];

      pagination.value = {
        ...pagination.value,
        page: page,
        rowsNumber: response.data.page.totalElements,
        sortBy: options.sortBy || "id",
        descending: options.descending || false,
        rowsPerPage: options.size
      };

      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message;
      $n.error(t(errorMessage));
    } finally {
      loading.value = false;
    }
  }

  // 帳號管理頁面，查詢所有角色清單。
  const searchRoleListUrl = `/api/modadw311w/allRole`;
  async function getRoleList(login) {
    api
      .post(
        searchRoleListUrl,
        { userId: login },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      )
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            roleList.value = response.data.data;
            roleListLength.value = roleList.value.length;
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        if (error.response && error.response.status === 403) {
          $n.error(t("account.error.permissionDenied"));
        }
        console.error("error:", error);
      });
  }

  // 取得帳號管理頁面所選擇帳號的角色清單。
  const getUserRolesUrl = `/api/modadw311w/userRoles`;
  async function getUserRoles() {
    const userDTO = selected.value;
    api
      .post(getUserRolesUrl, userDTO, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            selectedByRole.value = response.data.data;
            selectedByRoleMap.value = selectedByRole.value;
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        if (error.response && error.response.status === 403) {
          $n.error(t("account.error.permissionDenied"));
        }
        console.error("error:", error);
      });
  }

  // 查詢帳號資料詳情
  async function selectAccountDetail(extendedId) {
    const baseUrl = `/api/modadw311w/queryDetail`;
    const url = `${baseUrl}/${extendedId}`;
    const res = await api.get(url);

    res.data.data.orgId = res.data.data.orgId + " " + res.data.data.orgTwName;
    if (res && res.data && res.data.code === "0") {
      accountDetail.value = res.data.data;
      selected.value = res.data.data;
    } else {
      if (res.data.msg) {
        $n.error(res.data.msg);
      }
    }
  }

  // 查詢登入使用者是否具備admin角色
  async function checkAdminRoleForCurrentUser() {
    const baseUrl = `/api/modadw311w/checkAdminRole`;
    const res = await api.get(baseUrl);

    if (res && res.data && res.data.code) {
      if (res.data.code === "0") {
        hasAdminRole.value = true;
      } else {
        hasAdminRole.value = false;
      }
    } else {
      if (res.data.msg) {
        $n.error(res.data.msg);
      }
    }
  }

  const resetBwdUrl = `/api/modadw311w/resetBwd`;
  // 帳號管理頁面，重置所選擇帳號的密碼。
  const resetBwd = (row) => {
    api
      .post(
        resetBwdUrl,
        { login: row.login },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      )
      .then(async (response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            selected.value = response.data.data;
            $n.success(t("account.success.resendPwdMail"));
            await getAccountList(1, {
              size: pagination.value.rowsPerPage,
              sortBy: pagination.value.sortBy,
              descending: pagination.value.descending,
              filter: currentFilter.value
            });
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        if (error.response && error.response.status === 403) {
          $n.error(t("account.error.permissionDenied"));
        }
        console.error("error:", error);
      });
  };

  const deleteAccountUrl = `/api/modadw311w/deleteAccount`;
  // 帳號管理頁面，刪除所選擇的帳號。
  const deleteAccount = (row) => {
    api
      .post(
        deleteAccountUrl,
        { login: row.login },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      )
      .then(async (response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            $n.success(
              t("account.success.accountState", {
                action: t("delete"),
                input: row.login
              })
            );
            await getAccountList(1, {
              size: pagination.value.rowsPerPage,
              sortBy: pagination.value.sortBy,
              descending: pagination.value.descending,
              filter: currentFilter.value
            });
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        if (error.response && error.response.status === 403) {
          $n.error(t("account.error.permissionDenied"));
        }
        console.error("error:", error);
      });
  };

  const getOrgOptionsUrl = `/api/modadw311w/searchOrgList`;
  // 取得組織選項列表
  async function getOrgOptionsForAccountManagement() {
    const requestData = {
      state: "1"
    };

    try {
      const response = await api.get(getOrgOptionsUrl, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      // 過濾掉orgId為"default"的資料
      const filteredData = response.data.data.filter(
        (item) => item.orgId !== "default"
      );

      // 將資料映射為所需格式
      orgIdOptions.value = filteredData.map((item) => ({
        label: item.orgId + " " + item.orgTwName,
        value: item.orgId
      }));
    } catch (error) {
      console.error("error:", error);
    }
  }

  const getUserTypeIdOptionsUrl = `/api/modadw311w/searchUserTypeIdList`;
  // 取得帳號列表
  async function getUserTypeIdOptionsForAccountManagement() {
    const requestData = {
      state: "1"
    };

    try {
      const response = await api.get(getUserTypeIdOptionsUrl, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      const filteredData = response.data.data;
      userTypeIdOptions.value = filteredData.map((item) => ({
        label: item.name,
        value: item.code
      }));
    } catch (error) {
      console.error("error:", error);
    }
  }

  const changeActivateBwdUrl = `/api/modadw311w/changeActivated`;
  // 帳號管理頁面，變更所選擇帳號的啟停狀態。
  const updateAccountState = async (row) => {
    try {
      const response = await api.post(
        changeActivateBwdUrl,
        { login: row.login },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          selected.value = response.data.data;
          await getAccountList(1, {
            size: pagination.value.rowsPerPage,
            sortBy: pagination.value.sortBy,
            descending: pagination.value.descending,
            filter: currentFilter.value
          });
          selectAccountDetail(row.extendedId);
          // 返回一個包含isUpdateOK屬性的對象。
          return { isUpdateOK: true };
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          // 返回一個包含isUpdateOK屬性的對象，表示更新失敗。
          return { isUpdateOK: false };
        }
      }
    } catch (error) {
      console.error("error:", error);
      // 返回一個包含isUpdateOK屬性的對象，表示發生錯誤。
      return { isUpdateOK: false };
    }
  };

  // 重新寄送帳號啟用信件。
  const reSendActivationEmail = async (data) => {
    try {
      const url = `/api/modadw311w/reSendActivationEmail`;
      const response = await api.post(
        url,
        { id: data },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          $n.success(t("account.success.activateMail"));
          await getAccountList(1, {
            size: pagination.value.rowsPerPage,
            sortBy: pagination.value.sortBy,
            descending: pagination.value.descending,
            filter: currentFilter.value
          });
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
        }
      }
    } catch (error) {
      if (error.response && error.response.status === 403) {
        $n.error(t("account.error.permissionDenied"));
      }
      console.error("error:", error);
    }
  };

  const formDataCreate = ref({
    login: "",
    userName: "",
    orgId: "",
    tel: "",
    userTypeId: ""
  });

  const formDataEdit = ref({
    id: null,
    login: null,
    userName: null,
    orgId: null,
    tel: null,
    userTypeId: null
  });

  const formatClean = () => {
    formDataCreate.value.login = "";
    formDataCreate.value.userName = "";
    formDataCreate.value.orgId = "";
    formDataCreate.value.tel = "";

    if (userTypeIdOptions.value.length > 0) {
      formDataCreate.value.userTypeId = userTypeIdOptions.value[0];
    } else {
      formDataCreate.value.userTypeId = "";
    }
  };

  const openRoleDialog = (userId) => {
    getRoleList(userId);
    getUserRoles();
    isOpenRoleView.value = true;
  };

  const closeRoleDialog = () => {
    isOpenRoleView.value = false;
  };

  // 最後返回所有方法和狀態
  return {
    accountList,
    pagination,
    loading,
    dialogLoading,
    currentFilter,
    createDialogLoading,
    accountDataDialogLoading,
    isOpenCreate,
    isOpenDetail,
    isOpenEdit,
    formDataCreate,
    formatClean,
    getAccountList,
    getRoleList,
    getUserRoles,
    selectAccountDetail,
    accountDetail,
    selectedByRole,
    selectedByRoleMap,
    orgIdOptions,
    userTypeIdOptions,
    getOrgOptionsForAccountManagement,
    getUserTypeIdOptionsForAccountManagement,
    selected,
    formDataEdit,
    isOpenRoleView,
    openRoleDialog,
    closeRoleDialog,
    roleList,
    roleListLength,
    resetBwd,
    deleteAccount,
    updateAccountState,
    createUser,
    reSendActivationEmail,
    updateUser,
    checkAdminRoleForCurrentUser,
    hasAdminRole
  };
});
