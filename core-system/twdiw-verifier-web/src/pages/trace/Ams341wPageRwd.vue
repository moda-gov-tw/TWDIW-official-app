<template>
  <div class="q-pa-md">
    <q-table
      :rows="traces"
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
            <div class="titleRwd q-my-md">{{ t("trace.title") }}</div>
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
                      <div class="row q-col-gutter-md q-mb-md">
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.source"
                            :label="t('trace.table.requestIp')"
                            outlined
                            class="q-mb-sm"
                            dense
                            hide-bottom-space
                            :style="{ height: '60px' }"
                          />
                        </div>
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.jhiFrom"
                            :label="t('trace.table.form')"
                            outlined
                            class="q-mb-sm"
                            dense
                            hide-bottom-space
                            :style="{ height: '60px' }"
                          />
                        </div>
                      </div>
                      <div class="row q-col-gutter-md q-mb-md">
                        <div class="col-12 col-md-6">
                          <div class="col-12 col-md-6">
                            <q-input
                              v-model="searchForm.uri"
                              :label="t('trace.table.uri')"
                              outlined
                              class="q-mb-sm"
                              dense
                              hide-bottom-space
                              :style="{ height: '60px' }"
                            />
                          </div>
                        </div>
                        <div class="col-12 col-md-6">
                          <q-select
                            v-model="searchForm.statusCode"
                            :label="t('trace.table.statusCode')"
                            :options="[
                              { label: t('success'), value: 'success' },
                              { label: t('fail'), value: 'fail' }
                            ]"
                            outlined
                            hide-bottom-space
                            class="q-mb-sm"
                            dense
                            :style="{ height: '60px' }"
                          />
                        </div>
                      </div>
                      <div class="row q-col-gutter-md q-mb-md">
                        <div class="col-12 col-md-6">
                          <q-input
                            v-model="searchForm.beginDate"
                            :label="t('accountChange.search.beginDate')"
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
                            :label="t('accountChange.search.endDate')"
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

      <template v-slot:body-cell-statusCode="props">
        <q-td :props="props">
          <div>
            <q-badge
              :color="
                props.value === '200' ||
                props.value === '201' ||
                props.value === '202' ||
                props.value === '204' ||
                props.value === '0'
                  ? 'positive'
                  : 'grey-3'
              "
              :label="
                props.value === '200' ||
                props.value === '201' ||
                props.value === '202' ||
                props.value === '204' ||
                props.value === '0'
                  ? 'Success(' + props.value + ')'
                  : 'Fail(' + props.value + ')'
              "
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
            @click="showTraceInformationDialog(props.row)"
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
    <TraceInformationDialog></TraceInformationDialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify } from "src/utils/plugin";
import { useQuasar } from "quasar";
import TraceInformationDialog from "./TraceDetail.vue";

// store
const store = useTraceStore();
import { useTraceStore } from "stores/trace";
import { storeToRefs } from "pinia";

const { traces, loading, pagination, currentFilter, isOpenDetail } =
  storeToRefs(store);
const { selectTrace, getTraces } = store;

// plugin settings
const $q = useQuasar();
const $n = useNotify();
const { t } = useI18n();
const filter = ref("");

const columns = [
  {
    name: "jhiFrom",
    label: t("trace.table.form"),
    sortable: true,
    field: "jhiFrom",
    align: "left"
  },
  {
    name: "source",
    label: t("trace.table.requestIp"),
    sortable: true,
    field: "source",
    align: "left"
  },
  {
    name: "uri",
    label: t("trace.table.uri"),
    sortable: true,
    field: "uri",
    align: "left"
  },
  {
    name: "statusCode",
    label: t("trace.table.statusCode"),
    sortable: true,
    field: "statusCode",
    align: "left"
  },
  {
    name: "rtt",
    label: t("trace.table.RTT"),
    sortable: true,
    field: "rtt",
    align: "left"
  },
  {
    name: "timestamp",
    label: t("trace.table.createTime"),
    sortable: true,
    field: "timestamp",
    align: "left"
  },
  {
    name: "information",
    label: "檢視",
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

  await getTraces(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    source: filter?.source,
    jhiFrom: filter?.jhiFrom,
    uri: filter?.uri,
    statusCode: filter?.statusCode,
    beginDate: filter?.beginDate,
    endDate: filter?.endDate
  });
}

const showTraceInformationDialog = (row) => {
  selectTrace(row);
  isOpenDetail.value = true;
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);

const searchQuery = ref("");
const searchForm = ref({
  source: "",
  jhiFrom: "",
  uri: "",
  statusCode: "",
  beginDate: "",
  endDate: ""
});

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (searchForm.value.beginDate && searchForm.value.endDate) {
    const startDate = new Date(searchForm.value.beginDate);
    const endDate = new Date(searchForm.value.endDate);

    if (startDate > endDate) {
      $n.error(t("dateField.errors.startDateAfterEndDate"));
      return;
    }
  }

  if (isValid) {
    // Update search display text
    if (searchForm.value.source) {
      searchQuery.value = searchForm.value.source;
    } else if (searchForm.value.jhiFrom) {
      searchQuery.value = searchForm.value.jhiFrom;
    } else if (searchForm.value.uri) {
      searchQuery.value = searchForm.value.uri;
    } else if (searchForm.value.statusCode?.value === "success") {
      searchQuery.value = t("trace.statusSuccess");
    } else if (searchForm.value.statusCode?.value === "fail") {
      searchQuery.value = t("trace.statusFail");
    } else if (searchForm.value.beginDate && searchForm.value.endDate) {
      searchQuery.value = t("dateField.searchQuery");
    } else {
      searchQuery.value = t("filterCondition");
    }

    const newFilter = {
      source: searchForm.value.source,
      jhiFrom: searchForm.value.jhiFrom,
      uri: searchForm.value.uri,
      statusCode: searchForm.value.statusCode.value,
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
    source: "",
    jhiFrom: "",
    uri: "",
    statusCode: "",
    beginDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  // 清空 store 中的搜索條件
  currentFilter.value = {
    source: "",
    jhiFrom: "",
    uri: "",
    statusCode: "",
    beginDate: "",
    endDate: ""
  };
}

onMounted(() => {
  getTraces(1, {
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
