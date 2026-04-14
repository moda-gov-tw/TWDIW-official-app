<template>
  <div class="q-pa-md">
    <q-table
      :rows="funcs"
      :columns="columns"
      row-key="id"
      dense
      flat
      :hide-header="loading"
      class="sticky-header"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="onRequest"
      binary-state-sort
      :rows-per-page-options="pagination.rowsPerPageOptions"
    >
      <template v-slot:top>
        <div class="row full-width justify-between items-end q-pb-sm">
          <div class="col-grow">
            <div class="titleRwd q-my-md">{{ t("func.title") }}</div>
            <div class="row full-width justify-between items-end">
              <q-btn-dropdown
                color="white"
                text-color="black"
                class="search-dropdown ellipsis-label"
                flat
                dropdown-icon="none"
                rounded
                :style="dropdownStyle"
                :menu-offset="menuOffset"
                ref="dropdownRef"
              >
                <template v-slot:label>
                  <div class="ellipsis-wrapper">
                    <q-icon name="search" class="q-mr-sm" />
                    <span class="ellipsis-text">{{
                      searchQuery || t("filterCondition")
                    }}</span>
                  </div>
                </template>
                <q-form ref="searchFormRef" @submit="submitSearch">
                  <q-card>
                    <q-card-section>
                      <q-input
                        v-model="searchForm.resId"
                        :label="t('func.table.code')"
                        outlined
                        class="q-mb-sm"
                        dense
                        hide-bottom-space
                        :rules="[
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9]+$/.test(val) ||
                            t('validation.onlyEnNumAllowed')
                        ]"
                        :style="{ height: '60px' }"
                      />
                      <q-input
                        v-model="searchForm.resName"
                        :label="t('func.table.name')"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :rules="[
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
                            t('validation.onlyZhEnNumAllowed')
                        ]"
                        :style="{ height: '60px' }"
                      />
                      <q-select
                        v-model="searchForm.typeId"
                        :label="t('func.table.funcType')"
                        :options="[
                          { label: 'NODE', value: 'node' },
                          { label: 'WEB', value: 'web' }
                        ]"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :style="{ height: '60px' }"
                      />
                      <q-select
                        v-model="searchForm.state"
                        :label="t('func.table.enabled')"
                        :options="[
                          { label: t('enabled'), value: '1' },
                          { label: t('disabled'), value: '0' }
                        ]"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :style="{ height: '60px' }"
                      />
                      <div class="row justify-end q-gutter-sm">
                        <q-btn
                          :label="t('reset')"
                          class="text-primary"
                          outline
                          @click="resetForm"
                        />
                        <q-btn
                          unelevated
                          :label="t('filter')"
                          color="primary"
                          type="submit"
                          :disable="loading"
                          :loading="loading"
                        />
                      </div>
                    </q-card-section>
                  </q-card>
                </q-form>
              </q-btn-dropdown>
            </div>
          </div>
        </div>
      </template>

      <!-- 啟用狀態 -->
      <template v-slot:body-cell-state="props">
        <q-td style="width: 100px; min-width: 100px">
          <div class="row items-center">
            <q-badge
              :color="props.value === '0' ? 'grey-3' : 'positive'"
              class="q-pa-sm text-weight-bold"
              :label="props.value === '0' ? t('disabled') : t('enabled')"
            />
            <div class="q-pr-sm"></div>
            <q-btn
              dense
              size="sm"
              :icon="props.value === '0' ? 'play_arrow' : 'block'"
              @click="handleStateToggle(props.row)"
            >
              <q-tooltip :delay="700">
                {{
                  props.value === "0" ? t("func.enabled") : t("func.disabled")
                }}
              </q-tooltip>
            </q-btn>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-information="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            icon="search"
            color="indigo-4"
            dense
            @click="showFuncInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template #pagination="scope">
        <table-pagination :scope="scope" />
      </template>

      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:no-data="{ message }">
        <div class="full-width row flex-center q-gutter-sm">
          <span>
            {{ message }}
          </span>
        </div>
      </template>
    </q-table>
    <FuncInformationDialog></FuncInformationDialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import { useQuasar } from "quasar";
import FuncInformationDialog from "./FuncDetail.vue";

// store
const store = useFuncStore();
import { useFuncStore } from "stores/func";
import { storeToRefs } from "pinia";
const { funcs, loading, pagination, currentFilter, isOpenDetail } =
  storeToRefs(store);
