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
          <div :class="isMobile ? 'col-12 q-mt-md' : 'col-auto'">
            <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
              <q-btn
                unelevated
                color="primary"
                icon="add"
                :label="t('normalFields.add')"
                class="btn"
                @click="showDialog"
              />
            </div>
          </div>
        </table-top-dropdown>
      </template>

      <template v-slot:body-cell-information="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            unelevated
            dense
            icon="menu"
            color="indigo-4"
            @click="showInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-visible="props">
        <q-td class="q-gutter-x-sm" :props="props">
          <q-checkbox
            size="sm"
            keep-color
            color="indigo-4"
            v-model="props.row.visible"
            @update:model-value="updateVisible(props.row)"
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

  <confirm-dialog
    v-model="showVisibleDialog"
    :content="
      currentRow?.visible
        ? t('normalFields.visible.confirmShow', { name: currentRow?.cname })
        : t('normalFields.visible.confirmHide', { name: currentRow?.cname })
    "
    @confirm="onVisibleOk"
    @cancel="onVisibleCancel"
    :confirm-loading="changeLoading"
  />

  <confirm-dialog
    v-model="showDeleteDialog"
    :content="t('normalFields.delete.confirm', { name: currentRow?.cname })"
    @confirm="onDeleteOk"
    @cancel="onDeleteCancel"
    :confirm-loading="deleteLoading"
  />
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useQuasar } from "quasar";
import NormalFieldsDialog from "./NormalFieldsCreateDialog.vue";
import { useFieldStore } from "stores/field";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import NormalInformationDialog from "./NormalInformationDialog.vue";
import ConfirmDialog from "src/components/ConfirmDialog.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const { t } = useI18n();
const $q = useQuasar();
const fieldStore = useFieldStore();

const {
  fieldList,
  loading,
  pagination,
  deleteLoading,
  changeLoading,
  filter,
  defaultFormState
} = storeToRefs(fieldStore);

const {
  getFieldList,
  changeState,
  deleteField,
  resetFieldList,
  resetPagination,
  applySearch,
  resetSearchForm
} = fieldStore;

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 表格欄位設定
const baseColumns = [
  {
    name: "id",
    required: true,
    label: "#",
    align: "center",
    field: "id",
    sortable: true,
    style: "width: 100px;"
  },
  {
    name: "type",
    label: t("basicFields.column.type"),
    field: "type",
    align: "center",
    style: "width: 100px;",
    format: (val) => (val === "NORMAL" ? t("normalFields.title") : val)
  },
  {
    name: "cname",
    label: t("basicFields.column.cname"),
    field: "cname",
    align: "left",
    style: "width: 420px; white-space: normal; word-break: break-word"
  },
  {
    name: "ename",
    label: t("basicFields.column.ename"),
    field: "ename",
    align: "left",
    style: "width: 420px; white-space: normal; word-break: break-word"
  },
  {
    name: "visible",
    label: t("basicFields.column.visible"),
    field: "visible",
    align: "left",
    sortable: true,
    style: "width: 100px;"
  },
  {
    name: "information",
    label: t("normalFields.column.information"),
    field: "infomation",
    align: "center",
    style: "width: 100px;"
  },
  {
    name: "delete",
    label: t("delete"),
    field: "delete",
    align: "center",
    style: "width: 100px;"
  }
];

// 根據裝置類型返回對應的欄位設定
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

const title = t("normalFields.title");

const inputArray = [
  {
    paramsName: "cname",
    state: "input",
    class: "q-mb-sm",
    col: "col-12",
    label: t("basicFields.column.cname"),
    rules: rulesAll("cnameRules")
  },
  {
    paramsName: "ename",
    state: "input",
    class: "q-mb-sm",
    col: "col-12",
    label: t("basicFields.column.ename"),
    rules: rulesAll("enameRules")
  },
  {
    paramsName: "visible",
    state: "select",
    class: "q-mb-md",
    col: "col-12",
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

// 對話框相關狀態和操作
const showVisibleDialog = ref(false);
const showDeleteDialog = ref(false);
const currentRow = ref(null);

// 更新欄位可見狀態
const updateVisible = (row) => {
  currentRow.value = row;
  showVisibleDialog.value = true;
};

// 刪除欄位操作
const deleteRow = (row) => {
  currentRow.value = row;
  showDeleteDialog.value = true;
};

// 確認修改可見狀態
const onVisibleOk = async () => {
  try {
    await changeState(currentRow.value, currentRow.value.visible, "NORMAL");
  } catch (error) {
    console.error(`${t("normalFields.error.visible")}：${error}`);
  } finally {
    showVisibleDialog.value = false;
  }
};

// 取消修改可見狀態
const onVisibleCancel = () => {
  currentRow.value.visible = !currentRow.value.visible;
  showVisibleDialog.value = false;
};

// 確認刪除欄位
const onDeleteOk = async () => {
  try {
    await deleteField(currentRow.value);
  } catch (error) {
    console.error(t("normalFields.error.delete") + "：" + error);
  } finally {
    showDeleteDialog.value = false;
  }
};

// 取消刪除欄位
const onDeleteCancel = () => {
  showDeleteDialog.value = false;
};

// 顯示新增欄位對話框
const showDialog = () => {
  $q.dialog({
    component: NormalFieldsDialog
  })
    .onOk((rows) => {
      console.log("Dialog closed");
    })
    .onCancel(() => {
      console.log("Cancel");
    });
};

// 處理表格請求
const onRequest = async (props) => {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const { ename, cname, visible } = props.filter;

  await getFieldList("NORMAL", page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    cname: cname || "",
    ename: ename || "",
    visible: visible
  });
};

// 顯示資訊對話框的函數
const showInformationDialog = (row) => {
  $q.dialog({
    component: NormalInformationDialog,
    componentProps: {
      cname: row.cname,
      ename: row.ename,
      row: row.regularExpression
    }
  });
};

const getList = () => {
  getFieldList("NORMAL", 1, pagination.value.rowsPerPage);
};

onMounted(() => {
  getList();
});

onBeforeUnmount(() => {
  resetSearchForm();
  resetPagination();
  resetFieldList();
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
