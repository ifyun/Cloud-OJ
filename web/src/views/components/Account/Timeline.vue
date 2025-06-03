<template>
  <div>
    <n-h3 strong style="margin-bottom: 16px">最近记录</n-h3>
    <n-empty v-if="solutions.total === 0" description="没有记录" />
    <n-timeline>
      <n-timeline-item
        v-for="s in solutions.data"
        :key="s.solutionId"
        :type="ResultTypes[s.result!]"
        :title="s.resultText"
        :time="time(s)"
        line-type="dashed">
        <n-flex vertical size="small">
          <language-tag :language="s.language" />
          <router-link
            :to="{ name: 'submission', params: { pid: s.problemId } }">
            <n-button text>
              {{ `${s.problemId}.${s.title}` }}
            </n-button>
          </router-link>
        </n-flex>
      </n-timeline-item>
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { JudgeResult, type Page } from "@/api/type"
import { ResultTypes } from "@/type"
import dayjs from "dayjs"
import { NButton, NEmpty, NFlex, NH3, NTimeline, NTimelineItem } from "naive-ui"
import { onMounted, ref } from "vue"
import { RouterLink } from "vue-router"
import { LanguageTag } from "@/components"

const timeFmt = "YYYY/MM/DD HH:mm:ss"

const solutions = ref<Page<JudgeResult>>({ data: [], total: 0 })

onMounted(() => {
  UserApi.getSolutions(1, 5).then((data) => {
    solutions.value = data
  })
})

function time(s: JudgeResult) {
  return dayjs(s.submitTime!).format(timeFmt)
}
</script>
