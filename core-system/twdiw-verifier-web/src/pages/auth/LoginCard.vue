<template>
  <q-card class="card card-shadow">
    <q-form @submit="onLogin" class="column fit q-pa-lg">
      <div class="q-pa-sm">
        <!-- 標題 -->
        <div class="q-mb-sm">
          <span class="text-h6 text-grey-8">
            <span class="text-red">*</span>
            {{ t("login.email") }}
          </span>
        </div>
        <!-- 帳號(電子郵件) -->
        <div class="row items-center no-wrap">
          <q-input
            class="col q-pb-md q-mb-sm"
            ref="usernameInput"
            :type="showUsername ? 'text' : 'password'"
            v-model="account"
            :clearable="!isVerified"
            :readonly="isVerified"
            clear-icon="close"
            :placeholder="t('login.placeholder.account')"
            autocomplete="username"
            outlined
            input-class="text-subtitle1"
            autofocus
            :rules="[
              (val) =>
                (val && val.length >= 0) ||
                t('notBlank', { input: t('login.email') })
            ]"
          >
            <template #append>
              <q-btn
                flat
                dense
                :icon="showUsername ? 'visibility' : 'visibility_off'"
                @click="showUsername = !showUsername"
                :aria-label="t(`login.show.${showUsername}`)"
              />
            </template>
          </q-input>

          <!-- 驗證按鈕 -->
          <q-btn
            :color="verifyButtonColor"
            :label="verifyButtonLabel"
            unelevated
            class="verify-otp-btn self-stretch"
            :disable="verifyButtonDisabled"
            @click="handleVerifyClick"
            :loading="loading"
          />
        </div>

        <!-- 密碼 -->
        <span class="text-h6 text-grey-8"
          ><span class="text-red">*</span>{{ t("login.pwd") }}</span
        >
        <q-input
          class="q-pb-md q-mb-sm"
          :type="showPassword ? 'text' : 'password'"
          clearable
          clear-icon="close"
          v-model="pwd"
          :placeholder="t('input', { input: t('login.pwd') })"
          autocomplete="current-password"
          outlined
          input-class="text-subtitle1"
          :rules="[
            (val) =>
              (val && val.length >= 0) ||
              t('notBlank', { input: t('login.pwd') })
          ]"
        >
          <template #append>
            <!-- 顯示或隱藏密碼的按鈕 -->
            <q-btn
              flat
              dense
              :icon="showPassword ? 'visibility' : 'visibility_off'"
              @click="showPassword = !showPassword"
              :aria-label="t(`login.show.${showPassword}`)"
            />
          </template>
        </q-input>

        <!-- 驗證碼 -->
        <span class="text-h6 text-grey-8">
          <span class="text-red">*</span>{{ t("login.verificationCode") }}
        </span>
        <q-input
          class="q-pb-sm"
          outlined
          v-model="captcha"
          :placeholder="t('input', { input: t('login.verificationCode') })"
          maxlength="4"
          clearable
          clear-icon="close"
          input-class="text-subtitle1 text-uppercase"
          :rules="[
            (val) =>
              (val && val.length >= 0) ||
              t('notBlank', { input: t('login.verificationCode') })
          ]"
        >
          <template #append>
            <div class="row q-gutter-sm">
              <img
                v-if="!isCaptchaLoading"
                width="100"
                height="38"
                :src="captchaScr"
                :loading="!isCaptchaLoading"
                @click="genNonce"
                class="cursor-pointer rounded-borders"
              />
              <q-skeleton
                v-else
                type="rect"
                width="100px"
                height="38px"
              ></q-skeleton>
            </div>
            <q-space></q-space>
            <q-btn
              flat
              dense
              :ripple="false"
              color="dark"
              size="md"
              icon="refresh"
              :loading="isCaptchaLoading"
              @click="genNonce"
            />
          </template>
        </q-input>
        <div class="q-mt-md">
          <q-btn
            flat
            :ripple="false"
            color="primary"
            size="md"
            :label="t('login.button.forget')"
            @click="openManualPwdResetDialog"
          />
          <q-btn
            type="submit"
            :ripple="false"
            class="full-width q-mt-sm"
            size="large"
            color="indigo-6"
            :label="t('login.button.login')"
            :loading="loginLoading"
            unelevated
          />
        </div>
        <div
          :class="{ row: $q.screen.gt.sm, column: $q.screen.lt.md }"
          class="align-center justify-between q-mt-md"
        >
          <q-btn
            type="a"
            size="md"
            color="primary"
            outline
            rounded
            @click="handleDownload"
            class="text-body1 cursor-pointer q-mt-sm full-width"
            style="border: 1px solid"
            :loading="isDownloadManualFileLoading"
            >{{ t("login.button.operationManual") }}</q-btn
          >
        </div>
      </div>
      <!-- <q-space></q-space> -->
    </q-form>
  </q-card>

  <q-dialog v-model="isOpenManualPwdReset" persistent>
    <q-card style="width: 750px; min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">
          {{ t("login.dialog.title") }}
        </div>
        <q-space></q-space>
        <q-btn
          flat
          fab-mini
          icon="close"
          v-close-popup
          @click="onResetMailTel"
        ></q-btn>
      </q-card-section>
      <q-form @submit="onConfirm" class="q-pa-md">
        <div class="q-mb-md">
          <q-input
            v-model="pwdEditModel.mail"
            dense
            :rules="valid_mail"
            :placeholder="t('login.dialog.placeholder.account')"
            class="q-mt-sm"
            :label="t('login.email')"
          />
        </div>
        <div class="q-mb-md">
          <q-input
            v-model="pwdEditModel.tel"
            dense
            :rules="valid_phone"
            :placeholder="t('login.dialog.placeholder.tel')"
            class="q-mt-sm"
            :label="t('login.dialog.tel')"
          />
        </div>
        <div class="q-mb-md">
          <q-input
            v-model="resetCaptcha"
            dense
            :rules="valid_captcha"
            :placeholder="t('input', { input: t('login.verificationCode') })"
            class="q-mt-sm"
            :label="t('login.verificationCode')"
            maxlength="4"
            input-class="text-uppercase"
          >
            <template #append>
              <div class="row q-gutter-sm" style="margin-top: -20px">
                <img
                  v-if="!isResetCaptchaLoading"
                  width="100"
                  height="38"
                  :src="resetCaptchaScr"
                  :loading="!isResetCaptchaLoading"
                  @click="genResetNonce"
                  class="cursor-pointer rounded-borders"
                />
                <q-skeleton
                  v-else
                  type="rect"
                  width="100px"
                  height="38px"
                ></q-skeleton>
              </div>
              <q-space></q-space>
              <q-btn
                flat
                dense
                :ripple="false"
                color="dark"
                size="md"
                icon="refresh"
                :loading="isResetCaptchaLoading"
                @click="genResetNonce"
                style="margin-top: -20px"
              />
            </template>
          </q-input>
        </div>
        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn outline color="primary" @click="onResetMailTel">{{
            t("reset")
          }}</q-btn>
          <q-btn
            color="primary"
            type="submit"
            :disable="loading"
            :loading="loading"
            >{{ t("confirm") }}</q-btn
          >
        </div>
      </q-form>
    </q-card>
  </q-dialog>

  <!-- OTP 驗證視窗 -->
  <OtpVerifyDialog
    v-model="showOtpVerifyDialog"
    :email="account"
    @countdown="handleCountdownUpdate"
    @verify="handleOtpVerify"
    @resend="sendOTP"
  />
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { sha512 } from "js-sha512";
import { useNotify, useDialog } from "src/utils/plugin";
import { useManualDownloader } from "src/utils/downloadFile";
import { removeToken, setToken } from "src/boot/auth";
import { api } from "src/boot/axios";
import { useI18n } from "vue-i18n";
import OtpVerifyDialog from "src/pages/auth/OtpVerifyDialog.vue";

