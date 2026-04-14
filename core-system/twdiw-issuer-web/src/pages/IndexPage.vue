<template>
  <q-page class="full-height q-pa-sm column justify-between items-center">
    <div
      class="full-width shadow-3 rounded-borders q-pa-sm overflow-auto appBackground"
      v-if="$q.screen.gt.sm"
    >
      <div class="row justify-start wrap app-content">
        <div class="col q-ml-xl">
          <!-- 立即下載 數位憑證皮夾 APP -->
          <div class="title-big q-my-md">{{ t("home.title") }}</div>
          <div class="alert-text relative-position" style="min-height: 40px">
            <q-inner-loading
              v-if="!appDownloadDate"
              showing
              color="primary"
              size="sm"
              class="absolute-full flex flex-center"
            />

            <span v-else>
              {{ t("home.updateNotice", { date: appDownloadDate }) }}
            </span>
          </div>
          <div class="q-my-xl row justify-around">
            <div class="col">
              <div
                class="q-px-md justify-center column items-center"
                style="max-width: 100%"
              >
                <q-img
                  :src="isProductionEnv ? androidQrcodeProd : androidQrcode"
                  class="q-mb-md bg-white"
                  style="max-width: 150px"
                />
                <q-img
                  class="cursor-pointer"
                  :src="androidBtn"
                  @click="() => openApp('android')"
                  style="max-width: 160px"
                />
              </div>
              <div class="grey-text">{{ t("home.androidSupport") }}</div>
            </div>
            <div class="col">
              <div
                class="q-px-md justify-center column items-center"
                style="max-width: 100%"
              >
                <q-img
                  :src="isProductionEnv ? iosQrcodeProd : iosQrcode"
                  class="q-mb-md bg-white"
                  style="max-width: 150px"
                />
                <q-img
                  class="cursor-pointer"
                  :src="iosBtn"
                  @click="() => openApp('ios')"
                  style="max-width: 160px"
                />
              </div>
              <div class="grey-text">{{ t("home.iosSupport") }}</div>
            </div>
          </div>
          <div
            :class="
              $q.screen.lt.xl
                ? 'column align-center q-mb-sm q-mx-xl'
                : 'row align-center justify-around q-mt-md'
            "
          >
            <q-btn
              type="a"
              rounded
              @click="handleDownload"
              :loading="isDownloadManualFileLoading"
              class="text-body1 cursor-pointer q-mt-sm cus-btn"
              >{{ t("downloadManual") }}</q-btn
            >
          </div>
        </div>
        <div class="col q-ma-md">
          <div
            class="justify-center column items-center q-my-sm"
            style="max-width: 100%"
          >
            <q-img :src="phoneImg" style="max-width: 400px" />
          </div>
        </div>
      </div>
    </div>
    <div
      class="full-height full-width shadow-3 rounded-borders q-pa-sm overflow-auto appBackground"
      v-else
    >
      <div class="row justify-between wrap app-content">
        <div class="col q-ma-md">
          <div class="title-big">{{ t("home.title") }}</div>
          <div class="alert-text relative-position" style="min-height: 40px">
            <q-inner-loading
              v-if="!appDownloadDate"
              showing
              color="primary"
              size="sm"
              class="absolute-full flex flex-center"
            />
            <span v-else>
              {{ t("home.updateNotice", { date: appDownloadDate }) }}
            </span>
          </div>
          <div
            class="justify-center column items-center q-my-sm"
            style="max-width: 100%"
          >
            <q-img :src="phoneImg" style="max-width: 250px" />
          </div>
          <div class="q-mb-xl">
            <div
              class="justify-center column items-center"
              style="max-width: 100%"
            >
              <q-img
                :src="isProductionEnv ? androidQrcodeProd : androidQrcode"
                class="q-mb-sm bg-white"
                style="max-width: 120px"
              />
              <q-img
                class="cursor-pointer"
                :src="androidBtn"
                @click="() => openApp('android')"
                style="max-width: 130px"
              />
            </div>
            <div class="grey-text">{{ t("home.androidSupport") }}</div>
          </div>
          <div class="q-mb-xl">
            <div
              class="q-px-md justify-center column items-center"
              style="max-width: 100%"
            >
              <q-img
                :src="isProductionEnv ? iosQrcodeProd : iosQrcode"
                class="q-mb-sm bg-white"
                style="max-width: 120px"
              />
              <q-img
                class="cursor-pointer"
                :src="iosBtn"
                @click="() => openApp('ios')"
                style="max-width: 130px"
              />
            </div>
            <div class="grey-text">{{ t("home.iosSupport") }}</div>
          </div>
        </div>
      </div>
      <div class="column align-center q-mb-xl q-mx-md">
        <q-btn
          type="a"
          rounded
          @click="handleDownload"
          :loading="isDownloadManualFileLoading"
          class="text-body1 cursor-pointer q-mt-sm cus-btn"
          >{{ t("downloadManual") }}</q-btn
        >
      </div>
    </div>

    <ManagementInfo @env-loaded="onEnvLoaded" />
  </q-page>
</template>

<script setup>
import iosBtn from "src/assets/applestore.png";
import androidBtn from "src/assets/googleplay.png";
import iosQrcode from "src/assets/appUat/iosQrcode_1127.png";
import androidQrcode from "src/assets/appUat/androidQrcode_1127.png";
import iosQrcodeProd from "src/assets/appProd/iosQrcode_1127_prod.png";
import androidQrcodeProd from "src/assets/appProd/androidQrcode_1127_prod.png";
import phoneImg from "src/assets/top_phone.webp";
import ManagementInfo from "src/components/ManagementInfo.vue";
import { useManualDownloader } from "src/utils/downloadFile";
import { ref, computed } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const { downloadManualFile } = useManualDownloader();
const isDownloadManualFileLoading = ref(false);

defineOptions({
  name: "IndexPage"
});

// 當前環境
const currentEnv = ref("");
// APP 更新日期
const appDownloadDate = ref(null);

const onEnvLoaded = (env, downloadDate) => {
  currentEnv.value = env;
  appDownloadDate.value = downloadDate;
};

// 是否為 Production 環境
const isProductionEnv = computed(() =>
  ["prod", "release"].includes(currentEnv.value)
);

// App URL 管理
const appUrls = {
  ios: {
    dev: "https://testflight.apple.com/join/PssjD8qx",
    prod: "https://apps.apple.com/tw/app/id6742222992"
  },
  android: {
    dev: "https://www.wallet.gov.tw/apply/beta_app_download_page.html",
    prod: "https://play.google.com/store/apps/details?id=tw.gov.moda.diw"
  }
};

// 打開 App
const openApp = (platform) => {
  const env = isProductionEnv.value ? "prod" : "dev";
  window.open(appUrls[platform][env], "_blank");
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
</script>
<style scoped>
.grey-text {
  color: #999999;
  text-align: center;
  white-space: pre-wrap;
}
.appBackground {
  min-height: calc((30rem + 8vw));
  background: url(src/assets/background/top_bg-01.webp);
  background-color: white;
  background-position: center 400px;
  background-size: 100%;
  background-repeat: no-repeat;
  background-attachment: fixed;
  position: relative;
}
.title-big {
  font-size: calc(1rem + 1.5vw);
  font-weight: 700;
  display: inline-block;
  color: #4e4e4e;
  white-space: pre-wrap;
}

.cus-btn {
  border: 1px solid;
  min-width: 80%;
  background: #4e61a7;
  color: white;
  font-weight: 700;
}
</style>
