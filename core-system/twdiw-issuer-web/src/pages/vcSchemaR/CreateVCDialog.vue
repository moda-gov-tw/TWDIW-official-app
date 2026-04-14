<template>
  <q-dialog
    ref="dialogRef"
    @hide="onDialogHide"
    @show="onDialogShow"
    persistent
  >
    <q-card
      class="q-dialog-plugin text-black"
      style="max-width: 2000px; width: 1500px"
    >
      <q-card-section
        class="row justify-between items-center bg-grey-4 text-white"
      >
        <!-- 建立 VC 模板 -->
        <div class="text-h6">{{ t("vcSchema.dialog.title") }}</div>
        <q-btn flat round color="white" icon="close" @click="onCancelClick" />
      </q-card-section>

      <q-card-section v-if="!show">
        <q-scroll-area
          :style="{ height: isMobile ? '45vh' : '70vh', 'max-height': '100vh' }"
          :thumb-style="{
            borderRadius: '5px',
            background: '#D33E5F',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <q-form ref="formRef" greedy class="q-px-md">
            <div class="row q-my-sm q-col-gutter-md">
              <div class="col-12 col-lg-9">
                <div class="row q-col-gutter-md">
                  <div class="col-12 col-lg-6">
                    <div
                      :class="['q-mb-sm', !isMobile ? 'row items-start' : '']"
                    >
                      <p
                        :class="[
                          !isMobile ? 'q-mr-sm pt-10 title-width' : 'q-mb-sm'
                        ]"
                        style="display: flex; align-items: flex-end"
                      >
                        <span class="text-red">*</span>
                        {{ t("vcSchema.table.serialNo") }}
                      </p>
                      <q-input
                        outlined
                        class="col"
                        v-model="vcNumber"
                        :rules="[
                          (val) =>
                            !!val || t('vcSchema.dialog.serialNo.rules.input'),
                          (val) =>
                            val.length <= 35 ||
                            t('vcSchema.rules.length', { max: 35 }),
                          (val) =>
                            /^[a-z0-9_]+$/.test(val) ||
                            t('vcSchema.dialog.serialNo.rules.format')
                        ]"
                        :label="t('vcSchema.dialog.serialNo.placeholder')"
                        dense
                        :disable="Boolean(props.serialNo)"
                      />
                    </div>
                    <div
                      :class="['q-mb-sm', !isMobile ? 'row items-start' : '']"
                    >
                      <p
                        :class="[
                          !isMobile ? 'q-mr-sm pt-10 title-width' : 'q-mb-sm'
                        ]"
                        style="display: flex; align-items: flex-end"
                      >
                        <span class="text-red">*</span>
                        {{ t("vcSchema.table.name") }}
                      </p>
                      <q-input
                        outlined
                        class="col"
                        v-model="vcName"
                        :rules="[
                          (val) =>
                            !!val || t('vcSchema.dialog.name.rules.input'),
                          (val) =>
                            val.length <= 18 ||
                            t('vcSchema.rules.length', { max: 18 }),
                          (val) =>
                            /^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(val) ||
                            t('vcSchema.dialog.name.rules.format')
                        ]"
                        :label="t('vcSchema.dialog.name.placeholder')"
                        dense
                      />
                    </div>
                    <div
                      :class="['q-mb-sm', !isMobile ? 'row items-start' : '']"
                    >
                      <div
                        :class="[
                          'row items-start',
                          !isMobile
                            ? 'q-mr-sm pt-10 title-width justify-between'
                            : 'q-mb-sm'
                        ]"
                        style="display: flex; align-items: center"
                      >
                        <p>
                          <span class="text-red">*</span>
                          <span>
                            {{ t("vcSchema.validityPeriod") }}
                          </span>
                        </p>
                        <!-- 提醒 icon -->
                        <BaseTooltip
                          icon="info_outline"
                          color="primary"
                          size="sm"
                          :title="t('vcSchema.validityPeriod')"
                          :text="t('vcSchema.notice.showExpireTimeInfo')"
                          :position="
                            isMdSize
                              ? { top: '30px', left: '32px' }
                              : { top: '-10px', left: '460px' }
                          "
                          :iconProps="{
                            class: !isMobile ? 'q-ml-auto' : 'q-ml-xs'
                          }"
                          :width="isMdSize ? '' : '400px'"
                        />
                      </div>
                      <div class="row items-center col">
                        <q-input
                          outlined
                          class="col"
                          v-model="validityPeriod"
                          type="number"
                          :rules="[
                            (val) =>
                              !!val ||
                              t('vcSchema.dialog.validityPeriod.rules.input'),
                            (val) =>
                              val >= 1 ||
                              t(
                                'vcSchema.dialog.validityPeriod.rules.greaterThanZero'
                              ),
                            (val) =>
                              Number.isInteger(Number(val)) ||
                              t('vcSchema.dialog.validityPeriod.rules.integer'),
                            (val) =>
                              val.toString().length <= 4 ||
                              t('vcSchema.rules.length', { max: 4 })
                          ]"
                          :placeholder="
                            t('vcSchema.dialog.validityPeriod.placeholder')
                          "
                          dense
                          min="1"
                        />
                        <q-select
                          outlined
                          class="col q-ml-sm"
                          v-model="periodUnit"
                          :options="periodUnitOptions"
                          option-label="label"
                          option-value="value"
                          dense
                          :rules="[(val) => !!val || t('vcSchema.rules.unit')]"
                          :popup-content-class="'no-modal'"
                          behavior="menu"
                        />
                      </div>
                    </div>
                    <!-- IAL 等級 -->
                    <div
                      :class="['q-mb-sm', !isMobile ? 'row items-start' : '']"
                    >
                      <div
                        :class="[
                          'row items-start',
                          !isMobile
                            ? 'q-mr-sm pt-10 title-width justify-between'
                            : 'q-mb-sm'
                        ]"
                        style="display: flex; align-items: center"
                      >
                        <p>
                          {{ t("vcSchema.ial") }}
                        </p>
                        <!-- 提醒 icon -->
                        <BaseTooltip
                          icon="info_outline"
                          color="primary"
                          size="sm"
                          :title="t('vcSchema.ial')"
                          :text="t('vcSchema.notice.ial')"
                          :position="
                            isMdSize
                              ? { top: '30px', left: '32px' }
                              : { top: '-10px', left: '460px' }
                          "
                          :iconProps="{
                            class: !isMobile ? 'q-ml-auto' : 'q-ml-xs'
                          }"
                          :width="isMdSize ? '' : '400px'"
                        />
                      </div>
                      <div class="row items-center col">
                        <q-select
                          outlined
                          class="col"
                          v-model="ial"
                          :options="[...ialTypeIdOptions]"
                          option-label="label"
                          option-value="value"
                          dense
                          :rules="[(val) => !!val || t('vcSchema.rules.unit')]"
                          :popup-content-class="'no-modal'"
                          behavior="menu"
                        />
                      </div>
                    </div>

                    <!-- 分隔線 -->
                    <q-separator
                      class="q-mb-md q-mt-xs"
                      size="3px"
                      color="grey-1"
                    />
                    <!-- 進階設定 -->
                    <q-btn
                      class="q-mt-sm q-mb-xs text-indigo-6"
                      :icon="expandIcon()"
                      :label="t('vcSchema.dialog.button.showMore')"
                      color="indigo-2"
                      @click="showMore = !showMore"
                    />
                    <div v-show="showMore">
                      <!-- 靜態 QR Code -->
                      <div class="row items-center relative-position q-mt-md">
                        <div class="relative-position cus-title-width">
                          <p
                            style="
                              display: flex;
                              align-items: flex-end;
                              justify-content: space-between;
                            "
                          >
                            {{ t("vcSchema.dialog.qrCode.label") }}
                            <!-- 提醒 icon -->
                            <BaseTooltip
                              icon="info_outline"
                              color="primary"
                              size="sm"
                              :position="{ top: '-45px', left: '70px' }"
                              :iconProps="{
                                class: isMobile ? 'q-ml-sm' : ''
                              }"
                              :title="t('vcSchema.dialog.qrCode.label')"
                              width="357px"
                            >
                              <p class="text-body2">
                                {{ t("vcSchema.notice.webView") }}
                              </p>
                              <p class="text-body2 q-pt-sm">
                                {{ t("vcSchema.notice.openPage") }}
                              </p></BaseTooltip
                            >
                          </p>
                          <!-- 提醒 文字 -->
                        </div>
                        <!-- checkbox -->
                        <q-checkbox
                          v-model="needQRCode"
                          size="md"
                          keep-color
                          color="indigo-4"
                          :rules="[
                            (val) =>
                              (val !== null && val !== undefined) ||
                              t('vcSchema.rules.check')
                          ]"
                        />
                      </div>
                      <!-- 若勾選 checkbox -->
                      <div v-if="needQRCode" class="q-mt-lg">
                        <!-- 模式 -->
                        <div
                          :class="[
                            'q-mb-sm q-mt-md',
                            isMobile ? '' : 'row items-start'
                          ]"
                        >
                          <div class="row items-start">
                            <p
                              :class="[
                                !isMobile ? 'pt-10 cus-title-width' : 'q-mb-sm'
                              ]"
                              style="display: flex; align-items: flex-end"
                            >
                              {{ t("vcSchema.dialog.qrCode.type") }}
                            </p>
                          </div>
                          <div class="row items-center col">
                            <q-select
                              outlined
                              class="col"
                              :class="isMobile ? '' : 'q-ml-sm'"
                              v-model="formData.type"
                              :options="typeOptions"
                              option-label="label"
                              option-value="value"
                              dense
                              :label="
                                t('vcSchema.rules.selectField', {
                                  field: t('vcSchema.dialog.qrCode.type')
                                })
                              "
                              :rules="[
                                (val) =>
                                  !!val ||
                                  t('vcSchema.rules.selectField', {
                                    field: t('vcSchema.dialog.qrCode.type')
                                  })
                              ]"
                              :popup-content-class="'no-modal'"
                            />
                          </div>
                        </div>
                        <!-- 組織業務系統 -->
                        <div
                          :class="[
                            'q-mb-sm',
                            !isMobile ? 'row items-start ' : ''
                          ]"
                        >
                          <div class="relative-position cus-title-width">
                            <p
                              class="pt-10"
                              :style="[
                                isMobile
                                  ? {}
                                  : {
                                      display: 'flex',
                                      'align-items': 'center',
                                      'justify-content': 'space-between'
                                    }
                              ]"
                            >
                              {{ t("vcSchema.dialog.qrCode.issuerServiceUrl") }}
                              <!-- 提醒 icon -->
                              <BaseTooltip
                                icon="info_outline"
                                color="primary"
                                size="sm"
                                :title="
                                  t('vcSchema.dialog.qrCode.issuerServiceUrl')
                                "
                                :text="
                                  t('vcSchema.notice.issuerServiceUrlNotice')
                                "
                                :position="
                                  isMdSize
                                    ? { top: '30px', left: '32px' }
                                    : { top: '-5px', left: '380px' }
                                "
                                :iconProps="{
                                  class: isMobile ? 'q-ml-sm' : ''
                                }"
                              />
                            </p>
                          </div>
                          <q-input
                            outlined
                            class="col"
                            :class="isMobile ? '' : 'q-ml-sm'"
                            v-model="formData.issuerServiceUrl"
                            :rules="[
                              (val) => {
                                const pattern =
                                  /^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-\.~:\/?#\[\]@!$&'()*+,;=%]*)?$/;
                                return (
                                  pattern.test(val) || t('vcSchema.rules.url')
                                );
                              }
                            ]"
                            :placeholder="
                              t('vcSchema.rules.field', {
                                field: t(
                                  'vcSchema.dialog.qrCode.issuerServiceUrl'
                                )
                              })
                            "
                            dense
                          />
                        </div>
                      </div>
                      <!-- 分隔線 -->
                      <q-separator
                        class="q-mt-sm q-mb-md"
                        size="3px"
                        color="grey-1"
                      />
                    </div>
                  </div>
                  <div class="col-12 col-lg-6">
                    <div :class="[!isMobile ? 'row items-start' : '']">
                      <p :class="[!isMobile ? 'q-mr-md pt-10' : 'q-mb-sm']">
                        {{ t("vcSchema.dialog.cardUpload.label") }}
                      </p>
                      <div class="col">
                        <q-file
                          ref="fileInput"
                          outlined
                          v-model="picName"
                          dense
                          clearable
                          :label="t('vcSchema.dialog.cardUpload.placeholder')"
                          @update:model-value="onFilesAdded"
                        >
                          <template v-slot:label>
                            <q-icon
                              v-if="imageTemp"
                              class="q-mr-xs"
                              name="check_circle"
                              color="indigo-6"
                              size="sm"
                            />
                            <span>{{ fileLabel }}</span>
                          </template>
                          <template v-slot:append>
                            <q-icon
                              v-if="imageTemp"
                              name="cancel"
                              color="grey-4"
                              class="cursor-pointer"
                              @click="clearImage"
                            />
                            <q-icon name="attachment" @click="openFileDialog" />
                          </template>
                        </q-file>
                      </div>
                    </div>
                    <div
                      class="row items-center justify-between q-mt-md relative-position"
                    >
                      <!-- 提醒 icon -->
                      <BaseTooltip
                        :fullWidth="true"
                        :position="{ top: '40px', left: '0' }"
                        width="450px"
                        :title="t('vcSchema.dialog.button.notice')"
                      >
                        <template #trigger>
                          <q-btn
                            color="primary"
                            outline
                            rounded
                            icon="info_outline"
                            size="md"
                            :label="t('vcSchema.dialog.button.notice')"
                            style="border: 2px solid"
                            :class="isMobile ? 'full-width' : ''"
                          />
                        </template>

                        <p class="text-body2">
                          {{ t("vcSchema.notice.picNotice") }}
                        </p>
                        <p class="text-body2 q-pt-sm">
                          {{ t("vcSchema.notice.picNoticeCustom") }}
                        </p>
                      </BaseTooltip>

                      <q-btn
                        type="a"
                        size="md"
                        outline
                        rounded
                        :href="defaultSample"
                        download="sample.svg"
                        class="text-body1 text-orange-6"
                        :class="isMobile ? 'full-width q-mt-md' : ''"
                        style="border: 2px solid"
                        >{{ t("vcSchema.dialog.button.imageVector") }}
                      </q-btn>
                      <q-btn
                        type="a"
                        size="md"
                        outline
                        rounded
                        :href="mdCOver"
                        target="_blank"
                        class="text-body1 text-orange-6"
                        :class="isMobile ? 'full-width q-mt-md' : ''"
                        style="border: 2px solid"
                        >{{ t("vcSchema.dialog.button.imageSpecification") }}
                      </q-btn>
                    </div>
                  </div>
                </div>
              </div>

              <div class="col-12 col-lg-3">
                <div class="relative-position row justify-lg-end">
                  <template v-if="imageUrl">
                    <q-img
                      :src="imageUrl"
                      class="fit-cover"
                      :style="{
                        borderRadius: '8px',
                        width: isMobile ? '100%' : '320px',
                        aspectRatio: '8 / 5'
                      }"
                      :alt="t('vcSchema.dialog.imagePreview')"
                    >
                      <q-img
                        :src="sampleMark"
                        fit="fill"
                        class="bg-transparent absolute-bottom"
                        :style="{
                          padding: '0',
                          margin: '0'
                        }"
                      />
                    </q-img>
                  </template>
                  <div
                    v-else
                    class="row items-center justify-center text-h6 text-grey q-pt-sm"
                    :style="{
                      border: '2px dashed #ccc',
                      borderRadius: '10px',
                      width: isMobile ? '100%' : '324px',
                      aspectRatio: '8 / 5'
                    }"
                  >
                    {{ t("vcSchema.dialog.imagePreview") }}
                    <q-img
                      :src="sampleRe"
                      :style="{
                        width: isMobile ? '100%' : '320px',
                        aspectRatio: '8 / 5'
                      }"
                    ></q-img>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="row q-pb-sm q-my-md justify-between gt-md"
              :style="{ borderBottom: '1px solid #CCC' }"
            >
              <div class="col-auto">
                <p class="text-md-h6 text-center q-pl-xl q-mr-lg-xl">
                  {{ t("vcSchema.dialog.table.addAndDelete") }}
                </p>
              </div>
              <div class="col-auto text-center q-pl-md q-pl-lg-none">
                <p class="text-md-h6">
                  <span class="text-red">*</span>{{ t("fields.type") }}
                </p>
              </div>
              <div class="col-2 text-center q-pl-lg-xl">
                <p class="text-md-h6">
                  <span class="text-red">*</span>{{ t("fields.cname") }}
                </p>
              </div>
              <div class="col-2 text-center q-pl-xl q-mr-md">
                <p class="text-md-h6">
                  <span class="text-red">*</span>{{ t("fields.ename") }}
                </p>
              </div>
              <div class="col-2 text-center q-pl-xl">
                <p class="text-md-h6">
                  <span class="text-red">*</span
                  >{{ t("vcSchema.table.description") }}
                </p>
              </div>

              <div class="col-2 row justify-between">
                <div class="col-auto text-center q-px-lg-md">
                  <p class="text-md-h5">
                    {{ t("fields.isRequired") }} <span class="text-red"></span>
                  </p>
                </div>
                <div class="col-auto text-center q-pr-xl">
                  <p class="text-md-h5">
                    {{ t("vcSchema.dialog.table.sort")
                    }}<span class="text-red"></span>
                  </p>
                </div>
              </div>
            </div>
            <ul ref="list">
              <li v-for="(item, index) in rows" :key="item.id" class="q-mb-sm">
                <div
                  class="row items-start q-gutter-x-sm"
                  v-if="$q.screen.gt.md"
                >
                  <q-btn
                    unelevated
                    icon="remove"
                    color="indigo-6"
                    @click="removeRow(index)"
                    :disable="rows.length <= 1"
                    class="gt-xs"
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
                  <q-select
                    class="col-auto gt-xs"
                    outlined
                    v-model="rows[index].type"
                    :options="options"
                    option-value="value"
                    option-label="label"
                    dense
                    :rules="[(val) => !!val || t('vcSchema.rules.required')]"
                    hide-bottom-space
                    :label="
                      t('vcSchema.rules.field', { field: t('fields.type') })
                    "
                    @update:model-value="(val) => handleTypeChange(val, index)"
                    :popup-content-class="'no-modal'"
                    behavior="menu"
                    :style="{ width: '160px' }"
                  />
                  <q-select
                    v-if="rows[index].type.value !== 'custom'"
                    class="col gt-xs text-ellipsis-input"
                    outlined
                    ref="fieldRefs"
                    v-model="rows[index].cname"
                    :options="getNameOptions(rows[index].type.value)"
                    option-value="ename"
                    option-label="cname"
                    dense
                    :rules="[
                      (val) => fieldValidate(val, rows[index].type.value)
                    ]"
                    :label="
                      t('vcSchema.rules.selectField', {
                        field: t('fields.cname')
                      })
                    "
                    :popup-content-class="'no-modal'"
                    behavior="menu"
                    :popup-content-style="{
                      height: getPopupHeight(rows[index].type.value),
                      width: '280px'
                    }"
                  >
                    <template v-slot:option="scope">
                      <q-item v-bind="scope.itemProps">
                        <q-item-section>
                          <q-item-label class="wrap-text">
                            {{ scope.opt.cname }}
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                  <q-input
                    v-else
                    class="col gt-xs"
                    outlined
                    ref="fieldRefs"
                    v-model="rows[index].cname"
                    dense
                    :rules="[
                      (val) => !!val || t('vcSchema.rules.required'),
                      (val) =>
                        /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(val) ||
                        t('vcSchema.dialog.table.format'),
                      (val) =>
                        val.length <= 18 ||
                        t('vcSchema.rules.length', { max: 18 })
                    ]"
                    :placeholder="t('vcSchema.dialog.table.placeholder')"
                  />
                  <q-input
                    v-model="rows[index].ename"
                    outlined
                    class="col gt-xs"
                    dense
                    :disable="rows[index].type.value !== 'custom'"
                    :rules="[
                      (val) =>
                        !!val ||
                        t('vcSchema.rules.field', { field: t('fields.ename') }),
                      (val) =>
                        /^(?!id$)[a-zA-Z0-9_]+$/.test(val) ||
                        t('vcSchema.dialog.table.ename.format'),
                      (val) =>
                        val.length <= 50 ||
                        t('vcSchema.rules.length', { max: 50 })
                    ]"
                    :placeholder="
                      rows[index].type.value !== 'custom'
                        ? t('vcSchema.rules.field', {
                            field: t('fields.ename')
                          })
                        : t('vcSchema.dialog.table.ename.placeholder')
                    "
                  />
                  <q-select
                    class="col gt-xs text-ellipsis-input"
                    outlined
                    v-model="rows[index].regularExpressionId"
                    :options="regularExpressionsList"
                    :disable="rows[index].type.value !== 'custom'"
                    option-label="description"
                    option-value="id"
                    dense
                    :rules="[
                      (val) =>
                        !!val ||
                        t('vcSchema.rules.selectField', {
                          field: t('vcSchema.table.description')
                        })
                    ]"
                    :popup-content-class="'no-modal'"
                    :label="
                      t('vcSchema.rules.selectField', {
                        field: t('vcSchema.table.description')
                      })
                    "
                    behavior="menu"
                    :display-value="
                      getRegularExpressionName(rows[index].regularExpressionId)
                    "
                    @update:model-value="
                      (val) => {
                        rows[index].regularExpressionId = val.id; // 確保手動更新
                        rows[index].regularExpression = val;
                      }
                    "
                    :popup-content-style="{
                      height: getRegexPopupHeight(),
                      width: '280px'
                    }"
                  />
                  <div class="col-2 row justify-between">
                    <div class="col-auto q-mx-lg q-pl-xl-sm">
                      <q-checkbox
                        v-model="rows[index].isRequired"
                        size="md"
                        keep-color
                        color="indigo-5"
                        :disable="index === 0"
                      >
                        <q-tooltip
                          v-if="index === 0"
                          anchor="top middle"
                          self="bottom middle"
                          :offset="[0, 50]"
                          class="bg-grey-2 text-body2"
                          max-width="300px"
                        >
                          {{ t("vcSchema.notice.picNoticeCustom") }}
                        </q-tooltip>
                      </q-checkbox>
                    </div>
                    <div class="col-auto">
                      <q-btn
                        unelevated
                        icon="arrow_upward"
                        color="primary"
                        @click="moveRow(index, -1)"
                        :disable="index === 0"
                        class="q-mr-sm"
                        :style="{ height: '40px' }"
                      />
                      <q-btn
                        unelevated
                        icon="arrow_downward"
                        color="primary"
                        @click="moveRow(index, 1)"
                        :disable="index === rows.length - 1"
                        :style="{ height: '40px' }"
                      />
                    </div>
                  </div>
                </div>

                <div v-if="$q.screen.lt.lg" class="q-pb-md">
                  <div class="q-mb-sm">
                    <p>
                      <span class="text-red">*</span>{{ t("fields.type") }}：
                    </p>
                    <q-select
                      class="full-width"
                      outlined
                      v-model="rows[index].type"
                      :options="options"
                      option-value="value"
                      option-label="label"
                      dense
                      :rules="[(val) => !!val || t('vcSchema.rules.required')]"
                      :label="
                        t('vcSchema.rules.field', { field: t('fields.type') })
                      "
                      @update:model-value="
                        (val) => handleTypeChange(val, index)
                      "
                      menu-position="bottom"
                      :popup-content-class="'no-modal'"
                      behavior="menu"
                    />
                  </div>
                  <div class="q-mb-sm">
                    <p>
                      <span class="text-red">*</span>{{ t("fields.cname") }}：
                    </p>
                    <q-select
                      v-if="rows[index].type.value !== 'custom'"
                      class="text-ellipsis-input"
                      outlined
                      ref="fieldRefs"
                      v-model="rows[index].cname"
                      :options="getNameOptions(rows[index].type.value)"
                      dense
                      option-value="ename"
                      option-label="cname"
                      :rules="[
                        (val) => fieldValidate(val, rows[index].type.value)
                      ]"
                      :label="
                        t('vcSchema.rules.selectField', {
                          field: t('fields.cname')
                        })
                      "
                      menu-position="bottom"
                      :popup-content-class="'no-modal'"
                      behavior="menu"
                      :popup-content-style="{
                        width: '260px'
                      }"
                    >
                      <template v-slot:option="scope">
                        <q-item v-bind="scope.itemProps">
                          <q-item-section>
                            <q-item-label class="wrap-text">
                              {{ scope.opt.cname }}
                            </q-item-label>
                          </q-item-section>
                        </q-item>
                      </template>
                    </q-select>
                    <q-input
                      v-else
                      class="full-width ellipsis"
                      outlined
                      ref="fieldRefs"
                      v-model="rows[index].cname"
                      dense
                      :rules="[
                        (val) => !!val || t('vcSchema.rules.required'),
                        (val) =>
                          val.length <= 18 ||
                          t('vcSchema.rules.length', { max: 18 }),
                        (val) =>
                          /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(val) ||
                          t('vcSchema.dialog.table.length')
                      ]"
                      :placeholder="t('vcSchema.dialog.table.placeholder')"
                    />
                  </div>
                  <div class="q-mb-sm">
                    <p>
                      <span class="text-red">*</span>{{ t("fields.ename") }}：
                    </p>
                    <q-input
                      v-model="rows[index].ename"
                      outlined
                      class="full-width"
                      dense
                      :disable="rows[index].type.value !== 'custom'"
                      :rules="[
                        (val) =>
                          !!val ||
                          t('vcSchema.rules.field', {
                            field: t('fields.ename')
                          }),
                        (val) =>
                          !val ||
                          /^(?!id$)[a-zA-Z0-9_]+$/.test(val) ||
                          t('vcSchema.dialog.table.ename.format'),
                        (val) =>
                          val.length <= 50 ||
                          t('vcSchema.rules.length', { max: 50 })
                      ]"
                      :placeholder="
                        rows[index].type.value !== 'custom'
                          ? t('vcSchema.rules.field', {
                              field: t('fields.ename')
                            })
                          : t('vcSchema.dialog.table.ename.placeholder')
                      "
                    />
                  </div>
                  <div class="q-mb-xs">
                    <p>
                      <span class="text-red">*</span>
                      {{ t("vcSchema.table.description") }}：
                    </p>
                    <q-select
                      class="col text-ellipsis-input"
                      outlined
                      v-model="rows[index].regularExpressionId"
                      :options="regularExpressionsList"
                      :disable="rows[index].type.value !== 'custom'"
                      option-label="description"
                      option-value="id"
                      dense
                      :label="
                        t('vcSchema.rules.selectField', {
                          field: t('vcSchema.table.description')
                        })
                      "
                      :rules="[
                        (val) =>
                          !!val ||
                          t('vcSchema.rules.selectField', {
                            field: t('vcSchema.table.description')
                          })
                      ]"
                      :popup-content-class="'no-modal'"
                      behavior="menu"
                      :display-value="
                        getRegularExpressionName(
                          rows[index].regularExpressionId
                        )
                      "
                      @update:model-value="
                        (val) => {
                          rows[index].regularExpressionId = val.id; // 確保手動更新
                          rows[index].regularExpression = val;
                        }
                      "
                      :popup-content-style="{
                        height: getRegexPopupHeight(),
                        width: '280px'
                      }"
                    />
                  </div>
                  <div class="q-mb-sm">
                    {{ t("fields.isRequired") }}：
                    <q-checkbox
                      v-model="rows[index].isRequired"
                      size="md"
                      keep-color
                      color="indigo-4"
                      :disable="index === 0"
                    >
                      <q-tooltip
                        v-if="index === 0"
                        anchor="center right"
                        self="center left"
                        class="bg-grey-2 text-body2"
                      >
                        {{ t("vcSchema.notice.picNoticeCustom") }}
                      </q-tooltip>
                    </q-checkbox>
                  </div>
                  <div class="row items-center q-gutter-x-sm">
                    <q-btn
                      unelevated
                      icon="remove"
                      color="indigo-4"
                      @click="removeRow(index)"
                      :disable="rows.length <= 1"
                      class="col"
                    />
                    <q-btn
                      unelevated
                      icon="add"
                      color="primary"
                      @click="addRow"
                      class="col"
                      :disable="index !== rows.length - 1"
                    />
                    <div class="col-auto">
                      <q-btn
                        unelevated
                        icon="arrow_upward"
                        color="primary"
                        @click="moveRow(index, -1)"
                        :disable="index === 0"
                        class="q-mr-sm"
                      />
                      <q-btn
                        unelevated
                        icon="arrow_downward"
                        color="primary"
                        @click="moveRow(index, 1)"
                        :disable="index === rows.length - 1"
                      />
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </q-form>
        </q-scroll-area>
      </q-card-section>
      <!-- 下一步 -->
      <q-card-section style="max-width: 1500px; margin: 0 auto" v-else>
        <q-scroll-area
          :style="{ height: isMobile ? '45vh' : '60vh', 'max-height': '100vh' }"
          :thumb-style="{
            borderRadius: '5px',
            background: '#D33E5F',
            width: '5px',
            opacity: 0.75
          }"
          content-active-style="width: 100%;"
          content-style="width: 100%;"
        >
          <div class="q-pa-md">
            <div class="row">
              <div class="col-12 q-mb-md">
                <!-- 模板代碼 -->
                <div :class="[isMobile ? 'column' : ' info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("vcSchema.table.serialNo") + "：" }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{ vcNumber }}
                  </span>
                </div>
                <!-- 模板名稱 -->
                <div :class="[isMobile ? 'column' : ' info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("vcSchema.table.name") + "：" }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{ vcName }}
                  </span>
                </div>
                <!-- 有效期間 -->
                <div :class="[isMobile ? 'column' : ' info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("vcSchema.validityPeriod") + "：" }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{ validityPeriod }} {{ periodUnit.label }}
                  </span>
                </div>
                <!-- IAL 等級 -->
                <div :class="[isMobile ? 'column' : ' info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{ t("vcSchema.ial") + "：" }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{
                      ial.label === t("vcSchema.dialog.ial.firstOption")
                        ? t("vcSchema.dialog.ial.noChoice")
                        : ial.label
                    }}
                  </span>
                </div>
                <!-- 靜態 QR Code 模式-->
                <div :class="[isMobile ? 'column' : ' info-block']">
                  <span class="text-h6-cus text-grey-7">
                    {{
                      t("vcSchema.dialog.qrCode.label") +
                      " " +
                      t("vcSchema.dialog.qrCode.type") +
                      "："
                    }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{
                      needQRCode
                        ? formData.type.label
                        : t(`vcSchema.dialog.${needQRCode}`)
                    }}
                  </span>
                </div>
                <!-- 組織業務系統 URL -->
                <div
                  :class="[isMobile ? 'column' : ' info-block']"
                  v-if="needQRCode"
                >
                  <span class="text-h6-cus text-grey-7">
                    {{ t("vcSchema.dialog.qrCode.issuerServiceUrl") + "：" }}
                  </span>
                  <span class="text-h6-cus q-ma-none word-break">
                    {{ formData.issuerServiceUrl }}
                  </span>
                </div>
              </div>
            </div>
            <q-table
              dense
              flat
              class="sticky-header no-height"
              :rows="rows"
              :columns="isMobileColumns"
              row-key="id"
              :rows-per-page-options="[10, 20, 50, 0]"
              :pagination="pagination"
            >
              <template #pagination="scope">
                <table-pagination
                  :scope="scope"
                  :rows-number="computedPagination"
                />
              </template>
            </q-table>
          </div>
        </q-scroll-area>
      </q-card-section>

      <q-card-actions align="right">
        <div
          v-if="!Boolean(props.serialNo)"
          :class="
            isMobile ? 'col-12 text-negative text-center' : 'alert-text q-mr-md'
          "
        >
          {{ t("vcSchema.notice.temporaryStorage") }}
        </div>
        <q-btn
          outline
          unelevated
          v-if="show"
          :label="t('vcSchema.temporaryStorage')"
          color="indigo-6"
          :class="isMobile ? 'col-12 q-mt-xs' : ''"
          :style="!isMobile ? 'width: 120px' : ''"
          @click="onOKClick(true)"
          :disable="createDialogLoading || tempDialogLoading"
          :loading="tempDialogLoading"
        />
        <q-btn
          :outline="show"
          unelevated
          :label="t(`vcSchema.dialog.button.step.${show.toString()}`)"
          color="primary"
          @click="toggleShow"
          :class="isMobile ? 'col-12 q-mt-xs' : ''"
          :style="!isMobile ? 'width: 120px' : ''"
          :disable="createDialogLoading || tempDialogLoading"
        />
        <q-btn
          unelevated
          v-if="show"
          :label="t('vcSchema.dialog.button.submit')"
          color="primary"
          :class="isMobile ? 'col-12 q-mt-xs' : ''"
          :style="!isMobile ? 'width: 120px' : ''"
          @click="onOKClick()"
          :disable="createDialogLoading || tempDialogLoading"
          :loading="createDialogLoading"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import {
  ref,
  reactive,
  toRefs,
  computed,
  watch,
  onMounted,
  nextTick
} from "vue";
import { useDialogPluginComponent, useQuasar } from "quasar";
import autoAnimate from "@formkit/auto-animate";
import { useNotify } from "src/utils/plugin";
import { useVcSchemaStore } from "stores/vcSchema";
import { useFieldStore } from "stores/field";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import sampleRe from "src/assets/sampleRe.png";
import sampleMark from "src/assets/sampleMark.png";
import mdCOver from "src/assets/數位憑證皮夾＿卡面上傳規範.pdf";
import defaultSample from "src/assets/defaultSample.svg";
import { useUserConfigStore } from "stores/userConfig";
import BaseTooltip from "src/components/BaseTooltip.vue";

