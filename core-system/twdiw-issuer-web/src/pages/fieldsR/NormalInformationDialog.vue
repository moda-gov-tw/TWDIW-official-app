<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <!-- 詳細資訊 -->
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">{{ t("normalFields.column.information") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          :style="{ height: isMobile ? '45vh' : '60vh', 'max-height': '100vh' }"
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
          <q-card-section style="max-width: 1200px; margin: 0 auto">
            <!-- 欄位對外名稱 -->
            <div class="info-block">
              <span class="text-h6 text-grey-7">{{
                t("basicFields.column.cname") + "："
              }}</span>
              <span class="text-h6 q-ma-none word-break">{{ cname }}</span>
            </div>
            <!-- 欄位名稱(英) -->
            <div class="q-pt-sm items-baseline info-block">
              <span class="text-h6 text-grey-7">{{
                t("basicFields.column.ename") + "："
              }}</span>
              <span class="text-h6 q-ma-none word-break">{{ ename }}</span>
            </div>
            <q-form ref="formRef" greedy class="q-pt-md">
              <div class="row q-col-gutter-md">
                <!-- id -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span
                    >{{ t("normalFields.information.id") + "：" }}
                  </p>
                  <q-input outlined v-model="form.id" disable dense />
                </div>
                <!-- 類別 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.information.type") + "：" }}
                  </p>
                  <q-input outlined v-model="form.type" disable dense />
                </div>
                <!-- 名稱 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.information.name") + "：" }}
                  </p>
                  <q-input outlined v-model="form.name" disable dense />
                </div>
                <!-- 正規表達式 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.information.regular") + "：" }}
                  </p>
                  <q-input
                    outlined
                    v-model="form.regularExpression"
                    disable
                    dense
                  >
                  </q-input>
                </div>
                <!-- 限定資料格式 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.dialog.regularExpression") + "：" }}
                  </p>
                  <q-input outlined v-model="form.description" disable dense />
                </div>
                <!-- 錯誤訊息 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.information.errorMsg") + "：" }}
                  </p>
                  <q-input outlined v-model="form.errorMsg" disable dense />
                </div>
                <!-- 規則 -->
                <div class="col-12 col-md-6">
                  <p class="input-title">
                    <span class="text-red">*</span>
                    {{ t("normalFields.information.rule") + "：" }}
                  </p>
                  <q-input outlined v-model="ruleType" dense disable />
                </div>
              </div>
            </q-form>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, defineEmits, toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useI18n } from "vue-i18n";

const $q = useQuasar();

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const { t } = useI18n();

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogCancel } = useDialogPluginComponent();

const props = defineProps({
  ename: {
    type: String,
    required: true
  },
  cname: {
    type: String,
    required: true
  },
  row: {
    type: Object,
    required: true
  }
});

const { row, ename, cname } = toRefs(props);

const form = ref({});
const formRef = ref(null);

const onDialogShow = () => {
  form.value = row.value;
};

const ruleType = computed(() => {
  if (!form.value.ruleType) return "";
  return t(`normalFields.information.ruleType.${form.value.ruleType}`);
});

const onCancelClick = () => {
  onDialogCancel();
};
</script>
