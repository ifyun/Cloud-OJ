<template>
  <div class="problem-editor">
    <n-card
      :bordered="false"
      style="height: 100%"
      content-style="height: 100%; display: flex; flex-direction: column">
      <n-page-header class="page-header" @back="back">
        <template #title>{{ title }}</template>
        <template #extra>
          <n-space size="small">
            <n-button type="primary" size="small" @click="handleSave">
              <template #icon>
                <save-icon />
              </template>
              保存
            </n-button>
            <n-button type="info" size="small" ghost @click="toggleHelp">
              <template #icon>
                <help-icon />
              </template>
              帮助
            </n-button>
          </n-space>
        </template>
      </n-page-header>
      <!-- 表单 -->
      <n-space class="editor-from" vertical size="small">
        <n-spin :show="loading">
          <n-form
            ref="problemForm"
            :model="problem"
            label-placement="left"
            :rules="rules">
            <n-grid :cols="2" :x-gap="12">
              <n-form-item-grid-item label="题目名称" path="title" :span="1">
                <n-input
                  v-model:value="problem.title"
                  maxlength="16"
                  show-count
                  clearable />
              </n-form-item-grid-item>
            </n-grid>
            <n-grid :cols="4" :x-gap="12">
              <n-form-item-grid-item label="题目分数" path="score" :span="1">
                <n-input-number
                  v-model:value="problem.score"
                  :min="0"
                  :max="100"
                  :show-button="false"
                  placeholder="0 ~ 100">
                  <template #suffix>分</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item label="时间限制" path="timeout" :span="1">
                <n-input-number
                  v-model:value="problem.timeout"
                  :show-button="false"
                  :min="100"
                  :max="10000"
                  placeholder="100 ~ 10000">
                  <template #suffix>毫秒</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item
                label="内存限制"
                path="memoryLimit"
                :span="1">
                <n-input-number
                  v-model:value="problem.memoryLimit"
                  :show-button="false"
                  :min="16"
                  :max="256"
                  placeholder="16 ~ 256">
                  <template #suffix>MB</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item
                label="输出限制"
                path="outputLimit"
                :span="1">
                <n-input-number
                  v-model:value="problem.outputLimit"
                  :show-button="false"
                  :min="1"
                  :max="128"
                  placeholder="1 ~ 128">
                  <template #suffix>MB</template>
                </n-input-number>
              </n-form-item-grid-item>
            </n-grid>
            <n-form-item label="分类/标签" path="tags">
              <n-dynamic-tags v-model:value="problem.tags" type="primary" />
            </n-form-item>
          </n-form>
        </n-spin>
      </n-space>
      <!-- 题目内容编辑器 -->
      <div class="editor-markdown">
        <div>
          <markdown-editor v-model="problem.description" :read-only="loading" />
        </div>
        <div>
          <n-scrollbar style="max-height: 100%">
            <markdown-view
              :content="problem.description"
              style="margin: 30px 0 12px 0" />
          </n-scrollbar>
        </div>
      </div>
    </n-card>
  </div>
  <n-drawer v-model:show="showHelp" :width="750" placement="right">
    <n-drawer-content :native-scrollbar="false" body-content-style="padding: 0">
      <markdown-view :content="helpDoc" style="padding: 24px" />
    </n-drawer-content>
  </n-drawer>
</template>

