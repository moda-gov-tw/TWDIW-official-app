<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 1000px; width: 500px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 建立 VC 清除資料排程 -->
        <div class="text-h6">{{ t("schedule.dialog.addTitle") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section style="max-width: 500px; margin: 0 auto">
        <q-form ref="scheduleForm">
          <q-card-section class="q-mt-md">
            <div class="row items-start q-gutter-x-md">
              <p class="text-h6 pt-4">
                {{ t("schedule.dialog.frequency") + "：" }}
              </p>
              <q-select
                v-model="frequency"
                :options="frequencies"
                :label="t('choose', { input: t('schedule.table.type') })"
                emit-value
                map-options
                :rules="requiredRule"
                :style="{ height: '60px', minWidth: '246px' }"
                dense
                hide-bottom-space
                outlined
                menu-position="bottom"
                :popup-content-class="'no-modal'"
                behavior="menu"
                class="col"
              />
            </div>

            <!-- Daily -->
            <div
              v-show="frequency === 'Daily'"
              class="row items-start q-gutter-x-md q-mt-md"
            >
              <p class="text-h6 pt-4">
                {{ t("schedule.dialog.executionTime") + "：" }}
              </p>
              <q-input
                outlined
                v-model="time"
                mask="time"
                :rules="requiredRule"
                :style="{ minWidth: '220px' }"
                dense
                color="primary"
                class="col"
              >
                <template v-slot:append>
                  <q-icon name="access_time" class="cursor-pointer">
                    <q-popup-proxy
                      cover
                      transition-show="scale"
                      transition-hide="scale"
                    >
                      <q-time v-model="time">
                        <div class="row items-center justify-end">
                          <q-btn
                            v-close-popup
                            label="Close"
                            color="primary"
                            flat
                          />
                        </div>
                      </q-time>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>
            </div>

            <!-- Weekly -->
            <div v-show="frequency === 'Weekly'">
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionWeek") + "：" }}
                </p>
                <q-select
                  v-model="weekday"
                  :options="weekdays"
                  :rules="frequency === 'Weekly' ? requiredRule : []"
                  :style="{ height: '60px', minWidth: '246px' }"
                  dense
                  hide-bottom-space
                  outlined
                  emit-value
                  map-options
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  behavior="menu"
                  class="col"
                />
              </div>
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionTime") + "：" }}
                </p>
                <q-input
                  outlined
                  v-model="time"
                  mask="time"
                  :rules="requiredRule"
                  :style="{ minWidth: '220px' }"
                  dense
                  color="primary"
                  class="col"
                >
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer">
                      <q-popup-proxy
                        cover
                        transition-show="scale"
                        transition-hide="scale"
                      >
                        <q-time v-model="time">
                          <div class="row items-center justify-end">
                            <q-btn
                              v-close-popup
                              label="Close"
                              color="primary"
                              flat
                            />
                          </div>
                        </q-time>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>

            <!-- Monthly -->
            <div v-show="frequency === 'Monthly'">
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionWeek") + "：" }}
                </p>
                <q-select
                  v-model="day"
                  :options="days"
                  :rules="frequency === 'Monthly' ? requiredRule : []"
                  :style="{ height: '60px', minWidth: '246px' }"
                  dense
                  hide-bottom-space
                  outlined
                  emit-value
                  map-options
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  behavior="menu"
                  class="col"
                />
              </div>
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionTime") + "：" }}
                </p>
                <q-input
                  outlined
                  v-model="time"
                  mask="time"
                  :rules="requiredRule"
                  :style="{ minWidth: '220px' }"
                  dense
                  color="primary"
                  class="col"
                >
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer">
                      <q-popup-proxy
                        cover
                        transition-show="scale"
                        transition-hide="scale"
                      >
                        <q-time v-model="time">
                          <div class="row items-center justify-end">
                            <q-btn
                              v-close-popup
                              label="Close"
                              color="primary"
                              flat
                            />
                          </div>
                        </q-time>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>

            <!-- Quarterly -->
            <div v-show="frequency === 'Quarterly'">
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionWeek") + "：" }}
                </p>
                <q-select
                  v-model="quarter"
                  :options="quarters"
                  :rules="frequency === 'Quarterly' ? requiredRule : []"
                  :style="{ height: '60px', minWidth: '246px' }"
                  dense
                  hide-bottom-space
                  outlined
                  emit-value
                  map-options
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  behavior="menu"
                  class="col"
                />
              </div>
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4" style="visibility: hidden">
                  {{ t("schedule.dialog.executionWeek") + "：" }}
                </p>
                <q-select
                  v-model="day"
                  :options="days"
                  :rules="frequency === 'Quarterly' ? requiredRule : []"
                  :style="{ height: '60px', minWidth: '246px' }"
                  dense
                  hide-bottom-space
                  outlined
                  emit-value
                  map-options
                  menu-position="bottom"
                  :popup-content-class="'no-modal'"
                  behavior="menu"
                  :popup-content-style="{
                    height: getPopupHeight
                  }"
                  class="col"
                />
              </div>
              <div class="row items-start q-gutter-x-md q-mt-md">
                <p class="text-h6 pt-4">
                  {{ t("schedule.dialog.executionTime") + "：" }}
                </p>
                <q-input
                  outlined
                  v-model="time"
                  mask="time"
                  :rules="requiredRule"
                  :style="{ minWidth: '220px' }"
                  dense
                  color="primary"
                  class="col"
                >
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer">
                      <q-popup-proxy
                        cover
                        transition-show="scale"
                        transition-hide="scale"
                      >
                        <q-time v-model="time">
                          <div class="row items-center justify-end">
                            <q-btn
                              v-close-popup
                              label="Close"
                              color="primary"
                              flat
                            />
                          </div>
                        </q-time>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </div>
            </div>
          </q-card-section>
        </q-form>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          unelevated
          :label="t('cancel')"
          outline
          class="text-primary"
          @click="onCancelClick"
          :style="{ width: '120px' }"
        />
        <q-btn
          :label="t('confirm')"
          unelevated
          color="primary"
          @click="onSubmit"
          :style="{ width: '120px' }"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import { useNotify } from "src/utils/plugin";
import { useI18n } from "vue-i18n";
defineEmits([...useDialogPluginComponent.emits]);
const { t } = useI18n();
const { dialogRef, onDialogCancel, onDialogOK } = useDialogPluginComponent();

const $n = useNotify();
const onDialogHide = () => {};

const onDialogShow = () => {};

const onCancelClick = () => {
  onDialogCancel();
};

const q = useQuasar();

const scheduleForm = ref(null);
const frequency = ref("");
const time = ref(
  new Date().toLocaleTimeString("zh-TW", {
    hour12: false,
    hour: "2-digit",
    minute: "2-digit"
  })
);
const weekday = ref();
const day = ref();
const quarter = ref();

const frequencies = [
  { label: t("schedule.select.Daily"), value: "Daily" },
  { label: t("schedule.select.Weekly"), value: "Weekly" },
  { label: t("schedule.select.Monthly"), value: "Monthly" },
  { label: t("schedule.select.Quarterly"), value: "Quarterly" }
];

const weekdays = [
  { label: t("schedule.select.weekday.1"), value: 1 },
  { label: t("schedule.select.weekday.2"), value: 2 },
  { label: t("schedule.select.weekday.3"), value: 3 },
  { label: t("schedule.select.weekday.4"), value: 4 },
  { label: t("schedule.select.weekday.5"), value: 5 },
  { label: t("schedule.select.weekday.6"), value: 6 },
  { label: t("schedule.select.weekday.7"), value: 7 }
];

const days = [...Array(31)].map((_, i) => ({
  label: i + 1 + t("schedule.dialog.date"),
  value: i + 1
}));

const quarterGroups = [
  [1, 4, 7, 10],
  [2, 5, 8, 11],
  [3, 6, 9, 12]
];

const quarters = quarterGroups.map((group) => ({
  label: group.map((m) => t(`schedule.select.quarter.${m}`)).join("、"),
  value: group
}));

const getPopupHeight = computed(() => {
  const length = days.length;
  if (length >= 6) {
    return "144px";
  }
  return `${length * 48}px`;
});

const requiredRule = [(val) => !!val || t("required")];

const onSubmit = async () => {
  const isValid = await scheduleForm.value.validate();

  if (!isValid) {
    $n.error(t("requiredFields"));
    return;
  } else {
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    onDialogOK({
      type: frequency.value,
      week: weekday.value,
      date: day.value,
      month: quarter.value,
      time: time.value,
      timezone: timezone
    });
  }
};

watch(frequency, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    weekday.value = null;
    day.value = null;
    quarter.value = null;
  }
});
</script>

<style>
.pt-4 {
  padding-top: 4px;
}
</style>
