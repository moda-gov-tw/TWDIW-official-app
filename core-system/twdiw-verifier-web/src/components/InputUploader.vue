<template>
  <q-btn
    outline
    color="primary"
    dense
    icon="upload"
    :label="props.label"
    class="q-pa-sm q-ma-none"
    @click="qfile.pickFiles()"
  >
  </q-btn>
  <q-file
    class="hidden"
    v-show="false"
    @update:model-value="upload"
    ref="qfile"
    color="primary"
    v-model="file"
    :accept="props.accept"
  />
</template>
<script setup>
import { ref, defineProps } from "vue";
import { useNotify } from "src/utils/plugin";

const props = defineProps(["label", "accept"]);

const qfile = ref(null);
const $n = useNotify();

const upload = () => {
  $n.custom({
    message: "上傳檔案中，請稍候...",
    spinner: true,
    type: "info",
    timeout: 1300
  });
  setTimeout(() => {
    $n.success("上傳成功");
  }, 1500);
  file.value = null;
};

const file = ref(null);
</script>
