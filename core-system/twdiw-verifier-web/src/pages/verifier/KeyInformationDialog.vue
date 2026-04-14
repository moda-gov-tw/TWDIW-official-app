<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1400px; width: 1000px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">{{ t("orgKeySetting.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>
      <q-card-section>
        <div
          class="column justify-center relative-position"
          style="min-height: 310px"
        >
          <q-markup-table
            class="col-auto q-ma-lg"
            separator="horizontal"
            bordered
            flat
            dense
          >
            <tbody>
              <tr v-for="row in filteredTableRow" :key="row.field">
                <td
                  class="text-left text-bold bg-grey-1"
                  :style="{ width: isMobile ? 'auto' : '300px' }"
                >
                  {{ row.label }}
                </td>
                <td class="text-right word-break">
                  <!-- 若為公鑰/私鑰/金鑰則顯示 eye + copy -->
                  <template v-if="isKeyField(row.label)">
                    <div class="row items-center justify-end no-wrap">
                      <span class="q-mr-sm">
                        {{
                          revealMap[row.field]
                            ? row.value
                            : maskValue(row.value)
                        }}
                      </span>

                      <!-- 顯示/隱藏按鈕 -->
                      <q-btn
                        flat
                        dense
                        size="sm"
                        :icon="
                          revealMap[row.field] ? 'visibility_off' : 'visibility'
                        "
                        @click="toggleReveal(row.field)"
                        class="q-ml-sm"
                        :aria-label="
                          revealMap[row.field] ? t('hide') : t('show')
                        "
                      />

                      <!-- 複製按鈕 -->
                      <q-btn
                        flat
                        dense
                        size="sm"
                        icon="content_copy"
                        @click="copyToClipboard(row.value)"
                        class="q-ml-xs"
                        :aria-label="t('copy')"
                      />
                    </div>
                  </template>

                  <!-- 非公鑰/私鑰/金鑰 -->
                  <template v-else>
                    {{ row.value }}
                  </template>
                </td>
              </tr>
            </tbody>
          </q-markup-table>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from "vue";
import { storeToRefs } from "pinia";
import { useNotify } from "src/utils/plugin";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useI18n } from "vue-i18n";
import { useOrgKeySettingStore } from "src/stores/orgKeySetting";
import { format } from "utils/dateFormat";

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

defineEmits([...useDialogPluginComponent.emits]);

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();
const { yyyyMMddHHmmss } = format();

const orgKeySettingStore = useOrgKeySettingStore();
const { keyDetailData } = storeToRefs(orgKeySettingStore);
const { getKeyDetail, resetDetailList } = orgKeySettingStore;

const props = defineProps({
  row: Object
});

const onDialogShow = () => {
  if (props.row?.id) {
    getKeyDetail(props.row.id);
  }
};

const onDialogHide = () => {
  resetDetailList();
};

const onCancelClick = () => {
  onDialogCancel();
};

// table 欄位
const tableRow = computed(() => [
  {
    label: t("orgKeySetting.keyId.label"),
    field: "keyId",
    value: keyDetailData.value?.keyId || ""
  },
  {
    label: t("orgKeySetting.description.label"),
    field: "description",
    value: keyDetailData.value?.description || ""
  },
  {
    label: t("orgKeySetting.publicKey.label"),
    field: "publicKey",
    value: keyDetailData.value?.publicKey || ""
  },
  {
    label: t("orgKeySetting.privateKey.label"),
    field: "privateKey",
    value: keyDetailData.value?.privateKey || ""
  },
  {
    label: t("orgKeySetting.totpKey.label"),
    field: "totpKey",
    value: keyDetailData.value?.totpKey || ""
  },
  {
    label: t("orgKeySetting.hmacKey.label"),
    field: "hmacKey",
    value: keyDetailData.value?.hmacKey || ""
  },
  {
    label: t("orgKeySetting.isEnabled"),
    field: "isActive",
    value: keyDetailData.value?.isActive
      ? t("orgKeySetting.enabled")
      : t("orgKeySetting.disabled")
  },
  {
    label: t("modifyTime.label"),
    field: "upDatetime",
    value: yyyyMMddHHmmss(keyDetailData.value?.upDatetime)
  },
  {
    label: t("createTime.label"),
    field: "crDatetime",
    value: yyyyMMddHHmmss(keyDetailData.value?.crDatetime)
  }
]);

const filteredTableRow = computed(() => tableRow.value);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 控制每個欄位是否顯示明碼
const revealMap = ref({});

// 判斷是否為Curve25519 公鑰、Curve25519 私鑰、TOTP 金鑰、HMAC 金鑰
const isKeyField = (label) =>
  label.includes(t("orgKeySetting.publicKey.label")) ||
  label.includes(t("orgKeySetting.privateKey.label")) ||
  label.includes(t("orgKeySetting.totpKey.label")) ||
  label.includes(t("orgKeySetting.hmacKey.label"));

// 切換顯示與否
const toggleReveal = (field) => {
  revealMap.value[field] = !revealMap.value[field];
};

// 遮罩值：只顯示首尾，其它為 6 個 *
const maskValue = (val) => {
  // 太短就直接遮住
  if (!val || val.length < 2) {
    return "********";
  }
  return val[0] + "******" + val[val.length - 1];
};

// 複製功能
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    $n.success(t("copySuccess"));
  } catch (err) {
    $n.error(t("copyfail"));
  }
};
</script>
<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}
</style>
