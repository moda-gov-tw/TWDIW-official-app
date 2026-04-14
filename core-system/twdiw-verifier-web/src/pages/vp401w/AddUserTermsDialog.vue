<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card
      class="q-dialog-plugin text-black column"
      style="max-width: 1500px; width: 800px"
      :style="{ height: '80vh', 'max-height': '100vh' }"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">{{ t("vp.dialog.title.terms") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <div class="col q-px-xs">
        <q-scroll-area
          class="fit"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#4E4E4E',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <q-card-section
            style="max-width: 800px; margin: 0 auto"
            class="q-gutter-md"
          >
            <div class="row items-center q-ml-none">
              <span class="text-h5">{{
                t("vp.dialog.terms.contentTitle")
              }}</span>
              <q-space />
              <q-btn
                :class="[isMobile ? 'q-mt-sm' : '']"
                icon="save_alt"
                :label="t('vp.dialog.terms.importExampleTermsBtn')"
                color="indigo-6"
                @click="openImportTermsDialog"
                :disable="saveTermsLoading"
              />
            </div>
            <div class="row items-center q-ml-none">
              <span class="alert-text q-ml-none">{{
                t("vp.dialog.terms.basicNotice")
              }}</span>
            </div>

            <template v-if="showTermsTemplateNotice">
              <span class="q-ml-none alert-text">{{
                t("vp.dialog.terms.exampleNotice")
              }}</span>
            </template>

            <q-form ref="formRef" greedy class="q-ml-none">
              <q-input
                type="textarea"
                filled
                autogrow
                v-model="editTerms"
                :rules="[(val) => !!val || t('vp.dialog.terms.placeholder')]"
                :placeholder="t('vp.dialog.terms.placeholder')"
              />
            </q-form>
          </q-card-section>
        </q-scroll-area>
      </div>

      <q-card-actions align="right" class="q-pa-md row q-gutter-sm justify-end">
        <q-btn
          outline
          unelevated
          :label="t('cancel')"
          class="text-primary"
          v-close-popup
          :disable="saveTermsLoading"
        />
        <q-btn
          unelevated
          :label="t('store')"
          color="primary"
          @click="handleNextStep('create')"
          :loading="saveTermsLoading"
          :disable="saveTermsLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 確認 匯入條款範例 -->
  <q-dialog v-model="isOpenImportTerms" persistent>
    <q-card style="min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">
          {{ t("vp.dialog.terms.comfirmTitle") }}
        </div>
        <q-space></q-space>
        <q-btn flat fab-mini icon="close" v-close-popup></q-btn>
      </q-card-section>
      <q-card-section>
        <div class="q-pa-md">{{ t("vp.dialog.terms.comfirmContent") }}</div>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          v-close-popup
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          v-close-popup
          @click="importText"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useVpStore } from "stores/vp";
import { useNotify } from "src/utils/plugin";
import { storeToRefs } from "pinia";
import DOMPurify from "dompurify";
import termsText from "src/assets/termsTemplate.txt?raw";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();
const vpStore = useVpStore();
const { saveTermsLoading } = storeToRefs(vpStore);
const { saveTerms } = vpStore;
const $n = useNotify();
const $q = useQuasar();
const showTermsTemplateNotice = ref(false);
const formRef = ref(null);
defineEmits([...useDialogPluginComponent.emits]);

// VP 授權條款
const isOpenImportTerms = ref(false);
const openImportTermsDialog = () => {
  isOpenImportTerms.value = true;
};

const handleNextStep = async () => {
  // 在 show 的情況下，檢查是否有選中的資料
  if (!editTerms.value) {
    $n.error(t("vp.dialog.terms.placeholder"));
    return;
  }
  const terms = DOMPurify.sanitize(editTerms.value);

  if (editMode.value === "create") {
    onDialogOK(terms);
  } else {
    const data = {
      id: editId.value,
      serialNo: editSerialNo.value,
      terms: terms
    };
    await saveTerms(data);
    onDialogOK(terms);
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 匯入條款範例
const importText = async () => {
  try {
    editTerms.value = termsText;
    showTermsTemplateNotice.value = true;
  } catch (error) {
    console.error(`${t("vp.dialog.terms.error")}：${error}`);
  } finally {
    isOpenImportTerms.value = false;
  }
};

const props = defineProps({
  mode: String,
  id: Number,
  serialNo: String,
  terms: String
});
const editMode = ref(props.mode);
const editId = ref(props.id);
const editSerialNo = ref(props.serialNo);
const editTerms = ref(props.terms);
</script>
