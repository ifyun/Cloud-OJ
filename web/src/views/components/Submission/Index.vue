<template>
  <div class="submission">
    <n-card style="height: 100%">
      <Skeleton v-if="loading" />
      <error-result v-else-if="!loading && error != null" :error="error" />
      <div v-else class="content">
        <div>
          <n-scrollbar style="height: 100%">
            <n-tabs v-model:value="tab" type="line" @update:value="changeTab">
              <n-tab-pane
                name="problem"
                tab="题目描述"
                display-directive="show">
                <n-h2 style="margin-bottom: 6px">
                  {{ `${problem.problemId}.${problem.title}` }}
                </n-h2>
                <n-space size="small">
                  <n-tag type="success" size="small" round :bordered="false">
                    <template #icon>
                      <n-icon :component="HelpCircle" />
                    </template>
                    {{ problem.score }} 分
                  </n-tag>
                  <n-tag type="error" size="small" round :bordered="false">
                    <template #icon>
                      <n-icon :component="TimerOutlined" />
                    </template>
                    时间限制 {{ problem.timeout }} ms
                  </n-tag>
                  <n-tag type="warning" size="small" round :bordered="false">
                    <template #icon>
                      <n-icon :component="DataSaverOffRound" />
                    </template>
                    内存限制 {{ problem.memoryLimit }} MB
                  </n-tag>
                  <n-tag type="info" size="small" round :bordered="false">
                    <template #icon>
                      <n-icon :component="PrintRound" />
                    </template>
                    输出限制 {{ problem.outputLimit }} MB
                  </n-tag>
                </n-space>
                <!-- 题目内容 -->
                <markdown-view
                  :content="problem.description"
                  style="margin-top: 12px" />
              </n-tab-pane>
              <n-tab-pane name="solutions" tab="提交记录">
                <n-h2 style="margin-bottom: 12px">
                  {{ `${problem.problemId}.${problem.title}` }}
                </n-h2>
                <solution-single :problem-id="pid" />
              </n-tab-pane>
            </n-tabs>
          </n-scrollbar>
        </div>
        <!-- 代码编辑器 -->
        <div style="overflow: hidden">
          <code-editor
            :value="code"
            :available-languages="problem.languages"
            :loading="disableSubmit"
            @submit="submit" />
        </div>
      </div>
    </n-card>
  </div>
  <n-modal
    v-model:show="showResult"
    :auto-focus="false"
    :mask-closable="false"
    preset="card"
    style="width: 600px; margin-top: 200px">
    <result-dialog :solution-id="solutionId" style="margin-bottom: 12px" />
  </n-modal>
</template>

<script setup lang="ts">
import { computed, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "vuex"
import {
  NCard,
  NH2,
  NIcon,
  NModal,
  NScrollbar,
  NSpace,
  NTabPane,
  NTabs,
  NTag,
  useMessage
} from "naive-ui"
import { HelpCircle } from "@vicons/ionicons5"
import { DataSaverOffRound, PrintRound, TimerOutlined } from "@vicons/material"
import { CodeEditor, ErrorResult, MarkdownView } from "@/components"
import Skeleton from "./Skeleton.vue"
import ResultDialog from "./ResultDialog.vue"
import { ContestApi, JudgeApi, ProblemApi } from "@/api/request"
import { ErrorMessage, Problem, SubmitData, UserInfo } from "@/api/type"
import { SourceCode } from "@/type"
import { setTitle } from "@/utils"
import SolutionSingle from "./Solutions.vue"

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const props = withDefaults(defineProps<{ pid: string; cid: string | null }>(), {
  cid: null
})

const loading = ref<boolean>(true)
const tab = ref<string>("problem")
const showResult = ref<boolean>(false)
const disableSubmit = ref<boolean>(false)
const error = ref<ErrorMessage | null>(null)

const problem = ref<Problem>(new Problem())
const code = ref<string>("")
const solutionId = ref<string>("")
const userInfo = computed<UserInfo>(() => store.state.userInfo)

let problemId: number | null = null
let contestId: number | null = null

onBeforeMount(() => {
  const reg = /^\d+$/

  if (route.query.tab && route.query.tab.toString() === "solutions") {
    tab.value = "solutions"
  }

  if (props.cid != null && reg.test(props.cid)) {
    contestId = Number(props.cid)
  }

  if (reg.test(props.pid)) {
    problemId = Number(props.pid)
    queryProblem()
  } else {
    error.value = {
      status: 404,
      error: "Not Found",
      message: "找不到题目"
    }

    loading.value = false
  }
})

function changeTab(value: string) {
  tab.value = value
  router.push({
    query: { tab: value }
  })
}

/**
 * 获取题目数据
 */
function queryProblem() {
  if (contestId == null) {
    // 非竞赛题目
    ProblemApi.getSingle(problemId!, userInfo.value)
      .then((data) => {
        setTitle(data.title)
        problem.value = data
      })
      .catch((err: ErrorMessage) => (error.value = err))
      .finally(() => (loading.value = false))
  } else {
    // 竞赛题目
    ContestApi.getProblem(contestId, problemId!, userInfo.value)
      .then((data) => {
        setTitle(data.title)
        problem.value = data
      })
      .catch((err: ErrorMessage) => (error.value = err))
      .finally(() => (loading.value = false))
  }
}

/**
 * 提交代码
 */
function submit(data: SourceCode) {
  if (data.code.trim().length == 0 || disableSubmit.value) {
    return
  }

  disableSubmit.value = true

  const submitData: SubmitData = {
    language: data.language,
    problemId: problemId!,
    contestId,
    sourceCode: data.code.trim(),
    type: 0,
    userId: userInfo.value.userId!
  }

  JudgeApi.submit(submitData, userInfo.value)
    .then((id) => {
      solutionId.value = id
      showResult.value = true
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      disableSubmit.value = false
    })
}
</script>

<style lang="scss">
.cm-s-ttcn {
  border-left: 0;
  border-top: 1px solid #f7f7f7;
  border-right: 1px solid #f7f7f7;
  border-bottom: 1px solid #f7f7f7;

  .CodeMirror-gutters {
    border: none;
  }
}

.cm-s-material-darker {
  .CodeMirror-scroll {
    background-color: #161b22;
  }

  .CodeMirror-gutter {
    background-color: #161b22;
  }

  .CodeMirror-linenumber {
    background-color: #161b22;
  }
}
</style>

<style scoped lang="scss">
.submission {
  width: calc(100% - var(--layout-padding) * 2);
  min-height: 1000px;
  padding: var(--layout-padding);

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
