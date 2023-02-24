<template>
  <div class="submission">
    <n-card
      :bordered="false"
      style="height: 100%"
      content-style="overflow: hidden">
      <Skeleton v-if="loading" />
      <ErrorResult v-else-if="!loading && error != null" :error="error" />
      <div v-else class="content">
        <div>
          <n-scrollbar style="height: 100%">
            <n-tabs type="line">
              <n-tab-pane name="题目描述">
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
              <n-tab-pane name="提交记录"> 还没有做</n-tab-pane>
            </n-tabs>
          </n-scrollbar>
        </div>
        <!-- 代码编辑器 -->
        <div class="editor">
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
    preset="card"
    :mask-closable="false"
    style="width: 520px; margin-top: 240px">
    <result-dialog :solution-id="solutionId" style="margin-bottom: 24px" />
  </n-modal>
</template>

<script setup lang="ts">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import {
  NCard,
  NH2,
  NIcon,
  NModal,
  NScrollbar,
  NSpace,
  NTag,
  NTabPane,
  NTabs,
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

const store = useStore()
const message = useMessage()

const props = withDefaults(defineProps<{ pid: string; cid: string | null }>(), {
  cid: null
})

const loading = ref<boolean>(true)
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

  if (props.cid != null && reg.test(props.cid)) {
    contestId = Number(props.cid)
  }

  if (reg.test(props.pid)) {
    problemId = Number(props.pid)
    queryProblem()
  } else {
    error.value = {
      status: 404,
      message: "找不到题目"
    }

    loading.value = false
  }
})

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
}
</style>

<style scoped lang="scss">
.submission {
  width: calc(100% - var(--layout-padding) * 2);
  height: calc(100% - var(--header-height) - var(--layout-padding) * 2);
  min-height: 720px;
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
