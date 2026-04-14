<template>
  <div class="q-pa-md">
    <q-table
      :rows="roleChanges"
      :columns="columns"
      row-key="id"
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
        <div class="row full-width justify-between items-end q-pb-sm">
          <div class="col-grow">
            <!-- 角色異動清單查詢 -->
            <div class="titleRwd q-my-md">{{ t("roleChange.title") }}</div>
            <div class="row full-width justify-between items-end">
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
                <!-- search bar dialog-->
                <q-form ref="searchFormRef" @submit="submitSearch">
                  <q-card>
                    <q-card-section>
                      <q-input
                        v-model="searchForm.roleId"
                        :label="t('roleChange.table.code')"
                        outlined
                        class="q-mb-sm"
                        dense
                        hide-bottom-space
                        :style="{ height: '60px' }"
                      />

                      <q-select
                        v-model="searchForm.logType"
                        :label="t('roleChange.table.logType')"
                        :options="[
                          { label: t('add'), value: 'add' },
                          { label: t('modify'), value: 'modify' },
                          { label: t('delete'), value: 'delete' }
                        ]"
                        outlined
                        hide-bottom-space
                        class="q-mb-sm"
                        dense
                        :style="{ height: '60px' }"
                      />
                      <div class="row q-col-gutter-md q-mb-md">
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.beginDate"
                            :label="t('roleChange.search.beginDate')"
                            outlined
                            dense
                            type="datetime"
                          >
                            <template v-slot:append>
                              <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy
                                  cover
                                  transition-show="scale"
                                  transition-hide="scale"
                                >
                                  <q-date
                                    v-model="searchForm.beginDate"
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
                            :label="t('roleChange.search.endDate')"
                            outlined
                            dense
                            type="datetime"
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
                          outline
                          class="text-primary"
                          @click="resetForm"
                        />
                        <q-btn
                          unelevated
                          :label="t('filter')"
                          color="primary"
                          type="submit"
                        />
                      </div>
                    </q-card-section>
                  </q-card>
                </q-form>
              </q-btn-dropdown>
            </div>
          </div>
        </div>
      </template>

      <template v-slot:body-cell-state="props">
        <q-td style="width: 100px; min-width: 100px">
          <div class="row items-center">
            <q-badge
              :color="props.value === '0' ? 'grey-3' : 'positive'"
              class="q-pa-sm text-weight-bold"
              :label="props.value === '0' ? t('disabled') : t('enabled')"
            />
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-logType="props">
        <q-td style="width: 100px; min-width: 100px">
          <div class="row items-center">
            <q-badge
              class="q-pa-sm text-weight-bold"
              :color="
                props.value === 'modify'
                  ? 'orange-6'
                  : props.value === 'add'
                  ? 'red-2'
                  : 'indigo-6'
              "
              :label="t(`${props.value}`)"
            />
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-information="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            icon="search"
            color="indigo-4"
            dense
            @click="showRoleChangeInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template #pagination="scope">
        <table-pagination :scope="scope" />
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
    </q-table>
    <RoleChangeInformationDialog></RoleChangeInformationDialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import { useQuasar } from "quasar";
import RoleChangeInformationDialog from "./RoleChangeDetail.vue";

// store
const store = useRoleChangeStore();
import { useRoleChangeStore } from "stores/roleChange";
import { storeToRefs } from "pinia";
const { roleChanges, loading, pagination, currentFilter, isOpenDetail } =
  storeToRefs(store);
const { selectRoleChange, getRoleChanges } = store;

// plugin settings
const $q = useQuasar();
const $n = useNotify();
const $d = useDialog();
const { t } = useI18n();
const filter = ref("");

const columns = [
  {
    name: "roleId",
    label: t("roleChange.table.code"),
    sortable: true,
    field: "roleId",
    align: "left"
  },
  {
    name: "roleName",
    label: t("roleChange.table.name"),
    sortable: true,
    field: "roleName",
    align: "left"
  },
  {
    name: "description",
    label: t("roleChange.table.desc"),
    sortable: true,
    field: "description",
    align: "left"
  },
  {
    name: "state",
    label: t("roleChange.table.state"),
    sortable: true,
    field: "state",
    align: "left"
  },
  {
    name: "logType",
    label: t("roleChange.table.logType"),
    sortable: true,
    field: "logType",
    align: "left"
  },
  {
    name: "logTime",
    label: t("roleChange.table.logTime"),
    sortable: true,
    field: "logTime",
    align: "left"
  },
  {
    name: "information",
    label: t("view"),
    field: "information",
    align: "center"
  }
];

const isMobile = computed(() => {
  return $q.screen.lt.md;
});

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

  await getRoleChanges(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    roleId: filter?.roleId,
    logType: filter?.logType,
    beginDate: filter?.beginDate,
    endDate: filter?.endDate
  });
}

const showRoleChangeInformationDialog = (row) => {
  selectRoleChange(row);
  isOpenDetail.value = true;
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  roleId: "",
  logType: "",
  beginDate: "",
  endDate: ""
});

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (searchForm.value.beginDate && searchForm.value.endDate) {
    const startDate = new Date(searchForm.value.beginDate);
    const endDate = new Date(searchForm.value.endDate);

    if (startDate > endDate) {
      $n.error(t("valid.date"));
      return;
    }
  }

  if (isValid) {
    // Update search display text
    if (searchForm.value.roleId) {
      searchQuery.value = searchForm.value.roleId;
    } else if (
      searchForm.value.logType?.value === "add" ||
      searchForm.value.logType?.value === "modify" ||
      searchForm.value.logType?.value === "delete"
    ) {
      searchQuery.value = t(
        `roleChange.logType.${searchForm.value.logType?.value}`
      );
    } else if (searchForm.value.beginDate && searchForm.value.endDate) {
      searchQuery.value = t("roleChange.rangeTime");
    } else {
      searchQuery.value = t("filterCondition");
    }

    const newFilter = {
      roleId: searchForm.value.roleId,
      logType: searchForm.value.logType.value,
      beginDate: getStartDate(),
      endDate: getEndDate()
    };

    function getStartDate() {
      if (
        (searchForm.value.beginDate && searchForm.value.endDate) ||
        (searchForm.value.beginDate && !searchForm.value.endDate)
      ) {
        return searchForm.value.beginDate;
      } else if (!searchForm.value.beginDate && searchForm.value.endDate) {
        return "1970-01-01";
      } else {
        return null;
      }
    }

    function getEndDate() {
      if (
        (searchForm.value.beginDate && searchForm.value.endDate) ||
        (!searchForm.value.beginDate && searchForm.value.endDate)
      ) {
        return searchForm.value.endDate;
      } else if (searchForm.value.beginDate && !searchForm.value.endDate) {
        const today = new Date();
        const formattedToday = today.toISOString().split("T")[0];
        return formattedToday;
      } else {
        return null;
      }
    }

    filter.value = newFilter;
    currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

function resetForm() {
  searchForm.value = {
    roleId: "",
    logType: "",
    beginDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  // 清空 store 中的搜索條件
  currentFilter.value = {
    roleId: "",
    logType: "",
    beginDate: "",
    endDate: ""
  };
}

onMounted(() => {
  getRoleChanges(1, {
    size: pagination.value.rowsPerPage,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    filter: filter.value
  });
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
