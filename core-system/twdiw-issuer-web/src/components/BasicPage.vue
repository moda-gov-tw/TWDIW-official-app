<template>
  <q-page class="full-height q-pa-sm">
    <div
      class="full-height bg-white shadow-3 rounded-borders q-pa-sm overflow-auto"
    >
      <slot name="default">
        <h3>page tite</h3>
        <p>用此頁面來維持版型</p>
      </slot>
    </div>
  </q-page>
</template>
