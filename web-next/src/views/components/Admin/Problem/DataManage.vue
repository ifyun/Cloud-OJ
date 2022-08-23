<template>
  <div class="test-data-div">
    <n-card
      :bordered="false"
      style="height: 100%"
      content-style="height: 100%; display: flex; flex-direction: column">
      <n-page-header @back="back">
        <template #title>{{ title }}</template>
        <n-space vertical size="large">
          <n-data-table
            max-height="350"
            :loading="loading"
            :columns="columns"
            :data="testData" />
          <n-alert type="warning" title="注意换行符">
            文件中的换行符必须为 LF，不能使用 CRLF。
          </n-alert>
          <n-upload
            action="/api/file/test_data"
            multiple
            :headers="headers"
            :data="uploadData"
            accept=".in,.out"
            :disabled="disableUpload"
            @before-upload="beforeUpload"
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
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NAlert,
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NP,
  NPageHeader,
  NPopconfirm,
  NSpace,
  NText,
  NUpload,
  NUploadDragger,
  useMessage
} from "naive-ui"
import {
  ArchiveRound as ArchiveIcon,
  DeleteForeverRound as DeleteIcon,
  FileDownloadOutlined as DownloadIcon
} from "@vicons/material"
import axios from "axios"
import Mutations from "@/store/mutations"
import { ProblemApi } from "@/api/request"
import { ErrorMsg, Problem, TestData } from "@/api/type"

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
    render: (row) => <span>{row.size} 字节</span>
  },
  {
    title: "操作",
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
        <NPopconfirm>
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
            action: () => (
              <NButton
                size="small"
                type="error"
                ghost={true}
                onClick={() => deleteFile(row.fileName)}>
                确认
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
    token: userInfo.value.token
  }
})

const uploadData = computed(() => {
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
  const breadcrumb = ["题目管理", "测试数据管理"]
  store.commit(Mutations.SET_BREADCRUMB, breadcrumb)

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
      .catch((error: ErrorMsg) => {
        message.error(error.toString())
        loading.value = false
      })
  }
})

function queryData(id: number) {
  ProblemApi.getTestData(id, userInfo.value)
    .then((data) => {
      testData.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      loading.value = false
    })
}

function back() {
  router.back()
}

function beforeUpload({ file }: any) {
  const fileName = file.name as string
  return fileName.endsWith(".in") || fileName.endsWith(".out")
}

function handleUploadFinish({ file }: any) {
  message.success(`${file.name} 已上传`)
  queryData(problem.value!.problemId!)
}

function downloadFile(fileName: string) {
  const url = `/api/file/test_data/download/${problem.value!
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
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      queryData(problem.value!.problemId!)
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
