<template>
  <div class="code-editor">
    <n-input-group>
      <n-input-group-label :style="{ width: '110px' }"
      >选择语言
      </n-input-group-label
      >
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

<script lang="ts">
import { markRaw } from "vue"
import { Options, Vue } from "vue-class-component"
import { Emit, Prop, Watch } from "vue-property-decorator"
import {
  NSpace,
  NButton,
  NInput,
  NInputGroup,
  NInputGroupLabel,
  NSelect,
  NIcon
} from "naive-ui"
import { PlayCircleRound as SubmitIcon } from "@vicons/material"
import { LanguageOption, LanguageOptions } from "@/type"
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

@Options({
  name: "CodeEditor",
  components: {
    NSpace,
    NInputGroup,
    NInputGroupLabel,
    NSelect,
    NButton,
    NInput,
    NIcon,
    SubmitIcon
  }
})
export default class CodeEditor extends Vue {
  private renderLabel = (option: LanguageOption) => {
    return [option.label]
  }

  private cmEditor?: Editor | null

  private cmOptions: EditorConfiguration = {
    mode: Modes[0],
    theme: "material-darker",
    tabSize: 4,
    smartIndent: true,
    indentUnit: 4,
    lineNumbers: true,
    matchBrackets: true,
    autoCloseBrackets: true
  }

  private languageOptions: Array<LanguageOption> = LanguageOptions
  private language = 0 // 当前选中的语言ID

  @Prop({ type: Boolean, default: false })
  private loading?: boolean

  @Prop(String)
  private modelValue = ""

  @Prop(Number)
  private availableLanguages?: number | null // 可用语言，未指定时使用所有语言

  @Watch("availableLanguage", { immediate: true })
  availableLanguageChange(value: number) {
    if (typeof value === "undefined") {
      return
    }

    this.languageOptions = []
    const languageArray = LanguageUtil.toArray(value)
    languageArray.forEach((v) => {
      this.languageOptions.push(LanguageOptions[v])
    })
  }

  @Watch("language")
  languageChange(value: number) {
    this.cmOptions.mode = Modes[value]
  }

  @Watch("cmOptions", { deep: true })
  cmOptionsChange(value: EditorConfiguration) {
    this.cmEditor?.setOption("mode", value.mode)
    this.cmEditor?.setOption("theme", value.theme)
  }

  @Emit("update:modelValue")
  codeChange(value: string) {
    return value
  }

  @Emit("submit")
  submit() {
    return this.language
  }

  mounted() {
    this.cmEditor = CodeMirror.fromTextArea(
      this.$refs.editor as HTMLTextAreaElement,
      this.cmOptions
    )
    this.cmEditor = markRaw(this.cmEditor)
    this.cmEditor.setValue(this.modelValue)
    this.cmEditor.on("change", (cm: Editor) => {
      this.codeChange(cm.getValue())
    })
  }
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
