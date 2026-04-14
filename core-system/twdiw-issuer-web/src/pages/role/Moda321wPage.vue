<template>
  <basic-page>
    <role-table ref="table" />
  </basic-page>
  <role-detail />
  <role-edit @edited="refreshTable" />
</template>
<script setup>
import { ref } from "vue";
import RoleTable from "./RoleTable.vue";
import RoleDetail from "./RoleDetail.vue";
import RoleEdit from "./RoleEdit.vue";

const table = ref(null);

const refreshTable = () => {
  table.value.refreshTable();
};
</script>
