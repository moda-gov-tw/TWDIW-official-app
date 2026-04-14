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
        <div class="text-h6">{{ t("func.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
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
      </q-card-section>

      <q-card-section style="max-width: 1000px; margin: 0 auto" v-else>
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
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useFuncStore } from "stores/func";
import box from "assets/empty-box.svg";

import { useDialogPluginComponent } from "quasar";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const tableRow = [
  {
    label: t("func.table.id"),
    field: "id"
  },
  {
    label: t("func.table.res_group"),
    field: "resGrp"
  },
  {
    label: t("func.table.code"),
    field: "resId"
  },
  {
    label: t("func.table.name"),
    field: "resName"
  },
  {
    label: t("func.table.description"),
    field: "description"
  },
  {
    label: t("func.table.api_uri"),
    field: "apiUri"
  },
  {
    label: t("func.table.web_url"),
    field: "webUrl"
  },
  {
    label: t("func.table.funcType"),
    field: "typeId"
  },
  {
    label: t("func.table.enabled"),
    field: "state"
  },
  {
    label: t("func.table.createTime"),
    field: "createTime"
  }
];

const store = useFuncStore();
const { selected, isOpenDetail } = storeToRefs(store);

defineEmits([...useDialogPluginComponent.emits]);
const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

//欄位顯示文字設定
const getFieldDisplayValue = (field) => {
  if (field === "state") {
    let state = selected.value[field];
    if (state == 0) {
      return t("disabled");
    } else {
      return t("enabled");
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
