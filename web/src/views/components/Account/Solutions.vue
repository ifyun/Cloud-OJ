<template>
  <div>
    <source-code-view
      v-if="showSource"
      v-model:show="showSource"
      :show-user="false"
      :data="solution" />
    <n-space v-else vertical>
      <n-input-group style="width: 520px">
        <n-select
          v-model:value="filter.type"
          @update:value="filterChange"
          :options="filterOptions"
          style="width: 180px" />
        <n-input v-model:value="filter.keyword" :disabled="filter.type === 0" />
        <n-button
          :disabled="filter.type === 0"
          type="primary"
          secondary
          @click="search">
          搜索记录
          <template #icon>
            <n-icon>
              <search-round />
            </n-icon>
          </template>
        </n-button>
      </n-input-group>
      <n-data-table
        :single-column="false"
        :columns="columns"
        :data="solutions.data"
        :loading="loading" />
      <n-pagination
        v-model:page="pagination.page"
        simple
        :page-size="pagination.pageSize"
        :item-count="solutions.total"
        @update:page="pageChange">
        <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-space>
  </div>
</template>

<script lang="tsx">
export default {
  name: "SolutionList"
}
</script>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, type Page } from "@/api/type"
import { LanguageTag, ResultTag, SourceCodeView } from "@/components"
import { useStore } from "@/store"
import { ramUsage, timeUsage } from "@/utils"
import { SearchRound } from "@vicons/material"
import dayjs from "dayjs"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NPopover,
  NSelect,
  NSpace,
  NText,
  useMessage
} from "naive-ui"
import { nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref<boolean>(true)
const showSource = ref<boolean>(false)
const pagination = ref({
  page: 1,
  pageSize: 15
})

const solutions = ref<Page<JudgeResult>>({
  data: [],
  total: 0
})

const solution = ref<JudgeResult>(new JudgeResult())
const filter = ref<{
  type: number
  keyword: string
}>({ type: 0, keyword: "" })

const filterOptions = [
  { label: "关闭过滤", value: 0 },
  { label: "题目 ID", value: 1 },
  { label: "题目名称", value: 2 }
]

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
        class="resultTag"
        data={row}
        // @ts-ignore
        onClick={() => querySolution(row.solutionId)}
      />
    )
  },
  {
    title: "题名",
    key: "title",
    render: (row) => (
      <RouterLink to={{ name: "submission", params: { pid: row.problemId } }}>
        <NButton text>{row.title}</NButton>
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
    title: "CPU 时间",
    key: "time",
    align: "right",
    render: (row) => <NText type="success">{timeUsage(row.time)}</NText>
  },
  {
    title: "内存占用",
    key: "memory",
    align: "right",
    render: (row) => <NText>{ramUsage(row.memory)}</NText>
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
          trigger: () => <NButton text>{date(row.submitTime!)}</NButton>,
          default: () => (
            <NText italic={true}>
              {dayjs(row.submitTime!).format("H:mm:ss")}
            </NText>
          )
        }}
      </NPopover>
    )
  }
]

onMounted(() => {
  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  filter.value = JSON.parse(sessionStorage.getItem("query") ?? `{"type": 0}`)
  querySolutions()
})

function querySolutions() {
  loading.value = true
  const { page, pageSize } = pagination.value

  UserApi.getSolutions(page, pageSize, filter.value.type, filter.value.keyword)
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
  UserApi.getSolution(sid)
    .then((data) => {
      solution.value = data
      nextTick(() => (showSource.value = true))
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}

function pageChange(page: number) {
  router.replace({
    query: {
      tab: "solutions",
      page
    }
  })

  querySolutions()
}

function filterChange(value: number) {
  // 关闭过滤条件
  if (value == 0) {
    filter.value.keyword = ""
    pagination.value.page = 1
    pageChange(1)
  }
}

function search() {
  sessionStorage.setItem("query", JSON.stringify(filter.value))
  pagination.value.page = 1
  pageChange(1)
}

function date(time: number) {
  const now = dayjs()
  const t = dayjs(time)

  if (t.isSame(now, "day")) {
    return "今天"
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 DD")
  } else {
    return t.format("YYYY 年 M 月 DD")
  }
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
