<template>
  <n-space vertical align="center" size="large" item-style="width: 100%">
    <!-- 错误 -->
    <n-result
      v-if="error != null"
      status="error"
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
import { ErrorMessage, JudgeResult, UserInfo } from "@/api/type"
import { ramUsage } from "@/utils"
import { NResult, NSpace, NText } from "naive-ui"
import { computed, onMounted, onUnmounted, ref } from "vue"
import { useStore } from "vuex"

class Result {
  status: any
  title: string
  desc?: string
  error?: string

  constructor(status: string, title: string, desc?: string, error?: string) {
    this.status = status
    this.title = title
    this.desc = desc
    this.error = error
  }
}

const store = useStore()

const error = ref<ErrorMessage | null>(null)
const result = ref<Result>({
  status: "418",
  title: "提交成功，正在获取结果"
})

const darkTheme = computed<boolean>(() => store.state.theme != null)
const userInfo = computed<UserInfo>(() => store.state.userInfo)

const props = defineProps<{
  solutionId: string
}>()

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
  return `运行时间: ${t} ms，内存占用: ${m}`
}

/**
 * 获取结果
 */
function fetchResult() {
  sse = new EventSource(
    `${ApiPath.SOLUTION}/${props.solutionId}?token=${userInfo.value.token}`
  )

  sse.onmessage = (event) => {
    const data = JSON.parse(event.data) as JudgeResult

    if (data.state !== 0) {
      result.value = {
        status: "418",
        title: "在队列中，等待判题"
      }
    } else {
      setResult(data)
      sse.close()
    }
  }

  sse.onerror = () => {
    error.value = new ErrorMessage(500, "发生了错误，请查询提交记录")
    sse.close()
  }
}

function setResult(r: JudgeResult) {
  const passed = r.passed ? `${r.passed}/${r.total}` : ""
  switch (r.result) {
    case 0:
      result.value = new Result("success", "完全正确", usage(r))
      break
    case 1:
      result.value = new Result(
        "warning",
        "时间超限",
        `${usage(r)}，通过: ${passed}`
      )
      break
    case 2:
      result.value = new Result(
        "warning",
        "内存超限",
        `${usage(r)}，通过: ${passed}`
      )
      break
    case 3:
      result.value = new Result(
        "warning",
        "部分通过",
        `${usage(r)}，通过: ${passed}`
      )
      break
    case 4:
      result.value = new Result("error", "答案错误", usage(r))
      break
    case 5:
      result.value = new Result("error", "编译错误", undefined, r.errorInfo)
      break
    case 6:
      result.value = new Result("error", "运行错误", undefined, r.errorInfo)
      break
    case 7:
      result.value = new Result(
        "error",
        "内部错误",
        "判题服务器发生错误",
        r.errorInfo
      )
      break
    case 8:
      result.value = new Result(
        "warning",
        "输出超限",
        "你的程序产生的输出已超出题目限制"
      )
      break
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
