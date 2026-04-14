<template>
  <basic-page>
    <q-table
      :rows="orgList"
      :columns="columns"
      row-key="orgId"
      dense
      flat
      :hide-header="loading"
      class="sticky-header"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="onRequest"
      binary-state-sort
      :rows-per-page-options="pagination.rowsPerPageOptions"
    >
      <template v-slot:top>
        <div class="row q-ma-none q-pa-none items-center">
          <p class="titleRwd q-mb-md">{{ t("title.orgManagement") }}</p>
          <q-space></q-space>
          <div class="q-px-sm"></div>
        </div>
        <div class="row full-width justify-between">
          <q-btn-dropdown
            color="white"
            text-color="black"
            class="search-dropdown ellipsis-label"
            flat
            dropdown-icon="none"
            rounded
            :style="dropdownStyle"
            :menu-offset="menuOffset"
            ref="dropdownRef"
          >
            <template v-slot:label>
              <div class="ellipsis-wrapper">
                <q-icon name="search" class="q-mr-sm" />
                <span class="ellipsis-text">{{
                  searchQuery || t("filterCondition")
                }}</span>
              </div>
            </template>
            <q-form ref="searchFormRef" @submit="submitSearch">
              <q-card>
                <q-card-section>
                  <div class="row q-col-gutter-md q-mb-ls">
                    <div class="col-12 col-md-6">
                      <q-input
                        v-model="searchForm.orgTwName"
                        :label="t('org.orgTwName.label')"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :rules="[
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9\u4e00-\u9fa5.-]+$/.test(val) ||
                            t('org.orgTwName.pattern')
                        ]"
                        :style="{ height: '60px' }"
                      />
                    </div>
                    <div class="col-12 col-md-6">
                      <q-input
                        v-model="searchForm.orgEnName"
                        :label="t('org.orgEnName.label')"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :rules="[
                          (val) =>
                            !val ||
                            /^[a-zA-Z0-9]+$/.test(val) ||
                            t('org.orgEnName.pattern')
                        ]"
                        :style="{ height: '60px' }"
                      />
                    </div>
                  </div>
                  <q-input
                    v-model="searchForm.orgId"
                    :label="t('org.orgId.label')"
                    outlined
                    class="q-mb-sm"
                    dense
                    hide-bottom-space
                    :rules="[
                      (val) =>
                        !val || /^[0-9]+$/.test(val) || t('org.orgId.pattern')
                    ]"
                    :style="{ height: '60px' }"
                  />
                  <div class="row q-col-gutter-md q-mb-md">
                    <div class="col-12 col-md-6">
                      <q-input
                        v-model="searchForm.startDate"
                        :label="t('createTime.startDate')"
                        outlined
                        dense
                        type="datetime"
                        :rules="rulesAll('dateRules')"
                      >
                        <template v-slot:append>
                          <q-icon name="event" class="cursor-pointer">
                            <q-popup-proxy
                              cover
                              transition-show="scale"
                              transition-hide="scale"
                            >
                              <q-date
                                v-model="searchForm.startDate"
                                mask="YYYY-MM-DD"
                                today-btn
                                minimal
                              >
                                <div class="row items-center justify-end">
                                  <q-btn
                                    v-close-popup
                                    :label="t('close')"
                                    color="primary"
                                    flat
                                  />
                                </div>
                              </q-date>
                            </q-popup-proxy>
                          </q-icon>
                        </template>
                      </q-input>
                    </div>
                    <div class="col-12 col-md-6">
                      <q-input
                        v-model="searchForm.endDate"
                        :label="t('createTime.endDate')"
                        outlined
                        dense
                        type="datetime"
                        :rules="rulesAll('dateRules')"
                      >
                        <template v-slot:append>
                          <q-icon name="event" class="cursor-pointer">
                            <q-popup-proxy
                              cover
                              transition-show="scale"
                              transition-hide="scale"
                            >
                              <q-date
                                v-model="searchForm.endDate"
                                mask="YYYY-MM-DD"
                                minimal
                                today-btn
                              >
                                <div class="row items-center justify-end">
                                  <q-btn
                                    v-close-popup
                                    :label="t('close')"
                                    color="primary"
                                    flat
                                  />
                                </div>
                              </q-date>
                            </q-popup-proxy>
                          </q-icon>
                        </template>
                      </q-input>
                    </div>
                  </div>
                  <div class="row justify-end q-gutter-sm">
                    <q-btn
                      :label="t('reset')"
                      class="text-primary"
                      outline
                      @click="resetForm"
                    />
                    <q-btn
                      unelevated
                      :label="t('filter')"
                      color="primary"
                      type="submit"
                      :disable="loading"
                      :loading="loading"
                    />
                  </div>
                </q-card-section>
              </q-card>
            </q-form>
          </q-btn-dropdown>
        </div>
      </template>

      <template v-slot:body-cell-information="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            icon="search"
            color="indigo-4"
            dense
            @click="showOrgInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-edit="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            v-show="props.row.orgId !== 'default'"
            icon="edit"
            color="indigo-4"
            dense
            @click="showOrgEditDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-delete="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            v-show="props.row.orgId !== 'default' && !props.row.isDidOrg"
            icon="delete"
            color="indigo-4"
            dense
            @click="onDeleteOrg(props.row)"
          ></q-btn>
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

      <!-- footer -->
      <template v-slot:pagination="scope">
        <!-- 顯示分頁訊息 -->
        <span class="q-mx-md">
          {{
            t("account.table.row", {
              start:
                (scope.pagination.page - 1) * scope.pagination.rowsPerPage + 1,
              end: Math.min(
                scope.pagination.page * scope.pagination.rowsPerPage,
                scope.pagination.rowsNumber
              ),
              total: scope.pagination.rowsNumber
            })
          }}
        </span>

        <q-btn
          icon="skip_previous"
          color="grey-8"
          round
          dense
          flat
          :disable="scope.isFirstPage"
          @click="scope.firstPage"
        />

        <q-btn
          icon="chevron_left"
          color="grey-8"
          round
          dense
          flat
          :disable="scope.isFirstPage"
          @click="scope.prevPage"
        />

        <q-btn
          icon="chevron_right"
          color="grey-8"
          round
          dense
          flat
          :disable="scope.isLastPage"
          @click="scope.nextPage"
        />

        <q-btn
          icon="skip_next"
          color="grey-8"
          round
          dense
          flat
          :disable="scope.isLastPage"
          @click="scope.lastPage"
        />
      </template>
    </q-table>
    <CreateOrgDialog :modelValue="alert"></CreateOrgDialog>
    <OrgInformationDialog
      :modelValue="showOrgInformation"
    ></OrgInformationDialog>
    <OrgEditDialog :modelValue="showOrgEdit"></OrgEditDialog>
  </basic-page>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import CreateOrgDialog from "./CreateOrgDialog.vue";
