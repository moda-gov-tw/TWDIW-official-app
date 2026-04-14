<template>
  <q-table
    class="sticky-header"
    :dense="true"
    flat
    :hide-header="loading"
    :rows="roles"
    :columns="columns"
    :visible-columns="visibleColumns"
    row-key="id"
    v-model:selected="selected"
    v-model:pagination="tablePage"
    @request="onRequest"
    :loading="loading"
    :rows-per-page-options="[10, 20, 50, 0]"
    binary-state-sort
  >
    <!-- 設定 q-table 上方 -->
    <template #top>
      <div class="row q-ma-none q-pa-none items-center">
        <p class="titleRwd q-mb-md">{{ t("title.roleManagement") }}</p>
        <q-space></q-space>
        <div class="q-px-sm"></div>
      </div>
      <div class="row full-width justify-between">
        <table-dropdown
          ref="dropdown"
          :label="searchQuery || t('filterCondition')"
        >
          <form-wrapper
            v-model="searchModel"
            :fields="fields"
            @confirm="onConfirm"
            @reset="onResetSearch"
            :layout="[2, 1]"
          >
          </form-wrapper>
        </table-dropdown>
        <div :class="$q.screen.lt.md ? 'col-12 q-mt-md row' : 'col-auto'">
          <role-create @created="refreshTable" />
        </div>
      </div>
    </template>
    <template v-slot:body-cell-state="props">
      <q-td style="width: 100px; min-width: 100px">
        <template v-if="props.value === '0'">
          <div class="row items-center">
            <q-badge
              color="disable"
              class="q-pa-sm text-weight-bold"
              size="md"
              :label="t('disabled')"
            />
            <div class="q-pr-sm"></div>
            <q-btn
              dense
              size="sm"
              icon="play_arrow"
              @click="enableRole(props.row)"
            >
              <q-tooltip :delay="700">{{ t("role.enabledRoles") }}</q-tooltip>
            </q-btn>
          </div>
        </template>
        <template v-else-if="props.value === '1'">
          <div class="row items-center">
            <q-badge
              color="enable"
              class="q-pa-sm text-weight-bold"
              :label="t('enabled')"
            />
            <div class="q-pr-sm"></div>
            <q-btn
              dense
              size="sm"
              icon="block"
              @click="disableRole(props.row)"
              :disable="
                props.row.roleId === 'default_role' ||
                props.row.roleId === 'issuer_admin' ||
                props.row.roleId === 'verify_admin'
              "
            >
              <q-tooltip :delay="700">{{ t("role.disabledRoles") }}</q-tooltip>
            </q-btn>
          </div>
        </template>
        <span v-else>
          {{ props.value }}
        </span>
      </q-td>
    </template>
    <template v-slot:body-cell-detail="props">
      <q-td :props="props" style="width: 50px">
        <q-btn
          color="action"
          dense
          icon="search"
          @click="openDetail(props.row)"
        ></q-btn>
      </q-td>
    </template>
    <template v-slot:body-cell-edit="props">
      <q-td :props="props" style="width: 50px">
        <q-btn
          color="action"
          dense
          icon="edit"
          @click="openRoleEdit(props.row)"
        />
      </q-td>
    </template>
    <template v-slot:body-cell-editFunc="props">
      <q-td :props="props" style="width: 50px">
        <q-btn
          color="action"
          dense
          icon="checklist"
          @click="onResClick(props.row)"
        ></q-btn>
      </q-td>
    </template>
    <template v-slot:body-cell-delete="props">
      <q-td :props="props" style="width: 50px">
        <q-btn
          :disable="
            props.row.roleId === 'default_role' ||
            props.row.roleId === 'issuer_admin' ||
            props.row.roleId === 'verify_admin'
          "
          color="action"
          dense
          icon="delete"
          @click="onDelete(props.row)"
        ></q-btn>
      </q-td>
    </template>
    <template v-slot:loading>
      <q-inner-loading showing color="primary" />
    </template>
    <template #pagination="scope">
      <table-pagination :scope="scope" />
    </template>
    <template v-slot:no-data>
      <div class="full-width row flex-center q-gutter-sm">
        <span>{{ t("role.noData") }}</span>
      </div>
    </template>
  </q-table>
  <RoleResource
    v-model="isOpenDialog"
    @res-click="onResClick"
    ref="roleResource"
  />
</template>
<script setup>
import {
  ref,
  defineEmits,
  onMounted,
  onUnmounted,
  reactive,
  computed,
  defineExpose
} from "vue";
import { storeToRefs } from "pinia";
import { useNotify, useDialog } from "src/utils/plugin";
import { useRoleStore } from "stores/role";

import RoleResource from "./RoleResource.vue";
import RoleCreate from "./RoleCreate.vue";

import { useI18n } from "vue-i18n";
const { t } = useI18n();

//plugin settings
const $n = useNotify();
const $d = useDialog();

//dropdown refs
const dropdown = ref(null);

const searchQuery = ref("");

//receipt store settings
const store = useRoleStore();
const {
  roles,
  selected: storeSelected,
  roleResList,
  tablePage,
  roleDetailOpen,
  roleEditOpen,
  loading
} = storeToRefs(store);
const { searchRoles, selectRole, updateState, deleteRole, searchResTree } =
  store;

