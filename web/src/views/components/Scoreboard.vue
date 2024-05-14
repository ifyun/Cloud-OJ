<template>
  <div class="wrap">
    <empty-data v-if="noContent" />
    <div v-else>
      <n-space vertical>
        <n-data-table
          single-column
          :loading="loading"
          :columns="rankingColumns"
          :data="rankings.data" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="rankings.total"
          @update:page="pageChange" />
      </n-space>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { RankingApi } from "@/api/request"
import { ErrorMessage, type Page, Ranking } from "@/api/type"
import { EmptyData, UserAvatar } from "@/components"
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import { StarsRound } from "@vicons/material"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NPagination,
  NSpace,
  NText
} from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const rankings = ref<Page<Ranking>>({
  data: [],
  total: 0
})

const noContent = computed<boolean>(
  () => !loading.value && rankings.value.total === 0
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
        <UserAvatar
          size="small"
          uid={row.uid!}
          nickname={row.nickname}
          hasAvatar={row.hasAvatar}
        />
        <RouterLink to={{ name: "account", params: { uid: row.uid } }}>
          <NButton text strong iconPlacement="right">
            {{
              default: () => row.nickname,
              icon: () => (row.star ? <NIcon component={StarsRound} /> : null)
            }}
          </NButton>
        </RouterLink>
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

  queryRankings()
}

function queryRankings() {
  RankingApi.get(pagination.value.page, pagination.value.pageSize)
    .then((data) => {
      rankings.value = data
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
