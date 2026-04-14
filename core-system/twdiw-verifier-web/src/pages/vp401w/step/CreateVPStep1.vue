<template>
  <div class="q-px-xs">
    <q-form ref="formRef" greedy>
      <div
        class="row q-my-md-md q-col-gutter-md justify-center q-pt-md q-mb-md q-pa-md"
      >
        <!-- VP代碼 -->
        <div class="col-12 col-lg-6">
          <div :class="[isMobile ? '' : 'row items-start']">
            <p :class="[isMobile ? 'q-mb-sm' : 'title-width pt-10']">
              <span class="text-red">*</span>
              <span class="q-pr-sm">{{ t("vp.serialNo.label") }}</span>
            </p>
            <q-input
              outlined
              class="col"
              v-model.trim="formData.serialNo"
              :rules="[
                (val) =>
                  !!val || t('input', { input: ' ' + t('vp.serialNo.label') }),
                (val) =>
                  val.length <= 50 || t('validation.maxLength', { max: 50 }),
                ,
                (val) => /^[a-z0-9_]+$/.test(val) || t('vp.serialNo.pattern')
              ]"
              hide-bottom-space
              :placeholder="t('vp.serialNo.placeholder')"
              dense
              :disable="Boolean(props.isEdit)"
            />
          </div>
        </div>
        <!-- VP名稱 -->
        <div class="col-12 col-lg-6">
          <div :class="[isMobile ? '' : 'row items-start']">
            <p :class="[isMobile ? 'q-mb-sm' : 'title-width pt-10']">
              <span class="text-red">*</span>
              <span class="q-pr-md">{{ t("vp.name.label") }}</span>
            </p>
            <q-input
              outlined
              class="col"
              v-model.trim="formData.name"
              :rules="[
                (val) =>
                  !!val || t('input', { input: ' ' + t('vp.name.label') }),
                (val) =>
                  val.length <= 16 || t('validation.maxLength', { max: 16 }),
                (val) =>
                  /^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val) ||
                  t('vp.name.pattern')
              ]"
              hide-bottom-space
              :placeholder="t('vp.name.placeholder')"
              dense
            />
          </div>
        </div>
        <!-- VP 授權目的 -->
        <div class="col-12 col-lg-6">
          <div :class="[isMobile ? '' : 'row items-start']">
            <p :class="[isMobile ? 'q-mb-sm' : 'title-width pt-10']">
              <span class="text-red">*</span>
              <span class="q-mr-md">{{ t("vp.purpose.label") }}</span>
            </p>
            <q-input
              outlined
              class="col"
              v-model.trim="formData.purpose"
              :rules="[
                (val) =>
                  !!val || t('input', { input: ' ' + t('vp.purpose.label') }),
                (val) =>
                  /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
                  t('vp.purpose.pattern'),
                (val) =>
                  val.length <= 30 || t('validation.maxLength', { max: 30 })
              ]"
              hide-bottom-space
              :placeholder="t('vp.purpose.placeholder')"
              dense
            />
          </div>
        </div>
        <!-- VP 授權條款 -->
        <div class="col-12 col-lg-6">
          <div :class="[isMobile ? '' : 'row items-center relative-position']">
            <p
              :class="[
                isMobile
                  ? 'q-mb-sm'
                  : 'row items-end justify-between title-width q-pr-sm'
              ]"
            >
              <span
                ><span class="text-red">*</span>{{ t("vp.terms.label") }}</span
              >
              <q-icon
                :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                color="primary"
                name="info_outline"
                size="sm"
                @click="onShowTermsInfoClick"
              />
            </p>
            <q-btn
              icon="description"
              :label="t('vp.btn.editTerms')"
              :color="formData.terms ? 'indigo-6' : 'primary'"
              :class="isMobile ? 'full-width' : ''"
              @click="showAddUserTermsDialog"
            />
            <div
              :class="[
                isMobile
                  ? 'row items-center q-mt-sm'
                  : 'row items-center q-pl-md'
              ]"
              style="gap: 4px"
            >
              <template v-if="formData.terms">
                <q-icon
                  name="check_circle"
                  color="indigo-6"
                  class="q-ml-xs"
                  size="24px"
                />
                <span class="text-indigo-6">
                  {{ t("vp.terms.status.edited") }}
                </span>
              </template>
              <template v-else>
                <q-icon
                  name="cancel"
                  color="primary"
                  class="q-ml-xs"
                  size="24px"
                />
                <span class="text-primary">
                  {{ t("vp.terms.status.notEdited") }}
                </span>
              </template>
            </div>

            <div
              :class="[
                isMdSize
                  ? 'text-body2 text-white termsMdsizeNotice'
                  : 'text-body2 text-white termsNotice'
              ]"
              v-show="
                (!isMobile && showTermsInfo) || (isMdSize && showTermsInfo)
              "
              ref="termsNotice"
            >
              <p class="text-body2 text-white">
                {{ t("vp.terms.notice") }}
              </p>
            </div>
          </div>
        </div>
        <!-- 進階功能（未上線） -->
        <div class="col-12">
          <q-btn
            class="q-mt-sm q-mb-xs text-indigo-6"
            :icon="expandIcon()"
            :label="t('vp.btn.advancedFeature')"
            color="indigo-2"
            @click="formData.showMore = !formData.showMore"
          />
        </div>
        <!-- 靜態 QR Code 模式、Offline 模式 -->
        <div v-show="formData.showMore" class="col-12 q-mb-sm">
          <div class="row">
            <div
              :class="[
                isMobile ? 'col-12' : 'row items-start col-6 q-pr-sm ',
                'relative-position'
              ]"
            >
              <p
                :class="[
                  isMobile
                    ? 'q-mb-sm'
                    : 'row items-end justify-between title-width pt-10 q-pr-sm'
                ]"
              >
                <span>{{ t("vp.model.label") }}</span>
                <!-- 提醒 icon -->
                <q-icon
                  :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                  color="primary"
                  name="info_outline"
                  size="sm"
                  @click="showInfoOnClick()"
                />
              </p>
              <q-select
                class="col"
                v-model="formData.model"
                :options="modelTypeList"
                :label="t('choose', { input: t('vp.model.label') })"
                map-options
                outlined
                dense
              />
              <!-- 靜態 QR Code 模式提醒 文字 -->
              <!-- APP 出示憑證模式 模式提醒 文字 -->
              <div
                class="text-body2 text-white notice"
                v-show="!isMobile && showQRCodeOrAppInfo"
                ref="notice"
              >
                <p class="text-body2 text-white">
                  {{ t("vp.model.staticNotice") }}
                </p>
                <p class="text-body2 text-white">
                  {{ t("vp.model.offlineNotice") }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <!-- 組織業務系統 URL -->
        <div
          v-if="formData.model.value === '1' && formData.showMore"
          class="col-12 col-lg-6 q-mb-sm"
        >
          <div
            :class="[isMobile ? '' : 'row items-start ', 'relative-position']"
          >
            <p
              :class="[
                isMobile
                  ? ''
                  : 'row items-end justify-between title-width pt-10 q-pr-sm'
              ]"
            >
              <span
                ><span class="text-red">*</span
                >{{ t("vp.serviceUrl.label") }}</span
              >
              <!-- 提醒 icon -->
              <q-icon
                :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                color="primary"
                name="info_outline"
                size="sm"
                @click="showDeeplinkUrlInfoOnClick()"
              />
            </p>
            <q-input
              outlined
              :class="[isMdSize ? 'col' : 'col']"
              v-model="formData.verifierServiceUrl"
              :rules="[
                (val) => {
                  const pattern =
                    /^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-\.~:\/?#\[\]@!$&'()*+,;=%]*)?$/;
                  return pattern.test(val) || t('vp.serviceUrl.pattrrn');
                }
              ]"
              hide-bottom-space
              :placeholder="t('input', { input: t('vp.serviceUrl.label') })"
              dense
            />

            <!-- 組織業務系統 URL提醒 文字 -->
            <div
              class="text-body2 text-white deeplinkUrlNotice"
              v-show="
                (!isMobile && showDeeplinkUrlInfo) ||
                (isMdSize && showDeeplinkUrlInfo)
              "
              ref="deeplinkUrlNotice"
            >
              <p class="text-body2 text-white">
                {{ t("vp.serviceUrl.notice") }}
              </p>
            </div>
          </div>
        </div>
        <!-- CallBack URL -->
        <div
          v-if="formData.model.value === '1' && formData.showMore"
          class="col-12 col-lg-6"
        >
          <div
            :class="[isMobile ? '' : 'row items-start', 'relative-position']"
          >
            <p
              :class="[
                isMobile
                  ? 'q-mb-sm'
                  : 'row items-end justify-between title-width pt-10 q-pr-sm'
              ]"
            >
              <span
                ><span class="text-red">*</span
                >{{ t("vp.callBackUrl.label") }}</span
              >
              <!-- 提醒 icon -->
              <q-icon
                :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                color="primary"
                name="info_outline"
                size="sm"
                @click="showCallBackUrlInfoOnClick"
              />
            </p>
            <q-input
              outlined
              class="col"
              v-model.trim="formData.callBackUrl"
              :rules="[
                (val) => {
                  const pattern =
                    /^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-\.~:\/?#\[\]@!$&'()*+,;=%]*)?$/;
                  return pattern.test(val) || t('vp.callBackUrl.pattrrn');
                }
              ]"
              hide-bottom-space
              :placeholder="
                t('input', { input: ' ' + t('vp.callBackUrl.label') })
              "
              dense
            />
            <!-- CallBack URL 提醒文字 -->
            <div
              class="text-body2 text-white callBackUrlNotice"
              v-show="!isMobile && showCallBackUrlInfo"
              ref="callBackUrlNotice"
            >
              <p class="text-body2 text-white">
                {{ t("vp.callBackUrl.notice") }}
              </p>
            </div>
          </div>
        </div>

        <div
          v-if="formData.model.value === '1' && formData.showMore"
          class="col-12 q-mb-sm"
        >
          <div
            :class="[isMobile ? '' : 'row items-start', 'relative-position']"
          >
            <p
              :class="[
                isMobile
                  ? 'q-mb-sm'
                  : 'row items-end justify-between title-width pt-10 q-pr-sm'
              ]"
            >
              <span> {{ t("vp.customFields.label") }}</span>
              <!-- 提醒 icon -->
              <q-icon
                :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                color="primary"
                name="info_outline"
                size="sm"
                @click="showCustomFieldsInfoOnClick"
              />
            </p>
            <q-checkbox
              v-model="formData.isCustomFields"
              size="md"
              keep-color
              color="indigo-4"
            />
            <div
              class="text-body2 text-white customFieldsNotice"
              v-show="!isMobile && showCustomFieldsInfo"
              ref="customFieldsNotice"
            >
              {{ t("vp.customFields.notice") }}
            </div>
          </div>
        </div>

        <!-- 限定資料格式、欄位對外名稱、欄位名稱(英)、正規表達式、資料內容 -->
        <div
          v-if="formData.model.value === '1' && formData.showMore"
          :class="['col-12', isMobile ? '' : 'q-mt-md']"
        >
          <div
            v-if="$q.screen.gt.md"
            class="row q-pb-sm q-my-sm justify-around"
            :style="{ borderBottom: '1px solid #CCC' }"
          >
            <div
              v-for="field in tableList"
              :key="field.value"
              class="col-2 text-center"
            >
              <p class="text-md-h6">
                <span
                  v-if="field.required && formData.isCustomFields"
                  class="text-red"
                  >*</span
                >{{ field.label }}
              </p>
            </div>
          </div>

          <ul ref="list">
            <li
              v-for="(row, index) in formData.fields"
              :key="index"
              class="q-mb-sm"
            >
              <!-- 中、手機 -->
              <div v-if="$q.screen.lt.lg || isMobile" class="q-pb-md">
                <div
                  v-for="field in tableList"
                  class="q-mb-sm"
                  :key="field.value"
                >
                  <p class="q-mb-sm">
                    <span
                      v-if="field.required && formData.isCustomFields"
                      class="text-red"
                      >*</span
                    >{{ field.label }}：
                  </p>
                  <q-input
                    v-model="row[field.value]"
                    outlined
                    class="full-width"
                    dense
                    :disable="!formData.isCustomFields"
                    :rules="[...checkField(field.value, row)]"
                    :placeholder="
                      t('input', {
                        input: `${field.label}`
                      })
                    "
                  />
                </div>
              </div>
              <!-- 電腦版且無任何縮放 -->
              <div v-else class="q-pb-md">
                <!-- 一整列為單位 -->

                <div class="row" v-for="row in formData.fields" :key="row.id">
                  <div
                    v-for="field in tableList"
                    :key="field.value"
                    class="col text-left"
                  >
                    <q-input
                      class="q-mt-xs q-mx-xs"
                      v-model="row[field.value]"
                      dense
                      outlined
                      :disable="!formData.isCustomFields"
                      :rules="[...checkField(field.value, row)]"
                      :placeholder="
                        t('input', {
                          input: `${field.label}`
                        })
                      "
                    />
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
        <!-- 模組加密 -->
        <div
          v-if="formData.model.value === '2' && formData.showMore"
          class="col-12"
        >
          <div class="row">
            <div
              :class="[
                isMobile ? 'col-12' : 'row items-start col-6 q-pr-sm',
                'relative-position'
              ]"
            >
              <p
                :class="[
                  isMobile
                    ? ''
                    : 'row items-end justify-between title-width pt-10 q-pr-sm'
                ]"
              >
                <span>{{ t("vp.encryptEnabled.label") }}</span>
                <!-- 提醒 icon -->
                <q-icon
                  :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                  color="primary"
                  name="info_outline"
                  size="sm"
                  @click="showIsEncryptEnabledInfoOnClick"
                />
              </p>
              <q-checkbox
                v-model="formData.isEncryptEnabled"
                size="md"
                keep-color
                color="indigo-4"
              />
              <!-- 模組加密提醒 文字 -->
              <div
                class="text-body2 text-white isEncryptEnabledNotice"
                v-show="!isMobile && showIsEncryptEnabledInfo"
                ref="isEncryptEnabledNotice"
              >
                <p class="text-body2 text-white">
                  {{ t("vp.encryptEnabled.notice") }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <!-- TAG -->
        <div
          v-if="
            formData.model.value === '2' &&
            formData.isEncryptEnabled &&
            formData.showMore
          "
          class="col-12"
        >
          <div class="row">
            <div
              :class="[
                isMobile ? 'col-12' : 'row items-start col-6 q-pr-sm',
                'relative-position'
              ]"
            >
              <p
                :class="[
                  isMobile
                    ? ''
                    : 'row items-end justify-between title-width pt-10 q-pr-sm'
                ]"
              >
                <span
                  ><span class="text-red">*</span> {{ t("vp.tag.label") }}</span
                >
                <!-- 提醒 icon -->
                <q-icon
                  :class="['cursor-pointer', isMobile ? 'q-ml-sm' : '']"
                  color="primary"
                  name="info_outline"
                  size="sm"
                  @click="showTagInfoOnClick"
                />
              </p>
              <q-input
                outlined
                class="col"
                v-model.trim="formData.tag"
                :rules="[
                  (val) =>
                    !!val || t('input', { input: ' ' + t('vp.tag.label') }),
                  (val) =>
                    val.length <= 18 || t('validation.maxLength', { max: 18 })
                ]"
                hide-bottom-space
                :placeholder="t('input', { input: ' ' + t('vp.tag.label') })"
                dense
              />
              <!-- TAG提醒 文字 -->
              <div
                class="text-body2 text-white tagNotice"
                v-show="!isMobile && showTagInfo"
                ref="tagNotice"
              >
                <p class="text-body2 text-white">
                  {{ t("vp.tag.notice") }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </q-form>
  </div>

  <!-- 顯示條款說明文字（手機） -->
  <q-dialog v-model="showMobileTermsInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.terms.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">
          {{ t("vp.terms.notice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 顯示模式說明文字（手機） -->
  <q-dialog v-model="showMobileQRCodeOrAppInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.model.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">
          {{ t("vp.model.staticNotice") }}
        </p>
        <p class="text-body2">
          {{ t("vp.model.offlineNotice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 顯示Tag說明文字（手機） -->
  <q-dialog v-model="showMobileTagInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.tag.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">
          {{ t("vp.tag.notice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 顯示 組織業務系統 URL 說明文字（手機） -->
  <q-dialog v-model="showMobileDeeplinkUrlInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.serviceUrl.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">{{ t("vp.serviceUrl.notice") }}</p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
  <!-- 顯示 callBack URL 說明文字（手機） -->
  <q-dialog v-model="showMobileCallBackUrlInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.callBackUrl.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">{{ t("vp.callBackUrl.notice") }}</p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 顯示限定資料格式說明文字（手機） -->
  <q-dialog v-model="showMobileCustomFieldsInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.customFields.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2 word-break">
          {{ t("vp.customFields.notice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <!-- 顯示模組加密說明文字（手機） -->
  <q-dialog v-model="showMobileIsEncryptEnabledInfo">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.encryptEnabled.label") }}</div>
      </q-card-section>
      <q-card-section>
        <p class="text-body2">
          {{ t("vp.encryptEnabled.notice") }}
        </p>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useQuasar } from "quasar";
import AddUserTermsDialog from "../AddUserTermsDialog.vue";
import { useNotify } from "src/utils/plugin";
import { useVpStore } from "stores/vp";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const vpStore = useVpStore();
const { modelTypeList } = storeToRefs(vpStore);
const $n = useNotify();

const $q = useQuasar();

// 表單相關
const formRef = ref(null);
// 顯示條款說明文字
const showTermsInfo = ref(false);
// 顯示條款說明文字（手機）
const showMobileTermsInfo = ref(false);
// 授權條款資訊內容
const termsNotice = ref(null);
// 授權條款資訊內容 (平板)
// const termsMdsizeNotice = ref(null);
// 顯示靜態 QR Code、APP出示憑證 說明文字
const showQRCodeOrAppInfo = ref(false);
// 顯示靜態 QR Code、APP出示憑證 說明文字（手機）
const showMobileQRCodeOrAppInfo = ref(false);
// 靜態 QR Code、Offline資訊內容
const notice = ref(null);
// 顯示TAG說明文字
const showTagInfo = ref(false);
// 顯示TAG說明文字（手機）
const showMobileTagInfo = ref(false);
// TAG資訊內容
const tagNotice = ref(null);
// 顯示組織業務系統 URL 資訊內容
const deeplinkUrlNotice = ref(null);
// 顯示組織業務系統 URL 說明文字
const showDeeplinkUrlInfo = ref(false);
// 顯示組織業務系統 URL 說明文字 (手機)
const showMobileDeeplinkUrlInfo = ref(false);
// 顯示 callBackUrl 說明文字
const showCallBackUrlInfo = ref(false);
// 顯示 callBackUrl 說明文字 (手機)
const showMobileCallBackUrlInfo = ref(false);
// 顯示模組加密說明文字
const showIsEncryptEnabledInfo = ref(false);
// 顯示模組加密說明文字 (手機)
const showMobileIsEncryptEnabledInfo = ref(false);
// 模組加密資訊內容
const isEncryptEnabledNotice = ref(null);
// callBackUrl 資訊內容
const callBackUrlNotice = ref(null);
// 顯示限定資料格式說明文字
const showCustomFieldsInfo = ref(false);
// 顯示限定資料格式說明文字（手機）
const showMobileCustomFieldsInfo = ref(false);
// 限定資料格式資訊內容
const customFieldsNotice = ref(null);
// 用於生成dataLists唯一 id
let nextId = 1;

// 步驟資料
const formData = defineModel("formData");

const props = defineProps({
  isEdit: Boolean,
  row: Object
});

// table 欄位
const tableList = [
  {
    label: t("vp.customFields.fields.description.label"),
    value: "description",
    required: true
  },
  {
    label: t("vp.customFields.fields.cname.label"),
    value: "cname",
    required: true
  },
  {
    label: t("vp.customFields.fields.ename.label"),
    value: "ename",
    required: true
  },
  {
    label: t("vp.customFields.fields.regex.label"),
    value: "regex",
    required: true
  },
  {
    label: t("vp.customFields.fields.value.label"),
    value: "value",
    required: false
  }
];

// 正則表達式的基本結構
const commonPatterns = [
  /[\[\]\{\}\(\)\^\$\.\*\+\?\|\\]/, // 特殊字符
  /\\[dws]/, // 常見的轉義字符
  /\{[\d,]+\}/, // 重複次數
  /\[.*?\]/, // 字符類
  /\(.*?\)/ // 群組
];

// 初始化formData.dataLists
const initDataLists = () => {
  if (!formData.value.fields || formData.value.fields.length === 0) {
    formData.value.fields.push({
      id: nextId++,
      cname: "",
      ename: "",
      regex: "",
      value: "",
      description: ""
    });
  }
};

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const isMdSize = computed(() => {
  return $q.screen.lt.lg;
});

// 進階功能 Icon
const expandIcon = () => {
  if (formData.value.showMore) return "expand_less";
  else return "expand_more";
};

// 處理顯示條款說明文字
const onShowTermsInfoClick = () => {
  if (isMobile.value) {
    showMobileTermsInfo.value = true;
    showTermsInfo.value = false;
  } else {
    showTermsInfo.value = !showTermsInfo.value;
  }
};

// 顯示限定資料格式說明文字
const showCustomFieldsInfoOnClick = () => {
  if (isMobile.value) {
    showMobileCustomFieldsInfo.value = true;
    showCustomFieldsInfo.value = false;
  } else {
    showCustomFieldsInfo.value = !showCustomFieldsInfo.value;
  }
};

// 顯示 Offline、靜態 QR Code 模式說明文字
const showInfoOnClick = () => {
  if (isMobile.value) {
    showMobileQRCodeOrAppInfo.value = true;
    showQRCodeOrAppInfo.value = false;
  } else {
    showQRCodeOrAppInfo.value = !showQRCodeOrAppInfo.value;
  }
};

// 顯示Tag說明文字
const showTagInfoOnClick = () => {
  if (isMobile.value) {
    showMobileTagInfo.value = true;
    showTagInfo.value = false;
  } else {
    showTagInfo.value = !showTagInfo.value;
  }
};

// 顯示組織業務系統 URL 說明文字
const showDeeplinkUrlInfoOnClick = () => {
  if (isMobile.value) {
    showMobileDeeplinkUrlInfo.value = true;
    showDeeplinkUrlInfo.value = false;
  } else {
    showDeeplinkUrlInfo.value = !showDeeplinkUrlInfo.value;
  }
};

// 顯示 CallBack Url 說明文字
const showCallBackUrlInfoOnClick = () => {
  if (isMobile.value) {
    showMobileCallBackUrlInfo.value = true;
    showCallBackUrlInfo.value = false;
  } else {
    showCallBackUrlInfo.value = !showCallBackUrlInfo.value;
  }
};

// 顯示模組加密說明文字
const showIsEncryptEnabledInfoOnClick = () => {
  if (isMobile.value) {
    showMobileIsEncryptEnabledInfo.value = true;
    showIsEncryptEnabledInfo.value = false;
  } else {
    showIsEncryptEnabledInfo.value = !showIsEncryptEnabledInfo.value;
  }
};

// 顯示使用者條款 Dialog
const showAddUserTermsDialog = () => {
  $q.dialog({
    component: AddUserTermsDialog,
    componentProps: {
      mode: "create",
      terms: formData.value.terms
    }
  })
    .onOk((data) => {
      // 處理從 Dialog 返回的資料
      formData.value.terms = data;
    })
    .onCancel(() => {});
};

// 關閉授權條款資訊內容
const closeTermsNotice = (event) => {
  if (termsNotice.value && !termsNotice.value.contains(event.target)) {
    showTermsInfo.value = false;
  }
};

// 關閉限定資料格式資訊內容
const closeCustomFieldsNotice = (event) => {
  if (
    customFieldsNotice.value &&
    !customFieldsNotice.value.contains(event.target)
  ) {
    showCustomFieldsInfo.value = false;
  }
};

// 關閉模式 Offline、靜態 QR Code 資訊內容
const closeNotice = (event) => {
  if (notice.value && !notice.value.contains(event.target)) {
    showQRCodeOrAppInfo.value = false;
  }
};

// 關閉TAG資訊內容
const closeTagNotice = (event) => {
  if (tagNotice.value && !tagNotice.value.contains(event.target)) {
    showTagInfo.value = false;
  }
};

// 關閉組織業務系統 URL 資訊內容
const closeDeeplinkUrlNotice = (event) => {
  if (
    deeplinkUrlNotice.value &&
    !deeplinkUrlNotice.value.contains(event.target)
  ) {
    showDeeplinkUrlInfo.value = false;
  }
};

// 關閉 CallBack URL 資訊內容
const closeCallBackUrlNotice = (event) => {
  if (
    callBackUrlNotice.value &&
    !callBackUrlNotice.value.contains(event.target)
  ) {
    showCallBackUrlInfo.value = false;
  }
};

// 關閉模組加密資訊內容
const closeIsEncryptEnabledNotice = (event) => {
  if (
    isEncryptEnabledNotice.value &&
    !isEncryptEnabledNotice.value.contains(event.target)
  ) {
    showIsEncryptEnabledInfo.value = false;
  }
};

// Input 欄位檢核
const checkField = (value, row) => {
  if (value === "description") {
    return [
      (val) => {
        if (formData.value.isCustomFields) {
          return (
            (val && val.trim() !== "") ||
            t("input", { input: t("vp.customFields.fields.description.label") })
          );
        }
        return true;
      },
      (val) =>
        !val || val.length <= 12 || t("validation.maxLength", { max: 12 })
    ];
  }
  if (value === "cname") {
    return [
      (val) => {
        if (formData.value.isCustomFields) {
          return (
            (val && val.trim() !== "") ||
            t("input", { input: t("vp.customFields.fields.cname.label") })
          );
        }
        return true;
      },
      (val) =>
        !val || val.length <= 18 || t("validation.maxLength", { max: 18 }),
      (val) =>
        !val ||
        /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(val) ||
        t("vp.customFields.fields.cname.pattern")
    ];
  }

  if (value === "ename") {
    return [
      (val) => {
        if (formData.value.isCustomFields) {
          return (
            (val && val.trim() !== "") ||
            t("input", { input: t("vp.customFields.fields.ename.label") })
          );
        }
        return true;
      },
      (val) =>
        !val ||
        /^(?!id$)[a-zA-Z0-9_]+$/.test(val) ||
        t("vp.customFields.fields.ename.pattern"),
      (val) => val.length <= 50 || t("validation.maxLength", { max: 50 })
    ];
  }

  if (value === "regex") {
    return [
      (val) => {
        if (formData.value.isCustomFields) {
          return (
            (val && val.trim() !== "") ||
            t("input", { input: t("vp.customFields.fields.regex.label") })
          );
        }
        return true;
      },
      (val) => {
        if (val) {
          // 檢查是否只是純文字
          if (/^[a-zA-Z0-9]+$/.test(val)) {
            return t("vp.customFields.fields.regex.pattern");
          }

          // 檢查是否有正則表達式的基本結構
          const hasRegexPattern = commonPatterns.some((pattern) =>
            pattern.test(val)
          );

          if (!hasRegexPattern) {
            return t("vp.customFields.fields.regex.notVaild");
          }

          try {
            new RegExp(val);
            return true;
          } catch (e) {
            return t("vp.customFields.fields.regex.errorMessage");
          }
        }
      }
    ];
  }

  if (value === "value") {
    return [
      (val) =>
        !val || val.length <= 18 || t("validation.maxLength", { max: 18 }),
      (val) => {
        if (!val) return true;
        if (!row.regex) return t("vp.customFields.fields.value.regexBlank");
        try {
          const regex = new RegExp(row.regex);
          return regex.test(val) || t("vp.customFields.fields.value.notVaild");
        } catch (e) {
          return t("vp.customFields.fields.value.errorMessage");
        }
      }
    ];
  }
};

// 下一步 ＋ 檢核
const toNextStep = async () => {
  const isValid = await formRef.value.validate();
  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  }
  if (!formData.value.terms) {
    $n.error(t("input", { input: " " + t("vp.terms.label") }));
    return;
  }

  // 靜態 QR Code 模式
  if (formData.value.model.value === "1") {
    if (!formData.value.verifierServiceUrl)
      return $n.error(t("input", { input: t("vp.serviceUrl.label") }));

    if (!formData.value.callBackUrl)
      return $n.error(t("input", { input: " " + t("vp.callBackUrl.label") }));
  } else if (formData.value.model.value === "2") {
    // Offline 模式
    if (formData.value.isEncryptEnabled && !formData.value.tag)
      return $n.error(t("input", { input: " " + t("vp.tag.label") }));
  }

  return true;
};

// 處理顯示 / 關閉條款說明文字
watch(showTermsInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeTermsNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeTermsNotice);
  }
});

// 處理顯示 / 關閉模式 Offline、靜態 QR Code 說明文字
watch(showQRCodeOrAppInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeNotice);
  }
});

// 處理顯示 / 關閉TAG說明文字
watch(showTagInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeTagNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeTagNotice);
  }
});

// 處理顯示 / 關閉組織業務系統URL說明文字
watch(showDeeplinkUrlInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeDeeplinkUrlNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeDeeplinkUrlNotice);
  }
});

