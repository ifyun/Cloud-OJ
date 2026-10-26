<template>
  <n-flex vertical>
    <n-flex align="center">
      <n-h3 style="margin: 0">
        {{ `${problemId}.${title}` }}
      </n-h3>
      <n-button
        text
        type="primary"
        size="small"
        :loading="loading"
        @click="querySolutions">
        <template #icon>
          <n-icon :component="RefreshRound" />
        </template>
      </n-button>
      <n-text>最近 10 次提交：</n-text>
    </n-flex>
    <empty-data v-if="solutions.total === 0" />
    <n-timeline v-else style="width: 400px; margin-left: 12px">
      <n-timeline-item
        v-for="s in solutions.data"
        :key="s.solutionId"
        :type="ResultTypes[s.result!]"
        :title="s.resultText"
        :time="date(s.submitTime)"
        line-type="dashed">
        <n-flex align="center">
          <!-- Language -->
          <language-tag :language="s.language" />
          <!-- CPU Time -->
          <n-flex align="center" :size="4">
            <n-icon depth="2" :component="TimerOutlined" />
            <n-text depth="2" style="width: 100px">
              {{ timeUsage(s.time) }}
            </n-text>
          </n-flex>
          <!-- RAM -->
          <n-flex align="center" :size="4">
            <n-icon depth="3" :component="DataSaverOffRound" />
            <n-text depth="3">{{ ramUsage(s.memory) }}</n-text>
          </n-flex>
        </n-flex>
      </n-timeline-item>
    </n-timeline>
  </n-flex>
</template>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, type Page } from "@/api/type"
import { EmptyData, LanguageTag } from "@/components"
import { useActivated } from "@/composables"
import { ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import {
  DataSaverOffRound,
  RefreshRound,
  TimerOutlined
} from "@vicons/material"
import dayjs from "dayjs"
import {
  NButton,
  NFlex,
  NH3,
  NIcon,
  NText,
  NTimeline,
  NTimelineItem,
  useMessage
} from "naive-ui"
import { ref } from "vue"

defineOptions({
  name: "SolutionSingle"
})

const props = defineProps<{ problemId: number; title: string }>()
const message = useMessage()
const loading = ref<boolean>(true)
const solutions = ref<Page<JudgeResult>>({
  data: [],
  total: 0
})

useActivated(() => {
  querySolutions()
})

function querySolutions() {
  loading.value = true
  UserApi.getSolutions(1, 10, 1, props.problemId.toString())
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

function date(time?: number) {
  const now = dayjs()
  const t = dayjs(time)

  if (t.isSame(now, "day")) {
    return "今天 " + t.format("HH:mm:ss")
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 D 日 HH:mm:ss")
  } else {
    return t.format("YYYY 年 M 月 D 日")
  }
}
</script>
