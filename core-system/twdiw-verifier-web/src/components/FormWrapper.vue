<template>
  <q-form @submit="onSubmit" class="fit column q-gutter-sm q-pa-sm">
    <template v-for="(slices, rowIndex) in fieldSlices" :key="rowIndex">
      <div class="row q-gutter-sm">
        <template v-for="(field, idx) in slices" :key="idx">
          <div class="col" :class="{ 'q-mb-sm': field.outlined }">
            <slot :name="`field-${field.name}`" :field="field">
              <p v-if="field.title || false" class="q-mb-sm input-title">
                {{ field.label }}
              </p>
              <q-select
                v-model="model[field.field]"
                dense
                v-if="field.inputType === `select`"
                class="q-pa-none"
                :color="field.color || 'primary'"
                :options="field.filteredOptions || field.options"
                :disable="field.disable || false"
                :readonly="field.readonly || false"
                :label="field.label"
                :clearable="field.clearable || field.useInput || false"
                :rules="field.rules"
                :outlined="field.outlined || false"
                :class="{ disabled_style: field.disable }"
                :emit-value="field.emitValue || false"
                :use-input="field.useInput || false"
                new-value-mode="add"
                :behavior="field.behavior"
                :menu-anchor="field.menuAnchor"
                :menu-self="field.menuSelf"
                :offset="field.offset"
                :cover="field.cover"
                map-options
                @filter="(val, update) => filterFn(val, update, field)"
              />
              <date-picker
                dense
                minimal
                v-model="model[field.field]"
                v-else-if="field.inputType === 'date'"
                :disable="field.disable || false"
                :label="field.label"
                :clearable="field.clearable || false"
                :required="field.required || false"
                :outlined="field.outlined || false"
                :class="{ disabled_style: field.disable }"
                :dateRules="field.dateRules || false"
              />
              <q-input
                :type="isPwd ? 'password' : 'text'"
                v-else-if="field.inputType === 'password'"
                v-model="model[field.field]"
                dense
                :placeholder="field.hint"
                class="q-pa-none"
                :class="{ disabled_style: field.disable }"
                :clearable="field.clearable || false"
                :color="field.color || 'primary'"
                :rules="field.rules"
                :disable="field.disable || false"
                :readonly="field.readonly || false"
                :label="field.label"
                :outlined="field.outlined || false"
              >
                <template v-slot:append>
                  <q-icon
                    :name="isPwd ? 'visibility_off' : 'visibility'"
                    class="cursor-pointer"
                    @click="isPwd = !isPwd"
                  />
                </template>
              </q-input>
              <!-- option group -->
              <template v-else-if="field.inputType === 'optionGroup'">
                <div class="row items-center">
                  <p class="q-pa-none q-ma-none text-bold">{{ field.label }}</p>
                  <q-checkbox
                    v-if="field.selectAll"
                    label="全選"
                    v-model="selectAll"
                    @update:model-value="toggleSelectAll(field)"
                  />
                  <q-option-group
                    inline
                    :options="field.options"
                    :color="field.color || 'primary'"
                    :type="field.optionType"
                    v-model="model[field.field]"
                  />
                </div>
              </template>
              <template
                v-else-if="field.inputType === 'queryEmail' || 'queryTel'"
              >
                <q-input
                  :type="
                    [
                      'number',
                      'search',
                      'textarea',
                      'time',
                      'date',
                      'email',
                      'tel',
                      'file',
                      'url',
                      'datetime-local'
                    ].includes(field.inputType)
                      ? field.inputType
                      : 'text'
                  "
                  v-model="model[field.field]"
                  :placeholder="field.hint"
                  class="q-pa-none"
                  dense
                  :class="{ disabled_style: field.disable }"
                  :color="field.color || 'primary'"
                  :rules="field.rules"
                  :disable="field.disable || false"
                  :readonly="field.readonly || false"
                  label-slot
                  :outlined="field.outlined || false"
                >
                  <template v-slot:label>
                    <span style="font-size: 14px"> {{ field.label }}</span>
                  </template>
                </q-input>
              </template>
              <template v-else>
                <q-input
                  :type="
                    [
                      'number',
                      'search',
                      'textarea',
                      'time',
                      'date',
                      'email',
                      'tel',
                      'file',
                      'url',
                      'datetime-local'
                    ].includes(field.inputType)
                      ? field.inputType
                      : 'text'
                  "
                  v-model="model[field.field]"
                  dense
                  :placeholder="field.hint"
                  class="q-pa-none"
                  :class="{ disabled_style: field.disable }"
                  clearable
                  :color="field.color || 'primary'"
                  :rules="field.rules"
                  :disable="field.disable || false"
                  :readonly="field.readonly || false"
                  :label="field.label"
                  :outlined="field.outlined || false"
                />
              </template>
            </slot>
          </div>
        </template>
      </div>
    </template>

    <div class="row q-pa-sm">
      <template v-for="field in fields" :key="field.name">
        <div
          v-if="isVisible(field.visible) && extraList.includes(field.inputType)"
        >
          <q-toggle
            v-if="field.inputType === 'toggle'"
            left-label
            :color="field.color || 'primary'"
            :label="field.label"
            v-model="model[field.field]"
          />
          <q-checkbox
            v-else-if="field.inputType === 'checkbox'"
            left-label
            :color="field.color || 'primary'"
            :label="field.label"
            v-model="model[field.field]"
          />
        </div>
      </template>
    </div>
    <q-space></q-space>
    <div class="col">
      <slot name="action">
        <div class="row q-gutter-sm justify-end">
          <!-- 眼睛 -->
          <q-btn
            v-if="props.showVisible"
            flat
            dense
            color="primary"
            :icon="props.isAccountVisible ? 'visibility' : 'visibility_off'"
            @click="visible()"
          />
          <q-btn outline color="primary" type="reset" @click="onReset">
            {{ t("reset") }}
          </q-btn>
          <q-btn
            color="primary"
            type="submit"
            :disable="disabled"
            :loading="loading"
            >{{ props.submitLabel || t("filter") }}</q-btn
          >
        </div>
      </slot>
    </div>
  </q-form>