// 處理顯示 / 關閉 CallBack URL 說明文字
watch(showCallBackUrlInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeCallBackUrlNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeCallBackUrlNotice);
  }
});

// 處理顯示 / 關閉限定資料格式說明文字
watch(showCustomFieldsInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeCustomFieldsNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeCustomFieldsNotice);
  }
});

// 處理顯示 / 關閉模組加密說明文字
watch(showIsEncryptEnabledInfo, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      document.addEventListener("click", closeIsEncryptEnabledNotice);
    }, 0);
  } else {
    document.removeEventListener("click", closeIsEncryptEnabledNotice);
  }
});

// 若有模式，會打開進階功能
const showMoreAuto = () => {
  if (
    formData.value.model.value === "1" ||
    formData.value.model.value === "2"
  ) {
    formData.value.showMore = true;
  }
};

defineExpose({ toNextStep });

onMounted(() => {
  initDataLists();
  showMoreAuto();
});
</script>

<style scoped>
.pt-10 {
  padding-top: 8px;
}

.title-width {
  min-width: 165px;
}

.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

.q-dialog-plugin {
  max-height: 90vh;
  overflow: auto;
}

.termsNotice {
  margin-top: 8px;
  left: 163px;
  top: 33px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  position: absolute;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
}

.termsMdsizeNotice {
  margin-top: 8px;
  left: 435px;
  top: -8px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  position: absolute;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
}

.notice {
  position: absolute;
  top: -60px;
  left: 100%;
  margin-top: 8px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 380px;
}

.tagNotice {
  position: absolute;
  top: -8px;
  left: 100%;
  margin-top: 8px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 380px;
}

.deeplinkUrlNotice {
  position: absolute;
  top: 33px;
  left: 164px;
  margin-top: 8px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 470px;
}

.callBackUrlNotice {
  position: absolute;
  top: 33px;
  left: 164px;
  margin-top: 8px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 470px;
}

.customFieldsNotice {
  position: absolute;
  top: -5px;
  left: 210px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 500px;
  white-space: pre-line;
}

.isEncryptEnabledNotice {
  position: absolute;
  top: -5px;
  left: 210px;
  background-color: grey;
  border-radius: 5px;
  padding: 5px 10px;
  white-space: normal;
  z-index: 10;
  word-break: break-word;
  width: max-content;
  max-width: 500px;
}
</style>
