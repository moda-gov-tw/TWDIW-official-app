<template>
  <q-menu touch-position context-menu>
    <q-list dense>
      <q-item
        v-for="action in actionList"
        :key="action.label"
        clickable
        v-close-popup
        @click="action.method(props.data)"
      >
        <q-item-section avatar>
          <q-icon :color="action.color || 'secondary'" :name="action.icon" />
        </q-item-section>
        <q-item-section>
          <q-item-label>{{
            `${action.label}${action.batch === true ? t("batchable") : ""}`
          }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-menu>
</template>
<script setup>
import { computed, defineProps } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const props = defineProps(["actions", "data"]);
const actionList = computed(() =>
  props.actions.filter((e) => isVisible(e.visible))
);

const isVisible = (e) => {
  return typeof e === "function" ? e(props.data) : !(e === false);
};
</script>
