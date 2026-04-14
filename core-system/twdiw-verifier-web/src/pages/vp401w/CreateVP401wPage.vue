<template>
  <basic-page>
    <q-table
      :columns="columns"
      :rows="vpList"
      row-key="id"
      dense
      flat
      :filter="filter"
      @request="onRequest"
      class="sticky-header no-height"
      :hide-header="loading"
      :loading="loading"
      v-model:pagination="pagination"
      :rows-per-page-options="pagination.rowsPerPageOptions"
      binary-state-sort
    >
      <template v-slot:top>
        <div class="row q-ma-none q-pa-none items-center">
          <p class="titleRwd q-mb-md">{{ t("title.createVP") }}</p>
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
                        v-model.trim="searchForm.serialNo"
                        :label="t('vp.serialNo.label')"
                        outlined
                        class="q-mb-sm"
                        dense
                        hide-bottom-space
                        :rules="[
                          (val) =>
                            val.length <= 50 ||
                            t('validation.maxLength', { max: 50 }),
                          ,
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9$@_-]+$/.test(val) ||
                            t('vp.serialNo.pattern')
                        ]"
                        :style="{ height: '60px' }"
                      />

                      <q-input
                        v-model.trim="searchForm.name"
                        :label="t('vp.name.label')"
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
                            /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
                            t('vp.name.pattern')
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
                          @click="submitSearch"
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
                :disable="!verifierDID"
                :label="t('vp.btn.createVP')"
                @click="showVPDialog"
                class="col"
              >
                <q-tooltip
                  class="bg-orange-7 text-body2"
                  anchor="top right"
                  self="bottom right"
                  :offset="[0, 5]"
                  v-if="!verifierDID"
                >
                  {{ t("vp.createVPDisabled") }}
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

      <template v-slot:body-cell-owner="props">
        <q-td style="width: 80px" :props="props">
          <q-checkbox
            v-if="props.row.owner"
            size="sm"
            keep-color
            color="indigo-4"
            v-model="props.row.owner"
            :disable="true"
          />
        </q-td>
        <!-- checkDisable(props.row) -->
      </template>

      <template v-slot:body-cell-terms="props">
        <q-td :props="props" style="width: 50px">
          <!-- 當授權目的及條款都有值且 group_info 不為空，可編輯 VP 模板 -->
          <q-btn
            v-if="props.row.isShowEditIcon && props.row.isShowTermsIcon"
            unelevated
            dense
            icon="edit"
            color="indigo-4"
            @click="showVPDialogForEdit(props.row)"
          ></q-btn>

          <!-- 當授權目的及條款都有值但 group_info 為空，僅能編輯條款 -->
          <q-btn
            v-if="!props.row.isShowEditIcon && props.row.isShowTermsIcon"
            unelevated
            dense
            icon="description"
            color="indigo-4"
            @click="showTermsDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-detail="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            unelevated
            dense
            icon="menu"
            color="indigo-4"
            @click="showVPInformationDialog(props.row)"
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
  <q-dialog v-model="showDeleteDialog" persistent>
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.dialog.title.confirm") }}</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <div class="text-content">
          {{ t("vp.dialog.deleteVP", { serialNo: currentRow?.serialNo }) }}
        </div>
      </q-card-section>

      <q-card-actions align="right" class="text-primary">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          v-close-popup
          @click="onDeleteCancel"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          v-close-popup
          @click="onDeleteOk"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useVpStore } from "stores/vp";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";

import { useQuasar } from "quasar";
import { format } from "utils/dateFormat";
import CreateVPDialog from "./CreateVPDialog.vue";
import VPInformationDialog from "./VPInformationDialog.vue";
import AddUserTermsDialog from "./AddUserTermsDialog.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const { yyyyMMddHHmmss } = format();
const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();

const vpStore = useVpStore();
const { vpList, loading, pagination, currentFilter, vpItemData, verifierDID } =
  storeToRefs(vpStore);
const {
  getVpList,
  getModelTypeSelect,
  deleteVp,
  resetPagination,
  getVpDetail,
  getVpTerms,
  getStaticQRCode
} = vpStore;

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

const columns = ref([
  {
    name: "name",
    label: t("vp.name.label"),
    align: "left",
    field: "name",
    sortable: true,
    style: computed(() => ({
      width: "300px",
      ...(isMobile.value
        ? {}
        : { "white-space": "normal", "word-break": "break-word" })
    }))
  },
  {
    name: "serialNo",
    label: t("vp.serialNo.label"),
    align: "left",
    field: "serialNo",
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
    name: "owner",
    label: t("owner"),
    align: "center",
    field: "owner",
    style: "width: 80px;"
  },
  {
    name: "terms",
    label: t("edit"),
    align: "center",
    field: "terms",
    style: "width: 80px;"
  },
  {
    name: "detail",
    label: t("vp.detail"),
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

  await getVpList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    serialNo: filter?.serialNo,
    name: filter?.name,
    startDate: filter?.startDate,
    endDate: filter?.endDate
  });
};

