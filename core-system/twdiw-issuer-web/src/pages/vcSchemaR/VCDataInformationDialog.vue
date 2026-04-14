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
        <!-- 產生 VC 資料 -->
        <div class="text-h6">{{ t("vcSchema.dialog.buildVcDataTitle") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          :style="{
            height: isMobile ? '45vh' : '70vh',
            'max-height': '100vh',
            'min-height': '60vh'
          }"
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
          <!-- QR Code 頁面 -->
          <q-card-section
            v-if="Object.keys(responseObj).length > 0"
            class="q-px-md"
          >
            <div class="q-mx-sm">
              <div class="row justify-center">
                <div :class="isMobile ? 'col-12' : 'col-6 text-left q-my-sm'">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.serialNo") }}：</span
                    >
                    <span class="text-h6-cus word-break">{{
                      responseObj.vcItem.serialNo
                    }}</span>
                  </div>
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.name") }}：</span
                    >
                    <span class="text-h6-cus word-break">{{
                      responseObj.vcItemName
                    }}</span>
                  </div>
                  <div
                    v-if="responseObj.vcCid"
                    :class="[isMobile ? 'column' : 'info-block']"
                  >
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.expirationDate") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break"
                      >{{ yyyyMMddHHmmss(responseObj.expired) }}
                    </span>
                  </div>
                </div>
                <div
                  :class="isMobile ? 'col-12' : 'col-6 text-center'"
                  :style="{
                    border: '2px dashed #ccc',
                    minHeight: isVerify ? '480px' : '430px'
                  }"
                >
                  <div class="column justify-start q-my-sm q-mx-xs">
                    <div class="text-center">
                      <p :class="isMobile ? 'text-subtitle1' : 'text-h6'">
                        {{ t("vcSchema.vcData.qrCodeTitle") }}
                      </p>
                      <p class="text-left q-px-sm alert-text">
                        {{ t("vcSchema.vcData.qrCodeNotice") }}
                      </p>
                    </div>
                    <div
                      class="column justify-start items-center"
                      style="min-height: 340px"
                    >
                      <template
                        v-if="showQRCode && !responseObj.vcCid && !isExpired"
                      >
                        <div
                          v-if="isVerify"
                          class="full-width row justify-around q-my-sm"
                        >
                          <q-btn
                            unelevated
                            outline
                            class="col-auto"
                            color="indigo-6"
                            @click="goBackToVerify"
                            style="width: 160px"
                            >{{
                              t("vcSchema.dialog.button.resetOtpCode")
                            }}</q-btn
                          >
                        </div>
                        <div
                          v-else
                          class="full-width row justify-around q-my-sm"
                        >
                          <q-btn
                            unelevated
                            outline
                            :class="['col-auto', isMobile ? 'q-mb-sm' : '']"
                            color="indigo-6"
                            @click="regenerateQRCode"
                            style="width: 160px"
                            >{{
                              t("vcSchema.dialog.button.regenerateQRCode")
                            }}</q-btn
                          >
                        </div>
                        <div
                          :style="{
                            height: isMobile ? '160px' : '200px'
                          }"
                        >
                          <img
                            :src="responseObj.qrCode"
                            :style="{
                              width: isMobile ? '160px' : '200px',
                              height: isMobile ? '160px' : '200px'
                            }"
                          />
                        </div>
                        <p class="text-subtitle1">
                          {{ t("vcSchema.vcData.countdown") }}：<span
                            class="text-grey-7"
                          >
                            {{ `0${Math.floor(countdown / 60)}` }}:{{
                              String(countdown % 60).padStart(2, "0")
                            }}
                          </span>
                        </p>
                        <div class="q-my-sm">
                          <q-btn
                            unelevated
                            outline
                            class="col-auto"
                            color="primary"
                            type="a"
                            :label="t('vcSchema.dialog.button.openWithMobile')"
                            :href="responseObj.deepLink"
                            target="_blank"
                            rel="noopener noreferrer"
                            style="width: 160px"
                          ></q-btn>
                        </div>
                      </template>
                      <template v-if="regenerateLoading">
                        <q-spinner-ios
                          class="col text-grey full-height items-center"
                          size="lg"
                        />
                      </template>

                      <template v-else-if="responseObj.vcCid">
                        <p
                          class="col text-h6 items-center justify-center row text-positive"
                        >
                          {{ t("vcSchema.vcData.result.success") }}
                        </p>
                      </template>
                    </div>
                  </div>
                </div>
              </div>

              <div class="alert-text q-mt-sm">
                {{ t("vcSchema.vcData.inputNotice") }}
              </div>
              <q-table
                :rows="JSON.parse(responseObj.content)"
                :columns="isMobileResponseColumns"
                dense
                flat
                :hide-header="dialogLoading"
                class="sticky-header no-height"
                row-key="id"
                :loading="dialogLoading"
                :rows-per-page-options="[10, 20, 50, 0]"
                :hide-no-data="!dialogLoading"
              >
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

                <template #pagination="scope">
                  <table-pagination
                    :scope="scope"
                    :rows-number="computedPagination"
                  />
                </template>
              </q-table>
            </div>
          </q-card-section>

          <!-- 填寫驗證碼畫面 -->
          <q-card-section v-else-if="hasItemData && isVerify" class="q-px-md">
            <div class="q-mx-sm">
              <div class="row justify-center">
                <div :class="isMobile ? 'col-12' : 'col-6 text-left q-my-sm'">
                  <div class="info-block">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.name") }}：
                    </span>
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ name }}
                    </span>
                  </div>
                  <div class="info-block">
                    <span class="text-h6-cus text-grey-7">
                      {{ t("vcSchema.info.serialNo") }}：
                    </span>
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ serialNo }}
                    </span>
                  </div>
                  <!-- 若無填寫到日，顯示預設有效期間 -->
                  <div
                    v-if="!expirationDate"
                    :class="[isMobile ? 'column' : 'info-block']"
                  >
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.defaultValidityPeriod") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ lengthExpire }}
                      {{ convertUnitType(unitTypeExpire) }}
                    </span>
                  </div>
                </div>
                <div
                  :class="isMobile ? 'col-12' : 'col-6 text-center'"
                  style="border: 2px dashed #ccc; min-height: 480px"
                >
                  <div
                    class="column justify-between q-pa-sm"
                    style="height: 100%"
                  >
                    <p class="text-left q-px-sm alert-text">
                      {{ t("vcSchema.vcData.otpModeNotice") }}
                    </p>
                    <div class="row justify-center">
                      <q-input
                        ref="otp1"
                        v-model="verifyCode_1"
                        class="verify-opt-input q-mr-xs"
                        type="text"
                        outlined
                        square
                        input-class="text-center text-bold "
                        @update:model-value="
                          (val) => handleOtpInput(val.replace(/[^0-9]/g, ''), 1)
                        "
                        @keypress="
                          (e) => !/[0-9]/.test(e.key) && e.preventDefault()
                        "
                        :rules="[(val) => /^[0-9]*$/.test(val)]"
                      />
                      <q-input
                        ref="otp2"
                        v-model="verifyCode_2"
                        class="verify-opt-input q-ml-xs"
                        type="text"
                        outlined
                        square
                        input-class="text-center text-bold "
                        @update:model-value="
                          (val) => handleOtpInput(val.replace(/[^0-9]/g, ''), 2)
                        "
                        mask="#"
                      />
                    </div>
                    <div class="row justify-center q-mb-sm">
                      <q-btn
                        unelevated
                        :label="t('nextStep')"
                        color="primary"
                        size="16px"
                        @click="checkVerifyCodeAndSubmit"
                        dense
                        :style="{ width: '100px' }"
                        :loading="vcDataDialogLoading"
                        :disable="vcDataDialogLoading"
                      />
                    </div>
                  </div>
                </div>
              </div>
              <div class="alert-text q-mt-sm">
                {{ t("vcSchema.vcData.inputNotice") }}
              </div>
              <q-table
                :rows="updatedVCSchemaDetail"
                :columns="isMobileResponseColumns"
                dense
                flat
                :hide-header="dialogLoading"
                class="sticky-header no-height"
                row-key="id"
                :loading="dialogLoading"
                :rows-per-page-options="[10, 20, 50, 0]"
                :hide-no-data="!dialogLoading"
              >
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

                <template #pagination="scope">
                  <table-pagination
                    :scope="scope"
                    :rows-number="computedPagination"
                  />
                </template>
              </q-table>
            </div>
          </q-card-section>

          <!-- 填寫vc-item-data 資料 -->
          <q-card-section v-else style="max-width: 1200px; margin: 0 auto">
            <div class="q-mx-sm q-my-sm">
              <div class="row justify-center">
                <div :class="isMobile ? 'col-12' : 'col q-mt-xs'">
                  <div class="info-block">
                    <span class="text-h6-cus text-grey-7">
                      {{ t("vcSchema.table.serialNo") }}：
                    </span>
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ serialNo }}
                    </span>
                  </div>
                  <div class="info-block">
                    <span class="text-h6-cus text-grey-7">
                      {{ t("vcSchema.info.name") }}：
                    </span>
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ name }}
                    </span>
                  </div>
                  <div class="info-block">
                    <span class="text-h6-cus text-grey-7">
                      {{ t("vcSchema.defaultValidityPeriod") }}：
                    </span>
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ lengthExpire }}
                      {{ convertUnitType(unitTypeExpire) }}
                    </span>
                  </div>
                </div>
                <div :class="isMobile ? 'col-12' : 'col'">
                  <div class="row items-start">
                    <div
                      :class="['row', isMobile ? 'col-12' : 'col-auto q-mt-xs']"
                    >
                      <span class="text-h6-cus text-grey-7">{{
                        t("vcSchema.expirationDate")
                      }}</span>

                      <BaseTooltip
                        icon="info_outline"
                        color="primary"
                        size="sm"
                        :title="t('vcSchema.expirationDate')"
                        :text="t('vcSchema.expirationDateNotice')"
                        :position="{ top: '40px', left: '55px' }"
                        :iconProps="{ class: 'q-ml-xs' }"
                        width="320px"
                      />

                      <span class="text-h6-cus text-grey-7 q-ml-xs">：</span>
                    </div>
                    <div :class="isMobile ? 'col-12' : 'col-5 q-ml-xs'">
                      <q-input
                        v-model="expirationDate"
                        outlined
                        dense
                        type="datetime"
                        :rules="rulesAll('dateRules')"
                      >
                        <template v-slot:append>
                          <q-icon name="event" class="cursor-pointer">
                            <q-popup-proxy
                              transition-show="scale"
                              transition-hide="scale"
                              anchor="bottom right"
                              self="top right"
                              :offset="[11, 10]"
                            >
                              <q-date
                                v-model="expirationDate"
                                mask="YYYY-MM-DD"
                                today-btn
                                minimal
                              >
                                <div class="row items-center justify-end">
                                  <q-btn
                                    v-close-popup
                                    :label="t('close')"
                                    color="primary"
                                    flat
                                  />
                                </div>
                              </q-date>
                            </q-popup-proxy>
                          </q-icon>
                        </template>
                      </q-input>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="alert-text">
              {{ t("vcSchema.vcData.inputNotice") }}
            </div>

            <q-form ref="formRef" greedy>
              <q-table
                dense
                flat
                :hide-header="dialogLoading"
                class="sticky-header no-height"
                :rows="VCSchemaDetail"
                :columns="isMobileColumns"
                row-key="id"
                :loading="dialogLoading"
                :pagination="{ rowsPerPage: 0 }"
                :rows-per-page-options="[10, 20, 50, 0]"
                :hide-no-data="!dialogLoading"
              >
                <template v-slot:body-cell-cname="props">
                  <q-td
                    :props="props"
                    :style="{
                      width: '400px',
                      'white-space': 'normal',
                      'word-break': 'break-word'
                    }"
                  >
                    {{ props.row.cname }}
                  </q-td>
                </template>

                <template v-slot:body-cell-ename="props">
                  <q-td
                    :props="props"
                    :style="{
                      width: '400px',
                      'white-space': 'normal',
                      'word-break': 'break-word'
                    }"
                  >
                    {{ props.row.ename }}
                  </q-td>
                </template>

                <template v-slot:body-cell-type="props">
                  <q-td :props="props">
                    {{ typeMethod(props.row.type) }}
                  </q-td>
                </template>

                <template v-slot:body-cell-content="props">
                  <q-td :props="props">
                    <q-input
                      outlined
                      class="col"
                      v-model="inputData[props.row.id]"
                      :rules="[
                        ...(props.row.isRequired
                          ? [(val) => !!val || t('input', { input: t('data') })]
                          : []),

                        (val) => {
                          if (!val) return true;
                          const regex = new RegExp(
                            props.row.regularExpression.regularExpression
                          );

                          // 取得自定義錯誤訊息
                          const customErrorMessage =
                            props.row.regularExpression.ruleType === 'deny'
                              ? t('vcSchema.vcData.ruleTypeDeny') // deny 的錯誤訊息
                              : props.row.regularExpression.errorMsg; // allow 的錯誤訊息

                          if (
                            props.row.regularExpression.ruleType === 'allow'
                          ) {
                            // allow: 必須完全符合純中文規則
                            return regex.test(val) || customErrorMessage;
                          } else if (
                            props.row.regularExpression.ruleType === 'deny'
                          ) {
                            // deny: 如果符合規則就顯示錯誤
                            return !regex.test(val) || customErrorMessage;
                          }
                          return true;
                        },
                        (val) => {
                          if (!val) return true;
                          return (
                            val.length <= 18 ||
                            t('valid.maxLength', { max: 18 })
                          );
                        }
                      ]"
                      hide-bottom-space
                      :placeholder="t('input', { input: t('data') })"
                      dense
                      :style="{
                        height: '60px',
                        paddingTop: '10px',
                        width: isMobile && '240px'
                      }"
                      :error="Boolean(regularError[props.row.ename])"
                      :error-message="regularError[props.row.ename] || ''"
                      @update:model-value="regularError[props.row.ename] = ''"
                    >
                      <template v-slot:error>
                        <div class="error-message">{{ errorMessage }}</div>
                      </template>
                    </q-input>
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

                <template #pagination="scope">
                  <table-pagination
                    :scope="scope"
                    :rows-number="computedPagination"
                  />
                </template>
              </q-table>
            </q-form>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>

      <q-card-actions
        align="right"
        v-if="
          Object.keys(responseObj).length <= 0 && !(hasItemData && isVerify)
        "
      >
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          size="16px"
          @click="onCancelClick"
          dense
          :style="{ width: '100px' }"
          :disable="vcDataDialogLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
          :loading="vcDataDialogLoading"
          :disable="vcDataDialogLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, reactive, computed, onBeforeUnmount, watch } from "vue";
