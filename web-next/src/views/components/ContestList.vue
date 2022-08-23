<template>
  <div class="wrap">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <empty-data v-if="!loading && contests.count === 0" />
        <n-data-table
          v-else
          size="small"
          :loading="loading"
          :columns="contestColumns"
          :data="contests.data" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="contests.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"
import {
  DataTableColumns,
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NPagination,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import { ContestApi } from "@/api/request"
import { Contest, ErrorMsg, Page } from "@/api/type"
import { LanguageUtil, setTitle, stateTag } from "@/utils"
import { LanguageNames } from "@/type"
import EmptyData from "@/components/EmptyData.vue"

const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)

const contests = ref<Page<Contest>>({
  data: [],
  count: 0
})

const contestColumns: DataTableColumns<Contest> = [
  {
    title: "状态",
    key: "state",
    align: "center",
    width: 100,
    render: (row) => {
      const tag = stateTag(row)
      return (
        <NTag
          round={true}
          bordered={false}
          type={tag.type}
          style="margin-top: 2px">
          {{
            icon: () => <NIcon component={tag.icon} />,
            default: () => <span>{tag.state}</span>
          }}
        </NTag>
      )
    }
  },
  {
    title: "竞赛",
    key: "contest",
    render: (row) => (
      <NButton
        text
        onClick={() =>
          router.push({
            name: "contest_problems",
            params: { cid: row.contestId }
          })
        }>
        {row.contestName}
      </NButton>
    )
  },
  {
    title: "语言限制",
    key: "languages",
    render: (row: Contest) => {
      if (row.languages === 511) {
        return <span>没有限制</span>
      }
      const languages: Array<string> = []
      LanguageUtil.toArray(row.languages).forEach((value) => {
        languages.push(LanguageNames[value])
      })
      return (
        <span style={{ wordBreak: "break-word" }}>{languages.join(" / ")}</span>
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

onBeforeMount(() => {
  setTitle("竞赛")
  queryContests()
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })
}

function queryContests() {
  ContestApi.getAll(pagination.value.page, pagination.value.pageSize)
    .then((data) => {
      contests.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(`${error}: ${error.msg}`)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style>
td[class="n-data-table-td"] {
  vertical-align: middle;
}
</style>
