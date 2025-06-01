<template>
  <div class="admin-wrap">
    <n-flex vertical size="large">
      <n-flex align="center">
        <n-input-group style="width: auto">
          <n-input
            v-model:value="filter.keyword"
            clearable
            show-count
            maxlength="30"
            placeholder="输入关键字" />
          <n-button type="primary" @click="search">
            搜 索
            <template #icon>
              <n-icon>
                <search-round />
              </n-icon>
            </template>
          </n-button>
        </n-input-group>
        <n-checkbox :checked="filter.hideEnded" @updateChecked="checkboxChange">
          隐藏已结束竞赛
        </n-checkbox>
        <n-button type="info" tertiary style="margin-left: auto">
          <template #icon>
            <n-icon>
              <playlist-add-round />
            </n-icon>
          </template>
          <router-link :to="{ name: 'edit_contest', params: { id: 'new' } }">
            创建竞赛
          </router-link>
        </n-button>
      </n-flex>
      <n-data-table
        :row-props="rowProps"
        :columns="contestColumns"
        :data="contests.data"
        :loading="loading" />
      <n-pagination
        v-model:page="pagination.page"
        :page-size="pagination.pageSize"
        :item-count="contests.total"
        @update:page="pageChange">
        <template #prefix="{ itemCount }">共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-flex>
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
import { ContestApi } from "@/api/request"
import { Contest, ContestFilter, ErrorMessage, type Page } from "@/api/type"
import { useStore } from "@/store"
import { LanguageOptions } from "@/type"
import { LanguageUtil, renderIcon, stateTag } from "@/utils"
import {
  DeleteForeverRound,
  EditNoteRound,
  KeyRound,
  PlaylistAddRound,
  RefreshRound,
  SearchRound
} from "@vicons/material"
import dayjs from "dayjs"
import {
  type DataTableColumns,
  NButton,
  NCheckbox,
  NDataTable,
  NDropdown,
  NEllipsis,
  NFlex,
  NFormItem,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NTag,
  NText,
  useDialog,
  useMessage,
  useNotification
} from "naive-ui"
import { type HTMLAttributes, nextTick, onMounted, ref } from "vue"
import { useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()
const notification = useNotification()

const loading = ref<boolean>(true)
const filter = ref<ContestFilter>({
  keyword: "",
  hideEnded: false
})
const pagination = ref({
  page: 1,
  pageSize: 15
})

const point = ref({
  x: 0,
  y: 0
})

const showOperations = ref<boolean>(false)

let selectedContest: Contest | null
let confirmDelete = ""

const rowProps = (row: Contest): HTMLAttributes => {
  return {
    onContextmenu: (e: MouseEvent) => {
      if ((e.target as Element).tagName !== "TD") {
        return
      }

      e.preventDefault()
      showOperations.value = false

      nextTick().then(() => {
        // 设置当前选中的竞赛
        selectedContest = row
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
    icon: renderIcon(EditNoteRound, "#409EFF")
  },
  {
    key: "show_key",
    label: "查看邀请码",
    icon: renderIcon(KeyRound, "#18A058")
  },
  {
    key: "new_key",
    label: "重新生成邀请码",
    icon: renderIcon(RefreshRound, "#409EFF")
  },
  {
    key: "del",
    label: "删除",
    icon: renderIcon(DeleteForeverRound, "#F17C67")
  }
]

// #region 表格列选项
const contestColumns: DataTableColumns<Contest> = [
  {
    title: "状态",
    key: "state",
    align: "center",
    width: 120,
    render: (row) => {
      const tag = stateTag(row)
      return (
        <NTag round={true} bordered={false} type={tag.type}>
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
    key: "name",
    render: (row) => (
      <NButton text onClick={(event) => titleClick(event, row)}>
        {row.contestName}
      </NButton>
    )
  },
  {
    title: "语言限制",
    key: "languages",
    render: (row) => {
      if (row.languages === 511) {
        return "没有限制"
      }

      const languages: Array<string> = []
      LanguageUtil.toArray(row.languages).forEach((value) => {
        languages.push(LanguageOptions[value].label)
      })

      return <NEllipsis>{languages.join(" / ")}</NEllipsis>
    }
  },
  {
    title: "开始/结束时间",
    key: "startAt",
    align: "center",
    render: (row) => <NText>{calcTimeRange(row)}</NText>
  }
]
// endregion

const delRule = {
  trigger: ["input", "blur"],
  validator() {
    if (confirmDelete !== selectedContest!.contestName) {
      return new Error("输入竞赛名称")
    }
  }
}

const contests = ref<Page<Contest>>({
  data: [],
  total: 0
})

onMounted(() => {
  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  filter.value = JSON.parse(sessionStorage.getItem("query") ?? "{}")

  queryContests()
})

function titleClick(e: MouseEvent, row: Contest) {
  nextTick().then(() => {
    selectedContest = row
    point.value = {
      x: e.clientX,
      y: e.clientY
    }

    showOperations.value = true
  })
}

function hideOperation() {
  showOperations.value = false
}

function operationSelect(key: string) {
  hideOperation()
  switch (key) {
    case "edit":
      router.push({
        name: "edit_contest",
        params: { id: selectedContest!.contestId }
      })
      break
    case "show_key":
      showInviteKey()
      break
    case "new_key":
      newInviteKey()
      break
    case "del":
      deleteContest()
      break
    default:
      return
  }
}

function search() {
  sessionStorage.setItem("query", JSON.stringify(filter.value))
  pagination.value.page = 1
  pageChange(1)
}

function pageChange(page: number) {
  router.push({
    query: { page }
  })

  queryContests()
}

function queryContests() {
  loading.value = true
  const { page, pageSize } = pagination.value
  ContestApi.getAll(page, pageSize, filter.value)
    .then((data) => {
      contests.value = data
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function showInviteKey() {
  notification.info({
    title: selectedContest!.contestName,
    description: `邀请码: ${selectedContest!.inviteKey}`,
    duration: 6000
  })
}

function newInviteKey() {
  ContestApi.newInviteKey(selectedContest!.contestId!)
    .then((data) => {
      selectedContest!.inviteKey = data
      notification.info({
        title: selectedContest!.contestName,
        description: `新邀请码: ${data}`,
        duration: 6000
      })
    })
    .catch((err: ErrorMessage) => {
      notification.error({
        title: "重新生成邀请码失败",
        description: err.toString(),
        duration: 3000
      })
    })
}

function deleteContest() {
  const d = dialog.error({
    title: "删除竞赛",
    showIcon: false,
    content: () => (
      <NFormItem
        label="输入竞赛名称确认"
        rule={delRule}
        style="margin-top: 24px">
        <NInput
          placeholder={selectedContest!.contestName}
          onUpdateValue={(value) => (confirmDelete = value)}
        />
      </NFormItem>
    ),
    negativeText: "取消",
    positiveText: "删除",
    onPositiveClick: () => {
      if (confirmDelete !== selectedContest!.contestName) {
        return false
      }
      d.loading = true
      ContestApi.delete(selectedContest!.contestId!)
        .then(() => {
          message.warning(`${selectedContest!.contestName} 已删除！`)
          queryContests()
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

function calcTimeRange(c: Contest): string {
  const timeFmt = "YYYY 年 M 月 D 日 H:mm"
  const now = dayjs()
  const s = dayjs.unix(c.startAt!)
  const e = dayjs.unix(c.endAt!)

  let str: string

  if (s.isSame(now, "day")) {
    str = s.format("今天 H:mm ~ ")
  } else {
    str = s.format(timeFmt) + " ~ "
  }

  if (e.isSame(s, "day")) {
    str += e.format("H:mm")
  } else if (e.isSame(s, "year")) {
    str += e.format("M 月 D 日 HH:mm")
  } else {
    str += e.format(timeFmt)
  }

  return str
}

function checkboxChange(value: boolean) {
  filter.value.hideEnded = value
}
</script>
