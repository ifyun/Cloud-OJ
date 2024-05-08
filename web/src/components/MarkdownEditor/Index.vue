<!-- Markdown 编辑器 -->
<template>
  <div class="markdown-editor">
    <markdown-toolbar
      style="margin-bottom: 2px"
      @click="toolbarClick"
      @insert-table="insertTable" />
    <textarea ref="editor" />
  </div>
</template>

<script setup lang="tsx">
import { ApiPath } from "@/api"
import { ArchiveRound as ArchiveIcon } from "@vicons/material"
import CodeMirror, { type Editor, type EditorConfiguration } from "codemirror"
import "codemirror/addon/scroll/simplescrollbars"
import "codemirror/addon/scroll/simplescrollbars.css"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/markdown/markdown.js"
import "codemirror/theme/ayu-dark.css"
import "codemirror/theme/juejin.css"
import "codemirror/theme/material-darker.css"
import debounce from "lodash/debounce"
import {
  NIcon,
  NText,
  NUpload,
  NUploadDragger,
  type UploadFileInfo,
  type UploadInst,
  useDialog
} from "naive-ui"
import { nextTick, onMounted, ref, watch } from "vue"
import MarkdownToolbar from "./Toolbar.vue"

let cmEditor: Editor | null = null
const action = ApiPath.PROBLEM_IMAGE

const dialog = useDialog()

const cmOptions: EditorConfiguration = {
  mode: {
    name: "text/x-markdown",
    highlightFormatting: true
  },
  scrollbarStyle: "simple",
  lineNumbers: true,
  lineWrapping: true,
  tabSize: 4
}

const editor = ref<HTMLTextAreaElement | null>(null)

const props = withDefaults(
  defineProps<{
    modelValue: string
    readOnly: boolean
    theme: "light" | "dark"
    headers: Record<string, string>
  }>(),
  {
    modelValue: "",
    readOnly: false
  }
)

const emit = defineEmits<{
  // eslint-disable-next-line no-unused-vars
  (e: "update:modelValue", value: string): void
}>()

const fileCount = ref<number>(0)
const uploaded = ref<boolean>(false)
const uploadRef = ref<UploadInst | null>(null)

watch(
  () => props.readOnly,
  (val) => {
    nextTick(() => cmEditor!.setOption("readOnly", val))
  }
)

watch(
  () => props.theme,
  (val) => {
    nextTick(() => {
      if (val === "light") {
        cmEditor!.setOption("theme", "juejin")
      } else {
        cmEditor!.setOption("theme", "ayu-dark")
      }
    })
  },
  { immediate: true }
)

watch(
  () => props.modelValue,
  (val) => {
    // 内外数据相等时不更新 CodeMirror 编辑器
    if (cmEditor!.getValue() !== val) {
      nextTick(() => {
        cmEditor!.setValue(val)
      })
    }
  }
)

onMounted(() => {
  cmEditor = CodeMirror.fromTextArea(editor.value!, cmOptions)
  cmEditor.setValue(props.modelValue)
  cmEditor.on(
    "change",
    debounce((cm: Editor) => {
      // 改变内部数据，触发 emit
      emit("update:modelValue", cm.getValue())
    }, 250)
  )
})

function toolbarClick(key: string) {
  switch (key) {
    case "italic":
      addSymbol("*")
      break
    case "bold":
      addSymbol("**")
      break
    case "quote":
      addSymbol("> ", true)
      break
    case "info":
      addBlock("::: info", ":::")
      break
    case "warning":
      addBlock("::: warning", ":::")
      break
    case "code":
      addBlock("```", "```")
      break
    case "ul":
      addSymbol("- ", true)
      break
    case "ol":
      addSymbol("1. ", true)
      break
    case "img_link":
      addImage(true)
      break
    case "img_upload":
      addImage(false)
      break
    default:
      return
  }
}

/**
 * 插入表格
 * @param value {cols, rows}
 */
function insertTable(value: any) {
  addTable(value.cols, value.rows)
}

function hasSelected(): boolean | undefined {
  return cmEditor?.somethingSelected()
}

/**
 * 插入 markdown 符号
 * @param symbol 符号字符串
 * @param onlyLeft 是否只在左边插入
 */
