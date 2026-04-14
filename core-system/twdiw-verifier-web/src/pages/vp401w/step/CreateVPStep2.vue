<template>
  <div class="q-px-xs">
    <!-- 上方資訊 -->
    <div class="q-my-xs row">
      <div class="col-12 col-md-6">
        <div v-if="$q.screen.lt.md" class="col flex justify-end">
          <span class="alert-text">
            {{ t("vp.groups.notice") }}
          </span>
          <q-icon
            class="q-ml-auto cursor-pointer"
            color="primary"
            name="info_outline"
            size="sm"
            @click="toggleNotice"
          />
        </div>
        <!-- VP代碼 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.serialNo.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">{{
            formData.serialNo
          }}</span>
        </div>
        <!-- VP名稱 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.name.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">{{
            formData.name
          }}</span>
        </div>
      </div>

      <div v-if="$q.screen.gt.sm" class="col flex justify-end">
        <div class="column items-end">
          <!-- 提示文字 -->
          <span class="alert-text">
            {{ t("vp.groups.notice") }}
          </span>
          <!-- 選取規則按鈕 -->
          <q-btn
            outline
            color="primary"
            icon="info_outline"
            :label="t('vp.btn.groupRule')"
            style="height: 40px; width: auto"
            class="q-mt-sm"
          >
            <q-popup-proxy
              v-if="!isMobile"
              anchor="top left"
              self="top right"
              :offset="[5, 0]"
              class="bg-grey-2"
              max-width="600px"
            >
              <div class="text-white text-body2 notice">
                {{ t("vp.groups.rules.content") }}
              </div>
            </q-popup-proxy>
          </q-btn>
        </div>
      </div>
    </div>

    <!-- 新增群組資料 -->
    <div
      v-if="$q.screen.gt.md"
      class="row q-py-sm q-my-md justify-around gt-sm"
      :style="{ borderBottom: '1px solid #CCC' }"
    >
      <div class="col-1 col-lg-1">
        <p class="text-center q-pl-lg">{{ t("vp.groups.addRemove") }}</p>
      </div>
      <div class="col-4 col-lg-4">
        <p class="text-center q-pr-lg-xl">
          <span class="text-red">*</span>{{ t("vp.groups.name") }}
        </p>
      </div>
      <div class="col-4 col-lg-4">
        <p class="text-center q-pr-xl">
          <span class="text-primary">*</span>{{ t("vp.groups.pickRule.label") }}
        </p>
      </div>
      <div class="col-2 col-lg-2">
        <p class="text-center q-pr-xl">
          {{ t("vp.groups.importVcData") }}
        </p>
      </div>

      <div class="col-1 q-pr-lg">
        <p class="text-center">{{ t("vp.groups.index") }}</p>
      </div>
    </div>
    <q-form ref="formRef" greedy class="q-gutter-md q-mt-md">
      <ul ref="groupList">
        <li
          v-for="(item, index) in formData.groups"
          :key="item.id"
          class="q-mb-sm"
        >
          <div
            class="row items-start q-gutter-x-md"
            v-if="$q.screen.gt.md"
            style="height: 60px"
          >
            <!-- 增刪 -->
            <div class="gt-xs">
              <!-- 刪除列 -->
              <q-btn
                unelevated
                icon="remove"
                color="indigo-6"
                @click="removeGroupRow(index)"
                class="q-mr-sm"
                :disable="formData.groups.length <= 1"
                :style="{ height: '40px' }"
              />
              <!-- 新增列 -->
              <q-btn
                unelevated
                icon="add"
                color="primary"
                @click="addGroupRow"
                class="gt-xs"
                :disable="index !== formData.groups.length - 1 || index === 9"
                :style="{ height: '40px ' }"
              />
            </div>
            <!-- 群組名稱 -->
            <q-input
              v-model="formData.groups[index].name"
              outlined
              class="col"
              :rules="[
                (val) => !!val || t('input', { input: t('vp.groups.name') }),
                (val) =>
                  val.length <= 14 || t('validation.maxLength', { max: 14 })
              ]"
              :placeholder="t('input', { input: t('vp.groups.name') })"
              dense
            />

            <!-- 使用者選取規則 -->
            <div class="col row items-center q-gutter-sm">
              <q-radio
                v-model="formData.groups[index].rule"
                val="pick"
                :label="t('vp.groups.pickRule.pick')"
                color="primary"
              />
              <q-input
                v-model="formData.groups[index].max"
                type="number"
                class="q-mx-sm"
                dense
                outlined
                style="width: 32%"
                min="1"
                :disable="formData.groups[index].rule !== 'pick'"
                :rules="[
                  (val) => !!val || t('vp.groups.pickRule.notBlank'),
                  (val) => val >= 1 || t('vp.groups.pickRule.minOne'),
                  (val) => val <= 10 || t('vp.groups.pickRule.maxTen'),
                  (val) =>
                    Number.isInteger(Number(val)) ||
                    t('vp.groups.pickRule.pattern')
                ]"
                hide-bottom-space
              />
              <span class="q-ml-xs">{{ t("vp.groups.pickRule.pcs") }}</span>
              <q-radio
                v-model="formData.groups[index].rule"
                val="all"
                :label="t('vp.groups.pickRule.all')"
                color="primary"
                @update:model-value="(val) => onPickItemChange(index, val)"
              />
            </div>

            <!-- VC 資料匯入 / 顯示已選 VC 資料 -->
            <div class="row">
              <!-- VC 資料匯入 -->
              <q-btn
                unelevated
                icon="save_alt"
                color="indigo-6"
                :label="t('vp.btn.importVcData')"
                @click="showImportVCDialog(index)"
                :style="{ height: '40px ' }"
                class="text-center q-mr-xs"
              />

              <!-- 顯示已選 VC 資料  -->
              <div class="" style="font-size: small">
                <q-btn
                  unelevated
                  dense
                  icon="arrow_drop_down"
                  color="indigo-6"
                  class="cursor-pointer"
                  :style="{ height: '40px' }"
                  :disable="formData.groups[index].vcDatas?.length === 0"
                />
                <q-popup-proxy transition-show="scale" transition-hide="scale">
                  <q-banner class="bg-indigo-2">
                    <div
                      v-for="(vcData, index) in formData.groups[index].vcDatas"
                      :key="index"
                      class="q-pa-xs"
                    >
                      <div class="q-pa-sm bg-white shadow-1 rounded-borders">
                        {{ vcData.name }}（{{ vcData.businessName }}）
                      </div>
                    </div>
                  </q-banner>
                </q-popup-proxy>
              </div>
            </div>

            <!-- 排序 -->
            <div class="col-auto gt-xs">
              <q-btn
                unelevated
                icon="arrow_upward"
                color="primary"
                @click="moveGroupRow(index, -1)"
                :disable="index === 0"
                class="q-mr-sm"
                :style="{ height: '40px' }"
              />
              <q-btn
                unelevated
                icon="arrow_downward"
                color="primary"
                @click="moveGroupRow(index, 1)"
                :disable="index === formData.groups.length - 1"
                :style="{ height: '40px' }"
              />
            </div>
          </div>

          <div v-if="$q.screen.lt.lg" class="q-pb-md">
            <div class="q-mb-sm">
              <p><span class="text-red">*</span>{{ t("vp.groups.name") }}：</p>
              <q-input
                v-model="formData.groups[index].name"
                outlined
                class="full-width"
                :rules="[
                  (val) => !!val || t('input', { input: t('vp.groups.name') }),
                  (val) =>
                    val.length <= 14 || t('validation.maxLength', { max: 14 })
                ]"
                :placeholder="t('input', { input: t('vp.groups.name') })"
                dense
              />
            </div>
            <div class="q-mb-sm">
              <p>
                <span class="text-red">*</span
                >{{ t("vp.groups.pickRule.label") }}：
              </p>
              <div class="row items-center q-gutter-xs q-mb-md">
                <div class="row items-center">
                  <q-radio
                    v-model="formData.groups[index].rule"
                    val="pick"
                    :label="t('vp.groups.pickRule.pick')"
                    color="primary"
                  />
                  <q-input
                    v-model="formData.groups[index].max"
                    type="number"
                    class="q-mx-sm"
                    dense
                    outlined
                    style="width: 40%"
                    min="1"
                    :disable="formData.groups[index].rule !== 'pick'"
                    :rules="[
                      (val) => !!val || t('vp.groups.pickRule.notBlank'),
                      (val) => val >= 1 || t('vp.groups.pickRule.minOne'),
                      (val) => val <= 10 || t('vp.groups.pickRule.maxTen'),
                      (val) =>
                        Number.isInteger(Number(val)) ||
                        t('vp.groups.pickRule.pattern')
                    ]"
                    hide-bottom-space
                  />
                  <span class="q-ml-xs">{{ t("vp.groups.pickRule.pcs") }}</span>
                </div>
                <q-radio
                  v-model="formData.groups[index].rule"
                  val="all"
                  :label="t('vp.groups.pickRule.all')"
                  color="primary"
                  @update:model-value="(val) => onPickItemChange(index, val)"
                />
              </div>
            </div>
            <div class="q-mb-md">
              <!-- VC 資料匯入 / 顯示已選 VC 資料 -->
              <div class="row">
                <div class="q-mr-md flex items-center">
                  <p>
                    <span class="text-red">*</span
                    >{{ t("vp.groups.importVcData") }}：
                  </p>
                </div>
                <div class="row" :class="isMobile ? 'q-mt-sm' : ''">
                  <!-- VC 資料匯入 -->
                  <q-btn
                    unelevated
                    icon="save_alt"
                    color="indigo-6"
                    :label="t('vp.btn.importVcData')"
                    @click="showImportVCDialog(index)"
                    :style="{ height: '40px ' }"
                    class="text-center q-mr-xs"
                  />

                  <!-- 顯示已選 VC 資料  -->
                  <div style="font-size: small">
                    <q-btn
                      unelevated
                      dense
                      icon="arrow_drop_down"
                      color="indigo-6"
                      class="cursor-pointer"
                      :style="{ height: '40px' }"
                      :disable="formData.groups[index].vcDatas?.length === 0"
                    />
                    <q-popup-proxy
                      transition-show="scale"
                      transition-hide="scale"
                    >
                      <q-banner class="bg-indigo-2">
                        <div
                          v-for="(vcData, index) in formData.groups[index]
                            .vcDatas"
                          :key="index"
                          class="q-pa-xs"
                        >
                          <div
                            class="q-pa-sm bg-white shadow-1 rounded-borders"
                          >
                            {{ vcData.name }}（{{ vcData.businessName }}）
                          </div>
                        </div>
                      </q-banner>
                    </q-popup-proxy>
                  </div>
                </div>
              </div>
            </div>

            <div class="row items-center q-gutter-x-sm">
              <q-btn
                unelevated
                icon="remove"
                color="indigo-6"
                @click="removeGroupRow(index)"
                class="col"
                :disable="formData.groups.length <= 1"
                :style="{ height: '40px ' }"
              />
              <!-- 新增列  -->
              <q-btn
                unelevated
                icon="add"
                color="primary"
                @click="addGroupRow"
                class="col"
                :disable="index !== formData.groups.length - 1 || index === 9"
                :style="{ height: '40px ' }"
              />
              <q-btn
                unelevated
                icon="arrow_upward"
                color="primary"
                @click="moveGroupRow(index, -1)"
                :disable="index === 0"
                :style="{ height: '40px' }"
              />
              <q-btn
                unelevated
                icon="arrow_downward"
                color="primary"
                @click="moveGroupRow(index, 1)"
                :disable="index === formData.groups.length - 1"
                :style="{ height: '40px' }"
              />
            </div>
          </div>
        </li>
      </ul>
    </q-form>
  </div>
  <!-- 手機版的 i 資訊 -->
  <q-dialog v-model="showNotice">
    <q-card style="min-width: 300px">
      <q-card-section>
        <div class="text-h6">{{ t("vp.groups.rules.title") }}</div>
      </q-card-section>

      <q-card-section>
        <p class="text-body2 word-break">
          {{ t("vp.groups.rules.content") }}
        </p>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn outline :label="t('close')" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { useQuasar } from "quasar";
