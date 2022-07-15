<template>
  <div class="admin-wrap">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space>
          <n-button type="primary">
            <template #icon>
              <n-icon>
                <add-icon />
              </n-icon>
            </template>
            <router-link :to="{ name: 'edit_contest', params: { id: 'new' } }">
              创建竞赛
            </router-link>
          </n-button>
        </n-space>
        <n-data-table
          :row-props="rowProps"
          :columns="contestColumns"
          :data="contests.data"
          :loading="pagination.loading" />
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
import { useRouter } from "vue-router"
import {
  NButton,
  NCard,
  NDataTable,
  NDropdown,
  NIcon,
  NPagination,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import {
  DeleteForeverRound as DelIcon,
  EditNoteRound as EditIcon,
  PlaylistAddRound as AddIcon
} from "@vicons/material"
import { Contest, ErrorMsg, PagedData, UserInfo } from "@/api/type"
import { LanguageUtil, renderIcon, setTitle } from "@/utils"
import { LanguageOptions } from "@/type"
import { ContestApi } from "@/api/request"
import Mutations from "@/store/mutations"

let selectedId: number | undefined

type StateTag = {
  type: "info" | "error" | "success"
  state: string
}

const store = useStore()
const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const showOperations = ref<boolean>(false)

const point = ref({
  x: 0,
  y: 0
})

const rowProps = (row: Contest) => {
  return {
    onContextmenu: (e: PointerEvent) => {
      if ((e.target as Element).tagName !== "TD") {
        return
      }
      e.preventDefault()
      showOperations.value = false
      nextTick().then(() => {
        selectedId = row.contestId
        point.value = {
          x: e.clientX,
          y: e.clientY
        }
        showOperations.value = true
      })
    }
  }
}

const operations = [
  {
    key: "edit",
    label: "编辑",
    icon: renderIcon(EditIcon, "#399F58")
  },
  {
    key: "del",
    label: "删除",
    icon: renderIcon(DelIcon, "#F17C67")
  }
]

// #region 表格列选项
const contestColumns = [
  {
    title: "状态",
    align: "center",
    width: 120,
    render: (row: Contest) => {
      const tag = stateTag(row)
      return <NTag type={tag.type}>{tag.state}</NTag>
    }
  },
  {
    title: "竞赛",
    render: (row: Contest) => (
      <NSpace align="center">
        <NButton text>{row.contestName}</NButton>
      </NSpace>
    )
  },
  {
    title: "语言限制",
    render: (row: Contest) => {
      if (row.languages === 511) {
        return "没有限制"
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
// endregion

const contests = ref<PagedData<Contest>>({
  data: [],
  count: 0
})

const userInfo = computed<UserInfo>(() => store.state.userInfo)

onBeforeMount(() => {
  setTitle("竞赛管理")
  store.commit(Mutations.SET_BREADCRUMB, ["竞赛管理"])
  queryContests()
})

function hideOperation() {
  showOperations.value = false
}

function operationSelect(key: string) {
  hideOperation()
  switch (key) {
    case "edit":
      router.push({ name: "edit_contest", params: { id: selectedId } })
      break
    default:
      return
  }
}

/**
 * 竞赛状态标签
 */
function stateTag(c: Contest): StateTag {
  if (c.ended) {
    return {
      type: "error",
      state: "已结束"
    }
  } else if (c.started) {
    return {
      type: "success",
      state: "进行中"
    }
  } else {
    return {
      type: "info",
      state: "未开始"
    }
  }
}

function pageChange(page: number) {
  router.push({
    query: { page }
  })
}

function queryContests() {
  const { page, pageSize } = pagination.value
  ContestApi.getAll(page, pageSize, userInfo.value)
    .then((data) => {
      contests.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      pagination.value.loading = false
    })
}
</script>
