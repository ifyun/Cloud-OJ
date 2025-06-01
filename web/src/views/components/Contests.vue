<template>
  <div class="wrap">
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
      </n-flex>
      <n-data-table
        :loading="loading"
        :columns="contestColumns"
        :data="contests.data" />
      <n-pagination
        v-model:page="pagination.page"
        :page-size="pagination.pageSize"
        :item-count="contests.total"
        @update:page="pageChange">
        <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
      </n-pagination>
    </n-flex>
  </div>
</template>

<script lang="tsx">
export default {
  name: "ContestList"
}
</script>

<script setup lang="tsx">
import { ContestApi } from "@/api/request"
import { Contest, ContestFilter, ErrorMessage, type Page } from "@/api/type"
import { useStore } from "@/store"
import { LanguageNames } from "@/type"
import { LanguageUtil, stateTag } from "@/utils"
import { SearchRound } from "@vicons/material"
import dayjs from "dayjs"
import {
  type DataTableColumns,
  NButton,
  NCheckbox,
  NDataTable,
  NDropdown,
  NFlex,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { onMounted, ref } from "vue"
import { useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()

const pagination = ref({
  page: 1,
  pageSize: 15
})

const loading = ref<boolean>(true)
const filter = ref<ContestFilter>({
  keyword: "",
  hideEnded: false
})
const selectedContest = ref<Contest | null>(null)
const contests = ref<Page<Contest>>({
  data: [],
  total: 0
})

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

onMounted(() => {
  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  filter.value = JSON.parse(sessionStorage.getItem("query") ?? "{}")
  queryContests()
})

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
  ContestApi.getAll(
    pagination.value.page,
    pagination.value.pageSize,
    filter.value
  )
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
    if (store.user.isLoggedIn) {
      router.push({
        name: "contest_problems",
        params: { cid: contestId }
      })
    } else {
      router.push({ name: "auth", params: { tab: "login" } })
    }
  } else if (key === 2) {
    router.push({
      name: "scoreboard_contest",
      params: { cid: contestId }
    })
  }
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
