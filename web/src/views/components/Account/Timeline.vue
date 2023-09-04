<template>
  <div>
    <n-h3 strong style="margin-bottom: 16px">最近记录</n-h3>
    <n-empty v-if="solutions.count === 0" description="没有记录" />
    <n-timeline style="padding: 0 6px">
      <n-timeline-item
        v-for="item in solutions.data"
        :key="item.solutionId"
        :type="ResultTypes[item.result!].type as any"
        :title="ResultTypes[item.result!].text"
        :content="content(item)"
        :time="time(item)"
        line-type="dashed" />
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { NEmpty, NH3, NTimeline, NTimelineItem } from "naive-ui"
import { UserApi } from "@/api/request"
import { JudgeResult, Page } from "@/api/type"
import { LanguageNames, ResultTypes } from "@/type"
import moment from "moment-timezone"

const timeFmt = "yyyy/MM/DD HH:mm:ss"

const store = useStore()
const userInfo = computed(() => store.state.userInfo)
const solutions = ref<Page<JudgeResult>>({ data: [], count: 0 })

onBeforeMount(() => {
  UserApi.getSolutions(1, 5, userInfo.value).then((data) => {
    solutions.value = data
  })
})

function content(item: JudgeResult) {
  return `[${LanguageNames[item.language!]}] ${item.problemId}.${item.title}`
}

function time(item: JudgeResult) {
  return moment.unix(item.submitTime! / 1000).format(timeFmt)
}
</script>
