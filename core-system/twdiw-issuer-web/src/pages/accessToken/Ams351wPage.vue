<template>
  <q-page class="full-height q-pa-sm">
    <div
      class="full-height bg-white shadow-3 rounded-borders q-pa-sm overflow-auto"
    >
      <q-splitter
        v-model="splitterModel"
        class="q-pa-sm fit"
        :limits="[50, 100]"
      >
        <template #before>
          <div class="q-pa-md">
            <AccessTokenTable @action-click="openDetail" />
          </div>
        </template>
        <template #separator>
          <q-avatar
            rounded
            color="primary"
            text-color="white"
            size="30px"
            :icon="
              splitterModel < 100
                ? 'drag_indicator'
                : 'keyboard_double_arrow_left'
            "
            :ondblclick="onDragDbClick"
            @click="onDragClick"
          />
        </template>
        <template #after>
          <AccessTokenDetail @detail-click="onDragDbClick" />
        </template>
      </q-splitter>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from "vue";
import AccessTokenTable from "./AccessTokenTable.vue";
import AccessTokenDetail from "./AccessTokenDetail.vue";

const splitterModel = ref(100);

const openDetail = () => {
  onDragClick();
};

const onDragDbClick = () => {
  const current = splitterModel.value;
  if (current < 100) {
    splitterModel.value = 100;
  } else {
    splitterModel.value = 70;
  }
};

const onDragClick = () => {
  const current = splitterModel.value;
  if (current < 100) {
    return;
  }

  splitterModel.value = 70;
};
</script>
