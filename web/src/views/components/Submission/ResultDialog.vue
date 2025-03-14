<template>
  <n-space vertical align="center" size="large" item-style="width: 100%">
    <!-- 错误 -->
    <n-result
      v-if="error != null"
      size="small"
      status="warning"
      :title="error.error"
      :description="error.message">
    </n-result>
    <!-- 结果 -->
    <n-result v-else size="small" :status="result.status">
      <n-table
        size="small"
        style="text-align: center; margin-bottom: 12px"
        :single-line="false">
        <thead>
          <tr>
            <th>状态</th>
            <th>CPU 时间</th>
            <th>内存占用</th>
            <th>得分</th>
            <th>通过测试点</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <n-text strong :type="result.textColor">
                {{ result.statusText }}
              </n-text>
            </td>
            <td>{{ result.time }}</td>
            <td>{{ result.ram }}</td>
            <td>
              <n-text strong>{{ result.score }}</n-text>
            </td>
            <td>{{ result.passed }}</td>
          </tr>
        </tbody>
      </n-table>
      <n-text v-if="result.error" type="error">
        <pre :class="{ dark: darkTheme }">{{ result.error }}</pre>
      </n-text>
    </n-result>
  </n-space>
</template>

<script setup lang="ts">
import { ApiPath } from "@/api"
import { ErrorMessage, JudgeResult } from "@/api/type"
import { useStore } from "@/store"
import { ResultTypes } from "@/type"
import { ramUsage, timeUsage } from "@/utils"
import { NResult, NSpace, NTable, NText } from "naive-ui"
import { computed, onDeactivated, onMounted, ref } from "vue"

interface Result {
  status: any
  statusText: string
  textColor?: string
  time?: string
  ram?: string
  score?: string | number
  passed?: string
  error?: string
}

const props = defineProps<{
  submitTime: number
}>()

const store = useStore()

const error = ref<ErrorMessage | null>(null)
const solution = ref<JudgeResult | null>(null)

const darkTheme = computed(() => store.app.theme != null)

const result = computed<Result>(() => {
  const s = solution.value

  if (s == null) {
    return { status: "418", statusText: "提交成功" }
  }
  // 等待/编译/运行状态
  if (s.state !== 0) {
    return { status: "418", statusText: s.stateText!, textColor: "info" }
  }
  // 运行结束
  return {
    status: ResultTypes[s.result!],
    statusText: s.resultText!,
    textColor: ResultTypes[s.result!],
    time: s.time ? timeUsage(s.time) : "-",
    ram: s.memory ? ramUsage(s.memory) : "-",
    score: s.score ?? "-",
    passed: s.passed ? `${s.passed}/${s.total}` : "-",
    error: s.errorInfo
  }
})

let sse: EventSource

onMounted(() => {
  fetchResult()
})

onDeactivated(() => {
  sse.close()
})

/**
 * 获取结果
 */
function fetchResult() {
  sse = new EventSource(
    `${ApiPath.SOLUTION}/time/${props.submitTime}?token=${
      store.user.userInfo!.token
    }`
  )

  sse.onmessage = (event) => {
    const data = JSON.parse(event.data) as JudgeResult
    solution.value = data

    if (data.state === 0) {
      sse.close()
    }
  }

  sse.onerror = (event) => {
    const data = (event as MessageEvent).data
    error.value = new ErrorMessage(500, data ?? "无法获取结果")
    error.value.message = "你已提交成功，可稍后查询提交记录"
    sse.close()
  }
}
</script>

<style scoped lang="scss">
pre {
  font-family: inherit;
  margin: 0;
  padding: 12px;
  background-color: #f8f8f8;
  border-radius: 2px;
  white-space: pre-wrap;

  &.dark {
    background-color: #0d1117;
  }
}
</style>