const vcSchema = useVcSchemaStore();
const field = useFieldStore();
const {
  basicFieldList,
  normalFieldList,
  createDialogLoading,
  tempDialogLoading,
  ialTypeAllList,
  vcCoverInfo
} = storeToRefs(vcSchema);
const {
  getFieldList,
  createVCSchema,
  getIALTypeList,
  tempVCSchema,
  getVCCover,
  resetVCcover,
  resetVCSchemaDetail,
  checkSerialNumber
} = vcSchema;

const { regularExpressionsList } = storeToRefs(field);
const { regularExpression } = field;

const userConfig = useUserConfigStore();

const props = defineProps({
  row: Object,
  id: Number,
  businessId: String,
  serialNo: String,
  name: String,
  crDatetime: String,
  businessTWName: String,
  unitTypeExpire: String,
  lengthExpire: Number,
  ialTemp: String,
  isVerifyTemp: Boolean,
  isTemp: Boolean,
  schemaDetail: Object,
  isNeedQRCodeTemp: Boolean,
  typeTemp: String,
  issuerServiceUrlTemp: String
});

const { id, isTemp } = toRefs(props);

const $n = useNotify();
const { t } = useI18n();
const $q = useQuasar();
const show = ref(false);
const isVerify = ref(false);
const formRef = ref(null);
const showMore = ref(false);
const needQRCode = ref(false);

