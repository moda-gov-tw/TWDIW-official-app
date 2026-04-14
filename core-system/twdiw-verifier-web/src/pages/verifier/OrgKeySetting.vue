<template>
  <basic-page>
    <q-table
      :columns="columns"
      :rows="keyList"
      row-key="id"
      dense
      flat
      :filter="filter"
      @request="onRequest"
      class="sticky-header no-height"
      :loading="loading"
      v-model:pagination="pagination"
      :rows-per-page-options="pagination.rowsPerPageOptions"
      binary-state-sort
    >
      <template v-slot:top>
        <div class="row q-ma-none q-pa-none items-center">
          <p class="titleRwd q-mb-md">{{ t("title.orgKeySetting") }}</p>
          <q-space></q-space>
          <div class="q-px-sm"></div>
        </div>
        <div class="row full-width justify-between items-end">
          <div class="col-grow">
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
                <q-form
                  class="search-form"
                  ref="searchFormRef"
                  @submit="submitSearch"
                >
                  <q-card>
                    <q-card-section>
                      <q-input
                        v-model.trim="searchForm.keyId"
                        :label="t('orgKeySetting.keyId.label')"
                        outlined
                        class="q-mb-sm"
                        dense
                        hide-bottom-space
                        :rules="[
                          (val) =>
                            val.length <= 50 ||
                            t('validation.maxLength', { max: 50 }),
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9$@_-]+$/.test(val) ||
                            t('orgKeySetting.keyId.pattern')
                        ]"
                        :style="{ height: '60px' }"
                      />

                      <q-input
                        v-model.trim="searchForm.description"
                        :label="t('orgKeySetting.description.label')"
                        outlined
                        class="q-mb-sm"
                        dense
                        hide-bottom-space
                        :rules="[
                          (val) =>
                            val.length <= 18 ||
                            t('validation.maxLength', { max: 18 }),
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
                            t('orgKeySetting.description.pattern')
                        ]"
                        :style="{ height: '60px' }"
                      />
                      <div class="row q-col-gutter-md q-mb-md">
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.startDate"
                            :label="t('modifyTime.startDate')"
                            outlined
                            dense
                            type="datetime"
                            :rules="rulesAll('dateRules')"
                          >
                            <template v-slot:append>
                              <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy
                                  cover
                                  transition-show="scale"
                                  transition-hide="scale"
                                >
                                  <q-date
                                    v-model="searchForm.startDate"
                                    mask="YYYY-MM-DD"
                                    today-btn
                                    minimal
                                  >
                                    <div class="row items-center justify-end">
                                      <q-btn
                                        v-close-popup
                                        :label="t('close')"
                                        color="primary"
                                        flat
                                      />
                                    </div>
                                  </q-date>
                                </q-popup-proxy>
                              </q-icon>
                            </template>
                          </q-input>
                        </div>
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.endDate"
                            :label="t('modifyTime.endDate')"
                            outlined
                            dense
                            type="datetime"
                            :rules="rulesAll('dateRules')"
                          >
                            <template v-slot:append>
                              <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy
                                  cover
                                  transition-show="scale"
                                  transition-hide="scale"
                                >
                                  <q-date
                                    v-model="searchForm.endDate"
                                    mask="YYYY-MM-DD"
                                    minimal
                                    today-btn
                                  >
                                    <div class="row items-center justify-end">
                                      <q-btn
                                        v-close-popup
                                        :label="t('close')"
                                        color="primary"
                                        flat
                                      />
                                    </div>
                                  </q-date>
                                </q-popup-proxy>
                              </q-icon>
                            </template>
                          </q-input>
                        </div>
                      </div>

                      <div class="row justify-end q-gutter-sm">
                        <q-btn
                          :label="t('reset')"
                          outline
                          class="text-primary"
                          @click="resetForm"
                        />
                        <q-btn
                          :label="t('filter')"
                          unelevated
                          color="primary"
                          type="submit"
                        />
                      </div>
                    </q-card-section>
                  </q-card>
                </q-form>
              </q-btn-dropdown>
            </div>
          </div>

          <div :class="isMobile ? 'col-12 q-mt-md' : 'col-auto q-ml-md'">
            <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
              <q-btn
                unelevated
                color="primary"
                icon="add"
                :label="t('orgKeySetting.addKey')"
                @click="showCreateKeyDialog"
                class="col"
                :disable="!verifierDID"
              >
                <q-tooltip
                  class="bg-orange-7 text-body2"
                  anchor="top right"
                  self="bottom right"
                  :offset="[0, 5]"
                  v-if="!verifierDID"
                >
                  {{ t("orgKeySetting.didNotRegistered") }}
                </q-tooltip>
              </q-btn>
            </div>
          </div>
        </div>
      </template>

      <template v-slot:body-cell-upDatetime="props">
        <q-td :props="props">
          {{ yyyyMMddHHmmss(props.row.upDatetime) }}
        </q-td>
      </template>

      <template v-slot:body-cell-isActive="props">
        <q-td style="width: 80px" :props="props">
          <q-checkbox
            size="sm"
            keep-color
            color="indigo-4"
            :model-value="props.row.isActive"
            @update:model-value="confirmKeyActivation(props.row)"
            :disable="props.row.isActive"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-detail="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            unelevated
            dense
            icon="menu"
            color="indigo-4"
            @click="showKeyInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-delete="props">
        <q-td style="width: 50px" :props="props">
          <q-btn
            color="primary"
            unelevated
            dense
            icon="delete"
            @click="deleteRow(props.row)"
            :disable="props.row.isActive"
          />
        </q-td>
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

      <template #pagination="scope">
        <table-pagination :scope="scope" />
      </template>
    </q-table>
  </basic-page>

  <!-- 刪除金鑰 Dialog -->
  <q-dialog v-model="showDeleteDialog" persistent>
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("confirm") }}</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <div class="text-content">
          {{
            t("orgKeySetting.dialog.deleteMessage", {
              keyId: currentRow?.keyId
            })
          }}
        </div>
      </q-card-section>

      <q-card-actions align="right" class="text-primary">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          v-close-popup
          :disable="deleteLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          @click="onDeleteOk"
          :loading="deleteLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 啟用金鑰 Dialog -->
  <q-dialog v-model="showKeyActiveDialog" persistent>
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">
          {{ t("orgKeySetting.dialog.title.activateKey") }}
        </div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <div class="text-content">
          {{
            t("orgKeySetting.dialog.activate.message", {
              keyId: currentRow?.keyId
            })
          }}
        </div>
        <div class="text-content text-red text-bold">
          {{ t("orgKeySetting.dialog.activate.alert") }}
        </div>
      </q-card-section>

      <q-card-actions align="right" class="text-primary">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          v-close-popup
          :disable="setKeyActiveLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          @click="onActivateKeyConfirm"
          :loading="setKeyActiveLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useOrgKeySettingStore } from "src/stores/orgKeySetting";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useQuasar } from "quasar";
