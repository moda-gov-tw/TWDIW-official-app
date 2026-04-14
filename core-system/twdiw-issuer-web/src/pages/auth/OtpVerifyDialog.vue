<template>
  <q-dialog v-model="show" persistent>
    <q-card style="width: 450px; min-width: 25%; max-width: 100%">
      <!-- 驗證電子郵件 -->
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">
          {{ t("login.dialog.verifyOtpTitle") }}
        </div>
        <q-space></q-space>
        <q-btn
          icon="close"
          flat
          round
          dense
          v-close-popup
          @click="handleClose"
        />
      </q-card-section>

      <!-- 已寄送驗證碼至 -->
      <q-card-section>
        <div class="text-body1 q-mt-sm">
          {{ t("login.dialog.content.send") }}
          <span class="text-bold">{{ email }}</span
          ><br />
          {{ t("login.dialog.content.otp1") }}<br />
          {{ t("login.dialog.content.otp2") }}
        </div>

        <!-- 倒數 / 重新發送 -->
        <div class="text-body2 text-grey q-mt-sm">
          <template v-if="countdown > 0">
            {{ countdown }} {{ t("login.dialog.content.resendOtp") }}
          </template>
          <template v-else>
            <q-btn
              flat
              color="primary"
              :label="t('login.button.resendOtp')"
              @click="handleResendOtp"
            />
          </template>
        </div>

        <!-- OTP 輸入框 -->
        <div class="otp-container" @keydown.enter.prevent="verifyOtp">
          <q-input
            v-for="(digit, index) in otp"
            :key="index"
            v-model="otp[index]"
            ref="otpRefs"
            maxlength="1"
            dense
            outlined
            type="text"
            inputmode="numeric"
            class="q-mx-xs otp-box"
            @update:model-value="handleInput($event, index)"
            @paste="handlePaste($event, index)"
            @keydown.backspace="handleBackspace(index, $event)"
          />
        </div>

        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn
            outline
            :label="t('cancel')"
            color="primary"
            @click="handleClose"
          />
          <q-btn
            :label="t('confirm')"
            color="primary"
            @click="verifyOtp"
            :loading="loading"
          />
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, computed, toRefs } from "vue";
import { api } from "src/boot/axios";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";

const { t } = useI18n();
const $n = useNotify();
const loading = ref(false);

// Props 定義
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  email: {
    type: String,
    default: ""
  }
});

// Emits 定義
const emit = defineEmits([
  "update:modelValue",
  "verify",
  "resend",
  "countdown"
]);

// Props 響應式解構
const { modelValue, email } = toRefs(props);

// 響應式數據
const show = ref(modelValue.value);
const otp = ref(["", "", "", "", "", ""]);
const otpRefs = ref([]);
const countdown = ref(0);

// 定時器引用
let timer = null;

// 計算屬性
const otpValue = computed(() => otp.value.join(""));

// 清空 OTP 輸入
const clearOtp = () => {
  otp.value = ["", "", "", "", "", ""];
};

// 聚焦指定輸入框
const focusInput = (index) => {
  const input = otpRefs.value[index]?.$el?.querySelector("input");
  input?.focus();
};

// 啟動倒數
const startCountdown = () => {
  if (timer) {
    clearInterval(timer);
  }

  countdown.value = 60;
  // 向父組件發送倒數狀態
  emit("countdown", countdown.value);

  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
      // 持續向父組件發送倒數狀態
      emit("countdown", countdown.value);
    } else {
      clearInterval(timer);
      timer = null;
      // 倒數結束，通知父組件
      emit("countdown", 0);
    }
  }, 1000);
};

// 停止倒數
const stopCountdown = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  countdown.value = 0;
  // 停止倒數時通知父組件重置狀態
  emit("countdown", 0);
};

// 處理貼上事件
const handlePaste = (event, index) => {
  event.preventDefault();

  const pastedData = event.clipboardData.getData("text");

  const numbers = pastedData.replace(/[^0-9]/g, "");

  if (numbers.length > 0) {
    const remainingSlots = otp.value.length - index;
    const numbersToFill = numbers.slice(0, remainingSlots);

    for (let i = 0; i < numbersToFill.length; i++) {
      if (index + i < otp.value.length) {
        otp.value[index + i] = numbersToFill[i];
      }
    }

    const nextFocusIndex = Math.min(
      index + numbersToFill.length,
      otp.value.length - 1
    );
    nextTick(() => {
      focusInput(nextFocusIndex);
    });
  }
};

// OTP 輸入處理
const handleInput = (val, index) => {
  const numericValue = String(val || "")
    .replace(/[^0-9]/g, "")
    .slice(0, 1);
  otp.value[index] = numericValue;

  if (numericValue && index < otp.value.length - 1) {
    nextTick(() => {
      focusInput(index + 1);
    });
  }
};

// Backspace 處理
const handleBackspace = (index, event) => {
  if (otp.value[index] !== "") {
    otp.value[index] = "";
  } else if (index > 0) {
    event.preventDefault();
    otp.value[index - 1] = "";
    nextTick(() => {
      focusInput(index - 1);
    });
  }
};

// 重新發送 OTP
const handleResendOtp = () => {
  clearOtp();
  emit("resend");
  startCountdown();
  nextTick(() => {
    focusInput(0);
  });
};

// 關閉對話框
const handleClose = () => {
  show.value = false;
  emit("update:modelValue", false);
};

// 監聽器
watch(modelValue, (newVal, oldVal) => {
  show.value = newVal;
  if (newVal) {
    clearOtp();
    startCountdown();
    nextTick(() => {
      focusInput(0);
    });
  }
});

// 生命週期鉤子
onMounted(() => {
  // 組件掛載時，如果對話框已經是開啟狀態，則初始化
  if (modelValue.value) {
    clearOtp();
    startCountdown();
    nextTick(() => {
      focusInput(0);
    });
  }
});

// 驗證 OTP
const verifyOtp = () => {
  loading.value = true;

  const req = {
    email: email.value,
    otpToken: otpValue.value
  };

  api
    .post("api/modadw303w/verifyOtp", req, {
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((response) => {
      if (response.status === 200) {
        $n.success(t("login.success.verifyOtp"));
        emit("verify", otpValue.value);
        stopCountdown();
      } else {
        const errMsg = response.data.detail || t("login.error.verifyOtp");
        $n.error(errMsg);
      }
    })
    .catch((error) => {
      const errMsg = error.response.data.detail || t("login.error.verifyOtp");
      $n.error(errMsg);
    })
    .finally(() => {
      loading.value = false;
    });
};
</script>

<style scoped>
.otp-container {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;
}

.otp-box {
  width: 45px;
  height: 45px;
  text-align: center;
  border-radius: 6px;
}

/* 讓輸入框內的文字置中 */
.otp-box :deep(.q-field__control) {
  text-align: center;
}

.otp-box :deep(input) {
  text-align: center;
  font-size: 20px;
}

/* 隱藏 Chrome、Safari 的上下箭頭 */
.otp-box :deep(::-webkit-inner-spin-button),
.otp-box :deep(::-webkit-outer-spin-button) {
  -webkit-appearance: none;
  margin: 0;
}

/* 隱藏 Firefox 的上下箭頭 */
.otp-box :deep(input[type="number"]) {
  -moz-appearance: textfield;
}
</style>
