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
  "/dw"
];

export const useLinksStore = defineStore("links", () => {
  const { t } = i18n.global;
  const authroizedLinks = ref([]);
  retrieveAuthroizedLinks();

  const links = computed(() => {
    const allList = [
      {
        //撤銷 issuer
        header: t("menu.vpManagement"),
        icon: "fa-solid fa-person-chalkboard",
        links: [
          {
            title: t("title.createVP"),
            link: "/dw/CreateVP"
          }
        ]
      },
      {
        header: t("menu.verifierFunctions"),
        icon: "fa-solid fa-landmark",
        links: [
          {
            title: t("title.registerDid"),
            link: "/dw/DIDregister"
          },
          {
            title: t("title.orgLogoUpload"),
            link: "/dw/OrgLogoUpload"
          },
          {
            title: t("title.orgKeySetting"),
            link: "/dw/OrgKeySetting"
          }
        ]
      },
      {
        header: t("menu.accountAuth"),
        icon: "fa-solid fa-user-gear",
        links: [
          {
            title: t("title.accountManagement"),
            link: "/modadw/modadw311w"
          },
          {
            title: t("title.roleManagement"),
            link: "/modadw/modadw321w"
          },
          {
            title: t("title.orgManagement"),
            link: "/modadw/modaorg"
          },
          {
            title: t("title.func"),
            link: "/modadw/modadw331w"
          },
          {
            title: t("title.accountChange"),
            link: "/modadw/modadw312w"
          },
          {
            title: t("title.roleChange"),
            link: "/modadw/modadw322w"
          },
          {
            title: t("title.funcChange"),
            link: "/modadw/modadw332w"
          },
          {
            title: t("title.trace"),
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

  function retrieveAuthroizedLinks() {
    const fromSessionStorage = $s.getItem(AUTH_LINKS);
    if (fromSessionStorage) {
      authroizedLinks.value = fromSessionStorage;
      return;
    }

    const jwtToken = sessionStorage.getItem("jwt-user-object");
    if (!jwtToken) {
      return;
    }

    const url = "/api/modadw311w/getSideLink";
    const requestData = {};

    api
      .post(url, requestData, {
        headers: {
          "Content-Type": "application/json"
        }
      })
      .then((res) => {
        if (!res.data) {
          $n.error(t("account.error.emptyResponse"));
        }

        if (!res.data.code) {
          $n.error(t("account.error.noCode"));
        }

        if (res.data.code !== "0") {
          $n.error(response.data.msg || t("account.error.backendError"));
        }
        const fromAPI = res.data.data;
        const allLinks = [...fromAPI];

        authroizedLinks.value = allLinks;
        $s.setItem(AUTH_LINKS, allLinks);
      })
      .catch((err) => {
        console.error(`${t("error.notice")}：${err}`);
      });
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
