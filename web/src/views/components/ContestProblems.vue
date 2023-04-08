<template>
  <div v-if="error == null" class="wrap">
    <div>
      <n-space vertical size="large">
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
  </div>
  <div v-else>
    <error-result :error="error" />
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NSkeleton,
  NSpace,
  NTag,
  NText
} from "naive-ui"
import { ErrorResult } from "@/components"
import { ContestApi } from "@/api/request"
import { Contest, ErrorMessage, Problem } from "@/api/type"
import { LanguageUtil, setTitle, stateTag } from "@/utils"

const store = useStore()
const router = useRouter()

const props = defineProps<{
  cid: string
}>()

const error = ref<ErrorMessage | null>(null)
const userInfo = computed(() => store.state.userInfo)

const loading = ref<boolean>(false)
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
  ContestApi.getProblemsFromStarted(cid, userInfo.value)
    .then((data) => {
      problems.value = data
    })
    .catch((err) => {
      err.value = err
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