const { t } = useI18n();
const $r = useRouter();
//plugin settings
const $n = useNotify();
const $d = useDialog();
const { downloadManualFile } = useManualDownloader();

const showUsername = ref(true);
const showPassword = ref(false);
const showOtpVerifyDialog = ref(false);
// 是否已驗證成功
const isVerified = ref(false);

// 驗證按鈕倒數相關
const verifyCountdown = ref(0);

const isOpenManualPwdReset = ref(false);
const openManualPwdResetDialog = () => {
  isOpenManualPwdReset.value = true;
  resetCaptcha.value = "";
  genResetNonce();
};

const account = ref("");
const pwd = ref("");
const captcha = ref("");
const resetCaptcha = ref("");
const loginLoading = ref(false);
const isCaptchaLoading = ref(false);
const isResetCaptchaLoading = ref(false);
const isDownloadManualFileLoading = ref(false);
const loading = ref(false);

const nonce = ref("");
const resetNonce = ref("");
var captchaScr = ``;
var resetCaptchaScr = ``;

// 計算驗證按鈕的標籤和狀態
const verifyButtonLabel = computed(() => {
  if (isVerified.value) {
    return t("login.button.verified");
  }

  return verifyCountdown.value > 0
    ? `${verifyCountdown.value}s`
    : t("login.button.verify");
});

