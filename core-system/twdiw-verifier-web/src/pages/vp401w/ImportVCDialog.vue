<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black column"
      style="max-width: 1500px; width: 800px"
      :style="{ height: '80vh', 'max-height': '100vh' }"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">
          {{
            props.isEdit
              ? t("vp.dialog.title.editVp")
              : t("vp.dialog.title.createVp")
          }}
        </div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <div class="col q-px-xs" v-if="!show">
        <q-scroll-area
          class="fit"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#4E4E4E',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <q-card-section style="max-width: 800px; margin: 0 auto">
            <p class="text-h5 q-ml-none">
              {{ t("vp.importVc.selectVcTemplate") }}
            </p>
            <q-form ref="formRef" greedy class="q-pa-sm">
              <div class="flex">
                <p class="text-h6">{{ t("vp.importVc.orgName") }}：</p>
                <q-select
                  use-input
                  fill-input
                  input-debounce="0"
                  v-model="org"
                  :input-value="orgInput"
                  :options="categoriesVDRList"
                  @filter="filterCategories"
                  outlined
                  dense
                  hide-bottom-space
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  behavior="menu"
                  :style="{ height: '68px', width: '100%' }"
                  :disable="categoriesLoading"
                  :loading="categoriesLoading"
                  :popup-content-style="{
                    height: getPopupHeightCategory,
                    width: '280px'
                  }"
                  option-label="name"
                  option-value="taxId"
                  @update:model-value="(val) => handleBusinessId(val)"
                  @update:input-value="(val) => (orgInput = val)"
                >
                  <template v-slot:selected>
                    {{ orgInput }}
                  </template>
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps">
                      <q-item-section>
                        <q-item-label>{{ scope.opt.name }}</q-item-label>
                        <q-item-label caption>{{
                          scope.opt.taxId
                        }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </template>

                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section class="text-grey">
                        {{ t("noResult") }}
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </div>
            </q-form>
            <!-- 已選擇的 VC -->
            <div class="bg-white q-px-sm">
              <div class="q-py-sm text-subtitle1 text-weight-bold">
                <span
                  >{{
                    t("vp.importVc.selectedVcTemplates", {
                      count: selectedCount
                    })
                  }}
                </span>
              </div>
              <div
                class="row q-ma-none q-pa-sm round-sm bg-grey-11 inset-shadow scroll q-gutter-md"
                style="max-height: 20vh"
              >
                <template v-if="!selected || selected.length === 0">
                  <span class="q-ma-none text-grey-4">{{
                    t("vp.importVc.noSelectedVc")
                  }}</span>
                </template>
                <template v-else>
                  <template
                    v-for="vcData in selected"
                    :key="`selected-${vcData.serialNo}`"
                  >
                    <div class="q-ma-none q-pa-xs">
                      <div
                        class="q-pa-sm q-pr-md round-sm bg-white shadow-1 relative-position"
                      >
                        <span
                          >{{ vcData.name }}（{{ vcData.businessName }}）</span
                        >
                        <q-icon
                          name="cancel"
                          color="primary"
                          size="xs"
                          class="cursor-pointer absolute-top-left"
                          @click="removeSelected(vcData)"
                          style="transform: translate(-50%, -50%)"
                        />
                      </div>
                    </div>
                  </template>
                </template>
              </div>
            </div>
            <q-table
              :rows="filteredRows"
              :columns="isMobileDetailColumns"
              dense
              flat
              class="sticky-header q-my-md q-px-sm no-height"
              :row-key="rowKeyFn"
              :rows-per-page-options="[10, 20, 50, 0]"
              selection="multiple"
              v-model:selected="selected"
              :loading="categoriesSpecialListLoading"
            >
              <template v-slot:top>
                <q-btn-dropdown
                  color="white"
                  text-color="black"
                  class="search-dropdown q-mb-sm ellipsis-label"
                  flat
                  dropdown-icon="none"
                  rounded
                  ref="dropdownRef"
                  :style="dropdownStyle"
                >
                  <template v-slot:label>
                    <div class="ellipsis-wrapper">
                      <q-icon name="search" class="q-mr-sm" />
                      <span class="ellipsis-text">
                        {{ searchQuery || t("filterCondition") }}
                      </span>
                    </div>
                  </template>
                  <q-form ref="searchFormRef" @submit="submitSearch">
                    <q-card class="search-form">
                      <q-card-section>
                        <q-input
                          v-model.trim="searchForm.vcname"
                          :label="t('vp.importVc.vcName.label')"
                          outlined
                          class="q-mb-sm"
                          dense
                          hide-bottom-space
                          :rules="[
                            (val) =>
                              !val ||
                              /^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(val) ||
                              t('vp.importVc.vcName.pattern'),
                            (val) =>
                              !val ||
                              val.length <= 19 ||
                              t('validation.maxLength', { max: 19 })
                          ]"
                          :style="{ height: '60px' }"
                        />
                        <q-input
                          v-model.trim="searchForm.serialNo"
                          :label="t('vp.importVc.vcSerialNo.label')"
                          class="q-mb-sm"
                          outlined
                          dense
                          hide-bottom-space
                          :rules="[
                            (val) =>
                              !val ||
                              /^[a-z0-9_]+$/.test(val) ||
                              t('vp.importVc.vcSerialNo.pattern'),
                            (val) =>
                              !val ||
                              val.length <= 35 ||
                              t('validation.maxLength', { max: 35 })
                          ]"
                          :style="{ height: '60px' }"
                        />
                        <div class="row justify-end q-gutter-sm">
                          <q-btn
                            :label="t('reset')"
                            outline
                            class="text-primary"
                            @click="resetForm"
                          />
                          <q-btn
                            :label="t('filter')"
                            unelevated
                            color="primary"
                            type="submit"
                          />
                        </div>
                      </q-card-section>
                    </q-card>
                  </q-form>
                </q-btn-dropdown>
              </template>
              <template #pagination="scope">
                <table-pagination
                  :scope="scope"
                  :rows-number="computedPagination"
                />
              </template>
              <template v-slot:loading>
                <q-inner-loading showing color="primary" style="top: 100px" />
              </template>
            </q-table>
          </q-card-section>
        </q-scroll-area>
      </div>
      <q-card-actions align="right" class="q-pa-md row q-gutter-sm justify-end">
        <q-btn
          :label="t('cancel')"
          outline
          unelevated
          class="text-primary"
          style="width: 120px"
          @click="onCancelClick"
          :disable="cateoriesVCItemFieldLoading"
        />
        <q-btn
          :label="t('confirm')"
          color="primary"
          unelevated
          @click="onOKClick"
          style="width: 120px"
          :loading="cateoriesVCItemFieldLoading"
          :disable="cateoriesVCItemFieldLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import { useVpStore } from "stores/vp";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const vpStore = useVpStore();
