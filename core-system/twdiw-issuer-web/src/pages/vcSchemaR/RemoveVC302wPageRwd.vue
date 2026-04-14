<template>
  <basic-page>
    <q-table
      dense
      flat
      class="sticky-header no-height"
      :rows="vcDataList"
      :columns="isMobileColumns"
      row-key="vcCid"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="onRequest"
      selection="multiple"
      binary-state-sort
      v-model:selected="selected"
      @update:selected="onSelect"
      :selected-rows-label="getSelectedString"
      :no-data-label="t('noData')"
      :no-results-label="t('noResult')"
      :rows-per-page-options="pagination.rowsPerPageOptions"
    >
      <template v-slot:top>
        <table-top-dropdown
          :title="title"
          :inputArray="inputArray"
          v-model="localSearchForm"
          @submit="onSubmitSearch"
          @reset="onResetSearch"
        >
          <div :class="isMobile ? 'col-12 q-mt-sm' : 'col-auto'">
            <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
              <div class="col row justify-center">
                <q-btn
                  outline
                  color="primary"
                  icon="info_outline"
                  :label="t('remove.btn.notice')"
                  class="q-mr-sm"
                  style="height: 40px"
                  @click="toggleMobileInfo"
                >
                  <q-popup-proxy
                    v-if="!isMobile"
                    anchor="bottom left"
                    self="bottom right"
                    :offset="[5, 0]"
                    class="bg-grey-2"
                    max-width="600px"
                  >
                    <div class="text-body2 notice">
                      <div class="row items-center">
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('0')"
                          :label="t('remove.status.normal')"
                          class="text-white q-mr-sm"
                        />
                        {{ t("remove.notice.normalDesc") }}
                      </div>

                      <div class="row items-center">
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('1')"
                          :label="t('remove.status.inactive')"
                          class="text-white q-mr-sm"
                        />
                        {{ t("remove.notice.inactiveDesc") }}
                      </div>

                      <div class="row items-center">
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('1')"
                          :label="t('remove.status.inactive')"
                          class="text-white q-mr-sm"
                        />
                        {{ t("remove.notice.inactiveToRevokedDesc") }}
                      </div>

                      <div class="row items-center">
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('2')"
                          :label="t('remove.status.revoked')"
                          class="text-white q-mr-sm"
                        />
                        {{ t("remove.notice.revokedDesc") }}
                      </div>

                      <div class="row items-center">
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('3')"
                          :label="t('remove.status.expired')"
                          class="text-white"
                        />
                        <q-chip
                          square
                          size="sm"
                          :color="getStatusColor('2')"
                          :label="t('remove.status.revoked')"
                          class="text-white q-mr-sm"
                        />
                        {{ t("remove.notice.expiredDesc") }}
                      </div>
                    </div>
                  </q-popup-proxy>
                </q-btn>
                <q-btn
                  unelevated
                  color="primary"
                  icon="list_alt"
                  :label="t('remove.btn.vcStatusChange')"
                  @click="deleteVC"
                  class="col"
                />
              </div>
            </div>
          </div>
        </table-top-dropdown>
      </template>

      <template v-slot:body-selection="props">
        <q-checkbox
          dense
          v-model="props.selected"
          :disable="props.row.status === '2'"
        />
      </template>

      <template v-slot:body-cell-expiredStatus="props">
        <q-td :props="props">
          <span v-if="props.row.isExpired">
            <q-chip
              square
              size="sm"
              color="primary"
              :label="t('remove.status.expired')"
              text-color="white"
            ></q-chip>
          </span>
        </q-td>
      </template>

      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <q-chip
            square
            size="sm"
            :color="getStatusColor(props.row.status)"
            :label="props.row.statusName"
            text-color="white"
          ></q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-details="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            unelevated
            dense
            icon="menu"
            @click="showVCInformationDialog(props.row)"
          ></q-btn>
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
  <!-- 手機版 info 資訊 -->
  <q-dialog v-model="showMobileInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("remove.notice.label") }}</div>
      </q-card-section>

      <q-card-section>
        <div class="row items-center">
          <q-chip
            square
            size="sm"
            :color="getStatusColor('0')"
            :label="t('remove.status.normal')"
            class="text-white q-mr-sm"
          />
          <div class="col text-wrap text-break">
            {{ t("remove.notice.normalDesc") }}
          </div>
        </div>

        <div class="row items-center">
          <q-chip
            square
            size="sm"
            :color="getStatusColor('1')"
            :label="t('remove.status.inactive')"
            class="text-white q-mr-sm"
          />
          <div class="col text-wrap text-break">
            {{ t("remove.notice.inactiveDesc") }}
          </div>
        </div>

        <div class="row items-center">
          <q-chip
            square
            size="sm"
            :color="getStatusColor('1')"
            :label="t('remove.status.inactive')"
            class="text-white q-mr-sm"
          />
          <div class="col text-wrap text-break">
            {{ t("remove.notice.inactiveToRevokedDesc") }}
          </div>
        </div>

        <div class="row items-center">
          <q-chip
            square
            size="sm"
            :color="getStatusColor('2')"
            :label="t('remove.status.revoked')"
            class="text-white q-mr-sm"
          />
          <div class="col text-wrap text-break">
            {{ t("remove.notice.revokedDesc") }}
          </div>
        </div>

        <div class="row items-center">
          <q-chip
            square
            size="sm"
            :color="getStatusColor('3')"
            :label="t('remove.status.expired')"
            class="text-white"
          />
          <q-chip
            square
            size="sm"
            :color="getStatusColor('2')"
            :label="t('remove.status.revoked')"
            class="text-white"
          />

          <!-- 右側文字 (靠上對齊) -->
          <div class="col q-ml-xs flex items-center text-wrap text-break">
            {{ t("remove.notice.expiredDesc") }}
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch, onMounted, computed, onBeforeUnmount } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import RemoveVCDataDetailDialog from "./RemoveVCDataDetailDialog.vue";
import RemoveVCDialog from "./RemoveVCDialog.vue";
import { format } from "utils/dateFormat";
import { QChip, useQuasar } from "quasar";
import { useRemoveVCStore } from "stores/removeVC";
import { storeToRefs } from "pinia";
import TableTopDropdown from "src/components/TableTopDropdown.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const { t } = useI18n();
const { yyyyMMddHHmmss } = format();
const $q = useQuasar();
const removeVCStore = useRemoveVCStore();
const {
  vcDataList,
  loading,
  pagination,
  orgList,
  orgVcItemList,
  loadingState,
  filter,
  defaultFormState
} = storeToRefs(removeVCStore);
const {
  getVCDataList,
  getOrgList,
  getOrgVcItemsList,
  resetList,
  applySearch,
  resetSearchForm,
  getStatusColor
} = removeVCStore;

