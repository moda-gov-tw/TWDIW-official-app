import { api } from "src/boot/axios";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";

export const useManualDownloader = () => {
  const { t } = useI18n();
  const $n = useNotify();

  const downloadManualFile = async () => {
    try {
      const response = await api.get("/api/info/manual", {
        responseType: "blob"
      });

      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = t("manualPdf");
      document.body.appendChild(link);
      link.click();

      // 清理資源
      URL.revokeObjectURL(url);
      link.remove();
      $n.success(t("message.success.download"));
    } catch (error) {
      console.error("Fail to get manual file:", error);
      $n.error(t("message.error.download"));
    }
  };

  return { downloadManualFile };
};
