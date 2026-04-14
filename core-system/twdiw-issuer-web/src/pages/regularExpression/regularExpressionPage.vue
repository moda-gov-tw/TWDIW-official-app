<template>
  <basic-page>
    <q-table
      :rows="regularExpressionList"
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
          <div :class="isMobile ? 'col-12 q-mt-md' : 'col-auto'">
            <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
              <q-btn
                unelevated
                color="primary"
                icon="add"
                :label="t('regularExpression.add')"
                @click="openDialog('new')"
                class="col"
              />
            </div>
          </div>
        </table-top-dropdown>
      </template>

      <template v-slot:body-cell-edit="props">
        <q-td style="width: 50px" :props="props">
          <q-btn
            unelevated
            color="indigo-4"
            dense
            icon="edit"
            @click="openDialog('edit', props.row)"
            :disable="props.row.type === 'specified'"
          />
        </q-td>
      </template>

      <template v-slot:body-cell-delete="props">
        <q-td style="width: 50px" :props="props">
          <q-btn
            unelevated
            color="primary"
            dense
            icon="delete"
            @click="deleteRow(props.row)"
            :disable="props.row.type === 'specified'"
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

    <confirm-dialog
      v-model="showDeleteDialog"
      :content="
        t('regularExpression.notice.delete', { input: currentRow?.name })
      "
      @confirm="onDeleteOk"
      @cancel="onDeleteCancel"
      :confirm-loading="deleteLoading"
    />
  </basic-page>
</template>

<script setup>
import { ref, onMounted, computed, onBeforeUnmount } from "vue";
import { useI18n } from "vue-i18n";
import ConfirmDialog from "src/components/ConfirmDialog.vue";
import { useQuasar } from "quasar";
import { storeToRefs } from "pinia";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();

import { useRegularExpressionStore } from "stores/regularExpression";
import createEditDialog from "pages/regularExpression/createEditDialog.vue";
import TableTopDropdown from "src/components/TableTopDropdown.vue";
import { useUserConfigStore } from "stores/userConfig";

const { t } = useI18n();
const $q = useQuasar();
const regularExpressionStore = useRegularExpressionStore();
const userConfig = useUserConfigStore();
const { fromAPIorgId } = storeToRefs(userConfig);
const {
  regularExpressionList,
  pagination,
  loading,
  deleteLoading,
  filter,
  defaultFormState
} = storeToRefs(regularExpressionStore);

const {
  getRegularExpressionList,
  deleteRegularExpression,
  resetPagination,
  resetRegularExpressionList,
  applySearch,
  resetSearchForm
} = regularExpressionStore;

// plugin settings
const showDeleteDialog = ref(false);
const currentRow = ref(null);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const title = t("regularExpression.title");

// 表格欄位定義
const baseColumns = [
  {
    name: "id",
    label: "#",
    align: "center",
    field: "id",
    required: true,
    sortable: true,
    style: "width: 80px"
  },
  {
    name: "type",
    label: t("regularExpression.table.type"),
    align: "center",
    field: (row) => typeChange(row.type),
    required: true,
    sortable: true,
    style: "width: 100px"
  },
  {
    name: "name",
    label: t("regularExpression.table.name"),
    sortable: false,
    field: "name",
    align: "left",
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  {
    name: "description",
    label: t("regularExpression.table.description"),
    sortable: false,
    field: "description",
    align: "left",
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  {
    name: "edit",
    label: t("edit"),
    field: "delete",
    align: "center"
  },
  {
    name: "delete",
    label: t("delete"),
    field: "delete",
    align: "center"
  }
];

const inputArray = computed(() => {
  return [
    {
      paramsName: "name",
      state: "input",
      class: "q-mb-sm",
      col: "col-12",
      label: t("regularExpression.table.name"),
      rules: rulesAll("regularExpressionDropdownNameRules")
    },
    {
      paramsName: "description",
      state: "input",
      class: "q-mb-sm",
      col: "col-12",
      label: t("regularExpression.table.description"),
      rules: rulesAll("regularExpressionDropdownDescriptionRules")
    },
    {
      paramsName: "type",
      state: "select",
      class: "q-mb-md",
      col: "col-12",
      label: t("regularExpression.table.type"),
      rules: rulesAll(),
      options: [
        {
          label: t("regularExpression.select.custom"),
          value: fromAPIorgId.value
        },
        { label: t("regularExpression.select.specified"), value: "specified" }
      ]
    }
  ];
});

const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns.map((col) => ({
      ...col,
      style:
        col.name === "name" || col.name === "description"
          ? "width: 100%"
          : col.style
    }));
  }
  return baseColumns;
});

// 處理表格請求
const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;
  await getRegularExpressionList(page, rowsPerPage, sortBy, descending, filter);
};

const localSearchForm = ref({ ...defaultFormState });

const onSubmitSearch = (formData) => {
  applySearch(formData);
};

const onResetSearch = () => {
  resetSearchForm();
};

// 開啟編輯對話框
const openDialog = (type, row) => {
  $q.dialog({
    component: createEditDialog,
    componentProps: {
      type,
      row
    }
  });
};

// 根據類型值返回對應的類型名稱
const typeChange = (val) => {
  return t(
    `regularExpression.select.${val === "specified" ? "specified" : "custom"}`
  );
};

// 刪除行
const deleteRow = async (row) => {
  currentRow.value = row;
  showDeleteDialog.value = true;
};

// 確認刪除
const onDeleteOk = async () => {
  try {
    await deleteRegularExpression(currentRow.value);
    showDeleteDialog.value = false;
  } catch (error) {
    console.error("delete error:", error);
  }
};

// 修改取消刪除的處理函数
const onDeleteCancel = () => {
  currentRow.value = null;
  showDeleteDialog.value = false;
};

// 元件掛載時初始化資料
onMounted(() => {
  getRegularExpressionList(1, 10);
});

// 組件卸載前重置狀態
onBeforeUnmount(() => {
  resetSearchForm();
  resetPagination();
  resetRegularExpressionList();
});
</script>

<style scoped>
.mobile-btns {
  width: 100%;
  display: flex;
  gap: 8px;
}

.mobile-btns .btn {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.desktop-btns {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.text-content {
  word-break: break-all;
  overflow-wrap: break-word;
  max-width: 100%;
  padding: 0 8px;
}
</style>
