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
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 編輯 VC 資料來源 -->
        <div class="text-h6">{{ t("vcSchema.edit") }}</div>
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
          <div class="q-px-md q-pt-md">
            <div class="q-mb-md">
              <div class="info-block">
                <span class="text-h6 text-grey-7"
                  >{{ t("vcSchema.table.serialNo") }}：</span
                >
                <span class="text-h6 word-break">{{ serialNo }}</span>
              </div>
              <div class="q-my-md info-block">
                <span class="text-h6 text-grey-7"
                  >{{ t("vcSchema.table.name") }}：</span
                >
                <span class="text-h6 word-break">{{ name }}</span>
              </div>
              <div class="info-block">
                <span class="text-h6 text-grey-7"
                  >{{ t("vcSchema.validityPeriod") }}：</span
                >
                <span class="text-h6"
                  >{{ lengthExpire }}
                  {{ convertUnitType(unitTypeExpire) }}</span
                >
              </div>
            </div>

            <q-form ref="formRef" greedy>
              <div class="row q-col-gutter-md">
                <q-input
                  v-model="params.httpMethod"
                  outlined
                  class="col-12 col-md-3"
                  dense
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  label="METHOD"
                  behavior="menu"
                  hide-bottom-space
                  :style="{ height: '60px' }"
                  disable
                />
                <q-input
                  v-model="params.url"
                  class="col-12 col-md-9"
                  outlined
                  :rules="[
                    (val) => !!val || t('valid.urlRequired'),
                    (val) =>
                      /^(ftp|http|https):\/\/[^ ]+$/.test(val) ||
                      /^(www\.)[^ ]+$/.test(val) ||
                      t('valid.urlInvalid')
                  ]"
                  :placeholder="t('vcSchema.vcDataSource.inputUrl')"
                  :label="t('vcSchema.vcDataSource.inputUrl')"
                  dense
                />
              </div>

              <q-separator size="1px" class="q-mt-sm" />

              <p class="q-mt-sm text-h6">
                {{ t("vcSchema.vcDataSource.header") }}
              </p>

              <ul>
                <li
                  v-for="(header, index) in headersList"
                  :key="index"
                  class="q-mb-sm"
                >
                  <div
                    class="row items-start q-gutter-x-md"
                    v-if="$q.screen.gt.sm"
                    style="height: 60px"
                  >
                    <q-btn
                      unelevated
                      icon="remove"
                      color="indigo-7"
                      @click="removeRow(index)"
                      class="gt-xs"
                      :disable="headersList.length <= 1"
                    />

                    <q-btn
                      unelevated
                      icon="add"
                      color="primary"
                      @click="addRow"
                      class="gt-xs"
                      :disable="index !== headersList.length - 1"
                    />

                    <q-input
                      v-model="header.key"
                      class="col"
                      outlined
                      :rules="[
                        (val) =>
                          !!val ||
                          t('input', {
                            input: ' ' + t('vcSchema.vcDataSource.headerKey')
                          })
                      ]"
                      :placeholder="t('vcSchema.vcDataSource.inputHeaderKey')"
                      :label="t('vcSchema.vcDataSource.inputHeaderKey')"
                      dense
                    />
                    <q-input
                      v-model="header.value"
                      class="col"
                      outlined
                      :rules="[
                        (val) =>
                          !!val ||
                          t('input', {
                            input: ' ' + t('vcSchema.vcDataSource.headerValue')
                          })
                      ]"
                      :placeholder="t('vcSchema.vcDataSource.inputHeaderValue')"
                      :label="t('vcSchema.vcDataSource.inputHeaderValue')"
                      dense
                    />
                  </div>

                  <div v-if="$q.screen.lt.md" class="q-pb-md">
                    <q-input
                      v-model="header.key"
                      class="col q-mb-sm"
                      outlined
                      :rules="[
                        (val) =>
                          !!val ||
                          t('input', {
                            input: ' ' + t('vcSchema.vcDataSource.headerKey')
                          })
                      ]"
                      :placeholder="t('vcSchema.vcDataSource.inputHeaderKey')"
                      :label="t('vcSchema.vcDataSource.inputHeaderKey')"
                      dense
                    />
                    <q-input
                      v-model="header.value"
                      class="col q-mb-sm"
                      outlined
                      :rules="[
                        (val) =>
                          !!val ||
                          t('input', {
                            input: ' ' + t('vcSchema.vcDataSource.headerValue')
                          })
                      ]"
                      :placeholder="t('vcSchema.vcDataSource.inputHeaderValue')"
                      :label="t('vcSchema.vcDataSource.inputHeaderValue')"
                      dense
                    />

                    <div class="row items-center q-gutter-x-sm">
                      <q-btn
                        unelevated
                        icon="remove"
                        color="indigo-7"
                        @click="removeRow(index)"
                        class="col"
                        :disable="headersList.length <= 1"
                      />
                      <q-btn
                        unelevated
                        icon="add"
                        color="primary"
                        @click="addRow"
                        class="col"
                        :disable="index !== headersList.length - 1"
                      />
                    </div>
                  </div>
                </li>
              </ul>
            </q-form>
            <div class="row">
              <div class="col-12 col-md-10">
                <div v-if="responseHttpStatus" class="q-mb-sm">
                  <div class="row items-center q-mb-sm">
                    <q-btn
                      size="sm"
                      round
                      unelevated
                      :color="
                        responseHttpStatus === '404' ||
                        responseHttpStatus === '999'
                          ? 'primary'
                          : 'accent'
                      "
                      :disable="
                        responseHttpStatus === '404' ||
                        responseHttpStatus === '999'
                          ? verifyStatusError
                          : verifyStatusSuccess
                      "
                      :icon="
                        responseHttpStatus === '404' ||
                        responseHttpStatus === '999'
                          ? 'close'
                          : 'done'
                      "
                    />
                    <p class="text-body1 q-ml-sm">
                      {{
                        responseHttpStatus === "404" ||
                        responseHttpStatus === "999"
                          ? t("apiTest.fail")
                          : t("apiTest.success")
                      }}
                      {{ responseHttpStatus }}
                    </p>
                  </div>
                  <p class="word-break">
                    {{ responseMsg }}
                  </p>
                </div>
              </div>
              <div class="col-12 col-md-2 text-right">
                <q-btn
                  color="indigo-7"
                  :label="t('apiTest.lable')"
                  unelevated
                  :style="{ width: '100px' }"
                  @click="testConnection"
                  :disable="checkAPILoading"
                  :loading="checkAPILoading"
                />
              </div>
            </div>
          </div>
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
          :disable="settingLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
          :disable="settingLoading"
          :loading="settingLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, watch } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useVcSchemaStore } from "stores/vcSchema";
