<template>
  <pop-up v-model="roleDetailOpen" :header="t('role.detailTitle')">
    <div class="q-pa-sm q-gutter-sm bg-white">
      <template v-if="!selected">
        <q-img width="200px" :src="box" class="self-center"></q-img>
        <p class="text-h6 text-center">{{ t("noData") }}</p>
      </template>
      <template v-else>
        <q-list>
          <!-- 角色詳情 -->
          <q-markup-table separator="horizontal" bordered flat dense>
            <tbody>
              <tr v-for="row in tableRow" :key="row.field">
                <td class="text-left text-bold bg-grey-1">{{ row.label }}</td>
                <td class="text-right">
                  {{ getFieldDisplayValue(row.field) }}
                </td>
              </tr>
            </tbody>
          </q-markup-table>
          <!-- 功能資訊 -->
          <q-expansion-item expand-icon="none" class="q-mt-md" default-opened>
            <template #header>
              <q-item-section avatar>
                <q-icon size="sm" color="primary" name="table_rows" />
              </q-item-section>
              <q-item-section>
                <p
                  class="text-bold q-ma-none"
                  style="font-size: 20px; font-weight: normal"
                >
                  {{ t("role.detail.funcDetail") }}
                </p>
              </q-item-section>
            </template>
            <q-input
              dense
              filled
              v-model="fillter"
              :placeholder="t('role.detail.search')"
              clearable
            >
              <template v-slot:prepend>
                <q-icon name="search"></q-icon>
              </template>
            </q-input>
            <q-table
              bordered
              flat
              dense
              :columns="columns"
              :rows="roleResList"
              :no-data-label="t('role.detail.noData')"
              hide-header
              v-model:pagination="page"
              :style="roleResList.length > 0 ? 'height: 280px' : 'auto'"
              :filter="fillter"
            >
              <!-- 無資料時的顯示處理 -->
              <template #no-data="{ message }">
                <div class="full-width row text-grey-6">
                  <q-icon size="1.5em" name="info"></q-icon>
                  <span class="q-ml-xs">
                    {{ message }}
                  </span>
                </div>
              </template>
              <!-- bottom位置 用於消除預設的 pagination -->
              <template #bottom> </template>
            </q-table>
          </q-expansion-item>
        </q-list>
      </template>
    </div>
  </pop-up>
</template>
<script setup>
import { ref } from "vue";
import { storeToRefs } from "pinia";
import { useRoleStore } from "stores/role";
import box from "assets/empty-box.svg";

import { useI18n } from "vue-i18n";
const { t } = useI18n();

// 角色詳情 table
const tableRow = [
  {
    label: t("role.table.code"),
    field: "roleId"
  },
  {
    label: t("role.table.name"),
    field: "roleName"
  },
  {
    label: t("role.table.desc"),
    field: "description"
  },
  {
    label: t("role.table.state"),
    field: "state"
  },
  {
    label: t("role.table.createTime"),
    field: "createTime"
  }
];

const store = useRoleStore();
const { selected, roleResList, roleDetailOpen } = storeToRefs(store);

const emit = defineEmits(["detailClick"]);

//欄位顯示文字設定
const getFieldDisplayValue = (field) => {
  if (field === "state") {
    let state = selected.value[field];
    return state == 0 ? t("disabled") : t("enabled");
  } else if (field === "logTime" || field === "createTime") {
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

// 功能資訊
const columns = [
  {
    name: "resName",
    label: t("role.resName"),
    sortable: true,
    field: "resName",
    align: "left"
  },
  {
    name: "resId",
    label: t("role.resId"),
    sortable: true,
    field: "resId",
    align: "left"
  }
];

const page = { rowsPerPage: 0 };

const fillter = ref("");
</script>
