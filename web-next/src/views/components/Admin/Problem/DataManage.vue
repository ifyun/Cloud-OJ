<template>
  <div class="test-data-div">
    <n-card :bordered="false" style="height: 100%"
            content-style="height: 100%; display: flex; flex-direction: column">
      <n-page-header :subtitle="subtitle" @back="back">
        <template #title>{{ title }}</template>
        <n-space vertical size="large">
          <n-data-table max-height="350" :loading="loading" :columns="columns" :data="testData"/>
          <n-alert type="warning" title="注意换行符">
            文件中的换行符必须为 LF，不能使用 CRLF。
          </n-alert>
          <n-upload action="/api/file/test_data" multiple :headers="headers" :data="uploadData" accept=".in,.out"
                    :disabled="disableUpload" @beforeUpload="beforeUpload" @finish="handleUploadFinish">
            <n-upload-dragger style="width: 100%">
              <div>
                <n-icon size="48" depth="3">
                  <archive-icon/>
                </n-icon>
              </div>
              <n-text style="font-size: 16px">点击或拖动文件到该区域上传</n-text>
              <n-p depth="3">文件类型为 .in 和 .out，每个 .in 文件对应一个 .out 文件</n-p>
            </n-upload-dragger>
          </n-upload>
        </n-space>
      </n-page-header>
    </n-card>
  </div>
</template>

<script lang="tsx">
import {useStore} from "vuex"
import {useRoute, useRouter} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {
  NAlert,
  NBreadcrumb,
  NBreadcrumbItem,
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
import {ProblemApi} from "@/api/request"
import {ErrorMsg, Problem, TestData} from "@/api/type"

let self: any

@Options({
  name: "DataManage",
  components: {
    NCard,
    NSpace,
    NButton,
    NPageHeader,
    NDataTable,
    NUpload,
    NUploadDragger,
    NAlert,
    NText,
    NP,
    NIcon,
    ArchiveIcon
  }
})
export default class DataManage extends Vue {
  private route = useRoute()
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private loading: boolean = false
  private title = "测试数据管理"
  private problem: Problem | null = null
  private testData: Array<TestData> = []

  private columns = [
    {
      title: "#",
      align: "right",
      width: 50,
      render: (row: TestData, rowIndex: number) => <span>{rowIndex + 1}</span>
    },
    {
      title: "文件名",
      align: "left",
      render: (row: TestData) => {
        let type = ""
        if (row.fileName.endsWith(".in")) {
          type = "info"
        } else if (row.fileName.endsWith(".out")) {
          type = "success"
        }
        return <NText strong={true} type={type}>{row.fileName}</NText>
      }
    },
    {
      title: "文件长度",
      align: "right",
      render: (row: TestData) => <span>{row.size} 字节</span>
    },
    {
      title: "操作",
      align: "center",
      width: 240,
      render: (row: TestData) =>
          <NSpace size="small" justify="center">
            <NButton size="small" type="primary" secondary={true} onClick={() => self.downloadFile(row.fileName)}>
              {{
                icon: () => <NIcon><DownloadIcon/></NIcon>,
                default: () => <span>下载</span>
              }}
            </NButton>
            <NPopconfirm>
              {{
                trigger: () =>
                    <NButton size="small" type="error" secondary={true}>
                      {{
                        icon: () => <NIcon><DeleteIcon/></NIcon>,
                        default: () => <span>删除</span>
                      }}
                    </NButton>,
                action: () => <NButton size="small" type="error" ghost={true}
                                       onClick={() => self.deleteFile(row.fileName)}>确认</NButton>,
                default: () => <span>确定要删除吗？</span>
              }}
            </NPopconfirm>
          </NSpace>
    }
  ]

  get subtitle() {
    if (this.problem == null) {
      return ""
    } else {
      return `${this.problem.problemId} - ${this.problem.title}`
    }
  }

  get userInfo() {
    return this.store.state.userInfo
  }

  get headers() {
    return {
      "userId": this.userInfo.userId,
      "token": this.userInfo.token
    }
  }

  get uploadData() {
    if (this.problem == null) {
      return {}
    } else {
      return {
        "problemId": this.problem?.problemId
      }
    }
  }

  get disableUpload(): boolean {
    return this.loading || this.problem == null
  }

  beforeMount() {
    self = this
    const vNode =
        (<NBreadcrumb>
          <NBreadcrumbItem>题目管理</NBreadcrumbItem>
          <NBreadcrumbItem>{this.title}</NBreadcrumbItem>
        </NBreadcrumb>)
    this.store.commit(Mutations.SET_BREADCRUMB, vNode)

    const reg = /^[\d]+$/
    const id = this.route.params.id.toString()

    if (reg.test(id)) {
      this.loading = true
      const problemId = Number(id)
      ProblemApi.getSingle(
          problemId,
          this.userInfo
      ).then(p => {
        this.problem = p
        this.queryData(p.problemId!)
      }).catch((error: ErrorMsg) => {
        this.message.error(error.toString())
        this.loading = false
      })
    }
  }

  queryData(id: number) {
    ProblemApi.getTestData(
        id,
        this.userInfo
    ).then(data => {
      this.testData = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.loading = false
    })
  }

  back() {
    this.router.back()
  }

  beforeUpload({file}: any) {
    const fileName = file.name as string
    return fileName.endsWith(".in") || fileName.endsWith(".out")
  }

  handleUploadFinish({file}: any) {
    this.message.success(`${file.name} 已上传`)
    this.queryData(this.problem!.problemId!)
  }

  downloadFile(fileName: string) {
    const url = `/api/file/test_data/${this.problem!.problemId!}/${fileName}`
    const anchor = document.createElement("a")
    axios.get(url, {headers: this.headers, responseType: "blob"})
        .then(res => {
          const objectUrl = window.URL.createObjectURL(res.data)
          anchor.href = objectUrl
          anchor.download = fileName
          anchor.click()
          window.URL.revokeObjectURL(objectUrl)
        })
        .catch((error) => {
          this.message.error(error.toString())
        })
  }

  deleteFile(fileName: string) {
    ProblemApi.deleteTestData(
        this.problem!.problemId!,
        fileName,
        this.userInfo
    ).then(() => {
      this.message.warning(`${fileName} 已删除`)
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.queryData(this.problem!.problemId!)
    })
  }
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