// [FORM]
const form = {
  type: "",
  issuerServiceUrl: ""
};

// [REACTIVE]: 表單
const formData = reactive({ ...form });

const fileInput = ref(null);
const { fromAPIorgId } = storeToRefs(userConfig);

const ialTypeIdOptions = computed(() => [
  { label: t("vcSchema.dialog.ial.firstOption"), value: "" },
  ...ialTypeAllList.value
]);

const imageTemp = ref(null);

const validityPeriod = ref("");

const units = ["DAY", "MONTH", "YEAR"];
const periodUnitOptions = units.map((unit) => ({
  label: t(`vcSchema.select.${unit}`),
  value: unit
}));

const pagination = ref({
  rowsPerPage: 10, // 預設每頁顯示數量
  sortBy: "",
  descending: false,
  page: 1
});

// 判斷是否為手機
const isMobile = computed(() => {
  return $q.screen.lt.md;
});

const isMdSize = computed(() => {
  return $q.screen.lt.lg;
});

// 修改初始值
const periodUnit = ref({ label: t("vcSchema.select.MONTH"), value: "MONTH" });

const ial = ref({ label: t("vcSchema.select.default"), value: "" });

const rows = ref([
  {
    id: Date.now(),
    type: "",
    cname: "",
    ename: "",
    regularExpressionId: "",
    isRequired: true
  }
]);

