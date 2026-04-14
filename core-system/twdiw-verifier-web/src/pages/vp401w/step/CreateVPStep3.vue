<template>
  <div class="q-px-xs q-pb-xs">
    <!-- 上方資訊 -->
    <div class="q-my-xs row">
      <div class="col-12 col-md-6">
        <!-- VP 代碼 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.serialNo.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break" style="">{{
            formData.serialNo
          }}</span>
        </div>
        <!-- VP 名稱 -->
        <div :class="['row items-start', isMobile ? '' : 'q-mb-sm']">
          <span class="text-h6-cus text-grey-7"
            >{{ t("vp.name.label") }}：</span
          >
          <span class="text-h6-cus q-ma-none word-break">{{
            formData.name
          }}</span>
        </div>
      </div>
    </div>

    <!-- 樹狀資訊 -->
    <!-- 關鍵字 -->
    <div :class="[isMobile ? 'q-mt-sm' : 'q-mt-md']">
      <div :class="['q-mb-sm', isMobile ? 'column' : 'row']">
        <div class="row col">
          <q-input
            ref="filterRef"
            filled
            v-model="filter"
            :label="t('vp.selectFields.filter')"
            class="col"
            dense
            bg-color="indigo-1"
          />
        </div>
        <div :class="['row', isMobile ? 'q-mt-sm col' : 'col-auto']">
          <q-btn
            dense
            size="md"
            @click="selectAllNodes"
            icon="done_all"
            color="indigo-6"
            :label="t('vp.btn.selectAll')"
            :class="[isMobile ? 'col q-px-sm' : 'q-ml-sm q-px-md']"
            style="width: 120px"
          />
          <q-btn
            dense
            size="md"
            @click="cancelAllNodes"
            icon="cancel"
            color="primary"
            :label="t('vp.btn.cancelAll')"
            :class="['q-ml-sm', isMobile ? 'col q-px-sm' : 'q-px-md']"
            style="width: 120px"
          />
        </div>
      </div>

      <!-- 樹狀及已選擇欄位顯示 -->
      <div class="round-sm q-pa-sm shadow-1">
        <div
          class="scroll"
          :style="{
            height: isMobile ? '' : '45vh',
            'max-height': isMobile ? '' : '85vh'
          }"
        >
          <q-splitter
            v-model="splitterModel"
            :horizontal="isMobile"
            style="height: 100%"
          >
            <template v-slot:before>
              <div class="row justify-between q-mb-sm q-mr-md items-center">
                <div>
                  <q-chip icon="checklist" color="indigo-4" text-color="white">
                    {{ t("vp.selectFields.title") }}
                  </q-chip>
                </div>
                <!-- 展開 / 縮合 -->
                <div>
                  <q-btn
                    dense
                    outline
                    icon="remove"
                    color="indigo-6"
                    class="q-ml-xs"
                    size="sm"
                    @click="collapseAll"
                  />
                  <q-btn
                    dense
                    icon="add"
                    color="indigo-6"
                    class="q-ml-xs"
                    size="sm"
                    @click="expandAll"
                  />
                </div>
              </div>
              <!-- 左邊（上面）樹狀內容 -->
              <div class="q-pa-sm">
                <q-tree
                  :nodes="treeData.treeNodeDatas"
                  node-key="key"
                  tick-strategy="leaf"
                  :no-results-label="t('vp.selectFields.noResultData')"
                  no-nodes-label="t('vp.selectFields.noResultData')"
                  ref="treeElement"
                  :default-expand-all="true"
                  :filter="filter"
                  :filter-method="filterTree"
                  v-model:ticked="treeData.tickedNodes"
                  @update:ticked="handleTicked"
                >
                  <template v-slot:default-header="prop">
                    <span>
                      <template v-if="prop.node.ename && prop.node.cname">
                        {{ prop.node.ename }}（{{ prop.node.cname }}
                        <span
                          v-if="
                            prop.node.isRequired ||
                            prop.node.isRequired === null
                          "
                          class="text-red"
                          >*</span
                        >
                        ）
                      </template>
                      <template
                        v-else-if="prop.node.name && prop.node.businessName"
                      >
                        {{ prop.node.name }}（{{ prop.node.businessName }}）
                      </template>
                      <template v-else-if="prop.node.name && prop.node.rule">
                        {{ prop.node.name }}（
                        {{
                          prop.node.rule === "all"
                            ? t("vp.groups.ruleAllMsg", {
                                count: getGroupTickedCount(prop.node.name)
                              })
                            : t("vp.groups.rulePickMsg", { max: prop.node.max })
                        }}
                        ）</template
                      >
                      <template v-else>
                        {{
                          prop.node.label ||
                          prop.node.name ||
                          t("vp.groups.noRule")
                        }}
                      </template>
                    </span>
                  </template>
                </q-tree>
              </div>
            </template>
            <!-- 右邊（下面）已選擇的欄位 -->
            <template v-slot:after>
              <div
                :class="[
                  'q-mb-sm items-center',
                  isMobile ? '' : 'row  q-mr-md'
                ]"
              >
                <q-chip icon="check_circle" color="indigo-6" text-color="white">
                  {{ t("vp.selectFields.selected") }}
                </q-chip>
                <div
                  v-if="formData.model.value === '2'"
                  class="col text-indigo-6 q-ml-sm"
                >
                  {{ t("vp.selectFields.customFieldsNotice") }}
                </div>
              </div>
              <div class="q-pa-md">
                <q-form ref="formRef" greedy>
                  <!-- Group -->
                  <div
                    v-for="(groupData, groupDataIndex) in formData.groups"
                    :key="`${groupData.name}_${groupDataIndex}`"
                    class="q-mb-md"
                  >
                    <!-- VC 卡片資訊 -->
                    <div
                      v-for="(vcData, vcDataIndex) in groupData.vcDatas"
                      :key="`${vcData.serialNo}_${vcDataIndex}`"
                    >
                      <div
                        v-if="
                          vcData.vcFields.some((vcField) => vcField.isTicked)
                        "
                        class="text-h6 q-mb-sm"
                      >
                        {{ groupData.name }} / {{ vcData.name }}
                      </div>

                      <!-- VC 卡片欄位資訊 -->
                      <div
                        v-for="(vcField, vcFieldIndex) in vcData.vcFields"
                        :key="`${vcField.ename}_${vcFieldIndex}`"
                        class="q-mb-sm"
                      >
                        <template v-if="vcField.isTicked">
                          <q-item dense class="">
                            <q-item-section avatar style="min-width: auto">
                              <q-icon
                                name="label"
                                color="indigo-6"
                                size="16px"
                              />
                            </q-item-section>
                            <q-item-section>
                              <q-item-label>
                                <div class="q-mr-sm">
                                  {{ vcField.ename }}（{{ vcField.cname }}）
                                </div>
                              </q-item-label>
                            </q-item-section>
                          </q-item>
                          <!-- VP05 APP 出示憑證模式可輸入自定義名稱 -->
                          <q-item v-if="formData.model.value === '2'">
                            <q-item-section>
                              <q-input
                                v-model="vcField.customFieldName"
                                dense
                                outlined
                                :placeholder="
                                  t(
                                    'vp.selectFields.customFieldName.placeholder'
                                  )
                                "
                                :rules="[
                                  (val) =>
                                    !!val ||
                                    t(
                                      'vp.selectFields.customFieldName.placeholder'
                                    ),
                                  (val) =>
                                    val.length <= 50 ||
                                    t('validation.maxLength', { max: 50 }),
                                  (val) =>
                                    /^(?!id$)[a-zA-Z0-9_]+$/.test(val) ||
                                    t('vp.selectFields.customFieldName.pattern')
                                ]"
                                class="no-hint-padding"
                              >
                                <template v-slot:hint>
                                  <span
                                    v-if="
                                      checkCustomFieldNameDuplicate(
                                        vcField,
                                        vcField.customFieldName
                                      )
                                    "
                                    class="text-orange-8 text-caption q-pt-none"
                                  >
                                    {{
                                      t(
                                        "vp.selectFields.customFieldName.duplicateHint",
                                        { name: vcField.customFieldName }
                                      )
                                    }}
                                  </span>
                                </template></q-input
                              >
                            </q-item-section>
                          </q-item>
                        </template>
                      </div>
                      <q-separator
                        v-if="
                          vcData.vcFields.some((vcField) => vcField.isTicked)
                        "
                        class="q-my-md"
                      />
                    </div>
                  </div>
                </q-form>
              </div>
            </template>
          </q-splitter>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const $n = useNotify();
