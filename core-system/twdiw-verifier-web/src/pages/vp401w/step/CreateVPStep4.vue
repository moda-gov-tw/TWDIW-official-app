<template>
  <div class="q-px-xs">
    <!-- 上方資訊 -->
    <div class="q-my-xs row">
      <div class="col-12 col-md-6">
        <!-- VP代碼 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.serialNo.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">{{
            formData.serialNo
          }}</span>
        </div>
        <!-- VP名稱 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.name.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">{{
            formData.name
          }}</span>
        </div>
      </div>
      <div class="col-12 col-md-6">
        <!-- VP授權目的 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <div class="text-h6-cus text-grey-7">
            {{ t("vp.purpose.label") }}：
          </div>
          <div class="text-h6-cus q-ma-none word-break" style="gap: 4px">
            {{ formData.purpose }}
          </div>
        </div>
        <!-- VP授權條款 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.terms.label") }}：</span
          >
          <div class="row flex items-center" style="gap: 4px">
            <template v-if="formData.terms">
              <q-icon
                name="check_circle"
                color="indigo-6"
                class="q-ml-xs"
                size="24px"
              />
              <span class="text-h6-cus text-indigo-6">
                {{ t("vp.terms.status.edited") }}
              </span>
            </template>
            <template v-else>
              <q-icon
                name="cancel"
                color="primary"
                class="q-ml-xs"
                size="24px"
              />
              <span class="text-primary">
                {{ t("vp.terms.status.notEdited") }}
              </span>
            </template>
          </div>
        </div>
      </div>
      <!-- 進階功能（未上線） -->
      <div class="col-12">
        <q-btn
          class="q-mt-sm q-mb-sm text-indigo-6"
          :icon="expandIcon()"
          :label="t('vp.btn.advancedFeature')"
          color="indigo-2"
          @click="formData.showMore = !formData.showMore"
        />
      </div>
      <div v-show="formData.showMore" class="col-12 q-mt-md">
        <div class="row q-mb-sm items-start">
          <!-- 模式 -->
          <div class="col-12 col-md-6">
            <div class="row items-start">
              <span class="text-h6-cus text-grey-7"
                >{{ t("vp.model.label") }}：</span
              >
              <span class="text-h6-cus q-ma-none word-break">
                {{
                  formData.model.value === "0"
                    ? t("vp.model.noChoose")
                    : formData.model.label
                }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <!-- 靜態 QR Code 模式 -->
      <div
        v-show="formData.model.value === '1' && formData.showMore"
        class="col-12"
      >
        <!-- 組織業務系統 URL -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.serviceUrl.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">
            {{ formData.verifierServiceUrl }}
          </span>
        </div>
        <!-- Call Back URL -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.callBackUrl.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">
            {{ formData.callBackUrl }}
          </span>
        </div>

        <!-- 欄位對外名稱、欄位名稱(英)、限定資料格式 -->
        <div
          v-if="formData.fields && formData.isCustomFields"
          :class="['row items-start', isMobile ? '' : 'q-mb-sm']"
        >
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.customFields.name") }}：</span
          >
          <span
            class="text-h6-cus q-ma-none word-break"
            v-for="(data, index) in formData.fields"
            :key="index"
          >
            {{
              `${data.description}/ ${data.cname} / ${data.ename} / ${data.regex}/ ${data.value}`
            }}
            <span v-if="index !== formData.fields.length - 1">、</span>
          </span>
        </div>
      </div>
      <!-- Offline 模式 -->
      <div
        v-show="formData.model.value === '2' && formData.showMore"
        class="col-12"
      >
        <!-- 模組加密 -->
        <div class="col-12 col-md-6 q-mb-sm">
          <div class="row items-start">
            <span class="text-h6-cus text-grey-7"
              >{{ t("vp.encryptEnabled.label") }}：</span
            >
            <span class="text-h6-cus q-ma-none word-break">
              {{
                formData.isEncryptEnabled
                  ? t("vp.encryptEnabled.yes")
                  : t("vp.encryptEnabled.no")
              }}
            </span>
          </div>
        </div>
        <!-- TAG -->
        <div v-if="formData.isEncryptEnabled" class="col-12 col-md-6 q-mb-sm">
          <div class="row items-start">
            <span class="text-h6-cus text-grey-7"
              >{{ t("vp.tag.label") }}：</span
            >
            <span class="text-h6-cus q-ma-none word-break">
              {{ formData.tag }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="column col full-width">
      <!-- 確認內容 -->
      <span class="col q-mt-md q-mb-sm alert-text">
        {{ t("vp.confirmData") }}
      </span>
      <div class="col flex justify-center">
        <group-data-detail :formData="formData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useQuasar } from "quasar";
import { useVpStore } from "src/stores/vp";
import GroupDataDetail from "../GroupDataDetail.vue";
import { useI18n } from "vue-i18n";

const vpStore = useVpStore();
const { createVP, editVp } = vpStore;

const { t } = useI18n();
const $q = useQuasar();
const emit = defineEmits(["submit-completed"]);

// 步驟資料
const formData = defineModel("formData");
const props = defineProps({
  isEdit: Boolean,
  row: Object
});

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 進階功能ICON
const expandIcon = () => {
  if (formData.value.showMore) return "expand_less";
  else return "expand_more";
};

// 若非 VP05（APP 出示憑證模式），清除自定義名稱
const clearCustomFieldName = () => {
  if (formData.value.model.value === "2") return;

  formData.value.groups.forEach((groupData) => {
    groupData.vcDatas.forEach((vcData) => {
      vcData.vcFields.forEach((vcField) => {
        vcField.customFieldName = "";
      });
    });
  });
};

// 確認
const onOKClick = async () => {
  // 如果沒有顯示進階功能或模式是請選擇 => 清空資料
  if (formData.value.model.value === "0") {
    formData.value.verifierServiceUrl = null;
    formData.value.callBackUrl = null;
    formData.value.fields = null;
    formData.value.tag = null;
  } else if (formData.value.model.value === "1") {
    // 如果模式是靜態 QR Code 模式 => 清空 Offline 模式資料
    formData.value.tag = null;
    // 如果未打勾自定義資料 => 清空 fields 資料
    if (!formData.value.isCustomFields) {
      formData.value.fields = null;
    }
  } else if (formData.value.model.value === "2") {
    // 如果模式是 VP05（APP 出示憑證模式） => 清空靜態 QR Code 模式資料
    formData.value.verifierServiceUrl = null;
    formData.value.callBackUrl = null;
    formData.value.fields = null;
  }

  // 若非 VP05（APP 出示憑證模式），清除自定義名稱
  clearCustomFieldName();

  const data = {
    ...formData.value,
    model: formData.value.model.value
  };

  if (props.isEdit) {
    data.id = props.row.id;
  }

  // call api
  const result = props.isEdit ? await editVp(data) : await createVP(data);
  if (result) {
    emit("submit-completed", result);
  }
};

defineExpose({ onOKClick });
</script>

<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
</style>
