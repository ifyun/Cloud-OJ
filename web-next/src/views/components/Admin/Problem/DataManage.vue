<template>
  <div class="test-data-div">
    <n-card :bordered="false" style="height: 100%"
            content-style="height: 100%; display: flex; flex-direction: column">
      <n-page-header :subtitle="subtitle" @back="back">
        <template #title>{{ title }}</template>
        <template #extra>
          <n-button type="primary" round>
            <template #icon>
              <n-icon>
                <upload-icon/>
              </n-icon>
            </template>
            上传文件
          </n-button>
        </template>
        <n-data-table :loading="loading" :columns="tableColumns" :data="testData"/>
      </n-page-header>
    </n-card>
  </div>
</template>

<script lang="tsx">
import {useStore} from "vuex"
import {useRoute, useRouter} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NPageHeader,
  NSpace,
  useMessage
} from "naive-ui"
import {
  DeleteForeverRound as DeleteIcon,
  FileDownloadOutlined as DownloadIcon,
  UploadFileRound as UploadIcon
} from "@vicons/material"
import Mutations from "@/store/mutations"
import {ProblemApi} from "@/api/request"
import {ErrorMsg, TestData} from "@/api/type"

@Options({
  name: "DataManage",
  components: {
    NCard,
    NButton,
    NIcon,
    NPageHeader,
    NDataTable,
    UploadIcon
  }
})
export default class DataManage extends Vue {
  private route = useRoute()
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private loading: boolean = false
  private title = "测试数据管理"
  private subtitle: string = ""
  private testData: Array<TestData> = []

  private tableColumns = [
    {
      title: "#",
      align: "right",
      width: 50,
      render: (row: TestData, rowIndex: number) => (<span>{rowIndex + 1}</span>)
    },
    {
      title: "文件名",
      align: "left",
      render: (row: TestData) => (<span>{row.fileName}</span>)
    },
    {
      title: "文件长度",
      align: "right",
      render: (row: TestData) => (<span>{row.size} 字节</span>)
    },
    {
      title: "操作",
      align: "center",
      width: 240,
      render: () => (
          <NSpace size="small" justify="center">
            <NButton size="small" type="primary" secondary={true}>
              {{
                icon: () => <NIcon><DownloadIcon/></NIcon>,
                default: () => <span>下载</span>
              }}
            </NButton>
            <NButton size="small" type="error" secondary={true}>
              {{
                icon: () => <NIcon><DeleteIcon/></NIcon>,
                default: () => <span>删除</span>
              }}
            </NButton>
          </NSpace>
      )
    }
  ]

  get userInfo() {
    return this.store.state.userInfo
  }

  beforeMount() {
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
        this.subtitle = `${id} - ${p.title}`
        this.queryData(problemId)
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
</style>
