<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    v-model="isOpenDetail"
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 帳號檢視 -->
        <div class="text-h6">{{ t("account.title.detail") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="$q.screen.gt.sm">
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <div class="column q-pa-sm q-gutter-sm">
            <template v-if="!isOpenDetail">
              <q-img width="200px" :src="box" class="self-center"></q-img>
              <p class="text-h6 text-center">{{ t("noData") }}</p>
            </template>
            <template v-else>
              <div class="row full-width items-center q-px-sm">
                <!-- <p class="text-h5 q-px-sm q-ma-none">{{ t("account.detail") }}</p> -->
                <q-space />
                <!-- 眼睛 -->
                <q-btn
                  flat
                  :icon="isVisible ? 'visibility' : 'visibility_off'"
                  color="indigo-6"
                  class="q-pa-sm q-mr-md"
                  @click="isVisible = !isVisible"
                />
                <q-btn
                  outline
                  :label="t('refresh')"
                  icon="refresh"
                  color="indigo-6"
                  class="q-pa-sm q-mr-md"
                  @click="onRefresh"
                />
              </div>
              <q-markup-table separator="horizontal" bordered flat dense>
                <tbody>
                  <tr v-for="row in filteredTableRow" :key="row.field">
                    <td class="text-left text-bold bg-grey-1">
                      {{ row.label }}
                    </td>
                    <td :class="['text-right', getClassForField(row.field)]">
                      {{ getFieldDisplayValue(row.field) }}
                    </td>
                  </tr>
                </tbody>
              </q-markup-table>

              <!-- userRoles -->
              <p class="text-h6 text-center q-mb-none q-mt-lg">
                {{ t("account.userRoles") }}
              </p>
              <q-table
                class="sticky-header full-height item-center"
                flat
                :rows="selectedByRole"
                :columns="userRolesColumns"
                row-key="roleName"
                :hide-header="loading"
                :loading="loading"
                dense
                :pagination="{ rowsPerPage: 10 }"
                :rows-per-page-options="rowsPerPage"
              >
                <!-- footer -->
                <template v-slot:pagination="scope">
                  <!-- 顯示分頁訊息 -->
                  <span class="q-mx-md">
                    {{
                      t("account.table.row", {
                        start:
                          (scope.pagination.page - 1) *
                            scope.pagination.rowsPerPage +
                          1,
                        end: Math.min(
                          scope.pagination.page * scope.pagination.rowsPerPage,
                          selectedByRole.length
                        ),
                        total: selectedByRole.length
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

                <!-- 設定 q-table body-cell -->
                <template v-slot:loading>
                  <q-inner-loading showing color="primary" />
                </template>
              </q-table>
            </template>
          </div>
        </q-scroll-area>
      </q-card-section>
      <q-card-section style="max-width: 1000px; margin: 0 auto" v-else>
        <q-scroll-area
          style="height: 60vh; max-height: 100vh"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#1870C8',
            width: '5px',
            opacity: 0.75
          }"
        >
          <div class="column q-pa-sm q-gutter-sm">
            <template v-if="!isOpenDetail">
              <q-img width="200px" :src="box" class="self-center"></q-img>
              <p class="text-h6 text-center">{{ t("noData") }}</p>
            </template>
            <template v-else>
              <div class="row full-width items-center q-px-sm">
                <!-- <p class="text-h5 q-px-sm q-ma-none">{{ t("account.detail") }}</p> -->
                <q-space />
                <!-- 眼睛 -->
                <q-btn
                  flat
                  :icon="isVisible ? 'visibility' : 'visibility_off'"
                  color="indigo-6"
                  class="q-pa-sm q-mr-md"
                  @click="isVisible = !isVisible"
                />
                <q-btn
                  outline
                  label=""
                  icon="refresh"
                  color="indigo-6"
                  class="q-pa-sm q-mr-md"
                  @click="onRefresh"
                />
              </div>
              <q-markup-table separator="horizontal" bordered flat dense>
                <tbody>
                  <tr v-for="row in filteredTableRow" :key="row.field">
                    <td class="text-left text-bold bg-grey-1">
                      {{ row.label }}
                    </td>
                    <td :class="['text-right', getClassForField(row.field)]">
                      {{ getFieldDisplayValue(row.field) }}
                    </td>
                  </tr>
                </tbody>
              </q-markup-table>

              <!-- userRoles -->
              <p class="text-h6 text-center q-mb-none q-mt-lg">
                {{ t("account.userRoles") }}
              </p>
              <q-table
                class="sticky-header full-height item-center"
                flat
                :rows="selectedByRole"
                :columns="userRolesColumns"
                row-key="roleName"
                :hide-header="loading"
                :loading="loading"
                dense
                :pagination="{ rowsPerPage: 10 }"
                :rows-per-page-options="rowsPerPage"
              >
                <!-- footer -->
                <template v-slot:pagination="scope">
                  <div class="row items-center justify-start">
                    <!-- 顯示分頁訊息 -->
                    <span class="q-mx-sm">
                      {{
                        t("account.table.row", {
                          start:
                            (scope.pagination.page - 1) *
                              scope.pagination.rowsPerPage +
                            1,
                          end: Math.min(
                            scope.pagination.page *
                              scope.pagination.rowsPerPage,
                            selectedByRole.length
                          ),
                          total: selectedByRole.length
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

                <!-- 設定 q-table body-cell -->
                <template v-slot:loading>
                  <q-inner-loading showing color="primary" />
                </template>
              </q-table>
            </template>
          </div>
        </q-scroll-area>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { useDialogPluginComponent } from "quasar";
import { useAccountStore } from "stores/account";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { mask } from "src/utils/mask";

const accountStore = useAccountStore();
const { t } = useI18n();
const $m = mask();
const { isOpenDetail, selectedByRole, accountDetail, hasAdminRole } =
  storeToRefs(accountStore);
const { selectAccountDetail } = accountStore;
const loading = ref(false);
const isVisible = ref(false);

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogCancel } = useDialogPluginComponent();

//table分頁參數
const rowsPerPage = ref([10, 20, 50, 0]);

const tableRow = [
  {
    label: t("account.table.userId"),
    field: "login",
    isNotAdminShow: true
  },
  {
    label: t("account.table.name"),
    field: "userName",
    isNotAdminShow: true
  },
  {
    label: t("account.table.org"),
    field: "orgId",
    isNotAdminShow: true
  },
  {
    label: t("account.table.tel"),
    field: "tel",
    isNotAdminShow: true
  },
  {
    label: t("account.table.createdBy"),
    field: "createdBy",
    isNotAdminShow: false
  },
  {
    label: t("account.table.createTime"),
    field: "createTime",
    isNotAdminShow: false
  },
  {
    label: t("account.table.updatedBy"),
    field: "lastModifiedBy",
    isNotAdminShow: false
  },
  {
    label: t("account.table.updateTime"),
    field: "lastModifiedDate",
    isNotAdminShow: true
  },
  {
    label: t("account.table.enabled"),
    field: "activated",
    isNotAdminShow: true
  },
  {
    label: t("account.table.userTypeId"),
    field: "userTypeName",
    isNotAdminShow: false
  }
];

// 檢視欄位資料，若角色非 verify_admin 不會看到全部欄位資料
const filteredTableRow = computed(() => {
  return tableRow.filter((row) => hasAdminRole.value || row.isNotAdminShow);
});

// 增減class name設定
const getClassForField = (field) => {
  if (field === "status") {
    let state = accountDetail.value[field];
    if (state == 0) {
      return "text-deep-orange-7";
    } else {
      return "text-green-7";
    }
  } else {
    return "";
  }
};

// 欄位顯示文字設定
const getFieldDisplayValue = (field) => {
  //有符合的欄位名稱替換成相應內容，其餘顯示原本內容。
  if (field === "activated") {
    // 未驗證
    if (
      accountDetail.value["activationKey"] === "true" &&
      accountDetail.value["resetKey"] === "true"
    ) {
      return t("account.unverified");
    }

    // 狀態
    let state = accountDetail.value[field];
    if (state == 0) {
      return t("disabled");
    } else {
      return t("enabled");
    }
  } else if (
    // 最後編輯時間, 建立日期
    field === "lastModifiedDate" ||
    field === "createTime"
  ) {
    let dateTimeStr = accountDetail.value[field];
    if (!dateTimeStr) return "";
    const dateTime = new Date(dateTimeStr);
    // 格式化日期
    const formattedDate = `${dateTime.getFullYear()}/${(dateTime.getMonth() + 1)
      .toString()
      .padStart(2, "0")}/${dateTime.getDate().toString().padStart(2, "0")}`;

    // 格式化時間
    const formattedTime = `${dateTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${dateTime
      .getMinutes()
      .toString()
      .padStart(2, "0")}:${dateTime.getSeconds().toString().padStart(2, "0")}`;

    // 合併日期和時間
    const formattedDateTime = `${formattedDate} ${formattedTime}`;

    return formattedDateTime;
  } else if (
    field === "login" ||
    field === "createdBy" ||
    field === "lastModifiedBy"
  ) {
    return isVisible.value
      ? accountDetail.value[field]
      : $m.maskEmail(accountDetail.value[field]);
  } else if (field === "tel") {
    return isVisible.value
      ? accountDetail.value["tel"]
      : $m.maskPhone(accountDetail.value["tel"]);
  } else {
    return accountDetail.value[field];
  }
};

//欄位設定
const userRolesColumns = [
  {
    name: "roleId",
    label: t("role.code"),
    sortable: true,
    field: "roleId",
    align: "left"
  },
  {
    name: "roleName",
    label: t("role.name"),
    sortable: false,
    field: "roleName",
    align: "left"
  }
];

const onCancelClick = () => {
  onDialogCancel();
};

const onDialogHide = () => {};

const onDialogShow = () => {};

const onRefresh = () => {
  const extendedId = accountDetail.value?.extendedId;
  selectAccountDetail(extendedId);
};
</script>