// plugin settings
const $n = useNotify();
const $d = useDialog();

const title = t("remove.title");
const selected = ref([]);
const showMobileInfo = ref(false);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const baseColumns = [
  {
    name: "originalIndex",
    label: "#",
    align: "center",
    field: "originalIndex",
    required: true
  },
  {
    name: "vcSerialNo",
    label: t("vcSchema.table.serialNo"),
    align: "left",
    field: "vcSerialNo",
    required: true
  },
  {
    name: "vcName",
    label: t("vcSchema.table.name"),
    align: "left",
    field: "vcName",
    required: true
  },
  {
    name: "vcCidMask",
    label: t("remove.vcCidMask"),
    align: "center",
    field: "vcCidMask",
    required: true
  },
  {
    name: "dataTag",
    label: t("remove.dataTag"),
    align: "center",
    field: "dataTag",
    required: true
  },
  {
    name: "transactionId",
    label: t("remove.transactionId"),
    align: "center",
    field: "transactionId",
    required: true
  },
  {
    name: "issuanceDate",
    label: t("remove.issuanceDate"),
    align: "center",
    field: "issuanceDate",
    required: true,
    sortable: true,
    format: (val) => yyyyMMddHHmmss(val)
  },
  {
    name: "expiredStatus",
    label: t("remove.status.expired"),
    align: "center",
    field: "expiredStatus",
    required: true
  },
  {
    name: "status",
    label: t("remove.status.label"),
    align: "center",
    field: "status",
    required: true
  },
  {
    name: "details",
    label: t("remove.detail"),
    align: "center",
    field: "details",
    required: true
  }
];

