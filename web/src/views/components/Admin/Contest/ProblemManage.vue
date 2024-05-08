<template>
  <div class="contest-problem-tables">
    <n-space vertical size="large">
      <n-data-table
        :columns="columns1"
        :data="problems.data"
        :loading="pagination.loading" />
      <n-pagination
        v-model:page="pagination.page"
        simple
        :page-size="pagination.pageSize"
        :item-count="problems.total"
        @update:page="queryProblems">
        <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-space>
    <n-space vertical size="large">
      <n-data-table :columns="columns2" :data="contestProblems" />
      <n-button
        type="primary"
        size="small"
        :disabled="!orderChanged"
        @click="saveOrder">
        <template #icon>
          <n-icon :component="SaveRound" />
        </template>
        保存顺序
      </n-button>
    </n-space>
  </div>
</template>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { ErrorMessage, type Page, Problem } from "@/api/type"
import {
  ArrowDownwardRound,
  ArrowUpwardRound,
  SaveRound
} from "@vicons/material"
import {
  type DataTableColumns,
  NButton,
  NButtonGroup,
  NDataTable,
  NIcon,
  NPagination,
  NSpace,
  NText,
  useDialog,
  useMessage
} from "naive-ui"
import { ref, watch } from "vue"
import { useRouter } from "vue-router"

const props = defineProps<{ contestId?: number }>()

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const orderChanged = ref<boolean>(false)
const problems = ref<Page<Problem>>({
  data: [],
  total: 0
})

const contestProblems = ref<Array<Problem>>([])
const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const columns1: DataTableColumns<Problem> = [
  {
    title: "可用题目",
    key: "allProblems",
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
        key: "title",
        render: (row) => (
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
        key: "operation",
        align: "right",
        width: 100,
        render: (row) => (
          <NButton size="tiny" type="success" onClick={() => addToContest(row)}>
            添加
          </NButton>
        )
      }
    ]
  }
]

const columns2: DataTableColumns<Problem> = [
  {
    title: "已添加的题目",
    key: "contestProblems",
    align: "center",
    children: [
      {
        title: "#",
        key: "#",
        align: "center",
        width: 50,
        render: (_, index: number) => (
          <NText>{String.fromCharCode(index + 65)}</NText>
        )
      },
      {
        title: "ID",
        key: "problemId",
        align: "right",
        width: 70
      },
      {
        title: "题目名称",
        key: "title",
        render: (row) => (
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
        key: "operation",
        align: "center",
        width: 120,
        render: (row, index) => (
          <NButtonGroup size="tiny">
            <NButton onClick={() => moveUp(index)}>
              {{
                icon: () => <NIcon component={ArrowUpwardRound} />
              }}
            </NButton>
            <NButton onClick={() => moveDown(index)}>
              {{
                icon: () => <NIcon component={ArrowDownwardRound} />
              }}
            </NButton>
            <NButton
              size="tiny"
              type="warning"
              secondary
              onClick={() => handleRemove(row)}>
              移除
            </NButton>
          </NButtonGroup>
        )
      }
    ]
  }
]

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

function moveUp(index: number) {
  if (index != 0) {
    const data = contestProblems.value
    contestProblems.value[index] = data.splice(index - 1, 1, data[index])[0]
    orderChanged.value = true
  }
}

function moveDown(index: number) {
  if (index != 0) {
    const data = contestProblems.value
    contestProblems.value[index] = data.splice(index + 1, 1, data[index])[0]
    orderChanged.value = true
  }
}

/**
 * 获取可用题目
 */
function queryProblems() {
  pagination.value.loading = true
  const { page, pageSize } = pagination.value
  ContestApi.getProblemsNotInContest(props.contestId!, page, pageSize)
    .then((data) => {
      problems.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      pagination.value.loading = false
    })
}

/**
 * 获取当前竞赛的题目
 */
function queryContestProblems() {
  ContestApi.getProblems(props.contestId!)
    .then((data) => {
      contestProblems.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}

function saveOrder() {
  const orders = contestProblems.value.map((p) => p.problemId!)
  ContestApi.changeOrder(props.contestId!, orders)
    .then(() => {
      message.success("已保存当前排序")
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
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
  ContestApi.addProblem(props.contestId!, p.problemId!)
    .then(() => {
      message.success(`[${p.title}] 已添加`)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
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
  ContestApi.removeProblem(props.contestId!, p.problemId!)
    .then(() => {
      message.warning(`[${p.title}] 已移除`)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
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
