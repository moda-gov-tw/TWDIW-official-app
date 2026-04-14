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
        <!-- 編輯靜態 QR Code 功能 -->
        <div class="text-h6">
          {{ t("vcSchema.dialog.editVCServiceUrlTitle") }}
        </div>
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
        >
          <q-form ref="formRef" greedy>
            <div class="q-px-md q-pt-md">
              <div class="col-12">
                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.id") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break"
                      >{{ formData.id }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.name") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">{{
                      formData.name
                    }}</span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.org") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ formData.businessTWName }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.serialNo") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ formData.serialNo }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.credentialType") }} ：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ `${formData.businessId}_${formData.serialNo}` }}
                    </span>
                  </div>
                </div>

                <q-separator size="2px" class="q-my-lg" />

                <div :class="['row items-start', isMobile ? '' : 'q-mb-lg']">
                  <div
                    :class="[
                      'row justify-between items-end q-mt-xs',
                      isMobile ? 'col-auto' : 'input-title q-mr-md'
                    ]"
                  >
                    <span class="text-h6-cus text-grey-7">
                      {{ t("vcSchema.dialog.qrCode.label") }}</span
                    >
                    <q-icon
                      :class="['q-mb-xs', isMobile ? 'q-ml-md' : '']"
                      color="primary"
                      name="info_outline"
                      size="sm"
                      @click="toggleQRCodeInfo"
                    >
                      <q-popup-proxy
                        v-if="!isMobile"
                        anchor="top right"
                        self="top left"
                        :offset="[65, 15]"
                        class="bg-grey-2"
                      >
                        <div class="text-white text-body2 notice">
                          <p class="text-body2 text-white">
                            {{ t("vcSchema.notice.webView") }}
                          </p>
                          <p class="text-body2 q-pt-sm text-white">
                            {{ t("vcSchema.notice.openPage") }}
                          </p>
                        </div>
                      </q-popup-proxy>
                    </q-icon>
                  </div>
                  <q-checkbox
                    v-model="needStaticQRCode"
                    size="md"
                    keep-color
                    color="indigo-4"
                    :rules="[
                      (val) =>
                        (val !== null && val !== undefined) ||
                        t('vcSchema.rules.check')
                    ]"
                  />
                </div>

                <div v-if="needStaticQRCode" class="q-mt-lg">
                  <div :class="['q-mb-sm', !isMobile ? 'row items-start' : '']">
                    <div class="input-title q-mt-xs q-mr-md">
                      <span class="text-h6-cus text-grey-7">
                        <span class="text-red">*</span>
                        {{ t("vcSchema.dialog.qrCode.type") }}</span
                      >
                    </div>
                    <q-select
                      outlined
                      class="col"
                      :class="isMobile ? '' : 'q-ml-sm'"
                      v-model="formData.type"
                      :options="typeOptions"
                      option-label="label"
                      option-value="value"
                      dense
                      :label="
                        t('vcSchema.rules.selectField', {
                          field: t('vcSchema.dialog.qrCode.type')
                        })
                      "
                      :rules="[
                        (val) =>
                          !!val ||
                          t('vcSchema.rules.selectField', {
                            field: t('vcSchema.dialog.qrCode.type')
                          })
                      ]"
                      :popup-content-class="'no-modal'"
                    />
                  </div>

                  <div :class="['q-mb-sm', !isMobile ? 'row items-start' : '']">
                    <div
                      :class="[
                        'row items-end q-mt-xs',
                        isMobile
                          ? 'col-auto justify-start'
                          : 'justify-between input-title q-mr-md'
                      ]"
                    >
                      <span class="text-h6-cus text-grey-7">
                        <span class="text-red">*</span>
                        {{ t("vcSchema.dialog.qrCode.issuerServiceUrl") }}</span
                      >
                      <q-icon
                        :class="['q-mb-xs', isMobile ? 'q-ml-md' : '']"
                        color="primary"
                        name="info_outline"
                        size="sm"
                        @click="toggleServiceUrlInfo"
                      >
                        <q-popup-proxy
                          v-if="!isMobile"
                          anchor="bottom right"
                          self="top left"
                          :offset="[25, 10]"
                          class="bg-grey-2"
                        >
                          <div class="text-white text-body2 notice">
                            <p class="text-body2 text-white">
                              {{ t("vcSchema.notice.issuerServiceUrlNotice") }}
                            </p>
                          </div>
                        </q-popup-proxy>
                      </q-icon>
                    </div>
                    <q-input
                      outlined
                      class="col"
                      :class="isMobile ? '' : 'q-ml-sm'"
                      v-model="formData.issuerServiceUrl"
                      :rules="[
                        (val) => {
                          const pattern =
                            /^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-\.~:\/?#\[\]@!$&'()*+,;=%]*)?$/;
                          return pattern.test(val) || t('vcSchema.rules.url');
                        }
                      ]"
                      :placeholder="
                        t('vcSchema.rules.field', {
                          field: t('vcSchema.dialog.qrCode.issuerServiceUrl')
                        })
                      "
                      dense
                    />
                  </div>
                </div>
              </div></div
          ></q-form>
        </q-scroll-area>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          size="16px"
          @click="onCancelClick"
          dense
          :style="{ width: '100px' }"
          :disable="editServiceUrlLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
          :disable="editServiceUrlLoading"
          :loading="editServiceUrlLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 靜態QR Code -->
  <q-dialog v-model="showMobileQRCodeInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vcSchema.dialog.qrCode.label") }}</div>
      </q-card-section>

      <q-card-section>
        <p class="text-body2">
          {{ t("vcSchema.notice.webView") }}
        </p>
        <p class="text-body2 q-pt-sm">
          {{ t("vcSchema.notice.openPage") }}
        </p>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 組織業務系統 URL -->
  <q-dialog v-model="showMobileServicrUrlInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">
          {{ t("vcSchema.dialog.qrCode.issuerServiceUrl") }}
        </div>
      </q-card-section>

      <q-card-section>
        <p class="text-body2">
          {{ t("vcSchema.notice.issuerServiceUrlNotice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, computed, reactive } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useVcSchemaStore } from "stores/vcSchema";
import { useI18n } from "vue-i18n";
const vcSchema = useVcSchemaStore();
const { editServiceUrlLoading } = storeToRefs(vcSchema);
const { editServiceUrl } = vcSchema;
const { t } = useI18n();
const props = defineProps({
  row: Object
});

const $q = useQuasar();
const $n = useNotify();

const showMobileQRCodeInfo = ref(false);
const showMobileServicrUrlInfo = ref(false);
const needStaticQRCode = ref(props.row.type === "1" || props.row.type === "2");

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();

const onCancelClick = () => {
  onDialogCancel();
};

// [REACTIVE]: 表單
const formData = reactive({ ...props.row });

const onDialogShow = () => {
  setVcData();
};

const setVcData = () => {
  formData.type = matchSelectOption(typeOptions, props.row.type);
};

/**
 * 根據給定的值，在選項中找到對應的 option 物件
 */
const matchSelectOption = (options, value, optionValueKey = "value") => {
  return options.find((opt) => opt[optionValueKey] === value) || null;
};

const formRef = ref(null);

const typeOptions = [
  { label: t("vcSchema.select.1"), value: "1" },
  { label: t("vcSchema.select.2"), value: "2" }
];

const toggleQRCodeInfo = () => {
  if (isMobile.value) {
    showMobileQRCodeInfo.value = true;
  }
};

const toggleServiceUrlInfo = () => {
  if (isMobile.value) {
    showMobileServicrUrlInfo.value = true;
  }
};
const onOKClick = async () => {
  // [檢核] 所有欄位
  const isValid = await formRef.value.validate();
  if (needStaticQRCode.value && !isValid) {
    $n.error(t("requiredFields"));
    return;
  }

  if (!needStaticQRCode.value) {
    formData.type = null;
    formData.issuerServiceUrl = null;
  }

  submitForm();
};

// 資料送出
const submitForm = async () => {
  const submitData = {
    ...formData,
    type: needStaticQRCode.value ? formData.type?.value ?? null : null,
    issuerServiceUrl: needStaticQRCode.value ? formData.issuerServiceUrl : null
  };

  const result = await editServiceUrl(submitData);

  if (result) {
    onDialogCancel();
    onDialogOK(result);
  }
};
</script>

<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
.notice {
  background-color: grey;
  padding: 5px;
}
.input-title {
  min-width: 200px;
}
</style>