const { selectFunc, getFuncs, updateState } = store;

// plugin settings
const $q = useQuasar();
const $n = useNotify();
const $d = useDialog();
const { t } = useI18n();
const filter = ref("");

const columns = [
  {
    name: "resGrp",
    label: t("func.table.res_group"),
    align: "left",
    field: "resGrp",
    sortable: true
  },
  {
    name: "resId",
    label: t("func.table.code"),
    sortable: true,
    field: "resId",
    align: "left"
  },
  {
    name: "resName",
    label: t("func.table.name"),
    sortable: true,
    field: "resName",
    align: "left"
  },
  {
    name: "apiUri",
    label: t("func.table.api_uri"),
    sortable: true,
    field: "apiUri",
    align: "left"
  },
  {
    name: "webUrl",
    label: t("func.table.web_url"),
    sortable: true,
    field: "webUrl",
    align: "left"
  },
  {
    name: "typeId",
    label: t("func.table.funcType"),
    sortable: true,
    field: "typeId",
    align: "left"
  },
  {
    name: "state",
    label: t("func.table.enabled"),
    field: "state",
    align: "center",
    sortable: true,
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  {
    name: "createTime",
    label: t("func.table.createTime"),
    sortable: true,
    field: "createTime",
    align: "left"
  },
  {
    name: "information",
    label: t("view"),
    field: "information",
    align: "center"
  }
];

const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: isMobile.value ? "100%" : "300px",
  maxWidth: "100%",
  textTransform: "none"
}));

const menuOffset = computed(() => {
  return isMobile.value ? [150, 8] : [0, 0]; // [x偏移, y偏移]
});

async function onRequest(props) {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;

  await getFuncs(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    resId: filter?.resId,
    resName: filter?.resName,
    typeId: filter?.typeId,
    state: filter?.state
  });
}

const showFuncInformationDialog = (row) => {
  selectFunc(row);
  isOpenDetail.value = true;
};

const handleStateToggle = async (row) => {
  try {
    loading.value = true;
    const action = row.state === "1" ? t("disabled") : t("enabled"); // 確認是啟用還是停用
    const confirmMessage = t("func.message.state", {
      state: action,
      field: row.resName
    }); // 根據 action 顯示正確的訊息

    $d.confirm(t("confirm"), confirmMessage, async () => {
      const { isUpdateStateOK } = await updateState(row);
      if (isUpdateStateOK) {
        $n.success(
          t("func.success.state", {
            state: action,
            field: row.resName
          })
        );
      } else {
        $n.error(t("func.error.permissionDenied.state"));
      }
    });
  } catch (error) {
    console.error("Error selecting function toggleState:", error);
  } finally {
    loading.value = false;
  }
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  resId: "",
  resName: "",
  typeId: "",
  state: ""
});

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (isValid) {
    // Update search display text
    if (searchForm.value.resId) {
      searchQuery.value = searchForm.value.resId;
    } else if (searchForm.value.resName) {
      searchQuery.value = searchForm.value.resName;
    } else if (searchForm.value.typeId?.value === "node") {
      searchQuery.value = "類型為node";
    } else if (searchForm.value.typeId?.value === "WEB") {
      searchQuery.value = "類型為web";
    } else if (searchForm.value.state?.value === "啟用") {
      searchQuery.value = "狀態為啟用";
    } else if (searchForm.value.state?.value === "停用") {
      searchQuery.value = "狀態為停用";
    } else {
      searchQuery.value = t("filterCondition");
    }

    const newFilter = {
      resId: searchForm.value.resId,
      resName: searchForm.value.resName,
      typeId: searchForm.value.typeId.value,
      state: searchForm.value.state.value
    };

    filter.value = newFilter;
    currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

function resetForm() {
  searchForm.value = {
    resId: "",
    resName: "",
    typeId: "",
    state: ""
  };
  searchQuery.value = "";
  filter.value = null;

  // 清空 store 中的搜索條件
  currentFilter.value = {
    resId: "",
    resName: "",
    typeId: "",
    state: ""
  };
}

onMounted(() => {
  getFuncs(1, {
    size: pagination.value.rowsPerPage,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    filter: filter.value
  });
});
</script>
<style scoped>
.ellipsis-label .ellipsis-wrapper {
  max-width: calc(100% - 24px);
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
}

.ellipsis-text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mobile-btns {
  width: 100%;
  display: flex;
  gap: 8px;
}

.mobile-btns .btn {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.desktop-btns {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
