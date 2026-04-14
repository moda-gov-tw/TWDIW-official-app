<template>
  <q-btn-dropdown
    color="primary"
    fab-mini
    unelevated
    dropdown-icon="person"
    no-icon-animation
    @before-show="getUserInfo"
  >
    <q-list>
      <div style="width: 20rem">
        <div class="q-pa-md text-center q-gutter-sm">
          <q-avatar>
            <q-icon color="bg-grey-4" name="person" size="xl" />
          </q-avatar>
          <p class="">{{ userDetails.userId }} ({{ userDetails.userName }})</p>
        </div>
        <q-separator />
        <q-item
          clickable
          v-close-popup
          tabindex="0"
          @click="openAccountEditDialog"
        >
          <q-item-section>
            <q-item-label>
              <q-icon size="xs" class="q-mr-xs" name="person" />{{
                t("accountSettings")
              }}
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-separator />
        <q-item clickable v-close-popup tabindex="0" @click="openPwdEditDialog">
          <q-item-section>
            <q-item-label>
              <q-icon size="xs" class="q-mr-xs" name="edit" />{{ t("bwdEdit") }}
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-separator />
        <q-item
          clickable
          v-close-popup
          tabindex="0"
          @click="openResetAccessTokenDialog"
        >
          <q-item-section>
            <q-item-label>
              <q-icon size="xs" class="q-mr-xs" name="key" />{{
                t("resetAccessToken")
              }}
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-separator />
        <div class="q-pa-sm">
          <q-btn
            color="primary"
            :ripple="false"
            class="full-width"
            @click="onLogout"
          >
            <q-icon name="logout" />
            <span>
              {{ t("logout") }}
            </span>
          </q-btn>
        </div>
      </div>
    </q-list>
  </q-btn-dropdown>
  <!-- 個人資料設定 -->
  <q-dialog v-model="isOpenAccountEdit" persistent>
    <q-card style="width: 750px; min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">{{ t("personEdit") }}</div>
        <q-space></q-space>
        <q-btn flat fab-mini icon="close" v-close-popup></q-btn>
      </q-card-section>
      <FormAccount
        v-model="accountEditModel"
        :fields="accountFields"
        :layout="[1, 2, 2, 2, 2]"
        :submitLabel="t('confirm')"
        :show-visible="true"
        :is-account-visible="isVisible"
        @update:is-account-visible="(val) => (isVisible = val)"
        @confirm="editUserDataOnConfirm"
        @reset="onResetUserDetails"
      >
        <!-- 帳號 -->
        <template #field-userId="{ field }">
          <q-input
            dense
            :label="field.label"
            v-model="displayAccount"
            autocomplete="off"
            :outlined="field.outlined"
            :disable="field.disable"
          />
        </template>
        <!-- 手機號碼 -->
        <template #field-tel="{ field }">
          <q-input
            dense
            :label="field.label"
            :model-value="displayTel"
            @update:model-value="onTelInput"
            :name="field.name"
            autocomplete="off"
            :placeholder="field.hint"
            :outlined="field.outlined"
            :disable="field.disable"
            :rules="field.rules"
          />
        </template>
      </FormAccount>
    </q-card>
  </q-dialog>
  <!-- 修改密碼 -->
  <q-dialog v-model="isOpenPwdEdit" persistent>
    <q-card style="width: 750px; min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">{{ t("bwdEdit") }}</div>
        <q-space></q-space>
        <q-btn flat fab-mini icon="close" v-close-popup></q-btn>
      </q-card-section>
      <form-pwd
        v-model="pwdEditModel"
        :fields="pwdFields"
        :submitLabel="t('confirm')"
        @confirm="changeUserBwdOnConfirm"
        @reset="onReset"
      >
        <template #field-currentBwd="{ field }">
          <q-input
            :label="field.label"
            v-model="pwdEditModel.currentBwd"
            :type="showPasswordOld ? 'text' : 'password'"
            autocomplete="off"
            :rules="[(val) => !!val || t('account.bwd.notBlank')]"
          >
            <template #append>
              <!-- 顯示或隱藏密碼的按鈕 -->
              <q-btn
                flat
                dense
                :icon="showPasswordOld ? 'visibility' : 'visibility_off'"
                @click="showPasswordOld = !showPasswordOld"
                :aria-label="
                  showPasswordOld
                    ? t('account.bwd.hide.true')
                    : t('account.bwd.hide.false')
                "
              />

              <!-- 清除密碼欄位的按鈕 -->
              <q-btn
                flat
                dense
                icon="clear"
                @click="pwdEditModel.currentBwd = ''"
              />
            </template>
          </q-input>
        </template>

        <template #field-newBwd="{ field }">
          <q-input
            :label="field.label"
            v-model="pwdEditModel.newBwd"
            :type="showPasswordNew ? 'text' : 'password'"
            autocomplete="off"
            :rules="[
              (val) => !!val || t('account.bwd.notBlank'),
              (val) =>
                val.length >= 12 ||
                t('account.bwd.valid.include', { symbols: '(!@#)' }),
              (val) =>
                val.length <= 99 || t('account.bwd.valid.notGreaterThan'),
              pwdRule
            ]"
            :hint="t('account.bwd.valid.include', { symbols: '(!@#)' })"
          >
            <template #append>
              <!-- 顯示或隱藏密碼的按鈕 -->
              <q-btn
                flat
                dense
                :icon="showPasswordNew ? 'visibility' : 'visibility_off'"
                @click="showPasswordNew = !showPasswordNew"
                :aria-label="
                  showPasswordNew
                    ? t('account.bwd.hide.true')
                    : t('account.bwd.hide.false')
                "
              />

              <!-- 清除密碼欄位的按鈕 -->
              <q-btn
                flat
                dense
                icon="clear"
                @click="pwdEditModel.newBwd = ''"
              />
            </template>
          </q-input>
        </template>

        <template #field-checkBwd="{ field }">
          <q-input
            :label="field.label"
            v-model="pwdEditModel.checkBwd"
            :type="showPasswordCheck ? 'text' : 'password'"
            autocomplete="off"
            :rules="[
              (val) => !!val || t('account.bwd.notBlank'),
              (val) =>
                val === pwdEditModel.newBwd ||
                t('account.bwd.valid.inconsistency')
            ]"
            ref="checkBwdInput"
          >
            <template #append>
              <!-- 顯示或隱藏密碼的按鈕 -->
              <q-btn
                flat
                dense
                :icon="showPasswordCheck ? 'visibility' : 'visibility_off'"
                @click="showPasswordCheck = !showPasswordCheck"
                :aria-label="
                  showPasswordCheck
                    ? t('account.bwd.hide.true')
                    : t('account.bwd.hide.false')
                "
              />

              <!-- 清除密碼欄位的按鈕 -->
              <q-btn
                flat
                dense
                icon="clear"
                @click="pwdEditModel.checkBwd = ''"
              />
            </template>
          </q-input>
        </template>
      </form-pwd>
    </q-card>
  </q-dialog>
  <!-- 換發 Access Token -->
  <q-dialog v-model="isOpenResetAccessToken" persistent>
    <q-card style="width: 750px; min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-grey-4 text-white items-center">
        <div class="text-h5 text-weight-bold">
          {{ t("resetAccessToken") }}
        </div>
        <q-space></q-space>
        <q-btn flat fab-mini icon="close" v-close-popup></q-btn>
      </q-card-section>
      <q-card-section>
        <div class="q-pa-md">
          {{ t("accessToken.refresh.message") }}
        </div>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          :label="t('cancel')"
          color="grey"
          v-close-popup
          :disable="accessTokenLoading"
        />
        <q-btn
          :label="t('confirm')"
          color="primary"
          @click="resetAccessTokenOnConfirm"
          :loading="accessTokenLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from "vue";
