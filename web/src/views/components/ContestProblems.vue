<template>
  <div v-if="error == null" class="wrap">
    <n-space v-if="inputKey" vertical align="center">
      <n-form-item
        label="输入竞赛邀请码"
        show-require-mark
        style="width: 400px">
        <n-input v-model:value="inviteKey" placeholder="邀请码为 6 位数字" />
      </n-form-item>
      <n-button
        type="primary"
        round
        icon-placement="right"
        @click="inputInviteKey">
        加入竞赛
        <template #icon>
          <arrow-forward-round />
        </template>
      </n-button>
    </n-space>
    <n-space v-else vertical size="large">
      <n-space align="center">
        <n-skeleton v-if="contestState == null" round width="120" />
        <n-tag
          v-else
          round
          :bordered="false"
          :type="contestState.type"
          style="margin-top: 2px">
          <template #icon>
            <n-icon :component="contestState.icon" />
          </template>
          {{ contestState.state }}
        </n-tag>
        <n-skeleton v-if="contest == null" text width="250" />
        <n-text v-else strong>{{ contest.contestName }}</n-text>
      </n-space>
      <n-data-table
        single-column
        :columns="columns"
        :data="problems"
        :loading="loading" />
    </n-space>
  </div>
  <div v-else>
    <error-result :error="error" />
  </div>
</template>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { Contest, ErrorMessage, Problem } from "@/api/type"
import { ErrorResult } from "@/components"
import { ResultTypes } from "@/type"
import { LanguageUtil, setTitle, stateTag } from "@/utils"
import {
  ArrowForwardRound,
  CheckCircleFilled,
  ErrorRound
} from "@vicons/material"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NFormItem,
  NIcon,
  NInput,
  NSkeleton,
  NSpace,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { Component, computed, onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"
import { useStore } from "vuex"

const store = useStore()
const router = useRouter()
const message = useMessage()

const props = defineProps<{
  cid: string
}>()

const error = ref<ErrorMessage | null>(null)
const userInfo = computed(() => store.state.userInfo)

const loading = ref<boolean>(false)
const inputKey = ref<boolean>(false)
const inviteKey = ref<string>("")
const contest = ref<Contest | null>(null)
const languages = ref<Array<number>>([])
const problems = ref<Array<Problem>>([])

const columns: DataTableColumns<Problem> = [
  {
    title: "#",
    key: "#",
    align: "right",
    width: 50,
    render: (row, rowIndex: number) => <span>{rowIndex + 1}</span>
  },
  {
    title: "ID",
    key: "problemId",
    align: "right",
    width: 70
  },
  {
    title: "题名",
    key: "title",
    render: (row) => (
      <NButton
        text
        onClick={() =>
          router.push({
            name: "submission",
            params: { pid: row.problemId },
            query: { cid: contest.value!.contestId }
          })
        }>
        {row.title}
      </NButton>
    )
  },
  {
    title: "状态",
    key: "result",
    width: 100,
    align: "center",
    render: (row) => {
      let icon: Component
      if (typeof row.result !== "undefined") {
        if (row.result! === 0) {
          icon = CheckCircleFilled
        } else {
          icon = ErrorRound
        }

        const { type, text } = {
          type: ResultTypes[row.result!],
          text: row.resultText
        }

        return (
          <NTag size="small" bordered={false} type={type as any}>
            {{
              icon: () => <NIcon component={icon} />,
              default: () => <span>{text}</span>
            }}
          </NTag>
        )
      }
    }
  },
  {
    title: "分数",
    key: "score",
    align: "right"
  }
]

const contestState = computed(() => {
  return contest.value == null ? null : stateTag(contest.value)
})

onBeforeMount(() => {
  const reg = /^\d+$/
  if (reg.test(props.cid)) {
    queryContest(Number(props.cid))
  } else {
    error.value = {
      status: 404,
      error: "Not Found",
      message: "找不到竞赛"
    }

    loading.value = false
  }
})

function queryContest(cid: number) {
  loading.value = true
  ContestApi.getById(cid)
    .then((data) => {
      setTitle(data.contestName)
      contest.value = data
      if (data.languages < 511) {
        languages.value = LanguageUtil.toArray(data.languages)
      }
      queryProblems(cid)
    })
    .catch((err) => {
      error.value = err
      loading.value = false
    })
}

function queryProblems(cid: number) {
  loading.value = true
  ContestApi.getProblemsFromStarted(cid, userInfo.value)
    .then((data) => {
      problems.value = data
    })
    .catch((err: ErrorMessage) => {
      if (err.status === 402) {
        inputKey.value = true
      } else {
        error.value = err
      }
    })
    .finally(() => {
      loading.value = false
    })
}

function inputInviteKey() {
  const cid = contest.value!.contestId!

  ContestApi.joinContest(userInfo.value, cid, inviteKey.value)
    .then(() => {
      inputKey.value = false
      queryProblems(cid)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>
