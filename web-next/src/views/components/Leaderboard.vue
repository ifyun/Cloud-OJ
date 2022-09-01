<template>
  <div class="wrap">
    <error-result v-if="error != null" :error="error" />
    <empty-data v-else-if="noContent" style="margin-top: 48px" />
    <n-card v-else :bordered="false">
      <n-space vertical>
        <n-data-table
          size="small"
          :loading="loading"
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
import { computed, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NCard,
  NDataTable,
  NPagination,
  NSpace,
  NText
} from "naive-ui"
import { ErrorResult, UserAvatar } from "@/components"
import { RankingApi } from "@/api/request"
import { ErrorMessage, Page, Ranking } from "@/api/type"
import { setTitle } from "@/utils"
import EmptyData from "@/components/EmptyData.vue"

const route = useRoute()
const router = useRouter()

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)

const rankings = ref<Page<Ranking>>({
  data: [],
  count: 0
})

const noContent = computed<boolean>(
  () => !loading.value && rankings.value.count === 0
)

const rankingColumns: DataTableColumns<Ranking> = [
  {
    title: "排名",
    align: "center",
    width: 90,
    key: "rank"
  },
  {
    title: "用户",
    key: "name",
    render: (row) => (
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
    key: "score",
    width: 100,
    align: "right",
    render: (row) => (
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
    .catch((err: ErrorMessage) => {
      error.value = err
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