import { api } from "src/boot/axios";
import { useNotify, useDialog } from "src/utils/plugin";
import FormAccount from "components/FormWrapper.vue";
import FormPwd from "components/FormWrapper.vue";
import { getToken } from "src/boot/auth";
import { useI18n } from "vue-i18n";
import { RSAencrypt } from "src/utils/encrypt";
import { storeToRefs } from "pinia";
import { useUserConfigStore } from "src/stores/userConfig";
import { mask } from "src/utils/mask";

// store
const userConfig = useUserConfigStore();
const { userDetails } = storeToRefs(userConfig);
const { onLogout, getUser, updateCurrentUser } = userConfig;
const jwtToken = getToken();
const showPasswordOld = ref(false);
const showPasswordNew = ref(false);
const showPasswordCheck = ref(false);
const isVisible = ref(false);

const { t } = useI18n();
const checkBwdInput = ref(null);

//plugin settings
const $n = useNotify();
const $d = useDialog();
const $m = mask();

const valid_currentBwd = [(val) => !!val || t("required")];
const valid_newBwd = [
  (val) => !!val || t("required"),
  (val) =>
    (val || "").length >= 12 ||
    t("account.bwd.valid.include", { symbols: "(!@#)" }),
  (val) => (val || "").length <= 99 || t("account.bwd.valid.notGreaterThan")
];

