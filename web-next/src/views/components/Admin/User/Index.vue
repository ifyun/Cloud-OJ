<template>
  <div class="admin-wrap">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-space>
          <n-input-group>
            <n-select
              v-model:value="searchType"
              :options="searchTypes"
              style="width: 150px" />
            <n-input
              clearable
              show-count
              maxlength="15"
              v-model:value="keyword" />
            <n-button type="primary" @click="search">
              <template #icon>
                <n-icon>
                  <search-icon />
                </n-icon>
              </template>
              搜索
            </n-button>
          </n-input-group>
        </n-space>
        <n-data-table
          :columns="columns"
          :data="users.data"
          :loading="pagination.loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="users.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NSpace,
  NTag,
  useMessage
} from "naive-ui"
import {
  CalendarCheck as DateIcon,
  UserShield as RoleIcon,
  UserTag as UserIcon
} from "@vicons/fa"
import { PersonSearchRound as SearchIcon } from "@vicons/material"
import { UserAvatar } from "@/components"
import { ErrorMsg, PagedData, User, UserInfo } from "@/api/type"
import { UserApi } from "@/api/request"
import { setTitle } from "@/utils"
import moment from "moment"
import { Mutations } from "@/store"

const roles = [
  { text: "用户", type: "info" },
  { text: "题目管理员", type: "warning" },
  { text: "用户管理员", type: "warning" },
  { text: "ROOT", type: "error" }
]

const route = useRoute()
const router = useRouter()
const store = useStore()
const message = useMessage()

const searchTypes = [
  { label: "用户 ID", value: 1 },
  { label: "用户名", value: 2 }
]

const searchType = ref<number>(1)
const keyword = ref<string>("")

const users = ref<PagedData<User>>({
  data: [],
  count: 0
})

const pagination = ref({
  page: 1,
  pageSize: 15,
  loading: true
})

const columns = [
  {
    title: "ID",
    key: "userId",
    width: 150
  },
  {
    title: () => (
      <NSpace size="small" align="center">
        <NIcon style="display: flex">
          <UserIcon />
        </NIcon>
        <span>用户名</span>
      </NSpace>
    ),
    render: (row: User) => (
      <NSpace align="center">
        <UserAvatar size="small" userId={row.userId} />
        <NButton text={true}>
          <b>{row.name}</b>
        </NButton>
      </NSpace>
    )
  },
  {
    title: () => (
      <NSpace size="small" justify="center" align="center">
        <NIcon style="display: flex">
          <RoleIcon />
        </NIcon>
        <span>权限</span>
      </NSpace>
    ),
    render: (row: User) => (
      <NTag size="small" type={roles[row.roleId!].type as any}>
        {roles[row.roleId!].text}
      </NTag>
    ),
    align: "center"
  },
  {
    title: () => (
      <NSpace size="small" justify="end" align="center">
        <NIcon style="display: flex">
          <DateIcon />
        </NIcon>
        <span>注册时间</span>
      </NSpace>
    ),
    align: "right",
    render: (row: User) => (
      <span>{moment(row.createAt).format("YYYY-MM-DD")}</span>
    )
  }
]

const userInfo = computed<UserInfo>(() => store.state.userInfo)

const searchParams = computed(() => {
  const val = keyword.value.trim()

  if (val.length === 0) {
    return {}
  }

  return searchType.value === 1 ? { userId: val } : { name: val }
})

onBeforeMount(() => {
  setTitle("用户管理")
  store.commit(Mutations.SET_BREADCRUMB, ["用户管理"])
  const query = route.query

  if ("page" in query) {
    pagination.value.page = Number(query.page)
  }

  if ("keyword" in query) {
    keyword.value = String(query.keyword)
  }

  if ("searchType" in query) {
    const val = Number(query.searchType)
    searchType.value = val in [1, 2] ? val : 1
  }

  queryUsers()
})

function search() {
  pagination.value.page = 1
  pageChange(pagination.value.page)
}

function pageChange(page: number) {
  keyword.value = keyword.value.trim()
  const query: any = { page }

  if (keyword.value.length > 0) {
    query.keyword = keyword
    query.searchType = searchType
  }

  router.push({
    query
  })
}

function queryUsers() {
  UserApi.getAll(
    pagination.value.page,
    pagination.value.pageSize,
    searchParams,
    userInfo.value
  )
    .then((data) => {
      users.value = data
    })
    .catch((error: ErrorMsg) => {
      message.error(error.toString())
    })
    .finally(() => {
      pagination.value.loading = false
    })
}
</script>
