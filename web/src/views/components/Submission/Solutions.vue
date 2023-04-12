<template>
  <n-space vertical>
    <n-data-table
      single-column
      size="small"
      :columns="columns"
      :data="solutions.data"
      :loading="pagination.loading" />
    <n-pagination
      v-model:page="pagination.page"
      simple
      size="small"
      :page-size="pagination.pageSize"
      :item-count="solutions.count"
      @update:page="pageChange">
      <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
    </n-pagination>
  </n-space>
</template>

<script lang="tsx">
export default {
  name: "SolutionSingle"
}
</script>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NPagination,
  NSpace,
  NDataTable,
  NButton,
  NTooltip,
  NIcon,
  NTag,
  NText
} from "naive-ui"
import moment from "moment-timezone"
import { CircleRound, CheckCircleFilled, ErrorRound } from "@vicons/material"
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, Page } from "@/api/type"
import { LanguageNames, LanguageColors, ResultTypes } from "@/type"
import { timeUsage, ramUsage } from "@/utils"

const store = useStore()
const route = useRoute()
const router = useRouter()

const props = defineProps<{ problemId: string }>()

const pagination = ref({
  page: 1,
  pageSize: 20,
  loading: true
})

const userInfo = computed(() => store.state.userInfo)

const error = ref<ErrorMessage | null>(null)
const solutions = ref<Page<JudgeResult>>({
  data: [],
  count: 0
})

const columns: DataTableColumns<JudgeResult> = [
  {
    title: "状态",
    key: "result",
    width: 120,
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
    title: "运行时间",
    key: "time",
    align: "right",
    render: (row) => (
      <NText type="primary" strong>
        {timeUsage(row.time!)}
      </NText>
    )
  },
  {
    title: "内存消耗",
    key: "memory",
    align: "right",
    render: (row) => (
      <NText type="info" strong>
        {ramUsage(row.memory!)}
      </NText>
    )
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
              {moment.unix(row.submitTime!).format("YYYY/MM/DD")}
            </NButton>
          ),
          default: () => (
            <NText italic={true} style="color: #ffffff">
              {moment.unix(row.submitTime!).format("HH:mm:ss")}
            </NText>
          )
        }}
      </NTooltip>
    )
  }
]

onBeforeMount(() => {
  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  querySolutions()
})

function querySolutions() {
  pagination.value.loading = true
  const { page, pageSize } = pagination.value
  UserApi.getSolutions(page, pageSize, userInfo.value, {
    problemId: Number(props.problemId)
  })
    .then((data) => (solutions.value = data))
    .catch((err) => (error.value = err))
    .finally(() => (pagination.value.loading = false))
}

function pageChange(page: number) {
  router.push({
    query: { tab: "solutions", page }
  })
}
</script>