import OrgInformationDialog from "./OrgInformationDialog.vue";
import OrgEditDialog from "./OrgEditDialog.vue";

const { t } = useI18n();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// store
import { useQuasar } from "quasar";
import { useOrgStore } from "stores/org";
import { storeToRefs } from "pinia";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const $q = useQuasar();
const store = useOrgStore();
const { orgList, orgDetail, loading, pagination } = storeToRefs(store);
const { getOrgList, deleteOrg } = store;

// plugin settings
const $n = useNotify();
const $d = useDialog();

const filter = ref("");
const alert = ref(false);
const showOrgInformation = ref(false);
const showOrgEdit = ref(false);

const columns = [
  {
    name: "orgTwName",
    label: t("org.orgTwName.label"),
    field: "orgTwName",
    align: "left",
    sortable: true,
    style: "width: 30%; white-space: normal; word-break: break-word"
  },
  {
    name: "orgEnName",
    label: t("org.orgEnName.label"),
    field: "orgEnName",
    align: "left",
    sortable: true,
    style: "width: 30%; white-space: normal; word-break: break-word"
  },
  {
    name: "orgId",
    required: true,
    label: t("org.orgId.label"),
    align: "left",
    field: (row) => row.orgId,
    format: (val) => `${val}`,
    sortable: true,
    style: "width: 20%; white-space: normal;"
  },
  {
    name: "createTime",
    label: t("createTime.label"),
    field: "createTime",
    align: "left",
    sortable: true,
    style: "width: 20%; white-space: normal; word-break: break-word"
  },
  {
    name: "information",
    label: t("view"),
    field: "information",
    align: "center"
  },
  {
    name: "edit",
    label: t("edit"),
    field: "edit",
    align: "center"
  },
  {
    name: "delete",
    label: t("delete"),
    field: "delete",
    align: "center"
  }
];

const dropdownStyle = computed(() => ({
  border: "1px solid rgba(0, 0, 0, 0.12)",
  width: isMobile.value ? "100%" : "300px",
  maxWidth: "100%",
  textTransform: "none"
}));

