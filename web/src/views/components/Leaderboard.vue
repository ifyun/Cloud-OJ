<template>
  <div class="wrap">
    <empty-data v-if="noContent" style="margin-top: 48px" />
    <div v-else>
      <n-space vertical>
        <n-space v-if="contestState != null" justify="space-between">
          <n-space align="center">
            <n-tag round :bordered="false" :type="contestState.type">
              <template #icon>
                <n-icon :component="contestState.icon" />
              </template>
              {{ contestState.state }}
            </n-tag>
            <n-h4 style="margin-bottom: 4px">
              {{ contest?.contestName }} - 排名
            </n-h4>
          </n-space>
          <n-switch v-model:value="autoRefresh" :round="false">
            <template #checked>自动刷新</template>
            <template #unchecked>自动刷新</template>
          </n-switch>
        </n-space>
        <n-data-table
          single-column
          :loading="loading"
          :columns="rankingColumns"
          :data="rankings.data" />
        <n-pagination
          v-if="!autoRefresh"
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="rankings.count"
          @update:page="pageChange" />
      </n-space>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { ContestApi, RankingApi } from "@/api/request"
import { Contest, ErrorMessage, Page, Ranking } from "@/api/type"
import { EmptyData, UserAvatar } from "@/components"
import { useStore } from "@/store"
import { StateTag, setTitle, stateTag } from "@/utils"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NH4,
  NIcon,
  NPagination,
  NSpace,
  NSwitch,
  NTag,
  NText
} from "naive-ui"
import { computed, nextTick, onBeforeMount, onUnmounted, ref, watch } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()

const props = withDefaults(
  defineProps<{
    cid: string | null
  }>(),
  { cid: null }
)

let contestId: number | null = null
let contestState: StateTag | null = null
let timeout: number | undefined

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const autoRefresh = ref<boolean>(false)
const contest = ref<Contest | null>(null)
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
        <UserAvatar
          size="small"
          uid={row.uid!}
          nickname={row.nickname}
          hasAvatar={row.hasAvatar}
        />
        <RouterLink to={{ name: "account", params: { uid: row.uid } }}>
          <NButton text={true} strong={true}>
            {row.nickname}
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

watch(autoRefresh, (val) => {
  window.clearTimeout(timeout)
  if (val === true) {
    pagination.value = { page: 1, pageSize: 30 }
    nextTick(() => queryContest())
  } else {
    pagination.value = { page: 1, pageSize: 15 }
  }
})

onBeforeMount(() => {
  const reg = /^\d+$/
  setTitle("排名")

  if ("page" in route.query) {
    pagination.value.page = Number(route.query.page)
  }

  if (props.cid == null) {
    queryRankings()
  } else if (reg.test(props.cid)) {
    contestId = Number(props.cid)
    queryContest()
  } else {
    store.app.setError({
      status: 404,
      error: "Not Found",
      message: "找不到竞赛"
    })
  }
})

onUnmounted(() => {
  window.clearTimeout(timeout)
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })

  queryRankings()
}

function queryContest() {
  ContestApi.getById(contestId!)
    .then((data) => {
      contest.value = data
      contestState = stateTag(data)
      setTitle(`${data.contestName} | 排名`)
      queryRankings()
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      if (autoRefresh.value === true) {
        timeout = window.setTimeout(queryContest, 15000)
      }
    })
}

function queryRankings() {
  RankingApi.get(pagination.value.page, pagination.value.pageSize, contestId)
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
