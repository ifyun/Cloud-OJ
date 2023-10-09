<template>
  <n-space vertical align="center" size="large" item-style="width: 100%">
    <!-- 错误 -->
    <n-result
      v-if="error != null"
      status="warning"
      :title="error.error"
      :description="error.message">
    </n-result>
    <!-- 结果 -->
    <n-result
      v-else
      size="small"
      :status="result.status"
      :title="result.title"
      :description="result.desc">
    </n-result>
    <n-text v-if="result.error" type="error">
      <pre :class="{ dark: darkTheme }">{{ result.error }}</pre>
    </n-text>
  </n-space>
</template>

<script setup lang="ts">
import { ApiPath } from "@/api/request"
import { ErrorMessage, JudgeResult } from "@/api/type"
import { useStore } from "@/store"
import { ResultTypes } from "@/type"
import { ramUsage } from "@/utils"
import { NResult, NSpace, NText } from "naive-ui"
import { computed, onMounted, onUnmounted, ref } from "vue"

interface Result {
  status: any
  title: string
  desc?: string
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
    return { status: "418", title: "提交成功，正在获取结果" }
  }
  // 等待/编译/运行状态
  if (s.state !== 0) {
    return { status: "418", title: s.stateText! }
  }
  // 运行结束
  return {
    status: ResultTypes[s.result!],
    title: s.resultText!,
    desc: [5, 6, 7, 8].indexOf(s.result!) === -1 ? usage(s) : undefined,
    error: s.errorInfo
  }
})

let sse: EventSource

onMounted(() => {
  fetchResult()
})

onUnmounted(() => {
  sse.close()
})

function usage(r: JudgeResult) {
  const { time, memory } = r
  const t = (time! / 1000).toFixed(2)
  const m = ramUsage(memory)
  const text = `运行时间: ${t} ms, 内存占用: ${m}`

  if (r.passed) {
    return `${text}, 通过: ${r.passed}/${r.total}`
  } else {
    return text
  }
}

/**
 * 获取结果
 */
function fetchResult() {
  sse = new EventSource(
    `${ApiPath.SOLUTION}/${store.user.userInfo!.uid}/${
      props.submitTime
    }?token=${store.user.userInfo!.token}`
  )

  sse.onmessage = (event) => {
    const data = JSON.parse(event.data) as JudgeResult
    solution.value = data

    if (data.state === 0) {
      sse.close()
    }
  }

  sse.onerror = () => {
    error.value = new ErrorMessage(500, "无法获取结果")
    error.value.message = "请查询提交记录"
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
