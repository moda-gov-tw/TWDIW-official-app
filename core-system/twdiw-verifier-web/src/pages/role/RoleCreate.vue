<template>
  <q-btn
    unelevated
    color="primary"
    class="full-width"
    icon="add"
    :label="t('role.btn.creatRole')"
    :ripple="false"
    @click="openDialog"
  >
  </q-btn>
  <pop-up
    ref="popUp"
    v-model="isOpen"
    :header="t('role.title.create')"
    :persistent="true"
    @beforeShow="onReset"
  >
    <form-wrapper
      v-model="createModel"
      :fields="fields"
      :layout="[2, 2]"
      :submit-label="t('create')"
      @confirm="onConfirm"
      @reset="onReset"
      :disabled="loading"
      :loading="loading"
    >
    </form-wrapper>
  </pop-up>
</template>
<script setup>
import { ref, reactive, defineEmits } from "vue";
import { useNotify } from "src/utils/plugin";
import { useRoleStore } from "stores/role";
import { useI18n } from "vue-i18n";
import { storeToRefs } from "pinia";
const { t } = useI18n();

//store settings
const store = useRoleStore();
const { createRole } = store;
const { loading } = storeToRefs(store);

const $n = useNotify();
const isOpen = ref(false);

//emits
const emit = defineEmits(["created"]);

const openDialog = () => {
  isOpen.value = true;
};

const createModel = reactive({ isEnable: false, isColor: true });
const fields = ref([
  {
    // 角色代號
    name: "roleId",
    label: "*" + t("role.code"),
    inputType: "input",
    field: "roleId",
    hint: t("input", { input: t("role.code") }),
    outlined: true,
    rules: [
      (val) => !!val || t("notBlank", { input: t("role.code") }),
      (val) =>
        !val || val.length <= 20 || t("validation.maxLength", { max: 20 }),
      (val) =>
        !val ||
        /^[a-zA-Z0-9_$@#&]+$/.test(val) ||
        t("validation.onlyEnNumAllowedAnd", { symbols: "_$@#&" })
    ]
  },
  {
    // 角色名稱
    name: "roleName",
    label: "*" + t("role.name"),
    inputType: "input",
    field: "roleName",
    hint: t("input", { input: t("role.name") }),
    outlined: true,
    rules: [
      (val) => !!val || t("notBlank", { input: t("role.name") }),
      (val) =>
        !val || val.length <= 50 || t("validation.maxLength", { max: 50 }),
      (val) =>
        !val ||
        /^[\u4E00-\u9FFF\u3400-\u4DBFa-zA-Z0-9_$@#&]+$/.test(val) ||
        t("validation.onlyZhEnNumAllowedAnd", { symbols: "_$@#&" })
    ]
  },
  {
    // 角色描述
    name: "description",
    label: t("role.desc"),
    inputType: "input",
    field: "description",
    hint: t("input", { input: t("role.desc") }),
    outlined: true
  },
  {
    // 角色啟用狀態
    name: "state",
    label: "*" + t("role.enabled"),
    inputType: "select",
    field: "state",
    outlined: true,
    options: [
      { label: t("enabled"), value: 1 },
      { label: t("disabled"), value: 0 }
    ],
    rules: [(val) => !!val || t("notBlank", { input: t("role.enabled") })]
  }
]);

const onConfirm = (model) => {
  createRole(model.value).then(() => {
    const name = model.value.roleName;
    const message = `${t("role.doAction", {
      action: t("add")
    })}，「${name}」。`;

    $n.success(message);
    emit("created");
    isOpen.value = false;
  });
};

const onReset = (model) => {
  createModel.roleId = null;
  createModel.roleName = null;
  createModel.description = null;
  createModel.state = null;
};
</script>
