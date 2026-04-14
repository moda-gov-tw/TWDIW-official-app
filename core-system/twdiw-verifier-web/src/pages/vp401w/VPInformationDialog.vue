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
        <div class="text-h6">{{ t("vp.dialog.title.detail") }}</div>
        <q-btn
          flat
          round
          color="white"
          icon="close"
          @click="onCancelClick"
        ></q-btn>
      </q-card-section>
      <q-scroll-area
        class="col"
        style="height: 80vh"
        :thumb-style="{
          right: '4px',
          borderRadius: '5px',
          background: '#4E4E4E',
          width: '5px',
          opacity: 0.75
        }"
      >
        <!-- 上方資訊 -->
        <q-card-section class="q-pt-none">
          <div class="q-px-sm">
            <div class="row q-my-md">
              <!-- 左上 基本資料-->
              <div
                :class="[
                  'column',
                  isMobile ? 'col-12' : 'col-6 text-left q-my-sm'
                ]"
              >
                <!-- VP 模板資料 -->
                <div class="col-auto">
                  <div
                    :class="['q-mb-xs', isMobile ? '' : 'q-mr-lg']"
                    style="border-bottom: 2px solid var(--q-positive)"
                  >
                    <p
                      :class="[
                        'text-h6-cus text-positive',
                        isMobile ? 'text-weight-bold' : ''
                      ]"
                    >
                      {{ t("vp.detailTitle") }}
                    </p>
                  </div>
                  <div class="q-ml-xs">
                    <!-- 驗證服務代碼 (ref) -->
                    <div :class="[isMobile ? '' : ' info-block']">
                      <span class="text-h6-cus">{{ t("vp.vpRef") }}：</span>
                      <span class="text-h6-cus q-ma-none word-break">
                        {{ `${props.row.businessId}_${props.row.serialNo}` }}
                      </span>
                    </div>

                    <!-- VP 代碼 -->
                    <div :class="[isMobile ? '' : ' info-block']">
                      <span class="text-h6-cus"
                        >{{ t("vp.serialNo.label") }}：</span
                      >
                      <span class="text-h6-cus q-ma-none word-break">
                        {{ `${props.row.serialNo}` }}
                      </span>
                    </div>

                    <!-- VP 名稱 -->
                    <div :class="[isMobile ? '' : ' info-block']">
                      <span class="text-h6-cus"
                        >{{ t("vp.name.label") }}：</span
                      >
                      <span class="text-h6-cus q-ma-none word-break">
                        {{ `${props.row.name}` }}
                      </span>
                    </div>

                    <!-- VP 授權目的 -->
                    <div :class="[isMobile ? '' : ' info-block']">
                      <span class="text-h6-cus"
                        >{{ t("vp.purpose.label") }}：</span
                      >
                      <span class="text-h6-cus q-ma-none word-break">
                        {{ `${props.row.purpose}` }}
                      </span>
                    </div>
                  </div>
                  <!-- 分隔線 -->
                  <div
                    class="q-mt-md q-mb-xs q-mr-lg"
                    style="border-bottom: 2px solid var(--q-dark)"
                  ></div>
                  <q-btn
                    class="q-mt-sm q-mb-sm text-indigo-6"
                    :icon="expandIcon()"
                    :label="t('vp.btn.advancedFeature')"
                    color="indigo-2"
                    @click="showMore = !showMore"
                  />
                  <div v-if="showMore">
                    <!-- 靜態 QR Code -->
                    <div class="row full-width">
                      <div class="info-block">
                        <span class="text-h6-cus">
                          <!-- 是否啟模式： -->
                          {{ t("vp.model.label") }}：
                        </span>
                        <span class="text-h6-cus word-break">
                          {{
                            props.row.model.value === "0"
                              ? t("vp.model.noChoose")
                              : getModelLabel(props.row.model.value)
                          }}
                        </span>
                      </div>
                    </div>
                    <!-- 組織業務系統 URL -->
                    <div
                      v-if="props.row.model.value === '1'"
                      class="row full-width"
                    >
                      <div class="info-block q-mr-lg">
                        <span class="text-h6-cus">
                          {{ t("vp.serviceUrl.label") }}：
                        </span>
                        <span class="text-h6-cus q-ma-none word-break">
                          {{ props.row.verifierServiceUrl }}
                        </span>
                      </div>
                    </div>
                    <!-- Call Back URL -->
                    <div
                      v-if="props.row.model.value === '1'"
                      class="row full-width"
                    >
                      <div class="info-block q-mr-lg">
                        <span class="text-h6-cus">
                          {{ t("vp.callBackUrl.label") }}：
                        </span>
                        <span class="text-h6-cus q-ma-none word-break">
                          {{ props.row.callBackUrl }}
                        </span>
                      </div>
                    </div>

                    <!-- Offline 模式 -->
                    <div
                      v-if="props.row.model.value === '2'"
                      class="row full-width"
                    >
                      <div class="info-block">
                        <span class="text-h6-cus">
                          <!-- 模組加密： -->
                          {{ t("vp.encryptEnabled.label") }}：
                        </span>
                        <span class="text-h6-cus word-break">
                          {{
                            props.row.isEncryptEnabled
                              ? t("vp.encryptEnabled.yes")
                              : t("vp.encryptEnabled.no")
                          }}
                        </span>
                      </div>
                    </div>
                    <div
                      v-if="
                        props.row.model.value === '2' &&
                        props.row.isEncryptEnabled
                      "
                      class="row full-width q-mb-md"
                    >
                      <div class="info-block">
                        <span class="text-h6-cus">
                          <!-- TAG -->
                          {{ t("vp.tag.label") }}：
                        </span>
                        <span class="text-h6-cus word-break">
                          {{ props.row.tag }}
                        </span>
                      </div>
                    </div>
                    <div
                      v-if="
                        props.row.model.value === '1' &&
                        props.row.fields.length > 0
                      "
                      :class="classStyle"
                    >
                      <div class="info-block">
                        <span class="text-h6-cus">
                          <!-- 欄位對外名稱、欄位名稱(英)、限定資料格式 -->
                          {{ t("vp.customFields.name") }}：
                        </span>
                        <span
                          class="text-h6-cus q-ma-none word-break"
                          v-for="(data, index) in props.row.fields"
                          :key="index"
                        >
                          {{
                            `${data.description}/ ${data.cname}/ ${data.ename}/ ${data.regex}/ ${data.value}`
                          }}
                        </span>
                      </div>
                    </div>
                    <!-- 分隔線 -->
                    <div
                      class="q-mr-lg"
                      style="border-bottom: 2px solid var(--q-dark)"
                    ></div>
                  </div>
                </div>

                <!-- 屬性資料 -->
                <div
                  v-if="isSuccessVerifyQRCode"
                  class="col text-h5 justify-center row full-width"
                >
                  <div class="col items-center text-left q-mt-lg">
                    <div
                      class="q-mb-xs q-mr-lg"
                      style="border-bottom: 2px solid var(--q-positive)"
                    >
                      <p class="text-h6 text-positive">
                        {{ t("vp.attributeData") }}
                      </p>
                    </div>

                    <template v-if="claimsData && claimsData.length">
                      <div
                        v-for="data in claimsData"
                        :key="data.credentialType"
                        class="text-h6-cus q-mb-xs q-pl-xs"
                      >
                        {{ t("vp.vcName") }}：{{ data.vcName }}
                        <div class="row items-center q-gutter-x-sm">
                          <q-icon name="label" color="indigo-6" />
                          <span>
                            {{
                              data.claims
                                .map((i) => `${i.cname}：${i.value}`)
                                .join(" / ")
                            }}
                          </span>
                        </div>
                      </div>
                    </template>
                  </div>
                </div>
              </div>

              <!-- 右上 QR Code 資料 -->
              <div
                :class="isMobile ? 'col-12' : 'col-6 flex flex-center'"
                :style="{
                  border: '2px dashed #ccc',
                  minHeight: '430px',
                  borderRadius: '10px'
                }"
              >
                <div class="q-mx-lg q-py-lg">
                  <div style="max-width: 600px">
                    <q-card
                      style="border-radius: 5px 5px 0px 0px; height: 550px"
                    >
                      <q-tabs
                        v-model="defaultTab"
                        class="text-indigo-6"
                        active-bg-color="indigo-6"
                        indicator-color="indigo-6"
                        active-class="text-white"
                        align="justify"
                        inline-label
                        style="border-radius: 5px 5px 0 0"
                        @update:model-value="onTabChanged"
                      >
                        <q-tab
                          name="general"
                          :label="t('vp.generalTab')"
                          style="width: 50px"
                        />
                        <q-tab
                          v-if="
                            props.row.model.value === '1' && props.staticQRCode
                          "
                          name="staticQRCode"
                          :label="t('vp.staticQRCodeTab')"
                          style="width: 50px"
                          no-caps
                        />
                      </q-tabs>

                      <q-separator></q-separator>

                      <q-tab-panels v-model="defaultTab" animated>
                        <q-tab-panel name="general">
                          <div class="column justify-start">
                            <div class="text-center">
                              <p
                                :class="isMobile ? 'text-subtitle1' : 'text-h6'"
                              >
                                {{ t("vp.qrCode.title") }}
                              </p>
                              <p class="text-left q-px-sm alert-text">
                                {{ t("vp.qrCode.notice") }}
                              </p>
                              <div
                                class="column justify-start items-center"
                                style="min-height: 340px"
                              >
                                <!-- 重新產生 QR Code 按鈕 -->
                                <div class="col-auto full-width q-my-sm">
                                  <q-btn
                                    outline
                                    color="indigo-6"
                                    @click="regenerateQRCode"
                                    style="width: 160px"
                                    no-caps
                                  >
                                    {{ t("vp.btn.regenerateQrCode") }}
                                  </q-btn>
                                </div>

                                <!-- QR Code Loading -->
                                <div
                                  class="col full-width column justify-start items-center"
                                >
                                  <div
                                    v-if="qrcodeLoading"
                                    style="width: 200px; height: 200px"
                                    class="row justify-center items-center q-mb-xs"
                                  >
                                    <q-spinner-ios
                                      class="col text-grey items-center"
                                      size="lg"
                                    />
                                  </div>
                                </div>

                                <!-- QR Code -->
                                <div
                                  v-if="
                                    isShowQRCode &&
                                    !isExpired &&
                                    !isFailVerifyQRCode
                                  "
                                >
                                  <q-img
                                    :src="qrCodeDetail.qrcode_image"
                                    style="width: 200px; height: 200px"
                                    class="q-mb-xs"
                                  />

                                  <!-- 驗證倒數時間 -->
                                  <p class="text-subtitle1">
                                    {{ t("vp.qrCode.countdown") }}：
                                    <span class="text-grey-7">
                                      {{ `0${Math.floor(countdown / 60)}` }}:{{
                                        String(countdown % 60).padStart(2, "0")
                                      }}
                                    </span>
                                  </p>

                                  <!-- 使用手機開啟 按鈕 -->
                                  <div class="q-my-sm">
                                    <q-btn
                                      unelevated
                                      outline
                                      color="primary"
                                      type="a"
                                      :label="t('vp.btn.openWithMobile')"
                                      :href="qrCodeDetail.auth_uri"
                                      target="_blank"
                                      rel="noopener noreferrer"
                                      style="width: 160px"
                                    />
                                  </div>
                                </div>

                                <!-- 顯示驗證結果：失敗 -->
                                <div
                                  v-if="isFailVerifyQRCode"
                                  class="col text-h6 justify-center row full-width text-negative"
                                >
                                  {{ t("vp.qrCode.result.fail") }}
                                </div>

                                <!-- 顯示驗證結果：成功 -->
                                <div
                                  v-if="isSuccessVerifyQRCode"
                                  class="col text-h6 justify-center row full-width text-positive"
                                >
                                  {{ t("vp.qrCode.result.success") }}
                                </div>
                              </div>
                            </div>
                          </div>
                        </q-tab-panel>
                        <q-tab-panel name="staticQRCode">
                          <!-- 匯出 QR Code -->
                          <div
                            v-if="props.staticQRCode"
                            class="row q-mt-sm q-mb-md q-mt-lg"
                            :class="isMobile ? '' : 'justify-center'"
                            style="height: 100%"
                          >
                            <div class="q-mt-lg" style="width: 100%">
                              <div class="row justify-center q-px-md">
                                <q-btn
                                  class="full-width text-indigo-6"
                                  :label="t('vp.btn.exportQrCode')"
                                  icon="open_in_new"
                                  size="md"
                                  color="indigo-2"
                                  dense
                                  @click="exportPng"
                                  no-caps
                                />
                              </div>
                              <div class="q-mt-md">
                                <div
                                  class="q-mb-md"
                                  style="position: relative; text-align: center"
                                >
                                  <q-img
                                    :src="props.qrCode.base64"
                                    style="
                                      height: 250px;
                                      width: 250px;
                                      margin: 0 auto;
                                    "
                                  />
                                </div>
                                <div class="word-break q-px-md q-pb-sm">
                                  {{ props.qrCode.url }}
                                </div>
                              </div>
                            </div>
                          </div>
                        </q-tab-panel>
                      </q-tab-panels>
                    </q-card>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </q-card-section>

        <!-- 下方資訊 -->
        <q-card-section class="flex justify-center q-px-lg">
          <div
            v-if="detailLoading"
            class="row justify-center items-center q-mb-xs"
          >
            <q-spinner-ios class="col text-grey items-center" size="lg" />
          </div>
          <group-data-detail
            v-if="!detailLoading && vpItemData"
            :formData="vpItemData"
          />
        </q-card-section>
      </q-scroll-area>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useVpStore } from "stores/vp";
