import { getICToken, INObject, setVNum } from "src/assets/js/ChtICToken.js";
import { useAlertStore } from "src/stores/alertStore";

const alertStore = useAlertStore();
const { loadingShow, loadingHide, showError } = alertStore;

/**
 * 檢查使用者的瀏覽器名稱及版本，並回傳這些資訊。
 * @returns 瀏覽器名稱及版本
 */
const checkBrowser = () => {
  let nAgt = navigator.userAgent;
  let browserName = navigator.appName;
  let fullVersion = "" + parseFloat(navigator.appVersion);
  let nameOffset, verOffset, ix;

  // In MSIE, the true version is after "MSIE" in userAgent
  if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
    browserName = "Microsoft Internet Explorer";
    fullVersion = nAgt.substring(verOffset + 5);
  } else if (nAgt.indexOf("Trident") != -1 && nAgt.indexOf("rv:11") != -1) {
    browserName = "Microsoft Internet Explorer";
    fullVersion = "11";
  }
  // In Opera, the true version is after "Opera"
  else if ((verOffset = nAgt.indexOf("Opera")) != -1) {
    browserName = "Opera";
    fullVersion = nAgt.substring(verOffset + 6);
  }
  // In Chrome, the true version is after "Chrome"
  else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
    browserName = "Chrome";
    fullVersion = nAgt.substring(verOffset + 7);
  }
  // In Safari, the true version is after "Safari"
  else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
    browserName = "Safari";
    fullVersion = nAgt.substring(verOffset + 7);
  }
  // In Firefox, the true version is after "Firefox"
  else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
    browserName = "Firefox";
    fullVersion = nAgt.substring(verOffset + 8);
  }
  // In most other browsers, "name/version" is at the end of userAgent
  else if (
    (nameOffset = nAgt.lastIndexOf(" ") + 1) <
    (verOffset = nAgt.lastIndexOf("/"))
  ) {
    browserName = nAgt.substring(nameOffset, verOffset);
    fullVersion = nAgt.substring(verOffset + 1);
    if (browserName.toLowerCase() == browserName.toUpperCase()) {
      browserName = navigator.appName;
    }
  }

  // trim the fullVersion string at semicolon/space if present
  if ((ix = fullVersion.indexOf(";")) != -1)
    fullVersion = fullVersion.substring(0, ix);
  if ((ix = fullVersion.indexOf(" ")) != -1)
    fullVersion = fullVersion.substring(0, ix);
  let majorVersion = parseInt("" + fullVersion, 10);

  if (isNaN(majorVersion)) {
    fullVersion = "" + parseFloat(navigator.appVersion);
    majorVersion = parseInt(navigator.appVersion, 10);
  }

  console.log("browserName", browserName);
  console.log("fullVersion", fullVersion);

  return {
    browserName,
    fullVersion
  };
};

/**
 * 從 canvas 物件中取得影像資訊，並將其轉換為字串格式
 * @param {CanvasRenderingContext2D} ctx canvas 的 2D 繪圖上下文
 * @returns {string} 影像資訊的字串格式
 */
const getImageInfo = (ctx) => {
  let output = "";
  for (let i = 0; i < 2000; i++) {
    const data = ctx.getImageData(i, 0, 1, 1).data;
    if (data[2] == 0) break;
    output = output + String.fromCharCode(data[2], data[1], data[0]);
  }
  if (output == "")
    output = '{"ret_code": 1979711501,"message": "執行檔錯誤或逾時"}';
  return output;
};

/**
 * 根據錯誤碼回傳主要錯誤原因的描述
 * @param {number} rcode 錯誤碼
 * @returns {string} 錯誤原因的描述
 */
