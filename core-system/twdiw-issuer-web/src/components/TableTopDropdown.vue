<template>
  <div
    :class="
      title === t('vcSchema.createTitle')
        ? 'row items-center justify-between'
        : ''
    "
  >
    <div class="row q-ma-none q-pa-none items-center">
      <div class="titleRwd q-mb-md">{{ title }}</div>
      <q-space></q-space>
      <div class="q-px-sm"></div>
    </div>
  </div>
  <div class="row full-width justify-between items-end">
    <div class="col-grow">
      <div class="row full-width justify-between items-center">
        <div class="row items-center col-grow">
          <q-btn-dropdown
            color="white"
            text-color="black"
            class="search-dropdown ellipsis-label"
            flat
            dropdown-icon="none"
            rounded
            ref="dropdownRef"
            :menu-offset="menuOffset"
            :style="dropdownStyle"
          >
            <template v-slot:label>
              <div class="ellipsis-wrapper">
                <q-icon name="search" class="q-mr-sm" />
                <span class="ellipsis-text">{{
                  displaySearchQuery || t("searchCriteria")
                }}</span>
              </div>
            </template>

            <q-form
              ref="searchFormRef"
              @submit="submitSearch"
              @keydown.enter.prevent
            >
              <q-card :style="cardWidth">
                <q-card-section>
                  <div class="row">
                    <template
                      v-for="field in inputArray"
                      :key="field.paramsName"
                    >
                      <!-- 文字 -->
                      <div
                        :class="[
                          field.col,
                          title === t('remove.title') &&
                          field.label === t('vcSchema.table.serialNo')
                            ? 'q-pb-sm'
                            : field.label !== t('remove.status.label') &&
                              field.label !== t('data') &&
                              title === t('remove.title')
                            ? 'q-pb-md'
                            : ''
                        ]"
                        v-if="field.state === 'input'"
                      >
                        <q-input
                          v-model="model[field.paramsName]"
                          :label="field.label"
                          :class="field.class"
                          outlined
                          dense
                          :rules="field.rules"
                          hide-bottom-space
                        ></q-input>
                      </div>

                      <!-- 特殊的 orgId -->
                      <div
                        :class="[
                          field.col,
                          title === t('remove.title') &&
                          field.label === t('vcSchema.table.serialNo')
                            ? 'q-pb-sm'
                            : field.label !== t('remove.status.label') &&
                              field.label !== t('data') &&
                              title === t('remove.title')
                            ? 'q-pb-md'
                            : ''
                        ]"
                        v-if="field.state === 'orgInput'"
                      >
                        <q-input
                          v-model="model[field.paramsName]"
                          :label="field.label"
                          :class="field.class"
                          :disable="field.disable"
                          outlined
                          dense
                          :rules="field.rules"
                          :model-value="field.defaultValue.orgTwName"
                          @update:model-value="
                            (val) => {
                              model[field.paramsName] = field.defaultValue;
                            }
                          "
                        />
                      </div>

                      <div
                        :class="[
                          field.col,
                          title === t('remove.title') &&
                          field.label === t('vcSchema.table.serialNo')
                            ? 'q-pb-sm'
                            : field.label !== t('remove.status.label') &&
                              field.label !== t('data') &&
                              title === t('remove.title')
                            ? 'q-pb-md'
                            : ''
                        ]"
                        v-if="field.state === 'select'"
                      >
                        <!-- 選項 -->
                        <q-select
                          v-model="model[field.paramsName]"
                          :label="field.label"
                          :options="field.options"
                          :class="[
                            field.class,
                            title === t('remove.title') &&
                            field.label === t('vcSchema.table.serialNo')
                              ? 'q-pb-sm'
                              : ''
                          ]"
                          :loading="field?.loading"
                          :disable="field?.loading"
                          :use-input="field?.useInput"
                          :hide-selected="field?.hideSelected"
                          :fill-input="field?.fillInput"
                          :input-debounce="field?.inputDebounce"
                          :option-label="field?.optionLabel"
                          :option-value="field?.value"
                          :emit-value="field?.emitValue"
                          :map-options="field?.mapOptions"
                          @filter="
                            (val, update) => handleFilter(val, update, field)
                          "
                          :model-value="model[field.paramsName]"
                          outlined
                          dense
                          menu-position="bottom"
                          :popup-content-class="'no-modal'"
                          behavior="menu"
                          :rules="field.rules"
                        >
                          <template
                            v-slot:option="scope"
                            v-if="field.label === t('vcSchema.table.serialNo')"
                          >
                            <q-item v-bind="scope.itemProps">
                              <q-item-section>
                                <q-item-label>
                                  {{ scope.opt.orgId }} -
                                  {{ scope.opt.orgName }}
                                </q-item-label>
                                <q-item-label caption>
                                  {{ scope.opt.vcItemSerialNo }}
                                  <template v-if="isMobile"><br /></template>
                                  <template v-else>{{ " - " }}</template>
                                  {{ scope.opt.vcItemName }}
                                </q-item-label>
                              </q-item-section>
                            </q-item>
                          </template>
                          <!-- 添加無選項時的插槽 -->
                          <template
                            v-slot:no-option
                            v-if="field.paramsName === 'vcSerialNo'"
                          >
                            <q-item>
                              <q-item-section class="text-grey">
                                {{ t("noData") }}
                              </q-item-section>
                            </q-item>
                          </template>
                        </q-select>
                        <!-- 日期 -->
                      </div>

                      <!-- 日期範圍選擇器  q-pb-md-->
                      <template v-if="field.state === 'dateRange'">
                        <div
                          class="row full-width"
                          :class="
                            title === t('vcSchema.createTitle') ? 'q-pb-md' : ''
                          "
                        >
                          <div class="col-12 col-md-6">
                            <q-input
                              v-model="model[field.paramsName[0]]"
                              :label="field.labels.start"
                              outlined
                              dense
                              type="datetime"
                              :class="isMobile ? 'q-pb-md' : 'q-pr-md'"
                              :rules="field.rules"
                            >
                              <template v-slot:append>
                                <q-icon name="event" class="cursor-pointer">
                                  <q-popup-proxy
                                    cover
                                    transition-show="scale"
                                    transition-hide="scale"
                                  >
                                    <q-date
                                      v-model="model[field.paramsName[0]]"
                                      mask="YYYY-MM-DD"
                                      today-btn
                                      minimal
                                      :model-value="
                                        model[field.paramsName[0]] || ''
                                      "
                                    >
                                      <div class="row items-center justify-end">
                                        <q-btn
                                          v-close-popup
                                          :label="t('close')"
                                          color="primary"
                                          flat
                                        />
                                      </div>
                                    </q-date>
                                  </q-popup-proxy>
                                </q-icon>
                              </template>
                            </q-input>
                          </div>
                          <div class="col-12 col-md-6">
                            <q-input
                              v-model="model[field.paramsName[1]]"
                              :label="field.labels.end"
                              outlined
                              dense
                              type="datetime"
                              :rules="field.rules"
                            >
                              <template v-slot:append>
                                <q-icon name="event" class="cursor-pointer">
                                  <q-popup-proxy
                                    cover
                                    transition-show="scale"
                                    transition-hide="scale"
                                  >
                                    <q-date
                                      v-model="model[field.paramsName[1]]"
                                      mask="YYYY-MM-DD"
                                      minimal
                                      today-btn
                                      :model-value="
                                        model[field.paramsName[1]] || ''
                                      "
                                    >
                                      <div class="row items-center justify-end">
                                        <q-btn
                                          v-close-popup
                                          :label="t('close')"
                                          color="primary"
                                          flat
                                        />
                                      </div>
                                    </q-date>
                                  </q-popup-proxy>
                                </q-icon>
                              </template>
                            </q-input>
                          </div>
                        </div>
                      </template>
                    </template>
                  </div>
                  <div class="row justify-end q-gutter-sm">
                    <q-btn
                      :label="t('resetLabel')"
                      outline
                      class="text-primary"
                      @click="resetForm"
                    />
                    <q-btn
                      unelevated
                      :label="t('filterLabel')"
                      color="primary"
                      type="submit"
                    />
                  </div>
                </q-card-section>
              </q-card>
            </q-form>
          </q-btn-dropdown>
        </div>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script setup>
