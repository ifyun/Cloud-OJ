<template>
  <div class="submission">
    <n-card :bordered="false" style="height: 100%" content-style="overflow: hidden">
      <Skeleton v-if="loading"/>
      <div v-else class="content">
        <div>
          <n-scrollbar style="height: 100%">
            <n-tabs type="line">
              <n-tab-pane name="题目描述">
                <n-h2 style="margin-bottom: 6px">{{ `${problem.problemId}.${problem.title}` }}</n-h2>
                <n-space size="small">
                  <badge label="分数" color="#4EAA25">
                    <template #icon>
                      <question-circle/>
                    </template>
                    {{ problem.score }} 分
                  </badge>
                  <badge label="时间" color="#D9644D">
                    <template #icon>
                      <timer-outlined/>
                    </template>
                    {{ problem.timeout }} ms
                  </badge>
                  <badge label="内存" color="#007EC6">
                    <template #icon>
                      <memory-round/>
                    </template>
                    {{ problem.memoryLimit }} MB
                  </badge>
                  <badge label="输出" color="#F48041">
                    <template #icon>
                      <file-alt/>
                    </template>
                    {{ problem.outputLimit }} MB
                  </badge>
                </n-space>
                <!-- 题目内容 -->
                <markdown-view :content="problem.description" style="margin-top: 12px"/>
              </n-tab-pane>
              <n-tab-pane name="提交记录">
                还没有做
              </n-tab-pane>
            </n-tabs>
          </n-scrollbar>
        </div>
        <!-- 代码编辑器 -->
        <div class="editor">
          <code-editor v-model="code" @submit="submit" :loading="disableSubmit"/>
        </div>
      </div>
    </n-card>
  </div>
  <n-modal v-model:show="showResult" preset="card" :mask-closable="false"
           style="width: 520px; margin-top: 220px">
    <result-dialog :solution-id="solutionId"/>
  </n-modal>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {
  NCard,
  NGrid,
  NGridItem,
  NH2,
  NModal,
  NScrollbar,
  NSpace,
  NTabPane,
  NTabs,
  NTag,
  useMessage
} from "naive-ui"
import {QuestionCircle, FileAlt} from "@vicons/fa"
import {TimerOutlined, MemoryRound} from "@vicons/material"
import MarkdownView from "@/components/MarkdownView/Index.vue"
import CodeEditor from "@/components/CodeEditor.vue"
import Badge from "@/components/Badge.vue"
import Skeleton from "@/views/components/Submission/Skeleton.vue"
import ResultDialog from "@/views/components/Submission/ResultDialog.vue"
import {JudgeApi, ProblemApi} from "@/api/request"
import {ErrorMsg, Problem, SubmitData, UserInfo} from "@/api/type"

@Options({
  name: "Submission",
  components: {
    NCard,
    NGrid,
    NGridItem,
    NSpace,
    NScrollbar,
    NTabs,
    NTabPane,
    NH2,
    NTag,
    NModal,
    QuestionCircle,
    FileAlt,
    TimerOutlined,
    MemoryRound,
    Badge,
    Skeleton,
    MarkdownView,
    CodeEditor,
    ResultDialog,
  }
})
export default class Submission extends Vue {
  private store = useStore()
  private message = useMessage()

  private loading: boolean = true
  private error: ErrorMsg | null = null

  private showResult: boolean = false
  private disableSubmit: boolean = false

  private problemId: number | null = null
  private problem: Problem = new Problem()
  private code: string = ""
  private solutionId: string = ""

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    const problemId = this.$route.query.problemId

    if (typeof problemId !== "undefined") {
      this.problemId = Number(this.$route.query.problemId)
    }

    this.getProblem()
  }

  /**
   * 获取题目数据
   */
  getProblem() {
    if (this.problemId != null) {
      ProblemApi.getSingle(this.problemId)
          .then((data) => {
            this.problem = data
          })
          .catch((error) => {
            console.debug(error)
          })
          .finally(() => {
            this.loading = false
          })
    }
  }

  /**
   * 提交代码
   */
  submit(language: number) {
    if (this.code.trim().length == 0 || this.disableSubmit) {
      return
    }

    this.disableSubmit = true
    const data: SubmitData = {
      language,
      problemId: this.problemId!,
      sourceCode: this.code,
      type: 0,
      userId: this.userInfo.userId!
    }

    JudgeApi.submit(data, this.userInfo)
        .then((solutionId) => {
          this.solutionId = solutionId
          this.showResult = true
        })
        .catch((error: ErrorMsg) => {
          this.message.error(error.toString())
        })
        .finally(() => {
          this.disableSubmit = false
        })
  }
}
</script>

<style scoped lang="scss">
.submission {
  width: calc(100% - var(--layout-padding) * 2);
  height: calc(100% - var(--header-height) - var(--layout-padding) * 2);
  min-height: 720px;
  padding: var(--layout-padding);
  flex: auto;

  .content {
    height: 100%;
    display: flex;
    flex-direction: row;

    & > * {
      flex: 1;
      margin-left: 12px;

      &:first-child {
        margin-left: 0;
      }
    }
  }
}
</style>