const MajorErrorReason = (rcode) => {
  if (!isNaN(rcode) && Number.isInteger(Number(rcode))) {
    const intCode = parseInt(rcode);
    if (intCode < 0) {
      intCode = 0xffffffff + intCode + 1;
    }
  }
  switch (intCode) {
    case 0x76000001:
      return "未輸入金鑰";
    case 0x76000002:
      return "未輸入憑證";
    case 0x76000003:
      return "未輸入待簽訊息";
    case 0x76000004:
      return "未輸入密文";
    case 0x76000005:
      return "未輸入函式庫檔案路徑";
    case 0x76000006:
      return "未插入IC卡";
    case 0x76000007:
      return "未登入";
    case 0x76000008:
      return "型態錯誤";
    case 0x76000009:
      return "檔案錯誤";
    case 0x7600000a:
      return "檔案過大";
    case 0x7600000b:
      return "JSON格式錯誤";
    case 0x7600000c:
      return "參數錯誤";
    case 0x7600000d:
      return "執行檔錯誤或逾時";
    case 0x7600000e:
      return "不支援的方法";
    case 0x7600000f:
      return "禁止存取的網域";
    case 0x76000998:
      return "未輸入PIN碼";
    case 0x76000999:
      return "使用者已取消動作";
    case 0x76100001:
      return "無法載入IC卡函式庫檔案";
    case 0x76100002:
      return "結束IC卡函式庫失敗";
    case 0x76100003:
      return "無可用讀卡機";
    case 0x76100004:
      return "取得讀卡機資訊失敗";
    case 0x76100005:
      return "取得session失敗";
    case 0x76100006:
      //return "IC卡登入失敗(Pin碼輸入錯誤)";//Pin碼輸入錯誤
      return "IC卡PIN碼錯誤！請輸入正確的PIN碼。<br />注意：PIN碼累計錯誤3次IC卡將會鎖卡。";
    case 0x76100007:
      return "IC卡登出失敗";
    case 0x76100008:
      return "IC卡取得金鑰失敗";
    case 0x76100009:
      return "IC卡取得憑證失敗";
    case 0x7610000a:
      return "取得函式庫資訊失敗";
    case 0x7610000b:
      return "IC卡卡片資訊失敗";
    case 0x7610000c:
      return "找不到指定憑證";
    case 0x7610000d:
      return "找不到指定金鑰";
    case 0x76200001:
      return "pfx初始失敗";
    case 0x76200006:
      return "pfx登入失敗";
    case 0x76200007:
      return "pfx登出失敗";
    case 0x76200008:
      return "不支援的CA";
    case 0x76300001:
      return "簽章初始錯誤";
    case 0x76300002:
      return "簽章型別錯誤";
    case 0x76300003:
      return "簽章內容錯誤";
    case 0x76300004:
      return "簽章執行錯誤";
    case 0x76300005:
      return "簽章憑證錯誤";
    case 0x76300006:
      return "簽章DER錯誤";
    case 0x76300007:
      return "簽章結束錯誤";
    case 0x76300008:
      return "簽章驗證錯誤";
    case 0x76300009:
      return "簽章BIO錯誤";
    case 0x76400001:
      return "解密DER錯誤";
    case 0x76400002:
      return "解密型態錯誤";
    case 0x76400003:
      return "解密錯誤";
    case 0x76500001:
      return "憑證尚未生效";
    case 0x76500002:
      return "憑證已逾期";
    case 0x76600001:
      return "Base64編碼錯誤";
    case 0x76600002:
      return "Base64解碼錯誤";
    case 0x76700001:
      return "伺服金鑰解密錯誤";
    case 0x76700002:
      return "未登錄伺服金鑰";
    case 0x76700003:
      return "伺服金鑰加密錯誤";
    case 0x76210001:
      return "身分證字號或外僑號碼比對錯誤";
    case 0x76210002:
      return "未支援的憑證型別";
    case 0x76210003:
      return "非元大寶來憑證";
    case 0x76210004:
      return "非中華電信通用憑證管理中心發行之憑證";

    case 0x77100001:
      return "圖形驗證碼不符";
    case 0x77200001:
      return "未輸入附卡授權SNO碼";
    case 0x77200002:
      return "讀附卡授權證發生錯誤:Buffer太小";
    case 0x77200003:
      return "讀附卡授權證發生錯誤:卡片空間不足";
    case 0x77200004:
      return "讀附卡授權證發生錯誤:資料太大";
    case 0x77200005:
      return "讀附卡授權證發生錯誤:DLL載入發生錯誤(E_NOT_LOAD_DLL)";
    case 0x77200006:
      return "讀附卡授權證發生錯誤:支援函數錯誤(E_NOT_SUPPORT_FUNCTION)";
    case 0x77200007:
      return "讀附卡授權證發生錯誤:讀卡slot錯誤(E_SLOT)";
    case 0x77200008:
      return "讀附卡授權證發生錯誤:Index格式錯誤";
    case 0x77200009:
      return "讀附卡授權證發生錯誤:讀卡機未選擇(READER_NOT_SELECT_ERROR)";
    case 0x77200010:
      return "讀附卡授權證發生錯誤:SNO碼錯誤(SNO_EXIST)";
    case 0x77200011:
      return "讀附卡授權證發生錯誤:SNO碼錯誤(SNO_NO_EXIST)";
    case 0x77200101:
      return "寫新憑證功能發生錯誤：寫新憑證前刪除舊憑證發生錯誤";
    case 0x77200102:
      return "寫新憑證功能發生錯誤：要寫入新憑證時發生錯誤";
    case 0x77200103:
      return "寫新憑證功能發生錯誤：輸入內容PIN和SOPIN不可同時有值";

    case 0xe0000013: //0xE0000013
      return "金鑰不相符";
    case 0xe0000012: //0xE0000012
      return "使用者取消";
    case 0xe0000010: //0xE0000010
      return "建立金鑰容器失敗，可能是因為權限不足";
    case 0xe000000f: //0xE000000F
      return "找不到任一家CA發的該類別用戶憑證，但中華電信該憑證類別中有找到其他用戶";
    case 0xe000000e: //0xE000000E
      return "開啟物件(p7b)失敗";
    case 0xe000000d: //0xE000000D
      return "HEX字串格式錯誤";
    case 0xe000000c: //0xE000000C
      return "HEX字串長度錯誤";
    case 0xe000000b: //0xE000000B
      return "寬位元字串轉多位元字串轉換失敗";
    case 0xe000000a: //0xE000000A
      return "開啟CertStore失敗";
    case 0xe0000009: //0xE0000009
      return "匯出檔案失敗";
    case 0xe0000008: //0xE0000008
      return "匯入檔案失敗";
    case 0xe0000007: //0xE0000007
      return "必須輸入檔案路徑";
    case 0xe0000006: //0xE0000006
      return "找不到任一家CA發的該類別用戶憑證";
    case 0xe0000005: //0xE0000005
      return "找不到中華電信該類別用戶憑證，但找得到其他CA發的該類別用戶憑證";
    case 0xe0000004: //0xE0000004
      return "未支援的參加單位代碼";
    case 0xe0000003: //0xE0000003
      return "金鑰的雜湊值不一致";
    case 0xe0000002: //0xE0000002
      return "程式配置記憶體失敗";
    case 0xe0000001: //0xE0000001
      return "找不到由中華電信所核發且合乎搜尋條件的憑證";

    //啟用IC卡片鎖卡解鎖錯誤碼
    case 0x81000001:
      return "沒有CONTENT_LENGTH";
    case 0x81000002:
      return "CONTENT_LENGTH_SIZE太大";
    case 0x81000003:
      return "讀取設定檔錯誤";
    case 0x81000004:
      return "解析加密JSON錯誤(不是JSON格式)";
    case 0x81000005:
      return "解析加密JSON參數錯誤";
    case 0x81000111:
      return "解析JSON錯誤(不是JSON格式)";
    case 0x81000112:
      return "解析JSON參數錯誤";
    case 0x81000113:
      return "解析JSON API版本錯誤";
    case 0x81000114:
      return "解析JSON METHOD錯誤";
    case 0x81000115:
      return "解析JSON 請求逾時";
    case 0x81000201:
      return "用戶代碼錯誤1次";
    case 0x81000202:
      return "用戶代碼錯誤2次";
    case 0x81000203:
      return "用戶代碼錯誤3次";
    case 0x81000204:
      return "用戶代碼錯誤4次，用戶代碼已鎖定，請洽諮詢服務人員";
    case 0x81000221:
      return "DB連線錯誤";
    case 0x81000222:
      return "DB連線錯誤";
    case 0x81000223:
      return "DB連線錯誤";
    case 0x81000224:
      return "此卡號不存在";
    case 0x81000225:
      return "此卡號未啟用";
    case 0x81000226:
      return "此卡號已啟用";
    case 0x81000227:
      return "用戶代碼已鎖定，請洽諮詢服務人員";
    case 0x81000228:
      return "DB UNBLOCK錯誤";
    case 0x81000229:
      return "DB USERPIN錯誤";
    case 0x81000230:
      return "DB 輸入參數錯誤";
    case 0x81000231:
      return "DB錯誤";
    case 0x81000232:
      return "DB UNBLOCK解析錯誤";
    case 0x81000233:
      return "DB USERPIN解析錯誤";
    case 0x81000301:
      return "連線到RA錯誤";
    case 0x81000302:
      return "RA回應格式錯誤";
    case 0x81011000:
      return "底層錯誤Buffer size";
    case 0x81011001:
      return "底層錯誤 RSA加密";
    case 0x81011002:
      return "底層錯誤 RSA解密";
    case 0x81011003:
      return "底層錯誤 RSA簽章";
    case 0x81011004:
      return "底層錯誤 RSA驗簽";
    case 0x81011005:
      return "底層錯誤 AES加密";
    case 0x81011006:
      return "底層錯誤 AES解密";

    case 0x82000003:
      return "解析加密JSON錯誤(不是JSON格式)";
    case 0x82000004:
      return "解析加密JSON參數錯誤";
    case 0x82000111:
      return "解析JSON錯誤(不是JSON格式)";
    case 0x82000112:
      return "解析JSON參數錯誤";
    case 0x82000113:
      return "解析JSON API版本錯誤";
    case 0x82000114:
      return "解析JSON METHOD錯誤";
    case 0x82000115:
      return "用戶代碼參數比對錯誤";
    case 0x82000116:
      return "卡號參數比對錯誤";
    case 0x82000117:
      return "CANAME參數比對錯誤";
    case 0x82000118:
      return "回應逾時";
    case 0x83000100:
      return "插入的卡片不符合要求(非GPKI卡片)";
    case 0x83000101:
      return "選錯服務，您使用MOICA卡";
    case 0x83000102:
      return "選錯服務，您使用MOEACA卡";
    case 0x83000103:
      return "選錯服務，您使用GCA卡";
    case 0x83000104:
      return "選錯服務，您使用XCA卡";
    case 0x83000105:
      return "輸入之PIN碼格式錯誤";
    case 0x83000106:
      return "輸入之用戶代碼格式錯誤";
    default:
      return rcode;
  }
};