import { useNotify } from "src/utils/plugin";
import GroupDataDetail from "./GroupDataDetail.vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const { dialogRef, onDialogCancel } = useDialogPluginComponent();
const vpStore = useVpStore();
const {
  qrcodeLoading,
  qrCodeDetail,
  vpDetailList,
  vpItemData,
  detailLoading,
  modelTypeList
} = storeToRefs(vpStore);
const {
  getVpDetail,
  getQrcode,
  getQrcodeValidate,
  resetDetail,
  downloadStaticQRCode
} = vpStore;
const $q = useQuasar();
const $n = useNotify();

const props = defineProps({
  row: Object,
  staticQRCode: Boolean,
  qrCode: String
});

// QR Code 相關
const isExpired = ref(false);
const isFailVerifyQRCode = ref(false); // 驗證 QR Code 是否失敗
const isSuccessVerifyQRCode = ref(false); // 驗證 QR Code 是否成功
const isShowQRCode = ref(false); // QR Code 是否顯示
const initCountdownVal = 300; // 驗證倒數（單位：秒）
const countdown = ref(300); // 初始驗證倒數，方便重置
const countdownTimer = ref(null); // 儲存倒數計時的 timer
const pollTimer = ref(null); // 儲存輪詢的 timer
// 初始Tab
const defaultTab = ref("general");
// 進階功能（未上線）
const showMore = ref(false);

