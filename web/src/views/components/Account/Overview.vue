<template>
  <n-space vertical>
    <div id="chart-1">
      <languages :languages="overview.preference" style="flex: 0.5" />
      <results-panel :data="overview.results" style="flex: 0.5" />
    </div>
    <n-divider />
    <active-chart :data="overview.activities" />
    <n-divider v-if="showTimeline" />
    <timeline v-if="showTimeline" />
  </n-space>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { ErrorMessage, Overview } from "@/api/type"
import dayjs from "dayjs"
import { NDivider, NSpace, useMessage } from "naive-ui"
import { onBeforeMount, ref } from "vue"
import Languages from "./Languages.vue"
import ResultsPanel from "./ResultsPanel.vue"
import Timeline from "./Timeline.vue"
import ActiveChart from "./ActiveChart.vue"

const props = defineProps<{ uid: number; showTimeline: boolean }>()

const message = useMessage()

const year = ref<number>(dayjs().year())
const overview = ref<Overview>(new Overview())

onBeforeMount(() => {
  UserApi.getOverview(props.uid, year.value)
    .then((data) => {
      overview.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
})
</script>

<style scoped lang="scss">
#chart-1 {
  display: flex;

  > * {
    margin-left: 32px;

    &:first-child {
      margin-left: 0;
    }
  }
}
</style>
