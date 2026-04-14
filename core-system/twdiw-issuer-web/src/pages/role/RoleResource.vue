<template>
  <pop-up
    v-model="openResourceDialog"
    :persistent="true"
    :header="t('role.resource.title')"
  >
    <!-- 功能授權設定 -->
    <div class="bg-white q-pa-sm">
      <div class="row">
        <!-- left search & button -->
        <q-input
          dense
          clearable
          class="col"
          ref="leftFilterRef"
          v-model="leftFilter"
          :label="t('filterKey')"
        >
          <template #after>
            <q-btn
              flat
              dense
              @click="leftExpandAll"
              icon="unfold_more"
              color="action"
            ></q-btn>
            <q-btn
              flat
              dense
              @click="leftCollapseAll"
              icon="unfold_less"
              color="action"
            ></q-btn>
          </template>
        </q-input>
      </div>
      <div class="round-sm scroll q-pa-sm shadow-1" style="max-height: 40vh">
        <!-- left tree -->
        <q-tree
          node-key="label"
          tick-strategy="leaf"
          :no-results-label="t('noResult')"
          ref="left_treeElm"
          :nodes="resNodeDTO"
          :default-expand-all="true"
          :filter="leftFilter"
          v-model:ticked="tickedNodes"
          @update:ticked="handleSelect"
        ></q-tree>
      </div>

      <!-- 已選功能清單 -->
      <div class="q-pa-sm text-subtitle1 text-weight-bold">
        <span>{{ t("role.resource.selectedList") }}</span>
      </div>
      <div
        class="row q-ma-none q-pa-sm round-sm bg-grey-11 inset-shadow scroll q-gutter-md"
        style="max-height: 20vh"
      >
        <!-- 尚未選取任何功能 -->
        <template v-if="!tickedNodes || tickedNodes.length === 0">
          <span class="q-ma-none text-grey-4">{{
            t("role.resource.noSelectedList")
          }}</span>
        </template>
        <template v-else>
          <template v-for="tick in tickedNodes" :key="`ticked-${tick}`">
            <div class="q-ma-none q-pa-xs">
              <div class="q-pa-sm round-sm bg-white shadow-1">
                <span class="">{{ tick }}</span>
              </div>
            </div>
          </template>
        </template>
      </div>
    </div>
    <q-card-section class="q-ma-none row justify-end q-gutter-sm">
      <q-btn outline :label="t('cancel')" color="primary" v-close-popup />
      <q-btn
        :label="t('store')"
        color="primary"
        @click="onSaveRoleResRel"
        :disable="loading"
        :loading="loading"
      />
    </q-card-section>
  </pop-up>
</template>
<script setup>
import { ref, defineProps } from "vue";
import { storeToRefs } from "pinia";
import { useRoleStore } from "stores/role";

import { useI18n } from "vue-i18n";
const { t } = useI18n();

//store
const store = useRoleStore();
//tickedNodes 左邊樹狀checkbox選中的節點
const { loading, tickedNodes, resNodeDTO } = storeToRefs(store);
const { saveAuthFunc } = store;

const props = defineProps(["title"]);

// dialog開關控制項
const openResourceDialog = defineModel();
const closeDialog = () => {
  openResourceDialog.value = false;
};

// 儲存授予的功能
const onSaveRoleResRel = async () => {
  const resList = [];

  // 遞迴取得所有結構下的value值 (value=功能)
  const collectValues = (nodes) => {
    nodes.forEach((node) => {
      if (node.value) {
        let res = {};
        res.resId = node.value;
        resList.push(res);
      }
      if (node.children) {
        collectValues(node.children);
      }
    });
  };

  collectValues(selectedNodes.value);
  await saveAuthFunc(resList); // 將整理好的待授權功能送去儲存
  closeDialog();
};

// ======= 授權功能 結構處理區段 =======
//右邊樹狀選中的節點數據
const selectedNodes = ref([]);

//右邊樹狀,紀錄要展開的節點 (用來解決,當左側為收起狀態時做勾選,右側不會展開問題)
const expandedNodes = ref([]);

//選中節點數據處理
const handleSelect = (selected) => {
  tickedNodes.value = selected;
  copySelectedNodes();
};

//複製選中的節點,產生節點結構給右邊樹狀使用
const copySelectedNodes = () => {
  // 遞迴產生結構
  const findNodes = (nodes, labels, expanded) => {
    return nodes.reduce((result, node) => {
      if (labels.includes(node.label)) {
        expanded.push(node.label); //加入展開的節點
        result.push({
          ...node,
          children: node.children
            ? findNodes(node.children, labels, expanded)
            : []
        });
      } else if (node.children) {
        const childNodes = findNodes(node.children, labels, expanded);
        if (childNodes.length) {
          expanded.push(node.label); //加入展開的節點
          result.push({
            ...node,
            children: childNodes
          });
        }
      }
      return result;
    }, []);
  };

  expandedNodes.value = [];
  selectedNodes.value = findNodes(
    resNodeDTO.value, // 樹結構列表
    tickedNodes.value, // 已授權的功能(打勾)
    expandedNodes.value // 展開的節點
  );
};

//左邊q-tree展開 & 關閉
const left_treeElm = ref(null);
const leftExpandAll = () => {
  left_treeElm.value.expandAll();
};
const leftCollapseAll = () => {
  left_treeElm.value.collapseAll();
};

//右邊q-tree展開 & 關閉
const right_treeElm = ref(null);
const rightExpandAll = () => {
  right_treeElm.value.expandAll();
};
const rightCollapseAll = () => {
  right_treeElm.value.collapseAll();
};

//左邊關鍵字搜尋
const leftFilter = ref("");

//右邊關鍵字搜尋
const rightFilter = ref("");

defineExpose({ copySelectedNodes, leftExpandAll });
</script>