const {
  categoriesLoading,
  categoriesVDRList,
  categoriesSpecialListLoading,
  cateoriesSpecialList,
  cateoriesVCItemFieldLoading,
  cateoriesVCItemFieldsList
} = storeToRefs(vpStore);
const { getCategoriesVDR, getCateoriesSpecialList, getCateoriesVCItemFields } =
  vpStore;

const $n = useNotify();
const $q = useQuasar();
const show = ref(false);
const formRef = ref(null);
const selected = ref([]);
const filteredSerialNoList = ref([]);
const filter = ref("");
const dropdownRef = ref(null);
const searchQuery = ref("");
const searchFormRef = ref(null);

const org = ref(null);
const orgInput = ref("");
const allCategories = ref([]);

const rowKeyFn = (row) => `${row.businessId}_${row.serialNo}`;

const props = defineProps({
  isEdit: Boolean,
  selectedVcDatas: Object
});

const searchForm = ref({
  vcname: "",
  serialNo: ""
});

const validateAllInputs = async () => {
  // 驗證 formRef
  const isFormValid = await formRef.value.validate();

  return isFormValid;
};

const onDialogShow = async () => {
  await getCategoriesVDR();
  allCategories.value = [...categoriesVDRList.value];
};

const onCancelClick = () => {
  onDialogCancel();
};

const baseColumns = [
  {
    name: "name",
    label: t("vp.importVc.vcName.label"),
    field: "name",
    required: true,
    sortable: true,
    align: "left"
  },
  {
    name: "serialNo",
    label: t("vp.importVc.vcSerialNo.label"),
    field: "serialNo",
    required: true,
    sortable: true,
    align: "left"
  }
];

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 組織 可輸入篩選
const filterCategories = (val, update) => {
  update(() => {
    const needle = val.toLowerCase();
    categoriesVDRList.value = allCategories.value.filter(
      (item) =>
        item.name.toLowerCase().includes(needle) ||
        item.taxId.toString().toLowerCase().includes(needle)
    );
  });
};

