<template>
  <div class="test-data-div">
    <div style="margin: 4px">
      <n-page-header @back="back">
        <template #title>{{ title }}</template>
        <n-space vertical size="large">
          <n-alert type="info" :bordered="false">
            文件中的换行符必须为 LF，不能使用 CRLF
          </n-alert>
          <n-data-table
            max-height="350"
            :loading="loading"
            :columns="columns"
            :data="testData" />
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
                <n-icon size="48" depth="3">
                  <archive-icon />
                </n-icon>
              </div>
              <n-text style="font-size: 16px">
                点击或拖动文件到该区域上传
              </n-text>
              <n-p depth="3">
                文件类型为 .in 和 .out，每个 .in 文件对应一个 .out 文件
              </n-p>
            </n-upload-dragger>
          </n-upload>
        </n-space>
      </n-page-header>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { ApiPath, ProblemApi } from "@/api/request"
import { ErrorMessage, Problem, TestData } from "@/api/type"
import { setTitle } from "@/utils"
import {
  ArchiveRound as ArchiveIcon,
  DeleteForeverRound as DeleteIcon,
  FileDownloadOutlined as DownloadIcon
} from "@vicons/material"
import axios from "axios"
import {
  DataTableColumns,
  NAlert,
  NButton,
  NDataTable,
  NIcon,
  NP,
  NPageHeader,
  NPopconfirm,
  NSpace,
  NText,
  NUpload,
  NUploadDragger,
  UploadFileInfo,
  useMessage
} from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "vuex"

const action = ApiPath.TEST_DATA

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const loading = ref<boolean>(false)
const problem = ref<Problem | null>(null)
const testData = ref<Array<TestData>>([])

const columns: DataTableColumns<TestData> = [
  {
    title: "#",
    key: "#",
    align: "right",
    width: 50,
    render: (row, rowIndex: number) => <span>{rowIndex + 1}</span>
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
        type = "warning"
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
    render: (row) => <span>{row.size} 字节</span>
  },
  {
    title: "",
    key: "operation",
    align: "center",
    width: 240,
    render: (row) => (
      <NSpace size="small" justify="center">
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
      </NSpace>
    )
  }
]

const title = computed(() => {
  if (problem.value == null) {
    return ""
  } else {
    return `${problem.value.problemId}. ${problem.value.title}`
  }
})

const userInfo = computed(() => {
  return store.state.userInfo
})

const headers = computed(() => {
  return {
    userId: userInfo.value.userId,
    Authorization: `Baerer ${userInfo.value.token}`
  }
})

const uploadData = computed<any>(() => {
  if (problem.value == null) {
    return {}
  } else {
    return {
      problemId: problem.value?.problemId
    }
  }
})

const disableUpload = computed<boolean>(() => {
  return loading.value || problem.value == null
})

onBeforeMount(() => {
  setTitle(route.meta.title as string)

  const reg = /^\d+$/
  const id = route.params.id.toString()

  if (reg.test(id)) {
    loading.value = true
    const problemId = Number(id)
    ProblemApi.getSingle(problemId, userInfo.value)
      .then((p) => {
        problem.value = p
        queryData(p.problemId!)
      })
      .catch((err: ErrorMessage) => {
        message.error(err.toString())
        loading.value = false
      })
  }
})

function queryData(id: number) {
  ProblemApi.getTestData(id, userInfo.value)
    .then((data) => {
      testData.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      loading.value = false
    })
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
  queryData(problem.value!.problemId!)
}

function downloadFile(fileName: string) {
  const url = `${ApiPath.TEST_DATA}/download/${problem.value!
    .problemId!}/${fileName}`
  const anchor = document.createElement("a")
  axios
    .get(url, { headers: headers.value, responseType: "blob" })
    .then((res) => {
      const objectUrl = window.URL.createObjectURL(res.data)
      anchor.href = objectUrl
      anchor.download = fileName
      anchor.click()
      window.URL.revokeObjectURL(objectUrl)
    })
    .catch((error) => {
      message.error(error.toString())
    })
}

function deleteFile(fileName: string) {
  ProblemApi.deleteTestData(problem.value!.problemId!, fileName, userInfo.value)
    .then(() => {
      message.warning(`${fileName} 已删除`)
      queryData(problem.value!.problemId!)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>

<style scoped>
.test-data-div {
  height: calc(100% - var(--layout-padding));
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex-direction: column;
}
</style>

<style lang="scss">
.test-data-div {
  .n-upload-trigger {
    width: 100%;
  }
}
</style>
