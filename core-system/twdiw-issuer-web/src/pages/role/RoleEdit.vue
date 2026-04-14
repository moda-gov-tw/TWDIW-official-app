<template>
  <pop-up
    v-model="roleEditOpen"
    :header="t('role.edit')"
    :persistent="true"
    @beforeShow="onBeforeShow"
  >
    <form-wrapper
      v-model="editModel"
      :fields="fields"
      :layout="[1, 1, 1]"
      :submit-label="t('edit')"
      @confirm="onConfirm"
      @reset="onReset"
      :disabled="loading"
      :loading="loading"
    />
  </pop-up>
</template>
<script setup>
import { reactive, computed, defineEmits } from "vue";
import { storeToRefs } from "pinia";
import { useRoleStore } from "stores/role";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

//store settings
const store = useRoleStore();
const { selected, roleEditOpen, loading } = storeToRefs(store);
const { editRole } = store;

//emits
const emit = defineEmits("edited");

const editModel = reactive({
  isColor: true
});

const fields = computed(() => [
  {
    name: "roleName",
    label: "*" + t("role.table.name"),
    inputType: "input",
    field: "roleName",
    hint: t("input", { input: t("role.table.name") }),
    outlined: true,
    rules: [
      (val) => !!val || t("notBlank", { input: t("role.table.name") }),
      (val) => !val || val.length <= 50 || t("valid.maxLength", { max: 50 }),
      (val) =>
        !val ||
        /^[\u4E00-\u9FFF\u3400-\u4DBFa-zA-Z0-9_$@#&]+$/.test(val) ||
        t("valid.onlyZhEnNumAllowedAnd", { symbols: "_$@#&" })
    ]
  },
  {
    name: "description",
    label: t("role.table.desc"),
    inputType: "input",
    field: "description",
    outlined: true,
    hint: t("input", { input: t("role.table.desc") })
  },
  {
    name: "state",
    label: "*" + t("role.table.state"),
    inputType: "select",
    field: "state",
    outlined: true,
    options: [
      { label: t("enabled"), value: "1" },
      { label: t("disabled"), value: "0" }
    ],
    rules: [(val) => !!val || t("choose", { input: t("role.table.state") })],
    emitValue: true
  }
]);

const onConfirm = async (model) => {
  const role = {
    id: selected?.value?.id,
    roleId: selected?.value?.roleId,
    roleName: model?.value?.roleName,
    description: model?.value?.description,
    state: model?.value?.state
  };

  editRole(role).then(() => {
    roleEditOpen.value = false;
    emit("edited");
  });
};

const onReset = () => {
  Object.assign(editModel, selected.value);
};

const onBeforeShow = () => {
  Object.assign(editModel, selected.value);
};
</script>