// 資料相關
const claimsData = ref([]);

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 進階功能ICON
const expandIcon = () => {
  if (showMore.value) return "expand_less";
  else return "expand_more";
};

// 模式名稱
const getModelLabel = (value) => {
  if (value)
    return modelTypeList.value.find((data) => data.value === value).label;
};

// 對話框顯示時載入資料
const onDialogShow = () => {
  regenerateQRCode(false);
};

const onDialogHide = () => {
  stopPolling();
  stopCountdown();
  resetDetail();
};

// 取消處理
const onCancelClick = () => {
  onDialogCancel();
};

// 當tab不是general就停止建立輪詢函數
const onTabChanged = (newValue) => {
  if (newValue === "general") {
    regenerateQRCode();
  } else {
    stopPolling();
  }
};

// 重新產生 QR Code
const regenerateQRCode = async (isRegenerate) => {
  isExpired.value = false;
  isShowQRCode.value = false;
  isFailVerifyQRCode.value = false;

  try {
    const id = props.row.id;

    // 僅開啟 dialog 時 查詢VP 資訊
    if (!isRegenerate) {
      await getVpDetail(id);
    }

    if (vpItemData.value) {
      await getQrcode(vpItemData.value.vpItemId);
      startPolling();
    }

    if (vpDetailList.value?.length > 0) {
      await getQrcode(vpDetailList.value[0].vpItemId);
      startPolling();
    }
  } catch (error) {
    console.error(t("vp.qrCode.error.regenerate") + ":" + error);
    $n.error(t("vp.qrCode.error.regenerate"));
  }
};

