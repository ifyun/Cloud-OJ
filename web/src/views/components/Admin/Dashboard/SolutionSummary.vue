<template>
  <n-flex wrap>
    <n-card title="用户语言统计" style="flex: 1; min-width: 0">
      <language-usage-chart :data="summary.language" style="height: 240px" />
    </n-card>
    <n-card title="结果统计" style="flex: 1; min-width: 0">
      <result-chart :data="summary.results" style="height: 240px" />
    </n-card>
  </n-flex>
</template>

<script setup lang="ts">
import { AdminApi } from "@/api/request"
import type { ErrorMessage, SolutionSummary } from "@/api/type"
import { NCard, NFlex, useMessage } from "naive-ui"
import { onActivated, ref } from "vue"
import LanguageUsageChart from "./LanguageUsageChart.vue"
import ResultChart from "./ResultChart.vue"

const message = useMessage()
const summary = ref<SolutionSummary>({
  results: {
    AC: 0,
    PA: 0,
    WA: 0,
    CE: 0,
    RE: 0,
    MLE: 0,
    TLE: 0,
    OLE: 0
  },
  language: []
})

onActivated(() => {
  AdminApi.getSolutionSummary()
    .then((data) => {
      summary.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
})
</script>
