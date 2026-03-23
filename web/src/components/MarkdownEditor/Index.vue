<template>
  <div class="markdown-editor">
    <toolbar style="margin-bottom: 2px" @click="toolbarClick" />
    <div ref="editor" class="editor" />
  </div>
</template>

<script setup lang="tsx">
import { ApiPath } from "@/api"
import { closeBrackets } from "@codemirror/autocomplete"
import { indentWithTab } from "@codemirror/commands"
import { markdown } from "@codemirror/lang-markdown"
import { Compartment, type Extension } from "@codemirror/state"
import {
  EditorView,
  highlightActiveLine,
  keymap,
  lineNumbers
} from "@codemirror/view"
import { githubDark } from "@fsegurai/codemirror-theme-github-dark"
import { githubLight } from "@fsegurai/codemirror-theme-github-light"
import { ArchiveRound as ArchiveIcon } from "@vicons/material"
import { debounce } from "lodash-es"
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
import Toolbar from "./Toolbar.vue"

let cmView: EditorView
const action = ApiPath.PROBLEM_IMAGE

const dialog = useDialog()

const debouncedUpdate = debounce((doc) => {
  modelValue.value = doc
}, 300)

const readOnlyCompartment = new Compartment()
const themeCompartment = new Compartment()
const cmExtensions: Extension = [
  [closeBrackets(), highlightActiveLine(), lineNumbers()],
  markdown(),
  themeCompartment.of(githubLight),
  keymap.of([indentWithTab]),
  EditorView.lineWrapping,
  EditorView.updateListener.of((update) => {
    debouncedUpdate(update.state.doc.toString())
  })
]

const editor = ref<HTMLDivElement | null>(null)

const modelValue = defineModel<string>({ default: "" })
const props = withDefaults(
  defineProps<{
    readOnly: boolean
    theme: "light" | "dark"
    headers: Record<string, string>
  }>(),
  {
    readOnly: false
  }
)

const fileCount = ref<number>(0)
const uploaded = ref<boolean>(false)
const uploadRef = ref<UploadInst | null>(null)

watch(
  () => props.readOnly,
  (val) => {
    nextTick(() => {
      cmView.dispatch({
        effects: readOnlyCompartment.reconfigure(EditorView.editable.of(val))
      })
    })
  }
)

watch(
  () => props.theme,
  (val) => {
    const t = val === "light" ? githubLight : githubDark
    nextTick(() => {
      cmView.dispatch({
        effects: themeCompartment.reconfigure(t)
      })
    })
  },
  { immediate: true }
)

watch(modelValue, (val) => {
  // 内外数据相等时不更新 CodeMirror 编辑器
  if (cmView.state.doc.toString() !== val) {
    nextTick(() => {
      cmView.dispatch({
        changes: {
          from: 0,
          to: cmView.state.doc.length,
          insert: val
        }
      })
    })
  }
})

onMounted(() => {
  cmView = new EditorView({
    doc: modelValue.value,
    parent: editor.value!,
    extensions: cmExtensions
  })
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
      addBlock(":::info", ":::")
      break
    case "warning":
      addBlock(":::warning", ":::")
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

function hasSelected(): boolean | undefined {
  return cmView.state.selection.ranges.some((r) => !r.empty)
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

  let { anchor, head } = cmView.state.selection.main

  if (!onlyLeft) {
    cmView.dispatch({
      changes: { from: anchor, to: anchor, insert: symbol },
      selection: { anchor: anchor + symbol.length }
    })
  } else {
    const headLine = cmView.state.doc.lineAt(head)
    if (headLine.from !== head) {
      // 只在左边插入说明是块级元素，不在行首另起一行
      cmView.dispatch({
        changes: { from: head, to: head, insert: "\n\n" },
        selection: { anchor: head + 2 }
      })

      head += 2
    }
  }

  cmView.dispatch({
    changes: { from: head, to: head, insert: symbol },
    selection: { anchor: head + symbol.length }
  })
  head = head + symbol.length
  cmView.focus()
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

  let head = cmView.state.selection.main.head
  const headLine = cmView.state.doc.lineAt(head)

  if (headLine.from !== head) {
    // 不在行首，另起一行
    cmView.dispatch({
      changes: { from: head, to: head, insert: "\n\n" },
      selection: { anchor: head + 2 }
    })

    head = cmView.state.selection.main.anchor
  }

  cmView.dispatch({
    changes: { from: head, to: head, insert: `${start}\n\n${end}` },
    selection: { anchor: head + start.length + 1 } // 光标移到中间空行
  })
  cmView.focus()
}

function addImage(link: boolean) {
  if (link) {
    addSymbol("![]()", true)
    const head = cmView.state.selection.main.head
    cmView.dispatch({
      selection: { anchor: head - 1 }
    })
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
  dialog.destroyAll()
}
</script>

<style scoped lang="scss">
.markdown-editor {
  flex: 1;
  display: flex;
  flex-direction: column;

  .editor {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  :deep(.cm-editor) {
    flex: 1;
  }
}
</style>
