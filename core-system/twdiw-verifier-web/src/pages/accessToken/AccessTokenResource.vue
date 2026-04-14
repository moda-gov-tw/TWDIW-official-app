<template>
  <q-dialog v-model="isOpenDialog" persistent>
    <q-card style="min-width: 80vw" class="full-height">
      <q-card-section>
        <div class="row">
          <!-- 左半邊的search & button -->
          <div class="col row">
            <q-input
              class="col"
              ref="leftFilterRef"
              filled
              v-model="leftFilter"
              :label="t('filterKey')"
              bg-color="indigo-1"
            ></q-input>
            <q-btn
              flat
              @click="leftExpandAll"
              icon="unfold_more"
              class="col-1 bg-indigo-2"
              color="indigo-10"
            ></q-btn>
            <q-btn
              flat
              @click="leftCollapseAll"
              icon="unfold_less"
              class="col-1 bg-indigo-3"
              color="indigo-8"
            ></q-btn>
          </div>
          <div class="col-1"></div>
          <!-- 右半邊的search & button -->
          <div class="col row">
            <q-input
              ref="filterRef"
              filled
              v-model="rightFilter"
              :label="t('filterKey')"
              class="col"
              bg-color="deep-purple-1"
            ></q-input>
            <q-btn
              flat
              @click="rightExpandAll"
              icon="unfold_more"
              class="col-1 bg-deep-purple-2"
              color="deep-purple-10"
            ></q-btn>
            <q-btn
              flat
              @click="rightCollapseAll"
              icon="unfold_less"
              class="col-1 bg-deep-purple-3"
              color="deep-purple-8"
            ></q-btn>
          </div>
        </div>
        <div class="row">
          <!-- 左半邊的q-tree -->
          <div class="col border round-sm">
            <q-tree
              :nodes="resNodeDTO"
              node-key="label"
              tick-strategy="leaf"
              @update:ticked="handleSelect"
              v-model:ticked="tickedNodes"
              :default-expand-all="true"
              ref="left_treeElm"
              :filter="leftFilter"
            ></q-tree>
          </div>
          <div class="col-1"></div>
          <!-- 右半邊的q-tree -->
          <div class="col border round-sm">
            <q-tree
              v-if="selectedNodes.length > 0"
              :nodes="selectedNodes"
              node-key="label"
              :default-expand-all="true"
              v-model:expanded="expandedNodes"
              ref="right_treeElm"
              :filter="rightFilter"
            >
            </q-tree>
            <div v-else class="text-grey">
              {{ t("accessToken.noPermissionBound") }}
            </div>
            <!-- 展開的: {{ expandedNodes }} <br /> -->
            <!-- 打勾的: {{ tickedNodes }} -->
          </div>
        </div>
      </q-card-section>
      <q-card-actions class="justify-end">
        <q-btn
          outline
          :label="t('cancel')"
          color="blue-grey-8"
          @click="closeDialog"
        />
        <q-btn
          outline
          :label="t('store')"
          color="primary"
          @click="onSaveAccessTokenResRel"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { ref } from "vue";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { useAccessTokenStore } from "stores/accessToken";

const { t } = useI18n();

//receipt store settings
const store = useAccessTokenStore();
//tickedNodes 左邊樹狀checkbox選中的節點
const { tickedNodes, resNodeDTO } = storeToRefs(store);
const { saveAuthFunc } = store;

const isOpenDialog = defineModel();

const closeDialog = () => {
  isOpenDialog.value = false;
};

const onSaveAccessTokenResRel = () => {
  const relDTOList = [];

  const collectValues = (nodes) => {
    nodes.forEach((node) => {
      if (node.value) {
        let relDTO = {};
        relDTO.rightCode = node.value;
        relDTOList.push(relDTO);
      }
      if (node.children) {
        collectValues(node.children);
      }
    });
  };
  collectValues(selectedNodes.value);
  saveAuthFunc(relDTOList);
  closeDialog();
};

// ======= 授權功能 處理區段 =======
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
    resNodeDTO.value,
    tickedNodes.value,
    expandedNodes.value
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
