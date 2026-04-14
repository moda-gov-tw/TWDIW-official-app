<template>
  <div class="column q-pa-sm q-gutter-sm">
    <template v-if="!isOpenDetail">
      <q-img width="200px" :src="box" class="self-center"></q-img>
      <p class="text-h6 text-center">{{ t("noData") }}</p>
    </template>
    <template v-else>
      <!-- Token 詳情 -->
      <div class="row full-width items-center">
        <span class="text-h5 q-px-sm q-ma-none">{{
          t("accessToken.detail")
        }}</span>
        <q-btn
          outline
          :label="t('refresh')"
          icon="refresh"
          color="dark"
          class="q-pa-sm"
          @click="refreshDetail"
        />
        <div class="q-px-sm" />
        <q-btn
          outline
          :label="t('accessToken.editToken')"
          icon="edit"
          color="accent"
          class="q-pa-sm"
          @click="openDialog"
        />
        <div class="q-px-sm" />
        <q-btn
          outline
          :label="t('close')"
          icon="arrow_forward"
          color="dark"
          class="q-pa-sm"
          @click="closeDetail(false)"
        />
      </div>
      <div class="q-pa-md">
        <q-markup-table flat dense bordered separator="cell">
          <tbody>
            <tr v-for="row in tableRow" :key="row.field">
              <td
                class="text-center"
                style="background-color: bisque; width: 20%"
              >
                {{ row.label }}
              </td>
              <td class="text-left" style="white-space: pre-line">
                <span v-if="row.field === 'state'">
                  {{ formatState(selected[row.field]) }}
                </span>
                <span v-else-if="row.field === 'createTime'">
                  {{ formatDate(selected[row.field]) }}
                </span>
                <span v-else>
                  {{ selected[row.field] }}
                </span>
              </td>
            </tr>
          </tbody>
        </q-markup-table>
        <!-- accessTokenResList -->
        <!-- 賦予權限清單 -->
        <p class="text-h6 text-center q-mb-none q-mt-lg">
          {{ t("accessToken.resList") }}
        </p>
        <q-table
          class="sticky-header full-height"
          flat
          :rows="accessTokenResList"
          :columns="userRolesColumns"
          row-key="resId"
          :hide-header="loading"
          :loading="loading"
          dense
          :pagination="{ rowsPerPage: 10 }"
          :rows-per-page-options="rowsPerPage"
        >
          <!-- 設定 q-table body-cell -->
          <template v-slot:loading>
            <q-inner-loading showing color="primary" />
          </template>
        </q-table>
      </div>
      <!-- dialog -->
      <!-- 編輯 AccessToken -->
      <q-dialog v-model="isOpen" persistent>
        <q-card style="width: 750px; min-width: 30%; max-width: 80%">
          <q-card-section class="row bg-dark text-white items-center">
            <div class="text-h5 text-weight-bold">
              {{ t("accessToken.edit") }}
            </div>
            <q-space></q-space>
            <q-btn flat fab-mini icon="close" v-close-popup></q-btn>
          </q-card-section>
          <q-form
            @submit="onSubmit"
            @keydown="preventEnter"
            class="q-gutter-md"
          >
            <q-card-section class="">
              <div class="row q-mb-md">
                <!-- AccessToken -->
                <div class="col">
                  <p class="q-mb-sm input-title">
                    {{ t("accessToken.table.token") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    v-model="formData.accessToken"
                    name="accessToken"
                    type="text"
                    autocomplete="off"
                    disable
                  />
                </div>
              </div>
              <div class="row q-mb-md">
                <!-- Token 名稱 -->
                <div class="col q-mr-md">
                  <p class="q-mb-sm input-title">
                    {{ t("accessToken.table.name") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    v-model="formData.accessTokenName"
                    name="accessTokenName"
                    autocomplete="off"
                  />
                </div>
                <!-- 擁有者 -->
                <div class="col">
                  <p class="q-mb-sm input-title">
                    {{ t("accessToken.table.ownerName") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    v-model="formData.ownerName"
                    name="ownerName"
                    autocomplete="off"
                  />
                </div>
              </div>
              <div class="row q-mb-md">
                <!-- 組織 -->
                <div class="col q-mr-md">
                  <p class="q-mb-sm input-title">
                    {{ t("accessToken.table.orgId") }}
                  </p>
                  <q-input
                    dense
                    outlined
                    v-model="formData.orgId"
                    name="orgId"
                    autocomplete="off"
                  />
                </div>
                <!-- 啟用狀態 -->
                <div class="col">
                  <p class="q-mb-sm input-title">
                    {{ t("account.table.enabled") }}
                  </p>
                  <q-select
                    dense
                    outlined
                    v-model="isActivated"
                    name="state"
                    :options="[
                      { label: t('enabled'), value: 'enable' },
                      { label: t('disabled'), value: 'disable' }
                    ]"
                  />
                </div>
              </div>
            </q-card-section>
            <q-card-section>
              <div class="row q-gutter-sm full-width">
                <q-space></q-space>
                <q-btn
                  size="md"
                  :label="t('cancel')"
                  class="q-mt-md"
                  v-close-popup
                  @click="formClean"
                >
                </q-btn>
                <q-btn
                  class="q-mt-md"
                  size="md"
                  type="submit"
                  :label="t('ok')"
                  v-close-popup
                >
                </q-btn>
              </div>
            </q-card-section>
          </q-form>
        </q-card>
      </q-dialog>
    </template>
  </div>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { ref, reactive } from "vue";
import { useAccessTokenStore } from "stores/accessToken";
import box from "assets/empty-box.svg";
import { useI18n } from "vue-i18n";
import { api } from "src/boot/axios";
import { useNotify } from "src/utils/plugin";

const isOpen = ref(false);
const emit = defineEmits(["detailClick"]);
const { t } = useI18n();
const store = useAccessTokenStore();
const { selected, isOpenDetail, accessTokenResList } = storeToRefs(store);
const isActivated = ref("");
const loading = ref(false);
const { getAllAccessTokens } = store;
const $n = useNotify();
const jwtToken = sessionStorage.getItem("jwt-user-object");

let formData = reactive({
  id: "",
  accessToken: "",
  accessTokenName: "",
  ownerName: "",
  orgId: "",
  state: ""
});

//table分頁參數
const rowsPerPage = ref([5]);

//欄位設定
const userRolesColumns = [
  {
    name: "resName",
    label: t("func.table.name"),
    sortable: true,
    field: "resName",
    align: "left"
  },
  {
    name: "resId",
    label: t("func.table.code"),
    sortable: false,
    field: "resId",
    align: "left"
  }
];

const tableRow = [
  {
    label: t("accessToken.table.token"),
    field: "accessToken"
  },
  {
    label: t("accessToken.table.name"),
    field: "accessTokenName"
  },
  {
    label: t("accessToken.table.orgId"),
    field: "orgId"
  },
  {
    label: t("accessToken.table.orgName"),
    field: "orgName"
  },
  {
    label: t("accessToken.table.ownerName"),
    field: "ownerName"
  },
  {
    label: t("accessToken.table.enabled"),
    field: "state"
  },
  {
    label: t("accessToken.table.createTime"),
    field: "createTime"
  }
];

const openDialog = () => {
  setFormDataFromSelected();
  isOpen.value = true;
};

const setFormDataFromSelected = () => {
  if (selected.value) {
    formData.id = selected.value.id || "";
    formData.accessToken = selected.value.accessToken || "";
    formData.accessTokenName = selected.value.accessTokenName || "";
    formData.ownerName = selected.value.ownerName || "";
    formData.orgId = selected.value.orgId || "";
    isActivated.value =
      selected.value.state === "true" ? t("enabled") : t("disabled");
  }
};

const preventEnter = (evt) => {
  if (evt.key === "Enter") {
    evt.preventDefault();
  }
};

const onSubmit = () => {
  updateAccessToken();
};

const updateUrl = `/api/modadw351w/update`;
// AccessToken管理頁面，更新AccessToken。
const updateAccessToken = () => {
  if (isActivated.value === t("enabled")) {
    formData.state = true;
  } else if (isActivated.value === t("disabled")) {
    formData.state = false;
  } else if (
    isActivated.value.label === t("enabled") ||
    isActivated.value.value === "enabled"
  ) {
    formData.state = true;
  } else {
    formData.state = false;
  }

  api
    .post(updateUrl, formData, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`
      }
    })
    .then(async (response) => {
      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          selected.value = response.data.data;
          $n.success(t("accessToken.finish"));
          await getAllAccessTokens();
          formClean();
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

const closeDetail = (index) => {
  emit("detailClick", index);
  isOpenDetail.value = false;
};

const formatState = (status) => {
  return status != null
    ? status === "true"
      ? t("enabled")
      : t("disabled")
    : "";
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
};

const formClean = () => {
  isActivated.value = "";
  formData.id = "";
  formData.accessToken = "";
  formData.accessTokenName = "";
  formData.ownerName = "";
  formData.orgId = "";
};
</script>
