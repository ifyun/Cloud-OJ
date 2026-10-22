<template>
  <div class="admin-wrap">
    <source-code-view
      v-if="showSource"
      v-model:show="showSource"
      :show-user="true"
      :data="solution" />
    <n-flex v-else vertical size="large">
      <!-- 搜索过滤 -->
      <n-flex align="center">
        <n-input-group>
          <n-input-group-label>题目 ID:</n-input-group-label>
          <n-input-number
            v-model:value="filter.pid"
            :show-button="false"
            :style="{ width: '33%' }" />
          <n-input-group-label>用户名:</n-input-group-label>
          <n-input v-model:value="filter.username" :style="{ width: '33%' }" />
          <n-input-group-label>日期:</n-input-group-label>
          <n-date-picker
            v-model:value="filter.date"
            clearable
            type="date"
            :style="{ width: '33%' }" />
          <n-button type="primary" @click="search">
            查找记录
            <template #icon>
              <n-icon>
                <search-round />
              </n-icon>
            </template>
          </n-button>
        </n-input-group>
      </n-flex>
      <!-- 数据 -->
      <n-data-table
        :columns="columns"
        :data="solutions.data"
        :loading="loading" />
      <n-pagination
        v-model:page="pagination.page"
        :item-count="solutions.total"
        :page-size="pagination.pageSize"
        @update:page="pageChange">
        <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-flex>
  </div>
</template>

<script setup lang="tsx">
import { SolutionApi } from "@/api/request"
import {
  ErrorMessage,
  JudgeResult,
  type Page,
  type SolutionFilter
} from "@/api/type"
import { LanguageTag, ResultTag, SourceCodeView } from "@/components"
import { useStore } from "@/store"
import { SearchRound } from "@vicons/material"
import dayjs from "dayjs"
import "dayjs/locale/zh-cn"
import relativeTime from "dayjs/plugin/relativeTime"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NDatePicker,
  NFlex,
  NIcon,
  NInput,
  NInputGroup,
  NInputGroupLabel,
  NInputNumber,
  NPagination,
  NPopover,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { nextTick, onActivated, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

defineOptions({
  name: "SolutionAdmin"
})

dayjs.extend(relativeTime)
dayjs.locale("zh-cn")

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const loading = ref<boolean>(true)
const showSource = ref<boolean>(false)
const filter = ref<SolutionFilter>({})

const solution = ref<JudgeResult>(new JudgeResult())
const solutions = ref<Page<JudgeResult>>({
  data: [],
  total: 0
})

const pagination = ref({
  page: 1,
  pageSize: 15
})

const columns: DataTableColumns<JudgeResult> = [
  {
    title: "#",
    key: "#",
    width: 50,
    align: "right",
    render: (_row, rowIndex: number) => (
      <NText>
        {(pagination.value.page - 1) * pagination.value.pageSize + rowIndex + 1}
      </NText>
    )
  },
  {
    title: "状态",
    key: "result",
    width: 100,
    align: "center",
    render: (row) => (
      <ResultTag
        key={row.result}
        class="resultTag"
        data={row}
        // @ts-ignore
        onClick={() => querySolution(row.solutionId)}
      />
    )
  },
  {
    title: "Solution ID",
    key: "solutionId",
    align: "right",
    width: 100,
    render: (row) => (
      <NTag
        round
        checkable
        size="small"
        style="cursor: pointer"
        // @ts-ignore
        onClick={() => querySolution(row.solutionId)}>
        {row.solutionId}
      </NTag>
    )
  },
  {
    title: "题目",
    key: "title",
    render: (row) => (
      <RouterLink to={{ name: "submission", params: { pid: row.problemId } }}>
        <NButton text strong>
          {row.problemId}.{row.title}
        </NButton>
      </RouterLink>
    )
  },
  {
    title: "语言",
    key: "language",
    align: "left",
    render: (row) => <LanguageTag language={row.language} />
  },
  {
    title: "得分",
    key: "score",
    align: "right",
    render: (row) => {
      const type = row.score === 0 ? "error" : ""
      return (
        <NText strong type={type}>
          {row.score}
        </NText>
      )
    }
  },
  {
    title: "提交时间",
    key: "submitTime",
    width: "140",
    align: "right",
    render: (row) => (
      <NPopover trigger="click" placement="left">
        {{
          trigger: () => (
            <NButton text>{dayjs(row.submitTime!).fromNow()}</NButton>
          ),
          default: () => (
            <NText italic={true}>
              {dayjs(row.submitTime!).format("YYYY-MM-DD HH:mm:ss")}
            </NText>
          )
        }}
      </NPopover>
    )
  }
]

onActivated(() => {
  const { page = 1 } = route.query
  pagination.value.page = Number(page)

  filter.value = JSON.parse(sessionStorage.getItem("query") ?? "{}")
  querySolutions()
})

function querySolutions() {
  loading.value = true
  const { page, pageSize } = pagination.value

  SolutionApi.getByFilter(page, pageSize, filter.value)
    .then((data) => {
      solutions.value = data
    })
    .catch((err) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function querySolution(sid: string) {
  SolutionApi.getById(sid)
    .then((data) => {
      solution.value = data
      nextTick(() => (showSource.value = true))
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}

function search() {
  sessionStorage.setItem("query", JSON.stringify(filter.value))
  pagination.value.page = 1
  pageChange(1)
}

function pageChange(page: number) {
  router.replace({
    query: {
      page
    }
  })

  querySolutions()
}
</script>

<style scoped>
:deep(.resultTag) {
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }
}
</style>