import { useI18n } from "vue-i18n";
const vcSchema = useVcSchemaStore();
const { settingLoading, checkAPILoading } = storeToRefs(vcSchema);
const { settingUrl, checkAPI } = vcSchema;
const { t } = useI18n();
const props = defineProps({
  vcId: Number,
  name: String,
  serialNo: String,
  apiType: String,
  headers: String,
  httpMethod: String,
  url: String,
  lengthExpire: Number,
  unitTypeExpire: String
});

const {
  vcId,
  name,
  serialNo,
  lengthExpire,
  unitTypeExpire,
  apiType,
  httpMethod,
  headers,
  url
} = toRefs(props);

const $q = useQuasar();
const $n = useNotify();

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();

const onCancelClick = () => {
  onDialogCancel();
};

const params = ref({
  httpMethod: "POST",
  url: ""
});

const onDialogShow = () => {
  params.value.httpMethod = methodOptions[0].value;
  params.value.url = url.value || "";

  if (headers.value) {
    try {
      headersList.value = JSON.parse(headers.value);
    } catch (error) {
      console.error(`${t("vcSchema.vcDataSource.error.header")}：${error}`);
      headersList.value = [{ key: "", value: "" }];
    }
  }
};

const methodOptions = [
  {
    label: "POST",
    value: "POST"
  }
];

const formRef = ref(null);

const onOKClick = async () => {
  if (verifyStatusSuccess.value) {
    $n.error(t("apiTest.required"));
    return;
  } else {
    const keys = headersList.value.map((header) => header.key);
    const hasDuplicates = keys.some(
      (key, index) => keys.indexOf(key) !== index
    );

    if (hasDuplicates) {
      const duplicateKeys = keys.filter(
        (key, index) => keys.indexOf(key) !== index
      );
      $n.error(
        t("vcSchema.vcDataSource.header.duplicate", {
          key: [...new Set(duplicateKeys)].join(", ")
        })
      );
      return;
    }

    const paramsObj = createParamsObj();

    const success = await settingUrl(paramsObj);

    if (success) {
      onDialogOK();
    }
  }
};

const createParamsObj = () => {
  const ArrayToJson = JSON.stringify(headersList.value);
  const paramsUrl = params.value.url;

  return {
    vcId: vcId.value,
    vcSerialNo: serialNo.value,
    apiType: apiType.value ? apiType.value : "901i",
    headers: ArrayToJson,
    url: paramsUrl,
    httpMethod: httpMethod.value ? httpMethod.value : "post"
  };
};

const convertUnitType = (unit) => {
  switch (unit) {
    case "DAY":
      return t("vcSchema.select.DAY");
    case "MONTH":
      return t("vcSchema.select.MONTH");
    case "YEAR":
      return t("vcSchema.select.YEAR");
    default:
      return unit;
  }
};

const headersList = ref([{ key: "", value: "" }]);

const addRow = () => {
  headersList.value.push({ key: "", value: "" });
  resetStatus();
};

const removeRow = (index) => {
  headersList.value.splice(index, 1);
  resetStatus();
};

const resetStatus = () => {
  verifyStatusError.value = true;
  verifyStatusSuccess.value = true;
  responseHttpStatus.value = "";
  responseMsg.value = "";
};

const responseHttpStatus = ref("");
const responseMsg = ref("");

const testConnection = async () => {
  verifyStatusSuccess.value = true;
  verifyStatusError.value = true;

  const isValid = await formRef.value.validate();

  if (isValid) {
    const params = createParamsObj();

    const { headers, httpMethod, url, vcId, vcSerialNo } = params;

    const testParams = {
      vcId,
      vcSerialNo,
      url901i: url,
      httpMethod,
      headers
    };

    const response = await checkAPI(testParams);
    responseHttpStatus.value = response.httpStatus;
    responseMsg.value = response.msg;

    if (response.httpStatus !== "999" && response.httpStatus !== "404") {
      verifyStatusSuccess.value = false;
    } else {
      verifyStatusError.value = false;
    }
  } else {
    $n.error(t("requiredFields"));
  }
};

const verifyStatusError = ref(true);
const verifyStatusSuccess = ref(true);

watch(
  () => params.value.url,
  (newValue, oldValue) => {
    if (newValue !== oldValue) {
      resetStatus();
    }
  }
);

watch(
  headersList,
  (newHeaders, oldHeaders) => {
    if (newHeaders) {
      resetStatus();
    }
  },
  { deep: true }
);
</script>

<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
</style>
