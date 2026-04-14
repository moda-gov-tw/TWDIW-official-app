<template>
  <!-- dialog -->
  <q-dialog v-model="isOpenCreate" persistent>
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 新增帳號 -->
        <div class="text-h6 text-weight-bold">
          {{ t("account.title.create") }}
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
      <q-form
        @submit="onSubmit"
        @keydown="preventEnter"
        class="q-gutter-md q-mt-xl"
      >
        <q-card-section class="q-pt-none">
          <div class="row">
            <!-- 帳號(電子郵件) -->
            <div class="col">
              <p class="q-mb-sm input-title">
                <span class="text-red">*</span>{{ t("account.table.userId") }}
              </p>
              <q-input
                name="login"
                暱稱
                outlined
                v-model="formDataCreate.login"
                dense
                placeholder=""
                class="col-9 bg-white q-mr-xs"
                autocomplete="off"
                :rules="[
                  (val) =>
                    !!val ||
                    t('notBlank', { input: t('account.table.userId') }),
                  (val) =>
                    !val ||
                    emailRegex.test(val) ||
                    t('account.valid.format', {
                      input: t('account.table.userId')
                    }),
                  customMaxLengthRule(50)
                ]"
              />
            </div>
            <!-- 暱稱 -->
            <div class="col q-mr-sm">
              <p class="q-mb-sm input-title">
                <span class="text-red">*</span>{{ t("account.table.name") }}
              </p>
              <q-input
                name="firstName"
                outlined
                v-model="formDataCreate.userName"
                dense
                placeholder=""
                type="text"
                class="q-mb-lg bg-white"
                autocomplete="off"
                :rules="[
                  (val) =>
                    !!val || t('notBlank', { input: t('account.table.name') }),
                  customMaxLengthRule(15)
                ]"
              />
            </div>
          </div>
          <div class="row">
            <!-- 組織 -->
            <div class="col">
              <p class="q-mb-sm input-title">
                <span class="text-red">*</span>{{ t("account.table.org") }}
              </p>
              <q-select
                name="orgId"
                outlined
                v-model="formDataCreate.orgId"
                dense
                :options="orgIdOptions"
                class="q-mb-lg bg-white"
                autocomplete="off"
                :rules="[
                  (val) =>
                    !!val || t('choose', { input: t('account.table.org') })
                ]"
              />
            </div>
            <!-- 手機號碼 -->
            <div class="col">
              <p class="q-mb-sm input-title">
                <span class="text-red">*</span>{{ t("account.table.tel") }}
              </p>
              <q-input
                name="tel"
                outlined
                v-model="formDataCreate.tel"
                dense
                placeholder=""
                type="text"
                class="q-mb-lg bg-white"
                autocomplete="off"
                :rules="[
                  (val) =>
                    !!val || t('notBlank', { input: t('account.table.tel') }),
                  (val) => val.length >= 10 || t('account.valid.notLessThan'),
                  (val) =>
                    val.length <= 20 || t('account.valid.notGreaterThan'),
                  (val) =>
                    !/[\u4e00-\u9fa5]/.test(val) ||
                    t('account.valid.notEnterChinese'),
                  (val) => /^[0-9]*$/.test(val) || t('account.valid.onlyCan')
                ]"
              />
            </div>
          </div>
          <div class="row">
            <!-- 帳號類型 -->
            <div class="col">
              <p class="q-mb-sm input-title">
                <span class="text-red">*</span
                >{{ t("account.table.userTypeId") }}
              </p>
              <q-select
                name="userTypeId"
                outlined
                v-model="formDataCreate.userTypeId"
                dense
                :options="userTypeIdOptions"
                class="q-mb-lg bg-white"
                autocomplete="off"
                :rules="[
                  (val) =>
                    !!val ||
                    t('choose', { input: t('account.table.userTypeId') })
                ]"
              />
            </div>

            <div class="col q-mr-sm"></div>
          </div>
        </q-card-section>
        <q-card-section>
          <div class="row q-gutter-sm full-width">
            <q-space></q-space>
            <q-btn
              outline
              size="md"
              :label="t('reset')"
              class="q-mt-md"
              @click="formatClean"
              color="primary"
            >
            </q-btn>
            <q-btn
              class="q-mt-md"
              size="md"
              type="submit"
              :label="t('confirm')"
              color="primary"
              :disable="createDialogLoading"
              :loading="createDialogLoading"
            >
            </q-btn>
          </div>
        </q-card-section>
      </q-form>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { onMounted } from "vue";
import { useAccountStore } from "stores/account";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const store = useAccountStore();
const {
  isOpenCreate,
  formDataCreate,
  orgIdOptions,
  userTypeIdOptions,
  createDialogLoading
} = storeToRefs(store);
const { createUser, formatClean } = store;

const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// 計算字數的自定義規則
const customMaxLengthRule = (maxLength) => {
  return (val) => {
    const totalLength = val.length;
    return (
      totalLength <= maxLength || t("validation.maxLength", { max: maxLength })
    );
  };
};

const onSubmit = () => {
  createUser(formDataCreate.value);
};

const preventEnter = (evt) => {
  if (evt.key === "Enter") {
    evt.preventDefault();
  }
};

onMounted(() => {});
</script>
<style scoped lang="sass">
.sticky-header
  /* height or max-height is important */
  max-height: 85vh
  overflow-y: auto
  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th
    /* bg color is important for th; just specify one */
    background-color: #fff
  thead tr th
    position: sticky
    z-index: 1
  thead tr:first-child th
    top: 0

  /* this is when the loading indicator appears */
  &.q-table--loading thead tr:last-child th
    /* height of all previous header rows */
    top: 48px

.col
  margin-left: 10px
  margin-right: 10px
</style>
