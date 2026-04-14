<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 詳細資訊 -->
        <div class="text-h6">{{ t("remove.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#D33E5F',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <q-card-section style="max-width: 1200px; margin: 0 auto">
            <div class="row q-my-xs q-col-gutter-md">
              <div class="col-12 q-pt-none" :class="scheduleClass">
                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7"
                    >{{ t("vcSchema.info.name") }}：</span
                  >
                  <span class="text-h6-cus word-break">{{ row.vcName }}</span>
                </div>
                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7"
                    >{{ t("vcSchema.info.serialNo") }}：</span
                  >
                  <span class="text-h6-cus word-break">{{
                    row.vcSerialNo
                  }}</span>
                </div>

                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7"
                    >{{ t("remove.vcCidMask") }}(cid)：</span
                  >
                  <span class="text-h6-cus word-break">{{
                    row.vcCidMask
                  }}</span>
                </div>

                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("remove.dataTag") }}：
                  </span>
                  <span class="text-h6-cus word-break">
                    {{ row.dataTag ? row.dataTag : "無" }}
                  </span>
                </div>

                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("remove.transactionId") }}：
                  </span>
                  <span class="text-h6-cus word-break">
                    {{ row.transactionId }}
                  </span>
                </div>

                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7"
                    >{{ t("remove.issuanceDate") }}：</span
                  >
                  <span class="text-h6-cus word-break">{{
                    yyyyMMddHHmmss(row.issuanceDate)
                  }}</span>
                </div>
                <div :class="[isMobile ? 'column' : 'info-block']">
                  <span class="text-h6-cus text-grey-7"
                    >{{ t("remove.expiredDate") }}：</span
                  >
                  <span class="text-h6-cus word-break"
                    >{{
                      row.expiredDate
                        ? yyyyMMddHHmmss(row.expiredDate)
                        : t("remove.noExpiredDate")
                    }}
                    <span v-if="row.isExpired" class="q-ml-sm">
                      <q-chip
                        square
                        size="12px"
                        color="primary"
                        :label="t('remove.status.expired')"
                        text-color="white"
                      ></q-chip>
                    </span>
                  </span>
                </div>
              </div>
            </div>
            <q-table
              :rows="tableRows"
              :columns="isMobileColumns"
              dense
              flat
              :hide-header="detailLoading"
              class="sticky-header no-height"
              row-key="ename"
              :loading="detailLoading"
              :rows-per-page-options="[10, 20, 50, 0]"
              :hide-no-data="!detailLoading"
            >
              <template v-slot:body-cell-content="props">
                <q-td :props="props">
                  {{ props.row.content ? props.row.content : "-" }}
                </q-td>
              </template>
              <template v-slot:no-data="{ message }">
                <div class="full-width row flex-center q-gutter-sm">
                  <span>
                    {{ message }}
                  </span>
                </div>
              </template>

              <template v-slot:loading>
                <q-inner-loading showing color="primary" />
              </template>

              <template #pagination="scope">
                <table-pagination
                  :scope="scope"
                  :rows-number="computedPagination"
                />
              </template>
            </q-table>
            <div v-if="historyTableRows.length > 0" class="q-mt-sm">
              <div class="text-h6-cus text-grey-7 q-mt-xs">
                {{ t("remove.history") }}
              </div>
              <q-table
                :rows="computedHistoryPagination"
                :columns="historyColumns"
                row-key="id"
                dense
                flat
                :hide-header="detailLoading"
                class="sticky-header no-height"
                :loading="detailLoading"
                :rows-per-page-options="[10, 20, 50, 0]"
                :hide-no-data="!detailLoading"
                v-model:pagination="historyPagination"
                binary-state-sort
              >
                <template v-slot:body-cell-statusName="props">
                  <q-td :props="props">
                    <q-chip
                      square
                      size="12px"
                      :color="getStatusColor(props.row.status)"
                      :label="props.row.statusName"
                      text-color="white"
                    ></q-chip>
                  </q-td>
                </template>

                <template v-slot:body-cell-logDatetime="props">
                  <q-td :props="props">
                    {{ yyyyMMddHHmmss(props.row.logDatetime) }}
                  </q-td>
                </template>

                <template v-slot:loading>
                  <q-inner-loading showing color="primary" />
                </template>
              </q-table>
            </div>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useRemoveVCStore } from "stores/removeVC";
import { format } from "utils/dateFormat";
import { useI18n } from "vue-i18n";
const { yyyyMMddHHmmss } = format();
const $q = useQuasar();
const removeVCStore = useRemoveVCStore();
const { t } = useI18n();
const {
  removeDetailList,
  detailLoading,
  tableRows,
  historyTableRows,
  historyPagination
} = storeToRefs(removeVCStore);
const { removeVCDetail, resetRemoveDetailList, getStatusColor } = removeVCStore;

const props = defineProps({
  row: Object
});

const { row } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const onCancelClick = () => {
  // 如果有 vcCid 則回傳需要刷新
  if (removeDetailList.value.vcCid) {
    onDialogOK(true);
  } else {
    onDialogCancel();
  }
};
// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const baseColumns = [
  {
    name: "type",
    label: t("fields.type"),
    field: "type",
    align: "center",
    format: (val) => typeMethod(val),
    sortable: true
  },
  {
    name: "isRequired",
    label: t("fields.isRequired"),
    field: (row) =>
      row.isRequired ? t("fields.required") : t("fields.notRequired"),
    align: "center"
  },
  {
    name: "cname",
    label: t("fields.cname"),
    field: "cname",
    align: "center"
  },
  {
    name: "ename",
    label: t("fields.ename"),
    field: "ename",
    align: "center"
  }
];

const historyColumns = [
  {
    name: "originalIndex",
    label: "#",
    align: "center",
    field: "originalIndex",
    required: true
  },
  {
    name: "statusName",
    label: t("remove.status.label"),
    field: "statusName",
    align: "center",
    class: "col"
  },
  {
    name: "logDatetime",
    label: t("remove.logDatetime"),
    field: "logDatetime",
    align: "center",
    sortable: true,
    class: "col"
  }
];

const desktopStyle = {
  type: "width: 80px; white-space: normal; word-break: break-word",
  isRequired: "width: 150px; white-space: normal; word-break: break-word",
  cname: "width: 300px; white-space: normal; word-break: break-word",
  ename: "width: 300px; white-space: normal; word-break: break-word",
  content: "width: 300px; white-space: normal; word-break: break-word"
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

// 排序紀錄並加上序號
const computedHistoryPagination = computed(() => {
  const startIndex =
    (historyPagination.value.page - 1) * historyPagination.value.rowsPerPage;

  return historyTableRows.value.map((item, index) => ({
    ...item,
    originalIndex: startIndex + index + 1
  }));
});

const typeMethod = (type) => {
  if (type === "BASIC") {
    return t("vcSchema.select.basic");
  } else if (type === "NORMAL") {
    return t("vcSchema.select.normal");
  }
  return t("vcSchema.select.custom");
};

const onDialogShow = () => {
  if (row.value.vcCid) {
    removeVCDetail(row.value.vcCid);
  }
};

const onDialogHide = () => {
  resetRemoveDetailList();
};

const computedPagination = computed(() => {
  return tableRows.value?.length || 0;
});

const scheduleClass = computed(() => {
  return row.value.clearScheduleDatetime !== null ? "col-md-9" : "";
});
</script>
