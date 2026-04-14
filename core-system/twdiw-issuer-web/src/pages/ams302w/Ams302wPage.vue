<template>
  <q-page>
    <q-dialog v-model="isOpenPwdReset" persistent>
      <q-card style="width: 750px; min-width: 30%; max-width: 80%">
        <!-- 【數位憑證皮夾】發行端模組系統_重設您的密碼 -->
        <q-card-section class="row bg-grey-4 text-white items-center">
          <div class="text-h5 text-weight-bold">
            {{ t("ams302w.title") }}
          </div>
          <q-space></q-space>
        </q-card-section>
        <FormResetPwd
          v-model="pwdEditModel"
          :fields="pwdFields"
          :submitLabel="$t('ok')"
          @confirm="onConfirm"
          @reset="onReset"
        >
          <template #field-newPassword="{ field }">
            <div class="q-pb-md text-h7 text-weight-bold text-grey-4">
              {{ userName + t("input", { input: t("ams302w.inputPWD") }) }}
            </div>
            <q-input
              :label="field.label"
              v-model="pwdEditModel.newPassword"
              :type="showPasswordChangeNew ? 'text' : 'password'"
              autocomplete="off"
              :rules="[
                (val) => !!val || t('notBlank', { input: field.label }),
                (val) =>
                  (val || '').length >= 12 ||
                  t('ams302w.valid.include', { symbols: '(!@#)' }),
                (val) =>
                  (val || '').length <= 99 || t('ams302w.valid.notGreaterThan'),
                pwdRule
              ]"
              :hint="t('ams302w.valid.include', { symbols: '(!@#)' })"
            >
              <template #append>
                <!-- 顯示或隱藏密碼的按鈕 -->
                <q-btn
                  flat
                  dense
                  :icon="
                    showPasswordChangeNew ? 'visibility' : 'visibility_off'
                  "
                  @click="showPasswordChangeNew = !showPasswordChangeNew"
                  :aria-label="t(`ams302w.button.${showPasswordChangeNew}`)"
                />

                <!-- 清除密碼欄位的按鈕 -->
                <q-btn
                  flat
                  dense
                  icon="clear"
                  @click="pwdEditModel.newPassword = ''"
                />
              </template>
            </q-input>
          </template>
          <template #field-confirmPassword="{ field }">
            <q-input
              :label="field.label"
              v-model="pwdEditModel.confirmPassword"
              :type="showPasswordChangeCheck ? 'text' : 'password'"
              autocomplete="off"
              :rules="[
                (val) => !!val || t('notBlank', { input: field.label }),
                (val) =>
                  val === pwdEditModel.newPassword ||
                  t('ams302w.valid.inconsistency')
              ]"
              ref="checkBwdInput"
            >
              <template #append>
                <!-- 顯示或隱藏密碼的按鈕 -->
                <q-btn
                  flat
                  dense
                  :icon="
                    showPasswordChangeCheck ? 'visibility' : 'visibility_off'
                  "
                  @click="showPasswordChangeCheck = !showPasswordChangeCheck"
                  :aria-label="t(`ams302w.button.${showPasswordChangeCheck}`)"
                />

                <!-- 清除密碼欄位的按鈕 -->
                <q-btn
                  flat
                  dense
                  icon="clear"
                  @click="pwdEditModel.confirmPassword = ''"
                />
              </template>
            </q-input>
          </template>
        </FormResetPwd>
      </q-card>
    </q-dialog>
    <div class="col"></div>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useNotify, useDialog } from "src/utils/plugin";
import FormResetPwd from "components/FormWrapper.vue";
import { api } from "src/boot/axios";
import { RSAencrypt } from "src/utils/encrypt";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const $n = useNotify();
const $d = useDialog();
const router = useRouter();
const route = useRoute();
const showPasswordChangeNew = ref(false);
const showPasswordChangeCheck = ref(false);

const isOpenPwdReset = ref();
isOpenPwdReset.value = false;
const checkBwdInput = ref(null);
const userName = ref(null);

