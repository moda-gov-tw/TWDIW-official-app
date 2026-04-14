<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1500px; width: 1200px"
    >
      <!-- 常用欄位 -->
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <div class="text-h6">{{ t("normalFields.title") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>
      <q-card-section class="q-px-sm">
        <q-scroll-area
          :style="{ height: isMobile ? '45vh' : '60vh', 'max-height': '100vh' }"
          :thumb-style="{
            right: '4px',
            borderRadius: '5px',
            background: '#D33E5F',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <q-card-section style="max-width: 1200px; margin: 0 auto">
            <div
              class="row q-py-sm q-my-md gt-sm"
              :style="{ borderBottom: '1px solid #CCC' }"
            >
              <div class="col-2 col-lg-1">
                <p class="text-center q-pl-lg-lg">
                  {{ t("normalFields.dialog.addAndDelete") }}
                </p>
              </div>
              <div class="col-3 col-lg-4">
                <p class="text-center">
                  <span class="text-red">*</span
                  >{{ t("basicFields.column.cname") }}
                </p>
              </div>
              <div class="col-4">
                <p class="text-center q-pr-lg-xl">
                  <span class="text-red">*</span
                  >{{ t("basicFields.column.ename") }}
                </p>
              </div>
              <div class="col-3">
                <p class="text-center">
                  <span class="text-red">*</span
                  >{{ t("regularExpression.table.description") }}
                </p>
              </div>
            </div>
            <q-form ref="formRef" greedy class="q-gutter-md q-mt-md">
              <ul ref="list" class="">
                <li
                  v-for="(item, index) in rows"
                  :key="item.id"
                  class="q-mb-sm"
                >
                  <div
                    class="row items-start q-gutter-x-md"
                    v-if="$q.screen.gt.sm"
                    style="height: 60px"
                  >
                    <q-btn
                      unelevated
                      icon="remove"
                      color="indigo-7"
                      @click="removeRow(index)"
                      class="gt-xs"
                      :disable="rows.length <= 1"
                      :style="{ height: '40px' }"
                    />
                    <q-btn
                      unelevated
                      icon="add"
                      color="primary"
                      @click="addRow"
                      class="gt-xs"
                      :disable="index !== rows.length - 1"
                      :style="{ height: '40px' }"
                    />
                    <q-input
                      v-model="rows[index].cname"
                      outlined
                      class="col"
                      :rules="rulesAll('cnameCreateRules')"
                      :placeholder="
                        t('input', { input: t('basicFields.column.cname') })
                      "
                      dense
                    />
                    <q-input
                      v-model="rows[index].ename"
                      outlined
                      class="col"
                      :rules="rulesAll('enameCreateRules')"
                      :placeholder="
                        t('input', { input: t('basicFields.column.ename') })
                      "
                      dense
                    />
                    <q-select
                      outlined
                      class="col text-ellipsis-input"
                      v-model="rows[index].regularExpression"
                      :options="regularExpressionsList"
                      :label="
                        t('choose', {
                          input: t('normalFields.dialog.regularExpression')
                        })
                      "
                      option-label="name"
                      option-value="id"
                      dense
                      :rules="rulesAll('regularExpressionRules')"
                      menu-position="bottom"
                      :popup-content-class="'no-modal'"
                      behavior="menu"
                      :popup-content-style="{
                        height: getPopupHeight(regularExpressionsList),
                        width: '300px'
                      }"
                    >
                      <template v-slot:option="scope">
                        <q-item v-bind="scope.itemProps">
                          <q-item-section>
                            <q-item-label class="wrap-text">
                              {{ scope.opt.name }}
                            </q-item-label>
                          </q-item-section>
                        </q-item>
                      </template>
                    </q-select>
                  </div>

                  <div v-if="$q.screen.lt.md" class="q-pb-md">
                    <div class="q-mb-sm">
                      <p class="text-grey">
                        <span class="text-red">*</span>{{ t("fields.cname") }}：
                      </p>
                      <q-input
                        v-model="rows[index].cname"
                        outlined
                        class="col"
                        :rules="rulesAll('cnameCreateRules')"
                        :placeholder="
                          t('input', { input: t('basicFields.column.cname') })
                        "
                        dense
                      />
                    </div>
                    <div class="q-mb-sm">
                      <p class="text-grey">
                        <span class="text-red">*</span>{{ t("fields.ename") }}：
                      </p>
                      <q-input
                        v-model="rows[index].ename"
                        outlined
                        class="col"
                        :rules="rulesAll('enameCreateRules')"
                        :placeholder="
                          t('input', { input: t('basicFields.column.ename') })
                        "
                        dense
                      />
                    </div>
                    <div class="q-mb-sm">
                      <p class="text-grey">
                        <span class="text-red">*</span>
                        {{ t("normalFields.dialog.regularExpression") + "：" }}
                      </p>
                      <q-select
                        outlined
                        class="col text-ellipsis-input"
                        v-model="rows[index].regularExpression"
                        :options="regularExpressionsList"
                        option-label="name"
                        option-value="id"
                        dense
                        style="max-width: 100%"
                        :rules="rulesAll('regularExpressionRules')"
                        menu-position="bottom"
                        :popup-content-class="'no-modal'"
                        behavior="menu"
                        :popup-content-style="{
                          height: getPopupHeight(regularExpressionsList),
                          width: '260px'
                        }"
                      >
                        <template v-slot:option="scope">
                          <q-item v-bind="scope.itemProps">
                            <q-item-section>
                              <q-item-label class="wrap-text">
                                {{ scope.opt.name }}
                              </q-item-label>
                            </q-item-section>
                          </q-item>
                        </template>
                      </q-select>
                    </div>
                    <div class="row items-center q-gutter-x-sm">
                      <q-btn
                        unelevated
                        icon="remove"
                        color="indigo-4"
                        @click="removeRow(index)"
                        class="col"
                        :disable="rows.length <= 1"
                      />
                      <q-btn
                        unelevated
                        icon="add"
                        color="primary"
                        @click="addRow"
                        class="col"
                        :disable="index !== rows.length - 1"
                      />
                    </div>
                  </div>
                </li>
              </ul>
            </q-form>
          </q-card-section>
        </q-scroll-area>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          size="16px"
          @click="onCancelClick"
          dense
          :style="{ width: '100px' }"
          :disable="dialogLoading"
        />
        <q-btn
          unelevated
          :label="t('confirm')"
          color="primary"
          size="16px"
          @click="onOKClick"
          dense
          :style="{ width: '100px' }"
          :disable="dialogLoading"
          :loading="dialogLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch, defineEmits, computed } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import autoAnimate from "@formkit/auto-animate";