import { format } from "utils/dateFormat";
import CreateKeyDialog from "./CreateKeyDialog.vue";
import KeyInformationDialog from "./KeyInformationDialog.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const { yyyyMMddHHmmss } = format();

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();

const orgKeySettingStore = useOrgKeySettingStore();

const {
  keyList,
  loading,
  pagination,
  currentFilter,
  verifierDID,
  deleteLoading,
  setKeyActiveLoading
} = storeToRefs(orgKeySettingStore);
const { getKeyList, deleteKey, resetPagination, setKeyActive } =
  orgKeySettingStore;

const filter = ref("");

const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: isMobile.value ? "100%" : "300px",
  textTransform: "none",
  maxWidth: "100%"
}));

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const menuOffset = computed(() => {
  return isMobile.value ? [150, 8] : [300, 0]; // [x偏移, y偏移]
});

// 查詢當前金鑰筆數
const keyListCount = computed(() => keyList.value.length);

// table 欄位
const columns = ref([
  {
    name: "keyId",
    label: t("orgKeySetting.keyId.label"),
    align: "left",
    field: "keyId",
    sortable: true,
    style: computed(() => ({
      width: "400px",
      ...(isMobile.value
        ? {}
        : { "white-space": "normal", "word-break": "break-word" })
    }))
  },
  {
    name: "description",
    label: t("orgKeySetting.description.label"),
    align: "left",
    field: "description",
    sortable: true,
    style: computed(() => ({
      width: "300px",
      ...(isMobile.value
        ? {}
        : { "white-space": "normal", "word-break": "break-word" })
    }))
  },
  {
    name: "upDatetime",
    label: t("modifyTime.label"),
    align: "center",
    field: "upDatetime",
    sortable: true,
    style: "width: 120px;"
  },
  {
    name: "isActive",
    label: t("orgKeySetting.isActive"),
    align: "center",
    field: "isActive",
    style: "width: 80px;"
  },
  {
    name: "detail",
    label: t("orgKeySetting.detail"),
    align: "center",
    field: "detail",
    style: "width: 80px;"
  },
  {
    name: "delete",
    label: t("delete"),
    align: "center",
    field: "delete",
    style: "width: 80px;"
  }
]);

