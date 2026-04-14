import { defineStore } from "pinia";
import { api } from "src/boot/axios";
import { ref, reactive, computed } from "vue";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";

export const useAccessTokenStore = defineStore("accessToken", () => {
  //plugin settings
  const $n = useNotify();
  const { t } = useI18n();

  //state
  const pagination = ref({
    sortBy: "desc",
    descending: false,
    page: 1
  });
  const openBar = ref(null);
  const isOpenDetail = ref(false);
  const accessTokens = ref([]);
  const selected = ref(null);
  const page = 0;
  const size = 100;
  const sort = "createTime";
  const accessTokenResList = ref([]);
  const jwtToken = sessionStorage.getItem("jwt-user-object");
  const selectedFields = ref([]);

  const fields = computed(() => {
    return (
      "id" +
      (selectedFields.value.length > 0
        ? "," + selectedFields.value.join(",")
        : "")
    );
  });

  //搜尋相關內容
  const searchModel = reactive({
    accessTokenName: null,
    ownerName: null,
    beginDate: null,
    endDate: null,
    orgId: null,
    state: null
  });

  const cleanSearchModel = () => {
    searchModel.accessTokenName = null;
    searchModel.ownerName = null;
    searchModel.orgId = null;
    searchModel.beginDate = null;
    searchModel.endDate = null;
    searchModel.state = null;
  };

  // AccessToken管理頁面，取得所有的AccessToken清單。
  function getAllAccessTokens() {
    const searchByRuleUrl = `/api/modadw351w?page=${page}&size=${size}&sort=${sort},desc&fields=${fields.value}`;
    const requestData = {
      accessTokenName: searchModel.accessTokenName
        ? searchModel.accessTokenName
        : null,
      ownerName: searchModel.ownerName ? searchModel.ownerName : null,
      beginDate: searchModel.beginDate ? searchModel.beginDate : null,
      endDate: searchModel.endDate ? searchModel.endDate : null,
      orgId: searchModel.orgId ? searchModel.orgId : null,
      state: searchModel.state
        ? searchModel.state.value === "true"
          ? "enabled"
          : "disabled"
        : ""
    };
    api
      .post(searchByRuleUrl, requestData, {
        headers: {
          "Content-Type": "application/json"
        }
      })
      .then((response) => {
        if (response && response.data && response.data.data) {
          if (response.data.code === "0") {
            const headers = response.headers;
            const headerPage = headers.get("X-Total-Count");
            pagination.value.rowsNumber = parseInt(headerPage);
            accessTokens.value = response.data.data;
            if (selected.value) {
              for (let accessToken of accessTokens.value) {
                if (accessToken.id === selected.value.id) {
                  selectAccessToken(accessToken);
                }
              }
            }
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
        openBar.value.closeDropDown();
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  function selectAccessToken(row) {
    selected.value = row;
    //功能資訊
    loadAccessTokenHasRes(row);
  }

  // AccessToken管理頁面，儲存所選擇AccessToken的功能。
  async function saveAuthFunc(relDTOList) {
    const url = `/api/modadw351w/saveRel`;
    const requestData = {
      relDTOList: relDTOList,
      accessToken: selected.value.accessToken
    };

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            loadAccessTokenHasRes(selected.value);
            $n.success(t("accessToken.success"));
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  const url = `/api/modadw351w/getDetail/res`;
  // AccessToken管理頁面，讀取所選擇AccessToken已授權功能。
  function loadAccessTokenHasRes(row) {
    const requestData = {
      id: row.id,
      accessToken: row.accessToken
    };

    api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            accessTokenResList.value = response.data.data;
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  // 取得樹狀圖及AccessToken有的樹狀勾選資料。
  const tickedNodes = ref([]);
  const resNodeDTO = ref([]);
  async function searchResTree(row) {
    const url = `/api/modadw351w/searchResTree`;
    const requestData = {
      id: row.id,
      accessToken: row.accessToken
    };

    await api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            tickedNodes.value = response.data.data.tickedNodes;
            resNodeDTO.value = response.data.data.resNodeDTO;
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  }

  return {
    accessTokens,
    selected,
    isOpenDetail,
    pagination,
    searchModel,
    openBar,
    tickedNodes,
    resNodeDTO,
    accessTokenResList,
    getAllAccessTokens,
    selectAccessToken,
    cleanSearchModel,
    loadAccessTokenHasRes,
    searchResTree,
    saveAuthFunc,
    selectedFields
  };
});
