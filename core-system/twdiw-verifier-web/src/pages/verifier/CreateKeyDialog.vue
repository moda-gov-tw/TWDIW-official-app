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
        <div class="text-h6">{{ t("orgKeySetting.addKey") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section>
        <q-scroll-area
          :style="{ height: isMobile ? '50vh' : '65vh', 'max-height': '100vh' }"
          :thumb-style="{
            borderRadius: '5px',
            background: '#4E4E4E',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <div class="q-pa-sm">
            <div class="row justify-end">
              <q-btn
                unelevated
                color="indigo-6"
                icon="key"
                :label="t('orgKeySetting.generateKey')"
                :class="['q-mt-md', isMobile ? 'col-6 ' : '']"
                :style="!isMobile ? 'width: 160px' : ''"
                @click="onGenerateKey"
                :loading="generateKeyLoading"
              />
            </div>
            <q-form ref="formRef" greedy>
              <div
                :class="[
                  'q-mt-xs',
                  isMobile ? 'q-col-gutter-md' : 'q-col-gutter-lg q-gutter-y-sm'
                ]"
              >
                <!-- 金鑰代碼 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']">
                      <span class="text-red">*</span>
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.keyId.label")
                      }}</span>
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.keyId"
                      :rules="[
                        (val) => !!val || t('orgKeySetting.keyId.required'),
                        (val) =>
                          val.length <= 50 ||
                          t('validation.maxLength', { max: 50 }),
                        (val) =>
                          /^[a-z0-9_]+$/.test(val) ||
                          t('orgKeySetting.keyId.pattern')
                      ]"
                      hide-bottom-space
                      :placeholder="t('orgKeySetting.keyId.placeholder')"
                      dense
                    />
                  </div>
                </div>
                <!-- Curve25519 公鑰 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']">
                      <span class="text-red">*</span>
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.publicKey.label")
                      }}</span>
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.publicKey"
                      autogrow
                      :rules="[
                        (val) => !!val || t('orgKeySetting.publicKey.required'),
                        (val) =>
                          /^[A-Za-z0-9+/=._\-:@\s]+$/.test(val) ||
                          t('orgKeySetting.publicKey.pattern', {
                            allowed: '+/=-_.:@'
                          })
                      ]"
                      hide-bottom-space
                      :placeholder="
                        t('orgKeySetting.publicKey.placeholder', {
                          allowed: '+/=-_.:@'
                        })
                      "
                      dense
                    />
                  </div>
                </div>
                <!-- Curve25519 私鑰 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']">
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.privateKey.label")
                      }}</span>
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.privateKey"
                      autogrow
                      :rules="[
                        (val) => {
                          if (!val) return true;
                          return (
                            /^[A-Za-z0-9+/=._\-:@\s]+$/.test(val) ||
                            t('orgKeySetting.privateKey.pattern', {
                              allowed: '+/=-_.:@'
                            })
                          );
                        }
                      ]"
                      hide-bottom-space
                      :placeholder="
                        t('orgKeySetting.privateKey.placeholder', {
                          allowed: '+/=-_.:@'
                        })
                      "
                      dense
                    />
                  </div>
                </div>
                <!-- TOTP 金鑰 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p
                      :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']"
                      style="display: flex; align-items: flex-end"
                    >
                      <span class="text-red">*</span>
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.totpKey.label")
                      }}</span>
                      <q-icon
                        :class="[
                          'cursor-pointer',
                          !isMobile ? 'q-ml-auto q-pr-md' : 'q-ml-xs'
                        ]"
                        color="primary"
                        name="info_outline"
                        size="sm"
                        @click="toggleTOTPNotice"
                      >
                        <q-popup-proxy
                          v-if="!isMobile"
                          anchor="bottom left"
                          self="top left"
                          :offset="[-40, 8]"
                          class="bg-grey-2"
                        >
                          <div class="text-white text-body2 notice">
                            {{ t("orgKeySetting.totpKey.notice") }}
                          </div>
                        </q-popup-proxy></q-icon
                      >
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.totpKey"
                      autogrow
                      :rules="[
                        (val) => !!val || t('orgKeySetting.totpKey.required'),
                        (val) =>
                          /^[A-Za-z0-9+/=._\-:@\s]+$/.test(val) ||
                          t('orgKeySetting.totpKey.pattern', {
                            allowed: '+/=-_.:@'
                          })
                      ]"
                      hide-bottom-space
                      :placeholder="
                        t('orgKeySetting.totpKey.placeholder', {
                          allowed: '+/=-_.:@'
                        })
                      "
                      dense
                    />
                  </div>
                </div>
                <!-- HMAC 金鑰 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p
                      :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']"
                      style="display: flex; align-items: flex-end"
                    >
                      <span class="text-red">*</span>
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.hmacKey.label")
                      }}</span>
                      <q-icon
                        :class="[
                          'cursor-pointer',
                          !isMobile ? 'q-ml-auto q-pr-md' : 'q-ml-xs'
                        ]"
                        color="primary"
                        name="info_outline"
                        size="sm"
                        @click="toggleHMACNotice"
                      >
                        <q-popup-proxy
                          v-if="!isMobile"
                          anchor="bottom left"
                          self="top left"
                          :offset="[-40, 8]"
                          class="bg-grey-2"
                        >
                          <div class="text-white text-body2 notice">
                            {{ t("orgKeySetting.hmacKey.notice") }}
                          </div>
                        </q-popup-proxy></q-icon
                      >
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.hmacKey"
                      autogrow
                      :rules="[
                        (val) => !!val || t('orgKeySetting.hmacKey.required'),
                        (val) =>
                          /^[A-Za-z0-9+/=._\-:@\s]+$/.test(val) ||
                          t('orgKeySetting.hmacKey.pattern', {
                            allowed: '+/=-_.:@'
                          })
                      ]"
                      hide-bottom-space
                      :placeholder="
                        t('orgKeySetting.hmacKey.pattern', {
                          allowed: '+/=-_.:@'
                        })
                      "
                      dense
                    />
                  </div>
                </div>
                <!-- 金鑰備註 -->
                <div class="col-12 col-lg-6">
                  <div :class="[isMobile ? '' : 'row items-start']">
                    <p :class="['col-2', isMobile ? 'q-mb-sm' : 'pt-10']">
                      <span class="q-pr-xs">{{
                        t("orgKeySetting.description.label")
                      }}</span>
                    </p>
                    <q-input
                      outlined
                      class="col"
                      v-model.trim="formData.description"
                      :rules="[
                        (val) => {
                          if (!val) return true;
                          if (val.length > 18)
                            return t('validation.maxLength', { max: 18 });
                          if (!/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val))
                            return t('orgKeySetting.description.pattern');
                          return true;
                        }
                      ]"
                      hide-bottom-space
                      :placeholder="t('orgKeySetting.description.placeholder')"
                      dense
                    />
                  </div>
                </div>
              </div>
            </q-form>
          </div>
        </q-scroll-area>
      </q-card-section>
      <q-card-actions align="right">
        <div
          :class="[
            'alert-text',
            isMobile ? 'col-12 flex justify-center q-mb-sm' : 'q-mr-md'
          ]"
        >
          {{ t("orgKeySetting.activateNotice") }}
        </div>
        <q-btn
          outline
          unelevated
          :label="t('cancel')"
          color="primary"
          :class="isMobile ? 'col' : ''"
          :style="!isMobile ? 'width: 120px' : ''"
          v-close-popup
          :disable="createKeyLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          :class="isMobile ? 'col' : ''"
          :style="!isMobile ? 'width: 120px' : ''"
          @click="onOKClick()"
          :loading="createKeyLoading"
          :disable="createKeyLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 手機版的 TOTP 金鑰 i 資訊 -->
  <q-dialog v-model="showTOTPKeyNotice">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("orgKeySetting.totpKey.label") }}</div>
      </q-card-section>

      <q-card-section>
        <p class="text-body2">
          {{ t("orgKeySetting.totpKey.notice") }}
        </p>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 手機版的 HMAC 金鑰 i 資訊 -->
  <q-dialog v-model="showHMACKeyNotice">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("orgKeySetting.hmacKey.label") }}</div>
      </q-card-section>

      <q-card-section>
        <p class="text-body2">{{ t("orgKeySetting.hmacKey.notice") }}</p>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 金鑰啟用確認 Dialog -->
  <q-dialog v-model="showConfirmKeyActivationDialog" persistent>
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">
          {{ t("orgKeySetting.dialog.title.notice") }}
        </div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <div class="text-subtitle1">
          {{ t("orgKeySetting.dialog.noticeMessage") }}
        </div>
      </q-card-section>

      <q-card-actions align="right" class="text-primary">
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          @click="onActivateConfirm"
          :loading="createKeyLoading"
          :disable="createKeyLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, reactive } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";
