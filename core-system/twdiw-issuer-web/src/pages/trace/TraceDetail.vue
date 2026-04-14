<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    v-model="isOpenDetail"
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 使用者操作軌跡詳情 -->
        <div class="text-h6">{{ t("trace.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <div class="column q-pa-sm q-gutter-sm">
            <template v-if="!selected">
              <q-img width="200px" :src="box" class="self-center"></q-img>
              <p class="text-h6 text-center">{{ t("noData") }}</p>
            </template>
            <template v-else>
              <q-list>
                <q-markup-table separator="horizontal" bordered flat dense>
                  <tbody>
                    <tr v-for="row in tableRow" :key="row.field">
                      <td class="text-left text-bold bg-grey-1">
                        {{ row.label }}
                      </td>
                      <td class="text-left">
                        <template
                          v-if="
                            row.field === 'requestParam' ||
                            row.field === 'requestHeader' ||
                            row.field === 'requestBody' ||
                            row.field === 'responseHeader' ||
                            row.field === 'responseBody'
                          "
                        >
                          <pre>{{ getFieldDisplayValue(row.field) }}</pre>
                        </template>
                        <template v-else>
                          {{ getFieldDisplayValue(row.field) }}
                        </template>
                      </td>
                    </tr>
                  </tbody>
                </q-markup-table>
              </q-list>
            </template>
          </div>
        </q-scroll-area>
      </q-card-section>

      <q-card-section style="max-width: 1000px; margin: 0 auto" v-else>
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <div class="column q-pa-sm q-gutter-sm">
            <template v-if="!selected">
              <q-img width="200px" :src="box" class="self-center"></q-img>
              <p class="text-h6 text-center">{{ t("noData") }}</p>
            </template>
            <template v-else>
              <q-list>
                <!-- 組織詳情 -->
                <q-markup-table separator="horizontal" bordered flat dense>
                  <tbody>
                    <tr v-for="row in tableRow" :key="row.field">
                      <td class="text-left text-bold bg-grey-1">
                        {{ row.label }}
                      </td>
                      <td class="text-left">
                        <template
                          v-if="
                            row.field === 'requestParam' ||
                            row.field === 'requestHeader' ||
                            row.field === 'requestBody' ||
                            row.field === 'responseHeader' ||
                            row.field === 'responseBody'
                          "
                        >
                          <pre>{{ getFieldDisplayValue(row.field) }}</pre>
                        </template>
                        <template v-else>
                          {{ getFieldDisplayValue(row.field) }}
                        </template>
                      </td>
                    </tr>
                  </tbody>
                </q-markup-table>
              </q-list>
            </template>
          </div>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { useTraceStore } from "stores/trace";
import { storeToRefs } from "pinia";
import box from "assets/empty-box.svg";

import { useDialogPluginComponent } from "quasar";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const store = useTraceStore();
const { selected, isOpenDetail } = storeToRefs(store);

defineEmits([...useDialogPluginComponent.emits]);
const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const tableRow = [
  {
    label: "UUID",
    field: "uuid"
  },
  {
    label: t("trace.table.funcType"),
    field: "serviceId"
  },
  {
    label: t("trace.table.statusCode"),
    field: "statusCode"
  },
  {
    label: t("trace.table.form"),
    field: "jhiFrom"
  },
  {
    label: t("trace.table.to"),
    field: "jhiTo"
  },
  {
    label: t("trace.table.requestIp"),
    field: "source"
  },
  {
    label: t("trace.table.uri"),
    field: "uri"
  },
  {
    label: t("trace.table.url"),
    field: "url"
  },
  {
    label: t("trace.table.RTT"),
    field: "rtt"
  },
  {
    label: t("trace.table.createTime"),
    field: "timestamp"
  },
  {
    label: t("trace.table.requestMethod"),
    field: "requestMethod"
  },
  {
    label: t("trace.table.requestParam"),
    field: "requestParam"
  },
  {
    label: t("trace.table.requestHeader"),
    field: "requestHeader"
  },
  {
    label: t("trace.table.requestBody"),
    field: "requestBody"
  },
  {
    label: t("trace.table.responseHeader"),
    field: "responseHeader"
  },
  {
    label: t("trace.table.responseBody"),
    field: "responseBody"
  }
];

//欄位顯示文字設定
const getFieldDisplayValue = (field) => {
  if (field === "state") {
    let state = selected.value[field];
    return state == 0 ? t("disabled") : t("enabled");
  } else if (field === "logType") {
    let logType = selected.value[field];
    return logType == "add" ? t("add") : t("modify");
  } else if (field === "timestamp") {
    let dateTimeStr = selected.value[field];
    if (!dateTimeStr) return "";
    const dateTime = new Date(dateTimeStr);
    // 格式化日期
    const formattedDate = `${dateTime.getFullYear()}/${(dateTime.getMonth() + 1)
      .toString()
      .padStart(2, "0")}/${dateTime.getDate().toString().padStart(2, "0")}`;

    // 格式化時間
    const formattedTime = `${dateTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${dateTime
      .getMinutes()
      .toString()
      .padStart(2, "0")}:${dateTime.getSeconds().toString().padStart(2, "0")}`;

    // 合併日期和時間
    const formattedDateTime = `${formattedDate} ${formattedTime}`;

    return formattedDateTime;
  } else if (
    field === "requestParam" ||
    field === "requestHeader" ||
    field === "requestBody" ||
    field === "responseHeader" ||
    field === "responseBody"
  ) {
    try {
      const jsonValue =
        typeof selected.value[field] === "string"
          ? JSON.parse(selected.value[field])
          : selected.value[field];
      return JSON.stringify(jsonValue, null, 2);
    } catch (error) {
      return selected.value[field];
    }
  } else {
    return selected.value[field];
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {};

const onDialogShow = () => {};
</script>
