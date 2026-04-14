<template>
  <q-layout view="lHh Lpr lff" class="bg-grey-1">
    <q-header class="bg-grey-1">
      <div class="q-ma-sm bg-white round-md shadow-2">
        <q-toolbar>
          <q-toolbar-title>
            <div class="row items-center q-py-sm">
              <q-btn
                flat
                round
                icon="menu"
                color="grey-4"
                @click="drawer = !drawer"
              />
              <q-img
                class="cursor-pointer"
                :src="logoImg"
                width="180px"
                height="48px"
                fit="cover"
                v-if="$q.screen.gt.sm"
                @click="backToHome"
              />
              <q-img
                class="cursor-pointer"
                :src="iconImg"
                style="width: 48px"
                alt="logo"
                @click="backToHome"
                v-else
              />
            </div>
          </q-toolbar-title>
          <q-space />
          <div class="q-pa-sm q-gutter-sm">
            <links-download-dropdown />
            <personal-dropdown />
          </div>
        </q-toolbar>
      </div>
    </q-header>
    <q-drawer
      v-model="drawer"
      :mini="mini"
      @mouseover="if (!freeze) mini = false;"
      @mouseout="if (!freeze) mini = true;"
      class="bg-grey-4"
      show-if-above
      :width="250"
    >
      <div class="q-px-sm">
        <q-btn
          flat
          round
          :icon="freeze ? 'lock' : 'lock_open'"
          color="white"
          @click="toggleMini"
        />
      </div>
      <side-link v-model="expanded" />
      <div class="q-py-lg"></div>
    </q-drawer>

    <q-page-container class="bg-grey-1 container-height">
      <router-view />
    </q-page-container>

    <q-footer class="glass q-pa-sm text-center">
      <q-toolbar>
        <q-toolbar-title>
          <p class="text-footer">© TW DIW 2025</p>
        </q-toolbar-title>
      </q-toolbar>
    </q-footer>
  </q-layout>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useUserConfigStore } from "src/stores/userConfig";
import { storeToRefs } from "pinia";

import PersonalDropdown from "components/PersonalDropdown.vue";
import LinksDownloadDropdown from "src/components/LinksDownloadDropdown.vue";
import SideLink from "components/SideLink.vue";
import { useRouter } from "vue-router";

import logoImg from "src/assets/logo.svg";
import iconImg from "src/assets/dwIcon.png";

const router = useRouter();

const userConfig = useUserConfigStore();
const { toggleFreeze } = userConfig;

defineOptions({
  name: "MainLayout"
});

const { freeze } = storeToRefs(userConfig);
const expanded = reactive({});
const drawer = ref(true);
const mini = ref(!freeze.value);

function toggleMini() {
  toggleFreeze();
  mini.value = !freeze.value;
  if (mini.value) fold();
}

const fold = () => {
  Object.keys(expanded).forEach((e) => {
    expanded[e] = false;
  });
};
const backToHome = () => {
  router.push("/vc");
};
</script>
<style scoped>
.text-footer {
  font-size: 16px;
  color: #9d334a;
}
</style>