import { useDialogPluginComponent } from "quasar";
import { useVcSchemaStore } from "stores/vcSchema";
import { storeToRefs } from "pinia";
import { useQuasar } from "quasar";
import { useRemoveVCStore } from "stores/removeVC";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
import { useRulesAll } from "utils/rules";
import { format } from "src/utils/dateFormat";
import BaseTooltip from "src/components/BaseTooltip.vue";

const { rulesAll } = useRulesAll();
const { yyyyMMddHHmmss } = format();
const vcSchema = useVcSchemaStore();
const $q = useQuasar();
const $n = useNotify();
const { VCSchemaDetail, dialogLoading, vcDataDialogLoading, regularError } =
  storeToRefs(vcSchema);
const { getVCSchemaDetail, resetVCSchemaDetail, createVCData } = vcSchema;
const { t } = useI18n();
const removeVCStore = useRemoveVCStore();
const { checkVCDetail } = removeVCStore;
const regenerateLoading = ref(false);
const hasItemData = ref(false);
const updatedVCSchemaDetail = ref([]);
const verifyCode_1 = ref("");
const verifyCode_2 = ref("");
const otp1 = ref(null);
const otp2 = ref(null);
const finalCode = ref("");
const expirationDate = ref("");

const handleOtpInput = (val, index) => {
  const digits = val.replace(/[^0-9]/g, "").slice(-1);
  const codeRefs = [verifyCode_1, verifyCode_2];
  const currentCode = codeRefs[index - 1];

  currentCode.value = digits;

  if (digits.length === 1) {
    if (index === 1) {
      otp2.value?.$el.querySelector("input")?.focus();
    }
  }

  if (digits.length === 0) {
    if (index === 2) {
      otp1.value?.$el.querySelector("input")?.focus();
    }
  }

  if (verifyCode_1.value.length === 1 && verifyCode_2.value.length === 1) {
    finalCode.value = verifyCode_1.value + verifyCode_2.value;
  }
};

