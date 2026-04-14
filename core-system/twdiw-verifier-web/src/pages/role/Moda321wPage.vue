<template>
  <basic-page>
    <RoleTable ref="table" />
  </basic-page>
  <role-detail />
  <role-edit @edited="onEdited" />
</template>
<script setup>
import { ref } from "vue";

import RoleTable from "./RoleTable.vue";
import RoleDetail from "./RoleDetail.vue";
import RoleEdit from "./RoleEdit.vue";

const table = ref(null);

const onEdited = () => {
  table.value.refreshTable();
};
</script>