const $q = useQuasar();
const splitterModel = ref(50);

// 步驟資料
const formData = defineModel("formData");
const treeData = defineModel("treeData");

// 樹狀資料
const filterRef = ref(null);
const filter = ref(null);
const treeElement = ref(null);

// 右邊（下面）已選擇欄位表單相關
const formRef = ref(null);

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 展開樹狀圖
const expandAll = () => {
  treeElement.value.expandAll();
};

// 縮合樹狀圖
const collapseAll = () => {
  treeElement.value.collapseAll();
};

// 選中節點處理
const handleTicked = () => {
  // 已選擇的節點
  const tickedSet = new Set(treeData.value.tickedNodes);
  const newGroups = formData.value.groups.map((group) => {
    const groupName = group.name;

    // 將 VcDatas 加上 isTicked
    const newVcDatas = group.vcDatas.map((vcData) => {
      const vcSerialNo = vcData.serialNo;

      // 將 VcFields 加上 isTicked
      const newVcFields = vcData.vcFields.map((vcField, vcFieldsIndex) => {
        const vcFieldKey = `vcField_${groupName}_${vcSerialNo}_${vcFieldsIndex}`;
        const isTicked = tickedSet.has(vcFieldKey);
        return {
          ...vcField,
          isTicked,
          customFieldName: isTicked
            ? vcField.customFieldName || vcField.ename
            : ""
        };
      });

      // 判斷此 VC 是否全欄位勾選
      const isVcTicked = newVcFields.some((vcField) => vcField.isTicked);

      return {
        ...vcData,
        isTicked: isVcTicked,
        vcFields: newVcFields
      };
    });

    return {
      ...group,
      vcDatas: newVcDatas
    };
  });
  formData.value.groups = newGroups;
};

