<template>
  <div class="contest-editor">
    <n-card :bordered="false">
      <n-page-header
        class="page-header"
        :subtitle="subtitle"
        @back="router.back()">
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
              label-placement="left"
              :model="contest"
              :rules="rules"
              ref="contestForm">
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
                  label="时间范围"
                  path="timeRange">
                  <n-date-picker
                    type="datetimerange"
                    v-model:value="contest.timeRange"
                    clearable
                    format="yyyy/MM/dd - HH:mm:ss" />
                </n-form-item-grid-item>
              </n-grid>
              <n-form-item label="语言限制" path="languages">
                <n-transfer
                  source-title="不允许的语言"
                  target-title="允许的语言"
                  :options="languageOptions"
                  v-model:value="languages" />
              </n-form-item>
            </n-form>
          </n-spin>
        </n-tab-pane>
        <n-tab-pane v-if="!create" name="problems" tab="题目管理">
          <problem-manage :contest-id="contest.contestId" />
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref, watch } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  FormRules,
  NButton,
  NCard,
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
import { SaveOutlined as SaveIcon } from "@vicons/material"
import ProblemManage from "@/views/components/Admin/Contest/ProblemManage.vue"
import moment from "moment"
import { ContestApi } from "@/api/request"
import { Contest, ErrorMsg, UserInfo } from "@/api/type"
import { LanguageOption, LanguageOptions } from "@/type"
import { LanguageUtil } from "@/utils"
import Mutations from "@/store/mutations"

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const contest = ref<Contest>(new Contest())
const languageOptions = ref<Array<LanguageOption>>(LanguageOptions)
const languages = ref<Array<number>>([])
const loading = ref<boolean>(false)
const currentTab = ref<string>("base-info")
const create = ref<boolean>(false)

const rules: FormRules = {
  contestName: {
    required: true,
    trigger: ["input", "blur"],
    message: "请输入竞赛名称"
  },
  timeRange: {
    required: true,
    trigger: ["input", "blur"],
    validator(rule: any, value: Array<number>): Error | boolean {
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

const title = computed<string>(() => {
  return create.value ? "创建新竞赛" : "编辑竞赛"
})

const subtitle = computed(() => {
  if (typeof contest.value.contestId === "undefined") {
    return ""
  } else {
    return contest.value.contestName
  }
})

const userInfo = computed<UserInfo>(() => store.state.userInfo)

const showSaveButton = computed<boolean>(() => currentTab.value === "base-info")

const contestForm = ref<HTMLFormElement | null>(null)

watch(
  () => contest.value.timeRange,
  (value) => {
    const fmt = "yyyy-MM-DD HH:mm:ss"
    const [start, end] = value!
    contest.value.startAt = moment(start).format(fmt)
    contest.value.endAt = moment(end).format(fmt)
  }
)

watch(languages, (value) => {
  contest.value.languages = LanguageUtil.toNumber(value)
})

onBeforeMount(() => {
  const reg = /^\d+$/
  const id = route.params.id.toString()

  if (id === "new") {
    create.value = true
  } else if (reg.test(id)) {
    loading.value = true
    queryContest(Number(id))
  }

  setBreadcrumb()
})

function setBreadcrumb() {
  const breadcrumb = ["竞赛管理", title.value]
  store.commit(Mutations.SET_BREADCRUMB, breadcrumb)
}

function tabChange(tab: string) {
  currentTab.value = tab
}

function queryContest(contestId: number) {
  ContestApi.getById(contestId)
    .then((data) => {
      data.timeRange = []
      data.timeRange.push(Date.parse(data.startAt))
      data.timeRange.push(Date.parse(data.endAt))
      languages.value = LanguageUtil.toArray(data.languages)
      contest.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
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
  ContestApi.save(contest.value, userInfo.value, create.value)
    .then(() => {
      message.success(`${contest.value.contestName} 保存成功`)
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
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
  .n-transfer .n-transfer-list .n-transfer-list-body {
    height: 320px;
  }
}
</style>
