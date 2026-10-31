<template>
  <div class="wrap">
    <empty-data v-if="noContent" />
    <div v-else>
      <n-flex vertical size="large">
        <n-flex align="center" style="width: fit-content">
          <n-input-group>
            <n-input
              v-model:value="filter.keyword"
              maxlength="10"
              show-count
              clearable
              placeholder="输入题目名称、分类"
              @clear="search" />
            <n-button type="primary" @click="search">
              搜索题目
              <template #icon>
                <n-icon>
                  <search-round />
                </n-icon>
              </template>
            </n-button>
          </n-input-group>
        </n-flex>
        <n-data-table
          single-column
          :columns="problemColumns"
          :data="problems.data"
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="problems.total"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-flex>
    </div>
  </div>
</template>

<script lang="tsx">
export default {
  name: "ProblemList"
}
</script>

<script setup lang="tsx">
import { ProblemApi } from "@/api/request"
import { ErrorMessage, type Page, Problem } from "@/api/type"
import { EmptyData, ResultTag } from "@/components"
import { useAppStore } from "@/store/app"
import { useFilterStore } from "@/store/filter"
import { useUserStore } from "@/store/user"
import { SearchRound } from "@vicons/material"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NFlex,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NTag
} from "naive-ui"
import { storeToRefs } from "pinia"
import { computed, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const app = useAppStore()
const user = useUserStore()
const route = useRoute()
const router = useRouter()

const { filter } = storeToRefs(useFilterStore())

const loading = ref<boolean>(true)
const problems = ref<Page<Problem>>({
  data: [],
  total: 0
})
const pagination = ref({
  page: 1,
  pageSize: 15
})

/* 表格列配置 */
const problemColumns: DataTableColumns<Problem> = [
  {
    title: "#",
    key: "#",
    align: "right",
    width: 50,
    render: (_, rowIndex: number) => (
      <span>
        {(pagination.value.page - 1) * pagination.value.pageSize + rowIndex + 1}
      </span>
    )
  },
  {
    title: "ID",
    key: "problemId",
    align: "right",
    width: 70
  },
  {
    title: "题名",
    key: "title",
    render: (row) => (
      <RouterLink to={{ name: "problem", params: { pid: row.problemId } }}>
        <NButton text={true} strong={true}>
          {row.title}
        </NButton>
      </RouterLink>
    )
  },
  {
    title: () => (user.isLoggedIn ? "提交状态" : ""),
    key: "result",
    width: user.isLoggedIn ? 100 : 0,
    align: "center",
    render: (row) => {
      if (typeof row.result !== "undefined") {
        return <ResultTag data={row} />
      }
    }
  },
  {
    title: "分类",
    key: "category",
    align: "center",
    render: (row) => {
      if (row.category === "") {
        return ""
      }
      const tags = row.category.split(",")
      return (
        <NFlex align="center" justify="center" size="small">
          {tags.map((tag) => (
            <NTag
              class="category-tag"
              size="small"
              type="primary"
              round
              bordered={false}
              // @ts-ignore
              onClick={() => tagClick(tag)}>
              {tag}
            </NTag>
          ))}
        </NFlex>
      )
    }
  },
  {
    title: "分数",
    key: "score",
    align: "right"
  }
]

const noContent = computed<boolean>(
  () => !loading.value && problems.value.total === 0
)

onMounted(() => {
  const { page = 1 } = route.query
  pagination.value.page = Number(page)

  queryProblems()
})

function tagClick(tag: string) {
  filter.value.keyword = tag
  search()
}

function search() {
  nextTick(() => {
    pagination.value.page = 1
    pageChange(1)
  })
}

function pageChange(page: number) {
  router.replace({
    query: { page }
  })
}

/**
 * 获取题目
 */
function queryProblems() {
  loading.value = true
  const { page, pageSize } = pagination.value
  ProblemApi.getAllOpened(page, pageSize, filter.value.keyword)
    .then((data) => {
      problems.value = data
    })
    .catch((err: ErrorMessage) => {
      app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
