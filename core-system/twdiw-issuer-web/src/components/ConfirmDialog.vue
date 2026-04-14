<template>
  <q-dialog v-model="dialogModel" persistent>
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ title }}</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <div class="text-content word-break">
          {{ content }}
        </div>
      </q-card-section>

      <q-card-actions align="right" class="text-primary">
        <q-btn
          unelevated
          :label="cancelText"
          outline
          class="text-primary"
          :disable="confirmLoading"
          @click="onCancelClick"
        />
        <q-btn
          unelevated
          :label="confirmText"
          color="primary"
          @click="onConfirmClick"
          :disable="confirmLoading"
          :loading="confirmLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, toRefs, watch } from "vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: "確認"
  },
  content: {
    type: String,
    required: true
  },
  confirmText: {
    type: String,
    default: "確認"
  },
  cancelText: {
    type: String,
    default: "取消"
  },
  confirmLoading: {
    type: Boolean,
    default: false
  }
});

const { confirmLoading } = toRefs(props);

const emit = defineEmits(["update:modelValue", "confirm", "cancel"]);

const dialogModel = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value)
});

const onConfirmClick = () => {
  emit("confirm");
};

const onCancelClick = () => {
  if (!confirmLoading.value) {
    emit("cancel");
    emit("update:modelValue", false);
  }
};

watch(confirmLoading, (newVal, oldVal) => {
  if (oldVal && !newVal) {
    emit("update:modelValue", false);
  }
});
</script>
