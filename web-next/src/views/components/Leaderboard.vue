<template>
  <div class="leaderboard">
    <n-space vertical>
      <n-data-table :loading="pagination.loading" :columns="rankingColumns" :data="rankings.data"/>
      <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize"
                    :item-count="rankings.count" @update:page="pageChange"/>
    </n-space>
  </div>
</template>

<script lang="ts">
import {h} from "vue"
import {Options, Vue} from "vue-class-component"
import {useRouter} from "vue-router"
import {NDataTable, NPagination, NSpace, NText, useMessage} from "naive-ui"
import {RankingApi} from "@/api/request"
import {ErrorMsg, PagedData, Ranking} from "@/api/type"
import {setTitle} from "@/utils"

@Options({
  components: {
    NSpace,
    NDataTable,
    NPagination,
    NText
  }
})
export default class Leaderboard extends Vue {
  private router = useRouter()
  private message = useMessage()

  private pagination = {
    page: 1,
    pageSize: 20,
    loading: true
  }

  private rankingColumns = [
    {
      title: "排名",
      align: "center",
      width: 90,
      key: "rank"
    },
    {
      title: "用户",
      key: "name"
    },
    {
      title: "提交",
      width: 100,
      align: "right",
      key: "committed"
    },
    {
      title: "通过",
      width: 100,
      align: "right",
      key: "passed"
    },
    {
      title: "分数",
      width: 100,
      align: "right",
      render: (row: Ranking) => {
        return h(NText, {type: "success", strong: true}, {default: () => row.score})
      }
    }
  ]

  private rankings: PagedData<Ranking> = {
    data: [],
    count: 0
  }

  beforeMount() {
    setTitle("排名")

    if ("page" in this.$route.query) {
      this.pagination.page = Number(this.$route.query.page)
    }
    this.queryRankings()
  }

  pageChange(page: number) {
    this.router.push({
      query: {page}
    })
  }

  queryRankings() {
    RankingApi.get(
        this.pagination.page,
        this.pagination.pageSize
    ).then((data) => {
      this.rankings = data
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.pagination.loading = false
    })
  }
}
</script>

<style scoped>
.leaderboard {
  width: calc(100% - 50px);
  padding: 25px 0;
  margin: 0 auto;
}
</style>