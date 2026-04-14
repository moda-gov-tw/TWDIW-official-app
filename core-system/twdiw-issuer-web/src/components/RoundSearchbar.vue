<template>
  <div
    class="q-px-sm row items-center justify-between"
    style="border-radius: 30px; border: 1px lightgrey solid; min-width: 600px"
  >
    <q-btn flat round>
      <q-icon name="search" size="sm" @click="onIconClick" />
    </q-btn>
    <div class="q-pl-sm col">
      <q-input
        v-model="query"
        class="font-lg"
        borderless
        dense
        :placeholder="props.placeholder || '請輸入關鍵字'"
        clearable
      />
    </div>
    <q-btn-dropdown
      ref="dropDown"
      flat
      rounded
      dropdown-icon="tune"
      fab-mini
      no-icon-animation
      transition-show="jump-down"
      transition-hide="jump-up"
      menu-self="top right"
    >
      <div style="width: 600px; min-height: 200px">
        <slot name="form" />
      </div>
    </q-btn-dropdown>
  </div>
</template>
<script setup>
import { ref } from "vue";
const emit = defineEmits(["search"]);
const props = defineProps(["placeholder"]);
let query = defineModel();

const dropDown = ref(null);

const onIconClick = () => {
  emit("search", query.value);
};

const closeDropDown = () => {
  dropDown.value.hide();
};

defineExpose({ closeDropDown });
</script>
