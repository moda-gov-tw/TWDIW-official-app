<template>
  <div class="row">
    <!-- AccessToken 管理 -->
    <span class="title-size">{{ t("accessToken.title") }}</span>
    <div class="col">
      <AccessTokenCreate />
    </div>
  </div>
  <q-table
    class="sticky-header q-mr-md"
    dense
    flat
    :title="t('accessToken.title')"
    :rows="accessTokens"
    :columns="columns"
    :visible-columns="visibleColumns"
    row-key="id"
    v-model:selected="selectedByActions"
    :pagination="pagination"
    :hide-header="loading"
    :loading="loading"
    selection="multiple"
    :rows-per-page-options="rowsPerPage"
  >
    <!-- 設定 q-table body-cell -->
    <template v-slot:body-cell-enabled="props">
      <q-td :props="props">
        <div>
          <q-badge
            :color="props.value === 'true' ? 'green-5' : 'red-5'"
            :label="props.value === 'true' ? t('enabled') : t('disabled')"
          />
        </div>
      </q-td>
    </template>

    <!-- 設定 q-table 上方 -->
    <template #top>
      <div
        class="row full-width q-gutter-sm q-pa-sm items-center justify-around"
      >
        <div>
          <!-- 請輸入 Token 名稱 -->
          <RoundSearchbar
            ref="openBar"
            v-model="searchModel.accessTokenName"
            :placeholder="t('input', { input: ' ' + t('accessToken.name') })"
            @search="onSearch"
          >
            <template #form>
              <form-wrapper
                v-model="searchModel"
                :fields="fields"
                :layout="[2, 2, 2]"
                @confirm="onConfirm"
                @reset="cleanSearchModel"
              >
              </form-wrapper>
            </template>
          </RoundSearchbar>
        </div>
        <q-space />
      </div>
    </template>

    <!-- actions -->
    <template #body-cell-action="props">
      <q-td class="qtd">
        <action-dropdown
          :actions="actions"
          :data="props"
          @click="RowSelector(props)"
        />
      </q-td>
    </template>
    <template v-slot:loading>
      <q-inner-loading showing color="primary" />
    </template>
  </q-table>

  <!-- 賦予功能Dialog -->
  <AccessTokenResource
    v-model="isOpenDialog"
    @res-click="onResClick"
    ref="accessTokenResource"
  />
</template>
<script setup>
import { ref, defineEmits, onMounted, computed } from "vue";
import { storeToRefs } from "pinia";
import { useNotify, useDialog } from "src/utils/plugin";
import RoundSearchbar from "src/components/RoundSearchbar.vue";
import ActionDropdown from "components/ActionDropdown.vue";
import { useAccessTokenStore } from "stores/accessToken";
import AccessTokenCreate from "./AccessTokenCreate.vue";
import { useI18n } from "vue-i18n";
import { api } from "src/boot/axios";
import AccessTokenResource from "./AccessTokenResource.vue";

const { t } = useI18n();
const selectedByActions = ref([]);
let isOpenDialog = ref(false);
const jwtToken = sessionStorage.getItem("jwt-user-object");

//table分頁參數
const rowsPerPage = ref([10]);

//plugin settings
const $n = useNotify();
const $d = useDialog();

//receipt store settings
const store = useAccessTokenStore();
const {
  accessTokens,
  isOpenDetail,
  pagination,
  searchModel,
  openBar,
  selected,
  tickedNodes,
  selectedFields
} = storeToRefs(store);
const {
  getAllAccessTokens,
  selectAccessToken,
  cleanSearchModel,
  searchResTree
} = store;

//日期格式化
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
};

//欄位設定
const columns = [
  {
    name: "action",
    label: t("action"),
    align: "left",
    required: true
  },
  {
    name: "token",
    label: t("accessToken.table.token"),
    sortable: false,
    field: "accessToken",
    align: "left"
  },
  {
    name: "name",
    label: t("accessToken.table.name"),
    field: "accessTokenName",
    align: "left",
    sortable: true
  },
  {
    name: "ownerName",
    label: t("accessToken.table.ownerName"),
    field: "ownerName",
    align: "left",
    sortable: true
  },
  {
    name: "enabled",
    label: t("accessToken.table.enabled"),
    field: "state",
    align: "left",
    sortable: true
  },
  {
    name: "updateTime",
    label: t("accessToken.table.createTime"),
    field: "createTime",
    align: "left",
    format: formatDate,
    sortable: true
  }
];

