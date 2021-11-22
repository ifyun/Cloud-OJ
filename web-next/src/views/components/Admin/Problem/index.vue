<!-- Problem Admin -->
<template>
  <div class="problem-table">
    <n-space vertical size="large">
      <n-space align="center" justify="space-between">
        <n-space align="center">
          <n-input-group>
            <n-input maxlength="10" show-count clearable size="large"
                     placeholder="输入题目名称、分类" v-model:value="keyword">
              <template #prefix>
                <n-icon class="input-prefix-icon">
                  <search-icon/>
                </n-icon>
              </template>
            </n-input>
            <n-button type="primary" @click="search" size="large">
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
  </div>
  <n-dropdown trigger="manual" :show-arrow="true" :x="point.x" :y="point.y" :options="operations"
              :show="showOperations" @select="operationSelect" :on-clickoutside="hideOperation"/>
</template>

<script lang="ts">
import {h, nextTick} from "vue"
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {RouterLink, useRouter} from "vue-router"
import {
  NButton,
  NButtonGroup,
  NDataTable,
  NDropdown,
  NGrid,
  NGridItem,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSpace,
  NSwitch,
  NTag,
  useMessage
} from "naive-ui"
import {FileArchive as FileIcon, Search as SearchIcon, Tags as TagsIcon} from "@vicons/fa"
import {DeleteForeverRound as DelIcon, EditNoteRound as EditIcon, PostAddRound as AddIcon} from "@vicons/material"
import {ErrorMsg, PagedData, Problem, UserInfo} from "@/api/type"
import {renderIcon, setTitle, TagUtil} from "@/utils"
import {ProblemApi} from "@/api/request"

let selectedId: number | undefined

@Options({
  components: {
    NSpace,
    NGrid,
    NGridItem,
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
    TagsIcon,
    AddIcon
  }
})
export default class ProblemAdmin extends Vue {
  private store = useStore()
  private router = useRouter()
  private message = useMessage()

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

  private showOperations: boolean = false
  private point = {
    x: 0,
    y: 0
  }

  /* 表格行右键弹出菜单 */
  private rowProps = (row: Problem) => {
    return {
      onContextmenu: (e: PointerEvent) => {
        e.preventDefault()
        this.showOperations = false
        nextTick().then(() => {
          selectedId = row.problemId
          this.showOperations = true
          this.point = {
            x: e.clientX,
            y: e.clientY
          }
        })
      },
    }
  }

  private operations = [
    {
      label: "编辑",
      key: "edit",
      icon: renderIcon(EditIcon)
    },
    {
      label: "管理测试数据",
      key: "test_data",
      icon: renderIcon(FileIcon, "#399F58")
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
      render: (row: Problem) => {
        return h(NButton,
            {
              text: true,
              onContextmenu: (e: PointerEvent) => {
                e.stopPropagation()
              }
            },
            {
              default: () => h(RouterLink,
                  {to: {name: "submission", query: {problemId: row.problemId}}},
                  {default: () => row.title})
            })
      }
    },
    {
      title: () => h(NSpace, {size: "small", justify: "start", align: "center"}, {
        default: () => [
          h(NIcon, {style: {display: "flex"}}, {default: () => h(TagsIcon)}),
          h("span", {}, {default: () => "分类"})
        ]
      }),
      render: (row: Problem) => {
        if (row.category === "") {
          return ""
        }
        const tags = row.category.split(",")
        return tags.map((tag) => {
          return h(NTag,
              {
                size: "small",
                class: "tag",
                color: TagUtil.getColor(tag, this.theme),
                onContextmenu: (e: PointerEvent) => {
                  e.stopPropagation()
                }
              },
              {
                default: () => tag
              })
        })
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
      render: (row: Problem) => {
        return h(NSwitch, {
              value: row.enable,
              "onUpdate:value": value => {
                this.toggleIsEnable(row, value)
              },
              onContextmenu: (e: PointerEvent) => {
                e.stopPropagation()
              }
            },
            {checked: () => "开放", unchecked: () => "关闭"})
      }
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
        break;
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
        value, this.userInfo
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
  padding: 25px;
}
</style>