const menuOffset = computed(() => {
  return isMobile.value ? [150, 8] : [0, 0]; // [x偏移, y偏移]
});

async function onRequest(props) {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;

  // 調用更新後的 getFieldList
  await getOrgList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    orgId: filter?.orgId,
    orgTwName: filter?.orgTwName,
    orgEnName: filter?.orgEnName,
    startDate: filter?.startDate,
    endDate: filter?.endDate
  });
}

const showDialog = () => {
  $q.dialog({
    component: CreateOrgDialog
  })
    .onOk(async (data) => {
      console.log("Dialog closed");
    })
    .onCancel(() => {
      console.log("Cancel");
    });
};

const showOrgInformationDialog = (row) => {
  $q.dialog({
    component: OrgInformationDialog,
    componentProps: {
      orgId: row.orgId,
      orgTwName: row.orgTwName,
      orgEnName: row.orgEnName,
      vcDataSource: row.vcDataSource
    }
  });
};

const showOrgEditDialog = (row) => {
  $q.dialog({
    component: OrgEditDialog,
    componentProps: {
      id: row.id,
      orgId: row.orgId,
      orgTwName: row.orgTwName,
      orgEnName: row.orgEnName,
      vcDataSource: row.vcDataSource
    }
  });
};

const onDeleteOrg = (row) => {
  $d.confirm(
    t("confirm"),
    t("org.confirmDelete", { name: row.orgTwName }),
    async () => {
      const { isDeleteOK } = await deleteOrg(row);
      if (isDeleteOK) {
        $n.success(t("org.deleteSuccess", { name: row.orgTwName }));
      }
    }
  );
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  orgId: "",
  orgTwName: "",
  orgEnName: "",
  startDate: "",
  endDate: ""
});

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (searchForm.value.startDate && searchForm.value.endDate) {
    const startDate = new Date(searchForm.value.startDate);
    const endDate = new Date(searchForm.value.endDate);

    if (startDate > endDate) {
      $n.error(t("dateField.errors.startDateAfterEndDate"));
      return;
    }
  }

  if (isValid) {
    // Update search display text
    let query = "";
    if (searchForm.value.orgId) {
      query = searchForm.value.orgId;
    } else if (searchForm.value.orgTwName) {
      query = searchForm.value.orgTwName;
    } else if (searchForm.value.orgEnName) {
      query = searchForm.value.orgEnName;
    } else if (searchForm.value.startDate && searchForm.value.endDate) {
      query = t("dateField.searchQuery");
    }
    searchQuery.value = query;

    const newFilter = {
      orgId: searchForm.value.orgId,
      orgTwName: searchForm.value.orgTwName,
      orgEnName: searchForm.value.orgEnName,
      startDate: getStartDate(),
      endDate: getEndDate()
    };

    function getStartDate() {
      if (
        (searchForm.value.startDate && searchForm.value.endDate) ||
        (searchForm.value.startDate && !searchForm.value.endDate)
      ) {
        return searchForm.value.startDate;
      } else if (!searchForm.value.startDate && searchForm.value.endDate) {
        return "1970-01-01";
      } else {
        return null;
      }
    }

    function getEndDate() {
      if (
        (searchForm.value.startDate && searchForm.value.endDate) ||
        (!searchForm.value.startDate && searchForm.value.endDate)
      ) {
        return searchForm.value.endDate;
      } else if (searchForm.value.startDate && !searchForm.value.endDate) {
        const today = new Date();
        const formattedToday = today.toISOString().split("T")[0];
        return formattedToday;
      } else {
        return null;
      }
    }

    filter.value = newFilter;
    store.currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

function resetForm() {
  searchForm.value = {
    orgId: "",
    orgTwName: "",
    orgEnName: "",
    startDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  // 清空 store 中的搜索條件
  store.currentFilter.value = {
    orgId: "",
    orgTwName: "",
    orgEnName: ""
  };
}

onMounted(() => {
  orgList.value = []; // 載入頁面後先清空組織清單
  orgDetail.value = []; // 載入頁面後先清空組織詳情
  getOrgList(1, {
    size: pagination.value.rowsPerPage,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    filter: filter.value
  });
});

onUnmounted(() => {
  orgList.value = []; // 組件完全卸載後清空組織清單
  orgDetail.value = []; // 組件完全卸載後清空組織詳情
});
</script>
<style scoped>
.ellipsis-label .ellipsis-wrapper {
  max-width: calc(100% - 24px);
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
}

.ellipsis-text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

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
</style>