const props = defineProps({
  row: Object,
  id: Number,
  serialNo: String,
  name: String,
  unitTypeExpire: String,
  lengthExpire: Number,
  isVerify: Boolean,
  crDatetime: String
});

const {
  id,
  serialNo,
  name,
  unitTypeExpire,
  lengthExpire,
  isVerify,
  crDatetime
} = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const responseObj = ref({});

const onOKClick = async () => {
  // 強制觸發所有輸入框的驗證
  // 驗證失敗時，所有不合規則的輸入框都會顯示錯誤訊息
  const hasValidationError = !(await formRef.value
    .validate()
    .catch(() => false));

  // 檢查所有欄位是否為空，並略過非必填欄位
  const hasEmptyField = VCSchemaDetail.value.some((item) => {
    if (item.isRequired) {
      const inputValue = inputData[item.id];
      return (
        inputValue === null ||
        inputValue === undefined ||
        inputValue.toString().trim() === ""
      );
    }
    return false;
  });

  if (hasValidationError || hasEmptyField) {
    $n.error(t("requiredFields"));
    return;
  }

  // 若到期日有資料，檢核日期格式
  if (expirationDate.value) {
    const expiredDate = new Date(expirationDate.value);
    const today = new Date();
    const issuerDate = new Date(crDatetime.value?.slice(0, 10));
    today.setHours(0, 0, 0, 0);
    expiredDate.setHours(0, 0, 0, 0);
    issuerDate.setHours(0, 0, 0, 0);

    if (isNaN(expiredDate.getTime())) {
      $n.error(t("vcSchema.vcData.expirationDateError"));
      return;
    }

    if (expiredDate < today || expiredDate < issuerDate) {
      $n.error(t("vcSchema.vcData.expirationDateInvalid"));
      return;
    }
  }

  updatedVCSchemaDetail.value = VCSchemaDetail.value.map((item) => ({
    ...item,
    content: inputData[item.id] || ""
  }));

  if (updatedVCSchemaDetail.value) {
    hasItemData.value = true;
  }

  if (!isVerify.value) {
    sendVCDataMethod("new");
  }
};

