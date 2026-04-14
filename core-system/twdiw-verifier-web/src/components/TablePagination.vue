<template>
  <div class="row items-center justify-start">
    <span class="q-mx-sm">
      {{
        scope.pagination.rowsPerPage === 0
          ? `1-${scope.pagination.rowsNumber || rowsNumber}`
          : `${
              (scope.pagination.page - 1) * scope.pagination.rowsPerPage + 1
            }-${Math.min(
              scope.pagination.page * scope.pagination.rowsPerPage,
              scope.pagination.rowsNumber || rowsNumber
            )}`
      }}
      列，共 {{ scope.pagination.rowsNumber || rowsNumber }}列
    </span>
  </div>

  <q-btn
    icon="skip_previous"
    color="grey-8"
    dense
    flat
    :disable="scope.isFirstPage"
    @click="scope.firstPage"
  />

  <q-btn
    icon="navigate_before"
    color="grey-8"
    dense
    flat
    :disable="scope.isFirstPage"
    @click="scope.prevPage"
  />

  <q-btn
    icon="navigate_next"
    color="grey-8"
    dense
    flat
    :disable="scope.isLastPage"
    @click="scope.nextPage"
  />

  <q-btn
    icon="skip_next"
    color="grey-8"
    dense
    flat
    :disable="scope.isLastPage"
    @click="scope.lastPage"
  />
</template>
<script setup>
import { defineProps } from "vue";

const props = defineProps(["scope", "rowsNumber"]);
</script>
