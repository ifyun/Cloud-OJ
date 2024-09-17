<template>
  <div class="test-data-div">
    <div style="margin: 4px">
      <n-page-header @back="back">
        <template #title>
          <n-text>
            {{ title }}
          </n-text>
        </template>
        <template #extra>
          <n-tag v-if="isSPJ" type="info" round>
            <template #icon>
              <n-icon :component="VerifiedRound" />
            </template>
            Special Judge
          </n-tag>
        </template>
        <n-flex vertical size="large">
          <n-data-table
            v-if="problemData != null"
            max-height="350"
            :loading="loading"
            :columns="columns"
            :data="problemData.testData" />
          <n-flex align="center" justify="end">
            <n-button
              size="small"
              type="success"
              :loading="loading"
              :disabled="disableSaveConf"
              @click="saveScoreConf">
              <template #icon>
                <n-icon :component="SaveRound" />
              </template>
              保存分数配置
            </n-button>
          </n-flex>
          <n-upload
            multiple
            :action="action"
            :headers="headers"
            :data="uploadData"
            :disabled="disableUpload"
            accept=".in,.out"
            @before-upload="beforeUpload"
            @error="handleError"
            @finish="handleUploadFinish">
            <n-upload-dragger style="width: 100%">
              <div>
                <n-icon size="48" depth="3" :component="UnarchiveRound" />
              </div>
              <n-text style="font-size: 16px">
                点击或拖动文件到该区域上传
              </n-text>
              <n-p depth="3">
                文件类型为 .in 和 .out，每个 .in 文件对应一个 .out 文件<br />
                使用 SPJ 且不需要输出文件？请上传空的 .out 文件
              </n-p>
            </n-upload-dragger>
          </n-upload>
          <n-alert type="warning" :bordered="false">
            换行符必须使用 LF，不能使用 CRLF
          </n-alert>
        </n-flex>
        <n-divider title-placement="left">
          Special Judge ({{ isSPJ ? "已启用" : "未启用" }})
        </n-divider>
        <div id="spj-editor">
          <textarea ref="editor" />
          <n-button-group size="small" style="margin-top: 12px">
            <n-button secondary type="primary" @click="saveSPJ">
              提交编译
            </n-button>
            <n-button
              secondary
              size="small"
              type="error"
              :disabled="!isSPJ"
              @click="removeSPJ">
              移除 SPJ
            </n-button>
          </n-button-group>
        </div>
      </n-page-header>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { ApiPath } from "@/api"