// 回到驗證碼畫面
const goBackToVerify = async () => {
  responseObj.value = {};
  stopPolling();
  isExpired.value = false;
  showQRCode.value = true;
  verifyCode_1.value = "";
  verifyCode_2.value = "";
  finalCode.value = "";
};

const sendVCDataMethod = async (type) => {
  const params = {
    content: "",
    valid: 1,
    vcId: id.value,
    vcCid: serialNo.value,
    fields: updatedVCSchemaDetail.value,
    txCode: finalCode.value,
    expiredDate: expirationDate.value
      ? expirationDate.value.replace(/-/g, "")
      : ""
  };

  const response = await createVCData(params, type);
  if (response) {
    responseObj.value = response;
    // 開始輪詢
    startPolling();
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {
  resetVCSchemaDetail();
  stopPolling();
  regularError.value = {};
  Object.keys(inputData).forEach((key) => {
    inputData[key] = "";
  });
};

const checkVerifyCodeAndSubmit = () => {
  if (/^[0-9][0-9]$/.test(finalCode.value)) {
    sendVCDataMethod("new");
  } else {
    $n.error(t("vcSchema.valid.checkVerifyCode"));
  }
};

const rows = ref([{ chinese: "", english: "" }]);

const formRef = ref(null);

const inputData = reactive({});

const columns = [
  {
    name: "type",
    required: true,
    label: t("fields.type"),
    align: "left",
    field: "type", // 直接使用 type 值
    format: (val) => typeMethod(val), // 使用 typeMethod 來格式化顯示
    sortable: true,
    style: "width: 60px;"
  },
  {
    name: "isRequired",
    label: t("fields.isRequired"),
    align: "center",
    field: (row) =>
      row.isRequired ? t("fields.required") : t("fields.notRequired")
  },
  {
    name: "cname",
    label: t("fields.cname"),
    align: "center",
    // 修改field來處理不同類型的cname
    field: (row) =>
      typeof row.cname === "object" ? row.cname.cname : row.cname
  },
  {
    name: "ename",
    label: t("fields.ename"),
    align: "center",
    field: "ename"
  },
  {
    name: "content",
    label: t("data"),
    align: "center",
    field: "content",
    style: "width: 420px; padding: 16px 4px;"
  },
  {
    name: "description",
    label: t("regularExpression.table.description"),
    field: (row) => row.regularExpression?.description || "",
    align: "center"
  }
];

const desktopStyle = {
  description: "width: 420px; white-space: normal; word-break: break-word"
};

const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return columns;
  }

  return columns.map((col) => ({
    ...col,
    style: desktopStyle[col.name] || col.style
  }));
});