const desktopStyle = {
  originalIndex: "width: 80px; white-space: normal; word-break: break-word",
  vcSerialNo: "width: 420px; white-space: normal; word-break: break-word",
  vcName: "width: 420px; white-space: normal; word-break: break-word",
  vcCid: "width: 120px; white-space: normal; word-break: break-word",
  details: "width: 120px; white-space: normal; word-break: break-word",
  issuanceDate: "width: 180px; white-space: normal; word-break: break-word",
  status: "width: 80px; white-space: normal; word-break: break-word",
  expiredStatus: "width: 80px; white-space: normal; word-break: break-word"
};

const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns;
  }

  return baseColumns.map((col) => ({
    ...col,
    style: desktopStyle[col.name] || col.style
  }));
});

async function onRequest(props) {
  if (skipRequest.value) {
    skipRequest.value = false;
    return;
  }

  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;

  if (sortBy === "issuanceDate") {
    // 更新 pagination 中的狀態
    pagination.value.descending = descending;
  }

  // // 調用更新後的 getFieldList
  await getVCDataList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    orgId: filter?.orgId,
    vcSerialNo: filter?.vcSerialNo,
    startDate: filter?.startDate,
    endDate: filter?.endDate,
    state: filter?.state,
    dataTag: filter?.dataTag,
    transactionId: filter?.transactionId
  });
}

const inputArray = computed(() => {
  // 初始化表單項陣列
  const formItems = [];

  if (orgList.value.length > 1) {
    // 多個機構時使用 select
    formItems.push({
      state: "select",
      col: "col-12",
      label: t("remove.orgId"),
      paramsName: "orgId",
      options: orgList.value,
      class: "",
      optionLabel: "orgTwName",
      optionValue: "orgId",
      rules: rulesAll()
    });
  } else {
    // 單個機構時使用 input
    formItems.push({
      state: "orgInput",
      col: "col-12",
      label: t("remove.orgId"),
      paramsName: "orgId",
      class: "",
      disable: true,
      value: orgList.value[0]?.orgTwName,
      defaultValue: {
        orgId: orgList.value[0]?.orgId,
        orgTwName: orgList.value[0]?.orgTwName
      },
      rules: rulesAll()
    });
  }

  // 其他表單項
  formItems.push(
    {
      // VC 序號選擇
      state: localSearchForm.value.orgId ? "select" : "input",
      label: t("vcSchema.table.serialNo"),
      paramsName: "vcSerialNo",
      options: filteredOptions.value,
      col: "col-12",
      loading: loadingState.value,
      disable: loadingState.value,
      emitValue: true,
      mapOptions: true,
      useInput: true,
      hideSelected: true,
      fillInput: true,
      filterFn: filterOptions,
      inputDebounce: 0,
      optionLabel: "vcItemSerialNo",
      optionValue: "vcItemSerialNo",
      rules: localSearchForm.value.orgId
        ? rulesAll()
        : rulesAll("vcSchemaSerialNoRules")
    },
    {
      state: "dateRange",
      paramsName: ["startDate", "endDate"],
      labels: {
        start: t("remove.qrcodeStartTime"),
        end: t("remove.qrcodeEndTime")
      },
      class: "q-mb-md",
      rules: rulesAll("dateRules")
    },
    {
      state: "select",
      label: t("remove.status.label"),
      paramsName: "state",
      col: "col-12",
      class: "q-mb-md",
      options: [
        {
          label: t("remove.status.normal"),
          value: 0
        },
        {
          label: t("remove.status.inactive"),
          value: 1
        },
        {
          label: t("remove.status.revoked"),
          value: 2
        }
      ]
    },
    {
      state: "input",
      col: "col-12",
      label: t("remove.dataTag"),
      paramsName: "dataTag",
      rules: rulesAll("dataTagRules")
    },
    {
      state: "input",
      col: "col-12",
      label: t("remove.transactionId"),
      paramsName: "transactionId",
      rules: rulesAll("transactionIdRules")
    }
  );

  return formItems;
});

