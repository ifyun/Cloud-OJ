<template>
  <div>
    <n-h3 strong style="margin-bottom: 16px">最近提交</n-h3>
    <n-timeline>
      <n-timeline-item v-for="item in solutions.data" :key="item.solutionId" :type="ResultTypes[item.result].type"
                       :title="ResultTypes[item.result].text" :content="content(item)" :time="item.submitTime"/>
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import {computed, onBeforeMount, ref} from "vue"
import {useStore} from "vuex"
import {NH3, NTimeline, NTimelineItem} from "naive-ui"
import {UserApi} from "@/api/request"
import {JudgeResult, PagedData} from "@/api/type"
import {LanguageNames, ResultTypes} from "@/type"

const store = useStore()

const userInfo = computed(() => store.state.userInfo)

const solutions = ref<PagedData<JudgeResult>>({data: [], count: 0})

onBeforeMount(() => {
  UserApi.getSolutions(1, 5, userInfo.value).then(data => {
    solutions.value = data
  })
})

function content(item: JudgeResult) {
  return `[${LanguageNames[item.language!]}] ${item.problemId}.${item.title}`
}
</script>
