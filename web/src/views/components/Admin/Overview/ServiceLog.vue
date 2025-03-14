<template>
  <n-card title="服务日志">
    <n-scrollbar x-scrollable>
      <n-log :rows="20" :lines="logs" language="log" />
    </n-scrollbar>
  </n-card>
</template>

<script setup lang="ts">
import { onDeactivated, onMounted, ref } from "vue"
import { NCard, NLog, NScrollbar, useThemeVars } from "naive-ui"
import { LogApi } from "@/api/request"
import { LogFormatter } from "@/utils"

let timer: number
let lastTime: number = 0
const logs = ref<Array<string>>([])
const themeVars = useThemeVars()

onMounted(() => {
  getLogs(0)
})

onDeactivated(() => {
  clearTimeout(timer)
})

async function getLogs(time: number) {
  try {
    const data = await LogApi.getLatest10Logs(time)
    if (data.length > 0) {
      lastTime = data[data.length - 1].time
    }
    data.forEach((val) => {
      logs.value.push(LogFormatter.format(val))
    })
  } catch {
  } finally {
    timer = setTimeout(getLogs, 2000, lastTime)
  }
}
</script>

<style lang="scss">
.n-log {
  pre {
    white-space: pre;
  }
}

.log-time {
  color: v-bind("themeVars.textColor3");
}

.log-level-info {
  color: v-bind("themeVars.successColor");
}

.log-level-warn {
  color: v-bind("themeVars.warningColor");
}

.log-level-error {
  color: v-bind("themeVars.errorColor");
}

.log-class-name {
  color: darkcyan;
}

.log-instance-id,
.log-thread,
.log-msg {
  color: v-bind("themeVars.textColor2");
}
</style>
