<template>
  <q-dialog v-model="isOpenRoleView" persistent>
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <!-- 帳號授權角色 -->
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white q-mb-md"
      >
        <div class="text-h6">{{ t("account.accountRoleConfiguration") }}</div>
        <q-btn
          flat
          round
          color="white"
          icon="close"
          @click="unSet"
          v-close-popup
        />
      </q-card-section>
      <q-card-section class="q-pa-none q-pl-md q-mb-md">
        <div class="text-bold" style="font-size: 16px">
          <span class="q-mr-xs">{{ t("account.userAccount") + "：" }}</span
          ><span class="text-teal"> {{ selected.login }} </span
          ><span class="text-teal">({{ selected.userName }})</span>
        </div>
      </q-card-section>
      <q-card-section class="q-pb-none">
        <div class="q-pl-none">
          <div
            v-if="selectedByRole.length > 0"
            class="q-mr-xs text-h7 text-bold"
            style="font-size: 16px"
          >
            {{ t("account.setRol") + "：" }}
          </div>
          <div class="flex">
            <span v-for="role in selectedByRole" :key="role.id">
              <q-badge class="text-h7" color="primary" text-color="white">{{
                role.roleName
              }}</q-badge>
              <span>&nbsp;&nbsp;</span>
            </span>
          </div>
        </div>
      </q-card-section>
      <q-card-section class="q-py-none">
        <q-table
          class="sticky-header full-height q-mt-sm"
          flat
          :rows="roleList"
          :columns="columns"
          selection="multiple"
          row-key="id"
          v-model:selected="selectedByRole"
          :hide-header="loading"
          :loading="loading"
          dense
          :pagination="{ rowsPerPage: 10 }"
          :rows-per-page-options="[10, 20, 50, 0]"
        >
          <!-- 設定 q-table 上方 -->

          <!-- 自定義顯示勾選框header -->
          <template v-slot:header-selection="">
            {{ t("account.selected") }}
          </template>
          <!-- 自定義顯示勾選框body -->
          <template v-slot:body-selection="props">
            <div
              style="
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
              "
            >
              <!-- 否則顯示正常勾選框 -->
              <q-checkbox
                v-model="props.selected"
                :disable="disableCheckBox(props.row)"
              />
            </div>
          </template>

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
                      scope.pagination.page * scope.pagination.rowsPerPage,
                      roleListLength
                    ),
                    total: roleListLength
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
        </q-table>
      </q-card-section>
      <q-card-section>
        <div class="row q-gutter-sm full-width">
          <q-space></q-space>
          <q-btn
            outline
            size="md"
            :label="t('cancel')"
            class="q-mt-md"
            v-close-popup
            color="primary"
            @click="unSet"
          >
          </q-btn>
          <q-btn
            class="q-mt-md"
            size="md"
            type="submit"
            :label="t('confirm')"
            @click="saveRole"
            color="primary"
            :disable="loading"
            :loading="loading"
          >
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useNotify } from "src/utils/plugin";
import { useAccountStore } from "stores/account";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { api } from "src/boot/axios";
import { useUserConfigStore } from "src/stores/userConfig";

const userConfig = useUserConfigStore();
const { userDetails } = storeToRefs(userConfig);
const { getUser } = userConfig;

const { t } = useI18n();
const store = useAccountStore();
const { closeRoleDialog, getUserRoles } = store;
const {
  roleList,
  isOpenRoleView,
  selected,
  selectedByRole,
  selectedByRoleMap,
  roleListLength
} = storeToRefs(store);
const $n = useNotify();
const jwtToken = sessionStorage.getItem("jwt-user-object");

//欄位設定
const columns = [
  {
    name: "roleId",
    label: t("role.table.code"),
    align: "left",
    field: "roleId",
    required: true
  },
  {
    name: "roleName",
    label: t("role.table.name"),
    sortable: false,
    field: "roleName",
    align: "left"
  },
  {
    name: "description",
    label: t("role.table.desc"),
    sortable: true,
    field: "description",
    align: "left"
  }
];

const saveRole = async () => {
  await updateRoles();
  closeRoleDialog();
};

const unSet = () => {
  selectedByRole.value = selectedByRoleMap.value;
};

const updateRoleUrl = `/api/modadw311w/roles`;
// 帳號管理頁面，變更所選擇帳號的角色清單。
const updateRoles = async () => {
  loading.value = true;
  const userDTO = selected.value;
  const roleDTOList = selectedByRole.value;

  await api
    .post(
      updateRoleUrl,
      { userDTO, roleDTOList },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`
        }
      }
    )
    .then(async (response) => {
      if (response && response.data && response.data.code) {
        if (response.data.code === "0") {
          await getUserRoles();
          loading.value = false;
          $n.success(
            t("account.success.edit", { input: t("account.accountRole") })
          );
        } else {
          if (response.data.msg) {
            $n.error(response.data.msg);
          }
          loading.value = false;
        }
      }
    })
    .catch((error) => {
      if (error.response && error.response.status === 403) {
        $n.error(t("account.error.permissionDenied"));
      }
      console.error("error:", error);
      loading.value = false;
    });
};

//table refs
const loading = ref(false);

// 判斷checkBox是否要disable
const disableCheckBox = (row) => {
  const isDefault = row.roleId === "default_role";
  const isOrgManager = row.roleId === "issuer_org_manager";
  const isMyself = userDetails.value.userId == selected.value.login;

  return isDefault || (isOrgManager && isMyself);
};

onMounted(async () => {
  await getUser();
});
</script>