/**
 * 根據錯誤碼回傳次要錯誤原因的描述
 * @param {number} rcode 錯誤碼
 * @returns {string} 錯誤原因的描述
 */
const MinorErrorReason = (rcode) => {
  if (!isNaN(rcode) && Number.isInteger(Number(rcode))) {
    const intCode = parseInt(rcode);
    if (intCode < 0) {
      intCode = 0xffffffff + intCode + 1;
    }
  }
  switch (intCode) {
    case 0x06:
      return "函式失敗";
    case 0xa0:
      return "PIN碼錯誤";
    case 0xa2:
      return "PIN碼長度錯誤";
    case 0xa4:
      return "IC卡PIN碼錯誤！累計錯誤3次IC卡已鎖卡";
    case 0x150:
      return "記憶體緩衝不足";
    case 0x80000001:
    case -2147483647:
      //return "PIN碼錯誤，剩餘一次機會";
      return "IC卡PIN碼錯誤！還有一次機會。<br />注意：PIN碼累計錯誤3次IC卡將會鎖卡。";
    case 0x80000002:
    case -2147483646:
      //return "PIN碼錯誤，剩餘兩次機會";
      return "IC卡PIN碼錯誤！還有兩次機會。<br />注意：PIN碼累計錯誤3次IC卡將會鎖卡。";
    default:
      return rcode.toString(16);
  }
};

