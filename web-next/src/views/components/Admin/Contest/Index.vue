<template>
  <div class="contest-table">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space>
          <n-button type="primary">
            <template #icon>
              <n-icon>
                <add-icon/>
              </n-icon>
            </template>
            <router-link :to="{name: 'edit_contest', params: {id: 'new'}}">
              创建竞赛
            </router-link>
          </n-button>
        </n-space>
        <n-data-table :row-props="rowProps" :columns="contestColumns" :data="contests.data"
                      :loading="pagination.loading"/>
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                      :item-count="contests.count" @update:page="pageChange">
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
import {useRouter} from "vue-router"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NCard,
  NDataTable,
  NDropdown,
  NIcon,
  NPagination,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import {DeleteForeverRound as DelIcon, EditNoteRound as EditIcon, PlaylistAddRound as AddIcon} from "@vicons/material"
import {Contest, ErrorMsg, PagedData, UserInfo} from "@/api/type"
import {LanguageUtil, renderIcon, setTitle} from "@/utils"
import {LanguageOptions} from "@/type"
import {ContestApi} from "@/api/request"
import MutationType from "@/store/mutation-type";

let selectedId: number | undefined

type StateTag = {
  type: "info" | "error" | "success",
  state: string
}

@Options({
  name: "ContestAdmin",
  components: {
    NCard,
    NSpace,
    NBreadcrumb,
    NBreadcrumbItem,
    NDataTable,
    NPagination,
    NDropdown,
    NButton,
    NIcon,
    AddIcon
  }
})
export default class ContestAdmin extends Vue {
  private store = useStore()
  private router = useRouter()
  private message = useMessage()

  private pagination = {
    page: 1,
    pageSize: 15,
    loading: true
  }

  private showOperations: boolean = false
  private point = {
    x: 0,
    y: 0
  }

  private rowProps = (row: Contest) => {
    return {
      onContextmenu: (e: PointerEvent) => {
        if ((e.target as Element).tagName !== "TD") {
          return
        }
        e.preventDefault()
        this.showOperations = false
        nextTick().then(() => {
          selectedId = row.contestId
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
      key: "edit",
      label: "编辑",
      icon: renderIcon(EditIcon, "#399F58")
    },
    {
      key: "del",
      label: "删除",
      icon: renderIcon(DelIcon, "#F17C67")
    }
  ]

  // #region 表格列选项
  private contestColumns = [
    {
      title: "状态",
      align: "center",
      width: 120,
      render: (row: Contest) => {
        const tag = this.stateTag(row)
        return <NTag type={tag.type}>{tag.state}</NTag>
      }
    },
    {
      title: "竞赛",
      render: (row: Contest) => (
          <NSpace align="center">
            <NButton text>{row.contestName}</NButton>
          </NSpace>
      )
    },
    {
      title: "语言限制",
      render: (row: Contest) => {
        if (row.languages === 511) {
          return "没有限制"
        }
        const languages: Array<string> = []
        LanguageUtil.toArray(row.languages).forEach((value) => {
          languages.push(LanguageOptions[value].label)
        })
        return <span style={{wordBreak: "break-word"}}>{languages.join(" / ")}</span>
      }
    },
    {
      title: "开始时间",
      key: "startAt"
    },
    {
      title: "结束时间",
      key: "endAt"
    }
  ]
  // endregion

  private contests: PagedData<Contest> = {
    data: [],
    count: 0
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    setTitle("竞赛管理")
    this.store.commit(MutationType.SET_BREADCRUMB,
        <NBreadcrumb>
          <NBreadcrumbItem>竞赛管理</NBreadcrumbItem>
        </NBreadcrumb>
    )
    this.queryContests()
  }

  hideOperation() {
    this.showOperations = false
  }

  operationSelect(key: string) {
    this.hideOperation()
    switch (key) {
      case "edit":
        this.router.push({name: "edit_contest", params: {id: selectedId}})
        break;
      default:
        return
    }
  }

  /**
   * 竞赛状态标签
   */
  stateTag(c: Contest): StateTag {
    if (c.ended) {
      return {
        type: "error",
        state: "已结束"
      }
    } else if (c.started) {
      return {
        type: "success",
        state: "进行中"
      }
    } else {
      return {
        type: "info",
        state: "未开始"
      }
    }
  }

  pageChange(page: number) {
    this.router.push({
      query: {page}
    })
  }

  queryContests() {
    ContestApi.getAll(
        this.pagination.page,
        this.pagination.pageSize,
        this.userInfo
    ).then((data) => {
      this.contests = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.pagination.loading = false
    })
  }
}
</script>

<style scoped>
.contest-table {
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
}
</style>
