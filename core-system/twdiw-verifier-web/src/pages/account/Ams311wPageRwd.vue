<template>
  <basic-page>
    <!-- table rowData -->
    <q-table
      :rows="accountList"
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
      <!-- 上方元件 -->
      <template v-slot:top>
        <div class="row q-ma-none q-pa-none items-center">
          <!-- 帳號管理 -->
          <p class="titleRwd q-mb-md">{{ t("title.accountManagement") }}</p>
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
            <!-- search bar -->
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
                    v-model="searchForm.userId"
                    :label="t('account.table.userId')"
                    outlined
                    class="q-mb-sm"
                    dense
                    hide-bottom-space
                    :rules="[
                      (val) =>
                        !val ||
                        /^[a-zA-Z0-9._%+-@]+$/.test(val) ||
                        t('validation.onlyEnNumAllowedAnd', {
                          symbols: '._%+-@'
                        }),
                      customMaxLengthRule(50)
                    ]"
                    :style="{ height: '60px' }"
                  />

                  <q-select
                    v-model="searchForm.state"
                    :label="t('account.table.enabled')"
                    :options="[
                      { label: t('enabled'), value: t('enabled') },
                      { label: t('disabled'), value: t('disabled') }
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
          <div :class="$q.screen.lt.md ? 'col-12 q-mt-md row' : 'col-auto'">
            <!-- 顯示/ 隱藏 帳號-->
            <q-btn
              class="col"
              unelevated
              color="primary"
              :icon="isVisible ? 'visibility' : 'visibility_off'"
              @click="isVisible = !isVisible"
              flat
            />
            <q-btn
              class="col q-ml-sm"
              unelevated
              color="primary"
              icon="add"
              :label="t('account.btn.create')"
              @click="showDialog"
            />
          </div>
        </div>

        <div></div>
      </template>

      <!-- 啟用狀態 -->
      <template v-slot:body-cell-state="props">
        <q-td style="width: 100px; min-width: 100px">
          <!-- 檢查是否需要顯示信封圖案 -->
          <div
            v-if="
              props.row.activationKey === 'true' &&
              props.row.resetKey === 'true'
            "
            class="row items-center"
          >
            <q-badge
              color="orange-6"
              class="q-pa-sm text-weight-bold"
              :label="t('account.unverified')"
            />
            <div class="q-pr-sm"></div>
            <q-btn
              dense
              size="sm"
              icon="mail"
              @click="handleResendActivationEmail(props.row)"
            >
              <q-tooltip :delay="700">{{
                t("account.btn.resendActivationEmail")
              }}</q-tooltip>
            </q-btn>
          </div>

          <!-- 原本的狀態切換邏輯 -->
          <div v-else class="row items-center">
            <q-badge
              :color="props.value === '0' ? 'grey-3' : 'positive'"
              class="q-pa-sm text-weight-bold"
              :label="props.value === '0' ? t('disabled') : t('enabled')"
            />
            <div class="q-pr-sm"></div>
            <q-btn
              dense
              size="sm"
              :icon="props.value === '0' ? 'play_arrow' : 'block'"
              @click="handleStateToggle(props.row)"
            >
              <q-tooltip :delay="700">
                {{
                  props.value === "0"
                    ? t("account.activate.true")
                    : t("account.activate.false")
                }}
              </q-tooltip>
            </q-btn>
          </div>
        </q-td>
      </template>

      <!-- 建立時間 -->
      <template v-slot:body-cell-createTime="props">
        <q-td :props="props">
          {{ yyyyMMddHHmmss(props.row.createTime) }}
        </q-td>
      </template>

      <!-- 檢視 -->
      <template v-slot:body-cell-information="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            dense
            icon="search"
            @click="showAccountInformationDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <!-- 編輯 -->
      <template v-slot:body-cell-edit="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            dense
            icon="edit"
            @click="showAccountEditDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <!-- 授權角色 -->
      <template v-slot:body-cell-roleConfiguration="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            dense
            icon="checklist"
            @click="showAccountEditRoleDialog(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <!-- 重製密碼 -->
      <template v-slot:body-cell-resetBwd="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            dense
            icon="key"
            @click="handleResetBwd(props.row)"
            :disable="
              props.row.activationKey === 'true' &&
              props.row.resetKey === 'true'
            "
          ></q-btn>
        </q-td>
      </template>

      <!-- 刪除帳號 -->
      <template v-slot:body-cell-deleteAccount="props">
        <q-td :props="props" style="width: 50px">
          <q-btn
            color="indigo-4"
            dense
            icon="delete"
            @click="handleDeleteAccount(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <!-- footer -->
      <template v-slot:pagination="scope">
        <div class="row items-center justify-start">
          <!-- 顯示分頁訊息 -->
          <span class="q-mx-sm">
            {{
              t("account.table.row", {
                start:
                  (scope.pagination.page - 1) * scope.pagination.rowsPerPage +
                  1,
                end: Math.min(
                  scope.pagination.page * scope.pagination.rowsPerPage,
                  scope.pagination.rowsNumber
                ),
                total: scope.pagination.rowsNumber
              })
            }}
          </span>
        </div>
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
    <AccountCreateDialog></AccountCreateDialog>
    <AccountInformationDialog></AccountInformationDialog>
    <AccountEditDialog></AccountEditDialog>
    <AccountRoleView></AccountRoleView>
  </basic-page>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useNotify, useDialog } from "src/utils/plugin";
