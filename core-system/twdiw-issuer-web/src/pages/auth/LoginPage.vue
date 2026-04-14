<template>
  <q-page class="column justify-between items-center q-px-md q-pb-sm">
    <div class="column items-center">
      <div class="q-py-lg"></div>
      <!-- 發行端模組系統 -->
      <h3 class="text-center text-grey-5 font-title non-selectable">
        {{ t("login.title") }}
      </h3>
      <LoginCard />
    </div>

    <ManagementInfo />
  </q-page>
</template>
<script setup>
import LoginCard from "./LoginCard.vue";
import ManagementInfo from "src/components/ManagementInfo.vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
</script>
<style scoped>
.font-title {
  font-size: 40px;
  font-weight: 700;
}
</style>
