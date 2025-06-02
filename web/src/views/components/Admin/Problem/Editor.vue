<template>
  <div class="problem-editor">
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
            <n-form-item-grid-item label="题目分数" path="score" :span="1">
              <n-tooltip>
                <template #trigger>
                  <n-input-number
                    :value="problem.score"
                    :show-button="false"
                    disabled>
                    <template #suffix>分</template>
                  </n-input-number>
                </template>
                分数在测试数据页面设置
              </n-tooltip>
            </n-form-item-grid-item>
          </n-grid>
          <n-form-item label="分类/标签" path="tags">
            <n-dynamic-tags v-model:value="problem.tags" type="primary" />
          </n-form-item>
        </n-form>
      </n-spin>
    </n-space>
    <!-- 题目内容编辑器 -->
    <div class="editor-area">
      <markdown-editor
        v-model="problem.description"
        :read-only="loading"
        :theme="theme"
        :headers="headers" />
      <div>
        <markdown-view
          :content="problem.description"
          :theme="theme"
          style="
            margin-top: 30px;
            padding: 0 8px 0 8px;
            overflow: auto;
            max-height: calc(100% - 30px);
          " />
      </div>
    </div>
  </div>
  <n-drawer v-model:show="showHelp" :width="750" placement="right">
    <n-drawer-content
      title="Markdown 帮助"
      closable
      body-content-style="padding: 0">
      <n-scrollbar>
        <markdown-view
          :content="helpDoc"
          :theme="theme"
          style="padding: 20px" />
      </n-scrollbar>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup lang="tsx">
import { ProblemApi } from "@/api/request"
import { ErrorMessage, Problem } from "@/api/type"
import { MarkdownEditor, MarkdownView } from "@/components"
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import {
  HelpOutlineRound as HelpIcon,
  SaveOutlined as SaveIcon
} from "@vicons/material"
import {
  type FormRules,
  NButton,
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
  NTooltip,
  useMessage
} from "naive-ui"
import { computed, inject, onBeforeUnmount, onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import MarkdownHelp from "./help.md?raw"

const problemForm = ref<HTMLFormElement | null>(null)

const store = useStore()
const route = useRoute()
const router = useRouter()
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
  timeout: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: number): Error | boolean {
      if (value < 100 || value > 10000) {
        return new Error("请输入时间限制")
      }
      return true
    }
  },
  memoryLimit: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: number): Error | boolean {
      if (value < 16 || value > 256) {
        return new Error("请输入内存限制")
      }
      return true
    }
  },
  outputLimit: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: number): Error | boolean {
      if (value < 1 || value > 128) {
        return new Error("请输入输出限制")
      }
      return true
    }
  }
}

const theme = inject("themeStr") as "light" | "dark"

const headers = computed(() => {
  return {
    Authorization: `Baerer ${store.user.userInfo!.token}`
  }
})

const helpDoc = computed(() => {
  return `\`\`\`\`markdown\n${MarkdownHelp}\n\`\`\`\`\n${MarkdownHelp}`
})

const title = computed<string>(() => {
  if (create.value) {
    return route.meta._title as string
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
  setTitle(route.meta.title as string)
  document
    .querySelector(".admin .n-scrollbar-content")
    ?.classList.add("layout-max-height")
  const reg = /^\d+$/
  const id = route.params.id.toString()

  if (id === "new") {
    create.value = true
  } else if (reg.test(id)) {
    loading.value = true
    queryProblem(Number(id))
  }
})

onBeforeUnmount(() => {
  // 移除最大高度样式，避免其他页面不显示滚动条
  document
    .querySelector(".admin .n-scrollbar-content")
    ?.classList.remove("layout-max-height")
})

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
  ProblemApi.getSingle(problemId)
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
  ProblemApi.save(problem.value, create.value)
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
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex: 1;
  flex-direction: column;

  .page-header {
    margin-bottom: 12px;
  }

  .editor-area {
    display: flex;
    flex-direction: row;
    flex: 1;

    & > * {
      flex: 1;
      margin-left: 12px;
      height: auto;

      &:first-child {
        margin-left: 0;
      }
    }
  }
}
</style>