const fillRowsFromDetail = (detailList) => {
  rows.value = detailList.map((item) => {
    const isCustom = item.type?.toLowerCase() === "custom";

    return {
      id: item.id,
      type: matchSelectOption(options.value, item.type?.toLowerCase()),
      cname: isCustom
        ? item.cname
        : {
            cname: item.cname,
            ename: item.ename,
            regularExpression: item.regularExpression ?? null
          },
      ename: item.ename,
      regularExpressionId:
        item.regularExpressionId ?? item.regularExpression?.id ?? "",
      regularExpression: item.regularExpression ?? null,
      isRequired: item.isRequired
    };
  });
};

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogOK, onDialogCancel } = useDialogPluginComponent();

const onOKClick = async (isTemp = false) => {
  // 準備一個新的陣列來存放重整後的資料
  const formattedRows = [];

  // 使用 forEach 處理每一行資料
  rows.value.forEach((row, index) => {
    // 準備新的物件結構
    const formattedRow = {
      type: row.type.value.toUpperCase(), // 將 type 轉為大寫
      cname: "",
      ename: "",
      regularExpressionId: "",
      cardCoverData: index === 0 ? 1 : undefined, // 如果是第一個項目，則設定為 1
      isRequired: row.isRequired
    };

    // 判斷 chinese 是否為物件
    if (typeof row.cname === "object" && row.cname !== null) {
      // 如果是物件,使用物件內的值
      formattedRow.cname = row.cname.cname;
      formattedRow.ename = row.cname.ename;
      formattedRow.regularExpressionId = row.regularExpressionId;
    } else {
      // 如果不是物件,直接使用值
      formattedRow.cname = row.cname;
      formattedRow.ename = row.ename;
      formattedRow.regularExpressionId = row.regularExpressionId;
    }

    // 將處理好的物件加入陣列
    formattedRows.push(formattedRow);
  });

  const typeCnameMap = new Map();
  const duplicateNames = [];

  for (const row of formattedRows) {
    const key = `${row.ename}`;
    if (typeCnameMap.has(key)) {
      // 如果已經存在，將 ename 加入重複名稱陣列
      if (!duplicateNames.includes(`「${row.ename}」`)) {
        duplicateNames.push(`「${row.ename}」`);
      }
    } else {
      typeCnameMap.set(key, true);
    }
  }

  // 如果有重複的名稱，顯示所有重複的 cname
  if (duplicateNames.length > 0) {
    $n.error(t("vcSchema.error.repeat", { field: duplicateNames.join("、") }));
    return; // 停止執行
  }

  // 準備最終要傳送的資料
  const data = {
    serialNo: vcNumber.value,
    name: vcName.value,
    categoryId: 4,
    ial: ial.value.value,
    isVerify: isVerify.value,
    lengthExpire: validityPeriod.value,
    unitTypeExpire: periodUnit.value.value,
    vcItemFieldDTOList: formattedRows, // 使用重整後的陣列
    cardCover: imageUrl.value,
    needQRCode: needQRCode.value,
    type: formData.type.value,
    issuerServiceUrl: formData.issuerServiceUrl,
    isTemp: isTemp,
    id: id.value
  };

  const success = isTemp
    ? await tempVCSchema(data)
    : await createVCSchema(data);
  if (success) {
    onDialogOK(data);
  }
};

