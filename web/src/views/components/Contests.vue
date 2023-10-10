<template>
  <div class="wrap">
    <empty-data v-if="noContent" style="margin-top: 48px" />
    <div v-else>
      <n-space vertical size="large">
        <n-data-table
          :loading="loading"
          :columns="contestColumns"
          :data="contests.data" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="contests.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </div>
  </div>
</template>

<script lang="tsx">
export default {
  name: "ContestList"
}
</script>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { Contest, ErrorMessage, Page } from "@/api/type"
import { EmptyData } from "@/components"
import { useStore } from "@/store"
import { LanguageNames } from "@/type"
import { LanguageUtil, setTitle, stateTag } from "@/utils"
import dayjs from "dayjs"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NDropdown,
  NIcon,
  NPagination,
  NSpace,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"

const timeFmt = "YYYY 年 MM 月 DD, HH:mm"

const store = useStore()
const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const selectedContest = ref<Contest | null>(null)
const contests = ref<Page<Contest>>({
  data: [],
  count: 0
})

const noContent = computed<boolean>(
  () => !loading.value && contests.value.count === 0
)

const contestColumns: DataTableColumns<Contest> = [
  {
    title: "状态",
    key: "state",
    align: "center",
    width: 100,
    render: (row) => {
      const tag = stateTag(row)
      return (
        <NTag
          round={true}
          bordered={false}
          type={tag.type}
          style="margin-top: 2px">
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
    key: "contest",
    render: (row) => (
      <NDropdown
        showArrow
        trigger="click"
        placement="bottom-end"
        options={options}
        onSelect={handleSelect}>
        <NButton text onClick={() => (selectedContest.value = row)}>
          {row.contestName}
        </NButton>
      </NDropdown>
    )
  },
  {
    title: "语言限制",
    key: "languages",
    render: (row: Contest) => {
      if (row.languages === 511) {
        return <span>没有限制</span>
      }
      const languages: Array<string> = []
      LanguageUtil.toArray(row.languages).forEach((value) => {
        languages.push(LanguageNames[value])
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

const options = [
  {
    label: "进入竞赛",
    key: 1
  },
  {
    label: "查看排名",
    key: 2
  }
]

onBeforeMount(() => {
  setTitle("竞赛 & 练习")
  queryContests()
})

function pageChange(page: number) {
  router.push({
    query: { page }
  })

  queryContests()
}

function queryContests() {
  ContestApi.getAll(pagination.value.page, pagination.value.pageSize)
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

function handleSelect(key: number) {
  const { contestId, started } = selectedContest.value!

  if (!started) {
    message.info("还没开始呢！")
    return
  }

  if (key === 1) {
    router.push({
      name: "contest_problems",
      params: { cid: contestId }
    })
  } else if (key === 2) {
    router.push({
      name: "leaderboard",
      query: { cid: contestId }
    })
  }
}

function calcTimeRange(c: Contest): string {
  const s = dayjs.unix(c.startAt!)
  const e = dayjs.unix(c.endAt!)
  let str = s.format(timeFmt) + " ~ "

  if (e.isSame(s, "day")) {
    str += e.format("HH:mm")
  } else if (e.isSame(s, "year")) {
    str += e.format("MM 月 DD, HH:mm")
  } else {
    str += e.format(timeFmt)
  }

  return str
}
</script>
