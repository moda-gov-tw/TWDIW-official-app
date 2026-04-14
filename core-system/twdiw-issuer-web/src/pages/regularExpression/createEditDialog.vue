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
        <!-- 新增/編輯正規表示法 -->
        <div class="text-h6">
          {{
            type === "new"
              ? t("regularExpression.add")
              : t("regularExpression.edit")
          }}
        </div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section class="q-px-sm">
        <q-scroll-area
          :style="{ height: isMobile ? '45vh' : '60vh', 'max-height': '100vh' }"
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
            <q-form ref="formRef" greedy>
              <div class="row q-col-gutter-md">
                <div
                  v-for="field in formFields"
                  :key="field.name"
                  class="col-12 col-md-6"
                >
                  <p class="input-title">
                    <span class="text-red">*</span>{{ field.label }}：
                  </p>
                  <template v-if="field.state === 'select'">
                    <q-select
                      outlined
                      v-model="form[field.name]"
                      dense
                      :label="field.props.placeholder"
                      :rules="field.props.rules"
                      :options="field.options"
                      emit-value
                      map-options
                      menu-position="bottom"
                      :popup-content-class="'no-modal'"
                      behavior="menu"
                    />
                  </template>
                  <template v-else>
                    <q-input
                      outlined
                      :placeholder="field.props.placeholder"
                      v-model="form[field.name]"
                      :rules="field.props.rules"
                      dense
                    />
                  </template>
                </div>
              </div>
            </q-form>
          </q-card-section>
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
          :disable="dialogLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
          :disable="dialogLoading"
          :loading="dialogLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, defineEmits, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { useRegularExpressionStore } from "stores/regularExpression";

const regularExpressionStore = useRegularExpressionStore();
const { dialogLoading } = storeToRefs(regularExpressionStore);
const { createEditRegularExpression } = regularExpressionStore;
const { t } = useI18n();
defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();

const formRef = ref(null);
const $n = useNotify();
const $q = useQuasar();
// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const formFields = [
  {
    name: "name",
    state: "input",
    label: t("regularExpression.table.name"),
    props: {
      placeholder: t("input", {
        input:
          t("regularExpression.table.name") +
          t("regularExpression.dialog.example")
      }),
      rules: [
        (val) =>
          !!val || t("input", { input: t("regularExpression.table.name") }),
        (val) =>
          val.length <= 40 ||
          t("regularExpression.dialog.valid.lessThan", { max: 40 }),
        (val) =>
          /^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val) ||
          t("valid.onlyZhEnNumAllowed")
      ]
    }
  },
  {
    name: "regularExpression",
    state: "input",
    label: t("regularExpression.regular"),
    props: {
      placeholder: t("input", {
        input:
          t("regularExpression.regular") +
          t("regularExpression.dialog.exampleRegular", {
            regular: "^09\\d{8}$"
          })
      }),
      rules: [
        (val) => !!val || t("input", { input: t("regularExpression.regular") }),
        (val) =>
          val.length <= 200 ||
          t("regularExpression.dialog.valid.lessThan", { max: 200 }),
        (val) => {
          // 檢查是否只是純文字
          if (/^[a-zA-Z0-9]+$/.test(val)) {
            return t("valid.useRegexSyntax");
          }

          // 檢查是否有正則表達式的基本結構
          const commonPatterns = [
            /[\[\]\{\}\(\)\^\$\.\*\+\?\|\\]/, // 特殊字符
            /\\[dws]/, // 常見的轉義字符
            /\{[\d,]+\}/, // 重複次數
            /\[.*?\]/, // 字符類
            /\(.*?\)/ // 群組
          ];

          const hasRegexPattern = commonPatterns.some((pattern) =>
            pattern.test(val)
          );
          if (!hasRegexPattern) {
            return t("valid.useRegexSyntax");
          }

          try {
            new RegExp(val);
            return true;
          } catch (e) {
            return t("regularExpression.dialog.valid.regular");
          }
        }
      ]
    }
  },
  {
    name: "description",
    state: "input",
    label: t("regularExpression.table.description"),
    props: {
      placeholder: t("input", {
        input:
          t("regularExpression.table.description") +
          t("regularExpression.dialog.exampleDescription")
      }),
      rules: [
        (val) =>
          !!val ||
          t("input", { input: t("regularExpression.table.description") }),
        (val) =>
          val.length <= 30 ||
          t("regularExpression.dialog.valid.lessThan", { max: 30 })
      ]
    }
  },
  {
    name: "errorMsg",
    state: "input",
    label: t("regularExpression.errorMsg"),
    props: {
      placeholder: t("input", {
        input:
          t("regularExpression.errorMsg") +
          t("regularExpression.dialog.errorMessagesDescription")
      }),
      rules: [
        (val) =>
          !!val || t("input", { input: t("regularExpression.errorMsg") }),
        (val) =>
          val.length <= 30 ||
          t("regularExpression.dialog.valid.lessThan", { max: 30 })
      ]
    }
  },
  {
    name: "ruleType",
    state: "select",
    label: t("regularExpression.rule"),
    props: {
      placeholder: t("choose", { input: t("regularExpression.rule") }),
      rules: [
        (val) => !!val || t("choose", { input: t("regularExpression.rule") })
      ]
    },
    options: [
      {
        label: t("regularExpression.select.allow"),
        value: "allow"
      },
      {
        label: t("regularExpression.select.deny"),
        value: "deny"
      }
    ]
  }
];

const props = defineProps({
  type: String,
  row: Object
});

const form = ref({});

const onDialogShow = () => {
  if (props.type === "edit" && props.row) {
    form.value = { ...props.row };
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

const onOKClick = async () => {
  const isValid = await formRef.value.validate();

  if (isValid) {
    const params = {
      ...form.value
    };

    const success = await createEditRegularExpression(props.type, params);
    if (success) {
      onDialogOK();
    }
  } else {
    $n.error(t("requiredFields"));
  }
};
</script>
