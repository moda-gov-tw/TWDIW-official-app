<template>
  <q-dialog
    v-model="model"
    transition-show="fade"
    transition-hide="fade"
    :persistent="persistent"
    backdrop-filter="blur(4px) saturate(150%)"
    @before-show="onBeforeShow"
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1000px; width: 560px"
      ><q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <slot name="header">
          <span class="text-h6 text-weight-bold">{{
            header || "default header"
          }}</span>
          <q-space></q-space>
          <q-btn flat fab-mini icon="close" v-close-popup />
        </slot>
      </q-card-section>
      <slot>
        <p>default content</p>
      </slot>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { defineProps, defineModel, defineEmits } from "vue";
const props = defineProps(["header", "persistent"]);
const model = defineModel();
const emit = defineEmits(["beforeShow"]);

const onBeforeShow = (event) => {
  emit("beforeShow", event);
};
</script>
