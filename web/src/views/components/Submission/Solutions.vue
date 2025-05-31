<template>
  <n-flex vertical>
    <n-flex align="center">
      <n-button
        tertiary
        round
        size="small"
        :loading="loading"
        @click="querySolutions">
        <template #icon>
          <n-icon :component="RefreshRound" />
        </template>
        刷新
      </n-button>
      <n-text>最近 15 次提交：</n-text>
    </n-flex>
    <empty-data v-if="solutions.total === 0" />
    <n-timeline v-else style="width: 400px; margin-left: 12px">
      <n-timeline-item
        v-for="s in solutions.data"
        :key="s.solutionId"
        :type="ResultTypes[s.result!]"
        :title="s.resultText!"
        :time="date(s.submitTime!)"
        line-type="dashed">
        <n-flex align="center">
          <!-- Language -->
          <n-flex align="center" :size="4">
            <n-icon size="12" :color="LanguageColors[s.language!]">
              <circle-round />
            </n-icon>
            <n-text strong depth="2" style="width: 100px">
              {{ LanguageNames[s.language!] }}
            </n-text>
          </n-flex>
          <!-- CPU Time -->
          <n-flex align="center" :size="4">
            <n-icon depth="2" :component="TimerOutlined" />
            <n-text depth="2" style="width: 100px">
              {{ timeUsage(s.time!) }}
            </n-text>
          </n-flex>
          <!-- RAM -->
          <n-flex align="center" :size="4">
            <n-icon depth="3" :component="DataSaverOffRound" />
            <n-text depth="3">{{ ramUsage(s.memory!) }}</n-text>
          </n-flex>
        </n-flex>
      </n-timeline-item>
    </n-timeline>
  </n-flex>
</template>

<script lang="tsx">
export default {
  name: "SolutionSingle"
}
</script>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, JudgeResult, type Page } from "@/api/type"
import { LanguageColors, LanguageNames, ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import {
  CircleRound,
  DataSaverOffRound,
  RefreshRound,
  TimerOutlined
} from "@vicons/material"
import dayjs from "dayjs"
import {
  NButton,
  NFlex,
  NIcon,
  NText,
  NTimeline,
  NTimelineItem,
  useMessage
} from "naive-ui"
import { onBeforeMount, ref } from "vue"
import { EmptyData } from "@/components"

const props = defineProps<{ problemId: string }>()

const message = useMessage()

const loading = ref<boolean>(true)
const solutions = ref<Page<JudgeResult>>({
  data: [],
  total: 0
})

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
    return "今天 " + t.format("HH:mm:ss")
  } else if (t.isSame(now, "year")) {
    return t.format("M 月 D 日 HH:mm:ss")
  } else {
    return t.format("YYYY 年 M 月 D 日")
  }
}
</script>
