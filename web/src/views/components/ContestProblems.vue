<template>
  <div v-if="!loading" class="wrap">
    <!-- 输入邀请码部分 -->
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
    <!-- 题目部分 -->
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
</template>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { Contest, ErrorMessage, Problem } from "@/api/type"
import { useStore } from "@/store"
import { ResultTypes } from "@/type"
import { LanguageUtil, setTitle, stateTag } from "@/utils"
import {
  ArrowForwardRound,
  CheckCircleFilled,
  ErrorRound
} from "@vicons/material"
import {
  type DataTableColumns,
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
import { type Component, computed, onMounted, ref } from "vue"
import { useRouter } from "vue-router"

const store = useStore()
const router = useRouter()
const message = useMessage()

const props = defineProps<{
  cid: string
}>()

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
    align: "center",
    width: 100,
    render: (_, rowIndex: number) => (
      <NText italic>{String.fromCharCode(rowIndex + 65)}</NText>
    )
  },
  {
    title: "题目名称",
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
        icon = row.result! === 0 ? CheckCircleFilled : ErrorRound

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
    align: "right",
    render: (row) => <NText strong>{row.score}</NText>
  }
]

const contestState = computed(() => {
  return contest.value == null ? null : stateTag(contest.value)
})

onMounted(() => {
  const reg = /^\d+$/
  if (reg.test(props.cid)) {
    queryContest(Number(props.cid))
  } else {
    store.app.setError({
      status: 404,
      error: "Not Found",
      message: "找不到竞赛"
    })
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
      store.app.setError(err)
    })
}

function queryProblems(cid: number) {
  loading.value = true
  ContestApi.getProblemsFromStarted(cid)
    .then((data) => {
      problems.value = data
    })
    .catch((err: ErrorMessage) => {
      if (err.status === 402) {
        inputKey.value = true
      } else {
        store.app.setError(err)
      }
    })
    .finally(() => {
      loading.value = false
    })
}

function inputInviteKey() {
  const cid = contest.value!.contestId!

  ContestApi.joinContest(cid, inviteKey.value)
    .then(() => {
      inputKey.value = false
      queryProblems(cid)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>
