import { useI18n } from "vue-i18n";

export const useRulesAll = () => {
  const { t } = useI18n();

  const rulesAll = (ruleName) => {
    switch (ruleName) {
      case "cnameRules":
        return [
          (val) =>
            !val ||
            /^[a-zA-Z0-9\u4e00-\u9fa5_]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowedAnd", { symbols: "_" }),
          (val) => !val || val.length <= 18 || t("valid.maxLength", { max: 18 })
        ];

      case "enameRules":
        return [
          (val) =>
            !val ||
            /^[a-zA-Z0-9_]+$/.test(val) ||
            t("valid.onlyEnNumAllowedAnd", { symbols: "_" }),
          ,
          (val) => !val || val.length <= 50 || t("valid.maxLength", { max: 50 })
        ];

      case "cnameCreateRules":
        return [
          (val) =>
            !!val || t("input", { input: t("normalFields.dialog.cname") }),
          (val) => val.length <= 18 || t("valid.maxLength", { max: 18 }),
          (val) =>
            /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowedAnd", { symbols: "_" })
        ];

      case "enameCreateRules":
        return [
          (val) =>
            !!val || t("input", { input: t("normalFields.dialog.ename") }),
          (val) => val.length <= 50 || t("valid.maxLength", { max: 50 }),
          (val) =>
            /^(?!id$)[a-zA-Z0-9_]+$/.test(val) ||
            t("vcSchema.dialog.table.ename.format")
        ];

      case "regularExpressionRules":
        return [
          (val) =>
            !!val ||
            t("input", { input: t("normalFields.dialog.regularExpression") })
        ];

      case "regularExpressionDropdownNameRules":
        return [
          (val) =>
            !val || val.length <= 40 || t("valid.maxLength", { max: 40 }),
          (val) =>
            !val ||
            /^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowed")
        ];

      case "regularExpressionNameRules":
        return [
          (val) =>
            !!val || t("input", { input: t("regularExpression.table.name") }),
          (val) => val.length <= 40 || t("valid.maxLength", { max: 40 }),
          (val) =>
            /^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowed")
        ];

      case "regularExpressionDropdownDescriptionRules":
        return [
          (val) => !val || val.length <= 30 || t("valid.maxLength", { max: 30 })
        ];

      case "vcSchemaSerialNoRules":
        return [
          (val) =>
            !val ||
            /^[a-z0-9_]+$/.test(val) ||
            t("vcSchema.dialog.serialNo.rules.format"),
          (val) => !val || val.length <= 35 || t("valid.maxLength", { max: 35 })
        ];

      case "vcSchemaNameRules":
        return [
          (val) =>
            !val ||
            /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowed"),
          (val) => !val || val.length <= 19 || t("valid.maxLength", { max: 19 })
        ];

      case "contentRules":
        return [
          (val) => !val || val.length <= 18 || t("valid.maxLength", { max: 18 })
        ];

      case "vcSchemaOrgTwNameRules":
        return [
          (val) =>
            !val ||
            /^[\u4E00-\u9FFF\u3400-\u4DBF0-9a-zA-Z.]+$/.test(val) ||
            t("valid.onlyZhEnNumAllowedAnd", { symbols: "." })
        ];

      case "dateRules":
        return [
          (val) => {
            if (!val) return true;

            const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(val);
            if (!match) return t("valid.dateFormat");

            const year = Number(match[1]);
            const month = Number(match[2]);
            const day = Number(match[3]);

            if (month < 1 || month > 12) return t("valid.dateInvalid");
            const maxDay = new Date(year, month, 0).getDate();
            if (day < 1 || day > maxDay) return t("valid.dateInvalid");

            return true;
          }
        ];

      case "dataTagRules":
        return [
          (val) =>
            !val || /^[a-zA-Z0-9]+$/.test(val) || t("valid.onlyEnNumAllowed"),
          (val) =>
            !val || val.length <= 255 || t("valid.maxLength", { max: 255 })
        ];

      case "transactionIdRules":
        return [
          (val) =>
            !val ||
            /^[a-zA-Z0-9-]+$/.test(val) ||
            t("valid.onlyEnNumAllowedAnd", { symbols: "-" }),
          (val) => !val || val.length <= 50 || t("valid.maxLength", { max: 50 })
        ];

      // 必填欄位驗證
      case "required":
        return [(val) => !!val || t("required")];

      // 如果沒有匹配的規則名稱，返回空數組
      default:
        return [];
    }
  };

  return { rulesAll };
};
