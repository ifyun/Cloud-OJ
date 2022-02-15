<!-- Problem Admin -->
<template>
  <div class="problem-table">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space align="center" justify="space-between">
          <n-space align="center">
            <n-input-group>
              <n-input v-model:value="keyword" maxlength="10" show-count clearable placeholder="输入题目名称、分类">
                <template #prefix>
                  <n-icon class="input-prefix-icon">
                    <search-icon/>
                  </n-icon>
                </template>
              </n-input>
              <n-button type="primary" @click="search">
                <template #icon>
                  <n-icon>
                    <manage-search-round/>
                  </n-icon>
                </template>
                搜索题目
              </n-button>
            </n-input-group>
            <n-tag v-if="keywordTag != null" closable @close="clearKeyword">{{ keywordTag }}</n-tag>
          </n-space>
          <n-space align="center" justify="end">
            <n-button-group>
              <n-button type="primary">
                <template #icon>
                  <n-icon>
                    <add-icon/>
                  </n-icon>
                </template>
                <router-link :to="{name: 'edit_problem', params: {id: 'new'}}">
                  创建题目
                </router-link>
              </n-button>
            </n-button-group>
          </n-space>
        </n-space>
        <n-data-table :row-props="rowProps" :columns="problemColumns" :data="problems.data"
                      :loading="pagination.loading"/>
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                      :item-count="problems.count" @update:page="pageChange">
          <template #prefix="{itemCount}">
            共 {{ itemCount }} 项
          </template>
        </n-pagination>
      </n-space>
    </n-card>
  </div>
  <n-dropdown trigger="manual" :show-arrow="true" :x="point.x" :y="point.y" :options="operations"
              :show="showOperations" @select="operationSelect" :on-clickoutside="hideOperation"/>
</template>

<script lang="tsx">
import {nextTick} from "vue"
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {RouterLink, useRouter} from "vue-router"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NButtonGroup,
  NCard,
  NDataTable,
  NDropdown,
  NFormItem,
  NGrid,
  NGridItem,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSpace,
  NSwitch,
  NTag,
  useDialog,
  useMessage
} from "naive-ui"
import {FileArchive as FileIcon, Search as SearchIcon, Tags as TagsIcon} from "@vicons/fa"
import {
  ManageSearchRound,
  DeleteForeverRound as DelIcon,
  EditNoteRound as EditIcon,
  PostAddRound as AddIcon
} from "@vicons/material"
import {ErrorMsg, PagedData, Problem, UserInfo} from "@/api/type"
import {renderIcon, setTitle, TagUtil} from "@/utils"
import {ProblemApi} from "@/api/request"
import Mutations from "@/store/mutations";

let selectedId: number | undefined
let selectedTitle: string | undefined

@Options({
  name: "ProblemAdmin",
  components: {
    NCard,
    NSpace,
    NGrid,
    NGridItem,
    NFormItem,
    NInputGroup,
    NButtonGroup,
    NInput,
    NButton,
    NTag,
    NIcon,
    NDataTable,
    NPagination,
    NDropdown,
    SearchIcon,
    ManageSearchRound,
    TagsIcon,
    AddIcon
  }
})
export default class ProblemAdmin extends Vue {
  private store = useStore()
  private router = useRouter()
  private message = useMessage()
  private dialog = useDialog()

  private pagination = {
    page: 1,
    pageSize: 20,
    loading: true
  }

  private problems: PagedData<Problem> = {
    data: [],
    count: 0
  }

  private keyword: string = ""
  private keywordTag: string | null = null
  private confirmDelete: string = ""

  private showOperations: boolean = false
  private point = {
    x: 0,
    y: 0
  }

  /* 表格行 ContextMenu */
  private rowProps = (row: Problem) => {
    return {
      onContextmenu: (e: PointerEvent) => {
        if ((e.target as Element).tagName !== "TD") {
          return
        }
        e.preventDefault()
        this.showOperations = false
        nextTick().then(() => {
          selectedId = row.problemId
          selectedTitle = row.title
          this.showOperations = true
          this.point = {
            x: e.clientX,
            y: e.clientY
          }
        })
      },
    }
  }

  /* 操作（右键菜单） */
  private operations = [
    {
      label: "编辑",
      key: "edit",
      icon: renderIcon(EditIcon)
    },
    {
      label: "管理测试数据",
      key: "test_data",
      icon: renderIcon(FileIcon, "#18A058")
    },
    {
      label: "删除",
      key: "del",
      icon: renderIcon(DelIcon, "#F17C67")
    }
  ]

