<template>
  <div class="contest-problem-tables">
    <n-space vertical size="large">
      <n-data-table :columns="columns1" :data="problems.data" :loading="pagination1.loading"/>
      <n-pagination v-model:page="pagination1.page" :page-size="pagination1.pageSize"
                    :item-count="problems.count" @update:page="queryProblems">
        <template #prefix="{itemCount}">
          共 {{ itemCount }} 项
        </template>
      </n-pagination>
    </n-space>
    <n-space vertical size="large">
      <n-data-table :columns="columns2" :data="contestProblems.data" :loading="pagination2.loading"/>
      <n-pagination v-model:page="pagination2.page" :page-size="pagination2.pageSize"
                    :item-count="contestProblems.count" @update:page="queryContestProblems"/>
    </n-space>
  </div>
</template>

<script lang="ts">
import {h} from "vue"
import {useStore} from "vuex"
import {RouterLink} from "vue-router"
import {Options, Vue} from "vue-class-component"
import {Prop, Watch} from "vue-property-decorator"
import {NButton, NDataTable, NPagination, NSpace, useDialog, useMessage} from "naive-ui"
import {ErrorMsg, PagedData, Problem, UserInfo} from "@/api/type"
import {ContestApi} from "@/api/request"

let self: any

@Options({
  components: {
    NSpace,
    NButton,
    NDataTable,
    NPagination
  }
})
export default class ProblemManage extends Vue {
  private store = useStore()
  private message = useMessage()
  private dialog = useDialog()

  private problems: PagedData<Problem> = {
    data: [],
    count: 0
  }

  private contestProblems: PagedData<Problem> = {
    data: [],
    count: 0
  }

  private pagination1 = {
    page: 1,
    pageSize: 15,
    loading: true
  }

  private pagination2 = {
    page: 1,
    pageSize: 10,
    loading: true
  }

  private columns1 = [
    {
      title: "可用题目",
      align: "center",
      children: [
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
                {text: true},
                {
                  default: () => h(RouterLink,
                      {to: {name: "submission", query: {problemId: row.problemId}}},
                      {default: () => row.title})
                })
          }
        },
        {
          title: "分数",
          key: "score",
          align: "right"
        },
        {
          title: "操作",
          align: "right",
          width: 100,
          render: (row: Problem) =>
              h(NButton, {
                size: "tiny",
                type: "success",
                onClick: () => self.addToContest(row)
              }, {default: () => "添加"})
        }
      ]
    }
  ]

  private columns2 = [
    {
      title: "已添加的题目",
      align: "center",
      children: [
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
                {text: true},
                {
                  default: () => h(RouterLink,
                      {to: {name: "submission", query: {problemId: row.problemId}}},
                      {default: () => row.title})
                })
          }
        },
        {
          title: "分数",
          key: "score",
          align: "right"
        },
        {
          title: "操作",
          align: "right",
          width: 100,
          render: (row: Problem) =>
              h(NButton, {
                size: "tiny",
                type: "warning",
                onClick: () => self.handleRemove(row)
              }, {default: () => "移除"})
        }
      ]
    }
  ]

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  beforeMount() {
    self = this
  }

  @Prop(Number)
  private contestId: number = 0

  @Watch("contestId", {immediate: true})
  contestIdChanged(value: number) {
    if (value !== 0) {
      this.queryProblems()
      this.queryContestProblems()
    }
  }

  /**
   * 获取可用题目
   */
  queryProblems() {
    this.pagination1.loading = true
    ContestApi.getProblemsNotInContest(
        this.contestId,
        this.pagination1.page,
        this.pagination1.pageSize,
        this.userInfo
    ).then((data) => {
      this.problems = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.pagination1.loading = false
    })
  }

  /**
   * 获取当前竞赛的题目
   */
  queryContestProblems() {
    this.pagination2.loading = true
    ContestApi.getProblems(
        this.contestId,
        this.pagination2.page,
        this.pagination2.pageSize,
        this.userInfo
    ).then((data) => {
      this.contestProblems = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.pagination2.loading = false
    })
  }

  refresh() {
    this.queryContestProblems()
    this.queryProblems()
  }

  /**
   * 将题目添加到当前竞赛
   */
  addToContest(problem: Problem) {
    ContestApi.addProblem(
        this.contestId,
        problem.problemId!,
        this.userInfo
    ).then(() => {
      this.message.success(`[${problem.title}] 已添加`)
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.refresh()
    })
  }

  handleRemove(p: Problem) {
    this.dialog.warning({
      title: "警告",
      content: `是否移除 [${p.title}]？`,
      positiveText: "是",
      negativeText: "不要",
      onPositiveClick: () => this.remove(p)
    })
  }

  /**
   * 从竞赛中移除题目
   */
  remove(problem: Problem) {
    ContestApi.removeProblem(
        this.contestId,
        problem.problemId!,
        this.userInfo
    ).then(() => {
      this.message.warning(`[${problem.title}] 已移除`)
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.refresh()
    })
  }
}
</script>

<style scoped lang="scss">
.contest-problem-tables {
  display: flex;
  flex: 1;

  > * {
    flex: 1;
    margin-left: 12px;

    &:first-child {
      margin-left: 0;
    }
  }
}
</style>