const typeMethod = (type) => {
  if (type === "BASIC") {
    return t("vcSchema.select.basic");
  } else if (type === "NORMAL") {
    return t("vcSchema.select.normal");
  }
  return t("vcSchema.select.custom");
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
      return unit; // 如果不是已知的單位，則返回原始值
  }
};

// 初始化 inputData
if (columns.value) {
  columns.value.forEach((item) => {
    inputData[item.content] = "";
  });
}

const onDialogShow = () => {
  if (id.value) {
    getVCSchemaDetail(id.value);
  }
};

const computedPagination = computed(() => {
  return VCSchemaDetail.value?.length || 0;
});

const responseColumns = [
  {
    name: "type",
    label: t("fields.type"),
    field: "type",
    align: "left",
    format: (val) => typeMethod(val),
    sortable: true,
    style: "width: 80px"
  },
  {
    name: "isRequired",
    required: true,
    label: t("fields.isRequired"),
    align: "center",
    field: (row) =>
      row.isRequired ? t("fields.required") : t("fields.notRequired"),
    style: "width: 100px;"
  },
  {
    name: "cname",
    label: t("fields.cname"),
    field: "cname",
    align: "center"
  },
  {
    name: "ename",
    label: t("fields.ename"),
    field: "ename",
    align: "center"
  },
  {
    name: "content",
    label: t("data"),
    field: "content",
    align: "center",
    style: "width: 420px"
  }
];

