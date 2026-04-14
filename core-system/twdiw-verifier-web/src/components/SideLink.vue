<template>
  <q-list>
    <q-item clickable @click="goHome" class="light text-bold">
      <q-item-section avatar>
        <q-icon name="fa-solid fa-home" />
      </q-item-section>
      <q-item-section>回首頁</q-item-section>
    </q-item>
    <q-expansion-item
      group="group"
      v-for="headlink in links"
      :key="headlink.header"
      v-model="expanded[headlink.header]"
      :icon="headlink.icon"
      :label="headlink.header"
      :content-inset-level="1"
      dark
      class="light text-bold"
    >
      <q-item
        clickable
        class="title light"
        v-for="link in headlink.links"
        :to="link.link"
        :key="link.title"
      >
        <q-item-section>
          <q-item-label class="text-weight-regular">
            {{ link.title }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-expansion-item>
  </q-list>
</template>
<script setup>
import { useLinksStore } from "src/stores/links";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const expanded = defineModel();

const linkStore = useLinksStore();
const { links } = storeToRefs(linkStore);
const { retrieveAuthroizedLinks } = linkStore;
const router = useRouter();

const goHome = () => {
  router.push("/dw");
};

onMounted(() => {
  retrieveAuthroizedLinks();
});
</script>
<style scoped>
.light {
  color: white;
}
</style>
