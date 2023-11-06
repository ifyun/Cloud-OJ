<template>
  <div>
    <source-code-view
      v-if="showSource"
      v-model:show="showSource"
      :data="solution!" />
    <n-space v-else vertical>
      <n-input-group style="width: 520px">
        <n-select
          v-model:value="filter"
          :options="filterOptions"
          style="width: 180px"
          @update:value="filterChange" />
        <n-input v-model:value="filterValue" :disabled="filter == 0" />
        <n-button
          :disabled="filter == 0"
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
        :item-count="solutions.count"
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
import { ErrorMessage, JudgeResult, Page } from "@/api/type"
import { SourceCodeView } from "@/components"
import { useStore } from "@/store"
import { LanguageColors, LanguageNames, ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import {
  CheckCircleFilled,
  CircleRound,
  ErrorRound,
  SearchRound,
  TimelapseRound
} from "@vicons/material"
import dayjs from "dayjs"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NPopover,
  NSelect,
  NSpace,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { Component, nextTick, onBeforeMount, ref } from "vue"
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
  count: 0
})

const solution = ref<JudgeResult | null>(null)
const filter = ref<number>(0)
const filterValue = ref<string>("")
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
    render: (row) => {
      let icon: Component

      if (row.state != 0) {
        icon = TimelapseRound
      } else if (row.result! === 0) {
        icon = CheckCircleFilled
      } else {
        icon = ErrorRound
      }

      const { type, text } =
        row.state === 0
          ? { type: ResultTypes[row.result!], text: row.resultText }
          : { type: "info", text: row.stateText }

      return (
        <NTag
          class="resultTag"
          size="small"
          bordered={false}
          type={type as any}
          // @ts-ignore
          onClick={() => querySolution(row.solutionId)}>
          {{
            icon: () => <NIcon component={icon} />,
            default: () => <span>{text}</span>
          }}
        </NTag>
      )
    }
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
    render: (row) => (
      <div style="display: flex; align-items: center">
        <NIcon color={LanguageColors[row.language!]}>
          <CircleRound />
        </NIcon>
        <NText strong depth="1" style="margin-left: 6px">
          {LanguageNames[row.language!]}
        </NText>
      </div>
    )
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
    align: "right"
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

onBeforeMount(() => {
  if (route.query.filter) {
    filter.value = Number(route.query.filter)
    filterValue.value = route.query.filterValue as string
  }

  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  querySolutions()
})

function querySolutions() {
  loading.value = true
  const { page, pageSize } = pagination.value

  UserApi.getSolutions(page, pageSize, filter.value, filterValue.value)
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

function querySolution(sid: number) {
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
  router.push({
    query: {
      tab: "solutions",
      page,
      filter: filter.value,
      filterValue: filterValue.value
    }
  })

  querySolutions()
}

function filterChange(value: number) {
  // 关闭过滤条件
  if (value == 0) {
    filter.value = 0
    filterValue.value = ""
    pagination.value.page = 1
    pageChange(1)
  }
}

function search() {
  pagination.value.page = 1
  pageChange(1)
}

function date(time: number) {
  const now = dayjs()
  const t = dayjs(time)

  if (t.isSame(now, "day")) {
    return "今天"
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 D")
  }
}
</script>

<style>
.resultTag {
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }
}
</style>