const desktopResponseStyle = {
  cname: "width: 240px; white-space: normal; word-break: break-word",
  ename: "width: 240px; white-space: normal; word-break: break-word",
  content: "width: 240px; white-space: normal; word-break: break-word;"
};

const isMobileResponseColumns = computed(() => {
  if (isMobile.value) {
    return responseColumns;
  }

  return responseColumns.map((col) => ({
    ...col,
    style: desktopResponseStyle[col.name] || col.style
  }));
});

const showQRCode = ref(false);
const isExpired = ref(false);
const countdown = ref(300);
const countdownInterval = ref(null);

// 添加輪詢相關函數
const startCountdown = () => {
  // 計算剩餘秒數
  const calculateRemainingTime = () => {
    if (!responseObj.value?.crDatetime) return 0;
    const crTime = new Date(responseObj.value.crDatetime);
    const now = new Date();
    const validUntil = new Date(crTime.getTime() + 5 * 60 * 1000); // 五分鐘 60
    const remainingTime = Math.floor((validUntil - now) / 1000);
    return Math.max(0, remainingTime);
  };

  countdown.value = calculateRemainingTime();
  countdownInterval.value = setInterval(() => {
    countdown.value = calculateRemainingTime();
    if (countdown.value <= 0) {
      stopCountdown();
      isExpired.value = true;
      showQRCode.value = false;
    }
  }, 1000);
};