const valid_checkBwd = [
  (val) => !!val || t("required"),
  (val) => val === pwdEditModel.newBwd || t("account.bwd.valid.inconsistency")
];

// ============ 個人資料設定 =============

// 個人資料設定 dialog控制項
const isOpenAccountEdit = ref(false);

// 個人資料設定 讀取資料 & 打開dialog
const getUserInfo = async () => {
  await getUser();
  // 合併資料內容
  Object.assign(accountEditModel, userDetails.value);
};

// 個人資料設定 讀取資料 & 打開dialog
const openAccountEditDialog = async () => {
  isOpenAccountEdit.value = true;
};

// 計算字元數的自定義規則
const customMaxLengthRule = (maxLength) => {
  return (val) => {
    const totalLength = val.length;
    return (
      totalLength <= maxLength || t("validation.maxLength", { max: maxLength })
    );
  };
};

const pwdRule = (pwd) => {
  return (
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*(),.?":{}|<>]).*$/.test(pwd) ||
    t("account.bwd.valid.include", { symbols: "(!@#)" })
  );
};

//個人資料設定欄位
const accountFields = ref([
  {
    name: "userId",
    label: t("account.table.userId"),
    inputType: "input",
    field: "userId",
    disable: true,
    outlined: true
  },
  {
    name: "orgId",
    label: t("account.table.orgId"),
    inputType: "input",
    field: "orgId",
    disable: true,
    outlined: true
  },
  {
    name: "userTypeId",
    label: t("account.table.userTypeId"),
    inputType: "input",
    field: "applyUserTypeName",
    disable: true,
    outlined: true
  },
  {
    name: "name",
    label: "*" + t("account.table.name"),
    inputType: "input",
    field: "userName",
    hint: t("input", { input: t("account.table.name") }),
    rules: [
      (val) => !!val || t("notBlank", { input: t("account.table.name") }),
      ,
      customMaxLengthRule(50)
    ],
    outlined: true
  },
  {
    name: "tel",
    label: "*" + t("account.table.tel"),
    inputType: "input",
    field: "tel",
    hint: t("input", { input: t("account.table.tel") }),
    outlined: true,
    rules: [
      (val) => !!val || t("notBlank", { input: t("account.table.tel") }),
      customMaxLengthRule(100),
      (val) =>
        !/[\u4e00-\u9fa5]/.test(val) || t("account.valid.notEnterChinese"),
      () => /^[0-9]*$/.test(accountEditModel.tel) || t("account.valid.onlyCan")
    ]
  }
]);

const accountEditModel = reactive({
  isEnable: false,
  isColor: true
});

// 帳號呈現在畫面上的樣子
const displayAccount = computed(() => {
  return isVisible.value
    ? accountEditModel.userId
    : $m.maskEmail(accountEditModel.userId); // 電子郵件格式化 x******x@gmail.com
});

// 手機號碼呈現在畫面上的樣子
const displayTel = computed(() => {
  return isVisible.value ? accountEditModel.tel : forMask(accountEditModel.tel);
});

// 只要手機號碼欄位有變動就更新
const onTelInput = (val) => {
  if (isVisible.value) {
    // 眼睛開：直接更新
    accountEditModel.tel = val;
  } else {
    // 眼睛關：使用遮罩邏輯
    accountEditModel.tel = $m.applyMaskEdit(accountEditModel.tel, val);
  }
};

// 手機號碼格式化 0900***123
const forMask = (data) => {
  return $m.maskPhone(data);
};

// 更新個人資料
const editUserDataOnConfirm = async (model) => {
  // 更新個人資料
  const result = await updateCurrentUser(model.value);
  if (result) {
    isOpenAccountEdit.value = false;
  }
};

