<!-- Markdown 编辑器 -->
<template>
  <div class="markdown-editor">
    <toolbar @click="toolbarClick" @insertTable="insertTable" style="margin-bottom: 2px"/>
    <textarea ref="editor"/>
  </div>
</template>

<script lang="ts">
import {markRaw} from "vue"
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {Emit, Prop, Watch} from "vue-property-decorator"
import CodeMirror, {
  Editor,
  EditorConfiguration
} from "codemirror"
import "codemirror/lib/codemirror.css"
import "codemirror/theme/ayu-dark.css"
import "codemirror/theme/darcula.css"
import "codemirror/theme/elegant.css"
import "codemirror/addon/scroll/simplescrollbars.css"
import "codemirror/addon/scroll/simplescrollbars"
import "codemirror/mode/markdown/markdown.js"
import Toolbar from "./Toolbar.vue"

@Options({
  name: "MarkdownEditor",
  components: {
    Toolbar
  }
})
export default class MarkdownEditor extends Vue {
  private store = useStore()

  private firstLoad: boolean = true
  private cmEditor?: Editor | null
  private cmOptions: EditorConfiguration = {
    mode: {
      name: "text/x-markdown",
      highlightFormatting: true
    },
    scrollbarStyle: "simple",
    lineNumbers: true,
    lineWrapping: true,
    tabSize: 4,
  }

  get theme(): any {
    return this.store.state.theme
  }

  @Prop(String)
  private modelValue?: string

  @Prop(Boolean)
  private readOnly: boolean = false

  @Watch("readOnly")
  readOnlyChange(value: boolean) {
    this.cmEditor?.setOption("readOnly", value)
  }

  @Watch("theme")
  themeChange(value: any) {
    if (value == null) {
      this.cmEditor?.setOption("theme", "elegant")
    } else {
      this.cmEditor?.setOption("theme", "ayu-dark")
    }
  }

  @Watch("modelValue")
  valueChange(value: string) {
    if (this.firstLoad) {
      this.cmEditor?.setValue(value)
      this.firstLoad = false
    }
  }

  @Emit("update:modelValue")
  updateValue(value: string) {
    return value
  }

  mounted() {
    this.cmEditor = CodeMirror.fromTextArea(this.$refs.editor as HTMLTextAreaElement, this.cmOptions)
    this.cmEditor = markRaw(this.cmEditor)
    this.cmEditor.setValue(this.modelValue!)
    this.cmEditor.on("change", (cm: Editor) => {
      this.updateValue(cm.getValue())
    })
    this.themeChange(this.theme)
  }

  toolbarClick(key: string) {
    switch (key) {
      case "italic":
        this.addSymbol("*")
        break
      case "bold":
        this.addSymbol("**")
        break
      case "quote":
        this.addSymbol("> ", true)
        break
      case "info":
        this.addBlock("::: info", ":::")
        break
      case "warning":
        this.addBlock("::: warning", ":::")
        break
      case "code":
        this.addBlock("```", "```")
        break
      case "ul":
        this.addSymbol("- ", true)
        break
      case "ol":
        this.addSymbol("1. ", true)
        break
      default:
        return
    }
  }

  insertTable(value: any) {
    console.debug("add table.", value)
    this.addTable(value.cols, value.rows)
  }

  hasSelected(): boolean | undefined {
    return this.cmEditor?.somethingSelected()
  }

  /**
   * 插入 markdown 符号
   * @param symbol 符号字符串
   * @param onlyLeft 是否只在左边插入
   */
  addSymbol(symbol: string, onlyLeft: boolean = false) {
    if (this.hasSelected()) {
      return
    }

    const {anchor, head} = this.cmEditor?.listSelections()[0]!

    if (!onlyLeft) {
      this.cmEditor?.setCursor(anchor)
      this.cmEditor?.replaceSelection(symbol)
    } else if (head.ch != 0) {
      // 只在左边插入说明是块级元素，不在行首另起一行
      this.cmEditor?.replaceSelection("\n\n")
      head.line += 2
      head.ch = 0
    }

    this.cmEditor?.setCursor(head)
    this.cmEditor?.replaceSelection(symbol)
    head.ch += symbol.length
    this.cmEditor?.setCursor(head)  // 光标移动到符号中间（右侧）
    this.cmEditor?.focus()
  }

  /**
   * 插入块级 markdown 符号
   * @param start 起始符号
   * @param end 结束符号
   */
  addBlock(start: string, end: string) {
    if (this.hasSelected()) {
      return
    }

    const head = this.cmEditor?.getCursor()!

    if (head.ch != 0) {
      // 不在行首，另起一行
      this.cmEditor?.replaceSelection("\n\n")
      head.line += 2
      head.ch = 0
    }

    this.cmEditor?.setCursor(head)
    this.cmEditor?.replaceSelection(`${start}\n\n${end}`)
    head.line += 1
    this.cmEditor?.setCursor(head)
    this.cmEditor?.focus()
  }

  /**
   * 插入表格
   * @param cols 列数
   * @param rows 行数
   */
  addTable(cols: number, rows: number) {
    if (this.hasSelected()) {
      return
    }

    const headCell = "| Name\t\t"
    const dividerCell = "|:---------:"
    const bodyCell = "|\t\t\t"

    let header = []
    let divider = []
    let body = []

    for (let i = 0; i < cols; i += 1) {
      header.push(headCell)
      divider.push(dividerCell)
      body.push(bodyCell)
    }

    header.push("|")
    divider.push("|")
    body.push("|")

    let bodyRows = []

    for (let i = 0; i < rows; i += 1) {
      bodyRows.push(body.join(""))
      bodyRows.push("\n")
    }

    const content = `${header.join("")}\n${divider.join("")}\n${bodyRows.join("")}`
    const head = this.cmEditor?.getCursor()!

    if (head.ch != 0) {
      // 不在行首，另起一行
      this.cmEditor?.replaceSelection("\n\n")
      head.line += 2
      head.ch = 0
    }

    this.cmEditor?.setCursor(head)
    this.cmEditor?.replaceSelection(content)
    head.line += rows + 2
    this.cmEditor?.setCursor(head)
    this.cmEditor?.focus()
  }
}
</script>

<style lang="scss">
.markdown-editor {
  height: 100%;

  .cm-s-elegant {
    border-right: 1px solid #F7F7F7;

    .CodeMirror-gutters {
      border-right: none;
    }
  }

  .CodeMirror {
    font-size: 14px;
    font-family: Consolas, Monaco, monospace;
    height: calc(100% - 30px);

    &.cm-s-ayu-dark {
      .CodeMirror-scrollbar-filler {
        background-color: transparent;
      }

      .CodeMirror-simplescroll-horizontal {
        background: transparent;

        div {
          border: none;
          background-color: #28282C;
        }
      }

      .CodeMirror-simplescroll-vertical {
        background: transparent;

        div {
          border: none;
          background-color: #28282C;
        }
      }
    }
  }
}
</style>
