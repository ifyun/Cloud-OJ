<template>
  <n-space vertical>
    <div id="chart-1">
      <languages :languages="overview.preference" style="flex: 0.5" />
      <results-panel :data="overview.statistics" style="flex: 0.5" />
    </div>
    <n-divider />
    <heatmap :data="overview.activities" :year="year.toString()" />
    <n-divider />
    <timeline />
  </n-space>
</template>

<script setup lang="ts">
import { onBeforeMount, ref } from "vue"
import { NDivider, NSpace, useMessage } from "naive-ui"
import Languages from "./Languages.vue"
import ResultsPanel from "./ResultsPanel.vue"
import Heatmap from "./Heatmap.vue"
import Timeline from "./Timeline.vue"
import { UserApi } from "@/api/request"
import { ErrorMessage, Overview } from "@/api/type"
import moment from "moment"

const props = defineProps<{ userId: string }>()

const message = useMessage()

const year = ref<number>(moment().year())
const overview = ref<Overview>(new Overview())

onBeforeMount(() => {
  UserApi.getOverview(props.userId, year.value)
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

  * {
    margin-left: 24px;

    &:first-child {
      margin-left: 0;
    }
  }
}
</style>
