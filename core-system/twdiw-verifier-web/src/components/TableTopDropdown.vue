<template>
  <div class="row full-width justify-between items-end q-pb-sm">
    <div class="col-grow">
      <div class="titleRwd q-my-md">{{ title }}</div>
      <div class="row full-width justify-between items-center">
        <div class="row items-center">
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
                      <div :="field.col" v-if="field.state === 'input'">
                        <q-input
                          v-model="model[field.paramsName]"
                          :label="field.label"
                          :class="field.class"
                          outlined
                          dense
                          :rules="field.rules"
                        ></q-input>
                      </div>

                      <template v-if="field.state === 'dateRange'">
                        <div class="row full-width">
                          <div class="col-12 col-md-6">
                            <q-input
                              v-model="model[field.paramsName[0]]"
                              :label="field.labels.start"
                              outlined
                              dense
                              type="datetime"
                              :class="isMobile ? 'q-pb-md' : 'q-pr-md'"
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
  defineExpose,
  defineModel,
  toRefs,
  watch
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
    startDate: "",
    endDate: ""
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
  width: isMobile.value ? "300px" : "300px",
  maxWidth: "100%",
  textTransform: "none"
}));

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

// 驗證表單並更新篩選條件
const submitSearch = async (event) => {
  const isValid = await searchFormRef.value.validate();

  if (model.value.startDate && model.value.endDate) {
    const startDate = new Date(model.value.startDate);
    const endDate = new Date(model.value.endDate);

    // 判斷日期是否有效
    if (startDate > endDate) {
      $n.error(t("dateField.errors.startDateAfterEndDate"));
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
</script>
