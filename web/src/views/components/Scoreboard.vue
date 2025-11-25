<template>
  <div class="wrap">
    <empty-data v-if="noContent" />
    <div v-else>
      <n-flex vertical>
        <n-data-table
          :loading="loading"
          :columns="rankingColumns"
          :data="rankings.data" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="rankings.total"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-flex>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { RankingApi } from "@/api/request"
import { ErrorMessage, type Page, Ranking } from "@/api/type"
import { EmptyData, UserAvatar } from "@/components"
import { useStore } from "@/store"
import { StarRound } from "@vicons/material"
import {
  type DataTableColumns,
  NBadge,
  NButton,
  NDataTable,
  NFlex,
  NIcon,
  NPagination,
  NTag,
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
    key: "rank",
    align: "center",
    width: 90,
    render: (row) => <NText strong>{row.rank}</NText>
  },
  {
    title: "用户",
    key: "name",
    render: (row) => (
      <NFlex align="center" size="small">
        <NBadge color="transparent">
          {{
            default: () => (
              <UserAvatar
                size="large"
                uid={row.uid!}
                nickname={row.nickname}
                hasAvatar={row.hasAvatar}
              />
            ),
            value: () =>
              row.star ? (
                <NIcon component={StarRound} color="#f9a825" size="16" />
              ) : (
                ""
              )
          }}
        </NBadge>
        <NFlex vertical size={0}>
          <NFlex size="small">
            <RouterLink to={{ name: "account", params: { uid: row.uid } }}>
              <NButton text strong>
                {row.nickname}
              </NButton>
            </RouterLink>
            {row.uid === store.user?.userInfo?.uid ? (
              <NTag type="primary" size="small" round>
                你自己
              </NTag>
            ) : (
              ""
            )}
          </NFlex>
          {row.username ? (
            <NText depth="3" italic>
              {row.realName}@{row.username}
            </NText>
          ) : (
            ""
          )}
        </NFlex>
      </NFlex>
    )
  },
  {
    title: "提交",
    key: "committed",
    width: 100,
    align: "right",
    render: (row) => <NText strong>{row.committed}</NText>
  },
  {
    title: "通过",
    key: "passed",
    width: 100,
    align: "right",
    render: (row) => <NText strong>{row.passed}</NText>
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
