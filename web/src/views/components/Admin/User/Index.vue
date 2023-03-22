<template>
  <div class="admin-wrap">
    <error-result
      v-if="error != null"
      :error="error"
      style="margin-top: 48px" />
    <div v-else style="margin: 4px">
      <n-space vertical size="large">
        <n-space>
          <n-input-group>
            <n-select
              v-model:value="searchType"
              :options="searchTypes"
              style="width: 150px" />
            <n-input
              v-model:value="keyword"
              clearable
              show-count
              maxlength="15" />
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
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="users.count"
          simple
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import {
  DataTableColumns,
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NSpace,
  NTag,
  NText
} from "naive-ui"
import {
  CalendarCheck as DateIcon,
  UserShield as RoleIcon,
  UserTag as UserIcon
} from "@vicons/fa"
import { PersonSearchRound as SearchIcon } from "@vicons/material"
import { ErrorResult, UserAvatar } from "@/components"
import { ErrorMessage, Page, User, UserInfo } from "@/api/type"
import { UserApi } from "@/api/request"
import { setTitle } from "@/utils"
import moment from "moment"
import { Mutations } from "@/store"

const roles = [
  { text: "ADMIN", type: "primary" },
  { text: "用户", type: "info" },
  { text: "题目管理员", type: "warning" },
  { text: "用户管理员", type: "warning" }
]

const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)

const searchTypes = [
  { label: "用户 ID", value: 1 },
  { label: "用户名", value: 2 }
]

const searchType = ref<number>(1)
const keyword = ref<string>("")

const users = ref<Page<User>>({
  data: [],
  count: 0
})

const pagination = ref({
  page: 1,
  pageSize: 15
})

const columns: DataTableColumns<User> = [
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
    key: "userId",
    width: 140
  },
  {
    key: "name",
    title: () => (
      <NSpace size="small" align="center">
        <NIcon style="display: flex">
          <UserIcon />
        </NIcon>
        <NText>用户名称</NText>
      </NSpace>
    ),
    render: (row) => (
      <NSpace align="center">
        <UserAvatar size="small" userId={row.userId} />
        <NButton text={true}>
          <span style="font-weight: 500">{row.name}</span>
        </NButton>
      </NSpace>
    )
  },
  {
    key: "role",
    align: "center",
    title: () => (
      <NSpace size="small" justify="center" align="center">
        <NIcon style="display: flex">
          <RoleIcon />
        </NIcon>
        <NText>权限</NText>
      </NSpace>
    ),
    render: (row) => (
      <NTag size="small" type={roles[row.roleId!].type as any}>
        {roles[row.roleId!].text}
      </NTag>
    )
  },
  {
    key: "createAt",
    align: "right",
    title: () => (
      <NSpace size="small" justify="end" align="center">
        <NIcon style="display: flex">
          <DateIcon />
        </NIcon>
        <NText>注册时间</NText>
      </NSpace>
    ),
    render: (row) => (
      <span>{moment.unix(row.createAt!).format("yyyy/MM/DD")}</span>
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
    searchType.value = val === 1 || val === 2 ? val : 1
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
    query.keyword = keyword.value
    query.searchType = searchType.value
  }

  router.push({
    query
  })
}

function queryUsers() {
  loading.value = true
  UserApi.getAll(
    pagination.value.page,
    pagination.value.pageSize,
    searchParams.value,
    userInfo.value
  )
    .then((data) => {
      users.value = data
    })
    .catch((err: ErrorMessage) => {
      error.value = err
    })
    .finally(() => {
      loading.value = false
    })
}
</script>
