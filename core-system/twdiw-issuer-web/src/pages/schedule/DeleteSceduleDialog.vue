<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1000px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 刪除 VC 清除資料排程 -->
        <div class="text-h6">{{ t("schedule.delete") }}</div>
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
            <p class="text-primary" :class="isMobile ? 'text-h5' : 'text-h4'">
              {{ t("schedule.notice.recover") }}
            </p>
            <p class="text-h5">'">{{ t("schedule.notice.searchRecover") }}</p>
            <q-table
              :rows="[row]"
              :columns="responseColumns"
              dense
              flat
              class="sticky-header q-my-md"
              row-key="vcCid"
              hide-pagination
            >
              <template v-slot:body-cell-lastRunDatetime="props">
                <q-td :props="props">
                  {{
                    props.row.lastRunDatetime
                      ? yyyyMMddHHmmss(props.row.lastRunDatetime)
                      : "-"
                  }}
                </q-td>
              </template>
            </q-table>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          unelevated
          :label="cancel"
          outline
          class="text-primary"
          size="16px"
          @click="onCancelClick"
          dense
          :style="{ width: '100px' }"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useI18n } from "vue-i18n";
import { format } from "utils/dateFormat";

const { t } = useI18n();
const { yyyyMMddHHmmss, type, formatScheduleInfo } = format();
const $q = useQuasar();
const props = defineProps({
  row: Object
});

const { row } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const onOKClick = () => {
  onDialogOK(row.value.id);
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {};

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const responseColumns = [
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
    field: "time",
    align: "center"
  },
  {
    name: "lastRunDatetime",
    label: t("schedule.table.lastRunDatetime"),
    field: "lastRunDatetime",
    align: "center"
  }
];

const onDialogShow = () => {};
</script>