/**
 * 解析並處理從伺服器回傳的 JSON 資料，並根據回傳的資料更新 UI
 * @param {string} output 從伺服器回傳的 JSON 字串
 */
const setOutput = (output) => {
  const ret = JSON.parse(output);
  let Gbjct = new INObject();
  Gbjct.WbSckVrsn = setVNum();
  if (ret.ret_code == 1979711503) {
    showError(window.location.hostname + "非信任網站，請先加入信任網站");
  } else if (ret.ret_code == 0) {
  } else {
    if (ret.message == undefined) {
      ret.message = MajorErrorReason(ret.ret_code);
      if (ret.last_error) ret.message = MinorErrorReason(ret.last_error);
      showError(ret.message);
    } else {
      const ErrorInfo = ret.message;
      showError(ErrorInfo);
    }
  }
  if (ret.slots == undefined) {
    showError("讀卡機裝置無定義");
  } else {
    const slots = ret.slots;
    const selectSlot = document.getElementById("slotDescription");
    selectSlot.innerHTML = "<option value='-1' selected>請選擇卡片</option>";
    for (let index in slots) {
      if (slots[index].token instanceof Object) {
        if (slots[index].token instanceof Object) {
          const option = document.createElement("option");
          option.value = index;
          option.textContent = `${slots[index].slotDescription} 卡號:[${slots[index].token.serialNumber}]`;
          selectSlot.appendChild(option);
        }
      }
    }
    selectSlot.click();
    slots.length = slots.length ? slots.length : 0; // null situation set 0
    if (slots.length == 0) {
      showError("未偵測到讀卡機或憑證 IC 卡，請插卡後再重新勾選。");
    }
  }
  loadingHide();
};

