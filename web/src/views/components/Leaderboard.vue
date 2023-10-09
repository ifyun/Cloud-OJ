<template>
  <div class="wrap">
    <error-result v-if="error != null" :error="error" />
    <empty-data v-else-if="noContent" style="margin-top: 48px" />
    <div v-else>
      <n-space vertical>
        <n-space v-if="contestState != null" align="center">
          <n-tag round :bordered="false" :type="contestState.type">
            <template #icon>
              <n-icon :component="contestState.icon" />
            </template>
            {{ contestState.state }}
          </n-tag>
          <n-text strong>{{ contest?.contestName }} - 排名</n-text>
        </n-space>
        <n-data-table
          single-column
          :loading="loading"
          :columns="rankingColumns"
          :data="rankings.data" />
        <n-pagination
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
import { ErrorResult, UserAvatar } from "@/components"
import EmptyData from "@/components/EmptyData.vue"
import { StateTag, setTitle, stateTag } from "@/utils"
import {
  DataTableColumns,
  NDataTable,
  NIcon,
  NPagination,
  NSpace,
  NTag,
  NText
} from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"

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

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)
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
        <NText>{row.nickname}</NText>
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
    error.value = {
      status: 404,
      error: "Not Found",
      message: "找不到竞赛"
    }

    loading.value = false
  }
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
      queryRankings()
    })
    .catch((err: ErrorMessage) => {
      error.value = err
      loading.value = false
    })
}

function queryRankings() {
  RankingApi.get(pagination.value.page, pagination.value.pageSize, contestId)
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
