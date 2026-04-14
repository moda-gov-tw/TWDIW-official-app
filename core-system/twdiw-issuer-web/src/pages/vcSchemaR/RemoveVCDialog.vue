<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black relative-position"
      style="max-width: 1500px; width: 1000px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- VC 資料狀態管理 -->
        <div class="text-h6">{{ t("remove.btn.vcStatusChange") }}</div>
        <q-btn flat round color="white" icon="close" v-close-popup />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          style="height: 75vh; max-height: 100vh"
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
          <!-- 選擇操作 頁面 -->
          <q-card-section
            v-if="!show"
            :class="isMobile ? 'q-px-xs' : 'q-px-lg'"
            style="max-width: 1000px; margin: 0 auto"
          >
            <div :class="isMobile ? '' : 'row justify-between'">
              <div class="col-6 row text-h6-cus">
                <div
                  style="display: flex; align-items: start; min-width: 100px"
                >
                  {{ t("remove.vcStatus") }}
                </div>

                <q-select
                  outlined
                  class="col q-ml-md"
                  v-model="vcStatus"
                  :options="vcStatusOptions"
                  option-label="label"
                  option-value="value"
                  dense
                  behavior="menu"
                />
              </div>
              <div
                :class="['notice', isMobile ? 'col-12 q-mt-sm' : 'col-5']"
                style="min-height: 100px"
              >
                <div class="full-height column justify-around">
                  <div class="row items-center">
                    {{ t("remove.status.inactive") }}：
                    <div class="col row items-center">
                      {{ t("remove.rule.whenStatus") }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('0')"
                        :label="getStatusLabel('0')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.inactive")
                        })
                      }}
                    </div>
                  </div>

                  <div class="row items-center">
                    {{ t("remove.status.reuse") }}：
                    <div class="col row items-center">
                      {{ t("remove.rule.whenStatus") }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('1')"
                        :label="getStatusLabel('1')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.reuse")
                        })
                      }}
                    </div>
                  </div>

                  <div class="row items-center flex-wrap">
                    {{ t("remove.status.revoked") }}：
                    <div class="col row items-center">
                      {{ t("remove.rule.whenStatus") }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('0')"
                        :label="getStatusLabel('0')"
                        class="text-white"
                      />
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('1')"
                        :label="getStatusLabel('1')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.revoked")
                        })
                      }}，
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('3')"
                        :label="getStatusLabel('2')"
                        class="text-white"
                      />
                      {{ t("remove.notice.revokedDesc") }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="q-mt-xs">
              <p class="text-h6-cus text-grey-7 q-mb-xs">
                {{ t("remove.selectedVC") }}
              </p>
              <q-table
                :rows="transformRowData"
                :columns="isMobileColumns"
                dense
                flat
                class="sticky-header no-height"
                row-key="vcCid"
                selection="multiple"
                v-model:selected="selected"
                @update:selected="onSelect"
                :rows-per-page-options="[10, 20, 50, 0]"
              >
                <template v-slot:body-cell-transactionId="props">
                  <q-td :props="props">
                    <p>{{ "******" + props.row.transactionId.slice(-2) }}</p>
                  </q-td>
                </template>

                <template v-slot:body-cell-crDatetime="props">
                  <q-td :props="props">
                    {{ yyyyMMddHHmmss(props.row.crDatetime) }}
                  </q-td>
                </template>

                <template v-slot:body-cell-status="props">
                  <q-td :props="props">
                    <q-chip
                      square
                      size="sm"
                      :color="getStatusColor(props.row.status)"
                      :label="props.row.statusName"
                      class="text-white"
                    ></q-chip>
                  </q-td>
                </template>

                <template #pagination="scope">
                  <table-pagination
                    :scope="scope"
                    :rows-number="computedPagination"
                  />
                </template>
              </q-table>
            </div>
          </q-card-section>
          <!-- 確認 頁面 -->
          <q-card-section
            v-else
            :class="isMobile ? 'q-px-xs' : 'q-px-lg'"
            style="max-width: 1000px; margin: 0 auto"
          >
            <div
              v-if="executableList.length > 0 || nonExecutableList.length > 0"
            >
              <div :class="isMobile ? '' : 'row justify-between'">
                <div class="col-6 row text-h6-cus">
                  <div
                    style="display: flex; align-items: start; min-width: 100px"
                  >
                    {{ t("remove.vcStatus") }}
                  </div>

                  <span>：</span>
                  <div class="q-ml-sm alert-text">{{ vcStatus.label }}</div>
                </div>
                <div
                  :class="[
                    ' word-break',
                    isMobile ? 'col-12 q-mt-sm' : 'col-5'
                  ]"
                  :style="isMobile ? '' : 'min-height: 100px'"
                >
                  <div class="notice height-auto column justify-around">
                    <div class="row items-center" v-if="vcStatus.value === '1'">
                      {{ t("remove.status.inactive") }}：{{
                        t("remove.rule.whenStatus")
                      }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('0')"
                        :label="getStatusLabel('0')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.inactive")
                        })
                      }}
                    </div>

                    <div class="row items-center" v-if="vcStatus.value === '0'">
                      {{ t("remove.status.reuse") }}：{{
                        t("remove.rule.whenStatus")
                      }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('1')"
                        :label="getStatusLabel('1')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.reuse")
                        })
                      }}
                    </div>

                    <div class="row items-center" v-if="vcStatus.value === '2'">
                      {{ t("remove.status.revoked") }}：{{
                        t("remove.rule.whenStatus")
                      }}
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('0')"
                        :label="getStatusLabel('0')"
                        class="text-white"
                      />
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('1')"
                        :label="getStatusLabel('1')"
                        class="text-white"
                      />
                      {{
                        t("remove.rule.disable", {
                          action: t("remove.status.revoked")
                        })
                      }}，
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor('3')"
                        :label="getStatusLabel('2')"
                        class="text-white"
                      />
                      {{ t("remove.notice.revokedDesc") }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- 可執行 VC 資料 table -->
              <div class="q-mt-xs q-mb-md">
                <div class="col-auto text-h6-cus text-grey-7 q-mb-xs">
                  {{ t("remove.notice.executableListTitle") }}
                </div>

                <q-table
                  :rows="executableList"
                  :columns="isMobileColumns"
                  dense
                  flat
                  class="sticky-header q-mb-sm no-height"
                  row-key="vcCid"
                  :rows-per-page-options="[10, 20, 50, 0]"
                  :style="
                    nonExecutableList.length > 0
                      ? getTableStyle(executableList)
                      : {}
                  "
                >
                  <template v-slot:body-cell-transactionId="props">
                    <q-td :props="props">
                      <p>
                        {{ "******" + props.row.transactionId.slice(-2) }}
                      </p>
                    </q-td>
                  </template>

                  <template v-slot:body-cell-crDatetime="props">
                    <q-td :props="props">
                      {{ yyyyMMddHHmmss(props.row.crDatetime) }}
                    </q-td>
                  </template>

                  <template v-slot:body-cell-status="props">
                    <q-td :props="props">
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor(props.row.status)"
                        :label="props.row.statusName"
                        class="text-white"
                      ></q-chip>
                    </q-td>
                  </template>

                  <template #pagination="scope">
                    <table-pagination
                      :scope="scope"
                      :rows-number="executableList.length"
                    />
                  </template>
                </q-table>
              </div>

              <!-- 不可執行 VC 資料 table -->
              <div
                v-if="nonExecutableList.length > 0"
                class="q-pa-sm nonExecutableBlock bg-grey-1"
              >
                <div class="text-h6-cus text-grey-7 q-mb-xs row items-center">
                  <q-icon name="warning" size="sm" class="q-mr-sm" />
                  {{ t("remove.notice.nonExecutableListTitle") }}
                </div>
                <q-table
                  :rows="nonExecutableList"
                  :columns="isMobileColumns"
                  dense
                  flat
                  class="sticky-header no-height"
                  row-key="vcCid"
                  :rows-per-page-options="[10, 20, 50, 0]"
                  :style="getTableStyle(nonExecutableList)"
                >
                  <template v-slot:body-cell-transactionId="props">
                    <q-td :props="props">
                      <p>
                        {{ "******" + props.row.transactionId.slice(-2) }}
                      </p>
                    </q-td>
                  </template>

                  <template v-slot:body-cell-crDatetime="props">
                    <q-td :props="props">
                      {{ yyyyMMddHHmmss(props.row.crDatetime) }}
                    </q-td>
                  </template>

                  <template v-slot:body-cell-status="props">
                    <q-td :props="props">
                      <q-chip
                        square
                        size="sm"
                        :color="getStatusColor(props.row.status)"
                        :label="props.row.statusName"
                        class="text-white"
                      ></q-chip>
                    </q-td>
                  </template>

                  <template #pagination="scope">
                    <table-pagination
                      :scope="scope"
                      :rows-number="nonExecutableList.length"
                    />
                  </template>
                </q-table>
              </div>
            </div>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          :label="t(`remove.dialog.button.cancelStep.${show.toString()}`)"
          outline
          unelevated
          class="text-primary"
          size="16px"
          @click="onCancelClick"
          dense
          :style="{ width: '100px' }"
          :disable="removeLoading"
        />
        <q-btn
          v-if="!show"
          :label="t('nextStep')"
          color="primary"
          size="16px"
          dense
          unelevated
          @click="toggleShow"
          :style="{ width: '100px' }"
          :disable="removeLoading"
          :loading="removeLoading"
        />
        <q-btn
          v-else
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          unelevated
          :style="{ width: '100px' }"
          :disable="removeLoading"
          :loading="removeLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <q-dialog v-model="showNotice">
    <q-card class="q-pa-sm" style="width: 400px">
      <q-card-section class="text-h6 q-pb-none">
        {{ t("remove.notice.statusChange") }}
      </q-card-section>
      <q-card-section class="text-subtitle1 word-break">
        {{ noticeMsg }}
      </q-card-section>
      <q-card-actions align="right">
        <q-btn :label="t('confirm')" color="primary" @click="confirmCheck" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useVcSchemaStore } from "stores/vcSchema";
