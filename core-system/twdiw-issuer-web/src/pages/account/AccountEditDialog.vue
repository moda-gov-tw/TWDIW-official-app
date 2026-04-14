<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    v-model="isOpenEdit"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <!-- 帳號編輯 -->
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">{{ t("accountManagement.edit") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <q-form
            @keydown="preventEnter"
            class="q-gutter-md flex-grow justify-between"
            style="height: 100%"
          >
            <q-card-section class="column">
              <div class="row q-mb-md">
                <!-- 帳號(電子郵件) -->
                <div class="col q-mr-md">
                  <p class="q-mb-sm input-title">
                    <span class="text-red">*</span
                    >{{ t("account.table.userId") }}
                    <q-btn
                      flat
                      dense
                      :icon="isLoginVisible ? 'visibility' : 'visibility_off'"
                      color="black"
                      @click="visible('login')"
                    />
                  </p>
                  <q-input
                    dense
                    outlined
                    :model-value="
                      isLoginVisible
                        ? formDataEdit.login
                        : $m.maskEmail(formDataEdit.login)
                    "
                    name="login"
                    type="mail"
                    autocomplete="off"
                    disable
                  />
                </div>
                <!-- 暱稱 -->
                <div class="col">
                  <p class="q-mb-md input-title">
                    <span class="text-red">*</span>{{ t("account.table.name") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.userName"
                    name="userName"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('notBlank', { input: t('account.table.name') }),
                      customMaxLengthRule(50)
                    ]"
                  />
                </div>
              </div>
              <div class="row q-mb-md">
                <!-- 組織 -->
                <div class="col q-mr-md">
                  <p class="q-mb-md input-title">
                    <span class="text-red">*</span>{{ t("account.table.org") }}
                  </p>
                  <q-select
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.orgId"
                    :options="orgIdOptions"
                    name="orgId"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val || t('choose', { input: t('account.table.org') })
                    ]"
                  />
                </div>
                <!-- 手機號碼 -->
                <div class="col">
                  <p class="q-mb-sm input-title">
                    <span class="text-red">*</span>{{ t("account.table.tel") }}
                    <q-btn
                      flat
                      dense
                      :icon="isTelVisible ? 'visibility' : 'visibility_off'"
                      color="black"
                      @click="visible('tel')"
                    />
                  </p>
                  <q-input
                    dense
                    outlined
                    ref="formRef"
                    :model-value="displayTel"
                    @update:model-value="onTelInput"
                    name="tel"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('notBlank', { input: t('account.table.tel') }),
                      (val) =>
                        val.length >= 10 || t('account.valid.notLessThan'),
                      (val) =>
                        val.length <= 20 || t('account.valid.notGreaterThan'),
                      (val) =>
                        !/[\u4e00-\u9fa5]/.test(val) ||
                        t('account.valid.notEnterChinese'),
                      () =>
                        /^[0-9]*$/.test(formDataEdit.tel) ||
                        t('account.valid.onlyCan')
                    ]"
                  />
                </div>
              </div>
              <div class="row q-mb-md" v-if="hasAdminRole">
                <!-- 帳號類型 -->
                <div class="col q-mr-md">
                  <p class="q-mb-md input-title">
                    {{ t("account.table.userTypeId") }}
                  </p>
                  <q-select
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.userTypeId"
                    :options="userTypeIdOptions"
                    name="userTypeId"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('choose', { input: t('account.table.userTypeId') })
                    ]"
                  />
                </div>
                <div class="col"></div>
              </div>
            </q-card-section>
          </q-form>
        </q-scroll-area>
        <div class="row q-gutter-sm full-width">
          <q-space></q-space>
          <q-btn
            outline
            size="md"
            :label="t('cancel')"
            class="q-mt-md"
            v-close-popup
            @click="formClean"
            color="primary"
          >
          </q-btn>
          <q-btn
            class="q-mt-md"
            size="md"
            :label="t('ok')"
            @click="onSubmit"
            color="primary"
            :disable="loading"
            :loading="loading"
          >
          </q-btn>
        </div>
      </q-card-section>
      <q-card-section style="max-width: 1000px; margin: 0 auto" v-else>
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <q-form
            @keydown="preventEnter"
            class="q-gutter-md flex-grow justify-between"
            style="height: 100%"
          >
            <q-card-section class="column">
              <div class="row q-mb-md">
                <!-- 帳號(電子郵件) -->
                <div class="col q-mr-md">
                  <p class="q-mb-sm input-title">
                    {{ t("account.table.userId") }}
                    <q-btn
                      flat
                      dense
                      :icon="isLoginVisible ? 'visibility' : 'visibility_off'"
                      color="black"
                      @click="visible('login')"
                    />
                  </p>
                  <q-input
                    dense
                    outlined
                    :model-value="
                      isLoginVisible
                        ? formDataEdit.login
                        : $m.maskEmail(formDataEdit.login)
                    "
                    name="login"
                    type="mail"
                    autocomplete="off"
                    disable
                  />
                </div>
                <!-- 暱稱 -->
                <div class="col">
                  <p class="q-mb-md input-title">
                    *{{ t("account.table.name") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.userName"
                    name="userName"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('notBlank', { input: t('account.table.name') }),
                      customMaxLengthRule(50)
                    ]"
                  />
                </div>
              </div>
              <div class="row q-mb-md">
                <!-- 組織 -->
                <div class="col q-mr-md">
                  <p class="q-mb-md input-title">
                    *{{ t("account.table.org") }}
                  </p>
                  <q-select
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.orgId"
                    :options="orgIdOptions"
                    name="orgId"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('choose', { input: t('account.table.orgId') })
                    ]"
                  />
                </div>
                <!-- 手機號碼 -->
                <div class="col">
                  <p class="q-mb-sm input-title">
                    *{{ t("account.table.tel") }}
                    <q-btn
                      flat
                      dense
                      :icon="isTelVisible ? 'visibility' : 'visibility_off'"
                      color="black"
                      @click="visible('tel')"
                    />
                  </p>
                  <q-input
                    dense
                    outlined
                    ref="formRef"
                    :model-value="displayTel"
                    @update:model-value="onTelInput"
                    name="tel"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('notBlank', { input: t('account.table.tel') }),
                      (val) =>
                        val.length >= 10 || t('account.valid.notLessThan'),
                      (val) =>
                        val.length <= 20 || t('account.valid.notGreaterThan'),
                      (val) =>
                        !/[\u4e00-\u9fa5]/.test(val) ||
                        t('account.valid.notEnterChinese'),
                      () =>
                        /^[0-9]*$/.test(formDataEdit.tel) ||
                        t('account.valid.onlyCan')
                    ]"
                  />
                </div>
              </div>
              <div class="row q-mb-md" v-if="hasAdminRole">
                <!-- 帳號類型 -->
                <div class="col q-mr-md">
                  <p class="q-mb-md input-title">
                    *{{ t("account.table.userTypeId") }}
                  </p>
                  <q-select
                    dense
                    outlined
                    class=""
                    v-model="formDataEdit.userTypeId"
                    :options="userTypeIdOptions"
                    name="userTypeId"
                    autocomplete="off"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('choose', { input: t('account.table.userTypeId') })
                    ]"
                  />
                </div>
                <div class="col"></div>
              </div>
            </q-card-section>
          </q-form>
        </q-scroll-area>
        <div class="row q-gutter-sm full-width">
          <q-space></q-space>
          <q-btn
            outline
            size="md"
            :label="t('cancel')"
            class="q-mt-md"
            v-close-popup
            @click="formClean"
            color="primary"
          >
          </q-btn>
          <q-btn
            class="q-mt-md"
            size="md"
            :label="t('confirm')"
            @click="onSubmit"
            color="primary"
            :disable="loading"
            :loading="loading"
          >
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from "vue";
import { useDialogPluginComponent } from "quasar";
import { useAccountStore } from "stores/account";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { mask } from "src/utils/mask";
import { useNotify } from "src/utils/plugin";