const currentRow = ref(null);
const showDeleteDialog = ref(false);

const deleteRow = (row) => {
  currentRow.value = row;
  showDeleteDialog.value = true;
};

const onDeleteOk = async () => {
  try {
    await deleteVp(currentRow.value);
    showDeleteDialog.value = false;
  } catch (error) {
    console.log(error);
  }
};

const onDeleteCancel = () => {
  showDeleteDialog.value = false;
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  serialNo: "",
  name: "",
  startDate: "",
  endDate: ""
});

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
      searchForm.value.serialNo ||
      searchForm.value.name ||
      (searchForm.value.startDate && searchForm.value.endDate
        ? t("dateField.searchQuery")
        : "");

    const newFilter = {
      serialNo: searchForm.value.serialNo,
      name: searchForm.value.name,
      startDate: searchForm.value.startDate,
      endDate: searchForm.value.endDate
    };

    // 同時更新本地和 store 的篩選條件
    filter.value = newFilter;
    currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

function resetForm() {
  searchForm.value = {
    serialNo: "",
    name: "",
    startDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  currentFilter.value = {
    serialNo: "",
    name: "",
    startDate: "",
    endDate: ""
  };
}

const showVPDialog = () => {
  $q.dialog({
    component: CreateVPDialog
  }).onOk((data) => {
    console.log("ok");
    showVPInformationDialog(data);
    refreshList();
  });
};

// 詳細資料 Dialog
const showVPInformationDialog = async (rowData) => {
  console.log("ok");

  let qrCode = "";
  let staticQRCode = false;
  // 使用靜態 QR Code 模式
  if (rowData.model.value === "1") {
    if (rowData.businessId && rowData.serialNo) {
      // 取得靜態 QR Code
      qrCode = await getStaticQRCode(
        `${rowData.businessId}_${rowData.serialNo}`
      );
      staticQRCode = true;
    }
  }

  // 開啟 VP 詳細資料頁面
  $q.dialog({
    component: VPInformationDialog,
    componentProps: {
      row: rowData,
      staticQRCode: staticQRCode,
      qrCode: qrCode
    }
  })
    .onOk(() => {})
    .onCancel(() => {});
};

const showVPDialogForEdit = async (rowData) => {
  loading.value = true;
  try {
    await getVpDetail(rowData.id);
    $q.dialog({
      component: CreateVPDialog,
      componentProps: {
        isEdit: true,
        row: {
          ...rowData,
          vpItemData: vpItemData.value
        }
      }
    })
      .onOk((data) => {
        showVPInformationDialog(data);
        refreshList();
      })
      .onCancel(() => {});
  } catch (error) {
    console.error("Error showVPDialogForEdit:", error);
  } finally {
    loading.value = false;
  }
};

const showTermsDialog = async (rowData) => {
  loading.value = true;
  try {
    const response = await getVpTerms(rowData.serialNo, rowData.businessId);

    $q.dialog({
      component: AddUserTermsDialog,
      componentProps: {
        mode: "edit",
        id: rowData.id,
        serialNo: rowData.serialNo,
        terms: response
      }
    })
      .onOk(() => {})
      .onCancel(() => {});
  } catch (error) {
    console.error("Error showTermsDialog:", error);
  } finally {
    loading.value = false;
  }
};

const refreshList = () => {
  filter.value = null;
  resetForm();
  getList();
  resetPagination();
};

const getList = () => {
  getVpList(pagination.value.page, {
    size: 10,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    serialNo: currentFilter.value.serialNo,
    name: currentFilter.value.name,
    startDate: currentFilter.value.startDate,
    endDate: currentFilter.value.endDate
  });
};

// 設置裝置類型的 ref
const deviceType = ref(null);

const checkDevice = () => {
  const userAgent = navigator.userAgent.toLowerCase();

  return {
    isIOS: /iphone|ipad|ipod/.test(userAgent),
    isAndroid: /android/.test(userAgent)
  };
};

onMounted(() => {
  getModelTypeSelect();
  refreshList();

  const { isAndroid, isIOS } = checkDevice();

  if (isIOS) {
    deviceType.value = "ios";
  } else if (isAndroid) {
    deviceType.value = "android";
  } else {
    deviceType.value = "other";
  }
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