import { useRemoveVCStore } from "stores/removeVC";
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
const vcSchema = useVcSchemaStore();
const $q = useQuasar();
const $n = useNotify();
const { t } = useI18n();
const { resetVCSchemaDetail } = vcSchema;

const removeVCStore = useRemoveVCStore();
const { removeLoading } = storeToRefs(removeVCStore);
const { removeVC, getStatusColor, getStatusLabel } = removeVCStore;

import { format } from "utils/dateFormat";

const { yyyyMMddHHmmss } = format();

const props = defineProps({
  row: Array
});

const { row } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const show = ref(false);
const executableList = ref([]);
const nonExecutableList = ref([]);
const showNotice = ref(false);
const noticeMsg = ref(null);
const resultData = ref(null);

// 狀態清單
const vcStatusOptions = [
  { label: t("remove.status.reuse"), value: "0" },
  { label: t("remove.status.inactive"), value: "1" },
  { label: t("remove.status.revoked"), value: "2" }
];

const vcStatus = ref(vcStatusOptions[2]);

// 確認
const onOKClick = async () => {
  if (executableList.value.length > 0) {
    const removeVcCids = executableList.value.map((item) => ({
      encryptedVcCid: item.vcCid,
      action: vcStatus.value.value
    }));

    const result = await removeVC(removeVcCids);

    if (result.failList.length === 0) {
      $n.success(t("remove.success.statusChange"));

      noticeMsg.value = t("remove.success.successDetail", {
        count: removeVcCids.length
      });
      resultData.value = { success: true, removeVcCids };
      showNotice.value = true;
    } else {
      $n.error(t("remove.error.fail"));

      noticeMsg.value = t("remove.error.failDetail", { list: result.failList });
      resultData.value = { success: false };
      showNotice.value = true;
    }
  } else {
    $n.error(t("remove.error.fail"));
    noticeMsg.value = t("remove.error.noUpdatableData");
    resultData.value = { success: false };
    showNotice.value = true;
  }
};

