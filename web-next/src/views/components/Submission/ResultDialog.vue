<template>
  <n-space vertical>
    <!-- 错误 -->
    <n-result
      v-if="error != null"
      :status="error"
      :title="error.code"
      :description="error.msg">
      <template #footer>
        <n-button secondary size="small" type="primary" @click="retry"
        >重试
        </n-button
        >
      </template>
    </n-result>
    <!-- 结果 -->
    <n-result
      v-else
      size="small"
      :status="result.status"
      :title="result.title"
      :description="result.desc">
      <template #footer v-if="showRetry">
        <n-button secondary size="small" type="primary" @click="retry"
        >重试
        </n-button
        >
      </template>
    </n-result>
    <n-text v-if="result.error" type="error">
      <pre :class="{ dark: isDarkTheme }">{{ result.error }}</pre>
    </n-text>
    <n-text v-if="showRetry" depth="3">
      你可以关闭此窗口，稍后查看提交记录
    </n-text>
  </n-space>
</template>

<script lang="ts">
import { useStore } from "vuex"
import { Options, Vue } from "vue-class-component"
import { Prop } from "vue-property-decorator"
import { NButton, NResult, NSpace, NText } from "naive-ui"
import { ErrorMsg, JudgeResult, UserInfo } from "@/api/type"
import { JudgeApi } from "@/api/request"

class Result {
  status: string
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

@Options({
  name: "ResultDialog",
  components: {
    NSpace,
    NResult,
    NButton,
    NText
  }
})
export default class ResultDialog extends Vue {
  private store = useStore()

  private error: ErrorMsg | null = null
  private result: Result = {
    status: "418",
    title: "提交成功，正在等待结果"
  }

  private showRetry = false

  @Prop(String)
  private solutionId?: string

  get isDarkTheme(): boolean {
    return this.store.state.theme != null
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    this.fetchResult(0)
  }

  retry() {
    this.fetchResult(0)
  }

  /**
   * 获取结果
   * @param count 轮询次数
   */
  fetchResult(count: number) {
    if (count > 15) {
      this.result = new Result("info", "没有获取到结果")
      this.showRetry = true
      return
    }

    JudgeApi.getResult(this.solutionId!, this.userInfo)
      .then((data) => {
        if (data == null || data.state !== JUDGED) {
          setTimeout(() => this.fetchResult(count + 1), 1000)
        } else {
          this.setResult(data)
        }
      })
      .catch((error: ErrorMsg) => {
        this.error = error
        this.showRetry = true
      })
  }

  setResult(r: JudgeResult) {
    const passRate = r.passRate! * 100
    switch (r.result) {
      case 0:
        this.result = new Result("success", "完全正确", Cost(r))
        break
      case 1:
        this.result = new Result("warning", `时间超限(${passRate}%)`, Cost(r))
        break
      case 2:
        this.result = new Result("warning", `内存超限(${passRate}%)`, Cost(r))
        break
      case 3:
        this.result = new Result("warning", `部分通过(${passRate}%)`, Cost(r))
        break
      case 4:
        this.result = new Result("error", "答案错误", Cost(r))
        break
      case 5:
        this.result = new Result("error", "编译错误", undefined, r.errorInfo)
        break
      case 6:
        this.result = new Result("error", "运行错误", undefined, r.errorInfo)
        break
      case 7:
        this.result = new Result("error", "内部错误", "判题服务器发生错误")
        break
      case 8:
        this.result = new Result(
          "warning",
          "输出超限",
          "你的程序产生的输出已超出题目限制"
        )
        break
    }
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
