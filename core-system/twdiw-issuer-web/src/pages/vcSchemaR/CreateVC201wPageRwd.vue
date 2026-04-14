<template>
  <basic-page>
    <q-table
      :rows="vcSchemaList"
      :columns="isMobileColumns"
      row-key="id"
      dense
      flat
      class="sticky-header no-height"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="onRequest"
      binary-state-sort
      :no-data-label="t('noDataLabel')"
      :no-results-label="t('noResultsLabel')"
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
          <div :class="isMobile ? 'col-12' : 'col-auto'">
            <div :class="isMobile ? 'col-12 q-mt-md' : 'col-auto q-ml-md'">
              <div :class="isMobile ? 'mobile-btns' : 'desktop-btns'">
                <q-btn
                  :disable="!issuerDID"
                  unelevated
                  color="primary"
                  icon="add"
                  :label="t('vcSchema.add')"
                  @click="showDialog"
                  class="col"
                >
                  <q-tooltip
                    class="bg-orange-7 text-body2"
                    anchor="top right"
                    self="bottom right"
                    :offset="[0, 5]"
                    v-if="!issuerDID"
                  >
                    {{ t("vcSchema.notice.first") }}
                  </q-tooltip>
                </q-btn>
              </div>
            </div>
          </div>
        </table-top-dropdown>
      </template>

      <template v-slot:body-cell-owner="props">
        <q-td style="width: 80px" :props="props">
          <q-btn
            v-if="props.row.owner && !props.row.isTemp && props.row.activated"
            unelevated
            color="indigo-4"
            dense
            icon="edit"
            @click="showEditVCServiceUrlDialog(props.row)"
          />
          <q-btn
            v-if="props.row.owner && props.row.isTemp && props.row.activated"
            unelevated
            dense
            icon="edit"
            color="orange-6"
            @click="showTempVCDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-edit="props">
        <q-td class="q-gutter-x-sm" :props="props">
          <q-btn
            unelevated
            color="indigo-4"
            dense
            icon="edit"
            @click="showEditVCDataSourceDialog(props.row)"
            :disable="checkDisable(props.row)"
          />
        </q-td>
      </template>

      <!-- 詳細資訊 -->
      <template v-slot:body-cell-information="props">
        <q-td :props="props">
          <q-btn
            unelevated
            dense
            icon="menu"
            color="indigo-4"
            @click="showVCInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <!-- 產生 VC 資料 -->
      <template v-slot:body-cell-build="props">
        <q-td :props="props">
          <q-btn
            v-if="!props.row.isTemp && props.row.activated && !isProductionEnv"
            unelevated
            @click="vCDataInformationDialog(props.row)"
            dense
            color="indigo-4"
            icon="play_arrow"
          ></q-btn>
        </q-td>
      </template>

      <!-- 刪除 -->
      <template v-slot:body-cell-delete="props">
        <q-td :props="props">
          <!-- 已停止發行 -->
          <q-badge
            v-if="!props.row.activated"
            :color="props.row.activated ? 'positive' : 'grey-3'"
            class="q-pa-sm text-weight-bold"
            :label="t('vcSchema.stopIssuing')"
          />

          <!-- 已啟用且已發卡 -->
          <q-btn
            v-if="props.row.activated && props.row.used"
            dense
            :icon="props.row.activated ? 'block' : 'play_arrow'"
            @click="stopIssuing(props.row)"
          >
            <q-tooltip :delay="700">
              {{ t("vcSchema.tooltip.disableVcSchema") }}
            </q-tooltip>
          </q-btn>

          <!-- 已啟用且未發卡 -->
          <q-btn
            v-if="props.row.activated && !props.row.used"
            unelevated
            color="primary"
            dense
            icon="delete"
            @click="deleteRow(props.row)"
            :disable="checkDisable(props.row)"
          >
            <q-tooltip :delay="700">
              {{ t("vcSchema.tooltip.deleteVcSchema") }}
            </q-tooltip>
          </q-btn>
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
      :content="t('vcSchema.notice.delete', { field: currentRow?.name })"
      @confirm="onDeleteOk"
      @cancel="onDeleteCancel"
      :confirm-loading="deleteLoading"
    />

    <confirm-dialog
      v-model="showStopIssuingDialog"
      :content="t('vcSchema.notice.stopIssuing', { field: currentRow?.name })"
      @confirm="onStopIssuingOk"
      :confirm-loading="stopIssuingLoading"
    />
  </basic-page>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import CreateVCDialog from "./CreateVCDialog.vue";
import VCInformationDialog from "./VCInformationDialog.vue";
import VCDataInformationDialog from "./VCDataInformationDialog.vue";
import EditVCDataSourceDialog from "./EditVCDataSourceDialog.vue";
import { format } from "utils/dateFormat";
import ConfirmDialog from "src/components/ConfirmDialog.vue";
import TableTopDropdown from "src/components/TableTopDropdown.vue";
import EditVCServiceUrlDialog from "./EditVCServiceUrlDialog.vue";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();

const { t } = useI18n();
const { yyyyMMddHHmmss } = format();

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// store
import { useQuasar } from "quasar";
import { useVcSchemaStore } from "stores/vcSchema";
import { useUserConfigStore } from "stores/userConfig";
import { storeToRefs } from "pinia";

