<template>
  <div class="admin-wrap">
    <div style="margin: 4px">
      <n-flex vertical size="large">
        <n-input-group style="width: 520px">
          <n-select
            v-model:value="filter.type"
            :options="filterOptions"
            style="width: 180px"
            @update:value="filterChange" />
          <n-input
            v-model:value="filter.value"
            clearable
            show-count
            maxlength="15"
            :disabled="disableFilter" />
          <n-button type="primary" :disabled="disableFilter" @click="search">
            <template #icon>
              <n-icon>
                <search-icon />
              </n-icon>
            </template>
            搜索用户
          </n-button>
        </n-input-group>
        <n-data-table
          size="small"
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
      </n-flex>
    </div>
  </div>
  <n-dropdown
    trigger="manual"
    placement="bottom-start"
    :show-arrow="true"
    :x="menuPoint.x"
    :y="menuPoint.y"
    :options="operations"
    :show="menuVisible"
    :on-clickoutside="hideOperation"
    @select="operationSelect" />
</template>

<script setup lang="tsx">
import { UserApi } from "@/api/request"
import { ErrorMessage, type Page, User } from "@/api/type"
import { UserAvatar } from "@/components"
import { useContextMenu } from "@/composables"
import { useAppStore } from "@/store/app"
import { useFilterStore } from "@/store/filter"
import { useUserStore } from "@/store/user"
import { renderIcon } from "@/utils"
import {
  CalendarCheck as DateIcon,
  UserShield as RoleIcon,
  UserTag as UserIcon
} from "@vicons/fa"
import {
  AdminPanelSettingsRound as AdminIcon,
  LockResetRound,
  PersonSearchRound as SearchIcon,
  StarRound
} from "@vicons/material"
import dayjs from "dayjs"
import {
  type DataTableColumns,
  NButton,
  NDataTable,
  NDropdown,
  NFlex,
  NIcon,
  NInput,
  NInputGroup,
  NPagination,
  NSelect,
  NTag,
  NText,
  useMessage,
  useModal,
  useNotification
} from "naive-ui"
import { storeToRefs } from "pinia"
import { computed, type HTMLAttributes, nextTick, onMounted, ref } from "vue"
import { RouterLink, useRoute, useRouter } from "vue-router"

defineOptions({
  name: "UserAdmin"
})

const app = useAppStore()
const user = useUserStore()
const route = useRoute()
const router = useRouter()
const message = useMessage()
const modal = useModal()
const notification = useNotification()

const filterOptions = [
  { label: "关闭过滤", value: 0 },
  { label: "用户名", value: 1 },
  { label: "昵称", value: 2 }
]

const { filter } = storeToRefs(useFilterStore())
const { menuVisible, menuPoint, openMenu } = useContextMenu()

const loading = ref<boolean>(true)
const users = ref<Page<User>>({
  data: [],
  total: 0
})
const pagination = ref({
  page: 1,
  pageSize: 18
})

const disableFilter = computed(
  () => !filter.value.type || filter.value.type === 0
)

let selectedUser: User | undefined

const rowProps = (row: User): HTMLAttributes => {
  return {
    onContextmenu: (e: MouseEvent) => {
      if ((e.target as Element).tagName !== "SPAN") {
        return
      }

      openMenu(e, () => {
        selectedUser = row
      })
    }
  }
}

const operations = [
  {
    key: "set_admin",
    label: () => (selectedUser!.role === 0 ? "取消管理员" : "设为管理员"),
    icon: renderIcon(AdminIcon, "#409eff")
  },
  {
    key: "set_star",
    label: () => (selectedUser!.star ? "取消打星用户" : "设为打星用户"),
    icon: renderIcon(StarRound, "#efca44")
  },
  {
    key: "reset_passwd",
    label: "重置密码",
    icon: renderIcon(LockResetRound, "#d03050")
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
    width: 240,
    title: () => (
      <NFlex size="small" align="center" style="margin-left: 7px">
        <NIcon style="display: flex">
          <UserIcon />
        </NIcon>
        <NText>昵称</NText>
      </NFlex>
    ),
    render: (row) => (
      <NFlex align="center" size="small">
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
              icon: () =>
                row.star ? (
                  <NIcon component={StarRound} size={24} color="#efca44" />
                ) : (
                  ""
                )
            }}
          </NButton>
        </RouterLink>
        {row.uid === user.info!.uid ? (
          <NTag type="primary" size="small" round>
            你自己
          </NTag>
        ) : (
          ""
        )}
      </NFlex>
    )
  },
  {
    title: "真实姓名",
    key: "realName",
    align: "left",
    render: (row) => <NText depth="3">{row.realName}</NText>
  },
  {
    key: "role",
    align: "center",
    title: () => (
      <NFlex size="small" align="center" justify="center">
        <NIcon style="display: flex">
          <RoleIcon />
        </NIcon>
        <NText>权限</NText>
      </NFlex>
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
      <NFlex size="small" align="center" justify="end">
        <NIcon style="display: flex">
          <DateIcon />
        </NIcon>
        <NText>注册时间</NText>
      </NFlex>
    ),
    render: (row) => (
      <NText depth="3">{dayjs.unix(row.createAt!).format("YYYY/MM/DD")}</NText>
    )
  }
]

onMounted(() => {
  const { page = 1 } = route.query
  pagination.value.page = Number(page)

  queryUsers()
})

function filterChange(value: number) {
  if (value === 0) {
    nextTick(() => {
      filter.value.keyword = ""
      search()
    })
  }
}

function pageChange(page: number) {
  router.replace({
    query: { page }
  })

  queryUsers()
}

function search() {
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
      app.setError(err)
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
  menuVisible.value = false
}

function operationSelect(key: string) {
  if (key === "set_admin") {
    selectedUser!.role! ^= 1
    updateUser()
  } else if (key === "set_star") {
    selectedUser!.star = !selectedUser!.star
    updateUser()
  } else if (key === "reset_passwd") {
    modal.create({
      title: "提示",
      type: "info",
      content: `重置用户“${selectedUser?.nickname}”的密码？`,
      preset: "dialog",
      negativeText: "不要",
      positiveText: "确定",
      onPositiveClick: () => {
        const passwd =
          (crypto.getRandomValues(new Uint32Array(1))[0]! % 9e7) + 1e7
        selectedUser!.password = passwd.toString()
        UserApi.update(selectedUser!, true)
          .then(() => {
            notification.info({
              content: `用户“${selectedUser!.nickname}”的密码已重置`,
              meta: `新密码：${selectedUser!.password}`,
              duration: 10000
            })
          })
          .catch((err: ErrorMessage) => {
            message.error(err.toString())
          })
          .finally(() => modal.destroyAll())
      }
    })
  }

  hideOperation()
}
</script>