const pwdRule = (pwd) => {
  return (
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*(),.?":{}|<>]).*$/.test(pwd) ||
    t("ams302w.valid.include", { symbols: "(!@#)" })
  );
};

const valid_newPassword = [
  (val) => !!val || t("required"),
  (val) =>
    (val || "").length >= 12 ||
    t("ams302w.valid.include", { symbols: "(!@#)" }),
  (val) => (val || "").length <= 99 || t("ams302w.valid.notGreaterThan")
];
const valid_confirmPassword = [
  (val) => !!val || t("required"),
  (val) => val === pwdEditModel.newPassword || t("ams302w.valid.inconsistency")
];

const pwdEditModel = reactive({});
const pwdFields = ref([
  {
    name: "newPassword",
    label: t("ams302w.fields.newPwd"),
    inputType: "password",
    field: "newPassword",
    color: "primary",
    hint: t("input", { input: t("ams302w.inputPWD") }),
    rules: valid_newPassword
  },
  {
    name: "confirmPassword",
    label: t("ams302w.fields.confirmPassword"),
    inputType: "password",
    field: "confirmPassword",
    hint: t("ams302w.recheck"),
    rules: valid_confirmPassword
  }
]);

// 驗重置key
const keyValidate = () => {
  console.debug("param ", route.query);

  if (route.query.key) {
    api
      .post("/api/modadw302w/reset-bwd/validate/resetKey", route.query.key, {
        headers: {
          "Content-Type": "text/plain"
        }
      })
      .then((response) => {
        console.debug("重置key 驗證完畢", response.data);

        if (response.data) {
          isOpenPwdReset.value = true;
          userName.value =
            response.data.userName + "(" + response.data.userId + ")";
          $n.success(t("ams302w.success.valid"));
        } else {
          $n.warn(t("ams302w.warn.invalid"));
          $d.custom(
            {
              title: t("ams302w.resetPassword"),
              message: t("ams302w.message.resetPassword"),
              html: true,
              persistent: true
            },
            () => router.replace("/")
          );
        }
      })
      .catch((error) => {
        if (
          error.response &&
          error.response.data &&
          error.response.data.detail
        ) {
          $d.alert(t("ams302w.confirm"), error.response.data.detail);
        }
      });
  } else {
    $n.warn(t("ams302w.warn.invalid"));
    $d.custom(
      {
        title: t("ams302w.resetPassword"),
        message: t("ams302w.message.resetPassword"),
        html: true,
        persistent: true
      },
      () => router.replace("/")
    );
  }
};

// 送出修改
const onConfirm = async (model) => {
  const { confirmPassword, ...data } = { ...pwdEditModel };
  data.newPassword = await RSAencrypt(pwdEditModel.newPassword);
  api
    .post("/api/modadw302w/reset-bwd/finish", {
      ...data,
      key: route.query.key
    })
    .then((response) => {
      $n.success(t("ams302w.success.reset"));
      isOpenPwdReset.value = false;
      setTimeout(() => {
        router.replace("/");
      }, 5000);
    })
    .catch((error) => {
      if (error.response?.data?.message) {
        const message = error.response.data.message;
        // 判斷是否包含"密碼不可重複"
        if (message.includes(t("ams302w.error.repeat"))) {
          // 使用 message[0] 取得第一個字
          const firstChar = message.charAt(0);
          $d.alert(
            t("ams302w.confirm"),
            t("ams302w.message.repeat", { input: firstChar })
          );
        } else {
          $d.alert(t("ams302w.confirm"), message);
        }
        isOpenPwdReset.value = true;
      }
    });
};

watch(
  () => pwdEditModel.newPassword,
  (newVal) => {
    if (checkBwdInput.value && pwdEditModel.confirmPassword) {
      checkBwdInput.value.validate();
    }
  }
);

const onReset = () => {
  Object.assign(pwdEditModel, { newPassword: null, confirmPassword: null });
};

onMounted(() => {
  keyValidate();
});
</script>
<style scoped>
.h-full {
  height: 100vh;
}
</style>