import ImportVCDialog from "../ImportVCDialog.vue";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();

// 表單相關
const formRef = ref(null);
const groupList = ref(null);

// 步驟資料
const formData = defineModel("formData");
const treeData = defineModel("treeData");

const showNotice = ref(false);

const props = defineProps({
  isEdit: Boolean
});

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 資訊 i 開關
const toggleNotice = () => {
  if (isMobile.value) {
    showNotice.value = true;
  }
};

// 刪除列
const removeGroupRow = (index) => {
  formData.value.groups.splice(index, 1);
};

// 新增列
const addGroupRow = () => {
  formData.value.groups.push({
    name: "",
    rule: "pick",
    max: 1,
    vcDatas: []
  });
};

// 排序
const moveGroupRow = (index, direction) => {
  if (
    (direction === -1 && index > 0) ||
    (direction === 1 && index < formData.value.groups.length - 1)
  ) {
    const newIndex = index + direction;
    const temp = formData.value.groups[index];
    formData.value.groups[index] = formData.value.groups[newIndex];
    formData.value.groups[newIndex] = temp;
  }
};

// 選擇 VC 資料匯入
const showImportVCDialog = (index) => {
  $q.dialog({
    component: ImportVCDialog,
    componentProps: {
      isEdit: props.isEdit,
      selectedVcDatas: formData.value.groups[index].vcDatas
    }
  })
    .onOk((data) => {
      // 處理從第二個 dialog 返回的數據
      if (Array.isArray(data)) {
        // 處理 formData 資料
        formData.value.groups[index].vcDatas = data.map((vaData) => ({
          serialNo: vaData.serialNo,
          name: vaData.name,
          businessId: vaData.businessId,
          businessName: vaData.businessName,
          vcFields: vaData.vcItemFields.map((vcField) => ({
            cname: vcField.cname,
            ename: vcField.ename,
            isRequired: vcField.isRequired
          }))
        }));
      }
    })
    .onCancel(() => {
      // 處理取消事件
      console.log("showImportVCDialog cancel!");
    });
};

