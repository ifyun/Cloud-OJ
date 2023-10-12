<template>
  <n-space vertical>
    <n-space align="center">
      <n-button
        strong
        secondary
        size="small"
        :loading="loading"
        @click="querySolutions">
        <template #icon>
          <n-icon :component="HistoryRound" />
        </template>
        最近 15 次提交 (点击可刷新)
      </n-button>
    </n-space>
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
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, Page } from "@/api/type"
import { LanguageColors, LanguageNames, ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import {
  CheckCircleFilled,
  CircleRound,
  ErrorRound,
  HistoryRound,
  TimelapseRound
} from "@vicons/material"
import dayjs from "dayjs"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NPopover,
  NSpace,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { Component, onBeforeMount, ref } from "vue"

const props = defineProps<{ problemId: string }>()

const message = useMessage()

const loading = ref<boolean>(true)
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
      let icon: Component

      if (row.state != 0) {
        icon = TimelapseRound
      } else if (row.result === 0) {
        icon = CheckCircleFilled
      } else {
        icon = ErrorRound
      }

      const { type, text } =
        row.state === 0
          ? { type: ResultTypes[row.result!], text: row.resultText }
          : { type: "info", text: row.stateText }

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
        <NText depth="1" style="margin-left: 4px">
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
      <NPopover trigger="click" placement="left">
        {{
          trigger: () => <NButton text={true}>{date(row.submitTime!)}</NButton>,
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
  querySolutions()
})

function querySolutions() {
  loading.value = true
  UserApi.getSolutions(1, 15, 1, props.problemId)
    .then((data) => {
      solutions.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      loading.value = false
    })
}

function date(time: number) {
  const now = dayjs()
  const t = dayjs(time)

  if (t.isSame(now, "day")) {
    return "今天"
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 D 日")
  }
}
</script>
