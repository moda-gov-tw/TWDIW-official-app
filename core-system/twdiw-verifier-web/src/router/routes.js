const routes = [
  {
    path: "/",
    component: () => import("layouts/AuthLayout.vue"),
    children: [
      { path: "", component: () => import("pages/auth/LoginPage.vue") },
      {
        path: "/account/modadw302w/resetFinish",
        component: () => import("src/pages/ams302w/Ams302wPage.vue")
      },
      {
        path: "/account/modadw311w/activatePage",
        component: () => import("src/pages/account/ActivatePage.vue")
      }
    ]
  },
  {
    path: "/modadw",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      { path: "", component: () => import("pages/IndexPage.vue") },
      {
        path: "modadw311w",
        component: () => import("pages/account/Ams311wPageRwd.vue")
      },
      {
        path: "modadw321w",
        component: () => import("pages/role/Moda321wPage.vue")
      },
      {
        path: "modadw331w",
        component: () => import("pages/func/Ams331wPageRwd.vue")
      },
      {
        path: "modaorg",
        component: () => import("pages/org/OrgMainPage.vue")
      },
      {
        path: "modadw351w",
        component: () => import("pages/accessToken/Ams351wPage.vue")
      },
      {
        path: "modadw312w",
        component: () => import("pages/accountChange/Ams312wPageRwd.vue")
      },
      {
        path: "modadw322w",
        component: () => import("pages/roleChange/Ams322wPageRwd.vue")
      },
      {
        path: "modadw332w",
        component: () => import("pages/funcChange/Ams332wPageRwd.vue")
      },
      {
        path: "modadw341w",
        component: () => import("pages/trace/Ams341wPageRwd.vue")
      }
    ]
  },
  {
    path: "/dw",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      { path: "", component: () => import("pages/IndexPage.vue") },
      {
        path: "createVP",
        component: () => import("pages/vp401w/CreateVP401wPage.vue")
      },
      {
        path: "DIDregister",
        component: () => import("pages/verifier/DIDregister.vue")
      },
      {
        path: "OrgKeySetting",
        component: () => import("pages/verifier/OrgKeySetting.vue")
      },
      {
        path: "OrgLogoUpload",
        component: () => import("pages/verifier/OrgLogoUpload.vue")
      }
    ]
  },

  //TODO: 建立404 頁面
  //任何404 皆會暫時回到ams 首頁
  {
    path: "/:catchAll(.*)*",
    redirect: "/"
  }
];

export default routes;