// 當選全選事件
const onPickItemChange = (index, val) => {
  if (val === "all") {
    formData.value.groups[index].pickItemPartCount = null;
    // 切換時清空 最多可選取數
    formData.value.groups[index].max = 1;
  }
};

// 組樹狀資料
const getTreeNodeDatas = () => {
  return formData.value.groups.map((groupData) => ({
    name: groupData.name,
    rule: groupData.rule,
    max: groupData.max,
    key: `group_${groupData.name}`,
    children: groupData.vcDatas.map((vcData) => ({
      name: vcData.name,
      businessName: vcData.businessName,
      key: `vc_${groupData.name}_${vcData.serialNo}`,
      children: vcData.vcFields.map((vcField, vcFieldIndex) => ({
        cname: vcField.cname,
        ename: vcField.ename,
        isRequired: vcField.isRequired,
        key: `vcField_${groupData.name}_${vcData.serialNo}_${vcFieldIndex}`
      }))
    }))
  }));
};

// 取得所勾選資料
const getTickedKeys = () => {
  const tickedKeys = [];

  formData.value.groups.forEach((groupData) => {
    groupData.vcDatas.forEach((vcData) => {
      vcData.vcFields.forEach((vcField, vcFieldIndex) => {
        if (vcField.isTicked) {
          // 如果是 VP05（APP 出示憑證模式），處理自定義欄位
          if (formData.value.model.value === "2") {
            vcField.customFieldName = vcField.customFieldName || vcField.ename;
          }

          // 加入勾選資料 key
          tickedKeys.push(
            `vcField_${groupData.name}_${vcData.serialNo}_${vcFieldIndex}`
          );
        }
      });
    });
  });

  return tickedKeys;
};