function addSymbol(symbol: string, onlyLeft = false) {
  if (hasSelected()) {
    return
  }

  const { anchor, head } = cmEditor!.listSelections()[0]!

  if (!onlyLeft) {
    cmEditor?.setCursor(anchor)
    cmEditor?.replaceSelection(symbol)
  } else if (head.ch != 0) {
    // 只在左边插入说明是块级元素，不在行首另起一行
    cmEditor?.replaceSelection("\n\n")
    head.line += 2
    head.ch = 0
  }

  cmEditor?.setCursor(head)
  cmEditor?.replaceSelection(symbol)
  head.ch += symbol.length
  cmEditor?.setCursor(head) // 光标移动到符号中间（右侧）
  cmEditor?.focus()
}

/**
 * 插入块级 markdown 符号
 * @param start 起始符号
 * @param end 结束符号
 */
function addBlock(start: string, end: string) {
  if (hasSelected()) {
    return
  }

  const head = cmEditor?.getCursor()!

  if (head.ch != 0) {
    // 不在行首，另起一行
    cmEditor?.replaceSelection("\n\n")
    head.line += 2
    head.ch = 0
  }

  cmEditor?.setCursor(head)
  cmEditor?.replaceSelection(`${start}\n\n${end}`)
  head.line += 1
  cmEditor?.setCursor(head)
  cmEditor?.focus()
}

/**
 * 插入表格
 * @param cols 列数
 * @param rows 行数
 */
function addTable(cols: number, rows: number) {
  if (hasSelected()) {
    return
  }

  const headCell = "| Name\t\t"
  const dividerCell = "|:---------:"
  const bodyCell = "|\t\t\t"

  const header: Array<string> = []
  const divider: Array<string> = []
  const body: Array<string> = []
  const bodyRows: Array<string> = []

  for (let i = 0; i < cols; i += 1) {
    header.push(headCell)
    divider.push(dividerCell)
    body.push(bodyCell)
  }

  header.push("|")
  divider.push("|")
  body.push("|")

  for (let i = 0; i < rows; i += 1) {
    bodyRows.push(body.join(""))
    bodyRows.push("\n")
  }

  const content = `${header.join("")}\n${divider.join("")}\n${bodyRows.join(
    ""
  )}`
  const head = cmEditor?.getCursor()!

  if (head.ch != 0) {
    // 不在行首，另起一行
    cmEditor?.replaceSelection("\n\n")
    head.line += 2
    head.ch = 0
  }

  cmEditor?.setCursor(head)
  cmEditor?.replaceSelection(content)
  head.line += rows + 2
  cmEditor?.setCursor(head)
  cmEditor?.focus()
}

function addImage(link: boolean) {
  if (link) {
    addSymbol("![]()", true)
    const head = cmEditor?.getCursor()!
    head.ch -= 1
    cmEditor?.setCursor(head)
  } else {
    uploaded.value = false
    fileCount.value = 0
    dialog.create({
      title: "上传图片",
      showIcon: false,
      maskClosable: false,
      autoFocus: false,
      content: () => (
        <NUpload
          ref={uploadRef}
          action={action}
          headers={props.headers}
          showRetryButton={false}
          accept=".jpg,.png,.svg,.gif"
          onChange={fileListChange}
          onFinish={uploadFinish}>
          <NUploadDragger>
            <div style="margin-bottom: 12px">
              <NIcon size="48" depth="3">
                <ArchiveIcon />
              </NIcon>
            </div>
            <NText>点击或者拖动文件到该区域来上传</NText>
          </NUploadDragger>
        </NUpload>
      )
    })
  }
}

function fileListChange(options: { fileList: UploadFileInfo[] }) {
  fileCount.value = options.fileList.length
}

function uploadFinish(options: {
  file: UploadFileInfo
  event?: ProgressEvent
}) {
  uploaded.value = true
  let name = (options.event?.target as XMLHttpRequest).response
  addSymbol(`![](${name})`, true)
}
</script>

<style lang="scss">
.markdown-editor {
  height: 100%;

  .CodeMirror {
    font-size: 14px;
    font-family: Consolas, Monaco, monospace;
    height: calc(100% - 30px);

    &.cm-s-elegant {
      border-right: 1px solid #f7f7f7;

      .CodeMirror-gutters {
        border-right: none;
      }
    }

    &.cm-s-ayu-dark {
      .CodeMirror-scrollbar-filler {
        background-color: transparent;
      }

      .CodeMirror-simplescroll-horizontal {
        background: transparent;

        div {
          border: none;
          background-color: #28282c;
        }
      }

      .CodeMirror-simplescroll-vertical {
        background: transparent;

        div {
          border: none;
          background-color: #28282c;
        }
      }
    }
  }
}
</style>
