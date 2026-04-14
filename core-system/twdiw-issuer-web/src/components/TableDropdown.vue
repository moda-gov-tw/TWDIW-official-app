<template>
  <q-btn-dropdown
    color="white"
    text-color="black"
    class="search-dropdown ellipsis-label"
    flat
    dropdown-icon="none"
    rounded
    :style="dropdownStyle"
    :menu-offset="menuOffset"
    ref="dropdownRef"
  >
    <template v-slot:label>
      <div class="ellipsis-wrapper">
        <q-icon name="search" class="q-mr-sm" />
        <span class="ellipsis-text">{{ label || "請輸入欄位名稱" }}</span>
      </div>
    </template>
    <slot> </slot>
  </q-btn-dropdown>
</template>
<script setup>
import { ref, defineProps, computed, defineExpose } from "vue";
import { useQuasar } from "quasar";

const $q = useQuasar();
const dropdownRef = ref(null);

const props = defineProps(["label"]);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const menuOffset = computed(() => {
  return isMobile.value ? [150, 8] : [0, 0]; // [x偏移, y偏移]
});

const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: isMobile.value ? "100%" : "300px",
  maxWidth: "100%",
  textTransform: "none"
}));

const hide = () => {
  dropdownRef.value.hide();
};

defineExpose({ hide });
</script>
<style scoped>
.ellipsis-label .ellipsis-wrapper {
  max-width: calc(100% - 24px);
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
}

.ellipsis-text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