import { ProblemApi } from "@/api/request"
import { ErrorMessage, ProblemData, TestData } from "@/api/type"
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import {
  DeleteForeverRound as DeleteIcon,
  FileDownloadOutlined as DownloadIcon,
  SaveRound,
  UnarchiveRound,
  VerifiedRound
} from "@vicons/material"
import CodeMirror, { type Editor, type EditorConfiguration } from "codemirror"
import "codemirror/addon/edit/closebrackets.js"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/clike/clike.js"
import "codemirror/theme/material-darker.css"
import "codemirror/theme/ttcn.css"
import {
  type DataTableColumns,
  NAlert,
  NButton,
  NButtonGroup,
  NDataTable,
  NDivider,
  NFlex,
  NIcon,
  NInputNumber,
  NP,
  NPageHeader,
  NPopconfirm,
  NTag,
  NText,
  NUpload,
  NUploadDragger,
  type UploadFileInfo,
  useDialog,
  useMessage
} from "naive-ui"
import { computed, nextTick, onBeforeMount, onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import SPJDeclare from "./spj.cpp?raw"

let cmEditor: Editor | null = null

const editor = ref<HTMLTextAreaElement | null>(null)
const action = ApiPath.TEST_DATA

const store = useStore()
const route = useRoute()
const router = useRouter()
const dialog = useDialog()
const message = useMessage()

const loading = ref<boolean>(false)
const problemData = ref<ProblemData | null>(null)

const cmOptions = ref<EditorConfiguration>({
  mode: "text/x-c++src",
  tabSize: 4,
  smartIndent: true,
  indentUnit: 4,
  lineNumbers: true,
  matchBrackets: true,
  autoCloseBrackets: true,
  scrollbarStyle: "overlay"
})

const columns: DataTableColumns<TestData> = [
  {
    title: "#",
    key: "#",
    align: "right",
    width: 50,
    render: (_, rowIndex: number) => <span>{rowIndex + 1}</span>
  },
  {
    title: "文件名",
    key: "fileName",
    align: "left",
    render: (row) => {
      let type = ""
      if (row.fileName.endsWith(".in")) {
        type = "info"
      } else if (row.fileName.endsWith(".out")) {
        type = "success"
      }
      return (
        <NText strong={true} type={type}>
          {row.fileName}
        </NText>
      )
    }
  },
  {
    title: "文件长度",
    key: "size",
    align: "right",
    render: (row) => <NText>{row.size} 字节</NText>
  },
  {
    title: "测试点分数",
    key: "score",
    align: "right",
    width: 200,
    render: (row) => {
      if (row.fileName.endsWith(".out")) {
        return (
          <NFlex justify="end">
            <NInputNumber
              value={row.score}
              onUpdateValue={(val) => {
                row.score = val === null ? 1 : val
              }}
              min="1"
              precision={0}
              buttonPlacement="both"
              style="width: 120px"
            />
          </NFlex>
        )
      }
    }
  },
  {
    title: "",
    key: "operation",
    align: "right",
    width: 180,
    render: (row) => (
      <NFlex size="small" justify="end">
        <NButton
          size="small"
          type="primary"
          secondary={true}
          onClick={() => downloadFile(row.fileName)}>
          {{
            icon: () => (
              <NIcon>
                <DownloadIcon />
              </NIcon>
            ),
            default: () => <span>下载</span>
          }}
        </NButton>
        <NPopconfirm onPositiveClick={() => deleteFile(row.fileName)}>
          {{
            trigger: () => (
              <NButton size="small" type="error" secondary={true}>
                {{
                  icon: () => (
                    <NIcon>
                      <DeleteIcon />
                    </NIcon>
                  ),
                  default: () => <span>删除</span>
                }}
              </NButton>
            ),
            default: () => <span>确定要删除吗？</span>
          }}
        </NPopconfirm>
      </NFlex>
    )
  }
]

const isSPJ = computed(() => {
  if (problemData.value == null) {
    return false
  }

  return problemData.value.spj
})

const title = computed(() => {
  if (problemData.value == null) {
    return ""
  }

  return `${problemData.value.pid}. ${problemData.value.title}`
})

// upload token
const headers = computed(() => {
  return {
    Authorization: `Baerer ${store.user.userInfo!.token}`
  }
})

const uploadData = computed<any>(() => {
  if (problemData.value == null) {
    return {}
  } else {
    return {
      pid: problemData.value.pid
    }
  }
})

const disableUpload = computed<boolean>(() => {
  return loading.value || problemData.value == null
})

const disableSaveConf = computed<boolean>(() => {
  return loading.value || problemData.value?.testData.length === 0
})

watch(
  () => store.app.theme,
  (val) => {
    nextTick(() => {
      val == null
        ? cmEditor!.setOption("theme", "ttcn")
        : cmEditor!.setOption("theme", "material-darker")
    })
  },
  { immediate: true }
)

onBeforeMount(() => {
  setTitle(route.meta.title as string)
})

onMounted(() => {
  const reg = /^\d+$/
  const id = route.params.id.toString()
  cmEditor = CodeMirror.fromTextArea(editor.value!, cmOptions.value)

  if (reg.test(id)) {
    queryData(Number(id))
  }
})

function queryData(id: number) {
  loading.value = true
  ProblemApi.getProblemData(id)
    .then((data) => {
      problemData.value = data
      nextTick(() => {
        data.spj
          ? cmEditor!.setValue(data.SPJSource)
          : cmEditor!.setValue(SPJDeclare)
      })
    })
    .catch((err: ErrorMessage) => message.error(err.toString()))
    .finally(() => (loading.value = false))
}

function back() {
  router.back()
}

async function beforeUpload(options: { file: UploadFileInfo }) {
  const fileName = options.file.name
  return fileName.endsWith(".in") || fileName.endsWith(".out")
}

function handleError(options: { event?: ProgressEvent }) {
  const res = (options.event?.target as XMLHttpRequest).response
  if (res) {
    const err = ErrorMessage.from(JSON.parse(res))
    message.error(err.toString())
  }
}

function handleUploadFinish(options: { file: UploadFileInfo }) {
  message.success(`${options.file.name} 已上传`)
  reloadData()
}

function downloadFile(fileName: string) {
  ProblemApi.downloadData(problemData.value!.pid!, fileName).catch(
    (err: ErrorMessage) => message.error(err.toString())
  )
}

function deleteFile(fileName: string) {
  ProblemApi.deleteTestData(problemData.value!.pid!, fileName)
    .then(() => {
      message.warning(`${fileName} 已删除`)
      reloadData()
    })
    .catch((err: ErrorMessage) => message.error(err.toString()))
}

function saveSPJ() {
  const source = cmEditor!.getValue()

  if (source.trim().length === 0) {
    return
  }

  ProblemApi.saveSPJ(problemData.value!.pid!, source)
    .then(() => {
      message.success("SPJ 已编译")
      reloadData()
    })
    .catch((err) => message.error(err.toString()))
}

function removeSPJ() {
  const d = dialog.warning({
    title: "移除 SPJ",
    content: "此操作将删除 SPJ 源代码和动态库，继续吗？",
    positiveText: "确认",
    negativeText: "不要",
    onPositiveClick: () => {
      d.loading = true
      ProblemApi.deleteSPJ(problemData.value!.pid!)
        .then(() => message.warning("SPJ 已移除"))
        .catch((err) => message.error(err.toString()))
        .finally(() => {
          d.loading = false
          reloadData()
        })
    }
  })
}

function reloadData() {
  queryData(problemData.value!.pid!)
}

function saveScoreConf() {
  loading.value = true
  const conf: { [key: string]: number } = {}
  problemData.value!.testData.forEach((value) => {
    if (value.fileName.endsWith(".out")) {
      conf[value.fileName] = value.score
    }
  })

  ProblemApi.saveDataConf(problemData.value!.pid!, conf)
    .then(() => {
      message.success("分数配置已保存")
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => [(loading.value = false)])
}
</script>

<style scoped lang="scss">
.test-data-div {
  height: calc(100% - var(--layout-padding));
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex-direction: column;
}

#spj-editor {
  margin-bottom: 24px;

  :deep(.CodeMirror) {
    height: 100%;
    font-size: 14px;
    font-family: v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace;
  }
}
</style>
