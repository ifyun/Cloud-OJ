<template>
  <div class="submission">
    <Skeleton v-if="loading" />
    <div v-else class="content">
      <n-tabs type="line">
        <n-tab-pane
          name="problem"
          tab="题目描述"
          display-directive="show"
          style="height: calc(100% - 54px)">
          <n-scrollbar>
            <n-h3 style="margin-bottom: 6px">
              {{ `${problem.problemId}.${problem.title}` }}
            </n-h3>
            <n-space size="small">
              <n-tag type="success" size="small" round :bordered="false">
                <template #icon>
                  <n-icon :component="HelpRound" />
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
              :theme="theme"
              style="margin-top: 12px" />
          </n-scrollbar>
        </n-tab-pane>
        <n-tab-pane
          v-if="isLoggedIn"
          name="solutions"
          tab="提交记录"
          display-directive="show"
          style="height: calc(100% - 54px)">
          <n-scrollbar>
            <n-h3 style="margin-bottom: 6px">
              {{ `${problem.problemId}.${problem.title}` }}
            </n-h3>
            <solution-single :problem-id="pid" />
          </n-scrollbar>
        </n-tab-pane>
      </n-tabs>
      <!-- 代码编辑器 -->
      <div>
        <code-editor
          :value="code"
          :theme="theme"
          :loading="disableSubmit"
          :available-languages="problem.languages"
          @submit="submitClick" />
      </div>
    </div>
  </div>
  <n-modal
    v-model:show="showResult"
    :auto-focus="false"
    :mask-closable="false"
    preset="card"
    style="width: 600px; margin-top: 200px">
    <result-dialog :submit-time="submitTime" style="margin-bottom: 12px" />
  </n-modal>
</template>

<script setup lang="ts">
import { ContestApi, JudgeApi, ProblemApi } from "@/api/request"
import { ErrorMessage, Problem, type SubmitData } from "@/api/type"
import { CodeEditor, MarkdownView } from "@/components"
import { useStore } from "@/store"
import { type SourceCode } from "@/type"
import { setTitle } from "@/utils"
import {
  DataSaverOffRound,
  HelpRound,
  PrintRound,
  TimerOutlined
} from "@vicons/material"
import _ from "lodash"
import {
  NH3,
  NIcon,
  NModal,
  NScrollbar,
  NSpace,
  NTabPane,
  NTabs,
  NTag,
  useMessage
} from "naive-ui"
import { computed, inject, onBeforeMount, ref } from "vue"
import ResultDialog from "./ResultDialog.vue"
import Skeleton from "./Skeleton.vue"
import SolutionSingle from "./Solutions.vue"

const store = useStore()
const message = useMessage()

const props = withDefaults(defineProps<{ pid: string; cid: string | null }>(), {
  cid: null
})

const loading = ref<boolean>(true)
const showResult = ref<boolean>(false)
const disableSubmit = ref<boolean>(false)
const problem = ref<Problem>(new Problem())
const code = ref<string>("")
const submitTime = ref<number>(0)

const theme = inject("themeStr") as "light" | "dark"
const isLoggedIn = computed(() => store.user.isLoggedIn)

let problemId: number | null = null
let contestId: number | null = null

const submitClick = _.throttle(submit, 1000)

onBeforeMount(() => {
  const reg = /^\d+$/

  if (props.cid != null && reg.test(props.cid)) {
    contestId = Number(props.cid)
  }

  if (reg.test(props.pid)) {
    problemId = Number(props.pid)
    queryProblem()
  } else {
    store.app.setError({
      status: 404,
      error: "Not Found",
      message: "找不到题目"
    })
  }
})

/**
 * 获取题目数据
 */
function queryProblem() {
  if (contestId == null) {
    // 非竞赛题目
    ProblemApi.getSingle(problemId!)
      .then((data) => {
        setTitle(data.title)
        problem.value = data
      })
      .catch((err: ErrorMessage) => {
        store.app.setError(err)
      })
      .finally(() => {
        loading.value = false
      })
  } else {
    // 竞赛题目
    ContestApi.getProblem(contestId, problemId!)
      .then((data) => {
        setTitle(data.title)
        problem.value = data
      })
      .catch((err: ErrorMessage) => {
        store.app.setError(err)
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
  if (!store.user.isLoggedIn) {
    message.warning("你必须先登录才能提交代码！")
  }

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
    uid: store.user.userInfo!.uid!
  }

  JudgeApi.submit(submitData)
    .then((time) => {
      submitTime.value = time
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

<style scoped lang="scss">
.submission {
  height: calc(
    100vh - var(--layout-padding) *
      4 - var(--header-height) - var(--footer-height)
  );
  width: calc(100% - var(--layout-padding) * 4);
  padding: calc(var(--layout-padding) * 2);
  overflow: hidden;

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

    :deep(.n-scrollbar) {
      .n-scrollbar-content {
        display: flex;
        flex-direction: column;
        min-height: 100%;
      }
    }
  }
}
</style>
