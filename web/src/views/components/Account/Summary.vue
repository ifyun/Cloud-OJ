<template>
  <n-flex vertical>
    <n-flex :size="24">
      <languages :languages="summary.preference" style="flex: 0.5" />
      <results-panel :data="summary.results" style="flex: 0.5" />
    </n-flex>
    <n-divider />
    <n-heatmap
      :loading="loading"
      :data="summary.heatmap"
      :tooltip="{ placement: 'bottom', delay: 500 }"
      show-color-indicator
      style="align-self: center">
      <template #tooltip="{ timestamp: date, value: tooltipValue }">
        <div>
          <div>日期: {{ new Date(date).toLocaleDateString() }}</div>
          <div>通过: {{ tooltipValue ?? 0 }}</div>
        </div>
      </template>
      <template #footer>
        <n-text>{{ timeRange }}</n-text>
      </template>
    </n-heatmap>
    <n-divider v-if="showTimeline" />
    <timeline v-if="showTimeline" />
  </n-flex>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { ErrorMessage, UserSummary } from "@/api/type"
import dayjs from "dayjs"
import { NDivider, NFlex, NHeatmap, NText, useMessage } from "naive-ui"
import { computed, ref, watch } from "vue"
import Languages from "./Languages.vue"
import ResultsPanel from "./ResultsPanel.vue"
import Timeline from "./Timeline.vue"

defineOptions({
  name: "UserSummary"
})

const props = defineProps<{ uid: number; showTimeline: boolean }>()
const message = useMessage()
const loading = ref<boolean>(false)
const summary = ref<UserSummary>(new UserSummary())
const timeRange = computed<string>(() => {
  if (summary.value.heatmap.length === 0) {
    return ""
  }

  const start = summary.value.heatmap.at(0)!.timestamp
  return `${dayjs(start).format("YYYY/MM-DD")} ~ 今天`
})

watch(
  () => props.uid,
  (val) => {
    loading.value = true
    UserApi.getSummary(val)
      .then((data) => {
        summary.value = data
      })
      .catch((err: ErrorMessage) => {
        message.error(err.toString())
      })
      .finally(() => (loading.value = false))
  },
  { immediate: true }
)
</script>
