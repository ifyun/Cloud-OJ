<template>
  <div class="problem-table">
    <n-space vertical size="large">
      <n-space align="center">
        <n-input-group>
          <n-input size="large" maxlength="10" show-count clearable
                   placeholder="输入题目名称、分类" v-model:value="keyword">
            <template #prefix>
              <n-icon>
                <search-icon/>
              </n-icon>
            </template>
          </n-input>
          <n-button size="large" type="primary" @click="search">
            搜索题目
          </n-button>
        </n-input-group>
        <n-tag v-if="keywordTag != null" closable @close="clearKeyword">{{ keywordTag }}</n-tag>
      </n-space>
      <n-data-table :columns="problemColumns" :data="problems.data"
                    :loading="pagination.loading"/>
      <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                    :item-count="problems.count" @update:page="pageChange">
        <template #prefix="{itemCount}">
          共 {{ itemCount }} 项
        </template>
      </n-pagination>
    </n-space>
  </div>
</template>

<script lang="tsx">
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {RouterLink, useRouter} from "vue-router"
import {NButton, NDataTable, NIcon, NInput, NInputGroup, NPagination, NSpace, NTag, useMessage} from "naive-ui"
import {Search as SearchIcon} from "@vicons/fa"
import {ProblemApi} from "@/api/request"
import {ErrorMsg, PagedData, Problem, UserInfo} from "@/api/type"
import {setTitle, TagUtil} from "@/utils"

@Options({
  name: "ProblemTable",
  components: {
    NSpace,
    NInput,
    NInputGroup,
    NIcon,
    NButton,
    NTag,
    NDataTable,
    NPagination,
    SearchIcon
  }
})
export default class ProblemTable extends Vue {
  private store = useStore()
  private router = useRouter()
  private message = useMessage()

  private pagination = {
    page: 1,
    pageSize: 20,
    loading: true
  }

  /* 表格列配置 */
  private problemColumns = [
    {
      title: "#",
      align: "right",
      width: 50,
      render: (row: Problem, rowIndex: number) => (
          <span>{(this.pagination.page - 1) * this.pagination.pageSize + rowIndex + 1}</span>
      )
    },
    {
      title: "ID",
      key: "problemId",
      align: "right",
      width: 70
    },
    {
      title: "题目名称",
      render: (row: Problem) => (
          <NButton text>
            <RouterLink to={{name: "submission", query: {problemId: row.problemId}}}>
              {row.title}
            </RouterLink>
          </NButton>
      )
    },
    {
      title: "分类",
      render: (row: Problem) => {
        if (row.category === "") {
          return ""
        }
        const tags = row.category.split(",")
        return tags.map((tag) => {
          return <NTag class="tag" size="small" color={TagUtil.getColor(tag, this.theme)}>{tag}</NTag>
        })
      }
    },
    {
      title: "分数",
      key: "score",
      align: "right"
    }
  ]

  /* 题目数据 */
  private problems: PagedData<Problem> = {
    data: [],
    count: 0
  }

  private keyword: string = ""
  private keywordTag: string | null = null

  get theme() {
    return this.store.state.theme
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    setTitle("练习")

    if ("page" in this.$route.query) {
      this.pagination.page = Number(this.$route.query.page)
    }

    if ("keyword" in this.$route.query) {
      this.keyword = String(this.$route.query.keyword)
      this.keywordTag = this.keyword
    }

    this.queryProblems()
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
   * 获取题目
   */
  queryProblems() {
    this.pagination.loading = true
    ProblemApi.getAllOpened(
        this.pagination.page,
        this.pagination.pageSize,
        this.keyword
    ).then((data) => {
      this.problems = data
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.pagination.loading = false
    })
  }
}
</script>

<style scoped lang="scss">
.problem-table {
  width: calc(100% - 50px);
  padding: 25px 0;
  margin: 0 auto;
}
</style>
