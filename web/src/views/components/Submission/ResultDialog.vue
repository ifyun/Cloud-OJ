<template>
  <n-space vertical>
    <!-- 错误 -->
    <n-result
      v-if="error != null"
      status="error"
      :title="error.error"
      :description="error.message">
      <template #footer>
        <n-button secondary size="small" type="primary" @click="retry">
          重试
        </n-button>
      </template>
    </n-result>
    <!-- 结果 -->
    <n-result
      v-else
      size="small"
      :status="result.status"
      :title="result.title"
      :description="result.desc">
      <template v-if="showRetry" #footer>
        <n-button secondary size="small" type="primary" @click="retry">
          重试
        </n-button>
      </template>
    </n-result>
    <n-text v-if="result.error" type="error">
      <pre :class="{ dark: darkTheme }">{{ result.error }}</pre>
    </n-text>
    <n-text v-if="showRetry" depth="3">
      你可以关闭此窗口，稍后查看提交记录
    </n-text>
  </n-space>
</template>

<script setup lang="ts">
import { useStore } from "vuex"
import { NButton, NResult, NSpace, NText } from "naive-ui"
import { ErrorMessage, JudgeResult, UserInfo } from "@/api/type"
import { JudgeApi } from "@/api/request"
import { computed, onMounted, ref } from "vue"

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

const JUDGED = 0

const Cost = (r: JudgeResult) => {
  const { time, memory } = r
  return `运行时间: ${time} ms，内存占用: ${memory} KB`
}

const store = useStore()

const error = ref<ErrorMessage | null>(null)
const result = ref<Result>({
  status: "418",
  title: "提交成功，正在等待结果"
})

const showRetry = ref<boolean>(false)
const darkTheme = computed<boolean>(() => store.state.theme != null)
const userInfo = computed<UserInfo>(() => store.state.userInfo)

const props = defineProps<{
  solutionId: string
}>()

onMounted(() => {
  setTimeout(() => fetchResult(0), 100)
})

function retry() {
  setTimeout(() => fetchResult(0), 100)
  error.value = null
  result.value = {
    status: "418",
    title: "重新获取中"
  }
}

/**
 * 获取结果
 * @param count 轮询次数
 */
function fetchResult(count: number) {
  if (count > 15) {
    result.value = new Result("info", "没有获取到结果")
    showRetry.value = true
    return
  }

  JudgeApi.getResult(props.solutionId, userInfo.value)
    .then((data) => {
      if (data == null || data.state !== JUDGED) {
        setTimeout(() => fetchResult(count + 1), 500)
      } else {
        showRetry.value = false
        setResult(data)
      }
    })
    .catch((err: ErrorMessage) => {
      error.value = err
      showRetry.value = true
      if (err.status === 404) {
        error.value.message = "你的提交可能还未写入数据库"
      }
    })
}

function setResult(r: JudgeResult) {
  const passRate = r.passRate! * 100
  switch (r.result) {
    case 0:
      result.value = new Result("success", "完全正确", Cost(r))
      break
    case 1:
      result.value = new Result("warning", `时间超限(${passRate}%)`, Cost(r))
      break
    case 2:
      result.value = new Result("warning", `内存超限(${passRate}%)`, Cost(r))
      break
    case 3:
      result.value = new Result("warning", `部分通过(${passRate}%)`, Cost(r))
      break
    case 4:
      result.value = new Result("error", "答案错误", Cost(r))
      break
    case 5:
      result.value = new Result("error", "编译错误", undefined, r.errorInfo)
      break
    case 6:
      result.value = new Result("error", "运行错误", undefined, r.errorInfo)
      break
    case 7:
      result.value = new Result("error", "内部错误", "判题服务器发生错误")
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
  border-left: 2px solid #e88080;

  &.dark {
    background-color: #383842;
  }
}
</style>