//欄位設定
const columns = [
  {
    name: "roleId",
    label: t("role.code"),
    sortable: true,
    field: "roleId",
    align: "left"
  },
  {
    name: "roleName",
    label: t("role.name"),
    sortable: true,
    field: "roleName",
    align: "left"
  },
  {
    name: "description",
    label: t("role.desc"),
    sortable: true,
    field: "description",
    align: "left"
  },
  {
    name: "state",
    label: t("role.enabled"),
    sortable: true,
    field: "state",
    align: "left"
  },
  {
    name: "createTime",
    label: t("role.createTime"),
    sortable: true,
    field: "createTime",
    align: "left",
    visible: false
  },
  {
    name: "detail",
    label: t("view"),
    align: "center"
  },
  {
    name: "edit",
    label: t("edit"),
    align: "center"
  },
  {
    name: "editFunc",
    label: t("role.actions.giveResource"),
    align: "center"
  },
  {
    name: "delete",
    label: t("delete"),
    align: "center"
  }
];

//可見欄位
const visibleColumns = computed(() => {
  return columns
    .filter((col) => {
      return col.visible !== false;
    })
    .map((col) => col.name);
});

const roleResource = ref(null);
//操作(動作)
let isOpenDialog = ref(false);

const onResClick = async (row) => {
  isOpenDialog.value = true;
  selectRole(row);
  await searchResTree(row);

  roleResource.value.leftExpandAll();
  roleResource.value.copySelectedNodes();
};

//table refs
const selected = ref([]);

const emit = defineEmits(["actionClick"]);

//搜尋相關內容
const onRequest = (props) => {
  const pagination = props.pagination;
  searchRoles(searchModel, pagination, null);
};

const searchModel = reactive({});
const fields = ref([
  {
    name: "roleId",
    label: t("role.code"),
    inputType: "input",
    field: "roleId.contains",
    outlined: true,
    rules: [
      (val) =>
        !val ||
        /^[a-zA-Z0-9_$@#&]+$/.test(val) ||
        t("validation.onlyEnNumAllowedAnd", { symbols: "_$@#&" })
    ]
  },
  {
    name: "roleName",
    label: t("role.name"),
    inputType: "input",
    field: "roleName.contains",
    outlined: true,
    rules: [
      (val) =>
        !val ||
        /^[\u4E00-\u9FFF\u3400-\u4DBFa-zA-Z0-9_$@#&]+$/.test(val) ||
        t("validation.onlyZhEnNumAllowedAnd", { symbols: "_$@#&" })
    ]
  },
  {
    name: "state",
    label: t("role.enabled"),
    inputType: "select",
    field: "state.equals",
    options: [
      { label: t("enabled"), value: 1 },
      { label: t("disabled"), value: 0 }
    ],
    outlined: true,
    emitValue: true
  }
]);

const onConfirm = (model) => {
  const pageRequest = { ...tablePage.value, page: 1, rowsPerPage: 10 };
  searchRoles(model.value, pageRequest, null);
  dropdown.value.hide();

  if (model.value) {
    if (model.value["roleId.contains"]) {
      searchQuery.value = model.value["roleId.contains"];
    } else if (model.value["roleName.contains"]) {
      searchQuery.value = model.value["roleName.contains"];
    } else if (
      model.value["state.equals"] !== null &&
      model.value["state.equals"] !== undefined
    ) {
      if (model.value["state.equals"] === 1) {
        searchQuery.value = t("enabled");
      } else if (model.value["state.equals"] === 0)
        searchQuery.value = t("disabled");
    } else {
      searchQuery.value = null;
    }
  }
};

const onResetSearch = (model) => {
  Object.keys(searchModel).forEach((key) => {
    searchModel[key] = null;
  });
  const pageRequest = { ...tablePage.value, page: 1, rowsPerPage: 10 };
  searchRoles(model.value, pageRequest, null);
  searchQuery.value = null;
};

const openDetail = (row) => {
  selectRole(row).then(() => {
    roleDetailOpen.value = true;
  });
};

const openRoleEdit = (row) => {
  selectRole(row).then(() => {
    roleEditOpen.value = true;
  });
};

const disableRole = (row) => {
  const action = "disabled";
  const name = row.roleName;

  $d.confirm(t("confirm"), confirmMessage(action, name), async () => {
    const isUpdateOK = await updateState(row);
    if (isUpdateOK.isUpdateOK) {
      notifySuccess(action, name);
      refreshTable();
    }
  });
};

const enableRole = (row) => {
  const action = "enabled";
  const name = row.roleName;

  $d.confirm(t("confirm"), confirmMessage(action, name), async () => {
    const isUpdateOK = await updateState(row);
    if (isUpdateOK.isUpdateOK) {
      notifySuccess(action, name);
      refreshTable();
    }
  });
};

const onDelete = (row) => {
  const action = "delete";
  const name = row.roleName;

  $d.confirm(t("confirm"), confirmMessage(action, name), async () => {
    await deleteRole(row);
    refreshTable();
  });
};

const confirmMessage = (action, name) => {
  return t("role.confirmAction", { action: t(action), name });
};

const notifySuccess = (action, name) => {
  const message = `${t("role.doAction", { action: t(action) })}，「${name}」。`;
  $n.success(message);
};

const refreshTable = () => {
  searchRoles(searchModel, tablePage.value, null);
};

onMounted(() => {
  roles.value = []; // 載入頁面後先清空角色清單
  storeSelected.value = []; // 載入頁面後先清空角色詳情
  roleResList.value = []; // 載入頁面後先清空角色功能清單
  searchRoles(null, tablePage.value, null);
});

onUnmounted(() => {
  roles.value = []; // 組件完全卸載後清空角色清單
  storeSelected.value = []; // 組件完全卸載後清空角色詳情
  roleResList.value = []; // 組件完全卸載後清空角色功能清單
});

defineExpose({ refreshTable });
</script>
