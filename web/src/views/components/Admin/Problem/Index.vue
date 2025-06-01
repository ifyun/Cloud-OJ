<template>
  <div class="admin-wrap">
    <n-space vertical size="large">
      <n-space align="center" justify="space-between">
        <n-space align="center">
          <n-input-group>
            <n-input
              v-model:value="filter.keyword"
              maxlength="10"
              show-count
              clearable
              placeholder="输入题目名称、分类"
              @clear="search">
              <template #prefix>
                <n-icon class="input-prefix-icon">
                  <search-round />
                </n-icon>
              </template>
            </n-input>
            <n-button type="primary" @click="search">
              <template #icon>
                <n-icon>
                  <manage-search-round />
                </n-icon>
              </template>
              搜索题目
            </n-button>
          </n-input-group>
        </n-space>
        <n-space align="center" justify="end">
          <n-button-group>
            <n-button type="info" tertiary>
              <template #icon>
                <n-icon>
                  <add-icon />
                </n-icon>
              </template>
              <router-link
                :to="{ name: 'edit_problem', params: { id: 'new' } }">
                创建题目
              </router-link>
            </n-button>
          </n-button-group>
        </n-space>
      </n-space>
      <n-data-table
        :row-props="rowProps"
        :columns="problemColumns"
        :data="problems.data"
        :loading="loading" />
      <n-pagination
        v-model:page="pagination.page"
        :page-size="pagination.pageSize"
        :item-count="problems.total"
        @update:page="pageChange">
        <template #prefix="{ itemCount }">共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-space>
  </div>
  <n-dropdown
    trigger="manual"
    :show-arrow="true"
    :x="point.x"
    :y="point.y"
    :options="operations"
    :show="showOperations"
    :on-clickoutside="hideOperation"
    @select="operationSelect" />
</template>

<script setup lang="tsx">
import { ProblemApi } from "@/api/request"
import { ErrorMessage, type Page, Problem } from "@/api/type"
import { useStore } from "@/store"
import { renderIcon } from "@/utils"
import {
  DeleteForeverRound as DelIcon,
  EditNoteRound as EditIcon,
  FolderZipRound as FolderIcon,
  ManageSearchRound,
  PostAddRound as AddIcon,
  SearchRound
} from "@vicons/material"
import {
  type DataTableColumns,
  NButton,
  NButtonGroup,
  NDataTable,
  NDropdown,
  NFormItem,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSpace,
  NSwitch,
  NTag,
  useDialog,
  useMessage
} from "naive-ui"
import { type HTMLAttributes, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref<boolean>(true)

const pagination = ref({
  page: 1,
  pageSize: 20
})

const problems = ref<Page<Problem>>({
  data: [],
  total: 0
})

const point = ref({
  x: 0,
  y: 0
})

const filter = ref({ keyword: "" })
const showOperations = ref<boolean>(false)

let selectedId: number | undefined
let selectedTitle: string | undefined
let confirmDelete = ""

/* 表格行 ContextMenu */
const rowProps = (row: Problem): HTMLAttributes => {
  return {
    onContextmenu: (e: MouseEvent) => {
      if ((e.target as Element).tagName !== "TD") {
        return
      }

      e.preventDefault()
      showOperations.value = false

      nextTick().then(() => {
        selectedId = row.problemId
        selectedTitle = row.title
        point.value = {
          x: e.clientX,
          y: e.clientY
        }

        showOperations.value = true
      })
    }
  }
}

/* 操作（右键菜单） */
const operations = [
  {
    label: "编辑",
    key: "edit",
    icon: renderIcon(EditIcon)
  },
  {
    label: "数据管理",
    key: "test_data",
    icon: renderIcon(FolderIcon, "#18A058")
  },
  {
    label: "删除",
    key: "del",
    icon: renderIcon(DelIcon, "#F17C67")
  }
]

// region 表格列选项
const problemColumns: DataTableColumns<Problem> = [
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
      <RouterLink to={{ name: "submission", params: { pid: row.problemId } }}>
        <NButton text>{row.title}</NButton>
      </RouterLink>
    )
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
      return tags.map((tag) => (
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
      ))
    }
  },
  {
    title: "分数",
    align: "right",
    key: "score"
  },
  {
    title: "是否开放",
    key: "enable",
    align: "center",
    width: 120,
    render: (row) => (
      <NSwitch
        value={row.enable}
        round={false}
        onUpdateValue={(value: boolean) => toggleIsEnable(row, value)}>
        {{ checked: () => "开放", unchecked: () => "禁用" }}
      </NSwitch>
    )
  }
]
// endregion

const delRule = {
  trigger: ["input", "blur"],
  validator() {
    if (confirmDelete !== selectedTitle) {
      return new Error("输入题目名称")
    }
  }
}

onMounted(() => {
  const { page = 1, keyword = "" } = route.query
  pagination.value.page = Number(page)
  filter.value.keyword = String(keyword)

  queryProblems()
})

/**
 * 关闭操作菜单
 */
function hideOperation() {
  showOperations.value = false
}

/**
 * 操作菜单选择
 */
function operationSelect(key: string) {
  hideOperation()
  switch (key) {
    case "edit":
      router.push({ name: "edit_problem", params: { id: selectedId } })
      break
    case "test_data":
      router.push({ name: "test_data", params: { id: selectedId } })
      break
    case "del":
      deleteProblem()
      break
    default:
      return
  }
}

function pageChange(page: number) {
  router.push({
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
      router.push({
        query: { keyword: filter.value.keyword, page: 1 }
      })
    }

    queryProblems()
  })
}

function deleteProblem() {
  const d = dialog.error({
    title: "删除题目",
    showIcon: false,
    content: () => (
      <NFormItem
        label="输入题目名称确认"
        rule={delRule}
        style="margin-top: 24px">
        <NInput
          placeholder={selectedTitle}
          onUpdateValue={(value: string) => (confirmDelete = value)}
        />
      </NFormItem>
    ),
    negativeText: "取消",
    positiveText: "删除",
    onPositiveClick: () => {
      if (confirmDelete !== selectedTitle) {
        return false
      }
      d.loading = true
      ProblemApi.delete(selectedId!)
        .then(() => {
          message.warning(`${selectedTitle} 已删除！`)
          queryProblems()
          return true
        })
        .catch((error: ErrorMessage) => {
          message.error(error.toString())
          return false
        })
        .finally(() => {
          d.loading = false
        })
    }
  })
}

/**
 * 获取题目数据
 */
function queryProblems() {
  loading.value = true
  const { page, pageSize } = pagination.value
  ProblemApi.getAll(page, pageSize, filter.value.keyword)
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

/**
 * 切换开放/关闭
 */
function toggleIsEnable(p: Problem, value: boolean) {
  p.enable = value
  ProblemApi.changeState(p.problemId!, value)
    .then(() => {
      message.warning(`${p.problemId}.${p.title} 已${value ? "开放" : "禁用"}`)
    })
    .catch((err: ErrorMessage) => {
      p.enable = !value
      message.error(err.toString())
    })
}
</script>
