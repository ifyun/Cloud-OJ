<template>
  <n-flex>
    <n-card v-for="(value, name) in data" :key="name" :title="QueueNames[name]">
      <n-flex>
        <table class="queue-info">
          <tbody>
            <tr>
              <th>待分发</th>
              <td>
                <div class="dot dot-ready" />
                {{ value.messages_ready }}
              </td>
            </tr>
            <tr>
              <th>未确认</th>
              <td>
                <div class="dot dot-unack" />
                {{ value.messages_unacknowledged }}
              </td>
            </tr>
            <tr>
              <th>消息总数</th>
              <td>
                <div class="dot dot-total" />
                {{ value.messages }}
              </td>
            </tr>
            <tr>
              <th>消费者</th>
              <td>{{ value.consumers }}</td>
            </tr>
          </tbody>
        </table>
        <queue-info-chart
          :chart-name="name as string"
          :data="value.messages_details.samples.slice(0, 8)" />
      </n-flex>
    </n-card>
  </n-flex>
</template>

<script lang="ts" setup>
import { NCard, NFlex } from "naive-ui"
import type { QueuesInfo } from "@/api/type"
import QueueInfoChart from "./QueueInfoChart.vue"

const QueueNames: { [name: string]: string } = {
  Submit: "提交队列",
  Judge: "判题队列"
}

defineProps<{ data: QueuesInfo }>()
</script>

<style lang="scss" scoped>
.queue-info {
  th {
    font-weight: normal;
    text-align: right;
    padding: 4px 10px;
  }

  td {
    text-align: left;
    padding: 4px 10px;
  }
}

.dot {
  display: inline-block;
  margin-right: 4px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot-ready {
  background: #18a058;
}

.dot-unack {
  background: #409eff;
}

.dot-total {
  background: #f56c6c;
}
</style>
