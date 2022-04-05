<template>
  <div class="leaderboard">
    <n-card :bordered="false">
      <n-space vertical>
        <n-data-table
          :loading="pagination.loading"
          :columns="rankingColumns"
          :data="rankings.data" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="rankings.count"
          @update:page="pageChange" />
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import {
  NCard,
  NDataTable,
  NPagination,
  NSpace,
  NText,
  useMessage
} from "naive-ui"
import { UserAvatar } from "@/components"
import { RankingApi } from "@/api/request"
import { ErrorMsg, PagedData, Ranking } from "@/api/type"
import { setTitle } from "@/utils"

const route = useRoute()
const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const rankings = ref<PagedData<Ranking>>({
  data: [],
  count: 0
})

const rankingColumns = [
  {
    title: "排名",
    align: "center",
    width: 90,
    key: "rank"
  },
  {
    title: "用户",
    render: (row: Ranking) => (
      <NSpace align="center" size="small">
        <UserAvatar size="small" userId={row.userId} />
        <NText>{row.name}</NText>
      </NSpace>
    )
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
    render: (row: Ranking) => (
      <NText type="success" strong>
        {row.score}
      </NText>
    )
  }
]

onBeforeMount(() => {
  setTitle("排名")

  if ("page" in route.query) {
    pagination.value.page = Number(route.query.page)
  }

  queryRankings()
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })
}

function queryRankings() {
  RankingApi.get(pagination.value.page, pagination.value.pageSize)
    .then((data) => {
      rankings.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(`${error.code}: ${error.msg}`)
    })
    .finally(() => {
      pagination.value.loading = false
    })
}
</script>

<style scoped>
.leaderboard {
  width: 1100px;
  padding: var(--layout-padding) 0;
  margin: 0 auto;
}
</style>
