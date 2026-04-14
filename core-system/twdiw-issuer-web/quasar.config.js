/* eslint-env node */

/*
 * quasar 設定檔
 * 相關設定可參考quasar 官方網址
 * https://v2.quasar.dev/quasar-cli-vite/quasar-config-js
 */
const { configure } = require("quasar/wrappers");
const { loadEnv } = require("vite");
const path = require("path");

module.exports = configure(function (ctx) {
  // 從 .env 檔案載入環境變數
  const env = loadEnv(ctx.modeName, process.cwd(), "VITE_");
  const PATH = env.VITE_BASE_PATH || "/";
  const RSA_PUBLIC_KEY = env.VITE_RSA_PUBLIC_KEY || "";

  return {
    // 預先載入資料設定，與登入權限有關，相關可參考以下網址
    // https://v2.quasar.dev/quasar-cli-vite/prefetch-feature
    // preFetch: true,

    // 設定用來在App.vue 啟動前呼叫的js檔案，用來注入功能
    // 需要指定存放在src/boot 內的js 檔案名稱
    // 相關可參考以下網址:
    // https://v2.quasar.dev/quasar-cli-vite/boot-files
    boot: ["i18n", "axios", "auth"],

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#css
    css: ["app.scss"],

    // icon 字型設定，額外可參考
    // https://github.com/quasarframework/quasar/tree/dev/extras
    extras: [
      "roboto-font", // optional, you are not bound to it
      "material-icons", // optional, you are not bound to it
      "fontawesome-v6"
    ],

    // 專案建置選項
    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#build
    build: {
      publicPath: PATH,

      target: {
        browser: ["es2019", "edge88", "firefox78", "chrome87", "safari13.1"],
        node: "node20"
      },

      //設定路徑簡寫
      alias: {
        utils: path.join(__dirname, "./src/utils")
      },

      vueRouterMode: "history", // available values: 'hash', 'history'

      //取消options API 可以加快 compile 速度
      vueOptionsAPI: false,

      // 環境變數
      // 注意：實際部署時，這些環境變數應該在部署環境中設定，而不是硬編碼在原始碼中
      env: {
        VITE_BASE_PATH: PATH,
        VITE_RSA_PUBLIC_KEY: RSA_PUBLIC_KEY
      },

      // 專案build 出來的位置
      // distDir

      // extendViteConf (viteConf) {},
      // viteVuePluginOptions: {},

      vitePlugins: [
        [
          "@intlify/vite-plugin-vue-i18n",
          {
            include: path.resolve(__dirname, "./src/i18n/**")
          }
        ],
        [
          "vite-plugin-checker",
          {
            eslint: {
              lintCommand: 'eslint "./**/*.{js,mjs,cjs,vue}"'
            }
          },
          { server: false }
        ],
        ["@vue-macros/reactivity-transform/vite"],
        ["unplugin-vue-components/vite", { dirs: ["src/components/**"] }]
      ]
    },

    // 開發伺服器設定
    // 完整選項參考: https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#devServer
    devServer: {
      // https: true
      open: true, // 自動開啟瀏覽器
      port: 9000
      // proxy 設定：請根據您的後端服務位置進行設定
      // proxy: {
      //   "/api": {
      //     target: "http://localhost:8080/",
      //     changeOrigin: true
      //   },
      //   "/management": {
      //     target: "http://localhost:8080/",
      //     changeOrigin: true
      //   },
      // },
    },

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#framework
    framework: {
      lang: "zh-TW",
      config: {},
      cssAddon: true,
      // iconSet: 'material-icons', // Quasar icon set
      iconSet: "fontawesome-v6",

      // Quasar plugins
      plugins: ["Dialog", "Notify", "LocalStorage", "SessionStorage", "Loading"]
    },

    // animations: 'all', // --- includes all animations
    // https://v2.quasar.dev/options/animations
    animations: []
  };
});
