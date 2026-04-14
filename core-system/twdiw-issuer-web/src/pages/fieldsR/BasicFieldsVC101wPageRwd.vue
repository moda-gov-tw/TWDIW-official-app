<template>
  <basic-page>
    <q-table
      :rows="fieldList"
      :columns="isMobileColumns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="onRequest"
      binary-state-sort
      dense
      flat
      class="sticky-header no-height"
      :no-data-label="t('noData')"
      :no-results-label="t('noResult')"
      :rows-per-page-options="pagination.rowsPerPageOptions"
    >
      <template v-slot:top>
        <table-top-dropdown
          :title="title"
          :inputArray="inputArray"
          v-model="localSearchForm"
          @submit="onSubmitSearch"
          @reset="onResetSearch"
        >
        </table-top-dropdown>
      </template>

      <template v-slot:body-cell-visible="props">
        <q-td :props="props">
          <q-checkbox
            size="xs"
            color="indigo-4"
            keep-color
            v-model="props.row.visible"
            @update:model-value="updateVisible(props.row)"
          />
        </q-td>
      </template>

      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:no-data="{ message }">
        <div class="full-width row flex-center q-gutter-sm">
          <span>
            {{ message }}
          </span>
        </div>
      </template>

      <template #pagination="scope">
        <table-pagination :scope="scope" />
      </template>
    </q-table>
  </basic-page>
  <ConfirmDialog
    v-model="showDialog"
    :content="dialogContent"
    @confirm="onOk"
    @cancel="onCancel"
    :confirm-loading="changeLoading"
  />
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from "vue";
import { useI18n } from "vue-i18n";
import { useQuasar } from "quasar";
import { useFieldStore } from "stores/field";
import { storeToRefs } from "pinia";
import ConfirmDialog from "src/components/ConfirmDialog.vue";
import TableTopDropdown from "src/components/TableTopDropdown.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const $q = useQuasar();
const { t } = useI18n();

const fieldStore = useFieldStore();
const {
  fieldList,
  loading,
  pagination,
  changeLoading,
  filter,
  defaultFormState
} = storeToRefs(fieldStore);

const {
  getFieldList,
  changeState,
  resetPagination,
  resetFieldList,
  applySearch,
  resetSearchForm
} = fieldStore;

const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const baseColumns = [
  {
    name: "id",
    label: "#",
    align: "center",
    field: "id",
    required: true,
    sortable: true,
    style: "width: 100px"
  },
  {
    name: "type",
    label: t("basicFields.column.type"),
    align: "left",
    field: "type",
    required: true,
    style: "width: 100px;",
    format: (val) => (val === "BASIC" ? t("basicFields.title") : val)
  },
  {
    name: "cname",
    label: t("basicFields.column.cname"),
    sortable: false,
    field: "cname",
    align: "left",
    style: "width: 580px; white-space: normal; word-break: break-word"
  },
  {
    name: "ename",
    label: t("basicFields.column.ename"),
    sortable: false,
    field: "ename",
    align: "left",
    style: "width: 580px; white-space: normal; word-break: break-word"
  },
  {
    name: "visible",
    label: t("basicFields.column.visible"),
    sortable: true,
    field: "visible",
    align: "center"
  }
];

// 根據裝置類型動態調整欄位寬度
const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns.map((col) => ({
      ...col,
      style:
        col.name === "cname" || col.name === "ename"
          ? "width: 100px;"
          : col.style
    }));
  }
  return baseColumns;
});

const title = t("basicFields.title");

const inputArray = [
  {
    paramsName: "cname",
    state: "input",
    col: "col-12",
    class: "q-mb-sm",
    label: t("basicFields.column.cname"),
    rules: rulesAll("cnameRules")
  },
  {
    paramsName: "ename",
    state: "input",
    col: "col-12",
    class: "q-mb-sm",
    label: t("basicFields.column.ename"),
    rules: rulesAll("enameRules")
  },
  {
    paramsName: "visible",
    state: "select",
    col: "col-12",
    class: "q-mb-md",
    label: t("basicFields.column.visible"),
    rules: rulesAll(),
    options: [
      { label: t("basicFields.show.true"), value: true },
      { label: t("basicFields.show.false"), value: false }
    ]
  }
];

const localSearchForm = ref({ ...defaultFormState });

const onSubmitSearch = (formData) => {
  applySearch(formData);
};

const onResetSearch = () => {
  resetSearchForm();
};

// 處理表格資料請求
// 根據分頁、排序和篩選條件獲取資料
const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const { ename, cname, visible } = props.filter;

  await getFieldList("BASIC", page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    cname: cname || "",
    ename: ename || "",
    visible: visible
  });
};

// 顯示狀態管理相關
const showDialog = ref(false);
const currentRow = ref(null);

// 觸發確認對話框
const updateVisible = (row) => {
  currentRow.value = row;
  showDialog.value = true;
};

// 對話框內容
const dialogContent = computed(() => {
  if (!currentRow.value) return "";
  return t("basicFields.message.visible", {
    show: t(`basicFields.show.${currentRow.value.visible}`),
    field: currentRow.value.cname
  });
});

// 確認變更
const onOk = async () => {
  const isVisible = currentRow.value.visible;
  const isSuccess = await changeState(currentRow.value, isVisible, "BASIC");
  if (isSuccess) {
    showDialog.value = false;
  } else {
    currentRow.value.visible = !isVisible;
  }
};

// 取消變更
const onCancel = () => {
  currentRow.value.visible = !currentRow.value.visible;
  showDialog.value = false;
};

// 元件掛載時初始化資料
onMounted(() => {
  getFieldList("BASIC", 1, pagination.value.rowsPerPage);
});

// 元件卸載時清理資源
onBeforeUnmount(() => {
  resetSearchForm();
  resetPagination();
  resetFieldList();
});
</script>
