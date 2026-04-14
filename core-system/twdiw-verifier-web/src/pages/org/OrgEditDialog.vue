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
        <div class="text-h6">{{ t("org.title.edit") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-card-section style="max-width: 1200px; margin: 0 auto">
          <q-form ref="formRef" greedy>
            <q-table
              dense
              flat
              :hide-header="dialogLoading"
              class="sticky-header"
              :rows="orgEdit"
              :columns="columns"
              row-key="id"
              :loading="dialogLoading"
              :style="{ width: isMobile && '240px' }"
              :pagination="{ rowsPerPage: 0 }"
              :hide-bottom="true"
            >
              <template v-slot:body-cell-name="props">
                <q-td :props="props">
                  <span>
                    {{ getLabelName(props.row.name) }}
                  </span>
                </q-td>
              </template>

              <template v-slot:body-cell-value="props">
                <q-td :props="props">
                  <div>
                    <q-input
                      outlined
                      class="col"
                      v-model="inputData[props.row.name]"
                      :rules="getRules(props.row.name)"
                      hide-bottom-space
                      :placeholder="t('org.inputPlaceholder')"
                      dense
                      :style="{ height: '60px', paddingTop: '10px' }"
                      v-if="
                        getLabelName(props.row.name) ===
                        t('org.orgTwName.label')
                      "
                    ></q-input>
                    <q-input
                      outlined
                      class="col"
                      v-model="inputData[props.row.name]"
                      :rules="getRules(props.row.name)"
                      hide-bottom-space
                      :placeholder="t('org.inputPlaceholder')"
                      dense
                      :style="{ height: '60px', paddingTop: '10px' }"
                      v-else
                      disable
                    ></q-input>
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
          </q-form>
        </q-card-section>
      </q-card-section>

      <q-card-section v-else style="max-width: 1200px; margin: 0 auto">
        <div>
          <q-form ref="formRef" greedy>
            <q-table
              dense
              flat
              :hide-header="loading"
              class="sticky-header"
              :rows="orgEdit"
              :columns="columns"
              row-key="id"
              :loading="loading"
              :pagination="{ rowsPerPage: 0 }"
              :hide-bottom="true"
            >
              <template v-slot:body-cell-name="props">
                <q-td :props="props">
                  <span>
                    {{ getLabelName(props.row.name) }}
                  </span>
                </q-td>
              </template>

              <template v-slot:body-cell-value="props">
                <q-td :props="props">
                  <q-input
                    outlined
                    class="col"
                    v-model="inputData[props.row.name]"
                    :rules="getRules(props.row.name)"
                    hide-bottom-space
                    :placeholder="t('org.inputPlaceholder')"
                    lazy-rules
                    dense
                    :style="{
                      width: '180px',
                      height: '60px',
                      paddingTop: '10px'
                    }"
                    v-if="
                      getLabelName(props.row.name) === t('org.orgTwName.label')
                    "
                  >
                  </q-input>
                  <q-input
                    outlined
                    class="col"
                    v-model="inputData[props.row.name]"
                    :rules="getRules(props.row.name)"
                    hide-bottom-space
                    :placeholder="t('org.inputPlaceholder')"
                    lazy-rules
                    dense
                    :style="{
                      width: '180px',
                      height: '60px',
                      paddingTop: '10px'
                    }"
                    v-else
                    disable
                  >
                  </q-input>
                </q-td>
              </template>
              <template v-slot:loading>
                <q-inner-loading showing color="primary" />
              </template>
            </q-table>
          </q-form>
        </div>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          outline
          size="md"
          class="q-mt-md"
          :label="t('reset')"
          color="primary"
          @click="onResetClick"
        />
        <q-btn
          size="md"
          class="q-mt-md"
          :label="t('confirm')"
          color="primary"
          @click="onOKClick"
          :loading="loading"
          :disable="loading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, reactive, computed } from "vue";
import { useDialogPluginComponent } from "quasar";
import { useOrgStore } from "stores/org";
import { storeToRefs } from "pinia";
import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";

