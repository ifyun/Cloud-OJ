<template>
  <div class="submission">
    <n-card
      :bordered="false"
      style="height: 100%"
      content-style="overflow: hidden">
      <Skeleton v-if="loading" />
      <ErrorResult v-else-if="!loading && errorMsg != null" :error="errorMsg" />
      <div v-else class="content">
        <div>
          <n-scrollbar style="height: 100%">
            <n-tabs type="line">
              <n-tab-pane name="题目描述">
                <n-h2 style="margin-bottom: 6px">
                  {{ `${problem.problemId}.${problem.title}` }}
                </n-h2>
                <n-space size="small">
                  <badge label="分数" size="medium" color="#4EAA25">
                    <template #icon>
                      <question-circle />
                    </template>
                    {{ problem.score }}
                  </badge>
                  <badge label="时间" size="medium" color="#0288D1">
                    <template #icon>
                      <timer-outlined />
                    </template>
                    {{ problem.timeout }} 毫秒
                  </badge>
                  <badge label="内存" size="medium" color="#4EAA25">
                    <template #icon>
                      <memory-round />
                    </template>
                    {{ problem.memoryLimit }} MB
                  </badge>
                  <badge label="输出" size="medium" color="#D14748">
                    <template #icon>
                      <file-alt />
                    </template>
                    {{ problem.outputLimit }} MB
                  </badge>
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
  NModal,
  NScrollbar,
  NSpace,
  NTabPane,
  NTabs,
  useMessage
} from "naive-ui"
import { FileAlt, QuestionCircle } from "@vicons/fa"
import { MemoryRound, TimerOutlined } from "@vicons/material"
import { Badge, CodeEditor, ErrorResult, MarkdownView } from "@/components"
import Skeleton from "./Skeleton.vue"
import ResultDialog from "./ResultDialog.vue"
import { JudgeApi, ProblemApi } from "@/api/request"
import { ErrorMsg, Problem, SubmitData, UserInfo } from "@/api/type"
import { SourceCode } from "@/type"

const store = useStore()
const message = useMessage()

const props = defineProps<{ pid: string }>()

const loading = ref<boolean>(true)
const showResult = ref<boolean>(false)
const disableSubmit = ref<boolean>(false)
const errorMsg = ref<ErrorMsg | null>(null)

const problem = ref<Problem>(new Problem())
const code = ref<string>("")
const solutionId = ref<string>("")
const userInfo = computed<UserInfo>(() => store.state.userInfo)

let problemId: number | null = null

onBeforeMount(() => {
  const reg = /^\d+$/

  if (reg.test(props.pid)) {
    problemId = Number(props.pid)
    queryProblem()
  } else {
    errorMsg.value = {
      code: 400,
      msg: "无效的ID"
    }
    loading.value = false
  }
})

/**
 * 获取题目数据
 */
function queryProblem() {
  if (problemId != null) {
    ProblemApi.getSingle(problemId)
      .then((data) => {
        problem.value = data
      })
      .catch((error: ErrorMsg) => {
        errorMsg.value = error
      })
      .finally(() => {
        loading.value = false
      })
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
    sourceCode: data.code.trim(),
    type: 0,
    userId: userInfo.value.userId!
  }

  JudgeApi.submit(submitData, userInfo.value)
    .then((id) => {
      solutionId.value = id
      showResult.value = true
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      disableSubmit.value = false
    })
}
</script>

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
