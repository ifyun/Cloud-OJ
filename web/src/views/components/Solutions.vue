<template>
  <n-space vertical>
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
      single-column
      :columns="columns"
      :data="solutions.data"
      :loading="pagination.loading" />
    <n-pagination
      v-model:page="pagination.page"
      simple
      :page-size="pagination.pageSize"
      :item-count="solutions.count"
      @update:page="pageChange">
      <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
    </n-pagination>
  </n-space>
</template>

<script lang="tsx">
export default {
  name: "SolutionList"
}
</script>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NSpace,
  NDataTable,
  NButton,
  NTooltip,
  NTag,
  NText
} from "naive-ui"
import {
  CircleRound,
  CheckCircleFilled,
  ErrorRound,
  SearchRound
} from "@vicons/material"
import moment from "moment-timezone"
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, Page } from "@/api/type"
import { LanguageNames, LanguageColors, ResultTypes } from "@/type"
import { timeUsage, ramUsage } from "@/utils"

const store = useStore()
const route = useRoute()
const router = useRouter()

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const userInfo = computed(() => store.state.userInfo)

const error = ref<ErrorMessage | null>(null)
const solutions = ref<Page<JudgeResult>>({
  data: [],
  count: 0
})

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
    render: (row, rowIndex: number) => (
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
      const { type, text } = ResultTypes[row.result!]
      let icon: any
      if (row.result! === 0) {
        icon = CheckCircleFilled
      } else {
        icon = ErrorRound
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
            params: { pid: row.problemId }
          })
        }>
        {row.title}
      </NButton>
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
    render: (row) => <NText type="success">{timeUsage(row.time!)}</NText>
  },
  {
    title: "内存占用",
    key: "memory",
    align: "right",
    render: (row) => <NText>{ramUsage(row.memory!)}</NText>
  },
  {
    title: "分数",
    key: "score",
    align: "right"
  },
  {
    title: "提交时间",
    key: "submitTime",
    width: "140",
    align: "right",
    render: (row) => (
      <NTooltip trigger="click" placement="left">
        {{
          trigger: () => (
            <NButton text={true}>
              {moment.unix(row.submitTime! / 1000).format("YYYY/MM/DD")}
            </NButton>
          ),
          default: () => (
            <NText italic={true} style="color: #ffffff">
              {moment.unix(row.submitTime! / 1000).format("HH:mm:ss")}
            </NText>
          )
        }}
      </NTooltip>
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
  pagination.value.loading = true
  const { page, pageSize } = pagination.value

  UserApi.getSolutions(
    page,
    pageSize,
    userInfo.value,
    filter.value,
    filterValue.value
  )
    .then((data) => (solutions.value = data))
    .catch((err) => (error.value = err))
    .finally(() => (pagination.value.loading = false))
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
  if (value == 0) {
    filter.value = 0
    filterValue.value = ""
    pageChange(1)
  }
}

function search() {
  pageChange(1)
}
</script>
