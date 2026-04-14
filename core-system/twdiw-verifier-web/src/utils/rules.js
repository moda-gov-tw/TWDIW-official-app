import { useI18n } from "vue-i18n";

export const useRulesAll = () => {
  const { t } = useI18n();

  const rulesAll = (ruleName) => {
    switch (ruleName) {
      case "dateRules":
        return [
          (val) => {
            if (!val) return true;

            const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(val);
            if (!match) return t("dateField.rules.format");

            const year = Number(match[1]);
            const month = Number(match[2]);
            const day = Number(match[3]);

            if (month < 1 || month > 12) return t("dateField.rules.invalid");
            const maxDay = new Date(year, month, 0).getDate();
            if (day < 1 || day > maxDay) return t("dateField.rules.invalid");

            return true;
          }
        ];

      // 如果沒有匹配的規則名稱，返回空數組
      default:
        return [];
    }
  };

  return { rulesAll };
};
