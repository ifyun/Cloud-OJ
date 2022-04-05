<template>
  <div class="contest-table">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-data-table
          :loading="pagination.loading"
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
  NButton,
  NCard,
  NDataTable,
  NPagination,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import { ContestApi } from "@/api/request"
import { Contest, ErrorMsg, PagedData } from "@/api/type"
import { LanguageUtil, setTitle } from "@/utils"
import { LanguageOptions } from "@/type"

const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const contests = ref<PagedData<Contest>>({
  data: [],
  count: 0
})

const contestColumns = [
  {
    title: "#",
    align: "right",
    width: 50,
    render: (row: Contest, rowIndex: number) => (
      <span>
        {(pagination.value.page - 1) * pagination.value.pageSize + rowIndex + 1}
      </span>
    )
  },
  {
    title: "竞赛",
    render: (row: Contest) => (
      <NSpace align="center">
        <NTag type={contestTagType(row)}>{contestStateText(row)}</NTag>
        <NButton text>{row.contestName}</NButton>
      </NSpace>
    )
  },
  {
    title: "语言限制",
    render: (row: Contest) => {
      if (row.languages === 511) {
        return <span>"没有限制"</span>
      }
      const languages: Array<string> = []
      LanguageUtil.toArray(row.languages).forEach((value) => {
        languages.push(LanguageOptions[value].label)
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

function contestTagType(c: Contest) {
  if (c.ended) {
    return "error"
  } else if (c.started) {
    return "success"
  } else {
    return "info"
  }
}

function contestStateText(c: Contest) {
  if (c.ended) {
    return "已结束"
  } else if (c.started) {
    return "进行中"
  } else {
    return "未开始"
  }
}

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
      pagination.value.loading = false
    })
}
</script>

<style scoped>
.contest-table {
  width: 1100px;
  padding: var(--layout-padding) 0;
  margin: 0 auto;
}
</style>
