<template>
  <div class="admin-wrap">
    <div style="margin: 4px">
      <n-space vertical size="large">
        <n-input-group style="width: 520px">
          <n-select
            v-model:value="filter.type"
            :options="filterOptions"
            style="width: 180px"
            @update:value="filterChange" />
          <n-input
            v-model:value="filter.keyword"
            clearable
            show-count
            maxlength="15"
            :disabled="filter.type === 0" />
          <n-button
            type="primary"
            :disabled="filter.type === 0"
            @click="search">
            <template #icon>
              <n-icon>
                <search-icon />
              </n-icon>
            </template>
            搜索用户
          </n-button>
        </n-input-group>
        <n-data-table
          :row-props="rowProps"
          :columns="columns"
          :data="users.data"
          :loading="loading" />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="users.total"
          @update:page="pageChange">
          <template #prefix="{ itemCount }"> 共 {{ itemCount }} 项</template>
        </n-pagination>
      </n-space>
    </div>
  </div>
  <n-dropdown
    trigger="manual"
    placement="bottom-start"
    :show-arrow="true"
    :x="point.x"
    :y="point.y"
    :options="operations"
    :show="showOperations"
    :on-clickoutside="hideOperation"
    @select="operationSelect" />
</template>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, type Page, User, UserFilter } from "@/api/type"
import { UserAvatar } from "@/components"
import { useStore } from "@/store"
import { renderIcon } from "@/utils"
import {
  CalendarCheck as DateIcon,
  UserShield as RoleIcon,
  UserTag as UserIcon
} from "@vicons/fa"
import {
  AdminPanelSettingsRound as AdminIcon,
  PersonSearchRound as SearchIcon,
  StarsRound
} from "@vicons/material"
import dayjs from "dayjs"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NDropdown,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NSpace,
  NTag,
  NText,
  useMessage
} from "naive-ui"
import { type HTMLAttributes, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

const store = useStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()

const filterOptions = [
  { label: "关闭过滤", value: 0 },
  { label: "用户名", value: 1 },
  { label: "昵称", value: 2 }
]

const filter = ref<UserFilter>({})
const loading = ref<boolean>(true)
const showOperations = ref<boolean>(false)
const users = ref<Page<User>>({
  data: [],
  total: 0
})

const pagination = ref({
  page: 1,
  pageSize: 15
})

const point = ref({
  x: 0,
  y: 0
})

let selectedUser: User | undefined

const rowProps = (row: User): HTMLAttributes => {
  return {
    onContextmenu: (e: MouseEvent) => {
      if ((e.target as Element).tagName !== "SPAN") {
        return
      }

      e.preventDefault()
      showOperations.value = false

      nextTick().then(() => {
        selectedUser = row
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
    key: "set_admin",
    label: () => (selectedUser!.role === 0 ? "取消管理员" : "设为管理员"),
    icon: renderIcon(AdminIcon, "#409EFF")
  },
  {
    key: "set_star",
    label: () => (selectedUser!.star ? "取消打星用户" : "设为打星用户"),
    icon: renderIcon(StarsRound, "#E6A23C")
  }
]

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
    width: 150
  },
  {
    key: "nickname",
    width: 300,
    title: () => (
      <NSpace size="small" align="center" style="margin-left: 7px">
        <NIcon style="display: flex">
          <UserIcon />
        </NIcon>
        <NText>昵称</NText>
      </NSpace>
    ),
    render: (row) => (
      <NSpace align="center" size="small">
        <UserAvatar
          size="small"
          uid={row.uid!}
          nickname={row.nickname!}
          hasAvatar={row.hasAvatar}
        />
        <RouterLink to={{ name: "account", params: { uid: row.uid! } }}>
          <NButton text strong iconPlacement="right">
            {{
              default: () => row.nickname,
              icon: () => (row.star ? <NIcon component={StarsRound} /> : null)
            }}
          </NButton>
        </RouterLink>
        {row.uid === store.user.userInfo!.uid ? (
          <NTag type="primary" size="small" round>
            你自己
          </NTag>
        ) : (
          <span />
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
          <NTag round size="small" type="info">
            {{
              default: () => "管理员",
              icon: () => <NIcon component={AdminIcon} />
            }}
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

onMounted(() => {
  if (route.query.page) {
    pagination.value.page = Number(route.query.page)
  }

  filter.value = JSON.parse(sessionStorage.getItem("query") ?? "{}")

  queryUsers()
})

function filterChange(value: number) {
  if (value == 0) {
    nextTick(() => {
      filter.value.keyword = ""
      search()
    })
  }
}

function pageChange(page: number) {
  router.push({
    query: {
      page
    }
  })

  queryUsers()
}

function search() {
  sessionStorage.setItem("query", JSON.stringify(filter.value))
  pagination.value.page = 1
  pageChange(1)
}

function queryUsers() {
  loading.value = true
  UserApi.getByFilter(
    pagination.value.page,
    pagination.value.pageSize,
    filter.value
  )
    .then((data) => {
      users.value = data
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function updateUser() {
  UserApi.update(selectedUser!, true)
    .then(() => {
      message.success("用户信息已更新")
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
    .finally(() => {
      queryUsers()
    })
}

function hideOperation() {
  showOperations.value = false
}

function operationSelect(key: string) {
  if (key === "set_admin") {
    selectedUser!.role! ^= 1
    updateUser()
  } else if (key === "set_star") {
    selectedUser!.star = !selectedUser!.star
    updateUser()
  }

  hideOperation()
}
</script>
