<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px; margin: 0 auto"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 最近一次 VC 清除資料 -->
        <div class="text-h6">{{ t("schedule.lastDelete") }}</div>
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
          <q-card-section>
            <div class="q-gutter-sm">
              <p class="text-h6 text-grey-7 info-block">
                {{ t("schedule.dialog.frequency") + "：" }}
                <span class="text-h6 text-black">
                  {{ type(scheduleDetail.type) }}
                </span>
              </p>
              <p class="text-h6 text-grey-7 info-block">
                {{ t("schedule.dialog.executionWeek") + "：" }}
                <span class="text-h6 text-black">
                  {{ formatScheduleInfo(scheduleDetail) }}
                </span>
              </p>
              <p class="text-h6 text-grey-7 info-block">
                {{ t("schedule.dialog.executionTime") + "：" }}
                <span class="text-h6 text-black">
                  {{ formatTime(scheduleDetail) }}
                </span>
              </p>
              <p class="q-mb-md text-h6 text-grey-7 info-block">
                {{ t("schedule.table.lastRunDatetime") + "：" }}
                <span class="text-h6 text-black">
                  {{
                    scheduleDetail.lastRunDatetime
                      ? yyyyMMddHHmmss(scheduleDetail.lastRunDatetime)
                      : "-"
                  }}
                </span>
              </p>
            </div>

            <q-table
              row-key="id"
              :hide-header="detailLoading"
              :rows="scheduleVCItemDataTable"
              :columns="isMobileColumns"
              binary-state-sort
              dense
              flat
              class="sticky-header no-height"
              :loading="detailLoading"
              :rows-per-page-options="detailPagination.rowsPerPageOptions"
              v-model:pagination="detailPagination"
              @request="onRequest"
              :hide-no-data="!detailLoading"
            >
              <template v-slot:body-cell-valid="props">
                <q-td class="text-center">
                  <q-chip
                    square
                    size="sm"
                    :color="
                      props.row.valid === 0
                        ? 'orange-7'
                        : props.row.valid === 2
                        ? 'negative'
                        : 'transparent'
                    "
                    :label="
                      props.row.valid === 0
                        ? t('schedule.valid')
                        : props.row.valid === 2
                        ? t('schedule.revocation')
                        : ''
                    "
                    class="text-white"
                    v-if="props.row.valid !== 301"
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
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useI18n } from "vue-i18n";
import { useScheduleStore } from "stores/schedule";
import { storeToRefs } from "pinia";
import { format } from "utils/dateFormat";
const { t } = useI18n();
const scheduleStore = useScheduleStore();
const {
  detailLoading,
  scheduleDetail,
  scheduleVCItemDataTable,
  detailPagination
} = storeToRefs(scheduleStore);
const { getListClearDateInfo, getScheduleVCItemDataTable, reset } =
  scheduleStore;

const { yyyyMMddHHmmss, type, formatScheduleInfo, convertTimeToLocal } =
  format();
const $q = useQuasar();
const show = ref(false);

const props = defineProps({
  row: Object
});

const { row } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {
  reset();
};

const onDialogShow = () => {
  if (row.value) {
    getListClearDateInfo(row.value.id);
    getScheduleVCItemDataTable(row.value.id);
  }
};

const baseColumns = [
  {
    name: "id",
    label: "#",
    align: "center",
    field: "id",
    required: true,
    sortable: true
  },
  {
    name: "serialNo",
    label: t("schedule.column.serialNo"),
    align: "center",
    field: (row) => row.vcItem.serialNo
  },
  {
    name: "vcItemName",
    label: t("schedule.column.vcItemName"),
    align: "center",
    field: (row) => row.vcItem.name
  },
  {
    name: "vcCid",
    label: t("schedule.column.vcCid"),
    align: "center",
    field: (row) => {
      return row.vcCid ? `******${row.vcCid.slice(-2)}` : "";
    }
  },
  {
    name: "crDatetime",
    label: t("schedule.column.crDatetime"),
    align: "center",
    field: (row) => yyyyMMddHHmmss(row.issuanceDate)
  },
  {
    name: "valid",
    label: t("schedule.column.valid"),
    align: "center",
    field: (row) => row.valid
  }
];

const desktopStyle = {
  id: "width: 100px;",
  serialNo: "width: 320px; white-space: normal; word-break: break-word",
  vcItemName: "width: 320px; white-space: normal; word-break: break-word",
  vcCid: "width: 120px;",
  crDatetime: "width: 120px;",
  valid: "width: 80px;"
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

const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;

  // 先更新 pagination
  detailPagination.value = {
    ...detailPagination.value,
    page,
    rowsPerPage,
    sortBy,
    descending
  };

  await getScheduleVCItemDataTable(row.value.id, page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending
  });
};

// 在表格中使用
const formatTime = (row) => {
  if (row.time && row.timezone) {
    return convertTimeToLocal(row.time, row.timezone);
  }
  return row.time;
};

const computedPagination = computed(() => {
  return detailPagination.value.rowsNumber;
});
</script>
