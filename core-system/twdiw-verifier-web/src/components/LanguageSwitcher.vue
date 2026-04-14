<template>
  <q-select
    v-model="locale"
    :options="localeOptions"
    label="Language"
    dense
    borderless
    emit-value
    map-options
    options-dense
    style="min-width: 150px"
  />
</template>
<script setup>
import { watch } from "vue";
import { useI18n } from "vue-i18n";

const { locale } = useI18n({ useScope: "global" });

watch(locale, (newLocale) => {
  locale.value = newLocale;
});

const localeOptions = [
  { value: "zh-TW", label: "中文" },
  { value: "en-US", label: "English" }
];
</script>
