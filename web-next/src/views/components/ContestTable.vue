<template>
  <div class="contest-table">
    <n-space vertical size="large">
      <n-data-table :loading="pagination.loading" :columns="contestColumns" :data="contests.data"/>
      <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                    :item-count="contests.count" @update:page="pageChange">
        <template #prefix="{itemCount}">
          共 {{ itemCount }} 项
        </template>
      </n-pagination>
    </n-space>
  </div>
</template>

<script lang="ts">
import {h} from "vue"
import {useRouter} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {NButton, NDataTable, NPagination, NSpace, NTag, useMessage} from "naive-ui"
import {ContestApi} from "@/api/request"
import {Contest, ErrorMsg, PagedData} from "@/api/type"
import {LanguageUtil, setTitle} from "@/utils"
import {LanguageOptions} from "@/type"

@Options({
  components: {
    NSpace,
    NDataTable,
    NPagination,
    NButton,
    NTag
  }
})
export default class ContestTable extends Vue {
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private pagination = {
    page: 1,
    pageSize: 15,
    loading: true
  }

  private contestColumns = [
    {
      title: "#",
      align: "right",
      width: 50,
      render: (row: Contest, rowIndex: number) => {
        return (this.pagination.page - 1) * this.pagination.pageSize + rowIndex + 1
      }
    },
    {
      title: "竞赛",
      render: (row: Contest) => {
        return h(
            NSpace,
            {
              align: "center"
            },
            {
              default: () => [
                h(NTag, {type: this.contestTagType(row)},
                    {default: () => this.contestStateText(row)}),
                h(NButton, {text: true}, {default: () => row.contestName})
              ]
            })
      }
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
        return h("span",
            {style: {wordBreak: "break-word"}},
            {default: () => languages.join(" / ")}
        )
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

  private contests: PagedData<Contest> = {
    data: [],
    count: 0
  }

  get userInfo() {
    return this.store.state.userInfo
  }

  beforeMount() {
    setTitle("竞赛")
    this.queryContests()
  }

  contestTagType(contest: Contest) {
    if (contest.ended) {
      return "error"
    } else if (contest.started) {
      return "success"
    } else {
      return "info"
    }
  }

  contestStateText(c: Contest) {
    if (c.ended) {
      return "已结束"
    } else if (c.started) {
      return "进行中"
    } else {
      return "未开始"
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
        this.pagination.pageSize
    ).then((data) => {
      this.contests = data
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error}: ${error.msg}`)
    }).finally(() => {
      this.pagination.loading = false
    })
  }
}
</script>

<style scoped>
.contest-table {
  width: calc(100% - 50px);
  padding: 25px 0;
  margin: 0 auto;
}
</style>