/**
 * 向指定的 URL 發送 POST 請求，並回傳伺服器的回應
 * @param {string} target 目標 URL
 * @param {object} data 要發送的資料
 * @returns {string|null} 伺服器的回應或 null
 */
const postCheckingData = (target, data) => {
  if (!http.sendRequest) {
    return null;
  }
  http.url = target;
  http.actionMethod = "POST";
  const code = http.sendRequest(data);
  if (code != 0) return null;
  return http.responseText;
};

/**
 * 檢查 HiCOS 客戶端是否安裝並啟動，並根據不同的瀏覽器進行相應的處理
 * @param {string} __Browser_Name 瀏覽器名稱
 */
const checkHiCOS_Client = (__Browser_Name) => {
  console.log("check HiCOS");
  if (__Browser_Name != "InternetExplorer") {
    const DOMURL = window.URL || window.webkitURL || window;
    let img = document.createElement("img");
    img.crossOrigin = "Anonymous";
    img.src = "http://localhost:61161/p11Image.bmp";
    const canvas = document.createElement("canvas");
    canvas.width = 2000;
    canvas.height = 1;
    let ctx = canvas.getContext("2d", { willReadFrequently: true });
    const url = img.src;
    img.onload = function () {
      ctx.drawImage(img, 0, 0);
      let output = getImageInfo(ctx);
      setOutput(output);
      if (__Browser_Name != "Safari") {
        try {
          DOMURL.revokeObjectURL(url);
        } catch (e) {
          console.error(e);
        }
      }
    };

    img.onerror = function () {
      showError("HiCOS 未安裝，請先安裝HiCOS卡片管理工具");
    };
  } else {
    document.getElementById("httpObject").innerHTML =
      '<OBJECT id="http" width="1" height="1" style="LEFT: 1px; TOP: 1px" type="application/x-httpcomponent" VIEWASTEXT></OBJECT>';
    output = postCheckingData("http://localhost:61161/pkcs11info", "");
    if (output == null) {
      showError("未安裝客戶端程式或未啟動服務");
    } else {
      setOutput(output);
    }
  }
};

