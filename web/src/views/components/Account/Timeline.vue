<template>
  <div>
    <n-h3 strong style="margin-bottom: 16px">最近记录</n-h3>
    <n-empty v-if="solutions.count === 0" description="没有记录" />
    <n-timeline style="padding: 0 6px">
      <n-timeline-item
        v-for="item in solutions.data"
        :key="item.solutionId"
        :type="ResultTypes[item.result!]"
        :title="item.resultText!"
        :content="content(item)"
        :time="time(item)"
        line-type="dashed" />
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { JudgeResult, Page } from "@/api/type"
import { useStore } from "@/store"
import { LanguageNames, ResultTypes } from "@/type"
import dayjs from "dayjs"
import { NEmpty, NH3, NTimeline, NTimelineItem } from "naive-ui"
import { onBeforeMount, ref } from "vue"

const timeFmt = "YYYY/MM/DD HH:mm:ss"

const store = useStore()
const solutions = ref<Page<JudgeResult>>({ data: [], count: 0 })

onBeforeMount(() => {
  UserApi.getSolutions(1, 5, store.user.userInfo!).then((data) => {
    solutions.value = data
  })
})

function content(item: JudgeResult) {
  return `[${LanguageNames[item.language!]}] ${item.problemId}.${item.title}`
}

function time(item: JudgeResult) {
  return dayjs(item.submitTime!).format(timeFmt)
}
</script>
