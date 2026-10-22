<template>
  <div class="admin-wrap">
    <n-flex vertical>
      <solution-summary />
      <queues-info-view :data="queuesInfo" />
    </n-flex>
  </div>
</template>

<script setup lang="ts">
import { AdminApi } from "@/api/request"
import { ErrorMessage, type QueuesInfo } from "@/api/type"
import { NFlex, useMessage } from "naive-ui"
import { onActivated, onDeactivated, ref } from "vue"
import QueuesInfoView from "./QueuesInfoView.vue"
import SolutionSummary from "./SolutionSummary.vue"

defineOptions({
  name: "AdminIndex"
})

const message = useMessage()
const queuesInfo = ref<QueuesInfo>({
  Submit: {
    consumers: 0,
    messages: 0,
    messages_ready: 0,
    messages_unacknowledged: 0,
    messages_details: { samples: [] }
  },
  Judge: {
    consumers: 0,
    messages: 0,
    messages_ready: 0,
    messages_unacknowledged: 0,
    messages_details: { samples: [] }
  }
})

let timer: number

onActivated(() => {
  getQueuesInfo()
})

onDeactivated(() => {
  clearTimeout(timer)
})

function getQueuesInfo() {
  AdminApi.getQueuesInfo()
    .then((data) => (queuesInfo.value = data))
    .catch((err: ErrorMessage) => message.error(err.toString()))
    .finally(() => (timer = setTimeout(getQueuesInfo, 2500)))
}
</script>
