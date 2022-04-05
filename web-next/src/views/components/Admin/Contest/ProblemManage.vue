<template>
  <div class="contest-problem-tables">
    <n-space vertical size="large">
      <n-data-table
        :columns="columns1"
        :data="problems.data"
        :loading="pagination1.loading" />
      <n-pagination
        v-model:page="pagination1.page"
        :page-size="pagination1.pageSize"
        :item-count="problems.count"
        @update:page="queryProblems">
        <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-space>
    <n-space vertical size="large">
      <n-data-table
        :columns="columns2"
        :data="contestProblems.data"
        :loading="pagination2.loading" />
      <n-pagination
        v-model:page="pagination2.page"
        :page-size="pagination2.pageSize"
        :item-count="contestProblems.count"
        @update:page="queryContestProblems" />
    </n-space>
  </div>
</template>

<script setup lang="tsx">
import { computed, ref, watch } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import {
  NButton,
  NDataTable,
  NPagination,
  NSpace,
  useDialog,
  useMessage
} from "naive-ui"
import { ErrorMsg, PagedData, Problem, UserInfo } from "@/api/type"
import { ContestApi } from "@/api/request"

const props = defineProps<{ contestId: number }>()

const store = useStore()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const problems = ref<PagedData<Problem>>({
  data: [],
  count: 0
})

const contestProblems = ref<PagedData<Problem>>({
  data: [],
  count: 0
})

const pagination1 = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const pagination2 = ref({
  page: 1,
  pageSize: 10,
  loading: true
})

const columns1 = [
  {
    title: "可用题目",
    align: "center",
    children: [
      {
        title: "ID",
        key: "problemId",
        align: "right",
        width: 70
      },
      {
        title: "题目名称",
        render: (row: Problem) => (
          <NButton text={true} onClick={() => toSubmission(row)}>
            {row.title}
          </NButton>
        )
      },
      {
        title: "分数",
        key: "score",
        align: "right"
      },
      {
        title: "操作",
        align: "right",
        width: 100,
        render: (row: Problem) => (
          <NButton size="tiny" type="success" onClick={() => addToContest(row)}>
            添加
          </NButton>
        )
      }
    ]
  }
]

const columns2 = [
  {
    title: "已添加的题目",
    align: "center",
    children: [
      {
        title: "ID",
        key: "problemId",
        align: "right",
        width: 70
      },
      {
        title: "题目名称",
        render: (row: Problem) => (
          <NButton text={true} onClick={() => toSubmission(row)}>
            {row.title}
          </NButton>
        )
      },
      {
        title: "分数",
        key: "score",
        align: "right"
      },
      {
        title: "操作",
        align: "right",
        width: 100,
        render: (row: Problem) => (
          <NButton size="tiny" type="warning" onClick={() => handleRemove(row)}>
            移除
          </NButton>
        )
      }
    ]
  }
]

const userInfo = computed<UserInfo>(() => {
  return store.state.userInfo
})

watch(
  () => props.contestId,
  (value) => {
    if (typeof value !== "undefined") {
      queryProblems()
      queryContestProblems()
    }
  },
  { immediate: true }
)

/**
 * 获取可用题目
 */
function queryProblems() {
  pagination1.value.loading = true
  const { page, pageSize } = pagination1.value
  ContestApi.getProblemsNotInContest(
    props.contestId,
    page,
    pageSize,
    userInfo.value
  )
    .then((data) => {
      problems.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      pagination1.value.loading = false
    })
}

/**
 * 获取当前竞赛的题目
 */
function queryContestProblems() {
  pagination2.value.loading = true
  const { page, pageSize } = pagination2.value
  ContestApi.getProblems(props.contestId!, page, pageSize, userInfo.value)
    .then((data) => {
      contestProblems.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      pagination2.value.loading = false
    })
}

function refresh() {
  queryContestProblems()
  queryProblems()
}

function toSubmission(p: Problem) {
  router.push({ name: "submission", query: { problemId: p.problemId } })
}

/**
 * 将题目添加到当前竞赛
 */
function addToContest(p: Problem) {
  ContestApi.addProblem(props.contestId, p.problemId!, userInfo.value)
    .then(() => {
      message.success(`[${p.title}] 已添加`)
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      refresh()
    })
}

function handleRemove(p: Problem) {
  dialog.warning({
    title: "警告",
    content: `是否移除 [${p.title}]？`,
    positiveText: "是",
    negativeText: "不要",
    onPositiveClick: () => remove(p)
  })
}

/**
 * 从竞赛中移除题目
 */
function remove(p: Problem) {
  ContestApi.removeProblem(props.contestId, p.problemId!, userInfo.value)
    .then(() => {
      message.warning(`[${p.title}] 已移除`)
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      refresh()
    })
}
</script>

<style scoped lang="scss">
.contest-problem-tables {
  display: flex;
  flex: 1;

  > * {
    flex: 1;
    margin-left: 12px;

    &:first-child {
      margin-left: 0;
    }
  }
}
</style>