const verifyButtonDisabled = computed(() => {
  if (isVerified.value) {
    return true;
  }

  return verifyCountdown.value > 0 || !account.value;
});

const verifyButtonColor = computed(() => {
  if (isVerified.value) {
    return "grey-3";
  }

  return "primary";
});

// 處理驗證按鈕點擊
const handleVerifyClick = () => {
  if (!account.value.trim()) {
    $n.error(t("login.error.email"));
    return;
  }

  sendOTP();
};

// 處理倒數更新
const handleCountdownUpdate = (count) => {
  verifyCountdown.value = count;
};

// 處理 OTP 驗證
const handleOtpVerify = () => {
  showOtpVerifyDialog.value = false;
  // 設定已驗證狀態
  isVerified.value = true;
};

const valid_mail = [(val) => !!val || t("required")];
const valid_phone = [
  (val) => !!val || t("required"),
  (val) => (val || "").length >= 10 || t("account.valid.notLessThan"),
  (val) => (val || "").length <= 20 || t("account.valid.notGreaterThan")
];
const valid_captcha = [(val) => !!val || t("required")];

const pwdEditModel = reactive({});

const onResetMailTel = (model) => {
  pwdEditModel.mail = "";
  pwdEditModel.tel = "";
  resetCaptcha.value = "";
};

const genResetNonce = () => {
  api
    .get("/api/modadw303w/generateNonce")
    .then((response) => {
      if (response.data) {
        resetNonce.value = response.data;
        getResetCaptcha(resetNonce);
      }
    })
    .catch((error) => {
      console.error(error);
      if (error.response && error.response.data && error.response.data.detail) {
        $d.alert(t("pleaseConfirm"), error.response.data.detail);
      }
    });
};

const getResetCaptcha = () => {
  isResetCaptchaLoading.value = true;

  api
    .post("api/modadw304w/getCaptcha", resetNonce.value, {
      headers: {
        "Content-Type": "application/json"
      },
      responseType: "blob"
    })
    .then((response) => {
      resetCaptchaScr = URL.createObjectURL(response.data);
    })
    .finally(() => {
      setTimeout(() => {
        isResetCaptchaLoading.value = false;
      }, 500);
    });
};

// 確認重置密碼
const onConfirm = async (model) => {
  loading.value = true;

  try {
    const response = await api.post("/api/modadw301w/reset-bwd/init", {
      mail: pwdEditModel.mail,
      tel: pwdEditModel.tel,
      nonce: {
        ...resetNonce.value,
        captchaCode: resetCaptcha.value.toUpperCase()
      }
    });

    // 檢查回傳的 Nonce 物件是否驗證通過
    if (response.data && response.data.captchaCode === "pass") {
      $n.success(t("login.dialog.success.send"));
      loading.value = false;
      isOpenManualPwdReset.value = false;
      onResetMailTel();
    } else {
      if (error.response && error.response.data && error.response.data.detail) {
        $d.alert(t("pleaseConfirm"), error.response.data.detail);
      } else {
        $d.alert(t("confirm"), t("login.dialog.message.graphicError"));
      }
      genResetNonce();
      resetCaptcha.value = "";
      loading.value = false;
    }
  } catch (error) {
    loading.value = false;
    const detail = error.response?.data?.detail;
    const msg = error.response?.headers?.msg;
    genResetNonce();
    resetCaptcha.value = "";
    if (detail) {
      $d.alert(t("pleaseConfirm"), detail);
    } else if (msg) {
      $d.alert("", deBase64Msg(msg));
    }
  }
};

// 產nonce
const genNonce = () => {
  api
    .get("/api/modadw303w/generateNonce")
    .then((response) => {
      if (response.data) {
        nonce.value = response.data;
        getCaptcha(nonce);
      }
    })
    .catch((error) => {
      console.error(error);
      if (error.response && error.response.data && error.response.data.detail) {
        $d.alert(t("pleaseConfirm"), error.response.data.detail);
      }
    });
};

