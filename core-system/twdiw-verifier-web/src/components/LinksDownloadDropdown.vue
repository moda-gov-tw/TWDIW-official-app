<template>
  <q-btn-dropdown
    fab-mini
    dropdown-icon="link"
    no-icon-animation
    style="color: #4e61a7; border: 0px solid"
  >
    <q-list>
      <div style="width: 14rem">
        <q-separator />
        <q-item
          clickable
          v-close-popup
          tabindex="0"
          @click="handleDownload"
          :loading="isDownloadManualFileLoading"
        >
          <q-item-section>
            <q-item-label>
              <q-icon size="xs" class="q-mr-xs" name="save_alt" />{{
                t("downloadManual")
              }}
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-separator />
      </div>
    </q-list>
  </q-btn-dropdown>
</template>

<script setup>
import { ref } from "vue";
import { useManualDownloader } from "src/utils/downloadFile";
const { downloadManualFile } = useManualDownloader();
const isDownloadManualFileLoading = ref(false);
import { useI18n } from "vue-i18n";

const { t } = useI18n();

// 下載操作手冊
const handleDownload = async () => {
  isDownloadManualFileLoading.value = true;
  try {
    await downloadManualFile();
  } finally {
    isDownloadManualFileLoading.value = false;
  }
};
</script>
