<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px; margin: 0 auto"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="row justify-start items-center">
          <!-- 詳細資訊 -->
          <div class="text-h6">{{ t("vcSchema.table.information") }}</div>
          <div v-if="isTemp" class="text-h6">{{ t("vcSchema.tempTag") }}</div>
          <div v-if="!activated" class="text-h6">
            {{ t("vcSchema.inactiveTag") }}
          </div>
        </div>

        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          style="height: 70vh; max-height: 100vh"
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
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.id") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break"
                      >{{ id }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.name") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">{{
                      name
                    }}</span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.org") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ businessTWName }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.serialNo") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ serialNo }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.credentialType") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ `${businessId}_${serialNo}` }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.info.crDatetime") }} ：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ yyyyMMddHHmmss(crDatetime) }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.defaultValidityPeriod") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ lengthExpire }} {{ convertUnitType(unitTypeExpire) }}
                    </span>
                  </div>
                </div>

                <div class="row full-width">
                  <div :class="[isMobile ? 'column' : 'info-block']">
                    <span class="text-h6-cus text-grey-7"
                      >{{ t("vcSchema.ial") }}：</span
                    >
                    <span class="text-h6-cus q-ma-none word-break">
                      {{ convertIalValue(ial) }}
                    </span>
                  </div>
                </div>
                <!-- 分隔線 -->
                <q-separator
                  class="q-mb-md q-mt-xs"
                  size="3px"
                  color="grey-1"
                />
                <q-btn
                  class="q-mt-sm q-mb-xs text-indigo-6"
                  :icon="expandIcon()"
                  :label="t('vcSchema.dialog.button.showMore')"
                  color="indigo-2"
                  @click="showMore = !showMore"
                />
                <div v-show="showMore">
                  <div v-show="isVerify" class="row full-width">
                    <div class="info-block">
                      <span class="text-h6-cus text-grey-7"
                        >{{ t("vcSchema.info.isVerify") }}：</span
                      >
                      <span class="text-h6-cus q-ma-none word-break">
                        {{
                          isVerify
                            ? t("vcSchema.dialog.true")
                            : t("vcSchema.dialog.false")
                        }}
                      </span>
                    </div>
                  </div>
                  <!-- 靜態 QR Code -->
                  <div :class="classStyle">
                    <div class="info-block">
                      <span class="text-h6-cus text-grey-7">
                        <!-- 是否啟用靜態 QR Code 模式： -->
                        {{ t("vcSchema.info.isStaticQrcode") }}：
                      </span>
                      <span class="text-h6-cus q-ma-none word-break">
                        <!-- 否 / 外開 / WebView -->
                        {{
                          staticQRCode
                            ? t(`vcSchema.dialog.qrCode.${type}`)
                            : t(`vcSchema.dialog.qrCode.${staticQRCode}`)
                        }}
                      </span>
                    </div>
                  </div>
                  <!-- 組織業務系統 URL -->
                  <div v-if="staticQRCode" class="row full-width q-mb-md">
                    <div class="info-block">
                      <span class="text-h6-cus text-grey-7">
                        {{ t("vcSchema.info.issuerServiceUrl") + "：" }}
                      </span>
                      <span class="text-h6-cus q-ma-none word-break">
                        {{ issuerServiceUrl }}
                      </span>
                    </div>
                  </div>
                  <!-- 分隔線 -->
                  <q-separator class="q-mt-sm" size="3px" color="grey-1" />
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="relative-position">
                  <div v-if="coverLoading" class="row justify-end">
                    <div
                      class="bg-white row justify-center items-center"
                      :style="{
                        borderRadius: '10px',
                        width: isMobile ? '100%' : '324px',
                        aspectRatio: '8 / 5'
                      }"
                    >
                      <q-spinner-ios class="text-grey" />
                    </div>
                  </div>
                  <template v-else>
                    <div
                      v-if="vcCoverInfo.cardCover !== null"
                      class="row justify-end"
                    >
                      <q-img
                        :src="vcCoverInfo.cardCover"
                        class=""
                        :alt="t('vcSchema.dialog.imagePreview')"
                        :style="{
                          borderRadius: '10px',
                          width: isMobile ? '100%' : '320px',
                          aspectRatio: '8 / 5'
                        }"
                      >
                        <q-img
                          v-if="vcCoverInfo.cardCover"
                          :src="sampleMark"
                          fit="contain"
                          class="bg-transparent absolute-bottom"
                          :style="{
                            padding: '0',
                            margin: '0'
                          }"
                        />
                      </q-img>
                    </div>
                    <template v-else>
                      <div class="row justify-end">
                        <div
                          class="row justify-center items-center text-h6-cus text-grey"
                          :style="{
                            borderRadius: '10px',
                            width: isMobile ? '100%' : '324px',
                            aspectRatio: '8 / 5'
                          }"
                        >
                          <q-img
                            :src="sampleRe"
                            :style="{
                              width: isMobile ? '100%' : '320px',
                              aspectRatio: '8 / 5'
                            }"
                            :alt="t('vcSchema.dialog.imagePreview')"
                          ></q-img>
                        </div>
                      </div>
                    </template>
                  </template>
                  <!-- 匯出 QR Code -->
                  <div
                    v-if="staticQRCode"
                    class="row q-mt-sm q-mb-md"
                    :class="isMobile ? '' : 'justify-end'"
                  >
                    <div
                      style="width: 100%"
                      :style="{
                        border: '2px dashed #ccc',
                        borderRadius: '10px',
                        aspectRatio: isMobile ? '8.8 / 3.1' : '9 / 3.2',
                        maxWidth: isMobile ? '' : '319px'
                      }"
                    >
                      <div class="row justify-center q-px-md q-pt-md">
                        <q-btn
                          class="full-width"
                          :label="t('vcSchema.dialog.button.export')"
                          icon="open_in_new"
                          size="md"
                          color="indigo-6"
                          dense
                          :disable="!qrCode"
                          @click="exportPng"
                          no-caps
                        />
                      </div>
                      <div class="row justify-center q-px-md q-pt-md q-pb-md">
                        <q-btn
                          class="full-width"
                          :icon="showQRCodeExpandIcon()"
                          :label="t('vcSchema.dialog.button.show')"
                          color="indigo-6"
                          dense
                          :disable="!qrCode"
                          @click="showQRCode = !showQRCode"
                          no-caps
                        />
                      </div>
                      <div v-show="showQRCode && qrCode">
                        <div style="position: relative; text-align: center">
                          <q-img
                            :src="qrCode.base64"
                            style="height: 230px; width: 230px; margin: 0 auto"
                          />
                        </div>
                        <div class="word-break q-px-md q-pb-sm">
                          {{ qrCode.url }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div :class="showClassStyle">
              {{ t("vcSchema.vcData.inputNotice") }}
            </div>
            <q-table
              dense
              flat
              :hide-header="dialogLoading"
              class="sticky-header no-height"
              :rows="VCSchemaDetail"
              :columns="isMobileColumns"
              row-key="id"
              :loading="dialogLoading"
              :rows-per-page-options="[10, 20, 50, 0]"
              :pagination="pagination"
              :hide-no-data="!dialogLoading"
            >
              <template v-slot:body-cell-type="props">
                <q-td :props="props">
                  {{ typeMethod(props.row.type) }}
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
          </q-card-section>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, toRefs, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useVcSchemaStore } from "stores/vcSchema";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import sampleRe from "src/assets/sampleRe.png";
