<template>
  <div>
    <n-h3 strong style="margin-bottom: 16px">最近记录</n-h3>
    <n-empty v-if="solutions.total === 0" description="没有记录" />
    <n-timeline style="padding: 0 6px">
      <n-timeline-item
        v-for="s in solutions.data"
        :key="s.solutionId"
        :type="ResultTypes[s.result!]"
        :title="s.resultText!"
        :time="time(s)"
        line-type="dashed">
        <router-link :to="{ name: 'submission', params: { pid: s.problemId } }">
          <n-button text>
            {{ content(s) }}
          </n-button>
        </router-link>
      </n-timeline-item>
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { JudgeResult, type Page } from "@/api/type"
import { LanguageNames, ResultTypes } from "@/type"
import dayjs from "dayjs"
import { NButton, NEmpty, NH3, NTimeline, NTimelineItem } from "naive-ui"
import { onBeforeMount, ref } from "vue"
import { RouterLink } from "vue-router"

const timeFmt = "YYYY/MM/DD HH:mm:ss"

const solutions = ref<Page<JudgeResult>>({ data: [], total: 0 })

onBeforeMount(() => {
  UserApi.getSolutions(1, 5).then((data) => {
    solutions.value = data
  })
})

function content(s: JudgeResult) {
  return `[${LanguageNames[s.language!]}] ${s.problemId}.${s.title}`
}

function time(s: JudgeResult) {
  return dayjs(s.submitTime!).format(timeFmt)
}
</script>
