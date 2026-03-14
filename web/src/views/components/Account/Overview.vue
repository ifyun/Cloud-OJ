<template>
  <n-flex vertical>
    <n-flex :size="24">
      <languages :languages="overview.preference" style="flex: 0.5" />
      <results-panel :data="overview.results" style="flex: 0.5" />
    </n-flex>
    <n-divider />
    <n-heatmap
      :loading="loading"
      :data="overview.heatmap"
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
import { ErrorMessage, Overview } from "@/api/type"
import dayjs from "dayjs"
import { NDivider, NFlex, NHeatmap, NText, useMessage } from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import Languages from "./Languages.vue"
import ResultsPanel from "./ResultsPanel.vue"
import Timeline from "./Timeline.vue"

const message = useMessage()

const props = defineProps<{ uid: number; showTimeline: boolean }>()
const loading = ref<boolean>(false)
const overview = ref<Overview>(new Overview())

const timeRange = computed<string>(() => {
  if (overview.value.heatmap.length === 0) {
    return ""
  }

  const start = overview.value.heatmap.at(0)!.timestamp
  return `${dayjs(start).format("YYYY/MM-DD")} ~ 今天`
})

onBeforeMount(() => {
  loading.value = true
  UserApi.getOverview(props.uid)
    .then((data) => {
      overview.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => (loading.value = false))
})
</script>
