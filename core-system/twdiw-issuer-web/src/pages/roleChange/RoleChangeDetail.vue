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
        <!-- 角色異動詳情 -->
        <div class="text-h6">{{ t("roleChange.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-scroll-area
          style="height: 43vh; max-height: 100vh"
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
                      <td class="text-right">
                        {{ getFieldDisplayValue(row.field) }}
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
          style="height: 43vh; max-height: 100vh"
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
                      <td class="text-right">
                        {{ getFieldDisplayValue(row.field) }}
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
import { useRoleChangeStore } from "src/stores/roleChange";
import { storeToRefs } from "pinia";
import box from "assets/empty-box.svg";

import { useDialogPluginComponent } from "quasar";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const store = useRoleChangeStore();
const { selected, isOpenDetail } = storeToRefs(store);

defineEmits([...useDialogPluginComponent.emits]);
const { dialogRef, onDialogCancel } = useDialogPluginComponent();

const tableRow = [
  {
    label: t("roleChange.table.actor"),
    field: "actor"
  },
  {
    label: t("roleChange.table.code"),
    field: "roleId"
  },
  {
    label: t("roleChange.table.name"),
    field: "roleName"
  },
  {
    label: t("roleChange.table.desc"),
    field: "description"
  },
  {
    label: t("roleChange.table.state"),
    field: "state"
  },
  {
    label: t("roleChange.table.logType"),
    field: "logType"
  },
  {
    label: t("roleChange.table.logTime"),
    field: "logTime"
  },
  {
    label: t("roleChange.table.createTime"),
    field: "createTime"
  },
  {
    label: t("roleChange.table.authChangeTime"),
    field: "authChangeTime"
  }
];

//欄位顯示文字設定
const getFieldDisplayValue = (field) => {
  if (field === "state") {
    let state = selected.value[field];
    return state == 0 ? t("disabled") : t("enabled");
  } else if (field === "logType") {
    let logType = selected.value[field];
    if (["add", "modify", "delete"].includes(logType)) {
      return t(logType);
    }
  } else if (
    field === "logTime" ||
    field === "createTime" ||
    field === "authChangeTime"
  ) {
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