import {
  ref,
  defineProps,
  computed,
  defineEmits,
  defineModel,
  toRefs,
  onMounted
} from "vue";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";
import { useQuasar } from "quasar";

const $q = useQuasar();
const $n = useNotify();
const { t } = useI18n();

const model = defineModel({
  default: () => ({
    cname: "",
    ename: "",
    visible: null,

    name: "",
    description: "",

    serialNo: "",
    orgTwName: "",
    startDate: "",
    endDate: "",

    filterFunction: Function
  })
});

const props = defineProps({
  title: String,
  inputArray: Array
});

const { title, inputArray } = toRefs(props);

const emit = defineEmits(["submit", "reset"]);

// 下拉選單樣式設定
const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: isMobile.value ? "100%" : "300px",
  maxWidth: "100%",
  textTransform: "none"
}));

const cardWidth = computed(() => {
  const narrowWidthPages = [
    t("normalFieldsTitle"),
    t("basicFieldsTitle"),
    t("regularExpressionTitle")
  ];

  return {
    width: isMobile.value
      ? "100%"
      : narrowWidthPages.includes(title.value)
      ? "300px"
      : "660px"
  };
});

const menuOffset = computed(() => {
  return isMobile.value ? [150, 8] : [0, 0]; // [x偏移, y偏移]
});

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 表單和下拉選單參考
const searchFormRef = ref(null);
const dropdownRef = ref(null);
const displaySearchQuery = ref(t("searchCriteria"));
const showMobileInfo = ref(false);