// 建立輪詢函數
const startPolling = () => {
  // 啟動前先確保停止舊的
  stopPolling();

  const hasRequiredData =
    vpItemData.value?.vpItemId && qrCodeDetail.value?.transaction_id;
  if (!hasRequiredData) {
    console.log(t("vp.qrCode.error.missingRequiredParam"));
    return;
  }

  isShowQRCode.value = true;
  isExpired.value = false;
  isSuccessVerifyQRCode.value = false;
  isFailVerifyQRCode.value = false;
  startCountdown();

  // 每 2 秒輪詢一次
  pollTimer.value = setInterval(async () => {
    try {
      const result = await getQrcodeValidate(qrCodeDetail.value.transaction_id);

      if (result?.verifyResult === true) {
        // 驗證成功
        claimsData.value = result?.data;

        stopPolling();
        stopCountdown();
        isShowQRCode.value = false;
        isExpired.value = true;
        isSuccessVerifyQRCode.value = true;
      } else if (result?.verifyResult === false) {
        // 驗證失敗
        stopPolling();
        stopCountdown();
        isShowQRCode.value = false;
        isExpired.value = true;
        isFailVerifyQRCode.value = true;
      }
    } catch (error) {
      console.error(t("vp.qrCode.error.poll") + ":" + error);
      stopPolling();
      stopCountdown();
      isShowQRCode.value = false;
      isExpired.value = true;
      isFailVerifyQRCode.value = true;
    }
  }, 2000);
};

// 啟動驗證倒數函數
const startCountdown = () => {
  // 啟動前先確保停止舊的
  stopCountdown();

  // 重置倒數時間
  countdown.value = initCountdownVal;

  countdownTimer.value = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    }

    // 倒數結束
    if (countdown.value === 0) {
      stopCountdown();
      stopPolling();
      isExpired.value = true;
      isShowQRCode.value = false;
      isFailVerifyQRCode.value = true;
    }
  }, 1000);
};

// 停止驗證倒數相關函數
const stopCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
    countdownTimer.value = null;
  }
};

// 停止輪詢相關函數
const stopPolling = () => {
  if (pollTimer.value) {
    clearInterval(pollTimer.value);
    pollTimer.value = null;
  }
};

// 依是否使用靜態 QR Code 做調整
const classStyle = computed(() =>
  props.staticQRCode ? "row full-width" : "row full-width q-mb-md"
);

// 匯出 QR Code -> .png
const exportPng = () => {
  downloadStaticQRCode(props.qrCode.base64);
};

onBeforeUnmount(() => {
  stopPolling();
  stopCountdown();
});
</script>

<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
</style>