// 重置個人資料設定
const onResetUserDetails = (model) => {
  getUserInfo();
};

// ============ 修改密碼 =============

const isOpenPwdEdit = ref(false);

const openPwdEditDialog = () => {
  Object.keys(pwdEditModel).forEach((key) => {
    pwdEditModel[key] = "";
  });
  isOpenPwdEdit.value = true;
};

const pwdEditModel = reactive({
  currentBwd: "",
  newBwd: "",
  checkBwd: ""
});
const pwdFields = ref([
  {
    name: "currentBwd",
    label: t("account.bwd.nowBwd"),
    inputType: "password",
    field: "currentBwd",
    color: "primary",
    hint: t("account.bwd.enterNowBwd"),
    rules: valid_currentBwd
  },
  {
    name: "newBwd",
    label: t("account.bwd.newBwd"),
    inputType: "password",
    field: "newBwd",
    hint: t("account.bwd.enterNewBwd"),
    rules: valid_newBwd
  },
  {
    name: "checkBwd",
    label: t("account.bwd.confirmBwd"),
    inputType: "password",
    field: "checkBwd",
    hint: t("account.bwd.reCheck"),
    rules: valid_checkBwd
  }
]);

// 更換密碼
const changeUserBwdOnConfirm = async (model) => {
  const { checkBwd, ...data } = { ...pwdEditModel };
  data.currentBwd = await RSAencrypt(pwdEditModel.currentBwd);
  data.newBwd = await RSAencrypt(pwdEditModel.newBwd);

  api
    .post(
      "/api/modadw302w/change-bwd/finishWithAuthorization",
      {
        ...data
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      }
    )
    .then((response) => {
      if (response) {
        $n.success(t("account.success.editBwd"));
        isOpenPwdEdit.value = false;
      }
    })
    .catch((error) => {
      // 檢查錯誤物件的結構
      if (error.response && error.response.data) {
        const detail = error.response.data.detail; // 確保獲取 detail

        if (detail) {
          // 檢查具體的錯誤信息
          if (detail === "密碼輸入不正確") {
            $d.alert(t("pleaseConfirm"), t("account.errro.wrongBwd"));
          } else if (detail.includes("密碼不可重複")) {
            const firstChar = detail.charAt(0);
            $d.alert(
              t("pleaseConfirm"),
              t("account.error.repeatPrefix", { count: firstChar })
            );
          } else {
            $d.alert(t("pleaseConfirm"), detail);
          }
        } else {
          $d.alert(t("account.errro.title"), t("account.errro.unknown"));
        }
      } else {
        // 如果沒有 response，則顯示連接錯誤
        $d.alert(t("account.errro.title"), t("account.errro.noResponse"));
      }
    });
};

// 換發 Access Token
const isOpenResetAccessToken = ref(false);
const accessTokenLoading = ref(false);
const openResetAccessTokenDialog = () => {
  isOpenResetAccessToken.value = true;
};
const resetAccessTokenOnConfirm = async () => {
  accessTokenLoading.value = true;
  api
    .post(
      "/api/modadw311w/resetAccessToken",
      {},
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      }
    )
    .then((res) => {
      if (!res.data) {
        $n.error(t("account.errro.emptyResponse"));
        return false;
      }

      if (!res.data.code) {
        $n.error(t("account.errro.noCode"));
        return false;
      }

      if (res.data.code !== "0") {
        $n.error(res.data.msg || t("account.errro.backendError"));
        return false;
      }

      if (res) {
        $n.success(t("accessToken.refresh.success"));
        isOpenResetAccessToken.value = false;
      }
    })
    .catch((error) => {
      // 檢查錯誤物件的結構
      if (error.response) {
        $d.alert(t("account.errro.title"), t("account.errro.noResponse"));
      }
    })
    .finally(() => {
      accessTokenLoading.value = false;
    });
};

const onReset = () => {
  Object.keys(pwdEditModel).forEach((key) => {
    pwdEditModel[key] = null;
  });
};

watch(
  () => pwdEditModel.newBwd,
  (newVal) => {
    if (checkBwdInput.value && pwdEditModel.checkBwd) {
      checkBwdInput.value.validate();
    }
  }
);

onMounted(async () => {
  await getUser();
  Object.assign(accountEditModel, userDetails.value);
});
</script>
