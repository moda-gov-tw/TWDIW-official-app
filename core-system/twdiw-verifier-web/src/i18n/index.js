import enUS from "./en-US";
import zhTW from "./zh-TW";
import often from "./zh-TW/often";
import account from "./zh-TW/account";
import accountChange from "./zh-TW/accountChange";
import accessToken from "./zh-TW/accessToken";
import role from "./zh-TW/role";
import roleChange from "./zh-TW/roleChange";
import funcChange from "./zh-TW/funcChange";
import trace from "./zh-TW/trace";
import func from "./zh-TW/func";
import errorCode from "./zh-TW/errorCode";
import org from "./zh-TW/org";
import vp from "./zh-TW/vp";
import login from "./zh-TW/login.js";
import common from "./zh-TW/common.js";
import didregister from "./zh-TW/didregister";
import orgLogo from "./zh-TW/orgLogo";
import orgKeySetting from "./zh-TW/orgKeySetting";
import home from "./zh-TW/home";
import ams302w from "./zh-TW/ams302w";

export default {
  "en-US": enUS,
  "zh-TW": {
    ...zhTW,
    ...often,
    ...account,
    ...accountChange,
    ...accessToken,
    ...role,
    ...roleChange,
    ...funcChange,
    ...trace,
    ...func,
    ...errorCode,
    ...org,
    ...vp,
    ...login,
    ...common,
    ...didregister,
    ...orgLogo,
    ...orgKeySetting,
    ...home,
    ...ams302w
  }
};