import sampleMark from "src/assets/sampleMark.png";
import { format } from "utils/dateFormat";

const { yyyyMMddHHmmss } = format();
const vcSchema = useVcSchemaStore();
const $q = useQuasar();
const { VCSchemaDetail, dialogLoading, coverLoading, vcCoverInfo } =
  storeToRefs(vcSchema);

const {
  getVCSchemaDetail,
  resetVCSchemaDetail,
  getVCCover,
  resetVCcover,
  downloadStaticQRCode
} = vcSchema;
const { t } = useI18n();
const props = defineProps({
  row: Object,
  id: Number,
  businessId: String,
  serialNo: String,
  name: String,
  crDatetime: String,
  businessTWName: String,
  unitTypeExpire: String,
  lengthExpire: Number,
  ial: String,
  isVerify: Boolean,
  staticQRCode: Boolean,
  type: String,
  issuerServiceUrl: String,
  qrCode: Object,
  isTemp: Boolean,
  activated: Boolean
});

const {
  id,
  businessId,
  serialNo,
  name,
  crDatetime,
  businessTWName,
  unitTypeExpire,
  lengthExpire,
  ial,
  isVerify,
  staticQRCode,
  type,
  issuerServiceUrl,
  qrCode,
  isTemp,
  activated
} = toRefs(props);

