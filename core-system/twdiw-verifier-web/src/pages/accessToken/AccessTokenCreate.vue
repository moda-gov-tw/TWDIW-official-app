<template>
  <q-btn
    dense
    outline
    color="primary"
    class="q-ma-none q-pa-sm float-right"
    icon="add"
    :label="t('create')"
    :ripple="false"
    @click="openDialog"
  >
  </q-btn>
  <!-- 新增 AccessToken -->
  <q-dialog v-model="isOpen" persistent>
    <q-card style="width: 750px; min-width: 30%; max-width: 80%">
      <q-card-section class="row bg-dark text-white items-center">
        <div class="text-h5 text-weight-bold">
          {{ t("accessToken.create") }}
        </div>
        <q-space></q-space>
        <q-btn
          flat
          fab-mini
          icon="close"
          v-close-popup
          @click="formatClean"
        ></q-btn>
      </q-card-section>
      <q-form @submit="onSubmit" @keydown="preventEnter" class="q-gutter-md">
        <q-card-section class="q-pt-none">
          <div class="row">
            <div class="col">
              <p class="q-mb-sm q-mt-lg input-title">
                *{{ t("accessToken.table.name") }}
              </p>
              <div class="row flex-center q-mb-lg">
                <q-input
                  name="accessTokenName"
                  outlined
                  v-model="formData.accessTokenName"
                  dense
                  placeholder=""
                  class="col bg-white q-mr-xs"
                  autocomplete="off"
                  :rules="[
                    (val) =>
                      !!val ||
                      t('notBlank', { input: t('accessToken.table.name') })
                  ]"
                />
              </div>
            </div>
            <div class="col">
              <p class="q-mb-sm input-title q-mt-lg">
                {{ t("accessToken.table.ownerName") }}
              </p>
              <q-input
                name="ownerName"
                outlined
                v-model="formData.ownerName"
                dense
                placeholder=""
                type="text"
                class="q-mb-lg bg-white"
                autocomplete="off"
              />
            </div>
          </div>
          <div class="row">
            <div class="col">
              <p class="q-mb-sm input-title">
                *{{ t("accessToken.table.orgId") }}
              </p>
              <div class="row flex-center q-mb-lg">
                <q-input
                  name="orgId"
                  outlined
                  v-model="formData.orgId"
                  dense
                  placeholder=""
                  class="col bg-white q-mr-xs"
                  autocomplete="off"
                  :rules="[
                    (val) =>
                      !!val ||
                      t('notBlank', { input: t('accessToken.table.orgId') })
                  ]"
                />
              </div>
            </div>
            <div class="col">
              <p class="q-mb-sm input-title">
                {{ t("accessToken.table.enabled") }}
              </p>
              <q-select
                name="state"
                outlined
                dense
                v-model="isActivated"
                class="q-mb-lg bg-white"
                :options="[
                  { label: t('enabled'), value: 'enabled' },
                  { label: t('disabled'), value: 'disabled' }
                ]"
                :rules="[
                  (val) =>
                    !!val ||
                    t('notBlank', { input: t('accessToken.table.enabled') })
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
              :label="t('reset')"
              class="q-mt-md"
              @click="formatClean"
              color="teal"
            >
            </q-btn>
            <q-btn
              class="q-mt-md"
              size="md"
              type="submit"
              :label="t('confirm')"
              color="teal"
            >
            </q-btn>
          </div>
        </q-card-section>
      </q-form>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { ref } from "vue";
import { useNotify } from "src/utils/plugin";
import { useAccessTokenStore } from "stores/accessToken";
import { reactive } from "vue";
import { api } from "src/boot/axios";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const $n = useNotify();
const isOpen = ref(false);
const store = useAccessTokenStore();
const { getAllAccessTokens } = store;
const jwtToken = sessionStorage.getItem("jwt-user-object");

const formData = reactive({
  accessTokenName: "",
  ownerName: "",
  state: "",
  authRes: []
});

const openDialog = () => {
  isOpen.value = true;
};

const onSubmit = () => {
  createAccessToken();
};

// AccessToken管理頁面，新增AccessToken。
const createUrl = `/api/modadw351w/create`;
const createAccessToken = () => {
  if (isActivated.value.value === undefined) {
    formData.state = null;
  } else if (isActivated.value.value === "enabled") {
    formData.state = "enabled";
  } else {
    formData.state = "disabled";
  }
  api
    .post(createUrl, formData, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`
      }
    })
    .then((response) => {
      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          $n.success(t("accessToken.success.create"));
          formatClean();
          isOpen.value = false;
          getAllAccessTokens();
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

const preventEnter = (evt) => {
  if (evt.key === "Enter") {
    evt.preventDefault();
  }
};

const isActivated = ref("");
const formatClean = () => {
  formData.accessTokenName = "";
  formData.orgId = "";
  formData.ownerName = "";
  formData.state = "";
  formData.authRes = [];
  isActivated.value = "";
};
</script>
