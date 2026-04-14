<template>
  <basic-page>
    <div class="row q-ma-none q-py-sm q-pl-md items-center">
      <!-- 註冊 DID -->
      <p class="titleRwd q-mb-md">
        {{ t(`didregister.title.${isDIDRegistered}`) }}
      </p>
      <q-space></q-space>
      <div class="q-px-sm"></div>
    </div>

    <div v-if="!created">
      <!--選擇模式-->
      <div v-if="!isDIDRegistered" class="items-center">
        <q-card class="q-mb-lg">
          <div
            :class="['col', $q.screen.lt.lg ? 'column' : 'row justify-start']"
          >
            <div class="q-pa-md">
              <q-card-section>
                <h5 class="TextGradient">
                  {{ t("didregister.notice.title") }}
                </h5>
              </q-card-section>

              <q-card-section class="q-pt-none">
                <ul class="blue-ls">
                  <li class="meali">
                    {{ t("didregister.noticeList.didOnce") }}
                  </li>
                  <li class="meali">
                    {{ t("didregister.noticeList.checkMoDev") }}
                  </li>
                  <li class="meali">
                    {{ t("didregister.noticeList.govCert") }}
                  </li>
                  <li class="meali">
                    {{ t("didregister.noticeList.pleaseFor") }}
                    <a
                      target="_blank"
                      href="http://moica.nat.gov.tw/download_1.html"
                      :title="t('didregister.noticeList.title')"
                    >
                      {{ t("didregister.noticeList.this") }}
                    </a>
                    {{ t("didregister.noticeList.downloadHicos") }}
                  </li>
                </ul>
              </q-card-section>
            </div>
            <div
              :class="[
                'col-3 row items-center',
                $q.screen.lt.lg ? 'justify-center' : 'justify-start'
              ]"
            >
              <div class="q-pa-md">
                <div class="row justify-center items-center">
                  <div class="relative-position" style="display: inline-block">
                    <q-table
                      class="health-check-table text-h6 q-my-md"
                      flat
                      bordered
                      :rows="healthCheckRows"
                      :columns="healthCheckColumns"
                      row-key="name"
                      hide-pagination
                      separator="cell"
                    >
                      <template v-slot:body-cell-result="props">
                        <q-td :props="props">
                          <q-icon
                            :name="props.row.result ? 'check_circle' : 'cancel'"
                            :color="props.row.result ? 'indigo-6' : 'primary'"
                            size="30px"
                          />
                        </q-td>
                      </template>
                    </q-table>
                    <div
                      v-if="healthCheckLoading"
                      class="absolute flex flex-center"
                      style="
                        top: 50%;
                        left: 100%;
                        transform: translateY(-50%);
                        margin-left: 12px;
                        white-space: nowrap;
                      "
                    >
                      <q-spinner color="primary" size="2em" class="q-mr-sm" />
                      <span class="text-primary">{{
                        t("didregister.testing") + "..."
                      }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </q-card>
        <div class="row" v-if="isEnvTestPass">
          <!-- 請選擇使用的憑證 -->
          <span class="text-h6 q-mr-md">{{
            t("choose", { input: t("didregister.credentialsUsed") })
          }}</span>
          <q-select
            class="col-12 col-sm-auto"
            style="min-width: 300px"
            emit-value
            map-options
            outlined
            :disable="isCert"
            :options="[
              { label: t('chooseOnly'), value: '' },
              // { label: '使用自然人憑證註冊 (MOICA)', value: 'MOICA'},
              { label: t('didregister.select.MOEACA'), value: 'MOEACA' },
              { label: t('didregister.select.XCA'), value: 'XCA' },
              { label: t('didregister.select.GCA'), value: 'GCA' }
            ]"
            v-model="usingMode"
          />
          <!-- 偵測憑證 -->
          <q-btn
            color="primary"
            class="q-ml-lg"
            @click="onCertHandler"
            :loading="certLoading"
            >{{ t("didregister.detection") }}</q-btn
          >
        </div>
      </div>

      <!--輸入驗證憑證資訊-->
      <div v-if="usingMode === cardType && cardType && !isDIDRegistered">
        <q-separator class="q-my-lg" />
        <label class="text-h6">{{
          t("didregister.input.info", { input: getCardTypeName(usingMode) })
        }}</label>
        <q-table
          :key="tableKey"
          dense
          flat
          class="sticky-header"
          :rows="filteredCertRows"
          :columns="columns"
          row-key="id"
          :pagination="{ rowsPerPage: 0 }"
          hide-bottom
          :loading="certLoading"
          :hide-header="certLoading"
        >
          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <span>
                <span class="text-red">*</span
                >{{ getLabelName(props.row.name) }}
              </span>
            </q-td>
          </template>
          <template v-slot:body-cell-value="props">
            <q-td :props="props">
              <div>
                <q-input
                  outlined
                  class="col"
                  v-model="certInputData[props.row.name]"
                  hide-bottom-space
                  :placeholder="getPlaceHolder(props.row.name)"
                  dense
                  input-style="text-align: center"
                  :readonly="getCertReadOnly(props.row.name)"
                  :type="isPwd[props.row.name] ? 'password' : 'text'"
                  :rules="certRules[props.row.name]"
                  :style="{
                    height: '72px',
                    paddingTop: '10px'
                  }"
                >
                  <template #append>
                    <q-btn
                      flat
                      dense
                      :icon="
                        isPwd[props.row.name] ? 'visibility' : 'visibility_off'
                      "
                      @click="isPwd[props.row.name] = !isPwd[props.row.name]"
                    />
                  </template>
                </q-input>
              </div>
            </q-td>
          </template>
        </q-table>

        <div style="display: none">
          <q-select
            emit-value
            map-options
            outlined
            :options="cardOptions"
            v-model="icOption"
            ref="icOptionRef"
            :rules="[(val) => (!!val && val == '0') || t('required')]"
          />
        </div>

        <div class="text-right" v-if="!isCert">
          <q-btn
            color="primary"
            type="submit"
            @click="onSubmit"
            :loading="certLoading"
            :disable="usingMode !== cardType"
            >{{ t("nextStep") }}</q-btn
          >
        </div>

        <q-separator class="q-my-lg" />
      </div>

      <!--輸入IVPAS DID驗證資訊-->
      <div v-if="isCert">
        <label class="text-h6">{{
          t("didregister.input.info", { input: t("didregister.didInspection") })
        }}</label>
        <q-table
          :key="tableKey"
          dense
          flat
          class="sticky-header"
          :rows="didVerifyRows"
          :columns="columns"
          row-key="id"
          :pagination="{ rowsPerPage: 0 }"
          hide-bottom
          :loading="didVerifyLoading"
          :hide-header="didVerifyLoading"
        >
          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <span>
                <span class="text-red">*</span
                >{{ getLabelName(props.row.name) }}
              </span>
            </q-td>
          </template>
          <template v-slot:body-cell-value="props">
            <q-td :props="props">
              <div>
                <q-input
                  outlined
                  class="col"
                  v-model="didVerifyInputData[props.row.name]"
                  hide-bottom-space
                  :placeholder="getPlaceHolder(props.row.name)"
                  dense
                  input-style="text-align: center"
                  :readonly="getDidVerifyReadOnly(props.row.name)"
                  :rules="didVerifyRules[props.row.name]"
                  :style="{
                    height: '72px',
                    paddingTop: '10px'
                  }"
                >
                </q-input>
              </div>
            </q-td>
          </template>
        </q-table>

        <div class="text-right" v-if="!isDIDVerified">
          <q-btn
            color="primary"
            type="submit"
            @click="didVerifyHandler"
            :loading="didVerifyLoading"
            >{{ t("nextStep") }}</q-btn
          >
        </div>

        <q-separator class="q-my-lg" />
      </div>

      <!--輸入DID註冊資訊-->
      <div v-if="isDIDRegistered || isDIDVerified">
        <div v-if="isDIDRegistered" class="text-h6 q-pl-md">
          <span class="q-mr-md block">{{
            t("didregister.useCredentials") + "：" + getCardTypeName(cardType)
          }}</span>
          <span
            class="q-mr-md block"
            :style="{ color: DIDStatus === '有效' ? '#4E90AD' : '#D33E5F' }"
            >{{ t("didregister.didState") + "：" + DIDStatus }}</span
          >
          <span class="q-mr-md block">{{
            t("didregister.createTime") + "：" + registerTime
          }}</span>
        </div>
        <div v-else>
          <label class="text-h6">{{
            t("didregister.input.info", {
              input: " " + t("didregister.didRelated")
            })
          }}</label>
        </div>

        <q-table
          :key="tableKey"
          dense
          flat
          class="sticky-header"
          :rows="filteredRows"
          :columns="columns"
          row-key="id"
          :pagination="{ rowsPerPage: 0 }"
          hide-bottom
          :loading="registerLoading"
          :hide-header="registerLoading"
        >
          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <span>
                <span class="text-red">*</span
                >{{ getLabelName(props.row.name) }}
              </span>
            </q-td>
          </template>
          <template v-slot:body-cell-value="props">
            <q-td :props="props">
              <div>
                <q-input
                  outlined
                  class="col"
                  v-model="inputData[props.row.name]"
                  hide-bottom-space
                  :placeholder="getPlaceHolder(props.row.name)"
                  dense
                  :rules="rules[props.row.name]"
                  :readonly="getReadOnly(props.row.name)"
                  input-style="text-align: center"
                  :style="{
                    height: '72px',
                    paddingTop: '10px'
                  }"
                  :ref="(el) => (fieldRefs[props.row.name] = el)"
                >
                </q-input>
              </div>
            </q-td>
          </template>
          <template v-slot:loading>
            <q-inner-loading showing color="primary" />
          </template>
        </q-table>
        <div v-if="!isDIDRegistered" class="text-right">
          <q-btn
            color="primary"
            @click="issueHandler"
            :loading="registerLoading"
          >
            <q-icon name="arrow_right" size="md" class="" />
            {{ t("didregister.createDidIdentity") }}
          </q-btn>
        </div>
      </div>
    </div>
    <div v-else class="text-center">
      <span class="text-h6 block">{{
        t("didregister.noticeList.success")
      }}</span>
      <span class="text-h6 block">{{
        t("didregister.noticeList.logout")
      }}</span>
      <q-btn outline color="primary" class="q-mt-lg" @click="handleLogout">{{
        t("didregister.noticeList.BackToLogin")
      }}</q-btn>
    </div>
  </basic-page>
