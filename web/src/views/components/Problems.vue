<template>
  <div class="wrap">
    <error-result v-if="error != null" :error="error" />
    <empty-data v-else-if="noContent" style="margin-top: 48px" />
    <div v-else>
      <n-space vertical size="large">
        <n-space align="center">
          <n-input-group>
            <n-input
              v-model:value="keyword"
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
        </n-space>
        <n-data-table
          single-column
          :columns="problemColumns"
          :data="problems.data"
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="problems.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
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
import { ErrorMessage, Page, Problem } from "@/api/type"
import { EmptyData, ErrorResult } from "@/components"
import { ResultTypes } from "@/type"
import { setTitle } from "@/utils"
import { CheckCircleFilled, ErrorRound, SearchRound } from "@vicons/material"
import {
  DataTableColumns,
  DataTableColumn,
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSpace,
  NTag
} from "naive-ui"
import { Component, computed, nextTick, onBeforeMount, ref, render } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "vuex"

const store = useStore()
const route = useRoute()
const router = useRouter()

const pagination = ref({
  page: 1,
  pageSize: 20
})

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)

const userInfo = computed(() => store.state.userInfo)

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
    title: "题名",
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
    title: "状态",
    key: "result",
    width: 100,
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

/* 题目数据 */
const problems = ref<Page<Problem>>({
  data: [],
  count: 0
})

const keyword = ref<string>("")

const noContent = computed<boolean>(
  () => !loading.value && problems.value.count === 0
)

onBeforeMount(() => {
  setTitle("题目")

  if ("page" in route.query) {
    pagination.value.page = Number(route.query.page)
  }

  if ("keyword" in route.query) {
    keyword.value = String(route.query.keyword)
  }

  queryProblems()
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })

  queryProblems()
}

function tagClick(tag: string) {
  keyword.value = tag
  search()
}

function search() {
  nextTick(() => {
    if (keyword.value !== "") {
      router.push({
        query: { keyword: keyword.value }
      })
    }

    pageChange(1)
  })
}

/**
 * 获取题目
 */
function queryProblems() {
  loading.value = true
  const { page, pageSize } = pagination.value
  ProblemApi.getAllOpened(page, pageSize, keyword.value, userInfo.value)
    .then((data) => {
      problems.value = data
    })
    .catch((err: ErrorMessage) => {
      error.value = err
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