const onCancelClick = () => {
  onDialogCancel();
};

const options = computed(() => {
  if (fromAPIorgId.value === "00000000") {
    return [
      {
        id: 1,
        label: t("vcSchema.select.basic"),
        value: "basic"
      },
      {
        id: 3,
        label: t("vcSchema.select.custom"),
        value: "custom"
      }
    ];
  }
  return [
    {
      id: 1,
      label: t("vcSchema.select.basic"),
      value: "basic"
    },
    {
      id: 2,
      label: t("vcSchema.select.normal"),
      value: "normal"
    },
    {
      id: 3,
      label: t("vcSchema.select.custom"),
      value: "custom"
    }
  ];
});

const typeOptions = [
  { label: t("vcSchema.select.1"), value: "1" },
  { label: t("vcSchema.select.2"), value: "2" }
];

const vcNumber = ref("");
const vcName = ref("");

const list = ref(null);
const computedPagination = ref("");

// 下一步
const toggleShow = async () => {
  if (!show.value) {
    const isValid = await validateAllInputs();

    // 靜態QR Code未勾選
    if (!needQRCode.value) {
      // 清空模式、組織業務系統URL
      Object.assign(formData, form);
    }

    if (isValid) {
      if (!isTemp.value) {
        const ischeckSerialNumber = await checkSerialNumber(vcNumber.value);
        if (!ischeckSerialNumber) return;
      }
      show.value = true;
      computedPagination.value = rows.value.length;
    } else {
      $n.error(t("requiredFields"));
    }
  } else {
    // 從第二個頁面返回時，直接切換
    show.value = false;
  }
};

