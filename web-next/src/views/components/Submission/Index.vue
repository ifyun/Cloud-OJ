<template>
  <div class="submission">
    <Skeleton v-if="loading"/>
    <div v-else class="content">
      <div>
        <n-scrollbar style="height: 100%">
          <n-tabs type="line">
            <n-tab-pane name="题目描述">
              <n-h2 style="margin-bottom: 10px">{{ `${problem.problemId}.${problem.title}` }}</n-h2>
              <n-space size="small">
                <n-tag size="small">分数: {{ problem.score }} 分</n-tag>
                <n-tag size="small" type="info">时间: {{ problem.timeout }} ms</n-tag>
                <n-tag size="small" type="info">内存: {{ problem.memoryLimit }} MB</n-tag>
                <n-tag size="small" type="info">输出: {{ problem.outputLimit }} MB</n-tag>
              </n-space>
              <!-- 题目内容 -->
              <markdown-view :content="problem.description" style="margin-top: 15px"/>
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
  </div>
  <n-modal v-model:show="showResult" preset="card" :mask-closable="false"
           style="width: 520px; margin-top: 220px">
    <result-dialog :solution-id="solutionId"/>
  </n-modal>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {NGrid, NGridItem, NH2, NModal, NScrollbar, NSpace, NTabPane, NTabs, NTag, useMessage} from "naive-ui"
import MarkdownView from "@/components/MarkdownView/Index.vue"
import CodeEditor from "@/components/CodeEditor.vue"
import Skeleton from "@/views/components/Submission/Skeleton.vue"
import ResultDialog from "@/views/components/Submission/ResultDialog.vue"
import {JudgeApi, ProblemApi} from "@/api/request"
import {ErrorMsg, Problem, SubmitData, UserInfo} from "@/api/type"

@Options({
  name: "Submission",
  components: {
    ResultDialog,
    NGrid,
    NGridItem,
    NSpace,
    NScrollbar,
    NTabs,
    NTabPane,
    NH2,
    NTag,
    NModal,
    Skeleton,
    MarkdownView,
    CodeEditor
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
  width: calc(100% - 50px);
  height: calc(100% - var(--header-height) - 50px);
  min-height: 600px;
  padding: 25px;
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