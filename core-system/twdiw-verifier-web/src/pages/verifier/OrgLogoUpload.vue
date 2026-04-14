<template>
  <basic-page>
    <!-- HEADER -->
    <div class="row q-ma-none q-py-sm q-pl-md items-center">
      <p class="titleRwd q-mb-md">{{ t("title.orgLogoUpload") }}</p>
      <div
        v-if="!verifierDID"
        :class="['alert-text', isMobile ? '' : 'q-ml-lg']"
      >
        {{ t("orgLogo.firstComplete") }}
      </div>
      <div
        v-else-if="!isDidOrg"
        :class="['alert-text', isMobile ? '' : 'q-ml-lg']"
      >
        {{ t("orgLogo.noUploadPermission") }}
      </div>
      <q-space></q-space>
    </div>

    <div class="row justify-start q-pa-md" style="min-height: 70vh">
      <q-inner-loading :showing="loading" color="primary" />
      <!-- BODY -->
      <div
        :class="
          isMobile ? 'col-12' : 'col-6 col-xl-5 column justify-between q-pr-lg'
        "
      >
        <div class="q-mt-lg">
          <!-- Table欄位 -->
          <q-markup-table
            class="orgLogo-table"
            separator="horizontal"
            bordered
            flat
          >
            <tbody>
              <tr v-for="row in filteredRows" :key="row.field">
                <td class="text-left text-bold bg-grey-1" style="width: 200px">
                  {{ getLabel(row.name) }}
                </td>

                <td
                  class="text-right"
                  style="white-space: normal; word-break: break-word"
                >
                  {{ formData[row.name] }}
                </td>
              </tr>
            </tbody>
          </q-markup-table>
          <!-- 上傳 LOGO 檔案 -->
          <div class="q-mb-md q-mt-xl">
            <div class="row justify-start">
              <p class="cus-title-text q-mb-xs">
                {{ t("orgLogo.square.title") }}
              </p>
              <q-icon
                class="cursor-pointer col-auto q-ma-xs"
                color="primary"
                name="info_outline"
                size="sm"
                @click="toggleNotice('square')"
              >
                <q-popup-proxy
                  v-if="!isMobile"
                  anchor="bottom left"
                  self="top left"
                  :offset="[223, 50]"
                  class="bg-grey-2"
                  max-width="300px"
                >
                  <div class="text-white text-body2 notice">
                    {{ t("orgLogo.square.logoSize") }}
                  </div>
                </q-popup-proxy>
              </q-icon>
            </div>
            <q-file
              ref="fileInput"
              outlined
              dense
              :disable="!verifierDID || !isDidOrg"
              v-model="formData.picName"
              :label="fileLabel"
              @update:model-value="onFilesAdded"
            >
              <template v-slot:label>
                <q-icon
                  v-if="formData.logoSquare"
                  class="q-mr-xs"
                  name="check_circle"
                  color="indigo-6"
                  size="xs"
                />
                <span>{{ fileLabel }}</span>
              </template>
              <template v-slot:append>
                <q-icon
                  v-if="formData.logoSquare"
                  class="cursor-pointer"
                  name="cancel"
                  color="grey-4"
                  @click="clearImage('square')"
                />
                <q-icon name="attachment" @click="openFileDialog('square')" />
              </template>
            </q-file>

            <div class="row justify-end q-mt-md">
              <!-- LOGO 向量檔下載 -->
              <q-btn
                :class="[
                  'text-body1 q-mr-sm',
                  isMobile ? 'full-width q-mt-sm' : ''
                ]"
                type="a"
                size="md"
                outline
                :disable="!verifierDID || !isDidOrg"
                :href="defaultSample"
                download="sample.svg"
                color="grey-4"
              >
                {{ t("orgLogo.button.logoImageVector") }}
              </q-btn>
              <!-- 還原預設 LOGO -->
              <q-btn
                :class="['text-body1', isMobile ? 'full-width q-mt-sm' : '']"
                size="md"
                outline
                :disable="!verifierDID || !isDidOrg"
                color="indigo-6"
                @click="reductionDefaultImage('square')"
              >
                {{ t("orgLogo.button.reduction") }}
              </q-btn>
            </div>
          </div>

          <!-- 上傳長方形 LOGO 檔案 -->
          <div class="q-mb-md q-mt-xl">
            <div class="row justify-start">
              <p class="cus-title-text q-mb-xs">
                {{ t("orgLogo.rectangle.title") }}
              </p>
              <q-icon
                class="cursor-pointer col-auto q-ma-xs"
                color="primary"
                name="info_outline"
                size="sm"
                @click="toggleNotice('rectangle')"
              >
                <q-popup-proxy
                  v-if="!isMobile"
                  anchor="bottom left"
                  self="top left"
                  :offset="[223, 50]"
                  class="bg-grey-2"
                  max-width="300px"
                >
                  <div class="text-white text-body2 notice">
                    {{ t("orgLogo.rectangle.logoSize") }}
                  </div>
                </q-popup-proxy>
              </q-icon>
            </div>

            <q-file
              ref="rectangleFileInput"
              outlined
              dense
              :disable="!verifierDID || !isDidOrg"
              v-model="formData.rectanglePicName"
              :label="rectangleFileLabel"
              @update:model-value="onRectangleFilesAdded"
            >
              <template v-slot:label>
                <q-icon
                  v-if="formData.logoRectangle"
                  class="q-mr-xs"
                  name="check_circle"
                  color="indigo-6"
                  size="xs"
                />
                <span>{{ rectangleFileLabel }}</span>
              </template>
              <template v-slot:append>
                <q-icon
                  v-if="formData.logoRectangle"
                  name="cancel"
                  color="grey-4"
                  class="cursor-pointer"
                  @click="clearImage('rectangle')"
                />
                <q-icon
                  name="attachment"
                  @click="openFileDialog('rectangle')"
                />
              </template>
            </q-file>

            <div class="row justify-end q-mt-md">
              <!-- LOGO 向量檔下載 -->
              <q-btn
                :class="[
                  'text-body1 q-mr-sm',
                  isMobile ? 'full-width q-mt-sm' : ''
                ]"
                type="a"
                size="md"
                outline
                :disable="!verifierDID || !isDidOrg"
                :href="rectangleDefaultSample"
                download="rectangleSample.svg"
                color="grey-4"
              >
                {{ t("orgLogo.button.logoImageVector") }}
              </q-btn>
              <!-- 還原預設 LOGO -->
              <q-btn
                :class="['text-body1', isMobile ? 'full-width q-mt-sm' : '']"
                size="md"
                outline
                :disable="!verifierDID || !isDidOrg"
                color="indigo-6"
                @click="reductionDefaultImage('rectangle')"
              >
                {{ t("orgLogo.button.reduction") }}
              </q-btn>
            </div>
          </div>
        </div>

        <!-- 手機版的對話框 -->
        <q-dialog v-model="showMobileNotice">
          <q-card style="min-width: 300px">
            <q-card-section>
              <div class="text-h6">{{ t("orgLogo.filePick") }}</div>
            </q-card-section>

            <q-card-section>
              <p class="text-body2">
                {{ t("orgLogo.square.logoSize") }}
              </p>
            </q-card-section>

            <q-card-actions align="right">
              <q-btn
                outline
                :label="t('close')"
                color="primary"
                v-close-popup
              />
            </q-card-actions>
          </q-card>
        </q-dialog>

        <!-- 手機版的對話框 長方 -->
        <q-dialog v-model="showMobileRectangleNotice">
          <q-card style="min-width: 300px">
            <q-card-section>
              <div class="text-h6">{{ t("orgLogo.filePick") }}</div>
            </q-card-section>

            <q-card-section>
              <p class="text-body2">
                {{ t("orgLogo.rectangle.logoSize") }}
              </p>
            </q-card-section>

            <q-card-actions align="right">
              <q-btn
                outline
                :label="t('close')"
                color="primary"
                v-close-popup
              />
            </q-card-actions>
          </q-card>
        </q-dialog>

        <!-- FOOTER -->
        <div class="row justify-end q-mb-xs">
          <q-btn
            :disable="!verifierDID || loading || !isDidOrg"
            unelevated
            :label="t('orgLogo.button.confirmUpload')"
            color="primary"
            style="width: 150px"
            :loading="loading"
            @click="onOKClick"
          >
            <q-tooltip
              v-if="!verifierDID"
              class="bg-orange-7 text-body2"
              :anchor="isMobile ? 'bottom right' : 'top left'"
              :self="isMobile ? 'top right' : 'bottom left'"
              :offset="[0, 5]"
              :max-width="isMobile ? '350px' : ''"
              :loading="loading"
            >
              {{ t("orgLogo.firstComplete") }}
            </q-tooltip>
            <q-tooltip
              v-else-if="!isDidOrg"
              class="bg-orange-7 text-body2"
              :anchor="isMobile ? 'bottom right' : 'top left'"
              :self="isMobile ? 'top right' : 'bottom left'"
              :offset="[0, 5]"
              :max-width="isMobile ? '350px' : ''"
              :loading="loading"
            >
              {{ t("orgLogo.noUploadPermission") }}
            </q-tooltip>
          </q-btn>
        </div>
      </div>
      <!-- 右側 -->
      <div
        :class="['row justify-center', isMobile ? 'col-12 q-my-md' : 'col-6']"
      >
        <div>
          <div
            :class="[
              'column justify-start items-center',
              isMobile ? 'q-mt-md' : ''
            ]"
            :style="{
              border: '2px dashed #ccc',
              borderRadius: '10px',
              width: isMobile ? '100%' : '350px',
              minHeight: '400px'
            }"
          >
            <!-- 形象網站合作清單 示意圖 -->
            <p class="q-my-md text-h6 text-grey">
              {{ t("orgLogo.logoPreview") }}
            </p>

            <div
              class="column justify-start items-center"
              :style="{
                borderTop: '2px dashed #ccc',
                borderBottom: '2px dashed #ccc',
                minWidth: '100%'
              }"
            >
              <!-- 正方形 LOGO： -->
              <div class="q-mt-sm justify-center items-left shape-title-text">
                <p class="text-grey">
                  {{ t("orgLogo.square.previewSection") }}：
                </p>
              </div>
              <!-- 卡片 -->
              <div
                class="q-my-md justify-center items-left card-block"
                :style="{
                  minHeight: '150px',
                  width: isMobile ? '90%' : ''
                }"
              >
                <div class="column q-ma-md">
                  <div class="flex flex-center logo-container">
                    <template v-if="formData.logoSquare">
                      <q-img
                        :src="formData.logoSquare"
                        class="fit-cover logo-image"
                        :alt="t('orgLogo.preview')"
                      />
                    </template>
                    <template v-else>
                      <q-img
                        :src="sampleLogo"
                        class="fit-cover logo-image"
                        :alt="t('orgLogo.default')"
                      />
                    </template>
                  </div>
                  <div class="col-auto q-pa-xs q-mb-sm card-title-text">
                    {{ formData.orgCname }}
                  </div>
                  <div
                    class="col-4 row justify-end q-mt-sm"
                    style="height: 10%"
                  >
                    <div
                      class="row bg-grey-1 text-center justify-center"
                      style="border-radius: 30px"
                    >
                      <div class="row q-pa-xs items-center">
                        <q-icon
                          class="q-mr-xs"
                          name="verified"
                          color="secondary"
                          size="xs"
                        />
                        <p class="q-mr-xs" style="font-weight: 500">
                          {{ t("orgLogo.certified") }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 長方形 LOGO： -->
            <div class="q-mt-sm justify-center items-left shape-title-text">
              <p class="text-grey">
                {{ t("orgLogo.rectangle.previewSection") }}：
              </p>
            </div>
            <!-- 卡片 -->
            <div
              class="q-my-md justify-center items-left rectangle-card-block"
              :style="{ width: isMobile ? '' : '' }"
            >
              <div class="column q-ma-md">
                <div class="flex flex-center rectangle-logo-container">
                  <template v-if="formData.logoRectangle">
                    <q-img
                      :src="formData.logoRectangle"
                      class="fit-cover rectangle-logo-image"
                      :alt="t('orgLogo.preview')"
                    />
                  </template>
                  <template v-else>
                    <q-img
                      :src="rectangleSampleLogo"
                      class="fit-cover rectangle-logo-image"
                      :alt="t('orgLogo.default')"
                    />
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </basic-page>
</template>

<script setup>
// 引用
import { ref, onMounted, reactive, computed } from "vue";
import { useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
import { getToken } from "boot/auth";
import { storeToRefs } from "pinia";
import sampleLogo from "src/assets/defaultLogo_small.png";
import defaultSample from "src/assets/defaultLogo.svg";
import rectangleDefaultSample from "src/assets/rectangleDefaultLogo.svg";
import { useOrgLogoStore } from "src/stores/orgLogo";

// [STORE]
const orgLogoStore = useOrgLogoStore();
const { verifierDID, orgInfo, loading, rectangleSampleLogo } =
  storeToRefs(orgLogoStore);
const { searchOrg, uploadOrgLogo, getDefaultLogo } = orgLogoStore;

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();

const jwtToken = getToken();
const sampleLogoBase64 = ref(null);
const loginUserTaxId = ref("");
const showMobileNotice = ref(false);
const showMobileRectangleNotice = ref(false);
const fileInput = ref(null);
const rectangleFileInput = ref(null);

// [FORM]
const form = {
  orgId: "",
  orgCname: "",
  orgEname: "",
  picName: null,
  rectanglePicName: null,
  logoSquare: "",
  logoRectangle: ""
};

// [REACTIVE]: 表單
const formData = reactive({ ...form });

// [COLUMNS]
const rows = ref([
  { name: "orgId", value: "" },
  { name: "orgCname", value: "" },
  { name: "orgEname", value: "" }
]);

const filteredRows = computed(() => rows.value);

// TABLE欄位名稱
const getLabel = (name) => {
  switch (name) {
    case "orgId":
      return t("org.orgId.label");
    case "orgCname":
      return t("org.orgTwName.label");
    case "orgEname":
      return t("org.orgEnName.label");
    default:
      return "";
  }
};

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 方形Logo
const fileLabel = computed(() => {
  return formData.logoSquare
    ? t("orgLogo.upload.true")
    : t("orgLogo.upload.false");
});

// 長方形Logo
const rectangleFileLabel = computed(() => {
  return formData.logoRectangle
    ? t("orgLogo.upload.true")
    : t("orgLogo.upload.false");
});

// 選擇 LOGO 檔案 資訊 i 開關
const toggleNotice = (shape) => {
  if (shape === "square") {
    if (isMobile.value) {
      showMobileNotice.value = true;
    }
  } else {
    if (isMobile.value) {
      showMobileRectangleNotice.value = true;
    }
  }
};

// 打開檔案總管
const openFileDialog = (shape) => {
  if (shape === "square") {
    if (fileInput.value) {
      fileInput.value.pickFiles();
    }
  } else {
    if (rectangleFileInput.value) {
      rectangleFileInput.value.pickFiles();
    }
  }
};

// 初始化
const initImage = (shape) => {
  if (shape === "square") {
    formData.picName = form.picName;
    formData.logoSquare = form.logoSquare;
  } else {
    formData.rectanglePicName = form.rectanglePicName;
    formData.logoRectangle = form.logoRectangle;
  }
};

// [EVENT]：Logo 圖片讀取
const onFilesAdded = (file) => {
  const shape = "square";
  if (!file) {
    formData.logoSquare = form.logoSquare;
    return;
  }

  const fileToProcess = Array.isArray(file) ? file[0] : file;

  if (!fileToProcess) {
    console.log("No file provided");
    return;
  }

  // 圖片檔案白名單
  const validTypes = ["image/jpeg", "image/png"];
  // [檢核]：是否符合白名單
  if (!validTypes.includes(fileToProcess.type)) {
    $n.error(t("orgLogo.valid.imageArchive"));
    initImage(shape);
    return;
  }

  // [檢核]：圖片大小
  const maxSizeInKB = 100; // 最大大小100KB
  const fileSizeInKB = Math.floor(fileToProcess.size / 1024); // 捨棄小數
  const errorMessages = [];
  const img = new Image();

  if (fileSizeInKB > maxSizeInKB) {
    errorMessages.push(t("orgLogo.valid.lessThan"));
  }

  if (errorMessages.length > 0) {
    $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
    initImage(shape);
    return;
  }

  img.onload = () => {
    const { width, height } = img;
    const aspectRatio = width / height;
    const minAspectRatio = 0.99;
    const maxAspectRatio = 1.01;

    if (width != 48 && height != 48) {
      errorMessages.push(t("orgLogo.valid.squareSize"));
    }

    if (aspectRatio < minAspectRatio || aspectRatio > maxAspectRatio) {
      errorMessages.push(t("orgLogo.valid.ratio"));
    }

    if (errorMessages.length > 0) {
      $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
      initImage(shape);
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      if (e.target?.result) {
        formData.logoSquare = e.target.result;
      }
    };
    reader.onerror = (error) => {
      console.error("Error reading file:", error);
      $n.error(t("orgLogo.error.reader"));
    };
    reader.readAsDataURL(fileToProcess);
  };

  const reader = new FileReader();
  reader.onload = (e) => {
    img.src = e.target.result;
  };
  reader.readAsDataURL(fileToProcess);
};

// [EVENT]：長方形 Logo 圖片讀取
const onRectangleFilesAdded = (file) => {
  const shape = "rectangle";
  if (!file) {
    formData.logoRectangle = form.logoRectangle;
    return;
  }

  const fileToProcess = Array.isArray(file) ? file[0] : file;

  if (!fileToProcess) {
    console.log("No file provided");
    return;
  }

  // 圖片檔案白名單
  const validTypes = ["image/jpeg", "image/png"];
  // [檢核]：是否符合白名單
  if (!validTypes.includes(fileToProcess.type)) {
    $n.error(t("orgLogo.valid.imageArchive"));
    initImage(shape);
    return;
  }

  // [檢核]：圖片大小
  // 最大100KB
  const maxSizeInKB = 100;
  const fileSizeInKB = Math.floor(fileToProcess.size / 1024); // 捨棄小數
  const errorMessages = [];
  const img = new Image();

  if (fileSizeInKB > maxSizeInKB) {
    errorMessages.push(t("orgLogo.valid.lessThan"));
  }

  if (errorMessages.length > 0) {
    $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
    initImage(shape);
    return;
  }

  img.onload = () => {
    const { width, height } = img;

    if (width != 212 && height != 42) {
      errorMessages.push(t("orgLogo.valid.rectangleSize"));
    }

    if (errorMessages.length > 0) {
      $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
      initImage(shape);
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      if (e.target?.result) {
        formData.logoRectangle = e.target.result;
      }
    };
    reader.onerror = (error) => {
      console.error("Error reading file:", error);
      $n.error(t("orgLogo.error.reader"));
    };
    reader.readAsDataURL(fileToProcess);
  };

  const reader = new FileReader();
  reader.onload = (e) => {
    img.src = e.target.result;
  };
  reader.readAsDataURL(fileToProcess);
};

// [EVENT]：清除圖片
const clearImage = (shape) => {
  initImage(shape);
};

// [EVENT]：還原預設
const reductionDefaultImage = (shape) => {
  initImage(shape);
};

// [EVENT]：確認上傳
const onOKClick = async () => {
  // 若沒有則帶預設
  if (!formData.logoSquare) {
    formData.logoSquare = sampleLogoBase64.value;
  }

  // 若沒有則帶預設
  if (!formData.logoRectangle) {
    formData.logoRectangle = rectangleSampleLogo.value;
  }

  // 檢核
  if (formData.orgId === "default") {
    return $n.error(t("orgLogo.error.defaultOrg"));
  }
  const payload = {
    logoSquare: formData.logoSquare,
    logoRectangle: formData.logoRectangle
  };

  // API
  uploadOrgLogo(payload, callBackSuccess);
};

// [CALLBACK]
const callBackSuccess = () => {
  // 初始化 picName
  formData.picName = form.picName;
  formData.rectanglePicName = form.rectanglePicName;
};

// 將圖片轉為 base64
const getBase64FromUrl = (url) => {
  return new Promise((resolve, reject) => {
    const img = new Image();

    img.onload = () => {
      try {
        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        canvas.width = img.naturalWidth || img.width;
        canvas.height = img.naturalHeight || img.height;

        ctx.drawImage(img, 0, 0);

        // 轉為 base64，保持原始格式
        const base64 = canvas.toDataURL("image/png");
        resolve(base64);
      } catch (error) {
        reject(
          new Error(
            `${t("orgLogo.error.canvasProcessingFailed")}：${error.message}`
          )
        );
      }
    };

    img.onerror = () => {
      reject(new Error(t("orgLogo.error.imageLoadFailed")));
    };

    // 載入圖片
    img.src = url;
  });
};

const isDidOrg = computed(() => {
  if (!verifierDID.value) return false;
  return loginUserTaxId.value === formData.orgId;
});

onMounted(async () => {
  sampleLogoBase64.value = await getBase64FromUrl(sampleLogo);

  // 取得使用者組織編號
  const b64Payload = jwtToken
    .split(".")[1]
    .replace(/-/g, "+")
    .replace(/_/g, "/");
  const payloadData = JSON.parse(atob(b64Payload));
  const jwtUserObj = JSON.parse(payloadData.jwtuser);
  loginUserTaxId.value = jwtUserObj.orgId;

  // 組織代號
  formData.orgId = loginUserTaxId.value;

  // 查詢該組織資訊＆是否已註冊 DID
  await searchOrg();
  if (verifierDID.value) {
    formData.orgId = orgInfo.value.orgId;
    formData.orgCname = orgInfo.value.orgTwName;
    formData.orgEname = orgInfo.value.orgEnName;
    formData.logoSquare = orgInfo.value.logoSquare;
    formData.logoRectangle = orgInfo.value.logoRectangle;

    await getDefaultLogo(formData.orgCname);
  }
});
</script>
<style scoped>
.notice {
  background-color: grey;
  padding: 5px;
}

.cus-title-text {
  font-size: large;
  font-weight: 600;
}

.card-block {
  width: 90%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border-radius: 15px;
}

.shape-title-text {
  font-size: large;
  width: 90%;
}

.rectangle-card-block {
  height: 82px;
  width: 252px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-container {
  height: 64px;
  width: 64px;
}

.logo-image {
  max-width: 48px;
  max-height: 48px;
  display: block;
}

.rectangle-logo-container {
  width: 212px;
  height: 42px;
}

.rectangle-logo-image {
  width: 212px;
  height: 42px;
  object-fit: contain;
}

.card-title-text {
  font-weight: 700;
  font-size: medium;
}

.orgLogo-table td,
.orgLogo-table th {
  font-size: medium;
  font-weight: 500;
}
</style>
