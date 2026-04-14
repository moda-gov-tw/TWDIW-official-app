<template>
  <q-input
    ref="input"
    flat
    :dense="notFalse(props.dense)"
    :label="props.label"
    :hide-bottom-space="true"
    v-model="date"
    mask="date"
    lazy-rules="ondemand"
    placeholder="請點擊右側月曆圖示選擇日期"
    readonly
    :error="hasError"
    class="no-read"
  >
    <template #append>
      <q-btn
        v-if="notFalse(props.clearable)"
        v-show="canclear"
        color="grey"
        flat
        round
        dense
        padding="none"
        icon="cancel"
        class="cursor-pointer"
        @click="reset"
      />

      <q-btn
        round
        flat
        dense
        text-color="accent"
        icon="calendar_month"
        class="cursor-pointer"
      >
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-date
            :minimal="notFalse(props.minimal)"
            :subtitle="props.label"
            v-model="date"
            :options="optionsFn"
          >
            <div class="row items-center">
              <q-icon name="question_mark" class="cursor-question">
                <q-tooltip class="text-body2">
                  單點月曆中的日期選擇，再次點擊已選擇的日期{{
                    notFalse(props.clearable) ? "或點擊重置" : ""
                  }}可取消選擇
                </q-tooltip>
              </q-icon>
              <q-space></q-space>
              <q-btn
                v-if="notFalse(props.clearable)"
                :label="t('reset')"
                color="grey"
                flat
                @click="date = ''"
                :ripple="false"
              />
              <q-btn v-close-popup :label="t('close')" color="primary" flat />
            </div>
          </q-date>
        </q-popup-proxy>
      </q-btn>
    </template>
  </q-input>
</template>

<script setup>
import { computed } from "vue";
import { watch, defineModel } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const input = $ref(null);
const date = defineModel();
const props = defineProps([
  "label",
  "minimal",
  "clearable",
  "required",
  "dateRules"
]);

let canclear = $ref(false);

watch(date, (current, _) => {
  canclear = !!current;
});

let hasError = computed(() => {
  return props.required && !date.value;
});

const reset = () => {
  date.value = "";
  input.resetValidation();
};

const notFalse = (val) => {
  if (val === undefined) return false;
  return val !== false || val !== "false";
};

// 日期條件 toDayAfter(今天之後的日期可選)
const optionsFn = (date) => {
  if (props.dateRules === "toDayAfter") {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const comparisonDate = new Date(date);
    comparisonDate.setHours(0, 0, 0, 0);

    return comparisonDate >= today;
  }

  return true;
};
</script>
