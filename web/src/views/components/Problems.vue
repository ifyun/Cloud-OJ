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
import { EmptyData } from "@/components"
import { useStore } from "@/store"
import { ResultTypes } from "@/type"
import { CheckCircleFilled, ErrorRound, SearchRound } from "@vicons/material"
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
import { type Component, computed, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()

const pagination = ref({
  page: 1,
  pageSize: 20
})

const loading = ref<boolean>(true)

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
      <RouterLink to={{ name: "submission", params: { pid: row.problemId } }}>
        <NButton text={true} strong={true}>
          {row.title}
        </NButton>
      </RouterLink>
    )
  },
  {
    title: () => (store.user.isLoggedIn ? "状态" : ""),
    key: "result",
    width: store.user.isLoggedIn ? 100 : 0,
    align: "center",
    render: (row) => {
      let icon: Component
      if (typeof row.result !== "undefined") {
        icon = row.result! === 0 ? CheckCircleFilled : ErrorRound

        const { type, text } = {
          type: ResultTypes[row.result!],
          text: row.resultText
        }

        return (
          <NTag size="small" bordered={false} type={type as any}>
            {{
              icon: () => <NIcon component={icon} />,
              default: () => <span>{text}</span>
            }}
          </NTag>
        )
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
      return tags.map((tag) => {
        return (
          <NTag
            class="tag"
            size="small"
            type="primary"
            round
            bordered={false}
            // @ts-ignore
            onClick={() => tagClick(tag)}>
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

const problems = ref<Page<Problem>>({
  data: [],
  total: 0
})

const filter = ref({ keyword: "" })

const noContent = computed<boolean>(
  () => !loading.value && problems.value.total === 0
)

onMounted(() => {
  const { page = 1, keyword = "" } = route.query
  pagination.value.page = Number(page)
  filter.value.keyword = String(keyword)

  queryProblems()
})

function pageChange(page: number) {
  router.replace({
    query: { page }
  })

  queryProblems()
}

function tagClick(tag: string) {
  filter.value.keyword = tag
  search()
}

function search() {
  nextTick(() => {
    if (filter.value.keyword !== "") {
      pagination.value.page = 1
      router.replace({
        query: { keyword: filter.value.keyword, page: 1 }
      })
    }

    queryProblems()
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
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
