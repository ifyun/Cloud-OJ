<template>
  <div class="admin-wrap">
    <error-result
      v-if="error != null"
      :error="error"
      style="margin-top: 48px" />
    <div v-else style="margin: 4px">
      <n-space vertical size="large">
        <n-input-group style="width: 520px">
          <n-select
            v-model:value="filter"
            :options="filterOptions"
            style="width: 180px"
            @update:value="filterChange" />
          <n-input
            v-model:value="filterValue"
            clearable
            show-count
            maxlength="15"
            :disabled="filter == 0" />
          <n-button type="primary" :disabled="filter == 0" @click="search">
            <template #icon>
              <n-icon>
                <search-icon />
              </n-icon>
            </template>
            搜索用户
          </n-button>
        </n-input-group>
        <n-data-table
          :columns="columns"
          :data="users.data"
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="users.count"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, Page, User } from "@/api/type"
import { ErrorResult, UserAvatar } from "@/components"
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import {
  CalendarCheck as DateIcon,
  UserShield as RoleIcon,
  UserTag as UserIcon
} from "@vicons/fa"
import { PersonSearchRound as SearchIcon } from "@vicons/material"
import dayjs from "dayjs"
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
import { onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()

const loading = ref<boolean>(true)
const error = ref<ErrorMessage | null>(null)

const filterOptions = [
  { label: "关闭过滤", value: 0 },
  { label: "用户 ID", value: 1 },
  { label: "用户名", value: 2 }
]

const filter = ref<number>(0)
const filterValue = ref<string>("")

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
    render: (_, rowIndex: number) => (
      <span>
        {(pagination.value.page - 1) * pagination.value.pageSize + rowIndex + 1}
      </span>
    )
  },
  {
    title: "用户名",
    key: "username",
    width: 140
  },
  {
    key: "name",
    title: () => (
      <NSpace size="small" align="center" style="margin-left: 7px">
        <NIcon style="display: flex">
          <UserIcon />
        </NIcon>
        <NText>昵称</NText>
      </NSpace>
    ),
    render: (row) => (
      <NSpace align="center">
        <UserAvatar
          size="small"
          uid={row.uid!}
          nickname={row.nickname!}
          hasAvatar={row.hasAvatar}
        />
        <NButton text={true} strong={true}>
          {row.nickname}
        </NButton>
        {row.uid == store.user.userInfo!.uid ? (
          <NTag type="primary" size="small" round={true}>
            你自己
          </NTag>
        ) : (
          <span></span>
        )}
      </NSpace>
    )
  },
  {
    key: "role",
    align: "center",
    title: () => (
      <NSpace size="small" align="center" justify="center">
        <NIcon style="display: flex">
          <RoleIcon />
        </NIcon>
        <NText>权限</NText>
      </NSpace>
    ),
    render: (row) => {
      if (row.role! == 0) {
        return (
          <NTag size="small" type="primary">
            管理员
          </NTag>
        )
      }
    }
  },
  {
    key: "createAt",
    align: "right",
    title: () => (
      <NSpace size="small" align="center" justify="end">
        <NIcon style="display: flex">
          <DateIcon />
        </NIcon>
        <NText>注册时间</NText>
      </NSpace>
    ),
    render: (row) => (
      <NText depth="2">{dayjs.unix(row.createAt!).format("YYYY/MM/DD")}</NText>
    )
  }
]

onBeforeMount(() => {
  setTitle(route.meta.title as string)

  if (route.query.filter) {
    filter.value = Number(route.query.filter)
    filterValue.value = route.query.filterValue as string
  }

  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  queryUsers()
})

function filterChange(value: number) {
  if (value == 0) {
    filter.value = 0
    filterValue.value = ""
    pageChange(1)
  }
}

function pageChange(page: number) {
  router.push({
    query: {
      page,
      filter: filter.value,
      filterValue: filterValue.value
    }
  })

  queryUsers()
}

function search() {
  pageChange(1)
}

function queryUsers() {
  loading.value = true
  UserApi.getByFilter(
    pagination.value.page,
    pagination.value.pageSize,
    filter.value,
    filterValue.value,
    store.user.userInfo!
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
