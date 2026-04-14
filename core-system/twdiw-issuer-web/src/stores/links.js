import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { api } from "src/boot/axios";
import { i18n } from "src/boot/i18n";
import { SessionStorage as $s } from "quasar";

const AUTH_LINKS = "authroizedLinks";
const publicLinks = [
  "/account/modadw311w/activatePage",
  "/account/modadw302w/resetFinish",
  "/swaggerui",
  "/vc"
];

export const useLinksStore = defineStore("links", () => {
  const { t } = i18n.global;
  const authroizedLinks = ref([]);
  retrieveAuthroizedLinks();

  const links = computed(() => {
    const allList = [
      {
        // VC 欄位管理
        header: t("menu.fieldsManagement"),
        icon: "fa-solid fa-list",
        links: [
          {
            title: t("basicFieldsTitle"),
            link: "/vc/fields/basic"
          },
          {
            title: t("normalFieldsTitle"),
            link: "/vc/fields/normal"
          },
          {
            title: t("regularExpressionTitle"),
            link: "/vc/fields/regularExpression"
          }
        ]
      },
      {
        // VC 管理
        header: t("menu.vcManagement"),
        icon: "fa-regular fa-address-card",
        links: [
          {
            title: t("vcSchema.createVC"),
            link: "/vcSchema/createVC"
          },
          {
            title: t("vcSchema.removeVC"),
            link: "/vcSchema/removeVC"
          }
        ]
      },
      {
        // Issuer 功能
        header: t("menu.issuerFunctions"),
        icon: "fa-solid fa-landmark",
        links: [
          {
            title: t("didregister.menuTitle"),
            link: "/dw/DIDregister"
          },
          {
            title: t("orgLogo.title"),
            link: "/dw/OrgLogoUpload"
          }
        ]
      },
      {
        // 帳號權限
        header: t("menu.accountAuth"),
        icon: "fa-solid fa-user-gear",
        links: [
          {
            title: t("account.title"),
            link: "/modadw/modadw311w"
          },
          {
            title: t("role.title"),
            link: "/modadw/modadw321w"
          },
          {
            title: t("org.title"),
            link: "/modadw/modaorg"
          },
          {
            title: t("func.title"),
            link: "/modadw/modadw331w"
          },
          {
            title: t("accountChange.title"),
            link: "/modadw/modadw312w"
          },
          {
            title: t("roleChange.title"),
            link: "/modadw/modadw322w"
          },
          {
            title: t("funcChange.title"),
            link: "/modadw/modadw332w"
          },
          {
            title: t("trace.title"),
            link: "/modadw/modadw341w"
          }
        ]
      }
    ];

    return allList
      .map((e) => {
        return {
          ...e,
          links: e.links.filter((l) => {
            return isAuthorizedLink(l.link);
          })
        };
      })
      .filter((e) => e.links.length > 0);
  });

  async function retrieveAuthroizedLinks() {
    const fromSessionStorage = $s.getItem(AUTH_LINKS);
    if (fromSessionStorage) {
      authroizedLinks.value = fromSessionStorage;
      return;
    }

    const jwtToken = sessionStorage.getItem("jwt-user-object");
    if (!jwtToken) {
      return;
    }

    try {
      // 取得側邊欄連結
      const url = "/api/modadw311w/getSideLink";
      const requestData = {};

      const linkResponse = await api.post(url, requestData, {
        headers: {
          "Content-Type": "application/json"
        }
      });

      if (!linkResponse.data || !linkResponse.data.code) {
        throw new Error("Invalid response");
      }

      if (linkResponse.data.code !== "0") {
        throw new Error(linkResponse.data.msg || "Unknown error");
      }

      // 檢查管理員角色
      const roleResponse = await api.get("/api/schedule/check/isAdminRole", {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
          "Content-Type": "application/json"
        }
      });

      const fromAPI = linkResponse.data.data;
      const allLinks = [...fromAPI];
      authroizedLinks.value = allLinks;
      $s.setItem(AUTH_LINKS, allLinks);
    } catch (err) {
      console.error(`${t("error.notice")}：${err}`);
    }
  }

  function isAuthorizedLink(link) {
    if (authroizedLinks.value.length == 0) {
      authroizedLinks.value = $s.getItem(AUTH_LINKS) || [];
    }
    return authroizedLinks.value.includes(link);
  }

  function isPublicLink(link) {
    return publicLinks.includes(link);
  }

  return {
    links,
    retrieveAuthroizedLinks,
    authroizedLinks,
    isAuthorizedLink,
    isPublicLink
  };
});
