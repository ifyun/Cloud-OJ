<template>
  <n-space vertical>
    <n-data-table
      :columns="columns"
      :data="solutions.data"
      :loading="pagination.loading" />
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
import { useRouter } from "vue-router"
import {
  DataTableColumns,
  NSpace,
  NDataTable,
  NButton,
  NTooltip,
  NIcon,
  NTag,
  NText
} from "naive-ui"
import { CircleRound, CheckCircleFilled, ErrorRound } from "@vicons/material"
import { UserApi } from "@/api/request"
import { ErrorMsg, JudgeResult, PagedData } from "@/api/type"
import { LanguageNames, LanguageColors, ResultTypes } from "@/type"
import moment from "moment"

const store = useStore()
const router = useRouter()

withDefaults(defineProps<{ problemId: string | null }>(), {
  problemId: null
})

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const userInfo = computed(() => store.state.userInfo)

const error = ref<ErrorMsg | null>(null)
const solutions = ref<PagedData<JudgeResult>>({
  data: [],
  count: 0
})

const columns: DataTableColumns<JudgeResult> = [
  {
    title: "#",
    key: "#",
    width: 50,
    align: "right",
    render: (row, rowIndex: number) => (
      <span>
        {(pagination.value.page - 1) * pagination.value.pageSize + rowIndex + 1}
      </span>
    )
  },
  {
    title: "结果",
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
    title: "ID",
    key: "problemId",
    width: 90,
    align: "center"
  },
  {
    title: "题目",
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
    render: (row) => (
      <div style="display: flex; align-items: center">
        <NIcon color={LanguageColors[row.language!]}>
          <CircleRound />
        </NIcon>
        <NText depth="1" style="margin-left: 6px">
          {LanguageNames[row.language!]}
        </NText>
      </div>
    )
  },
  {
    title: "得分",
    key: "score",
    align: "right"
  },
  {
    title: "提交时间",
    key: "submitTime",
    align: "right",
    render: (row) => (
      <NTooltip trigger="hover" placement="left">
        {{
          trigger: () => (
            <NButton text={true}>
              {moment(row.submitTime).format("YYYY-MM-DD")}
            </NButton>
          ),
          default: () => (
            <span>{moment(row.submitTime).format("HH:mm:ss")}</span>
          )
        }}
      </NTooltip>
    )
  }
]

onBeforeMount(() => {
  querySolutions()
})

function querySolutions() {
  pagination.value.loading = true
  const { page, pageSize } = pagination.value
  UserApi.getSolutions(page, pageSize, userInfo.value)
    .then((data) => (solutions.value = data))
    .catch((err) => (error.value = err))
    .finally(() => (pagination.value.loading = false))
}
</script>