const localSearchForm = ref({ ...defaultFormState });

const onSubmitSearch = (formData) => {
  applySearch(formData);
};

const onResetSearch = () => {
  resetSearchForm();
};

const skipRequest = ref(false);

const showVCInformationDialog = (row) => {
  $q.dialog({
    component: RemoveVCDataDetailDialog,
    componentProps: {
      row: row
    }
  })
    .onOk(() => {
      $q.loading.hide();
    })
    .onCancel(() => {
      console.log("Cancel");
    })
    .onDismiss(() => {
      console.log("Dismissed");
    });
};

const deleteVC = () => {
  if (selected.value.length === 0) {
    $n.error(t("remove.error.noSelectVc"));
    return;
  }

  cencelVCDialog(selected.value);
};

const cencelVCDialog = (row) => {
  $q.dialog({
    component: RemoveVCDialog,
    componentProps: {
      row: row
    }
  })
    .onOk((props) => {
      if (props.success) {
        selected.value = [];
        // 刷新列表
        getVCDataList(pagination.value.page, {
          size: pagination.value.rowsPerPage,
          sortBy: pagination.value.sortBy,
          descending: pagination.value.descending,
          ...filter.value
        });
      }
    })
    .onCancel(() => {
      console.log("cencel");
    })
    .onDismiss(() => {
      console.log("Dismissed");
    });
};

watch(
  () => localSearchForm.value.orgId,
  (newValue) => {
    if (newValue && (newValue.orgId || typeof newValue === "string")) {
      const orgId = typeof newValue === "object" ? newValue.orgId : newValue;
      getOrgVcItemsList(orgId);
      localSearchForm.value.vcSerialNo = "";
    }
  },
  { deep: true }
);

watch(
  () => orgVcItemList.value,
  (newValue) => {
    if (newValue) {
      filteredOptions.value = newValue;
    }
  }
);

const filteredOptions = ref([]);

const filterOptions = (val, update, fieldName) => {
  if (fieldName === "vcSerialNo") {
    localSearchForm.value.vcSerialNo = val;
  }

  // 繼續過濾邏輯
  if (val === "") {
    update(() => {
      filteredOptions.value = orgVcItemList.value;
    });
    return;
  }

  update(() => {
    const needle = val.toLowerCase();
    filteredOptions.value = orgVcItemList.value.filter(
      (v) =>
        v.vcItemName.toLowerCase().includes(needle) ||
        v.vcItemSerialNo.toLowerCase().includes(needle) ||
        v.orgId.toLowerCase().includes(needle) ||
        v.orgName.toLowerCase().includes(needle)
    );
  });
};

const getSelectedString = () => {
  return selected.value.length === 0
    ? ""
    : t("remove.selected", { count: selected.value.length });
};

function onSelect(val) {
  selected.value = val.filter((item) => item.status !== "2");
}

// info 資訊開關
const toggleMobileInfo = () => {
  if (isMobile.value) {
    showMobileInfo.value = !showMobileInfo.value;
  }
};

onMounted(async () => {
  try {
    await getVCDataList(1, {
      size: 10,
      sortBy: pagination.value.sortBy,
      descending: pagination.value.descending,
      filter: filter.value
    });

    await getOrgList();

    filteredOptions.value = orgVcItemList.value;
  } catch (error) {
    console.error(`${t("remove.error.init")}：${error}`);
  }
});

onBeforeUnmount(() => {
  resetSearchForm();
  resetList();
});
</script>

<style scoped>
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

.notice {
  background-color: rgb(213, 213, 213);
  padding: 5px;
}
</style>
