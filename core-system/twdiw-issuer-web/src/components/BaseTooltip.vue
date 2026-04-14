<template>
  <div
    ref="wrapper"
    :style="{
      position: 'relative',
      display: props.fullWidth ? 'flex' : 'inline-flex',
      alignItems: 'center',
      width: props.fullWidth && isMobile ? '100%' : 'auto'
    }"
  >
    <!-- trigger -->
    <span
      @click.stop="toggle"
      :style="props.fullWidth && isMobile ? 'width: 100%' : 'auto'"
    >
      <slot name="trigger">
        <q-icon
          :name="icon"
          :color="color"
          :size="size"
          class="cursor-pointer"
          v-bind="iconProps"
        />
      </slot>
    </span>

    <!-- tooltip -->
    <transition name="fade">
      <div
        v-if="visible && !isMobile"
        :style="tooltipStyle"
        class="base-tooltip"
      >
        <slot>{{ text }}</slot>
      </div>
    </transition>

    <!-- dialog(小螢幕) -->
    <q-dialog v-model="dialogVisible">
      <q-card style="min-width: 300px">
        <q-card-section>
          <div class="text-h6">{{ title }}</div>
        </q-card-section>
        <q-card-section style="white-space: pre-wrap">
          <slot> {{ text }}</slot>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn outline :label="t('close')" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";

const props = defineProps({
  icon: { type: String, default: "" },
  color: { type: String, default: "primary" },
  size: { type: String, default: "sm" },
  title: { type: String, default: "" },
  text: { type: String, default: "" },
  position: { type: Object, default: () => ({ top: "100%", left: "0" }) },
  iconProps: { type: Object, default: () => ({}) },
  width: { type: String, default: "" },
  fullWidth: { type: Boolean, default: false }
});

const { t } = useI18n();
const $q = useQuasar();
const visible = ref(false);
const wrapper = ref(null);
const dialogVisible = ref(false);
const tooltipId = Math.random().toString(36);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 點擊 icon
const toggle = () => {
  if (isMobile.value) {
    dialogVisible.value = true;
    return;
  }

  window.dispatchEvent(
    new CustomEvent("base-tooltip-open", { detail: tooltipId })
  );

  visible.value = !visible.value;
};

// 點擊空白關閉
const closeTooltip = (e) => {
  if (wrapper.value && !wrapper.value.contains(e.target)) {
    visible.value = false;
  }
};

// 全局監聽，關閉其他 tooltip
const handleGlobalOpen = (e) => {
  if (e.detail !== tooltipId) {
    visible.value = false;
  }
};
onMounted(() => {
  document.addEventListener("click", closeTooltip);
  window.addEventListener("base-tooltip-open", handleGlobalOpen);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeTooltip);
  window.removeEventListener("base-tooltip-open", handleGlobalOpen);
});

const tooltipStyle = computed(() => ({
  position: "absolute",
  zIndex: 999,
  backgroundColor: "grey",
  color: "#fff",
  padding: "5px",
  borderRadius: "5px",
  fontSize: "0.85rem",
  ...(props.width ? { width: props.width } : { width: "max-content" }),
  ...props.position
}));
</script>

<style scoped>
.base-tooltip {
  transition: opacity 0.2s ease, transform 0.2s ease;
  opacity: 1;
  white-space: pre-wrap;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: translateY(0);
}
</style>
