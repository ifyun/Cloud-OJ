<template>
  <div class="code-editor">
    <n-input-group>
      <n-input-group-label :style="{ width: '110px' }">
        选择语言
      </n-input-group-label>
      <n-select
        v-model:value="language"
        :options="languageOptions"
        :render-label="renderLabel" />
      <n-button type="primary" secondary :loading="loading" @click="submit">
        <template #icon>
          <n-icon>
            <send-round />
          </n-icon>
        </template>
        提交运行
      </n-button>
    </n-input-group>
    <div class="editor-wrapper" :class="theme">
      <textarea ref="editor" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { LanguageOption, SourceCode } from "@/type"
import { LanguageOptions } from "@/type"
import { LanguageUtil } from "@/utils"
import { SendRound } from "@vicons/material"
import CodeMirror, { type Editor, type EditorConfiguration } from "codemirror"
import "codemirror/addon/edit/closebrackets.js"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/clike/clike.js"
import "codemirror/mode/go/go.js"
import "codemirror/mode/javascript/javascript.js"
import "codemirror/mode/python/python.js"
import "codemirror/mode/shell/shell.js"
import "codemirror/mode/sql/sql.js"
import "codemirror/theme/material-darker.css"
import "codemirror/theme/ttcn.css"
import {
  NButton,
  NIcon,
  NInputGroup,
  NInputGroupLabel,
  NSelect
} from "naive-ui"
import { nextTick, onMounted, ref, watch } from "vue"

// CodeMirror 语言模式
const Modes = [
  "text/x-csrc",
  "text/x-c++src",
  "text/x-java",
  "text/x-python",
  "text/x-sh",
  "text/x-csharp",
  "text/javascript",
  "text/x-kotlin",
  "text/x-go"
]

const renderLabel = (option: LanguageOption) => {
  return [option.label]
}

const cmOptions = ref<EditorConfiguration>({
  mode: Modes[0],
  tabSize: 4,
  smartIndent: true,
  indentUnit: 4,
  lineNumbers: true,
  matchBrackets: true,
  autoCloseBrackets: true,
  scrollbarStyle: "overlay"
})

const language = ref<number>(0) // 当前选中的语言ID
const languageOptions = ref<Array<LanguageOption>>(LanguageOptions)
const editor = ref<HTMLTextAreaElement | null>(null)

let cmEditor: Editor | null = null

const props = withDefaults(
  defineProps<{
    value: string
    loading: boolean
    availableLanguages: number | null // 可用语言，未指定时使用所有语言
    theme: "light" | "dark"
  }>(),
  {
    loading: false,
    value: "",
    availableLanguages: null
  }
)

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void

  (e: "submit", value: SourceCode): void
}>()

watch(
  () => props.availableLanguages,
  (val) => {
    if (val == null) {
      return
    }

    const languageArray = LanguageUtil.toArray(val)
    languageOptions.value = []
    languageArray.forEach((v) => {
      languageOptions.value.push(LanguageOptions[v])
    })
  },
  { immediate: true }
)

watch(
  () => props.theme,
  (val) => {
    if (val === "light") {
      cmOptions.value.theme = "ttcn"
    } else {
      cmOptions.value.theme = "material-darker"
    }
  },
  { immediate: true }
)

watch(language, (val) => {
  cmOptions.value.mode = Modes[val]
})

watch(
  cmOptions,
  (val) => {
    nextTick(() => {
      cmEditor!.setOption("mode", val.mode)
      cmEditor!.setOption("theme", val.theme)
    })
  },
  { deep: true, immediate: true }
)

watch(
  () => props.value,
  (val) => {
    nextTick(() => {
      cmEditor!.setValue(val)
    })
  }
)

onMounted(() => {
  cmEditor = CodeMirror.fromTextArea(editor.value!, cmOptions.value)
  cmEditor.setValue(props.value)
})

function submit() {
  emit("submit", { language: language.value, code: cmEditor!.getValue() })
}
</script>

<style scoped lang="scss">
.code-editor {
  display: flex;
  flex-direction: column;
  height: 100%;

  .editor-wrapper {
    margin-top: 5px;
    height: calc(100% - 39px);
  }

  .editor-wrapper {
    :deep(.CodeMirror) {
      height: 100%;
      font-size: 14px;
      font-family: v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace;
    }

    &.dark {
      :deep(.CodeMirror-overlayscroll-horizontal div),
      :deep(.CodeMirror-overlayscroll-vertical div) {
        width: 5px;
        background-color: #5c6065;
      }
    }
  }
}
</style>