// 產captcha
const getCaptcha = () => {
  isCaptchaLoading.value = true;

  api
    .post("api/modadw304w/getCaptcha", nonce.value, {
      headers: {
        "Content-Type": "application/json"
      },
      responseType: "blob" // 確保回傳的是二進制資料
    })
    .then((response) => {
      captchaScr = URL.createObjectURL(response.data);
    })
    .finally(() => {
      setTimeout(() => {
        isCaptchaLoading.value = false;
      }, 500);
    });
};

// 登入按扭
const onLogin = () => {
  loginLoading.value = true;
  validateCaptcha(nonce.value);
};

// 驗captcha
const validateCaptcha = (data) => {
  api
    .post("api/modadw304w/validateCaptcha", {
      ...data,
      captchaCode: captcha.value.toUpperCase()
    })
    .then((response) => {
      if (response.data && response.data.captchaCode === "pass") {
        console.debug(t("login.dialog.message.graphicSuccess"));
        auth();
      } else {
        loginLoading.value = false;
        genNonce();
        $d.alert(t("confirm"), t("login.dialog.message.graphicError"));
      }
    })
    .catch((error) => {
      loginLoading.value = false;
      const detail =
        error.response && error.response.data && error.response.data.detail;
      const msg =
        error.response && error.response.headers && error.response.headers.msg;

      genNonce();
      captcha.value = "";

      if (detail) {
        $d.alert(t("pleaseConfirm"), detail);
      } else if (msg) {
        $d.alert("", deBase64Msg(msg));
      }
    });
};

// 登入
const auth = () => {
  loginLoading.value = true;
  const req = {
    username: account.value.toLowerCase().trim(),
    password: sha512(sha512(pwd.value) + nonce.value.nonceId),
    rememberMe: "",
    nonce: { ...nonce.value },
    captcha: captcha.value.toUpperCase()
  };

  api
    .post("/api/modadw303w/authenticate", req)
    .then((response) => {
      if (response.data && response.data.id_token) {
        setToken(response.data.id_token);
        loginLoading.value = false;
        $n.success(t("login.success.login"));
        $r.push("/dw");
      } else {
        $n.error(t("login.error.token"));
      }
    })
    .catch((error) => {
      captcha.value = "";
      const msg =
        error.response?.data?.detail ??
        t("login.error.accountAndPasswordMistake");
      $d.alert(t("login.error.login"), msg);
      genNonce();
    })
    .finally(() => {
      loginLoading.value = false;
    });
};

const deBase64Msg = (msg) => {
  return decodeURIComponent(escape(atob(msg)));
};

// 發送 OTP
const sendOTP = () => {
  loading.value = true;
  const email = account.value.toLowerCase().trim();

  api
    .post(
      "api/modadw303w/generateOtp",
      { email },
      {
        headers: {
          "Content-Type": "application/json"
        }
      }
    )
    .then((response) => {
      if (response.status === 200) {
        $n.success(t("login.success.sendOtp"));
        showOtpVerifyDialog.value = true;
      } else {
        $n.error(t("login.error.sendOtp"));
      }
    })
    .catch((error) => {
      if (error.response && error.response.data && error.response.data.detail) {
        $n.error(error.response.data.detail);
      } else {
        $n.error(t("login.error.sendOtp"));
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

// 下載操作手冊
const handleDownload = async () => {
  isDownloadManualFileLoading.value = true;
  try {
    await downloadManualFile();
  } finally {
    isDownloadManualFileLoading.value = false;
  }
};

onMounted(() => {
  if (localStorage.getItem("sessionExpired")) {
    localStorage.removeItem("sessionExpired");
    $d.alert(t("errorTitle"), t("login.error.expired"));
  }
  removeToken();
  genNonce();
});
</script>
<style scoped>
.card {
  border-radius: 24px;

  width: 80vw;
  min-width: 300px;
  max-width: 600px;

  background-color: white;
  box-shadow: rgba(100, 100, 111, 0.3) 0px 7px 20px 0px;
}

.verify-otp-btn {
  height: 56px;
  margin-left: -2px;
  background: #d33e5f;
  border-radius: 0px 8px 8px 0px;
  color: #fff;
  padding: 15px 20px 15px 20px;
}
</style>