const $q = useQuasar();
const vcSchema = useVcSchemaStore();
const userConfig = useUserConfigStore();
const {
  vcSchemaList,
  loading,
  pagination,
  deleteLoading,
  stopIssuingLoading,
  filter,
  defaultFormState,
  vcSourceType,
  issuerDID,
  currentEnv
} = storeToRefs(vcSchema);
const {
  getVCSchemaList,
  createVCData,
  removeVCSchema,
  stopIssuingVCSchema,
  resetPagination,
  resetVcShemaList,
  applySearch,
  resetSearchForm,
  getStaticQRCode,
  getVCSchemaDetail,
  getVersionInfo
} = vcSchema;
const { fromAPIorgId } = storeToRefs(userConfig);
const title = t("vcSchema.createVC");

// 是否為 Production 環境
const isProductionEnv = computed(() =>
  ["prod", "release"].includes(currentEnv.value)
);

const baseColumns = [
  {
    name: "name",
    label: t("vcSchema.table.name"),
    field: "name",
    align: "left",
    sortable: true
  },
  {
    name: "serialNo",
    label: t("vcSchema.table.serialNo"),
    field: "serialNo",
    align: "left",
    sortable: true
  },
  {
    name: "org",
    label: t("vcSchema.table.org"),
    field: (row) => row.businessTWName,
    sortable: true,
    align: "center",
    style: "width: 480px;"
  },
  {
    name: "crDatetime",
    label: t("vcSchema.table.crDatetime"),
    field: "crDatetime",
    format: (val) => yyyyMMddHHmmss(val),
    align: "center",
    sortable: true
  },
  {
    name: "owner",
    label: t("vcSchema.table.owner"),
    field: "owner",
    align: "center",
    sortable: true
  },

  {
    name: "information",
    label: t("vcSchema.table.information"),
    field: "information",
    align: "center"
  },
  {
    name: "build",
    label: t("vcSchema.table.build"),
    field: "buildVCData",
    align: "center"
  },
  {
    name: "delete",
    label: t("vcSchema.table.deleteStopIssuing"),
    field: "delete",
    align: "center"
  }
];

const isMobileColumns = computed(() => {
  let columns = [...baseColumns];

  // 正式環境不顯示 產生VC資料 欄位
  if (currentEnv == "" || isProductionEnv.value || loading.value) {
    columns = columns.filter((col) => col.name !== "build");
  }

  // 只有當 vcSourceType 是 901 且 fromAPIorgId 不是 '00000000' 時，才添加編輯欄位
  if (vcSourceType.value === 901 && fromAPIorgId.value !== "00000000") {
    const editColumn = {
      name: "edit",
      label: t("vcSchema.edit"),
      field: "edit",
      align: "center"
    };

    const informationIndex = columns.findIndex(
      (col) => col.name === "information"
    );
    columns.splice(informationIndex, 0, editColumn);
  }

  if (!isMobile.value) {
    columns = columns.map((col) => ({
      ...col,
      style: getColumnStyle.value[col.name] || col.style
    }));
  }

  return columns;
});

const getColumnStyle = computed(() => {
  if (isMobile.value) {
    return {};
  }

  return {
    org: "min-width: 140px; width: 480px; white-space: normal; word-break: break-word",
    serialNo: "width: 480px; white-space: normal; word-break: break-word",
    name: "width: 480px; white-space: normal; word-break: break-word",
    crDatetime: "width: 220px; white-space: normal; word-break: break-word"
  };
});

const inputArray = [
  {
    paramsName: "name",
    state: "input",
    class: "q-mb-sm",
    col: "col-12",
    label: t("vcSchema.table.name"),
    rules: rulesAll("vcSchemaNameRules")
  },
  {
    paramsName: "serialNo",
    state: "input",
    class: "q-mb-sm",
    col: "col-12",
    label: t("vcSchema.table.serialNo"),
    rules: rulesAll("vcSchemaSerialNoRules")
  },
  {
    paramsName: "orgTwName",
    state: "input",
    class: "q-mb-sm",
    col: "col-12",
    label: t("vcSchema.orgTwName"),
    rules: rulesAll("vcSchemaOrgTwNameRules")
  },
  {
    paramsName: ["startDate", "endDate"],
    state: "dateRange",
    class: "q-mb-mb",
    labels: {
      start: t("vcSchema.startDate"),
      end: t("vcSchema.endDate")
    },
    rules: rulesAll("dateRules")
  }
];

async function onRequest(props) {
  const { page, rowsPerPage, sortBy, descending } = props.pagination;
  const filter = props.filter;

  await getVCSchemaList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    org: filter?.org,
    serialNo: filter?.serialNo,
    name: filter?.name,
    startDate: filter?.startDate,
    endDate: filter?.endDate
  });
}

const showDialog = () => {
  $q.dialog({
    component: CreateVCDialog
  })
    .onOk(async (data) => {
      console.log("Dialog closed");
    })
    .onCancel(() => {
      console.log("Cancel");
    });
};