const toggleInfo = () => {
  if (deviceType.value) {
    showMobileInfo.value = true;
  }
};

const handleFilter = (val, update, field) => {
  if (field?.filterFn && typeof field.filterFn === "function") {
    field.filterFn(val, update, field.paramsName);
  } else {
    // 預設行為
    update(() => {
      // 默認過濾邏輯
    });
  }
};

// 驗證表單並更新篩選條件
const submitSearch = async (event) => {
  const isValid = await searchFormRef.value.validate();

  if (model.value.startDate && model.value.endDate) {
    const startDate = new Date(model.value.startDate);
    const endDate = new Date(model.value.endDate);

    if (startDate > endDate) {
      $n.error(t("valid.date"));
      return;
    }
  }

  if (isValid) {
    const filteredModel = Object.fromEntries(
      Object.entries(model.value).filter(([_, value]) => {
        if (typeof value === "string") return value.trim() !== "";
        if (value === null || value === undefined) return false;
        return true;
      })
    );

    if (
      filteredModel.vcSerialNo &&
      typeof filteredModel.vcSerialNo === "object"
    ) {
      displaySearchQuery.value = filteredModel.vcSerialNo.vcItemSerialNo;
    } else if (filteredModel.vcSerialNo) {
      displaySearchQuery.value = filteredModel.vcSerialNo;
    } else if (filteredModel.orgTwName) {
      displaySearchQuery.value = filteredModel.orgTwName;
    } else {
      displaySearchQuery.value =
        filteredModel?.cname ||
        filteredModel?.ename ||
        filteredModel.visible?.label ||
        filteredModel?.name ||
        filteredModel?.description ||
        filteredModel.type?.label ||
        filteredModel?.serialNo ||
        (filteredModel.startDate || filteredModel.endDate
          ? t("rangeTime")
          : "") ||
        filteredModel.orgId?.orgTwName ||
        filteredModel.state?.label ||
        (filteredModel?.content ? t("data") : "") ||
        t("searchCriteria");
    }

    emit("submit", filteredModel);

    dropdownRef.value.hide();
  }
};

// 重置所有搜尋條件和篩選狀態
const resetForm = () => {
  displaySearchQuery.value = t("searchCriteria");
  model.value = {};
  emit("reset");
};

// 設置裝置類型的 ref
const deviceType = ref(null);

const checkDevice = () => {
  const userAgent = navigator.userAgent.toLowerCase();

  return {
    isIOS: /iphone|ipad|ipod/.test(userAgent),
    isAndroid: /android/.test(userAgent)
  };
};

onMounted(() => {
  const { isAndroid, isIOS } = checkDevice();

  if (isIOS) {
    deviceType.value = "ios";
    console.log("iOS 設備");
  } else if (isAndroid) {
    deviceType.value = "android";
    console.log("Android 設備");
  } else {
    deviceType.value = "other";
    console.log("其他設備");
  }
});
</script>