  // region 表格列选项
  private problemColumns = [
    {
      title: "ID",
      key: "problemId",
      align: "right",
      width: 70
    },
    {
      title: "题目名称",
      key: "title",
      render: (row: Problem) => (
          <NButton text>
            <RouterLink to={{name: "submission", query: {problemId: row.problemId}}}>
              {row.title}
            </RouterLink>
          </NButton>
      )
    },
    {
      title: () => (
          <NSpace size="small" justify="start" align="center">
            <NIcon style="display: flex">
              <TagsIcon/>
            </NIcon>
            <span>分类</span>
          </NSpace>
      ),
      render: (row: Problem) => {
        if (row.category === "") {
          return ""
        }
        const tags = row.category.split(",")
        return tags.map((tag) => (
            <NTag class="tag" size="small" color={TagUtil.getColor(tag, this.theme)}>{tag}</NTag>
        ))
      }
    },
    {
      title: "分数",
      align: "right",
      key: "score"
    },
    {
      title: "是否开放",
      align: "center",
      width: 120,
      render: (row: Problem) => (
          <NSwitch value={row.enable} onUpdateValue={value => this.toggleIsEnable(row, value)}>
            {{checked: () => "开放", unchecked: () => "关闭"}}
          </NSwitch>
      )
    }
  ]

  // endregion

  get theme() {
    return this.store.state.theme
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    setTitle("题目管理")
    this.store.commit(Mutations.SET_BREADCRUMB,
        <NBreadcrumb>
          <NBreadcrumbItem>题目管理</NBreadcrumbItem>
        </NBreadcrumb>
    )

    if ("page" in this.$route.query) {
      this.pagination.page = Number(this.$route.query.page)
    }

    if ("keyword" in this.$route.query) {
      this.keyword = String(this.$route.query.keyword)
      this.keywordTag = this.keyword
    }

    this.queryProblems()
  }

  /**
   * 关闭操作菜单
   */
  hideOperation() {
    this.showOperations = false
  }

  /**
   * 操作菜单选择
   */
  operationSelect(key: string) {
    this.hideOperation()
    switch (key) {
      case "edit":
        this.router.push({name: "edit_problem", params: {id: selectedId}})
        break
      case "test_data":
        this.router.push({name: "test_data", params: {id: selectedId}})
        break
      case "del":
        this.deleteProblem()
        break
      default:
        return
    }
  }

  pageChange(page: number) {
    this.router.push({
      query: {page}
    })
  }

  clearKeyword() {
    this.router.push({
      query: {}
    })
  }

  search() {
    if (this.keyword !== "") {
      this.router.push({
        query: {keyword: this.keyword}
      })
    }
  }

  deleteProblem() {
    const d = this.dialog.error({
      title: "删除题目",
      showIcon: false,
      content: () => (
          <NFormItem label="输入题目名称确认" style="margin-top: 24px">
            <NInput placeholder={selectedTitle} value={this.confirmDelete}
                    onUpdateValue={value => this.confirmDelete = value}/>
          </NFormItem>
      ),
      negativeText: "取消",
      positiveText: "删除",
      onPositiveClick: () => {
        if (this.confirmDelete !== selectedTitle) {
          this.message.error("请输入题目名称")
          return false
        }
        d.loading = true
        ProblemApi
            .delete(selectedId!, this.userInfo)
            .then(() => {
              this.message.warning(`${selectedTitle} 已删除！`)
              this.queryProblems()
              return true
            })
            .catch((error: ErrorMsg) => {
              this.message.error(error.toString())
              return false
            })
            .finally(() => {
              d.loading = false
            })
      }
    })
  }

  /**
   * 获取题目数据
   */
  queryProblems() {
    this.pagination.loading = true
    ProblemApi.getAll(
        this.pagination.page,
        this.pagination.pageSize,
        this.keyword,
        this.userInfo
    ).then((data) => {
      this.problems = data
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.pagination.loading = false
    })
  }

  /**
   * 切换开放/关闭
   */
  toggleIsEnable(p: Problem, value: boolean) {
    ProblemApi.changeState(
        p.problemId!,
        value,
        this.userInfo
    ).then(() => {
      p.enable = value
      this.message.info(`${p.title} 已${value ? "开放" : "关闭"}`)
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    })
  }
}
</script>

<style scoped>
.problem-table {
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
}
</style>