<script setup lang="tsx">
import { computed, onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "vuex"
import {
  FormRules,
  NButton,
  NCard,
  NDrawer,
  NDrawerContent,
  NDynamicTags,
  NForm,
  NFormItem,
  NFormItemGridItem,
  NGrid,
  NInput,
  NInputNumber,
  NPageHeader,
  NScrollbar,
  NSpace,
  NSpin,
  useMessage
} from "naive-ui"
import {
  HelpOutlineRound as HelpIcon,
  SaveOutlined as SaveIcon
} from "@vicons/material"
import { MarkdownEditor, MarkdownView } from "@/components"
import { ErrorMessage, Problem, UserInfo } from "@/api/type"
import { ProblemApi } from "@/api/request"
import { setTitle } from "@/utils"
import Mutations from "@/store/mutations"
import MarkdownHelp from "./help.md?raw"

const problemForm = ref<HTMLFormElement | null>(null)

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const problem = ref<Problem>(new Problem())
const showHelp = ref<boolean>(false)
const loading = ref<boolean>(false)
const create = ref<boolean>(false)

/* 表单验证规则 */
const rules: FormRules = {
  title: {
    required: true,
    trigger: ["blur", "input"],
    message: "请输入题目名称"
  },
  type: {
    required: true
  },
  score: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: number): Error | boolean {
      if (value < 0 || value > 100) {
        return new Error("请输入分数")
      }
      return true
    }
  },
  timeout: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: number): Error | boolean {
      if (value < 100 || value > 10000) {
        return new Error("请输入时间限制")
      }
      return true
    }
  },
  memoryLimit: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: number): Error | boolean {
      if (value < 16 || value > 256) {
        return new Error("请输入内存限制")
      }
      return true
    }
  },
  outputLimit: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: number): Error | boolean {
      if (value < 1 || value > 128) {
        return new Error("请输入输出限制")
      }
      return true
    }
  }
}

const helpDoc = computed(() => {
  return `\`\`\`\`markdown\n${MarkdownHelp}\n\`\`\`\`\n${MarkdownHelp}`
})

const userInfo = computed<UserInfo>(() => {
  return store.state.userInfo
})

const title = computed<string>(() => {
  if (create.value) {
    return "创建题目"
  }

  if (typeof problem.value.problemId === "undefined") {
    return ""
  } else {
    return `${problem.value.problemId}. ${problem.value.title}`
  }
})

watch(title, (value) => {
  setTitle(value)
})

watch(
  () => problem.value.tags,
  (value) => {
    problem.value.category = value.join(",")
  }
)

onMounted(() => {
  const reg = /^\d+$/
  const id = route.params.id.toString()

  if (id === "new") {
    create.value = true
  } else if (reg.test(id)) {
    loading.value = true
    queryProblem(Number(id))
  }

  setBreadcrumb()
})

function setBreadcrumb() {
  const breadcrumb = ["题目管理", create.value ? "创建题目" : "编辑题目"]
  store.commit(Mutations.SET_BREADCRUMB, breadcrumb)
}

function back() {
  router.back()
}

/**
 * 显示/隐藏帮助
 */
function toggleHelp() {
  showHelp.value = !showHelp.value
}

function queryProblem(problemId: number) {
  ProblemApi.getSingle(problemId, userInfo.value)
    .then((data) => {
      if (data.category.length > 0) {
        data.tags = data.category.split(",")
      } else {
        data.tags = []
      }
      problem.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      loading.value = false
    })
}

function handleSave() {
  if (problem.value.description.trim().length === 0) {
    message.warning("请输入题目内容！")
    return
  }
  problemForm.value?.validate((errors: any) => {
    if (!errors) {
      save()
    }
  })
}

/**
 * 保存
 */
function save() {
  loading.value = true
  ProblemApi.save(problem.value, userInfo.value, create.value)
    .then(() => {
      message.success(`${problem.value.title} 已保存`)
      create.value && back()
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped lang="scss">
.problem-editor {
  min-height: 850px;
  height: calc(100% - var(--layout-padding));
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex-direction: column;

  .page-header {
    margin-bottom: 12px;
  }

  .editor-markdown {
    margin-top: -12px;
    display: flex;
    flex-direction: row;
    flex: 1;
    overflow: hidden;

    & > * {
      flex: 1;
      margin-left: 12px;
      height: 100%;

      &:first-child {
        margin-left: 0;
      }
    }
  }
}
</style>
