<template>
  <basic-page>
    <q-table
      :rows="scheduleList"
      :columns="columns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="loading"
      @request="onRequest"
      binary-state-sort
      dense
      flat
      class="sticky-header no-height"
      :rows-per-page-options="pagination.rowsPerPageOptions"
      :no-data-label="t('noData')"
      :no-results-label="t('noResult')"
    >
      <template v-slot:top>
        <!-- 定時清除資料排程 -->
        <div class="row q-ma-none q-pa-none items-center">
          <p class="titleRwd q-mb-md">{{ t("schedule.title") }}</p>
          <q-space></q-space>
          <div class="q-px-sm"></div>
        </div>
        <div class="row full-width justify-end items-end">
          <div :class="isMobile ? 'col-12 q-mt-md' : 'col-auto'">
            <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
              <q-btn
                unelevated
                color="primary"
                icon="add"
                :label="t('schedule.add')"
                @click="addSchedule"
                class="col"
              />
            </div>
          </div>
        </div>
      </template>

      <template v-slot:body-cell-lastClearDateInfo="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            unelevated
            color="indigo-4"
            dense
            icon="menu"
            @click="showClearDateInfoDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-delete="props">
        <q-td style="width: 50px" :props="props">
          <q-btn
            unelevated
            color="primary"
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
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import { format } from "utils/dateFormat";
import LastClearDateInfoDialog from "./LastClearDateInfoDialog.vue";
import AddScheduleDialog from "./AddScheduleDialog.vue";
import DeleteSceduleDialog from "./DeleteSceduleDialog.vue";

const { yyyyMMddHHmmss, type, formatScheduleInfo, convertTimeToLocal } =
  format();
const { t } = useI18n();

import { useQuasar } from "quasar";
import { useScheduleStore } from "stores/schedule";
import { storeToRefs } from "pinia";

const $q = useQuasar();
const scheduleStore = useScheduleStore();
const { loading, pagination, scheduleList } = storeToRefs(scheduleStore);
const { getScheduleList, createSchedule, deleteSchedule, resetScheduleList } =
  scheduleStore;

// plugin settings
const $n = useNotify();
const $d = useDialog();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const columns = ref([
  {
    name: "id",
    label: "#",
    align: "center",
    field: "id",
    required: true,
    sortable: true,
    style: "width: 100px"
  },
  {
    name: "type",
    label: t("schedule.table.type"),
    field: (row) => type(row.type),
    align: "center",
    style: "width: 80px"
  },
  {
    name: "scheduleInfo",
    label: t("schedule.table.scheduleInfo"),
    field: (row) => formatScheduleInfo(row),
    align: "center",
    style: "width: 240px"
  },
  {
    name: "time",
    label: t("schedule.table.time"),
    field: (row) => formatTime(row),
    align: "center"
  },
  {
    name: "lastRunDatetime",
    label: t("schedule.table.lastRunDatetime"),
    field: "lastRunDatetime",
    field: (row) =>
      row.lastRunDatetime ? yyyyMMddHHmmss(row.lastRunDatetime) : "-",
    align: "center"
  },
  {
    name: "lastClearDateInfo",
    label: t("schedule.table.lastClearDateInfo"),
    field: "lastClearDateInfo",
    align: "center"
  },
  {
    name: "delete",
    label: t("delete"),
    field: "delete",
    align: "center"
  }
]);

// 在表格中使用
const formatTime = (row) => {
  if (row.time && row.timezone) {
    return convertTimeToLocal(row.time, row.timezone);
  }
  return row.time;
};

const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  pagination.value = props.pagination;

  // 更新 store 中的分頁資料並獲取新的清單
  await getScheduleList(page, rowsPerPage, sortBy, descending);
};

const showClearDateInfoDialog = (row) => {
  $q.dialog({
    component: LastClearDateInfoDialog,
    componentProps: {
      row: row
    }
  });
};

const deleteRow = (row) => {
  $q.dialog({
    component: DeleteSceduleDialog,
    componentProps: {
      row: row
    }
  }).onOk(async (id) => {
    await deleteSchedule(id);
  });
};

const addSchedule = () => {
  $q.dialog({
    component: AddScheduleDialog
  }).onOk(async (props) => {
    // 收到的參數會有 null 值，需要過濾
    const filteredProps = Object.fromEntries(
      Object.entries(props).filter(([key, value]) => value !== null)
    );
    // 裡面如果有 month 是個字串陣列 需要轉換成字串
    if (filteredProps.month) {
      filteredProps.month = filteredProps.month.join(",");
    }

    await createSchedule(filteredProps);
  });
};

onMounted(() => {
  getScheduleList(1, 10);
});

onBeforeUnmount(() => {
  resetScheduleList();
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
</style>
