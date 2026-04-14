<template>
  <div
    v-if="verifiermgrVersionInfo"
    class="version-info text-grey-3 text-caption q-pa-sm"
  >
    {{ verifiermgrVersionInfo }}
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { api } from "src/boot/axios";
import { useMeta } from "quasar";

// 顯示於畫面使用
const verifiermgrVersionInfo = ref("");
const versionInfo = ref();

const emit = defineEmits(["env-loaded"]);

// 初始化 meta
useMeta(() => {
  if (!versionInfo.value) return {};

  return {
    meta: [
      {
        name: "verifier.oid4vp.version",
        content:
          `${versionInfo.value.oid4vp?.version || ""}_${
            versionInfo.value.oid4vp?.abbrev || ""
          }` || ""
      },
      {
        name: "verifier.vp.version",
        content:
          `${versionInfo.value.vp?.version || ""}_${
            versionInfo.value.vp?.abbrev || ""
          }` || ""
      }
    ]
  };
});

onMounted(async () => {
  try {
    const response = await api.get("/api/info/version");
    if (response.data && Object.keys(response.data).length > 0) {
      const responseData = response.data.data;
      const abbrev = responseData.verifiermgr?.abbrev || "";
      const version = responseData.verifiermgr?.version || "";
      const apiVersion = responseData.verifiermgr?.apiVersion || "";
      const currentEnv = responseData.env || "";
      const appDownloadDate = responseData.appDownloadDate || "";

      verifiermgrVersionInfo.value = `v${version}_${abbrev}(API: ${apiVersion})`;
      versionInfo.value = responseData;

      emit("env-loaded", currentEnv, appDownloadDate);
    }
  } catch (error) {
    console.error("Failed to load management info: ", error);
  }
});
</script>
<style scoped>
.version-info {
  align-self: flex-end;
  text-align: right;
  width: 100%;
  max-width: 90vw;
  white-space: normal;
  word-break: break-word;
}
</style>