const showTempVCDialog = (row) => {
  getVCSchemaDetail(row.id).then((schemaDetail) => {
    $q.dialog({
      component: CreateVCDialog,
      componentProps: {
        id: row.id,
        businessId: row.businessId,
        serialNo: row.serialNo,
        crDatetime: row.crDatetime,
        businessTWName: row.businessTWName,
        name: row.name,
        unitTypeExpire: row.unitTypeExpire,
        lengthExpire: row.lengthExpire,
        ialTemp: row.ial,
        isVerifyTemp: row.isVerify,
        isTemp: row.isTemp,
        schemaDetail: schemaDetail,
        isNeedQRCodeTemp: row.type ? true : false,
        typeTemp: row.type,
        issuerServiceUrlTemp: row.issuerServiceUrl
      }
    });
  });
};

const showVCInformationDialog = async (row) => {
  let qrCode = {};
  let staticQRCode = false;
  // 有使用靜態 QR Code
  if (row.type && row.issuerServiceUrl) {
    staticQRCode = true;
    // 取得靜態QR Code
    qrCode = await getStaticQRCode(`${row.businessId}_${row.serialNo}`);
  }

  $q.dialog({
    component: VCInformationDialog,
    componentProps: {
      id: row.id,
      businessId: row.businessId,
      serialNo: row.serialNo,
      crDatetime: row.crDatetime,
      businessTWName: row.businessTWName,
      name: row.name,
      unitTypeExpire: row.unitTypeExpire,
      lengthExpire: row.lengthExpire,
      ial: row.ial,
      isVerify: row.isVerify,
      staticQRCode: staticQRCode,
      type: row.type,
      issuerServiceUrl: row.issuerServiceUrl,
      qrCode: qrCode,
      isTemp: row.isTemp,
      activated: row.activated
    }
  });
};

const vCDataInformationDialog = (row) => {
  $q.dialog({
    component: VCDataInformationDialog,
    componentProps: {
      id: row.id,
      serialNo: row.serialNo,
      name: row.name,
      unitTypeExpire: row.unitTypeExpire,
      lengthExpire: row.lengthExpire,
      isVerify: row.isVerify,
      crDatetime: row.crDatetime
    }
  })
    .onOk(async (props) => {
      await createVCData(props);
    })
    .onCancel(() => {
      console.log("Cancel");
    })
    .onDismiss(() => {
      console.log("Dismissed");
    });
};

const showEditVCServiceUrlDialog = (row) => {
  $q.dialog({
    component: EditVCServiceUrlDialog,
    componentProps: {
      row: row
    }
  }).onOk(() => {
    console.log("Ok");
  });
};

const showEditVCDataSourceDialog = (row) => {
  $q.dialog({
    component: EditVCDataSourceDialog,
    componentProps: {
      vcId: row.id,
      name: row.name,
      serialNo: row.serialNo,
      apiType: row.apiType,
      headers: row.headers,
      httpMethod: row.httpMethod,
      url: row.url,
      lengthExpire: row.lengthExpire,
      unitTypeExpire: row.unitTypeExpire
    }
  })
    .onOk(() => {
      console.log("Ok");
    })
    .onCancel(() => {
      console.log("Cancel");
    })
    .onDismiss(() => {
      console.log("Dismissed");
    });
};

const localSearchForm = ref({ ...defaultFormState });

const onSubmitSearch = (formData) => {
  applySearch(formData);
};

const onResetSearch = () => {
  resetSearchForm();
};

const showDeleteDialog = ref(false);
const showStopIssuingDialog = ref(false);
const currentRow = ref(null);

const deleteRow = (row) => {
  currentRow.value = row;
  showDeleteDialog.value = true;
};

const onDeleteCancel = () => {
  showDeleteDialog.value = false;
};

const onDeleteOk = async () => {
  try {
    await removeVCSchema(currentRow.value);
    showDeleteDialog.value = false;
  } catch (error) {
    console.error("delete error:", error);
  }
};

const stopIssuing = (row) => {
  currentRow.value = row;
  showStopIssuingDialog.value = true;
};

// 停止發行
const onStopIssuingOk = async () => {
  try {
    await stopIssuingVCSchema(currentRow.value);
    showStopIssuingDialog.value = false;
  } catch (error) {
    console.error("stopIssuing error:", error);
  }
};

const showMobileInfo = ref(false);

// 設置裝置類型的 ref
const deviceType = ref(null);

const checkDevice = () => {
  const userAgent = navigator.userAgent.toLowerCase();

  return {
    isIOS: /iphone|ipad|ipod/.test(userAgent),
    isAndroid: /android/.test(userAgent)
  };
};

onMounted(async () => {
  getVersionInfo();
  await getVCSchemaList();

  const { isAndroid, isIOS } = checkDevice();

  if (isIOS) {
    deviceType.value = "ios";
  } else if (isAndroid) {
    deviceType.value = "android";
  } else {
    deviceType.value = "other";
  }
});

onBeforeUnmount(() => {
  resetSearchForm();
  resetPagination();
  resetVcShemaList();
});

const checkDisable = (props) => {
  return fromAPIorgId.value !== props.businessId;
};
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
</style>
