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
                <n-tag>分数: {{ problem.score }} 分</n-tag>
                <n-tag type="info">时间限制: {{ problem.timeout }} ms</n-tag>
                <n-tag type="info">内存限制: {{ problem.memoryLimit }} MB</n-tag>
                <n-tag type="info">输出限制: {{ problem.outputLimit }} MB</n-tag>
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
        <code-editor v-model="code" @submit="submit"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {NGrid, NGridItem, NH2, NScrollbar, NSpace, NTabPane, NTabs, NTag} from "naive-ui"
import MarkdownView from "@/components/MarkdownView/index.vue"
import CodeEditor from "@/components/CodeEditor.vue"
import Skeleton from "@/views/components/Submission/Skeleton.vue"
import {ProblemApi} from "@/api/request"
import {ErrorMsg, Problem} from "@/api/type"

@Options({
  components: {
    NGrid,
    NGridItem,
    NSpace,
    NScrollbar,
    NTabs,
    NTabPane,
    NH2,
    NTag,
    Skeleton,
    MarkdownView,
    CodeEditor
  }
})
export default class Submission extends Vue {
  private loading: boolean = true         // 加载中
  private error: ErrorMsg | null = null   // 错误信息

  private problemId: number | null = null
  private problem: Problem = new Problem()
  private code: string = ""

  beforeMount() {
    const problemId = this.$route.query.problemId

    if (typeof problemId !== "undefined") {
      this.problemId = Number(this.$route.query.problemId)
    }

    this.getProblem()
  }

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

  submit(language: number) {
    console.debug("language:", language)
    console.debug("code:", this.code)
  }
}
</script>

<style scoped lang="scss">
.submission {
  width: calc(100% - 50px);
  height: calc(100% - var(--header-height) - 50px);
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