const validateAllInputs = async () => {
  const isFormValid = await formRef.value.validate();
  const isValidityPeriodValid = validityPeriod.value && periodUnit.value;
  const areRowsValid = rows.value.every(
    (row) => row.type && row.cname && row.ename
  );

  return isFormValid && areRowsValid && isValidityPeriodValid;
};

watch(list, (newValue) => {
  if (newValue) {
    autoAnimate(list.value);
  }
});

const addRow = () => {
  rows.value.push({
    id: Date.now(),
    type: "",
    cname: "",
    ename: "",
    regularExpressionId: "",
    isRequired: true
  });
};

const removeRow = (index) => {
  if (rows.value.length > 1) {
    rows.value.splice(index, 1);
  }
};

const moveRow = (index, direction) => {
  if (
    (direction === -1 && index > 0) ||
    (direction === 1 && index < rows.value.length - 1)
  ) {
    const newIndex = index + direction;
    const temp = rows.value[index];
    rows.value[index] = rows.value[newIndex];
    rows.value[newIndex] = temp;
  }
  rows.value[0].isRequired = true;
};

const fieldRefs = ref([]);

const handleTypeChange = (selectedType, index) => {
  rows.value[index].type = selectedType;
  rows.value[index].cname = null;
  rows.value[index].ename = "";
  rows.value[index].regularExpressionId = "";

  nextTick(() => {
    const inputRef = fieldRefs.value[index];
    if (inputRef && inputRef.validate) {
      inputRef.validate();
    }
  });
};