// 通知的"確認"
const confirmCheck = () => {
  showNotice.value = false;

  onDialogOK(resultData.value);
  resultData.value = null;
};

const onCancelClick = () => {
  if (show.value) {
    show.value = false;
  } else {
    onDialogCancel();
  }
};

const onDialogHide = () => {
  resetVCSchemaDetail();
};

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const baseColumns = [
  {
    name: "serialNo",
    label: t("vcSchema.table.serialNo"),
    field: "serialNo",
    align: "left",
    sortable: true
  },
  {
    name: "name",
    label: t("vcSchema.table.name"),
    field: "name",
    align: "left",
    sortable: true
  },
  {
    name: "transactionId",
    label: t("remove.vcCidMask"),
    field: "transactionId",
    align: "left",
    sortable: true
  },
  {
    name: "crDatetime",
    label: t("remove.issuanceDate"),
    align: "center",
    field: "crDatetime",
    required: true,
    sortable: true
  },
  {
    name: "status",
    label: t("remove.status.label"),
    align: "center",
    field: "status",
    required: true,
    sortable: true
  }
];

const rowHeight = 48;
const maxVisibleRows = 6;

const getTableStyle = (list) => {
  const rowsCount = list.length;
  const height =
    rowsCount <= maxVisibleRows
      ? rowHeight * rowsCount
      : rowHeight * maxVisibleRows;

  return {
    maxHeight: rowsCount > maxVisibleRows ? `${height}px` : "",
    overflowY: rowsCount > maxVisibleRows ? "auto" : "hidden"
  };
};

