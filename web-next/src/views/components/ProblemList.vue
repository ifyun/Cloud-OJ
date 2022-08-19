<template>
  <div class="wrap">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space align="center">
          <n-input-group>
            <n-input
              size="large"
              maxlength="10"
              show-count
              clearable
              placeholder="输入题目名称、分类"
              v-model:value="keyword">
              <template #prefix>
                <n-icon>
                  <search-icon />
                </n-icon>
              </template>
            </n-input>
            <n-button size="large" type="primary" @click="search">
              搜索题目
            </n-button>
          </n-input-group>
          <n-tag v-if="keywordTag != null" closable @close="clearKeyword">
            {{ keywordTag }}
          </n-tag>
        </n-space>
        <n-data-table
          :bordered="false"
          :columns="problemColumns"
          :data="problems.data"
          :loading="pagination.loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="problems.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import { Search as SearchIcon } from "@vicons/fa"
import { ProblemApi } from "@/api/request"
import { ErrorMsg, PagedData, Problem } from "@/api/type"
import { setTitle, TagUtil } from "@/utils"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 20,
  loading: true
})

/* 表格列配置 */
const problemColumns: DataTableColumns<Problem> = [
  {
    title: "#",
    key: "#",
    align: "right",
    width: 50,
    render: (row, rowIndex: number) => (
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
    title: "题目名称",
    key: "title",
    render: (row) => (
      <NButton
        text
        onClick={() =>
          router.push({
            name: "submission",
            params: { pid: row.problemId }
          })
        }>
        {row.title}
      </NButton>
    )
  },
  {
    title: "分类",
    key: "category",
    render: (row) => {
      if (row.category === "") {
        return ""
      }
      const tags = row.category.split(",")
      return tags.map((tag) => {
        return (
          <NTag
            class="tag"
            size="small"
            color={TagUtil.getColor(tag, theme.value)}>
            {tag}
          </NTag>
        )
      })
    }
  },
  {
    title: "分数",
    key: "score",
    align: "right"
  }
]

/* 题目数据 */
const problems = ref<PagedData<Problem>>({
  data: [],
  count: 0
})

const keyword = ref<string>("")
const keywordTag = ref<string | null>(null)

const theme = computed(() => {
  return store.state.theme
})

onBeforeMount(() => {
  setTitle("练习")

  if ("page" in route.query) {
    pagination.value.page = Number(route.query.page)
  }

  if ("keyword" in route.query) {
    keyword.value = String(route.query.keyword)
    keywordTag.value = keyword.value
  }

  queryProblems()
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })
}

function clearKeyword() {
  router.push({
    query: {}
  })
}

function search() {
  if (keyword.value !== "") {
    router.push({
      query: { keyword: keyword.value }
    })
  }
}

/**
 * 获取题目
 */
function queryProblems() {
  pagination.value.loading = true
  const { page, pageSize } = pagination.value
  ProblemApi.getAllOpened(page, pageSize, keyword.value)
    .then((data) => {
      problems.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(`${error.code}: ${error.msg}`)
    })
    .finally(() => {
      pagination.value.loading = false
    })
}
</script>