const getNameOptions = (rowType) => {
  try {
    if (!rowType) return [];

    if (rowType === "basic" && basicFieldList.value) {
      return basicFieldList.value;
    } else if (rowType === "normal" && normalFieldList.value) {
      return normalFieldList.value;
    }
    return [];
  } catch (error) {
    console.error("Error in getNameOptions:", error);
    return [];
  }
};

const fieldValidate = (val, typeValue) => {
  const options = getNameOptions(typeValue);

  if (typeValue && (!options || options.length === 0)) {
    return t("vcSchema.valid.commonFields");
  }

  if (!val) return t("vcSchema.rules.required");

  return true;
};

const getPopupHeight = (rowType) => {
  const options = getNameOptions(rowType);
  const length = options.length;
  if (length >= 6) {
    return "144px";
  }
  return `${length * 48}px`;
};

watch(
  () => rows.value.map((row) => row.cname),
  (newValues, oldValues) => {
    rows.value.forEach((row, index) => {
      if (row.type.value === "custom") {
        const selectedRegex = regularExpressionsList.value.find(
          (item) => item.id === row.regularExpressionId
        );
        if (selectedRegex) {
          rows.value[index].regularExpression = selectedRegex;
        }
      } else if (row.type.value !== "custom" && row.cname) {
        if (typeof row.cname === "object" && row.cname.ename) {
          rows.value[index].ename = row.cname.ename;
          rows.value[index].regularExpressionId =
            row.cname.regularExpression?.id;
          rows.value[index].regularExpression = row.cname.regularExpression;
        }
      }
    });
  },
  { deep: true }
);