const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;

  await getKeyList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    keyId: filter?.keyId,
    description: filter?.description,
    startDate: filter?.startDate,
    endDate: filter?.endDate
  });
};

const currentRow = ref(null);
const showDeleteDialog = ref(false);
const showKeyActiveDialog = ref(false);

// 刪除金鑰
const deleteRow = (row) => {
  currentRow.value = row;
  showDeleteDialog.value = true;
};

const onDeleteOk = async () => {
  try {
    await deleteKey(currentRow.value);
    showDeleteDialog.value = false;
  } catch (error) {
    console.log(error);
  }
};

// 啟用金鑰
const confirmKeyActivation = (row) => {
  currentRow.value = row;
  showKeyActiveDialog.value = true;
};

const onActivateKeyConfirm = async () => {
  try {
    await setKeyActive(currentRow.value);
    showKeyActiveDialog.value = false;
  } catch (error) {
    console.log(error);
  }
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  keyId: "",
  description: "",
  startDate: "",
  endDate: ""
});

// 篩選 搜尋條件
const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (searchForm.value.startDate && searchForm.value.endDate) {
    const startDate = new Date(searchForm.value.startDate);
    const endDate = new Date(searchForm.value.endDate);

    // 判斷日期是否有效
    if (startDate > endDate) {
      $n.error(t("dateField.errors.startDateAfterEndDate"));
      return;
    }
  }

  if (isValid) {
    searchQuery.value =
      searchForm.value.keyId ||
      searchForm.value.description ||
      (searchForm.value.startDate && searchForm.value.endDate
        ? t("dateField.searchQuery")
        : "");

    const newFilter = {
      keyId: searchForm.value.keyId,
      description: searchForm.value.description,
      startDate: searchForm.value.startDate,
      endDate: searchForm.value.endDate
    };

    // 同時更新本地和 store 的篩選條件
    filter.value = newFilter;
    currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

// 更新金鑰 table
const refreshList = () => {
  filter.value = null;
  resetForm();
  resetPagination();
  getList();
};

// 查詢金鑰
const getList = () => {
  getKeyList(pagination.value.page, {
    size: 10,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    keyId: currentFilter.value.keyId,
    description: currentFilter.value.description,
    startDate: currentFilter.value.startDate,
    endDate: currentFilter.value.endDate
  });
};

// 重置搜尋條件
function resetForm() {
  searchForm.value = {
    keyId: "",
    description: "",
    startDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  currentFilter.value = {
    keyId: "",
    description: "",
    startDate: "",
    endDate: ""
  };
}

// 新增金鑰 Dialog
const showCreateKeyDialog = () => {
  $q.dialog({
    component: CreateKeyDialog,
    componentProps: {
      keyListCount: keyListCount.value
    }
  }).onOk((data) => {
    console.log("ok");
    refreshList();
  });
};

// 金鑰詳情 Dialog
const showKeyInformationDialog = (row) => {
  $q.dialog({
    component: KeyInformationDialog,
    componentProps: {
      row: row
    }
  });
};

onMounted(() => {
  refreshList();
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

.text-content {
  word-break: break-all;
  overflow-wrap: break-word;
  max-width: 100%;
  padding: 0 8px;
}
</style>