</template>
<script setup>
import { ref } from "vue";
import DatePicker from "components/DatePicker.vue";
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const props = defineProps([
  "fields",
  "submitLabel",
  "layout",
  "disabled",
  "loading",
  "showVisible",
  "isAccountVisible"
]);
const model = defineModel();
const extraList = ["toggle", "checkbox"];

const isPwd = ref(true);
const selectAll = ref(false);

// 眼睛呈現
const visible = () => {
  emit("update:isAccountVisible", !props.isAccountVisible);
};

const isVisible = (e) => {
  return typeof e === "function" ? e() : !(e === false);
};

const emit = defineEmits(["confirm", "reset", "update:isAccountVisible"]);
const onSubmit = () => {
  emit("confirm", model);
};

const onReset = () => {
  emit("reset", model);
};

const fieldSlices = computed(() => {
  const res = [];
  const fields = props.fields;
  const layout =
    props.layout || Array.from({ length: fields.length }, (x) => 1);
  let start = 0;

  for (let i = 0; i < layout.length; i++) {
    const end = start + layout[i];
    res.push(fields.slice(start, end));
    start += layout[i];
  }
  return res;
});

const toggleSelectAll = (field) => {
  if (selectAll.value) {
    model.value[field.field] = field.options.map((e) => e.value);
  } else {
    model.value[field.field] = field.options
      .filter((e) => e.disable)
      .map((e) => e.value);
  }
};

const filterFn = (val, update, field) => {
  if (val === "") {
    update(() => {
      // 恢復到原始選項
      field.filteredOptions = [...field.options];
    });
    return;
  }

  update(() => {
    const needle = val.toLowerCase();

    field.filteredOptions = field.options.filter((option) => {
      // 假設選項可能是字串或物件，根據實際情況處理。
      if (typeof option === "string") {
        return option.toLowerCase().includes(needle);
      } else if (typeof option === "object" && option.label) {
        return option.label.toLowerCase().includes(needle);
      }
      // 如果選項既不是字串也不是有 label 屬性的物件，默認不過濾。
      return false;
    });
  });
};
</script>
<style scoped>
.label {
  width: 30px;
}

.disabled_style {
  background-color: #eeeeee;
  color: #ffffff;
  border-radius: 5px;
}
</style>
