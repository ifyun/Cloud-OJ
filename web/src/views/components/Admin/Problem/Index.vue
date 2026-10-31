<template>
  <div class="admin-wrap">
    <n-flex vertical size="large">
      <n-flex align="center" justify="space-between">
        <n-flex align="center">
          <n-input-group>
            <n-input
              v-model:value="filter.keyword"
              maxlength="10"
              show-count
              clearable
              placeholder="输入题目名称、分类"
              @clear="search">
              <template #prefix>
                <n-icon>
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
        </n-flex>
        <n-flex align="center" justify="end">
          <n-button-group>
            <n-button type="info" tertiary>
              <template #icon>
                <n-icon>
                  <add-icon />
                </n-icon>
              </template>
              <router-link :to="{ name: 'edit-problem' }">
                创建题目
              </router-link>
            </n-button>
          </n-button-group>
        </n-flex>
      </n-flex>
      <n-data-table
        size="small"
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
    </n-flex>
  </div>
  <n-dropdown
    trigger="manual"
    :show-arrow="true"
    :x="menuPoint.x"
    :y="menuPoint.y"
    :options="operations"
    :show="menuVisible"
    :on-clickoutside="hideOperation"
    @select="operationSelect" />
</template>

<script setup lang="tsx">
import { ProblemApi } from "@/api/request"
import { ErrorMessage, type Page, Problem } from "@/api/type"
import { useContextMenu } from "@/composables"
import { useAppStore } from "@/store/app"
import { useFilterStore } from "@/store/filter"
import { renderIcon } from "@/utils"
import {
  PostAddRound as AddIcon,
  DeleteForeverRound as DelIcon,
  EditNoteRound as EditIcon,
  FolderZipRound as FolderIcon,
  ManageSearchRound,
  SearchRound
} from "@vicons/material"
import {
  type DataTableColumns,
  NButton,
  NButtonGroup,
  NDataTable,
  NDropdown,
  NFlex,
  NFormItem,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSwitch,
  NTag,
  useDialog,
  useMessage
} from "naive-ui"
import { storeToRefs } from "pinia"
import { type HTMLAttributes, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

defineOptions({
  name: "ProblemAdmin"
})

const app = useAppStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const { filter } = storeToRefs(useFilterStore())
const { menuVisible, menuPoint, openMenu } = useContextMenu()

const loading = ref<boolean>(true)
const problems = ref<Page<Problem>>({
  data: [],
  total: 0
})
const pagination = ref({
  page: 1,
  pageSize: 18
})

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

      openMenu(e, () => {
        selectedId = row.problemId
        selectedTitle = row.title
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
    key: "test-data",
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
      <RouterLink to={{ name: "problem", params: { pid: row.problemId } }}>
        <NButton text strong={true}>
          {row.title}
        </NButton>
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
  required: true,
  trigger: ["input", "blur"],
  validator() {
    if (confirmDelete !== selectedTitle) {
      return new Error("输入题目名称")
    }
  }
}

onMounted(() => {
  const { page = 1 } = route.query
  pagination.value.page = Number(page)

  queryProblems()
})

/**
 * 关闭操作菜单
 */
function hideOperation() {
  menuVisible.value = false
}

/**
 * 操作菜单选择
 */
function operationSelect(key: string) {
  hideOperation()
  switch (key) {
    case "edit":
      router.push({ name: "edit-problem", params: { id: selectedId } })
      break
    case "test-data":
      router.push({ name: "test-data", params: { id: selectedId } })
      break
    case "del":
      deleteProblem()
      break
    default:
      return
  }
}

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

  queryProblems()
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
          message.warning(`${selectedId}.${selectedTitle} 已删除！`)
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
      app.setError(err)
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
