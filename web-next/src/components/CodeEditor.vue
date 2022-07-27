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
      <n-button type="primary" @click="submit" :loading="loading">
        <template #icon>
          <n-icon>
            <submit-icon />
          </n-icon>
        </template>
        提交运行
      </n-button>
    </n-input-group>
    <div class="editor-wrapper">
      <textarea ref="editor" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from "vue"
import {
  NButton,
  NIcon,
  NInputGroup,
  NInputGroupLabel,
  NSelect
} from "naive-ui"
import { PlayCircleRound as SubmitIcon } from "@vicons/material"
import { SourceCode, LanguageOption, LanguageOptions } from "@/type"
import { LanguageUtil } from "@/utils"
import CodeMirror, { Editor, EditorConfiguration } from "codemirror"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/clike/clike.js"
import "codemirror/mode/go/go.js"
import "codemirror/mode/python/python.js"
import "codemirror/mode/shell/shell.js"
import "codemirror/mode/javascript/javascript.js"
import "codemirror/mode/sql/sql.js"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import "codemirror/theme/material-darker.css"

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

const cmOptions: EditorConfiguration = {
  mode: Modes[0],
  theme: "material-darker",
  tabSize: 4,
  smartIndent: true,
  indentUnit: 4,
  lineNumbers: true,
  matchBrackets: true,
  autoCloseBrackets: true
}

const language = ref<number>(0) // 当前选中的语言ID
const languageOptions = ref<Array<LanguageOption>>(LanguageOptions)
const editor = ref<HTMLTextAreaElement | null>(null)

let cmEditor: Editor | null = null

const props = withDefaults(
  defineProps<{
    loading: boolean
    value: string
    availableLanguages: number | null // 可用语言，未指定时使用所有语言
  }>(),
  {
    loading: false,
    value: "",
    availableLanguages: null
  }
)

const emit = defineEmits<{
  // eslint-disable-next-line no-unused-vars
  (e: "update:modelValue", value: string): void
  // eslint-disable-next-line no-unused-vars
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

watch(language, (val) => {
  cmOptions.mode = Modes[val]
})

watch(
  () => cmOptions,
  (val) => {
    nextTick(() => {
      cmEditor!.setOption("mode", val.mode)
      cmEditor!.setOption("theme", val.theme)
    })
  }
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
  cmEditor = CodeMirror.fromTextArea(editor.value!, cmOptions)
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
    flex: auto;
  }
}
</style>

<style lang="scss">
.editor-wrapper {
  .CodeMirror {
    height: 100%;
    font-size: 14px;
    font-family: v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace;
  }
}
</style>