// 下一步 ＋ 檢核
const toNextStep = async () => {
  const groupLabelMap = new Map();
  const duplicateNames = [];
  const isGroupEmptyList = [];
  const isGroupOverLimit = [];
  const groupPickCountFailList = [];

  const isValid = await formRef.value.validate();
  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  }
  // 組樹狀資料
  treeData.value.treeNodeDatas = getTreeNodeDatas();
  treeData.value.tickedNodes = getTickedKeys();

  // 檢核 group 名稱不能重複
  treeData.value.treeNodeDatas.forEach((groupNode, index) => {
    const key = `${formData.value.groups[index].name}`;
    if (groupLabelMap.has(key)) {
      // 如果已經存在，將 group name 加入重複名稱陣列
      if (!duplicateNames.includes(`「${key}」`)) {
        duplicateNames.push(`「${key}」`);
      }
    } else {
      groupLabelMap.set(key, true);
    }

    // 檢核 最多可選取筆數不可大於匯入 VC 模板數
    if (formData.value.groups[index].max > groupNode.children.length) {
      groupPickCountFailList.push(`「${key}」`);
    }

    // 檢核 群組內模板不能為 0
    if (groupNode.children.length == 0) {
      isGroupEmptyList.push(`「${key}」`);
    }

    // 檢核 群組內模板不能超過 10 個
    if (groupNode.children.length > 10) {
      isGroupOverLimit.push(`「${key}」`);
    }
  });

  // 檢核 群組不能超過 10 個
  if (formData.value.groups.length > 10) {
    $n.error(t("vp.groups.error.maxGroups"));
    return;
  }

  if (isGroupOverLimit.length > 0) {
    $n.error(
      t("vp.groups.error.groupVcLimit", {
        groups: isGroupOverLimit.join("、")
      })
    );
    return;
  }

  if (duplicateNames.length > 0) {
    $n.error(
      t("vp.groups.error.duplicateNames", {
        names: duplicateNames.join("、")
      })
    );
    return;
  }

  if (isGroupEmptyList.length > 0) {
    $n.error(
      t("vp.groups.error.emptyGroup", {
        groups: isGroupEmptyList.join("、")
      })
    );
    return;
  }

  if (groupPickCountFailList.length > 0) {
    $n.error(
      t("vp.groups.error.pickCountFail", {
        groups: groupPickCountFailList.join("、")
      })
    );
    return;
  }

  return true;
};

defineExpose({ toNextStep });
</script>

<style scoped>
.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

.notice {
  background-color: grey;
  padding: 5px;
  white-space: pre-wrap;
}
</style>
