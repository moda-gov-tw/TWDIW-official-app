<template>
  <q-page>
    <q-dialog v-model="isOpenPwdReset" persistent backdrop-filter="" seamless>
      <q-card style="width: 750px; min-width: 30%; max-width: 80%">
        <!-- 【數位憑證皮夾】發行端模組系統_重設您的密碼 -->
        <q-card-section class="row bg-grey-4 text-white items-center">
          <div class="text-h5 text-weight-bold">
            {{ t("activate.title") }}
          </div>
          <q-space></q-space>
          <q-space></q-space>
        </q-card-section>
        <FormResetPwd
          v-model="pwdEditModel"
          :fields="pwdFields"
          :submitLabel="t('confirm')"
          @confirm="onConfirm"
          @reset="onReset"
        >
          <template #field-newPassword="{ field }">
            <div class="q-pb-md text-h7 text-weight-bold text-grey-4">
              {{ userName + t("activate.newPwdTip") }}
            </div>
            <q-input
              :label="field.label"
              v-model="pwdEditModel.newPassword"
              :type="showPasswordChangeNew ? 'text' : 'password'"
              autocomplete="off"
              :rules="[
                (val) => !!val || t('notBlank', { input: field.label }),
                customMaxLengthRule(50),
                (val) =>
                  (val || '').length >= 12 ||
                  t('activate.valid.include', { symbols: '(!@#)' }),
                (val) =>
                  (val || '').length <= 99 ||
                  t('activate.valid.notGreaterThan'),
                pwdRule
              ]"
              :hint="t('activate.valid.include', { symbols: '(!@#)' })"
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
                  :aria-label="t(`activate.hide.${showPasswordChangeNew}`)"
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
                customMaxLengthRule(50),
                (val) =>
                  val === pwdEditModel.newPassword ||
                  t('activate.valid.inconsistency')
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
                  :aria-label="t(`activate.hide.${showPasswordChangeCheck}`)"
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
    t("activate.valid.include", { symbols: "(!@#)" })
  );
};

const valid_newPassword = [
  (val) => !!val || t("required"),
  (val) =>
    (val || "").length >= 12 ||
    t("activate.valid.include", { symbols: "(!@#)" }),
  (val) => (val || "").length <= 99 || t("activate.valid.notGreaterThan")
];
const valid_confirmPassword = [
  (val) => !!val || t("required"),
  (val) => val === pwdEditModel.newPassword || t("activate.valid.inconsistency")
];

// 計算字元數的自定義規則
const customMaxLengthRule = (maxLength) => {
  return (val) => {
    const totalLength = val.length;
    return totalLength <= maxLength || t("valid.maxLength", { max: maxLength });
  };
};

const pwdEditModel = reactive({});
const pwdFields = ref([
  {
    name: "newPassword",
    label: t("activate.newPwd"),
    inputType: "password",
    field: "newPassword",
    color: "primary",
    hint: t("activate.newPwdTip"),
    rules: valid_newPassword
  },
  {
    name: "confirmPassword",
    label: t("activate.confirmPassword"),
    inputType: "password",
    field: "confirmPassword",
    hint: t("activate.reCheck"),
    rules: valid_confirmPassword
  }
]);

// 驗啟用key&重置key。
const validateUrl = "/api/modadw311w/validate/key";
const keyValidate = () => {
  if (route.query.key1 && route.query.key2) {
    const postData = {
      activationKey: route.query.key1,
      resetKey: route.query.key2
    };

    api
      .post(validateUrl, postData, {
        headers: {
          "Content-Type": "application/json"
        }
      })
      .then((response) => {
        if (response && response.data && response.data.code) {
          if (response.data.code === "0") {
            isOpenPwdReset.value = true;
            userName.value =
              response.data.data.userName +
              "(" +
              response.data.data.userId +
              ")";
            $n.success(t("activate.success.valid"));
          } else {
            if (response.data.msg) {
              $n.error(response.data.msg);
            }
          }
        }
      })
      .catch((error) => {
        console.error("error:", error);
      });
  } else {
    $n.warn(t("activate.error.invalid"));
    $d.alert(t("activate.error.reset"), t("activate.error.info"));
  }
};

// 送出修改。
const onConfirm = async () => {
  const { confirmPassword, ...data } = { ...pwdEditModel };
  data.newPassword = await RSAencrypt(pwdEditModel.newPassword);

  api
    .post("/api/modadw311w/activate", {
      ...data,
      activationKey: route.query.key1,
      resetKey: route.query.key2
    })
    .then((response) => {
      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          $n.success(t("activate.success.finish"));
          isOpenPwdReset.value = false;
          onReset();
          setTimeout(() => {
            router.replace("/");
          }, 5000);
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
        }
      }
    })
    .catch((error) => {
      console.error("error:", error);
    });
};

const onReset = () => {
  if (pwdEditModel.newPassword || pwdEditModel.confirmPassword) {
    pwdEditModel.newPassword = null;
    pwdEditModel.confirmPassword = null;
  }
};

watch(
  () => pwdEditModel.newPassword,
  (newVal) => {
    if (checkBwdInput.value && pwdEditModel.confirmPassword) {
      checkBwdInput.value.validate();
    }
  }
);

onMounted(() => {
  keyValidate();
});
</script>
<style scoped>
.h-full {
  height: 100vh;
}
</style>
