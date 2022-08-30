<template>
  <n-space v-if="isLoggedIn" align="center" size="small">
    <user-avatar size="small" :user-id="userId" />
    <n-dropdown
      trigger="click"
      :show-arrow="true"
      placement="bottom-end"
      :options="userInfo.roleId === 1 ? userMenuOptions : adminMenuOptions"
      @select="userMenuSelect">
      <n-button text icon-placement="right" style="padding: 6px 0">
        {{ userInfo.name }}
        <template #icon>
          <n-icon>
            <drop-down-icon />
          </n-icon>
        </template>
      </n-button>
    </n-dropdown>
  </n-space>
  <n-space v-else size="small">
    <n-button secondary type="primary" round size="small" @click="login">
      注册登录
    </n-button>
  </n-space>
</template>

<script setup lang="tsx">
import { computed } from "vue"
import { useStore } from "vuex"
import { RouterLink, useRouter } from "vue-router"
import {
  NButton,
  NDropdown,
  NIcon,
  NSpace,
  useDialog,
  useMessage
} from "naive-ui"
import { UserAvatar } from "@/components"
import {
  ArrowDropDownFilled as DropDownIcon,
  DashboardCustomizeRound as DashboardIcon,
  ExitToAppOutlined as LogoutIcon
} from "@vicons/material"
import { HouseUser as UserHomeIcon } from "@vicons/fa"
import { renderIcon } from "@/utils"
import { AuthApi } from "@/api/request"
import { ErrorMessage, UserInfo } from "@/api/type"
import { Mutations } from "@/store"

const store = useStore()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const userMenuOptions = [
  {
    label: "个人中心",
    key: "account",
    icon: renderIcon(UserHomeIcon)
  },
  {
    label: "退出",
    key: "exit",
    icon: renderIcon(LogoutIcon)
  }
]

const adminMenuOptions = [
  {
    label: () => <RouterLink to={{ name: "account" }}>个人中心</RouterLink>,
    key: "account",
    icon: renderIcon(UserHomeIcon)
  },
  {
    label: () => (
      <RouterLink to={{ name: "problem_admin" }}>系统管理</RouterLink>
    ),
    key: "admin",
    icon: renderIcon(DashboardIcon)
  },
  {
    label: "退出登录",
    key: "exit",
    icon: renderIcon(LogoutIcon)
  }
]

const isLoggedIn = computed<boolean>(() => {
  return store.getters.isLoggedIn
})

const userInfo = computed<UserInfo>(() => {
  return store.state.userInfo
})

const userId = computed<string>(() => store.state.userInfo.userId)

function userMenuSelect(key: string) {
  if (key === "exit") {
    exit()
  }
}

function login() {
  router.push({ name: "auth" })
}

function logoff() {
  AuthApi.logoff(userInfo.value)
    .then(() => {
      message.success(`${userInfo.value.name} 已退出`)
    })
    .catch((err: ErrorMessage) => {
      message.warning(err.toString())
    })
    .finally(() => {
      store.commit(Mutations.CLEAR_TOKEN)
      router.push({ name: "auth" })
    })
}

function exit() {
  dialog.warning({
    title: "警告",
    content: "确定退出吗？",
    positiveText: "确定",
    onPositiveClick: () => {
      logoff()
    }
  })
}
</script>