defineEmits([...useDialogPluginComponent.emits]);

const showMore = ref(false);
const showQRCode = ref(false);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const onOKClick = () => {
  onDialogOK();
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {
  resetVCcover();
  resetVCSchemaDetail();
};

// 匯出 QR Code -> .png
const exportPng = () => {
  downloadStaticQRCode(props.qrCode.base64);
};

// 依是否使用靜態QR Code做調整
const classStyle = computed(() =>
  props.staticQRCode ? "row full-width" : "row full-width q-mb-md"
);

// 依是否使用靜態QR Code做調整
const showClassStyle = computed(() =>
  props.staticQRCode ? "alert-text-sm q-mt-md" : "alert-text-sm q-mt-lg"
);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const baseColumns = [
  {
    name: "type",
    required: true,
    label: t("fields.type"),
    align: "left",
    field: "type", // 直接使用 type 值
    format: (val) => typeMethod(val), // 使用 typeMethod 來格式化顯示
    sortable: true
  },
  {
    name: "isRequired",
    required: true,
    label: t("fields.isRequired"),
    align: "center",
    field: (row) => (row.isRequired ? t("required") : t("notRequired")),
    style: "width: 100px;"
  },
  {
    name: "cname",
    label: t("fields.cname"),
    align: "left",
    // 修改field來處理不同類型的cname
    field: (row) =>
      typeof row.cname === "object" ? row.cname.cname : row.cname
  },
  {
    name: "ename",
    label: t("fields.ename"),
    align: "left",
    field: "ename"
  },
  {
    name: "description",
    label: t("regularExpression.table.description"),
    align: "left",
    field: (row) => row.regularExpression?.description || ""
  }
];

const desktopStyle = {
  type: "width: 80px",
  cname: "width: 240px; white-space: normal; word-break: break-word",
  ename: "width: 340px; white-space: normal; word-break: break-word",
  description: "width: 300px; white-space: normal; word-break: break-word"
};

const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns;
  }

  return baseColumns.map((col) => ({
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

const convertIalValue = (ial) => {
  switch (ial) {
    case "1":
      return "IAL 1";
    case "2":
      return "IAL 2";
    case "3":
      return "IAL 3";
    default:
      return t("vcSchema.noIalChoice"); // 如果不是已知的單位，則返回原始值
  }
};

const onDialogShow = () => {
  if (id.value) {
    getVCSchemaDetail(id.value);
    getVCCover(id.value);
  }
};

const computedPagination = computed(() => {
  return VCSchemaDetail.value?.length || 0;
});

const pagination = ref({
  rowsPerPage: 10, // 預設每頁顯示數量
  sortBy: "",
  descending: false,
  page: 1
});

const expandIcon = () => {
  if (showMore.value) return "expand_less";
  else return "expand_more";
};

const showQRCodeExpandIcon = () => {
  if (showQRCode.value) return "expand_less";
  else return "expand_more";
};
</script>

<style scoped>
/* 確保圖片容器樣式 */
.full-height {
  height: 100%;
}

.full-width {
  width: 100%;
}
</style>