const baseColumns = [
  {
    name: "type",
    required: true,
    label: t("fields.type"),
    align: "left",
    field: (row) => row.type.label,
    sortable: true,
    style: "width: 80px;"
  },
  {
    name: "isRequired",
    required: true,
    label: t("fields.isRequired"),
    align: "center",
    field: (row) => (row.isRequired ? t("required") : t("notRequired")),
    style: "width: 150px;"
  },
  {
    name: "cname",
    label: t("fields.cname"),
    align: "left",
    field: (row) =>
      typeof row.cname === "object" ? row.cname.cname : row.cname
  },
  {
    name: "ename",
    label: t("fields.ename"),
    align: "left",
    field: "ename"
  },
  {
    name: "description",
    label: t("vcSchema.table.description"),
    align: "left",
    field: (row) => {
      if (row.type.value === "custom") {
        return row.regularExpression?.description;
      }
      return row.cname?.regularExpression?.description || "";
    }
  }
];

const desktopStyle = {
  cname: "width: 420px; white-space: normal; word-break: break-word",
  ename: "width: 420px; white-space: normal; word-break: break-word",
  description: "width: 420px; white-space: normal; word-break: break-word"
};

const isMobileColumns = computed(() => {
  if (isMobile.value) {
    return baseColumns;
  }

  return baseColumns.map((col) => ({
    ...col,
    style: desktopStyle[col.name] || col.style
  }));
});

const onDialogShow = () => {
  getFieldList("BASIC", true, 2000);
  getFieldList("NORMAL", true, 2000);
  regularExpression();

  if (id.value) {
    getVCCover(id.value).then(() => {
      imageTemp.value = vcCoverInfo.value.cardCover;
      imageUrl.value = imageTemp.value;
    });
  }
};

const onDialogHide = () => {
  resetVCcover();
  resetVCSchemaDetail();
};

const getRegularExpressionName = (id) => {
  const regex = regularExpressionsList.value.find((item) => item.id === id);
  if (!regex) return "";

  return regex.description;
};

const picName = ref(null);
const imageUrl = ref(null);

const fileLabel = computed(() => {
  return imageTemp.value ? t("vcSchema.uploaded") : t("vcSchema.notes");
});

const openFileDialog = () => {
  if (fileInput.value) {
    fileInput.value.pickFiles();
  }
};

const clearImage = () => {
  imageUrl.value = null;
  imageTemp.value = null;
};

const onFilesAdded = (file) => {
  imageTemp.value = null;
  if (!file) {
    imageUrl.value = null;
    return;
  }

  const fileToProcess = Array.isArray(file) ? file[0] : file;

  if (!fileToProcess) {
    console.log("No file provided");
    return;
  }

  // Check file type
  const validTypes = ["image/jpeg", "image/png"];
  if (!validTypes.includes(fileToProcess.type)) {
    $n.error(t("vcSchema.valid.pic"));
    picName.value = null;
    imageUrl.value = null;
    imageTemp.value = null;
    return;
  }

  // Check file size
  const maxSizeInKB = 500; // 最大大小500KB
  const minSizeInKB = 40; // 最小大小40KB
  const fileSizeInKB = Math.floor(fileToProcess.size / 1024); // 捨棄小數
  const errorMessages = [];
  const img = new Image();

  if (fileSizeInKB < minSizeInKB || fileSizeInKB > maxSizeInKB) {
    errorMessages.push(t("vcSchema.valid.size"));
  }

  if (errorMessages.length > 0) {
    $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
    picName.value = null;
    imageUrl.value = null;
    imageTemp.value = null;
    return;
  }

  img.onload = () => {
    const { width, height } = img;
    const aspectRatio = width / height;
    const minAspectRatio = 1.59;
    const maxAspectRatio = 1.61;

    if (width > 2048) {
      errorMessages.push(t("vcSchema.valid.lengthLessThan"));
    }

    if (aspectRatio < minAspectRatio || aspectRatio > maxAspectRatio) {
      errorMessages.push(t("vcSchema.valid.limit"));
    }

    if (errorMessages.length > 0) {
      $n.error(errorMessages.join("，") + "。"); // 顯示所有錯誤訊息
      picName.value = null;
      imageUrl.value = null;
      imageTemp.value = null;
      return;
    }

    // 建立 canvas (用原圖大小來繪製，補滿底色即可)
    const targetWidth = 1600;
    const targetHeight = 1000;
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
    canvas.width = targetWidth;
    canvas.height = targetHeight;

    // 填滿白底
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, targetWidth, targetHeight);

    // 計算等比例縮放
    const scale = Math.min(targetWidth / img.width, targetHeight / img.height);
    const newWidth = img.width * scale;
    const newHeight = img.height * scale;

    // 居中位置
    const offsetX = (targetWidth - newWidth) / 2;
    const offsetY = (targetHeight - newHeight) / 2;

    // 貼上原圖
    ctx.drawImage(img, offsetX, offsetY, newWidth, newHeight);

    // 輸出成 base64
    imageUrl.value = canvas.toDataURL("image/jpeg", 0.9);
  };

  const reader = new FileReader();
  reader.onload = (e) => {
    img.src = e.target.result;
  };
  reader.readAsDataURL(fileToProcess);
};

const getRegexPopupHeight = () => {
  const length = regularExpressionsList.value.length;
  if (length >= 6) {
    return "144px";
  }
  return `${length * 48}px`;
};

onMounted(async () => {
  await getIALTypeList();

  if (isEditMode.value) {
    setTempVcData();
    fillRowsFromDetail(props.schemaDetail);
  }
});

const isEditMode = computed(() => {
  return props.id !== undefined && props.id !== null;
});

const expandIcon = () => {
  if (showMore.value) return "expand_less";
  else return "expand_more";
};

const setTempVcData = () => {
  vcNumber.value = props.serialNo;
  vcName.value = props.name;
  isVerify.value = props.isVerifyTemp;
  validityPeriod.value = props.lengthExpire;
  periodUnit.value = matchSelectOption(periodUnitOptions, props.unitTypeExpire);
  ial.value = matchSelectOption(ialTypeIdOptions.value, props.ialTemp);
  needQRCode.value = props.isNeedQRCodeTemp; //matchSelectOption(typeOptions.value, props.type);
  formData.type = matchSelectOption(typeOptions, props.typeTemp);
  formData.issuerServiceUrl = props.issuerServiceUrlTemp;
};

/**
 * 根據給定的值，在選項中找到對應的 option 物件
 */
const matchSelectOption = (options, value, optionValueKey = "value") => {
  return options.find((opt) => opt[optionValueKey] === value) || null;
};
</script>

<style scoped>
.pt-10 {
  padding-top: 10px;
}

.cus-title-width {
  min-width: 180px;
}

.word-break {
  word-break: break-all;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

.title-width {
  min-width: 100px;
  max-width: 100px;
  text-align: justify;
}

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
