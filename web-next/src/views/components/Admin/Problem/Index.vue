<template>
  <div class="admin-wrap">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space align="center" justify="space-between">
          <n-space align="center">
            <n-input-group>
              <n-input
                v-model:value="keyword"
                maxlength="10"
                show-count
                clearable
                placeholder="输入题目名称、分类">
                <template #prefix>
                  <n-icon class="input-prefix-icon">
                    <search-icon />
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
            <n-tag v-if="keywordTag != null" closable @close="clearKeyword">
              {{ keywordTag }}
            </n-tag>
          </n-space>
          <n-space align="center" justify="end">
            <n-button-group>
              <n-button type="primary">
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
  <n-dropdown
    trigger="manual"
    :show-arrow="true"
    :x="point.x"
    :y="point.y"
    :options="operations"
    :show="showOperations"
    @select="operationSelect"
    :on-clickoutside="hideOperation" />
</template>

<script setup lang="tsx">
import { computed, nextTick, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  NButton,
  NButtonGroup,
  NCard,
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
import {
  FileArchive as FileIcon,
  Search as SearchIcon,
  Tags as TagsIcon
} from "@vicons/fa"
import {
  DeleteForeverRound as DelIcon,
  EditNoteRound as EditIcon,
  ManageSearchRound,
  PostAddRound as AddIcon
} from "@vicons/material"
import { ErrorMsg, PagedData, Problem, UserInfo } from "@/api/type"
import { renderIcon, setTitle, TagUtil } from "@/utils"
import { ProblemApi } from "@/api/request"
import { Mutations } from "@/store"

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()
const dialog = useDialog()

const pagination = ref({
  page: 1,
  pageSize: 20,
  loading: true
})

const problems = ref<PagedData<Problem>>({
  data: [],
  count: 0
})

const keyword = ref<string>("")
const keywordTag = ref<string | null>(null)
const showOperations = ref<boolean>(false)

const point = ref({
  x: 0,
  y: 0
})

let selectedId: number | undefined
let selectedTitle: string | undefined
let confirmDelete = ""

/* 表格行 ContextMenu */
const rowProps = (row: Problem) => {
  return {
    onContextmenu: (e: PointerEvent) => {
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
    label: "管理测试数据",
    key: "test_data",
    icon: renderIcon(FileIcon, "#18A058")
  },
  {
    label: "删除",
    key: "del",
    icon: renderIcon(DelIcon, "#F17C67")
  }
]

// region 表格列选项
const problemColumns = [
  {
    title: "ID",
    key: "problemId",
    align: "right",
    width: 70
  },
  {
    title: "题目名称",
    key: "title",
    render: (row: Problem) => (
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
    title: () => (
      <NSpace size="small" justify="start" align="center">
        <NIcon style="display: flex">
          <TagsIcon />
        </NIcon>
        <span>分类</span>
      </NSpace>
    ),
    render: (row: Problem) => {
      if (row.category === "") {
        return ""
      }
      const tags = row.category.split(",")
      return tags.map((tag) => (
        <NTag class="tag" size="small" color={TagUtil.getColor(tag, theme)}>
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
    align: "center",
    width: 120,
    render: (row: Problem) => (
      <NSwitch
        value={row.enable}
        onUpdateValue={(value) => toggleIsEnable(row, value)}>
        {{ checked: () => "开放", unchecked: () => "关闭" }}
      </NSwitch>
    )
  }
]

// endregion

const theme = computed(() => {
  return store.state.theme
})

const userInfo = computed<UserInfo>(() => {
  return store.state.userInfo
})

onBeforeMount(() => {
  setTitle("题目管理")
  store.commit(Mutations.SET_BREADCRUMB, ["题目管理"])

  if ("page" in route.query) {
    pagination.value.page = Number(route.query.page)
  }

  if ("keyword" in route.query) {
    keyword.value = String(route.query.keyword)
    keywordTag.value = keyword.value
  }

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

function deleteProblem() {
  const d = dialog.error({
    title: "删除题目",
    showIcon: false,
    content: () => (
      <NFormItem label="输入题目名称确认" style="margin-top: 24px">
        <NInput
          placeholder={selectedTitle}
          value={confirmDelete}
          onUpdateValue={(value) => (confirmDelete = value)}
        />
      </NFormItem>
    ),
    negativeText: "取消",
    positiveText: "删除",
    onPositiveClick: () => {
      if (confirmDelete !== selectedTitle) {
        message.error("请输入题目名称")
        return false
      }
      d.loading = true
      ProblemApi.delete(selectedId!, userInfo.value)
        .then(() => {
          message.warning(`${selectedTitle} 已删除！`)
          queryProblems()
          return true
        })
        .catch((error: ErrorMsg) => {
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
  pagination.value.loading = true
  const { page, pageSize } = pagination.value
  ProblemApi.getAll(page, pageSize, keyword.value, userInfo.value)
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

/**
 * 切换开放/关闭
 */
function toggleIsEnable(p: Problem, value: boolean) {
  ProblemApi.changeState(p.problemId!, value, userInfo.value)
    .then(() => {
      p.enable = value
      message.info(`${p.title} 已${value ? "开放" : "关闭"}`)
    })
    .catch((error: ErrorMsg) => {
      message.error(`${error.code}: ${error.msg}`)
    })
}
</script>