// 取得所有樹狀的 key
const getAllTreeKeys = (treeNodeDatas) => {
  let keys = [];
  treeNodeDatas.forEach((item) => {
    if (item.children && item.children.length) {
      keys.push(...getAllTreeKeys(item.children));
    } else {
      keys.push(item.key);
    }
  });

  return keys;
};

// 全選
const selectAllNodes = () => {
  treeData.value.tickedNodes = getAllTreeKeys(treeData.value.treeNodeDatas);
  handleTicked();
};

// 取消全選
const cancelAllNodes = () => {
  treeData.value.tickedNodes = [];
  handleTicked();
};

// 計算該群組 isTicked=true 的 VC 模板數量
const getGroupTickedCount = (groupName) => {
  const group = formData.value.groups.find((group) => group.name === groupName);
  if (!group || !group.vcDatas) return 0;

  return group.vcDatas.filter((vc) => vc.isTicked).length;
};

// 輸入關鍵字比對節點
const filterTree = (node, filter) => {
  if (!filter) return true;
  const text = filter.toLowerCase();

  // 當前節點
  const matchesCurrent =
    node.name?.toLowerCase().includes(text) ||
    node.businessName?.toLowerCase().includes(text) ||
    node.cname?.toLowerCase().includes(text) ||
    node.ename?.toLowerCase().includes(text);

  // 有 children 的節點，往下檢查
  const matchesChildren = Array.isArray(node.toLowerCase)
    ? node.children.some((child) => filterTree(child.filter))
    : false;

  return matchesCurrent || matchesChildren;
};

const checkCustomFieldNameDuplicate = (currentVcField, val) => {
  if (!val) return false;

  const groups = formData.value.groups;

  const currentGroup = groups.find((group) =>
    group.vcDatas.some((vcData) => vcData.vcFields.includes(currentVcField))
  );

  // 檢查 跨群組重複
  const crossGroupDuplicate = groups
    .filter((g) => g !== currentGroup) // 排除自己所在群組
    .some((group) =>
      group.vcDatas.some((vcData) =>
        vcData.vcFields.some(
          (vcField) => vcField.isTicked && vcField.customFieldName === val
        )
      )
    );

  if (crossGroupDuplicate) return true;

  // 檢查 同群組內重複（僅限 pick && max > 1 或 all）
  if (
    (currentGroup.rule === "pick" && currentGroup.max > 1) ||
    currentGroup.rule === "all"
  ) {
    const names = currentGroup.vcDatas.flatMap((vcData) =>
      vcData.vcFields
        .filter((vcField) => vcField.isTicked && vcField.customFieldName)
        .map((vcField) => vcField.customFieldName)
    );

    let count = 0;
    const sameGroupDuplicate = names.some((name) => {
      if (name === val) count++;
      return count > 1;
    });

    if (sameGroupDuplicate) return true;
  }

  return false;
};

// 下一步 ＋ 檢核
const toNextStep = async () => {
  const groupPickCountFailList = [];

  const isValid = await formRef.value.validate();
  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  }

  formData.value.groups.map((group) => {
    const groupName = group.name;

    // 檢核 模板數量
    // 群組內模板數不可為 0
    // 若選擇 最多選取，勾選數量不可少於最多選取筆數
    const tickedCount = group.vcDatas.filter((vc) => vc.isTicked).length;
    if (group.rule === "pick") {
      if (group.max > tickedCount) {
        groupPickCountFailList.push(
          t("vp.selectFields.error.pickAtLeastMax", {
            groupName,
            max: group.max
          })
        );
      }
    } else {
      if (tickedCount == 0) {
        groupPickCountFailList.push(
          t("vp.selectFields.error.pickAtLeastOne", {
            groupName
          })
        );
      }
    }
  });

  // 模板數量錯誤 訊息
  if (groupPickCountFailList.length > 0) {
    $n.error(groupPickCountFailList.join("、"));
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

.no-hint-padding :deep(.q-field__bottom) {
  padding-top: 0 !important;
}
</style>
