<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black column"
      :style="{
        maxWidth: '1500px',
        width: '1400px',
        height: isMobile ? '80vh' : '100vh'
      }"
    >
      <!-- dialog HEADER -->
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">
          {{
            props.isEdit
              ? t("vp.dialog.title.editVp")
              : t("vp.dialog.title.createVp")
          }}
        </div>
        <q-btn
          flat
          round
          color="white"
          icon="close"
          @click="onCancelClick"
        ></q-btn>
      </q-card-section>
      <!-- 步驟內容 -->
      <div :class="['col', isMobile ? 'q-pa-sm' : 'q-pa-md']">
        <q-stepper
          v-model="step"
          ref="stepper"
          active-color="indigo-6"
          done-color="indigo-6"
          :alternative-labels="$q.screen.sm"
          header-nav
          animated
          style="height: 100%; display: flex; flex-direction: column"
        >
          <q-step
            :name="1"
            :title="isMobile ? '' : t('vp.stepHeader.step1')"
            :caption="t('vp.stepCaption.step1')"
            icon="settings"
            :done="step > 1"
            :header-nav="step > 1"
          >
            <create-vp-step1
              ref="stepRef"
              v-model:formData="formData"
              :is-edit="isEdit"
            />
          </q-step>
          <q-step
            :name="2"
            :title="isMobile ? '' : t('vp.stepHeader.step2')"
            :prefix="isMobile ? '2' : ''"
            :caption="t('vp.stepCaption.step2')"
            icon="settings"
            :done="step > 2"
            :header-nav="step > 2"
          >
            <create-vp-step2
              ref="stepRef"
              v-model:formData="formData"
              v-model:treeData="treeData"
              :is-edit="isEdit"
            />
          </q-step>
          <q-step
            :name="3"
            :title="isMobile ? '' : t('vp.stepHeader.step3')"
            :prefix="isMobile ? '3' : ''"
            :caption="t('vp.stepCaption.step3')"
            icon="settings"
            :done="step > 3"
            :header-nav="step > 3"
          >
            <create-vp-step3
              ref="stepRef"
              v-model:formData="formData"
              v-model:treeData="treeData"
            />
          </q-step>
          <q-step
            :name="4"
            :title="isMobile ? '' : t('vp.stepHeader.step4')"
            :prefix="isMobile ? '4' : ''"
            :caption="t('vp.stepCaption.step4')"
            icon="settings"
            :header-nav="step > 4"
          >
            <create-vp-step4
              ref="stepRef"
              v-model:formData="formData"
              :is-edit="isEdit"
              :row="row"
              @submit-completed="handleFinalSubmit"
            />
          </q-step>
        </q-stepper>
      </div>
      <q-separator />

      <q-card-actions align="right" class="q-pa-md row q-gutter-sm justify-end">
        <div
          v-if="step == 4"
          :class="[
            'alert-text',
            isMobile ? 'col-12 flex justify-center' : 'q-mr-md'
          ]"
        >
          {{ t("vp.dialog.alertMessage") }}
        </div>
        <q-btn
          v-if="step > 1"
          outline
          :label="t('previousStep')"
          color="primary"
          unelevated
          @click="$refs.stepper.previous()"
          style="width: 120px"
          :class="isMobile ? 'col' : ''"
          :disable="createVPLoading"
        />
        <q-btn
          v-if="step < 4"
          :label="t('nextStep')"
          color="primary"
          unelevated
          @click="handleNextStep"
          :disable="checkAllFieldByStepLoading"
          :loading="checkAllFieldByStepLoading"
          style="width: 120px"
          :class="isMobile ? 'col' : ''"
        />
        <q-btn
          v-if="step == 4"
          :label="t('confirm')"
          color="primary"
          unelevated
          @click="onOKClick"
          style="width: 120px"
          :disable="createVPLoading"
          :loading="createVPLoading"
          :class="isMobile ? 'col' : ''"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useVpStore } from "src/stores/vp";
import { computed, onMounted, reactive, ref } from "vue";
import CreateVpStep1 from "./step/CreateVPStep1.vue";
import CreateVpStep2 from "./step/CreateVPStep2.vue";
import CreateVpStep3 from "./step/CreateVPStep3.vue";
import CreateVpStep4 from "./step/CreateVPStep4.vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const vpStore = useVpStore();
const { checkAllFieldByStepLoading, createVPLoading } = storeToRefs(vpStore);
const { checkAllFieldByStep } = vpStore;

defineEmits([...useDialogPluginComponent.emits]);

const props = defineProps({
  isEdit: Boolean,
  row: Object
});

const $q = useQuasar();
const { dialogRef, onDialogHide, onDialogCancel, onDialogOK } =
  useDialogPluginComponent();

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 步驟表單資料
const formData = reactive({
  // step1
  // VP 代碼
  serialNo: "",
  // VP 名稱
  name: "",
  // VP 目的
  purpose: "",
  // VP 授權條款內容
  terms: "",
  // 顯示進階功能
  showMore: false,
  // 模式
  model: "0",
  // 組織業務系統URL
  verifierServiceUrl: "",
  // CallBack URL
  callBackUrl: "",
  // 是否自定義資料
  isCustomFields: false,
  // 欄位
  fields: [],
  // TAG
  tag: "",
  // 模組加密
  isEncryptEnabled: false,

  // step2 & step3 群組資料
  groups: [
    {
      name: "",
      rule: "pick", // 預設最多可選取
      max: "1",
      vcDatas: []
    }
  ]
});

// 樹狀圖資料
const treeData = reactive({
  treeNodeDatas: [],
  tickedNodes: []
});

// 步驟相關
const step = ref(1);
const stepper = ref(null);
const stepRef = ref(null);

// 對話框顯示時載入資料
const onDialogShow = () => {
  console.log("onDialogShow START");
};

const handleNextStep = async () => {
  const isValid = await stepRef.value.toNextStep();

  if (!isValid) return;

  const data = {
    ...formData,
    model: formData.model.value,
    step: step.value,
    isEdit: props.isEdit
  };

  const passed = await checkAllFieldByStep(data);
  if (!passed) return;

  stepper.value.next();
};

// 取消處理
const onCancelClick = () => {
  onDialogCancel();
};

// 確認送出
const onOKClick = async () => {
  stepRef.value.onOKClick();
};

// 處理最終送出完成邏輯
const handleFinalSubmit = (resultData) => {
  onDialogOK(resultData);
};

onMounted(() => {
  // 若為編輯，初始化 formData
  if (props.isEdit) {
    Object.assign(formData, props.row.vpItemData);
    formData.isCustomFields = props.row.vpItemData?.fields?.length > 0;
    formData.isEncryptEnabled = formData.isEncryptEnabled ?? false;
  }
});
</script>

<style scoped>
@media (max-width: 599px) {
  ::v-deep(.q-stepper__tab) {
    flex: 1 1 0;
    max-width: none;
    padding: 14px 4px;
  }

  ::v-deep(.q-stepper__step-inner) {
    padding: 10px;
  }
}
</style>
