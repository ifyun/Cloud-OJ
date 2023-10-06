<template>
  <div class="admin-wrap">
    <error-result
      v-if="error != null"
      :error="error"
      style="margin-top: 48px" />
    <div v-else style="margin: 4px">
      <n-space vertical size="large">
        <n-space>
          <n-button type="info" secondary round>
            <template #icon>
              <n-icon>
                <playlist-add-round />
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
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="contests.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }">共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </div>
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
import { Contest, ErrorMessage, Page, UserInfo } from "@/api/type"
import { ErrorResult } from "@/components"
import { LanguageOptions } from "@/type"
import { LanguageUtil, renderIcon, setTitle, stateTag } from "@/utils"
import {
  DeleteForeverRound,
  EditNoteRound,
  KeyRound,
  PlaylistAddRound
} from "@vicons/material"
import moment from "moment-timezone"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NDropdown,
  NFormItem,
  NIcon,
  NInput,
  NPagination,
  NSpace,
  NTag,
  NText,
  useDialog,
  useMessage,
  useNotification
} from "naive-ui"
import { computed, HTMLAttributes, nextTick, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "vuex"

const timeFmt = "yyyy-MM-DD HH:mm"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()
const notification = useNotification()

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)

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
    key: "show_key",
    label: "查看邀请码",
    icon: renderIcon(KeyRound, "#18A058")
  },
  {
    key: "edit",
    label: "编辑",
    icon: renderIcon(EditNoteRound, "#409EFF")
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
      <NSpace align="center">
        <NButton text>{row.contestName}</NButton>
      </NSpace>
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
      return (
        <span style={{ wordBreak: "break-word" }}>{languages.join(" / ")}</span>
      )
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
  count: 0
})

const userInfo = computed<UserInfo>(() => store.state.userInfo)

onBeforeMount(() => {
  setTitle(route.meta.title as string)
  queryContests()
})

function hideOperation() {
  showOperations.value = false
}

function operationSelect(key: string) {
  hideOperation()
  switch (key) {
    case "show_key":
      showInviteKey()
      break
    case "edit":
      router.push({
        name: "edit_contest",
        params: { id: selectedContest!.contestId }
      })
      break
    case "del":
      deleteContest()
      break
    default:
      return
  }
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
  ContestApi.getAll(page, pageSize, userInfo.value)
    .then((data) => {
      contests.value = data
    })
    .catch((err: ErrorMessage) => {
      error.value = err
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
      ContestApi.delete(selectedContest!.contestId!, userInfo.value)
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
  const s = moment.unix(c.startAt!)
  const e = moment.unix(c.endAt!)
  let str = s.format(timeFmt) + " ~ "

  if (e.isSame(s)) {
    str += e.format("HH:mm:ss")
  } else {
    str += e.format(timeFmt)
  }

  return str
}
</script>