import AccountCreateDialog from "./AccountCreateDialog.vue";
import AccountInformationDialog from "./AccountInformationDialog.vue";
import AccountEditDialog from "./AccountEditDialog.vue";
import AccountRoleView from "./AccountRoleView.vue";
import { useQuasar } from "quasar";
import { useAccountStore } from "stores/account";
import { storeToRefs } from "pinia";
import { format } from "utils/dateFormat";
import { mask } from "src/utils/mask";
import { useRulesAll } from "utils/rules";

// store
const $q = useQuasar();
const $m = mask();
const { rulesAll } = useRulesAll();
const accountStore = useAccountStore();
const {
  accountList,
  accountDetail,
  loading,
  pagination,
  isOpenCreate,
  isOpenDetail,
  isOpenEdit,
  selected,
  formDataEdit,
  userTypeIdOptions,
  formDataCreate
} = storeToRefs(accountStore);
const {
  getAccountList,
  getRoleList,
  getUserRoles,
  selectAccountDetail,
  getOrgOptionsForAccountManagement,
  getUserTypeIdOptionsForAccountManagement,
  openRoleDialog,
  resetBwd,
  updateAccountState,
  reSendActivationEmail,
  deleteAccount,
  checkAdminRoleForCurrentUser
} = accountStore;

// plugin settings
const $n = useNotify();
const $d = useDialog();
const filter = ref("");
const { t } = useI18n();

const { yyyyMMddHHmmss } = format();
const isVisible = ref(false);

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 計算字元數的自定義規則
const customMaxLengthRule = (maxLength) => {
  return (val) => {
    const totalLength = val.length;
    return (
      totalLength <= maxLength || t("validation.maxLength", { max: maxLength })
    );
  };
};