const accountStore = useAccountStore();
const { t } = useI18n();
const $m = mask();
const $n = useNotify();
const {
  isOpenEdit,
  orgIdOptions,
  userTypeIdOptions,
  formDataEdit,
  loading,
  hasAdminRole
} = storeToRefs(accountStore);
const { updateUser } = accountStore;
const isLoginVisible = ref(false);
const isTelVisible = ref(false);
// [REF]: FROM
const formRef = ref(null);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

// 計算字元數的自定義規則
const customMaxLengthRule = (maxLength) => {
  return (val) => {
    const totalLength = val.length;
    return totalLength <= maxLength || t("valid.maxLength", { max: maxLength });
  };
};

// 帳號或手機完整顯示
const visible = (type) => {
  if (type === "login") return (isLoginVisible.value = !isLoginVisible.value);
  if (type === "tel") return (isTelVisible.value = !isTelVisible.value);
};

// 手機號碼呈現在畫面上的樣子
const displayTel = computed(() => {
  return isTelVisible.value
    ? formDataEdit.value.tel
    : forMask(formDataEdit.value.tel);
});

// 只要手機號碼欄位有變動就更新
const onTelInput = (val) => {
  if (isTelVisible.value) {
    // 眼睛開：直接更新
    formDataEdit.value.tel = val;
  } else {
    // 眼睛關：使用遮罩邏輯
    formDataEdit.value.tel = $m.applyMaskEdit(formDataEdit.value.tel, val);
  }
};

// 手機號碼格式化 0900***123
const forMask = (data) => {
  return $m.maskPhone(data);
};

const onSubmit = async () => {
  // 驗證表單
  const isValid = await formRef.value.validate();
  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  }

  if (typeof formDataEdit.value.orgId == "object") {
    formDataEdit.value.orgId = formDataEdit.value.orgId.value;
  } else {
    formDataEdit.value.orgId = formDataEdit.value.orgId.split(" ")[0];
  }

  const isOk = await updateUser(formDataEdit.value);
  if (isOk) {
    isOpenEdit.value = false;
  }
};

const preventEnter = (evt) => {
  if (evt.key === "Enter") {
    evt.preventDefault();
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {};

const onDialogShow = () => {};

const formClean = () => {
  formDataEdit.value.id = null;
  formDataEdit.value.login = null;
  formDataEdit.value.userName = null;
  formDataEdit.value.orgId = null;
  formDataEdit.value.tel = null;
};
</script>