const getPopupHeightCategory = computed(() => {
  const length = categoriesVDRList.value.length;
  if (length >= 5) {
    return "200px";
  }
  return `${length * 48}px`;
});

const computedPagination = computed(() => {
  return filteredRows.value?.length || 0;
});

const handleBusinessId = (val) => {
  org.value = val;
  const selected = categoriesVDRList.value.find((item) => item.taxId === val);
  orgInput.value = selected?.name || "";
  clearFilter();
  getCateoriesSpecialList(val);
};

const onDialogHide = () => {
  cateoriesSpecialList.value = [];
  filteredSerialNoList.value = [];
};

const filteredRows = computed(() => {
  return filterMethod(cateoriesSpecialList.value || [], filter.value);
});

const filterMethod = (rows, terms) => {
  if (!terms || typeof terms === "string") {
    return rows;
  }

  if (!terms.serialNo && !terms.vcname) {
    return rows;
  }

  return rows.filter((row) => {
    let matches = true;

    if (terms.serialNo) {
      matches =
        matches &&
        row.serialNo.toLowerCase().includes(terms.serialNo.toLowerCase());
    }

    if (terms.vcname) {
      matches = matches && row.name?.toLowerCase().includes(terms.vcname);
    }
    return matches;
  });
};

// 清空搜尋條件
const clearFilter = () => {
  // 清空表單值
  searchForm.value = {
    serialNo: "",
    vcname: ""
  };
  // 清空過濾條件
  filter.value = {
    serialNo: "",
    vcname: ""
  };
  searchQuery.value = "";
};

const desktopDetailStyles = {
  serialNo: "width: 240px; white-space: normal; word-break: break-word",
  name: "width: 240px; white-space: normal; word-break: break-word"
};

const isMobileDetailColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns;
  }

  return baseColumns.map((col) => ({
    ...col,
    style: desktopDetailStyles[col.name] || col.style
  }));
});

const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: "300px",
  textTransform: "none",
  maxWidth: "100%"
}));

const resetForm = () => {
  searchFormRef.value.reset();
  // 清空表單值
  searchForm.value = {
    serialNo: "",
    vcname: ""
  };
  // 清空過濾條件
  filter.value = {
    serialNo: "",
    vcname: ""
  };
  // 清空搜尋顯示文字
  searchQuery.value = "";
};

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (isValid) {
    searchQuery.value = searchForm.value.vcname || searchForm.value.serialNo;

    filter.value = {
      vcname: searchForm.value.vcname.toLowerCase(),
      serialNo: searchForm.value.serialNo.toLowerCase()
    };

    dropdownRef.value.hide();
  }
};

const removeSelected = (row) => {
  selected.value = selected.value.filter((item) => item !== row);
};

const selectedCount = computed(() => selected.value.length);

const onOKClick = async () => {
  // 進行驗證
  const isValid = await validateAllInputs();

  if (isValid) {
    // VC 模板數量檢核，不可多於 10 個 VC 模板
    if (selected.value.length > 10) {
      $n.error(t("vp.importVc.maxSelectVc"));
      return;
    }

    for (const item of selected.value) {
      const params = {
        serialNo: item.serialNo,
        taxId: item.businessId
      };

      await getCateoriesVCItemFields(params);
      item.vcItemFields = [...cateoriesVCItemFieldsList.value];
    }

    onDialogOK(selected.value);
  } else {
    $n.error(t("requiredFields"));
  }
};

onMounted(() => {
  // 初始化顯示當筆已選擇資料
  selected.value = props.selectedVcDatas;
});
</script>

<style scoped>
.pt-4 {
  padding-top: 4px;
}

/* 添加這個樣式來置中勾選框 */
:deep(.q-table tbody td:first-child),
:deep(.q-table thead tr th:first-child) {
  text-align: center;
  padding-left: 24px !important;
  padding-right: 0;
}

.text-ellipsis-input :deep(.q-field__native) {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.text-ellipsis-input :deep(.q-field__control) {
  overflow: hidden;
}

.titlePaddingRight {
  padding-right: 64px;
}

.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

:deep(.q-item__label) {
  white-space: normal;
  word-break: break-word;
  line-height: 1.2;
}
</style>
