<template>
  <q-layout view="hHh lpR fff">
    <q-header class="header q-pa-md bg-white row">
      <q-img src="~assets/logo.svg" width="200px" height="48px" fit="cover" />
    </q-header>

    <q-page-container class="page-bg container-height">
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
<style lang="scss" scoped>
.header {
  border-bottom: 1px solid #d1d5db;
}

.text-footer {
  font-size: 16px;
  color: #9d334a;
}
</style>