import { useFieldStore } from "stores/field";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { useRulesAll } from "utils/rules";

const { rulesAll } = useRulesAll();
const { t } = useI18n();

const fieldStore = useFieldStore();
const { dialogLoading, regularExpressionsList } = storeToRefs(fieldStore);
const { addField, regularExpression } = fieldStore;

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();

// 表單相關參考
const formRef = ref(null);
const $q = useQuasar();
const $n = useNotify();

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 動畫效果參考
const list = ref(null);

// 資料列初始化
const rows = ref([{ cname: "", ename: "", regularExpression: null }]);

// 新增列
const addRow = () => {
  rows.value.push({ cname: "", ename: "", regularExpression: null });
};

// 刪除列
const removeRow = (index) => {
  rows.value.splice(index, 1);
};

// 確認送出
const onOKClick = async () => {
  const isValid = await formRef.value.validate();

  if (isValid) {
    // 檢查重複
    const enameSet = new Set();
    const duplicateEnames = [];

    for (const row of rows.value) {
      if (enameSet.has(row.ename)) {
        duplicateEnames.push(row.ename);
      } else {
        enameSet.add(row.ename);
      }
    }

    if (duplicateEnames.length > 0) {
      $n.error(
        t("normalFields.dialog.error.repeat", {
          input: duplicateEnames.join(", ")
        })
      );
    } else {
      const newNows = rows.value.map((row) => ({
        cname: row.cname,
        ename: row.ename,
        regularExpressionId: row.regularExpression.id
      }));

      const success = await addField(newNows);
      if (success) {
        onDialogOK();
      }
    }
  } else {
    $n.error(t("requiredFields"));
  }
};

// 對話框顯示時載入資料
const onDialogShow = () => {
  regularExpression();
};

// 取消處理
const onCancelClick = () => {
  onDialogCancel();
};

// 監聽列表變化套用動畫
watch(list, (newValue) => {
  if (newValue) {
    autoAnimate(list.value);
  }
});

// 計算下拉選單高度
const getPopupHeight = (list) => {
  const ITEM_HEIGHT = 48;
  const MAX_ITEMS = 3;
  return list.length >= MAX_ITEMS ? "144px" : `${list.length * ITEM_HEIGHT}px`;
};
</script>
<style scoped>
.wrap-text {
  white-space: normal;
  word-wrap: break-word;
}

.text-ellipsis-input :deep(.q-field__native) span {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  max-width: 100%;
}

.text-ellipsis-input :deep(.q-field__control) {
  width: 100%;
  overflow: hidden;
}
</style>