/**
 * 將字串轉換為 Base64 編碼
 * @param {string} str 要轉換的字串
 * @returns {string} Base64 編碼後的字串
 */
const toBase64 = (str) => {
  const encoder = new TextEncoder();
  const data = encoder.encode(str);
  let binary = "";
  const len = data.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(data[i]);
  }
  return btoa(binary);
};

/**
 * 處理簽章結果資訊
 */
const signatureResult = () => {
  const icToken = getICToken();
  const rCode = Number(icToken.RetObj.RCode);
  if (rCode !== 0) {
    showError(`簽章失敗! 錯誤碼：${rCode}, 原因：${icToken.RetObj.RMsg}`);
    return;
  }
  console.log("簽章成功");
  // console.log(icToken.RetObj.B64Signature);
  document.getElementById("orgSignature").value = icToken.RetObj.B64Signature;
  document.getElementById("serialNumber").click();
};

/**
 * 處理簽章操作，包括初始化 IC 卡、選擇讀卡機、進行簽章等
 * @param {string} selectSlotValue 選擇的卡片值
 * @param {string} pincode PIN 碼
 * @param {string} signContent 簽章內容
 */
const signHandler = (selectSlotValue, pincode, signContent) => {
  loadingShow();
  getICToken().goodDay(() => {
    const icToken = getICToken();
    const rCode = Number(icToken.RetObj.RCode);
    if (rCode !== 0) {
      showError(
        `初始化 IC 卡失敗! 錯誤碼：${rCode}, 原因：${icToken.RetObj.RMsg}`
      );
      return;
    }

    //簽章(選擇讀卡機)
    const setReturn = icToken.setActiveSlotID(selectSlotValue); //使用選擇的讀卡機
    const setReturnRCode = Number(setReturn.RCode);
    if (setReturnRCode !== 0) {
      showError(
        `使用選擇的讀卡機失敗! 錯誤碼：${setReturnRCode}, 原因：${setReturn.RMsg}`
      );
      return;
    }

    const tbs = toBase64(signContent);
    const pin = pincode;
    const hashAlgorithm = "SHA256"; // Hash 演算法
    const signatureType = "PKCS7"; // 簽章標準

    icToken.sign(tbs, pin, hashAlgorithm, signatureResult, signatureType);
  });
};

/**
 * 處理重新載入 IC 卡按鈕的點擊事件
 */
const btnReloadIcCardClickHandler = (event) => {
  setTimeout(() => {
    const browserInfo = checkBrowser();
    const { browserName, fullVersion } = browserInfo;
    checkHiCOS_Client(browserName);
  }, 1000);
};

/**
 * 處理簽章按鈕的點擊事件
 */
const btnClickHandler = (selectSlotValue, pincode, type) => {
  if (type === "MOEACA") {
    const businessId = document.getElementById("businessId").value;
    const companySerialNumber = document.getElementById("subjectID").value;

    if (businessId.length !== 8) {
      showError("統編長度錯誤");
      return;
    } else if (businessId !== companySerialNumber) {
      showError("統編輸入錯誤");
      return;
    }
  } else if (type === "MOICA") {
    const pid = document.getElementById("pid").value;
    const pidInCard = document.getElementById("subjectID").value;
    if (pid.length !== 10) {
      showError("身分證長度錯誤");
      return;
    } else if (pid.slice(-4) !== pidInCard) {
      showError("身分證輸入錯誤");
      return;
    }
  }

  const signContent = "簽章內容";
  try {
    if (Number(selectSlotValue) === -1) {
      showError("請選擇卡片");
      return;
    }
  } catch (error) {
    showError("請選擇卡片");
    return;
  }

  if (false === !!pincode) {
    showError("請輸入 PIN 碼");
    return;
  }

  signHandler(selectSlotValue, pincode, signContent);
};
// })();

export { btnReloadIcCardClickHandler, btnClickHandler, signHandler };