const desktopStyle = {
  serialNo: "width: 240px; white-space: normal; word-break: break-word",
  name: "width: 240px; white-space: normal; word-break: break-word",
  transactionId: "width: 80px; white-space: normal; word-break: break-word",
  crDatetime: "width: 160px; white-space: normal; word-break: break-word",
  status: "width: 80px; white-space: normal; word-break: break-word"
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

// 下一步
const toggleShow = async () => {
  if (selected.value.length === 0) {
    $n.error(t("remove.error.noSelectVc"));
    return;
  }
  executableList.value = computedExecutableVC.value?.executable ?? [];
  nonExecutableList.value = computedExecutableVC.value?.nonExecutable ?? [];
  show.value = true;
};

const onDialogShow = () => {};

const transformRowData = computed(() => {
  if (!row.value) return [];

  return row.value.map((item) => ({
    serialNo: item.vcSerialNo,
    name: item.vcName,
    transactionId: item.vcCidMask,
    vcCid: item.vcCid,
    crDatetime: item.issuanceDate,
    status: item.status,
    statusName: item.statusName
  }));
});

const computedPagination = computed(() => {
  return transformRowData.value?.length || 0;
});

const selected = ref([...transformRowData.value]);

const onSelect = (val) => {
  selected.value = val.filter((item) => item.status !== "2");
};

// 判斷是否可狀態變更邏輯
const computedExecutableVC = computed(() => {
  if (!selected.value.length) return { executable: [], nonExecutable: [] };

  const executable = [];
  const nonExecutable = [];

  const type = vcStatus.value?.value;

  // item.status === 0 狀態"正常"
  // item.status === 1 狀態"停用"
  // item.status === 2 狀態"撤銷"
  for (const item of selected.value) {
    switch (type) {
      case "0": // 復用
        if (item.status === "1") {
          // 僅接受狀態"停用"
          executable.push(item);
        } else {
          nonExecutable.push(item);
        }
        break;

      case "1": // 停用
        if (item.status === "0") {
          // 僅接受狀態"正常"
          executable.push(item);
        } else {
          nonExecutable.push(item);
        }
        break;

      case "2": // 撤銷
        if (item.status === "0" || item.status === "1") {
          // 接受狀態"正常"、"停用"
          executable.push(item);
        } else {
          nonExecutable.push(item);
        }
        break;

      default:
        nonExecutable.push(item);
        break;
    }
  }

  return { executable, nonExecutable };
});
</script>
<style scoped>
.nonExecutableBlock {
  border-radius: 5px;
}

.notice {
  background-color: rgb(220, 220, 220);
  z-index: 10;
  border-radius: 5px;
  padding: 5px 10px;
}

.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
.height-auto {
  height: auto;
}
</style>