const visibleColumns = computed(() => {
  return selectedFields.value.map((e) => {
    for (let col of columns) {
      if (e === col.field) {
        return col.name;
      }
    }
    return null;
  });
});
//操作
const actions = [
  {
    label: t("accessToken.viewAndEdit"),
    color: "primary",
    icon: "edit",
    method: (props) => {
      emit("actionClick");
      selectAccessToken(props.row);
      isOpenDetail.value = true;
    }
  },
  {
    label: t("actions.giveResource"),
    color: "accent",
    icon: "checklist",
    method: (props) => {
      onResClick(props);
    }
  },
  {
    label: t("enabled"),
    color: "primary",
    icon: "fact_check",
    visible: (props) => props.row.state === "false",
    method: (props) => {
      selectAccessToken(props.row);
      $d.confirm(
        t("confirm"),
        t("accessToken.enabled", {
          input: "" + t("accessToken.name") + props.row.accessTokenName
        }),
        () => {
          changeState(props.row);
        }
      );
    }
  },
  {
    label: t("disabled"),
    color: "negative",
    icon: "fact_check",
    visible: (props) => props.row.state === "true",
    method: (props) => {
      selectAccessToken(props.row);
      $d.confirm(
        t("confirm"),
        t("accessToken.disabled", {
          input: "" + t("accessToken.name") + props.row.accessTokenName
        }),
        () => {
          changeState(props.row);
        }
      );
    }
  },
  {
    label: t("delete"),
    color: "negative",
    icon: "delete",
    method: (props) => {
      const AccessTokenIdList = selectedByActions.value.map(
        (accessToken) => accessToken.id
      );
      const AccessTokenTitleList = selectedByActions.value
        .map((accessToken) => accessToken.accessTokenName)
        .join("<br>");
      const config = {
        title: t("confirm"),
        message: `${t("accessToken.delete", {
          input: " " + t("accessToken.token")
        })}</br>${AccessTokenTitleList}`,
        cancel: true,
        html: true
      };
      $d.custom(config, () => {
        deleteAccessTokenByListId(AccessTokenIdList);
      });
    }
  }
];

const accessTokenResource = ref(null);
const onResClick = async (props) => {
  tickedNodes.value = [];
  isOpenDialog.value = true;
  selectAccessToken(props.row);
  await searchResTree(props.row);
  accessTokenResource.value.leftExpandAll();
  accessTokenResource.value.copySelectedNodes();
};

const onSearch = (data) => {
  searchModel.accessTokenName = data;
  searchTable();
};

const searchTable = () => {
  getAllAccessTokens();
};

const onConfirm = (model) => {
  searchModel.accessTokenName = model.value.login ?? null;
  searchModel.ownerName = model.value.ownerName ?? null;
  searchModel.endDate = model.value.endDate ?? null;
  searchModel.orgId = model.value.orgId ?? null;
  searchModel.state = model.value.state ?? null;
  searchTable();
};

const fields = ref([
  {
    name: "accessTokenName",
    label: t("accessToken.table.name"),
    inputType: "input",
    field: "accessTokenName",
    hint: t("input", { input: t("accessToken.name") })
  },
  {
    name: "ownerName",
    label: t("accessToken.table.ownerName"),
    inputType: "input",
    field: "ownerName",
    hint: t("input", { input: t("accessToken.ownerName") })
  },
  {
    name: "beginDate",
    label: t("startDate"),
    inputType: "date",
    field: "beginDate",
    hint: t("startDate")
  },
  {
    name: "endDate;",
    label: t("endDate"),
    inputType: "date",
    field: "endDate",
    hint: t("endDate")
  },
  {
    name: "orgId",
    label: t("accessToken.table.orgId"),
    inputType: "input",
    field: "orgId",
    hint: t("input", { input: t("accessToken.orgId") })
  },
  {
    name: "enabled",
    label: t("accessToken.table.enabled"),
    inputType: "select",
    field: "state",
    options: [
      { label: t("enabled"), value: "true" },
      { label: t("disabled"), value: "false" }
    ]
  }
]);

//table refs
const loading = ref(false);
const emit = defineEmits(["actionClick"]);

const RowSelector = (props) => {
  if (selectedByActions.value.includes(props.row)) {
    return;
  }
  selectedByActions.value = [props.row];
};

onMounted(() => {
  loading.value = true;
  getAllAccessTokens();
  selected.value = [];
  loading.value = false;
  selectedFields.value = columns.filter((e) => !!e.field).map((e) => e.field);
});

const deleteUrl = `/api/modadw351w`;
// AccessToken管理頁面，刪除所選擇的AccessToken。
const deleteAccessTokenByListId = (idList) => {
  api
    .delete(deleteUrl, {
      data: idList,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`
      }
    })
    .then(async (response) => {
      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          if (
            selectedByActions.value.some(
              (accessToken) => accessToken.id === selected.value.id
            )
          ) {
            isOpenDetail.value = false;
          }
          $n.success(
            t("accessToken.deleteSuccess", {
              input: t("accessToken.table.token") + " "
            })
          );
          await getAllAccessTokens();
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
};

const changeActivateBwdUrl = `/api/modadw351w/changeState`;
// AccessToken管理頁面，變更選擇的AccessToken啟停狀態。
const changeState = (row) => {
  api
    .post(
      changeActivateBwdUrl,
      { id: row.id },
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
          $n.success(
            t("accessToken.updateSuccess", {
              input:
                t("accessToken.table.token") +
                " " +
                t("accessToken.table.enabled")
            })
          );
          await getAllAccessTokens();
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
};
</script>

<style scoped lang="sass">
.sticky-header
 /* height or max-height is important */
 max-height: 85vh
 overflow-y: auto
 .q-table__top,
 .q-table__bottom,
 thead tr:first-child th
   /* bg color is important for th; just specify one */
 background-color: #fff
 thead tr th
   position: sticky
   z-index: 1
 thead tr:first-child th
   top: 0

 /* this is when the loading indicator appears */
 &.q-table--loading thead tr:last-child th
   /* height of all previous header rows */
   top: 48px

.col
 margin-left: 10px
 margin-right: 10px
</style>
