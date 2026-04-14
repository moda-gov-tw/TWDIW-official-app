<template>
  <div
    class="round-sm scroll q-pa-sm shadow-3"
    :style="{
      width: '100%',
      overflowX: 'auto',
      maxWidth: isMobile ? 'clamp(60vw, 80vw, 75vw)' : '100%'
    }"
  >
    <q-splitter
      v-model="splitterModel"
      :style="{
        height: isMobile ? '51vh' : '45vh',
        'max-height': '85vh'
      }"
      separator-class="bg-grey-3"
      :horizontal="isMobile"
      :disable="isMobile"
    >
      <!-- 左側 Tabs -->
      <template v-slot:before>
        <div class="full-height bg-indigo-7">
          <div
            class="scroll-container"
            :style="{
              overflowX: 'auto',
              overflowY: 'hidden',
              whiteSpace: 'nowrap',
              minWidth: '0',
              ...(isMobile ? { flex: 1, height: '100%' } : {})
            }"
          >
            <q-tabs
              v-model="selectedTab"
              :vertical="!isMobile"
              :horizontal="isMobile"
              class="text-white bg-indigo-7"
              indicator-color="orange-6"
              active-color="orange-6"
              active-bg-color="indigo-5"
              no-caps
              inline-label
              scrollable
              left-icon="null"
              right-icon="null"
              :style="{
                width: isMobile ? 'max-content' : '100%',
                height: isMobile ? '100%' : ''
              }"
            >
              <template
                v-for="(group, groupIndex) in props.formData.groups"
                :key="group.name"
              >
                <q-tab
                  :name="group.name"
                  :label="group.name"
                  :icon="group.rule === 'all' ? 'done_all' : 'filter_alt'"
                  class="wrap-tab-label"
                />
                <q-separator
                  v-if="groupIndex !== props.formData.groups.length - 1"
                  color="indigo-5"
                />
              </template>
            </q-tabs>
          </div>
        </div>
      </template>

      <!-- 右側 Panels -->
      <template v-slot:after>
        <q-tab-panels
          v-model="selectedTab"
          animated
          vertical
          transition-prev="jump-up"
          transition-next="jump-up"
        >
          <q-tab-panel
            v-for="group in props.formData.groups"
            :key="group.name"
            :name="group.name"
            :class="[isMobile ? 'q-pa-xs' : 'q-pt-none']"
          >
            <div
              :class="[
                'justify-between alert-text justify-end full-width q-my-sm',
                isMobile ? 'column' : 'row'
              ]"
            >
              <span v-if="group.rule === 'all'">
                {{
                  t("vp.groups.ruleAllMsg", {
                    count: group.vcDatas.filter((item) => item.isTicked).length
                  })
                }}
              </span>
              <span v-else>
                {{ t("vp.groups.rulePickMsg", { max: group.max }) }}
              </span>

              <div
                :class="[
                  'row items-center no-wrap',
                  isMobile ? 'justify-end' : 'justify-start'
                ]"
              >
                <q-btn
                  dense
                  outline
                  icon="remove"
                  color="indigo-6"
                  class="q-ml-xs"
                  size="sm"
                  @click="expandAll(false)"
                />
                <q-btn
                  dense
                  icon="add"
                  color="indigo-6"
                  class="q-ml-xs"
                  size="sm"
                  @click="expandAll(true)"
                />
              </div>
            </div>

            <template v-for="vcData in group.vcDatas" :key="vcData.serialNo">
              <div v-if="vcData.isTicked" class="q-mb-sm">
                <q-list bordered class="rounded-borders">
                  <!-- VC 模板 -->
                  <q-expansion-item
                    v-model="expansionStates[vcData.serialNo]"
                    header-class="text-indigo-6 text-h6-cus rounded-borders"
                    expand-separator
                    :label="`${vcData.name}（${vcData.businessName}）`"
                  >
                    <q-card>
                      <q-card-section
                        :class="['text-h7 q-pt-xs', isMobile ? 'q-pb-xs' : '']"
                      >
                        <!-- 選擇的欄位 -->
                        <q-list>
                          <template
                            v-for="(vcField, vcFieldIndex) in vcData.vcFields"
                            :key="`${vcField.ename}_${vcFieldIndex}`"
                          >
                            <q-item
                              dense
                              v-if="vcField.isTicked"
                              class="q-pa-none text-h7"
                            >
                              <q-item-section avatar>
                                <q-icon
                                  name="check_circle"
                                  color="indigo-6"
                                  :size="isMobile ? 'xs' : 'sm'"
                                />
                              </q-item-section>
                              <q-item-section>
                                <q-item-label>
                                  {{
                                    formData.model.value === "2" &&
                                    vcField.customFieldName
                                      ? vcField.customFieldName
                                      : vcField.ename
                                  }}（{{ vcField.cname }}
                                  <span
                                    v-if="
                                      vcField.isRequired ||
                                      vcField.isRequired === null
                                    "
                                    class="text-red"
                                    >*</span
                                  >
                                  ）</q-item-label
                                >
                              </q-item-section>
                            </q-item>
                          </template>
                        </q-list>
                      </q-card-section>
                    </q-card>
                  </q-expansion-item>
                </q-list>
              </div>
            </template>
          </q-tab-panel>
        </q-tab-panels>
      </template>
    </q-splitter>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useQuasar } from "quasar";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const $q = useQuasar();

// 確認頁資料
const splitterModel = ref(12);
const selectedTab = ref("");

const expansionStates = ref({});

const props = defineProps({
  formData: Object
});

// 判斷是否為手機裝置
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

// 初始化展開狀態
const initExpansionStates = () => {
  const state = {};
  props.formData.groups.forEach((group) => {
    group.vcDatas.forEach((vc) => {
      state[vc.serialNo] = true;
    });
  });
  expansionStates.value = state;
};

const expandAll = (shouldExpand) => {
  const currentGroup = props.formData.groups.find(
    (group) => group.name === selectedTab.value
  );
  if (!currentGroup) return;

  currentGroup.vcDatas.forEach((vc) => {
    expansionStates.value[vc.serialNo] = shouldExpand;
  });
};

onMounted(() => {
  // 預設選第一個 group
  if (props.formData.groups.length > 0) {
    selectedTab.value = props.formData.groups[0].name;
  }
  initExpansionStates();
});
</script>
<style scoped>
.scroll-container::-webkit-scrollbar {
  height: 6px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: #d3d3d3;
  border-radius: 10px;
  border: 1px solid transparent;
  background-clip: padding-box;
}

.wrap-tab-label {
  white-space: normal;
  word-break: break-word;
  text-align: left;
  justify-content: left;
  padding-left: 20px;
}
</style>
