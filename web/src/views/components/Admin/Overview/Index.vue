<template>
  <div class="admin-wrap">
    <n-flex vertical>
      <queues-info-view :data="queuesInfo" />
      <service-log />
    </n-flex>
  </div>
</template>

<script setup lang="ts">
import { onDeactivated, onMounted, ref } from "vue"
import QueuesInfoView from "./QueuesInfoView.vue"
import ServiceLog from "./ServiceLog.vue"
import { NFlex, useMessage } from "naive-ui"
import { useStore } from "@/store"
import { type QueuesInfo } from "@/api/type"
import { QueuesInfoPoller } from "@/api/request"

const store = useStore()
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

let poller: QueuesInfoPoller

onMounted(() => {
  poller = new QueuesInfoPoller(
    store.user.userInfo!.token,
    (data) => {
      queuesInfo.value = data
    },
    (error) => {
      message.error(error)
    }
  )
})

onDeactivated(() => {
  poller.close()
})
</script>
