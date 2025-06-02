<template>
  <div class="contest-editor">
    <n-page-header class="page-header" @back="router.back()">
      <template #title>{{ title }}</template>
      <template #extra>
        <n-button
          v-if="showSaveButton"
          round
          secondary
          type="primary"
          size="small"
          @click="handleSave">
          <template #icon>
            <save-icon />
          </template>
          保存
        </n-button>
      </template>
    </n-page-header>
    <n-tabs type="line" default-value="base-info" @update:value="tabChange">
      <n-tab-pane name="base-info" tab="竞赛设置">
        <n-spin :show="loading">
          <n-form
            ref="contestForm"
            label-placement="top"
            :model="contest"
            :rules="rules">
            <n-grid :cols="2" x-gap="12">
              <n-form-item-grid-item
                :span="1"
                label="竞赛名称"
                path="contestName">
                <n-input
                  v-model:value="contest.contestName"
                  maxlength="24"
                  show-count
                  clearable />
              </n-form-item-grid-item>
              <n-form-item-grid-item
                :span="1"
                label="开始/结束时间"
                path="timeRange">
                <n-date-picker
                  v-model:value="contest.timeRange"
                  type="datetimerange"
                  clearable
                  format="yyyy 年 M 月 d 日 H:mm" />
              </n-form-item-grid-item>
            </n-grid>
            <n-form-item label="语言限制" path="languages">
              <n-transfer
                v-model:value="languages"
                :options="languageOptions" />
            </n-form-item>
          </n-form>
        </n-spin>
      </n-tab-pane>
      <n-tab-pane v-if="!create" name="problems" tab="题目管理">
        <problem-manage :contest-id="contest.contestId" />
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { Contest, ErrorMessage } from "@/api/type"
import { type LanguageOption, LanguageOptions } from "@/type"
import { LanguageUtil } from "@/utils"
import { SaveOutlined as SaveIcon } from "@vicons/material"
import dayjs from "dayjs"
import {
  type FormRules,
  NButton,
  NDatePicker,
  NForm,
  NFormItem,
  NFormItemGridItem,
  NGrid,
  NInput,
  NPageHeader,
  NSpin,
  NTabPane,
  NTabs,
  NTransfer,
  useMessage
} from "naive-ui"
import { computed, onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import ProblemManage from "./ProblemManage.vue"

const route = useRoute()
const router = useRouter()
const message = useMessage()

const contest = ref<Contest>(new Contest())
const languageOptions = ref<Array<LanguageOption>>(LanguageOptions)
const languages = ref<Array<number>>([])
const loading = ref<boolean>(false)
const currentTab = ref<string>("base-info")
const create = ref<boolean>(false)
const contestForm = ref<HTMLFormElement | null>(null)

const rules: FormRules = {
  contestName: {
    required: true,
    trigger: ["input", "blur"],
    message: "请输入竞赛名称"
  },
  timeRange: {
    required: true,
    trigger: ["input", "blur"],
    validator(_, value: Array<number>): Error | boolean {
      if (!value) {
        return new Error("请输入时间范围")
      }

      return true
    }
  },
  languages: {
    required: true,
    trigger: ["input"],
    validator: (): Error | boolean => {
      if (languages.value.length === 0) {
        return new Error("请选择语言")
      }
      return true
    }
  }
}

const title = computed(() => {
  if (create.value) {
    return route.meta._title as string
  }

  if (typeof contest.value.contestId === "undefined") {
    return ""
  } else {
    return contest.value.contestName
  }
})

const showSaveButton = computed<boolean>(() => currentTab.value === "base-info")

watch(
  () => contest.value.timeRange,
  (value) => {
    const [start, end] = value!
    // 时间选择器的 UNIX 时间是 13 位的
    // 传回后端需要 10 位
    // 将秒设为 0
    contest.value.startAt = dayjs(start).second(0).valueOf() / 1000
    contest.value.endAt = dayjs(end).second(0).valueOf() / 1000
  }
)

watch(languages, (value) => {
  contest.value.languages = LanguageUtil.toNumber(value)
})

onMounted(() => {
  const reg = /^\d+$/
  const id = route.params.id.toString()

  if (id === "new") {
    create.value = true
  } else if (reg.test(id)) {
    loading.value = true
    queryContest(Number(id))
  }
})

function tabChange(tab: string) {
  currentTab.value = tab
}

function queryContest(contestId: number) {
  ContestApi.getById(contestId)
    .then((data) => {
      data.timeRange = [data.startAt! * 1000, data.endAt! * 1000]
      languages.value = LanguageUtil.toArray(data.languages)
      contest.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      loading.value = false
    })
}

function handleSave() {
  contestForm.value?.validate((errors: any) => {
    if (!errors) {
      save()
    }
  })
}

function save() {
  ContestApi.save(contest.value, create.value)
    .then(() => {
      message.success(`${contest.value.contestName} 已保存`)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>

<style scoped lang="scss">
.contest-editor {
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex-direction: column;

  .page-header {
    margin-bottom: 12px;
  }
}
</style>

<style lang="scss">
.contest-editor {
  margin: 4px;
}
</style>
