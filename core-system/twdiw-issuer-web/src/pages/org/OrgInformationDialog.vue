<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card class="q-dialog-plugin" style="max-width: 1500px; width: 1200px">
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 組織詳情 -->
        <div class="text-h6">{{ t("org.information") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-card-section style="max-width: 1200px; margin: 0 auto">
          <q-table
            dense
            flat
            :hide-header="dialogLoading"
            class="sticky-header"
            :rows="orgDetailFiltered"
            :columns="columns"
            row-key="orgId"
            :loading="dialogLoading"
            :rows-per-page-options="[10, 20, 50, 0]"
            :pagination="pagination"
            :hide-bottom="true"
          >
            <template v-slot:body-cell-value="props">
              <q-td :props="props">
                <div>
                  {{ props.row.value }}
                </div>
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
          </q-table>
        </q-card-section>
      </q-card-section>
      <q-card-section style="max-width: 1000px; margin: 0 auto" v-else>
        <q-table
          dense
          flat
          :hide-header="dialogLoading"
          class="sticky-header"
          :rows="orgDetailFiltered"
          :columns="columns"
          row-key="orgId"
          :loading="dialogLoading"
          :rows-per-page-options="[10, 20, 50, 0]"
          :pagination="pagination"
          :hide-bottom="true"
        >
          <template v-slot:body-cell-value="props">
            <q-td :props="props">
              <div>
                {{ props.row.value }}
              </div>
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
        </q-table>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, computed } from "vue";
import { useDialogPluginComponent } from "quasar";
import { useOrgStore } from "stores/org";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";

const store = useOrgStore();
const { t } = useI18n();

const { orgDetail, dialogLoading } = storeToRefs(store);
const { getOrgDetail, resetOrgDetail } = store;

const props = defineProps({
  row: Object,
  orgId: String,
  orgTwName: String,
  orgEnName: String,
  vcDataSource: Number
});

const { orgId } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {
  resetOrgDetail();
};

const excludedKeys = [
  t("org.table.vcDataSource"),
  t("org.logo"),
  t("org.logoUrl")
];

const orgDetailFiltered = computed(() => {
  return orgDetail.value.filter((item) => !excludedKeys.includes(item.name));
});

const formatJson = (data) => {
  try {
    const jsonValue = typeof data === "string" ? JSON.parse(data) : data;
    return JSON.stringify(jsonValue, null, 2);
  } catch (error) {
    return data;
  }
};

const columns = [
  {
    name: "name",
    label: t("org.fieldName"),
    align: "center",
    field: "name",
    style: "width: 120px; white-space: normal; word-break: break-word"
  },
  {
    name: "value",
    label: t("org.fieldData"),
    align: "left",
    // 修改field來處理不同類型的cname
    field: "value",
    style: "white-space: normal; word-break: break-word"
  }
];

const onDialogShow = () => {
  if (orgId.value) {
    getOrgDetail(orgId.value);
  }
};

const pagination = ref({
  rowsPerPage: 10, // 預設每頁顯示數量
  sortBy: "",
  descending: false,
  page: 1
});
</script>
