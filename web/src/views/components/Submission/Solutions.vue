<template>
  <n-space vertical>
    <n-data-table
      :columns="columns"
      :data="solutions.data"
      :loading="loading" />
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
import moment from "moment-timezone"
import { CircleRound, CheckCircleFilled, ErrorRound } from "@vicons/material"
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, Page } from "@/api/type"
import { LanguageNames, LanguageColors, ResultTypes } from "@/type"
import { timeUsage, ramUsage } from "@/utils"

const store = useStore()

const props = defineProps<{ problemId: string }>()

const userInfo = computed(() => store.state.userInfo)
const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)
const solutions = ref<Page<JudgeResult>>({
  data: [],
  count: 0
})

const columns: DataTableColumns<JudgeResult> = [
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
    title: "语言",
    key: "language",
    align: "left",
    render: (row) => (
      <div style="display: flex; align-items: center">
        <NIcon color={LanguageColors[row.language!]}>
          <CircleRound />
        </NIcon>
        <NText strong depth="1" style="margin-left: 4px">
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
    title: "提交时间",
    key: "submitTime",
    width: "120",
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
  querySolutions()
})

function querySolutions() {
  loading.value = true
  UserApi.getSolutions(1, 16, userInfo.value, 1, props.problemId)
    .then((data) => (solutions.value = data))
    .catch((err) => (error.value = err))
    .finally(() => (loading.value = false))
}
</script>