import { useOrgKeySettingStore } from "src/stores/orgKeySetting";

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();

defineEmits([...useDialogPluginComponent.emits]);

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();

const orgKeySettingStore = useOrgKeySettingStore();
const { createKeyLoading, generateKeyLoading } =
  storeToRefs(orgKeySettingStore);
const { createKey, generateKey } = orgKeySettingStore;

const props = defineProps({
  keyListCount: Number
});

// [FORM]
const form = {
  keyId: null,
  description: null,
  publicKey: null,
  privateKey: null,
  totpKey: null,
  hmacKey: null,
  isActive: false
};

// [REACTIVE]: 表單
const formData = reactive({ ...form });

// 表單相關
const formRef = ref(null);
const showTOTPKeyNotice = ref(false);
const showHMACKeyNotice = ref(false);
const showConfirmKeyActivationDialog = ref(false);

// 產生金鑰
const onGenerateKey = async () => {
  const generated = await generateKey();
  if (generated) {
    formData.publicKey = generated.publicKey;
    formData.privateKey = generated.privateKey;
    formData.totpKey = generated.totpKey;
    formData.hmacKey = generated.hmacKey;
  } else {
    console.log(t("orgKeySetting.error.generatedFail"));
  }
};

const onDialogShow = () => {};
const onCancelClick = () => {
  onDialogCancel();
};

// 送出
const onOKClick = async () => {
  // [檢核] 所有欄位
  const isValid = await formRef.value.validate();
  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  }

  // 若為第一筆金鑰，isActive = true
  if (props.keyListCount === 0) {
    formData.isActive = true;
  }
  showConfirmKeyActivationDialog.value = true;
};

// 金鑰啟用 dialog：是
const onActivateConfirm = () => {
  submitForm();
};

// 資料送出
const submitForm = async () => {
  const result = await createKey(formData);

  showConfirmKeyActivationDialog.value = false;
  if (result) {
    onDialogCancel();
    onDialogOK(result);
  }
};

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// TOTP 資訊 i 開關
const toggleTOTPNotice = () => {
  if (isMobile.value) {
    showTOTPKeyNotice.value = true;
  }
};

// HMAC資訊 i 開關
const toggleHMACNotice = () => {
  if (isMobile.value) {
    showHMACKeyNotice.value = true;
  }
};
</script>

<style scoped>
.pt-10 {
  padding-top: 10px;
}
.mobile-btns {
  width: 100%;
  display: flex;
  gap: 8px;
}

.mobile-btns .btn {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.desktop-btns {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.notice {
  background-color: grey;
  padding: 3px;
}
</style>