</template>

<script setup>
import { ref, onMounted, reactive, watch, computed } from "vue";
import { api } from "src/boot/axios";
import { useDialog, useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
import { getToken } from "boot/auth";
import { format } from "src/utils/dateFormat";
import { useAlertStore } from "src/stores/alertStore";
import { getUserCert } from "assets/js/ChtICToken";
import {
  btnClickHandler,
  btnReloadIcCardClickHandler,
  signHandler
} from "assets/js";
import { storeToRefs } from "pinia";
import { HttpStatusCode } from "axios";
import { useUserConfigStore } from "stores/userConfig";

const alertStore = useAlertStore();
const { loadingShow, loadingHide, showError, showSuccess } = alertStore;
const { alert, alertText, alertSuccess, cardType, certLoading, usingMode } =
  storeToRefs(alertStore);
const userConfig = useUserConfigStore();
const { onLogout } = userConfig;
const { t } = useI18n();
const $n = useNotify();
const $d = useDialog();
const jwtToken = getToken();
const loginUserTaxId = ref("");
const didVerifyLoading = ref(false);
const registerLoading = ref(false);
const healthCheckLoading = ref(false);
const baseUrl = ref("");
const tableKey = ref(0);
const created = ref(false);
const isPwd = ref({});
const issuerOrg = ref({});
isPwd.value = { PID: true, orgID: true, PIN: true };
const isCert = ref(false);
const isDIDVerified = ref(false);
const cardOptions = ref([
  { label: t("choose", { input: t("didregister.card") }), value: "" }
]);
const icOption = ref(null);
const icOptionRef = ref(null);
const isEnvTestPass = ref(false);
const certRows = ref([
  { name: "PID", value: "", mode: ["MOICA"] },
  { name: "orgID", value: "", mode: ["MOEACA"] },
  { name: "PIN", value: "", mode: ["MOICA", "MOEACA", "XCA", "GCA"] }
]);
const didVerifyRows = ref([
  { name: "serviceBaseURL", value: "" },
  { name: "token", value: "" }
]);
const rows = ref([
  { name: "taxId", value: "" },
  { name: "nameTw", value: "" },
  { name: "nameEn", value: "" },
  { name: "info", value: "" },
  { name: "serviceBaseURL", value: "" },
  { name: "token", value: "" }
]);
const certInputData = ref({});
const didVerifyInputData = ref({});
const inputData = reactive({});
const isDIDRegistered = ref(true);
const registerTime = ref("");
const { yyyyMMddHHmmss } = format();
const DIDStatus = ref("");

const healthCheckColumns = [
  {
    name: "name",
    required: true,
    label: t("didregister.testItems"),
    align: "left",
    field: (row) => row.name,
    format: (val) => `${val}`
  },
  {
    name: "result",
    align: "center",
    label: t("didregister.result"),
    field: "result"
  }
];

const healthCheckRows = ref([
  {
    name: t("didregister.frontendService"),
    result: false
  },
  {
    name: t("didregister.ivpasService"),
    result: false
  }
]);

const columns = [
  {
    name: "name",
    label: t("didregister.fieldName"),
    align: "center",
    field: "name",
    style: "width: 50%; white-space: normal; word-break: break-word"
  },
  {
    name: "value",
    label: t("didregister.fieldData"),
    align: "center",
    field: "value",
    style: "width: 50%; white-space: normal; word-break: break-word"
  }
];

const getCardTypeName = (name) => {
  if (!name) return t("didregister.no");
  return t(`didregister.cardTypeName.${name}`, t("didregister.no"));
};

const getLabelName = (name) => {
  const labels = t(`didregister.labelName.${name}`, t("didregister.undefined"));
  return labels;
};

const getPlaceHolder = (name) => {
  let placeHolder = "";
  if (name == "token") {
    placeHolder =
      " " + t(`didregister.labelName.${name}`, t("didregister.data"));
  } else {
    placeHolder = t(`didregister.labelName.${name}`, t("didregister.data"));
  }
  return t("input", { input: placeHolder });
};

const getCertReadOnly = (name) => {
  if (isCert.value === true) {
    return true;
  } else {
    const values = {
      PID: false,
      orgID: false,
      PIN: false
    };
    return values[name] || false;
  }
};

const getDidVerifyReadOnly = (name) => {
  if (isDIDVerified.value === true) {
    return true;
  } else {
    const values = {
      serviceBaseURL: true,
      token: false
    };
    return values[name] || false;
  }
};

const getReadOnly = (name) => {
  if (isDIDRegistered.value === true) {
    return true;
  } else {
    const values = {
      taxId:
        usingMode.value === "MOEACA" ||
        usingMode.value === "XCA" ||
        usingMode.value === "GCA",
      nameTw:
        usingMode.value === "MOEACA" ||
        usingMode.value === "XCA" ||
        usingMode.value === "GCA",
      nameEn: false,
      info: false,
      serviceBaseURL: true,
      token: true
    };
    return values[name] || false;
  }
};

const certRules = ref({
  PID: [
    (val) => !!val || t("input", { input: t("didregister.labelName.PID") }),
    (val) => /^[A-Z][1289]\d{8}$/.test(val) || t("didregister.valid.PID")
  ],
  orgID: [
    (val) => !!val || t("input", { input: t("didregister.labelName.orgID") }),
    (val) => /^\d{8}$/.test(val) || t("didregister.valid.orgID")
  ],
  PIN: [(val) => !!val || t("input", { input: t("didregister.labelName.PIN") })]
});

const didVerifyRules = ref({
  serviceBaseURL: [
    (val) =>
      !!val || t("input", { input: t("didregister.labelName.serviceBaseURL") }),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value,
    (val) => ruleTestURL(val) || errMsgURL.value
  ],
  token: [
    (val) => !!val || t("didregister.valid.token"),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value
  ]
});

const rules = ref({
  nameTw: [
    (val) => !!val || t("input", { input: t("didregister.labelName.nameTw") }),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value
  ],
  nameEn: [
    (val) => !!val || t("input", { input: t("didregister.labelName.nameEn") }),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value,
    (val) => ruleTestName(val) || errMsgName.value
  ],
  info: [
    (val) => !!val || t("input", { input: t("didregister.labelName.info") }),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value,
    (val) => ruleTestURL(val) || errMsgURL.value
  ],
  taxId: [
    (val) => !!val || t("input", { input: t("didregister.labelName.taxId") }),
    (val) => ruleTestTax(val) || errMsgTax.value
  ],
  serviceBaseURL: [
    (val) =>
      !!val || t("input", { input: t("didregister.labelName.serviceBaseURL") }),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value,
    (val) => ruleTestURL(val) || errMsgURL.value
  ],
  token: [
    (val) => !!val || t("didregister.valid.token"),
    (val) => ruleTest1(val) || errMsg1.value,
    (val) => ruleTest2(val) || errMsg2.value,
    (val) => ruleTest3(val) || errMsg3.value
  ]
});

const regex1 = ref();
const regex2 = ref();
const regex3 = ref();
const regexName = ref();
const regexURL = ref();
const regexTax = ref();
const errMsg1 = ref();
const errMsg2 = ref();
const errMsg3 = ref();
const errMsgName = ref();
const errMsgURL = ref();
const errMsgTax = ref();

const ruleTest1 = (val) => {
  return !regex1.value || regex1.value.test(val);
};
const ruleTest2 = (val) => {
  return !regex2.value || !regex2.value.test(val);
};
const ruleTest3 = (val) => {
  return !regex3.value || !regex3.value.test(val);
};
const ruleTestName = (val) => {
  return !regexName.value || regexName.value.test(val);
};
const ruleTestURL = (val) => {
  return !regexURL.value || regexURL.value.test(val);
};
const ruleTestTax = (val) => {
  return !regexTax.value || regexTax.value.test(val);
};

const filteredCertRows = computed(() => {
  return certRows.value.filter((row) => row.mode.includes(usingMode.value));
});

const filteredRows = computed(() => {
  if (isDIDRegistered.value) {
    return rows.value;
  }
  return rows.value.filter(
    (row) =>
      row.name !== "taxId" &&
      row.name !== "serviceBaseURL" &&
      row.name !== "token"
  );
});

// 偵測插卡 (插卡驗證用)
const mountCert = () => {
  // 讀卡機初始化
  btnReloadIcCardClickHandler();

  // listener
  generateListener(document.getElementById("slotDescription"), "click", () => {
    const list = document.getElementById("slotDescription").options;
    const icList = Array.from(list).map((el) => {
      return { label: el.label, value: el.value };
    });
    cardOptions.value = icList;
    loadingShow();
    icOption.value = icList[1]?.value;
    document.getElementById("slotDescription").value = icOption.value;
    if (icOption.value) {
      setTimeout(() => {
        loadingShow();
        getUserCert();
      });
      console.log(
        `${t("didregister.success.reader") + "："}`,
        icList[1]?.label
      );
    } else {
      showError(t("didregister.error.reader"));
    }
  });
};

// 驗證憑證 (插卡驗證用)
const onSubmit = () => {
  if (icOptionRef.value.validate()) {
    if (usingMode.value === "MOEACA" && !certInputData.value.orgID) {
      showError(t("input", { input: t("didregister.labelName.orgID") }));
    } else if (usingMode.value === "MOICA" && !certInputData.value.PID) {
      showError(t("input", { input: t("didregister.labelName.PID") }));
    } else if (!certInputData.value.PIN) {
      showError(t("input", { input: t("didregister.labelName.PIN") }));
    } else {
      loadingShow();
      document.getElementById("slotDescription").value = icOption.value;
      document.getElementById("pid").value = certInputData.value.PID;
      document.getElementById("businessId").value = certInputData.value.orgID;
      btnClickHandler(icOption.value, certInputData.value.PIN, usingMode.value);
    }
  } else {
    showError(t("didregister.error.reader"));
  }
};

// 偵測憑證卡片
const onCertHandler = () => {
  if (usingMode.value !== "") {
    cardType.value = "";
    isCert.value = false;
    isDIDVerified.value = false;
    certInputData.value = {};
    didVerifyInputData.value = {};
    didVerifyInputData.value.serviceBaseURL = baseUrl.value;
    issuerOrg.value = {};
    Object.keys(inputData).forEach((key) => {
      delete inputData[key];
    });
    tableKey.value++;
    initCertElement();
    mountCert();
  } else {
    $n.error(t("didregister.error.selectFirst"));
  }
};

const initCertElement = () => {
  icOption.value = null;
  cardOptions.value = [
    { label: t("choose", { input: t("didregister.card") }), value: "" }
  ];
  clearElement("businessId");
  clearElement("certb64");
  clearElement("endDate");
  clearElement("issuerDN");
  clearElement("pid");
  clearElement("serialNumber");
  clearElement("sn");
  clearElement("startDate");
  clearElement("subjectDN");
  clearElement("subjectID");
  clearElement("type");
  clearElement("userOrg");
  clearElement("orgSignature");
  document.getElementById("userOrg").value = loginUserTaxId.value;
};

// 呼叫IVPAS驗證DID
const didVerifyHandler = async () => {
  if (!didVerifyInputData.value.token) {
    showError(t("input", { input: " access token" }));
  } else {
    didVerifyLoading.value = true;
    try {
      const requestBody = {
        b64Data: document.getElementById("certb64").value,
        baseUrl: baseUrl.value,
        token: didVerifyInputData.value.token
      };

      const response = await api.post(
        "/api/modadw101w/verifyDid",
        requestBody,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`
          }
        }
      );

      if (
        response.status === HttpStatusCode.Ok &&
        response.data &&
        response.data.code === "0"
      ) {
        isDIDVerified.value = true;
        showSuccess(t("didregister.success.validSuccess"));

        if (response.data.data.orgEnName) {
          inputData["nameEn"] = response.data.data.orgEnName;
        } else {
          $n.error(t("didregister.error.noData"));
        }
        didVerifyLoading.value = false;
      } else {
        if (response.data.code) {
          let msg = response.data.msg;
          if (response.data.data) {
            msg +=
              " ( " +
              t("didregister.error.code") +
              "：" +
              response.data.data.detailCode +
              "，" +
              t("didregister.error.message") +
              "：" +
              response.data.data.detailMessage +
              ")";
          }
          showError(msg);
        } else {
          showError(t("didregister.error.unknown"));
        }
        didVerifyLoading.value = false;
      }
    } catch (error) {
      showError(t("didregister.error.unknown"));
      didVerifyLoading.value = false;
    }
  }
};

const fieldRefs = reactive({});
// 註冊DID
const issueHandler = async () => {
  let allValid = true;
  for (const key in fieldRefs) {
    if (key === "taxId" || key === "token" || key === "serviceBaseURL")
      continue;
    const isValid = await fieldRefs[key].validate();
    if (!isValid) {
      allValid = false;
    }
  }
  if (!allValid) {
    registerLoading.value = false;
    $n.error(t("didregister.valid.fix"));
    return;
  } else {
    issuerOrg.value = {
      name: inputData["nameTw"],
      name_en: inputData["nameEn"],
      info: inputData["info"],
      taxId: inputData["taxId"],
      serviceBaseURL: baseUrl.value,
      x509_subject: isCert.value
        ? document.getElementById("subjectDN").value
        : undefined,
      x509_serial: isCert.value
        ? document.getElementById("sn").value
        : undefined,
      x509_type: isCert.value ? usingMode.value : undefined,
      access_token: didVerifyInputData.value.token,
      orgType: 1
    };
    signHandler(
      icOption.value,
      certInputData.value.PIN,
      JSON.stringify(issuerOrg.value)
    );
  }
};

const handleLogout = () => {
  if (window.logoutTimer) {
    clearTimeout(window.logoutTimer);
    window.logoutTimer = null;
  }
  onLogout();
};

// 創建元素 (插卡驗證用)
const generateElement = (type, id) => {
  const element = document.createElement(type);
  element.id = id;
  element.hidden = true;
  document.body.appendChild(element);
  return element;
};

// 建立listener (插卡驗證用)
const generateListener = (el, event, todo) => {
  el.addEventListener(event, () => todo());
};

// 刪除元素 (插卡驗證用)
const clearElement = (id) => {
  const el = document.getElementById(id);
  if (el) {
    el.value = "";
  }
};

// 提示視窗 (插卡驗證用)
watch([alert, alertSuccess], async ([newAlert, newAlertSuccess]) => {
  if (newAlert === true) {
    $d.confirm(t("errorTitle"), alertText.value);
    alert.value = false;
  }
  if (newAlertSuccess === true) {
    $d.confirm(t("confirm"), alertText.value);
    alertSuccess.value = false;
  }
});

onMounted(async () => {
  // 取得使用者組織編號
  registerLoading.value = true;
  const b64Payload = jwtToken
    .split(".")[1]
    .replace(/-/g, "+")
    .replace(/_/g, "/");
  const payloadData = JSON.parse(atob(b64Payload));
  const jwtUserObj = JSON.parse(payloadData.jwtuser);
  loginUserTaxId.value = jwtUserObj.orgId;
  inputData["taxId"] = loginUserTaxId.value;

  try {
    // 檢查是否已發行DID
    const response = await api.get("/api/modadw101w/didRegister", {
      params: { orgId: loginUserTaxId.value },
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`
      }
    });

    if (response.data.data.registered) {
      isDIDRegistered.value = true;

      if (response.data.data.issuerData) {
        DIDStatus.value = t(
          `didregister.status.${response.data.data.issuerData.data.status}`
        );
        registerTime.value = yyyyMMddHHmmss(
          response.data.data.issuerData.data.createdAt
        );

        inputData["nameTw"] = response.data.data.issuerData.data.org.name;
        inputData["nameEn"] = response.data.data.issuerData.data.org.name_en;
        inputData["info"] = response.data.data.issuerData.data.org.info;
        inputData["taxId"] = response.data.data.issuerData.data.org.taxId;
        inputData["serviceBaseURL"] =
          response.data.data.issuerData.data.org.serviceBaseURL || " ";
        inputData["token"] = response.data.data.token;
        cardType.value = response.data.data.issuerData.data.org.x509_type;
        rows.value.find((row) => row.name === "nameEn").isReadonly = true;
        rows.value.find((row) => row.name === "info").isReadonly = true;
        rows.value.find(
          (row) => row.name === "serviceBaseURL"
        ).isReadonly = true;
        rows.value.find((row) => row.name === "token").isReadonly = true;
      } else {
        $n.error(t("didregister.error.issuingEndChain"));
      }
    } else {
      isDIDRegistered.value = false;
      if (!response.data.data.orgTwName) {
        $n.error(t("didregister.error.orgName"));
      } else {
        inputData["nameTw"] = response.data.data.orgTwName;
      }
      regex1.value = new RegExp(
        response.data.data.regex_json.regularExpression
      );
      errMsg1.value = response.data.data.regex_json.errorMessage;
      regex2.value = new RegExp(response.data.data.regex_xss.regularExpression);
      errMsg2.value = response.data.data.regex_xss.errorMessage;
      regex3.value = new RegExp(response.data.data.regex_sql.regularExpression);
      errMsg3.value = response.data.data.regex_sql.errorMessage;
      regexName.value = new RegExp(
        response.data.data.regex_enAndNum.regularExpression
      );
      errMsgName.value = response.data.data.regex_enAndNum.errorMessage;
      regexURL.value = new RegExp(
        response.data.data.regex_url.regularExpression
      );
      errMsgURL.value = response.data.data.regex_url.errorMessage;
      regexTax.value = new RegExp("^[0-9-]+$");
      errMsgTax.value = t("valid.onlyNumDashAllowed");
      baseUrl.value = response.data.data.baseUrl;
    }
  } catch (error) {
    $n.error(t("didregister.error.init"));
  } finally {
    registerLoading.value = false;
  }

  // did註冊環境檢測
  healthCheckLoading.value = true;
  try {
    const envTestResponse = await api.get("/api/modadw101w/envTest", {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`
      }
    });

    isEnvTestPass.value =
      envTestResponse.data.data.frontendSuccess &&
      envTestResponse.data.data.ivpasSuccess;

    healthCheckRows.value[0].result = envTestResponse.data.data.frontendSuccess;
    healthCheckRows.value[1].result = envTestResponse.data.data.ivpasSuccess;

    if (!healthCheckRows.value[0].result) {
      $n.error(t("didregister.error.environmentFrontend"));
    }
    if (!healthCheckRows.value[1].result) {
      $n.error(t("didregister.error.environmentIvpas"));
    }
  } catch (error) {
    $n.error(t("didregister.error.environmentApi"));
  } finally {
    healthCheckLoading.value = false;
  }

  // 建立元素 (插卡驗證用)
  const userOrg = generateElement("input", "userOrg");
  userOrg.value = loginUserTaxId.value;
  const serialNumber = generateElement("input", "serialNumber");
  generateElement("input", "subjectDN");
  generateElement("input", "subjectID");
  generateElement("input", "startDate");
  generateElement("input", "endDate");
  generateElement("select", "slotDescription");
  generateElement("input", "businessId");
  generateElement("input", "issuerDN");
  generateElement("input", "sn");
  generateElement("input", "type");
  generateElement("input", "certb64");
  generateElement("input", "pid");
  generateElement("input", "orgSignature");

  // 簽章成功事件
  generateListener(serialNumber, "click", async () => {
    if (isCert.value === false) {
      // 插卡簽章完成後呼叫VA檢查憑證狀態
      try {
        const requestBody = {
          pid: document.getElementById("pid").value || undefined,
          b64Data: document.getElementById("certb64").value
        };

        const response = await api.post(
          "/api/modadw101w/verifyCert/" + cardType.value,
          requestBody,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${jwtToken}`
            }
          }
        );

        if (
          response.status === HttpStatusCode.Ok &&
          response.data &&
          response.data.code === "0"
        ) {
          isCert.value = true;
          const subjectDNStr = document.getElementById("subjectDN").value;
          let subjectDNJson = {};
          subjectDNStr.split(",").forEach((pair) => {
            let [key, value] = pair.split("=");
            if (subjectDNJson[key]) {
              // 已經存在，轉成陣列或加入陣列
              if (!Array.isArray(subjectDNJson[key])) {
                subjectDNJson[key] = [subjectDNJson[key]];
              }
              subjectDNJson[key].push(value);
            } else {
              subjectDNJson[key] = value;
            }
          });

          // OU 可能為陣列
          if (subjectDNJson.OU && subjectDNJson.OU.length !== 0) {
            let ouValue = Array.isArray(subjectDNJson.OU)
              ? subjectDNJson.OU.join("-")
              : subjectDNJson.OU;
            subjectDNJson.O = subjectDNJson.O + "-" + ouValue;
          }

          if (cardType.value === "MOEACA") {
            inputData["taxId"] = document.getElementById("subjectID").value;
            inputData["nameTw"] = subjectDNJson.O;
          } else if (cardType.value === "MOICA") {
          } else {
            inputData["taxId"] = document
              .getElementById("subjectID")
              .value.replace(/\./g, "-");
            inputData["nameTw"] = subjectDNJson.O;
          }
          isPwd.value.PIN = true;
          showSuccess(t("didregister.success.validSuccess"));
          loadingHide();
        } else {
          if (response.data.code) {
            let msg = response.data.msg;
            if (response.data.data) {
              msg +=
                " ( " +
                t("didregister.error.code") +
                "：" +
                response.data.data.detailCode +
                "，" +
                t("didregister.error.message") +
                "：" +
                response.data.data.detailMessage +
                ")";
            }
            showError(msg);
          } else {
            showError(t("didregister.error.verifyService"));
          }
          loadingHide();
        }
      } catch (error) {
        showError(t("didregister.error.verifyService"));
        loadingHide();
      }
    } else {
      // DID註冊對org簽章後呼叫核心系統DID註冊api
      registerLoading.value = true;
      delete issuerOrg.value.access_token;
      delete issuerOrg.value.orgType;
      const requestBody = {
        org: issuerOrg.value,
        signature: document.getElementById("orgSignature").value
      };

      if (
        requestBody.org.name &&
        requestBody.org.name_en &&
        requestBody.org.info &&
        requestBody.org.taxId &&
        requestBody.org.serviceBaseURL
      ) {
        try {
          const response = await api.post(
            "/api/modadw101w/didRegister",
            requestBody,
            {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwtToken}`
              }
            }
          );
          if (response && response.data && response.data.code === "0") {
            created.value = true;
            const logoutTimer = setTimeout(() => {
              onLogout();
            }, 10000);
            window.logoutTimer = logoutTimer;
          } else {
            $n.error(response.data.msg);
          }
        } catch (error) {
          $n.error(t("didregister.error.didApi"));
        }
      } else {
        $n.error(t("didregister.valid.inputAll"));
      }
      registerLoading.value = false;
    }
  });
});
</script>
<style scoped>
.health-check-table::v-deep(th) {
  background-color: gray;
  color: white;
  font-size: 18px;
}

.health-check-table::v-deep(td) {
  font-size: 16px;
  font-weight: 400;
  padding-top: 10px;
  padding-bottom: 10px;
}

a {
  color: #4e61a7;
  text-decoration-line: underline;
}

.blue-ls {
  padding-left: 0.1rem;
  list-style-type: none;
}

.blue-ls li {
  padding-left: 1.5rem;
  background-image: url(../../assets/top-star.svg);
  background-position: 0 5px;
  background-repeat: no-repeat;
  margin: 2rem 0;
}

.blue-ls li.meali {
  margin: 1rem 0 !important;
  font-family: "Noto Sans TC", system-ui, -apple-system, Roboto,
    "Helvetica Neue", Arial, "Noto Sans", "Liberation Sans", sans-serif,
    "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
  font-weight: 400;
  font-size: 16px;
  color: #4e4e4e;
  line-height: 1.75;
}

.TextGradient {
  background: linear-gradient(to right, #c94d70, #d07744);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
  margin: 0;
}

/* q-select 相關 */
.col :deep(.q-field__native > span) {
  width: 100%;
  text-align: center;
}
</style>
