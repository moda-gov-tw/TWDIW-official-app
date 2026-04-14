export const mask = () => {
  /**
   * 將電子郵件格式化
   * @param {String} email
   * @returns x******x@gmail.com
   */
  const maskEmail = (email) => {
    // 檢核
    if (!email) return "";

    const parts = email.split("@");
    if (parts.length !== 2) {
      if (email.length <= 1) return "******";

      return email[0] + "******" + email[email.length - 1];
    }

    const [username, domain] = parts;
    // x@gmail.com -> ******@gmail.com
    if (username.length <= 1) return "******@" + domain;

    // xxxxx@gmail.com -> x***x@gmail.com
    const mask = username[0] + "******" + username[username.length - 1];
    return mask + "@" + domain;
  };

  /**
   * 將手機號碼格式化
   * @param {String} phone
   * @returns 0919***123
   */
  const maskPhone = (phone) => {
    if (!phone) return "";
    if (phone.length < 10) return phone;

    const arr = phone.split("");

    // 第5~7碼替換為 *
    for (let i = 4; i <= 6; i++) {
      if (i < arr.length) {
        arr[i] = "*";
      }
    }

    return arr.join("");
  };

  /**
   * 將使用者輸入的 masked 值轉換回真實值
   * @param {*} originalTel 原始真實字串
   * @param {*} newMaskedVal 使用者輸入後的掩碼字串
   * @returns
   */
  const applyMaskEdit = (originalTel, newMaskedVal) => {
    // 原始電話轉成陣列
    let original = originalTel.split("");
    // 轉成隱碼
    const mask = maskPhone(originalTel);
    // 回傳值
    let newVal = [];
    let oldIndices = [];
    const element = "*";

    let oldIndex = mask.indexOf(element);
    // 找出所有隱碼位置
    while (oldIndex != -1) {
      oldIndices.push(oldIndex);
      oldIndex = mask.indexOf(element, oldIndex + 1);
    }

    let getIndex = 0;
    for (let i = 0; i < newMaskedVal.length; i++) {
      const inputChar = newMaskedVal[i];

      // 如果新值為 *
      if (inputChar === element) {
        newVal.push(original[oldIndices[getIndex]]);
        getIndex++;
      } else {
        newVal.push(inputChar);
      }
    }

    return newVal.join("");
  };

  return {
    maskEmail,
    maskPhone,
    applyMaskEdit
  };
};
