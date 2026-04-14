import { Notify, Dialog } from "quasar";

export const useNotify = () => {
  //決定通知出現的位置
  const position = "bottom-right";

  const closeBtnConfig = {
    icon: "close",
    color: "white",
    round: true,
    size: "sm",
    multiLine: true,
    actions: []
  };

  const globalconfig = {
    position: position,
    actions: [closeBtnConfig],
    ok: {
      flat: false
    }
  };

  //顯示通知資訊
  const info = (message) => {
    const config = {
      ...globalconfig,
      message: message,
      type: "info"
    };

    Notify.create(config);
  };

  //顯示成功訊息
  const success = (message) => {
    const config = {
      ...globalconfig,
      message: message,
      type: "positive"
    };

    Notify.create(config);
  };

  //顯示失敗/錯誤訊息
  const error = (message) => {
    const config = {
      ...globalconfig,
      message: message,
      type: "negative"
    };

    Notify.create(config);
  };

  //顯示失敗/錯誤訊息
  const warn = (message) => {
    const config = {
      ...globalconfig,
      message: message,
      type: "warning"
    };

    Notify.create(config);
  };
  //
  //客製化通知訊息
  //config 設定可參考上方或
  //https://quasar.dev/quasar-plugins/notify
  const custom = (config) => {
    Notify.create({ ...globalconfig, ...config });
  };

  return { info, success, error, warn, custom };
};

export const useDialog = () => {
  const globalConfig = {
    ok: {
      flat: false
    }
  };
  //顯示通知alert 訊息
  const alert = (title, message, customConfig = {}) => {
    const config = {
      ...globalConfig,
      persistent: true,
      title: title,
      message: message,
      ok: {
        label: "確定",
        color: "primary",
        ...customConfig.ok
      }
    };

    Dialog.create(config);
  };

  const doNothing = () => {};

  //顯示確認視窗
  const confirm = (
    title,
    message,
    okCallback = doNothing,
    cancelCallback = doNothing,
    customConfig = {}
  ) => {
    const config = {
      ...globalConfig,
      title: title,
      message: message,
      cancel: {
        label: "取消", // 預設取消按鈕文字
        color: "primary", // 預設取消按鈕顏色
        class: "", // 預設取消按鈕的class
        outline: true,
        ...customConfig.cancel // 合併自定義取消按鈕配置
      },
      ok: {
        label: "確認", // 預設確認按鈕文字
        color: "primary", // 預設確認按鈕顏色
        class: "", // 預設確認按鈕的class
        ...customConfig.ok // 合併自定義確認按鈕配置
      }
    };

    Dialog.create(config).onOk(okCallback).onCancel(cancelCallback);
  };

  //顯示詢問視窗
  const ask = (
    title,
    message,
    okCallback = (data) => {
      console.debug("data: ", data);
    },
    cancelCallback = doNothing
  ) => {
    const config = {
      ...globalConfig,
      title: title,
      message: message,
      cancel: true,
      prompt: {
        model: ""
      }
    };

    Dialog.create(config).onOk(okCallback).onCancel(cancelCallback);
  };

  //客製化詢問視窗
  // 設定可參考上方或
  //https://quasar.dev/quasar-plugins/notify
  const custom = (
    config,
    okCallback = (data) => {
      console.debug("data: ", data);
    },
    cancelCallback = doNothing
  ) => {
    Dialog.create(config).onOk(okCallback).onCancel(cancelCallback);
  };

  return { alert, confirm, ask, custom };
};