const { t } = useI18n();
const store = useOrgStore();
const $q = useQuasar();
const $n = useNotify();
const { dialogLoading, loading, orgEdit } = storeToRefs(store);
const { editOrg, resetOrgEdit } = store;

const props = defineProps({
  row: Object,
  id: Number,
  orgId: String,
  orgTwName: String,
  orgEnName: String
});

const { id, orgId, orgTwName, orgEnName } = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const getRules = (columnName) => {
  switch (columnName) {
    case "orgId":
      return [
        (val) => !!val || t("input", { input: t("org.orgId.label") }),
        (val) => !val || /^[0-9-]+$/.test(val) || t("org.orgId.pattern")
      ];
    case "orgTwName":
      return [
        (val) => !!val || t("input", { input: t("org.orgTwName.label") }),
        (val) =>
          /^[\u4E00-\u9FFF\u3400-\u4DBFa-zA-Z0-9.-]+$/.test(val) ||
          t("org.orgTwName.pattern"),
        (val) => val.length <= 30 || t("validation.maxLength", { max: 30 })
      ];
    case "orgEnName":
      return [
        (val) => !!val || t("input", { input: t("org.orgEnName.label") }),
        (val) =>
          /^([a-zA-Z0-9.,&\s-]+)$/.test(val) || t("org.orgEnName.inputPattern"),
        (val) =>
          (val && val.length <= 100) || t("validation.maxLength", { max: 100 })
      ];
    default:
      return [];
  }
};

const getLabelName = (name) => {
  const labels = {
    orgId: t("org.orgId.label"),
    orgTwName: t("org.orgTwName.label"),
    orgEnName: t("org.orgEnName.label")
  };
  return labels[name] || t("org.undefined");
};

const onOKClick = async () => {
  // 強制觸發所有輸入框的驗證
  await formRef.value.validate().catch((err) => {
    // 驗證失敗時，所有不合規則的輸入框都會顯示錯誤訊息
    return false;
  });

  // 檢查是否有空欄位
  const hasEmptyFields = orgEdit.value.some((item) => {
    return !inputData[item.name];
  });

  if (hasEmptyFields) {
    $n.error(t("requiredFields"));
    return;
  }

  // 只有當表單驗證通過且沒有空欄位時，才執行後續邏輯
  const isValid = await formRef.value.validate();
  if (isValid) {
    const updatedOrgDetail = {
      id: id.value,
      orgId: inputData["orgId"] || "",
      orgTwName: inputData["orgTwName"] || "",
      orgEnName: inputData["orgEnName"] || ""
    };

    const { isEditOK } = await editOrg(updatedOrgDetail);
    if (isEditOK) {
      dialogRef.value.hide();
    }
  }
};

const onResetClick = () => {
  resetOrgEdit(orgId.value, orgTwName.value, orgEnName.value);
  resetInputData();
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {
  resetOrgEdit("", "", "", "");
  clearInputData();
};

const formRef = ref(null);

const inputData = reactive({ orgId: "", orgTwName: "", orgEnName: "" });

const columns = [
  {
    name: "name",
    label: t("org.fieldName"),
    align: "center",
    field: "name",
    style: "width: 50%; white-space: normal; word-break: break-word"
  },
  {
    name: "value",
    label: t("org.fieldValue"),
    align: "left",
    field: "value",
    style: "width: 50%; white-space: normal; word-break: break-word"
  }
];

const resetInputData = () => {
  inputData.orgId = orgId.value;
  inputData.orgTwName = orgTwName.value;
  inputData.orgEnName = orgEnName.value;
};

const clearInputData = () => {
  inputData.orgId = "";
  inputData.orgTwName = "";
  inputData.orgEnName = "";
};

const onDialogShow = () => {
  resetOrgEdit(orgId.value, orgTwName.value, orgEnName.value);
  resetInputData();
};
</script>

<style scoped>
.q-table td.q-td {
  height: 90px !important; /* 設定固定高度 */
}
</style>