const columns = [
  {
    name: "login",
    required: true,
    label: t("account.table.userId"),
    align: "center",
    field: (row) => row.login,
    format: (val) => (isVisible.value ? `${val}` : $m.maskEmail(val)),
    sortable: true,
    style: "width: 100px;"
  },
  {
    name: "orgTwName",
    label: t("account.table.orgTwName"),
    field: "orgTwName",
    align: "center",
    sortable: true,
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  {
    name: "userName",
    label: t("account.table.name"),
    field: "userName",
    align: "center",
    sortable: true,
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  {
    name: "createTime",
    label: t("account.table.createTime"),
    field: "createTime",
    align: "center",
    sortable: true
  },
  {
    name: "state",
    label: t("account.table.enabled"),
    field: "state",
    align: "center",
    sortable: true,
    style: "width: 480px; white-space: normal; word-break: break-word"
  },
  // 檢視
  {
    name: "information",
    label: t("view"),
    field: "information",
    align: "center"
  },
  // 編輯
  {
    name: "edit",
    label: t("edit"),
    field: "edit",
    align: "center"
  },
  // 授權角色
  {
    name: "roleConfiguration",
    label: t("account.roleConfiguration"),
    field: "roleConfiguration",
    align: "center"
  },
  // 重置密碼
  {
    name: "resetBwd",
    label: t("account.reset"),
    field: "resetBwd",
    align: "center"
  },
  // 刪除帳號
  {
    name: "deleteAccount",
    label: t("accountManagement.delete"),
    field: "deleteAccount",
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
  await getAccountList(page, {
    size: rowsPerPage,
    sortBy: sortBy,
    descending: descending,
    userId: filter?.userId,
    state: filter?.state,
    startDate: filter?.startDate,
    endDate: filter?.endDate
  });
}

const showDialog = () => {
  if (userTypeIdOptions.value.length > 0) {
    formDataCreate.value.userTypeId = userTypeIdOptions.value[0];
  }
  isOpenCreate.value = true;
};

const handleStateToggle = async (row) => {
  loading.value = true;
  try {
    const action = row.state === "1" ? t("disabled") : t("enabled"); // 確認是啟用還是停用
    const confirmMessage = t("account.message.actionAccount", {
      action: action,
      input: row.login
    }); // 根據 action 顯示正確的訊息

    $d.confirm(t("confirm"), confirmMessage, async () => {
      const { isUpdateOK } = await updateAccountState(row);
      if (isUpdateOK) {
        $n.success(
          t("account.success.accountState", {
            action: action,
            input: row.login
          })
        );
      } else {
        $n.error(t("account.error.accountState"));
      }
    });
  } catch (error) {
    console.error("Error selecting account toggleState:", error);
  } finally {
    loading.value = false;
  }
};

const handleResetBwd = async (row) => {
  loading.value = true;
  try {
    $d.confirm(
      t("confirm"),
      t("account.message.resetBwd", { input: row.login }),
      () => {
        resetBwd(row);
      }
    );
  } catch (error) {
    console.error("Error selecting account resetBwd:", error);
  } finally {
    loading.value = false;
  }
};

const handleDeleteAccount = async (row) => {
  loading.value = true;
  try {
    $d.confirm(
      t("confirm"),
      t("account.message.deleteAccount", { input: row.login }),
      () => {
        deleteAccount(row);
      }
    );
  } catch (error) {
    console.error("Error selecting account deleteAccount:", error);
  } finally {
    loading.value = false;
  }
};

const handleResendActivationEmail = async (row) => {
  loading.value = true;
  try {
    await selectAccountDetail(row.extendedId);
    $d.confirm(
      t("confirm"),
      t("account.message.resendActivationEmail", { input: row.login }),
      async () => {
        reSendActivationEmail(row.id);
      }
    );
  } catch (error) {
    console.error("Error selecting account resendActivationEmail:", error);
  } finally {
    loading.value = false;
  }
};

const showAccountEditRoleDialog = async (row) => {
  loading.value = true;
  try {
    await checkAdminRoleForCurrentUser();
    await selectAccountDetail(row.extendedId);
    openRoleDialog(row.login);
  } catch (error) {
    console.error("Error selecting account details:", error);
  } finally {
    loading.value = false;
  }
};

const showAccountInformationDialog = async (row) => {
  await selectAccountDetail(row.extendedId);
  await getRoleList(row.login);
  await getUserRoles();
  isOpenDetail.value = true;
};

const showAccountEditDialog = async (row) => {
  await selectAccountDetail(row.extendedId);
  await setFormDataFromSelected();
  isOpenEdit.value = true;
};

const setFormDataFromSelected = async () => {
  if (selected.value) {
    formDataEdit.value.id = selected.value.id || null;
    formDataEdit.value.login = selected.value.login || null;
    formDataEdit.value.userName = selected.value.userName || null;
    formDataEdit.value.orgId = selected.value.orgId || null;
    formDataEdit.value.tel = selected.value.tel || null;

    // 使用userTypeIdOptions判斷userTypeId
    const matchedOption = userTypeIdOptions.value.find(
      (option) => option.value === selected.value.userTypeId
    );
    formDataEdit.value.userTypeId = matchedOption || "";
  }
};

const searchFormRef = ref(null);
const dropdownRef = ref(null);
const searchQuery = ref("");
const searchForm = ref({
  userId: "",
  state: "",
  startDate: "",
  endDate: ""
});

const submitSearch = async () => {
  const isValid = await searchFormRef.value.validate();

  if (searchForm.value.startDate && searchForm.value.endDate) {
    const startDate = new Date(searchForm.value.startDate);
    const endDate = new Date(searchForm.value.endDate);

    // 判斷日期是否有效
    if (startDate > endDate) {
      $n.error(t("dateField.errors.startDateAfterEndDate"));
      return;
    }
  }

  if (isValid) {
    // Update search display text
    let query = "";
    if (searchForm.value.userId) {
      query = searchForm.value.userId;
    } else if (searchForm.value.state?.value === t("enabled")) {
      query = t("enabled");
    } else if (searchForm.value.state?.value === t("disabled")) {
      query = t("disabled");
    } else if (searchForm.value.startDate && searchForm.value.endDate) {
      query = t("dateField.searchQuery");
    }
    searchQuery.value = query;

    const newFilter = {
      userId: searchForm.value.userId,
      state:
        searchForm.value.state !== ""
          ? searchForm.value.state.value === t("enabled")
            ? "1"
            : searchForm.value.state.value === t("disabled")
            ? "0"
            : searchForm.value.state.value
          : "",
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
    accountStore.currentFilter.value = newFilter;

    dropdownRef.value.hide();
  }
};

function resetForm() {
  searchForm.value = {
    userId: "",
    state: "",
    startDate: "",
    endDate: ""
  };
  searchQuery.value = "";
  filter.value = null;

  // 清空 store 中的搜索條件
  accountStore.currentFilter.value = {
    userId: "",
    state: "",
    startDate: "",
    endDate: ""
  };
}

onMounted(async () => {
  accountList.value = []; // 載入頁面後先清空帳號清單
  accountDetail.value = []; // 載入頁面後先清空帳號詳情
  await checkAdminRoleForCurrentUser();
  await getAccountList(1, {
    size: pagination.value.rowsPerPage,
    sortBy: pagination.value.sortBy,
    descending: pagination.value.descending,
    filter: filter.value
  });
  await getOrgOptionsForAccountManagement();
  await getUserTypeIdOptionsForAccountManagement();
});

onUnmounted(() => {
  accountList.value = []; // 組件完全卸載後清空帳號清單
  accountDetail.value = []; // 組件完全卸載後清空帳號詳情
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
</style>
