import enUS from "./en-US";
import zhTW from "./zh-TW";
import accessToken from "./zh-TW/accessToken";
import account from "./zh-TW/account";
import accountChange from "./zh-TW/accountChange";
import ams301w from "./zh-TW/ams301w";
import ams302w from "./zh-TW/ams302w";
import didregister from "./zh-TW/didregister";
import errorCode from "./zh-TW/errorCode";
import fields from "./zh-TW/fields";
import fieldsR from "./zh-TW/fieldsR";
import func from "./zh-TW/func";
import funcChange from "./zh-TW/funcChange";
import login from "./zh-TW/login";
import often from "./zh-TW/often";
import org from "./zh-TW/org";
import orgLogo from "./zh-TW/orgLogo";
import regularExpression from "./zh-TW/regularExpression";
import remove from "./zh-TW/remove";
import role from "./zh-TW/role";
import roleChange from "./zh-TW/roleChange";
import schedule from "./zh-TW/schedule";
import status from "./zh-TW/status";
import trace from "./zh-TW/trace";
import vcSchema from "./zh-TW/vcSchema";
import common from "./zh-TW/common";
import home from "./zh-TW/home";

export default {
  "en-US": enUS,
  "zh-TW": {
    ...zhTW,
    ...accessToken,
    ...account,
    ...accountChange,
    ...ams301w,
    ...ams302w,
    ...didregister,
    ...errorCode,
    ...fields,
    ...fieldsR,
    ...func,
    ...funcChange,
    ...login,
    ...often,
    ...org,
    ...orgLogo,
    ...regularExpression,
    ...remove,
    ...role,
    ...roleChange,
    ...schedule,
    ...status,
    ...trace,
    ...vcSchema,
    ...common,
    ...home
  }
};