const stopCountdown = () => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value);
    countdownInterval.value = null;
  }
};

// 用於追蹤並控制輪循狀態
const isPolling = ref(false);
let timeoutId = null;

// 開始輪循
const startPolling = async () => {
  if (responseObj.value.id) {
    showQRCode.value = true;
    startCountdown();
    isPolling.value = true;

    // 定義一個進行API呼叫的函數
    const pollAPI = async () => {
      // 如果已經停止輪循，直接返回
      if (!isPolling.value) return;

      try {
        const now = new Date();
        const crTime = new Date(responseObj.value.crDatetime);
        const validUntil = new Date(crTime.getTime() + 5 * 60 * 1000); // 5分鐘

        if (now >= validUntil) {
          stopPolling();
          isExpired.value = true;
          showQRCode.value = false;
          return;
        }

        // 呼叫 API 檢查狀態
        const response = await checkVCDetail(responseObj.value.id);

        // 再次檢查是否應該繼續輪循
        if (!isPolling.value) return;

        // 只有在成功獲取回應後才繼續
        if (response) {
          if (response?.valid === 0) {
            responseObj.value.vcCid = response?.vcCid;
            responseObj.value.valid = response?.valid;
            responseObj.value.expired = response?.expired;
            stopPolling();
          } else if (isPolling.value) {
            // 如果有回應但狀態未變更，則在0.5秒後再次輪循
            timeoutId = setTimeout(pollAPI, 500);
          }
        } else if (isPolling.value) {
          // 如果沒有回應，也等待0.5秒後再試
          timeoutId = setTimeout(pollAPI, 500);
        }
      } catch (error) {
        // 發生錯誤時，等待0.5秒後再試（如果還在輪循中）
        console.error(`${t("vcSchema.vcData.pollError")}：${error}`);
        if (isPolling.value) {
          timeoutId = setTimeout(pollAPI, 500);
        }
      }
    };

    // 開始第一次輪循
    pollAPI();
  }
};

// 停止輪循
const stopPolling = () => {
  isPolling.value = false;
  if (timeoutId) {
    clearTimeout(timeoutId);
    timeoutId = null;
  }
  stopCountdown();
  showQRCode.value = false;
};

onBeforeUnmount(() => {
  stopPolling();
  stopCountdown();
});

const regenerateQRCode = async () => {
  regenerateLoading.value = true;
  try {
    isExpired.value = false;
    showQRCode.value = false;
    stopPolling();
    await sendVCDataMethod("regenerateQRCode");
  } finally {
    regenerateLoading.value = false;
  }
};

watch(updatedVCSchemaDetail, (newVal) => {
  if (!Array.isArray(newVal)) {
    updatedVCSchemaDetail.value = [];
  }
});
</script>

<style scoped>
.q-table td.q-td {
  height: 126px !important; /* 設定固定高度 */
}

.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

:deep(.q-field__messages) {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
  line-height: 1.2;
  padding: 2px 0;
}
.verify-opt-input {
  border: 2px solid #ccc;
  width: 55px;
  height: 60px;
  font-size: 30px;
}
</style>
