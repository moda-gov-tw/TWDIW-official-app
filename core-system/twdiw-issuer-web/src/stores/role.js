import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";
import { ref } from "vue";
import { getToken } from "src/boot/auth.js";
import { i18n } from "src/boot/i18n";
import {
  createPageableUrlParams,
  createFilterParams,
  createFieldsUrlParams
} from "src/utils/query";

const jwtToken = getToken();
const { t } = i18n.global;
const defaultFields = [
  "id",
  "roleId",
  "roleName",
  "description",
  "state",
  "createTime"
];

export const useRoleStore = defineStore("role", () => {
  //plugin settings
  const $n = useNotify();

  //state
  const tablePage = ref({
    sortBy: undefined,
    descending: false,
    page: 1,
    rowsPerPage: 10,
    rowsPerPageOptions: [10, 20, 50, 0]
  });

  const roles = ref([]);
  const selected = ref({});
  const roleResList = ref([]);
  const roleDetailOpen = ref(false);
  const roleEditOpen = ref(false);
  const loading = ref(false);

  /**
   * 查詢所有角色資料
   * @param {*} filter
   * @param {*} paginationRequest
   * @param {*} fields
   */
  async function searchRoles(filter, paginationRequest, fields) {
    const requestFields = fields ?? defaultFields;

    loading.value = true;
    try {
      const baseUrl = `/api/modadw321w/roles`;
      const filterParams = createFilterParams(filter);
      const fieldsParams = createFieldsUrlParams(requestFields);
      const paginationParams = createPageableUrlParams(paginationRequest);

      const url = `${baseUrl}?${filterParams}&${fieldsParams}&${paginationParams}`;
      const { data } = await api.get(url);

      roles.value = data.content;
      Object.assign(tablePage.value, paginationRequest);
      tablePage.value.rowsNumber = data.page.totalElements;
    } catch (err) {
      console.error(err);
    } finally {
      loading.value = false;
    }
  }

  //載入所有資料
  function getRoles() {
    const { page, size, sort } = tablePage.value;
    const url = `/api/modadw321w/search?page=${
      page - 1
    }&size=${size}&sort=${sort},desc`;
    const requestData = {};
    api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        const code = response?.data?.code;
        const data = response?.data?.data;

        if (code !== "0" || !data) {
          $n.error(response?.data?.msg || t("role.error.unknown"));
        }

        const headerPage = response?.headers?.get("X-Total-Count");

        tablePage.value.rowsNumber = parseInt(headerPage);
        roles.value = response.data.data;
      })
      .catch((error) => {
        console.error(t("role.error.roleList") + " : ", error);
      });
  }

  //當下選擇的功能詳情資料
  async function selectRole(row) {
    selected.value = row;
    //功能資訊
    if (row) {
      await loadRoleHasRes(row);
    }
  }

  function clearSelectedRole() {
    selected.value = {};
  }

  //更新啟停狀態
  async function updateState(row) {
    let messageAlert = {};
    const url = "/api/modadw321w/updateState";
    const requestData = {
      id: row.id,
      roleId: row.roleId
    };

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (
          response &&
          response.data &&
          response.data.code === "0" &&
          response.data.data
        ) {
          //以功能代碼找到相應的資料行後,替換成更新後的內容
          roles.value = roles.value.map((row) => {
            if (row.id === response.data.data.id) {
              if (row.state != response.data.data.state) {
                messageAlert.isUpdateOK = true;
              }
              selectRole(response.data.data);
              // 對於目標物件，返回新的資料
              return response.data.data;
            } else {
              // 對於其他物件，保持不變
              return row;
            }
          });
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
    return messageAlert;
  }

  //條件搜尋
  function searchTableApi(searchModel) {
    const { page, size, sort } = tablePage.value;
    const url = `/api/modadw321w/search?page=${
      page - 1
    }&size=${size}&sort=${sort},desc`;

    const requestData = {
      roleId: searchModel.roleId,
      roleName: searchModel.roleName,
      state: searchModel.state?.value
    };

    api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        const { headers, data } = response;
        const headerPage = headers.get("X-Total-Count");

        tablePage.value.rowsNumber = parseInt(headerPage);
        roles.value = data.data;
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  //新增角色
  function createRole(role) {
    loading.value = true;
    const url = `/api/modadw321w/create`;
    const requestData = {
      roleId: role.roleId,
      roleName: role.roleName,
      description: role.description,
      state: role.state.value
    };

    return api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code === "0") {
          loading.value = false;
          return Promise.resolve(response);
        } else {
          const errMsg = response?.data?.msg ?? t("role.error.unknown");
          loading.value = false;
          return Promise.reject(new Error(errMsg));
        }
      })
      .catch((error) => {
        loading.value = false;
        return Promise.reject(error);
      });
  }

  //編輯角色
  function editRole(role) {
    loading.value = true;
    const url = `/api/modadw321w/update`;

    const requestData = {
      id: role.id,
      roleId: role.roleId,
      roleName: role.roleName,
      description: role.description,
      state: role?.state
    };

    if (
      (requestData.roleId === "default_role" ||
        requestData.roleId === "issuer_admin" ||
        requestData.roleId === "verify_admin") &&
      requestData.state == "0"
    ) {
      const warningMessage = t("role.notice.defaultRoleState");
      $n.warn(warningMessage);
      loading.value = false;
      return Promise.reject(new Error(warningMessage));
    }

    return api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (
          response &&
          response.data &&
          response.data.code === "0" &&
          response.data.data
        ) {
          const name = response.data.data.roleName;
          const message = t("role.notice.actionSuccess", {
            action: t("edit"),
            field: name
          });
          $n.success(message);

          loading.value = false;
          return Promise.resolve(response.data.data);
        } else {
          const errMsg = response?.data?.msg ?? t("role.error.unknown");
          $n.error(errMsg);
          loading.value = false;
          return Promise.reject(new Error(errMsg));
        }
      })
      .catch((error) => {
        loading.value = false;
        return Promise.reject(error);
      });
  }

  //刪除角色
  async function deleteRole(role) {
    const url = `/api/modadw321w/delete`;
    const requestData = {
      id: role.id
    };

    if (
      role.roleId === "default_role" ||
      role.roleId === "issuer_admin" ||
      role.roleId === "verify_admin"
    ) {
      const warningMessage = t("role.notice.defaultRoleDelete");
      $n.warn(warningMessage);
      loading.value = false;
      return Promise.reject(new Error(warningMessage));
    }

    try {
      const response = await api.post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      });

      if (response && response.data && response.data.code === "0") {
        clearSelectedRole();
        $n.success(
          t("role.notice.actionSuccess", {
            action: t("delete"),
            field: role.roleName
          })
        );
      } else {
        const errorMessage = response?.data?.msg || t("role.error.unknown");

        $n.error(t("role.error.delete") + " : " + errorMessage);
      }
    } catch (err) {
      console.error("error:", err);
    }
  }

  //======以下為授權功能相關======
  //儲存授權功能
  async function saveAuthFunc(resList) {
    loading.value = true;
    const url = `/api/modadw321w/saveConferRes`;
    const requestData = {
      resList: resList,
      id: selected.value.id,
      roleId: selected.value.roleId
    };

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (
          response &&
          response.data &&
          response.data.code === "0" &&
          response.data.data
        ) {
          roleResList.value = response.data.data;
          $n.success(t("role.actions.giveResource") + t("success"));
          loading.value = false;
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          loading.value = false;
        }
      })
      .catch((error) => {
        console.error("error:", error);
        loading.value = false;
      });
  }

  // 讀取角色已授權功能
  async function loadRoleHasRes(row) {
    const url = `/api/modadw321w/searchConferRes`;
    const requestData = {
      id: row.id,
      roleId: row.roleId
    };

    return api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (
          response &&
          response.data &&
          response.data.code === "0" &&
          response.data.data
        ) {
          roleResList.value = response.data.data;
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  // 取得樹狀圖及角色有的樹狀勾選資料
  const tickedNodes = ref([]);
  const resNodeDTO = ref([]);
  async function searchResTree(row) {
    const url = `/api/modadw321w/searchResTree`;
    const requestData = {
      id: row.id,
      roleId: row.roleId
    };

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (
          response &&
          response.data &&
          response.data.code === "0" &&
          response.data.data
        ) {
          tickedNodes.value = response.data.data.tickedNodes;
          resNodeDTO.value = response.data.data.resNodeDTO;
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  return {
    roles,
    updateState,
    selected,
    roleDetailOpen,
    roleEditOpen,
    roleResList,
    tickedNodes,
    resNodeDTO,
    tablePage,
    loading,
    searchRoles,
    getRoles,
    searchTableApi,
    createRole,
    selectRole,
    editRole,
    deleteRole,
    saveAuthFunc,
    loadRoleHasRes,
    searchResTree
  };
});
