<template>
  <div>
    <n-h4 strong style="margin-bottom: 16px">最近提交</n-h4>
    <n-timeline>
      <n-timeline-item v-for="item in solutions.data" :key="item.solutionId" :type="TYPES[item.result]"
                       :content="item.title" :title="RESULTS[item.result]" :time="item.submitTime"/>
    </n-timeline>
  </div>
</template>

<script setup lang="ts">
import {computed, onBeforeMount, ref} from "vue"
import {useStore} from "vuex"
import {NH4, NTimeline, NTimelineItem} from "naive-ui"
import {UserApi} from "@/api/request"
import {JudgeResult, PagedData} from "@/api/type"

const TYPES = ["success", "warning", "warning", "warning", "error", "error", "error", "error", "warning"]
const RESULTS = [
  "正确", "时间超限", "内存超限", "部分通过", "答案错误",
  "编译错误", "运行错误", "内部错误", "输出超限"
]

const store = useStore()

const userInfo = computed(() => store.state.userInfo)

const solutions = ref<PagedData<JudgeResult>>({data: [], count: 0})

onBeforeMount(() => {
  UserApi.getSolutions(1, 5, userInfo.value).then(data => {
    solutions.value = data
  })
})
</script>
