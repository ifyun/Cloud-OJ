<template>
  <n-space v-if="isLoggedIn" align="center" size="small">
    <user-avatar
      size="small"
      :uid="userInfo!.uid!"
      :nickname="userInfo!.nickname!"
      :has-avatar="userInfo!.hasAvatar" />
    <n-dropdown
      trigger="click"
      :show-arrow="true"
      placement="bottom-end"
      :options="userInfo!.role === 1 ? userMenuOptions : adminMenuOptions"
      @select="userMenuSelect">
      <n-button text icon-placement="right">
        {{ userInfo!.nickname }}
        <template #icon>
          <n-icon>
            <arrow-drop-down-round />
          </n-icon>
        </template>
      </n-button>
    </n-dropdown>
  </n-space>
  <n-space v-else size="small">
    <n-button secondary type="primary" round size="small" @click="login">
      注册/登录
    </n-button>
  </n-space>
</template>

<script setup lang="tsx">
import { AuthApi } from "@/api/request"
import { ErrorMessage } from "@/api/type"
import { UserAvatar } from "@/components"
import { useStore } from "@/store"
import { renderIcon } from "@/utils"
import { HouseUser } from "@vicons/fa"
import {
  ArrowDropDownRound,
  DashboardRound,
  ExitToAppRound
} from "@vicons/material"
import {
  NButton,
  NDropdown,
  NIcon,
  NSpace,
  useDialog,
  useMessage
} from "naive-ui"
import { computed } from "vue"
import { RouterLink, useRouter } from "vue-router"

const store = useStore()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const userMenuOptions = [
  {
    label: () => <RouterLink to={{ name: "account" }}>个人中心</RouterLink>,
    key: "account",
    icon: renderIcon(HouseUser)
  },
  {
    label: "退出",
    key: "exit",
    icon: renderIcon(ExitToAppRound)
  }
]

const adminMenuOptions = [
  {
    label: () => <RouterLink to={{ name: "account" }}>个人中心</RouterLink>,
    key: "account",
    icon: renderIcon(HouseUser)
  },
  {
    label: () => (
      <RouterLink to={{ name: "problem_admin" }}>系统管理</RouterLink>
    ),
    key: "admin",
    icon: renderIcon(DashboardRound)
  },
  {
    label: "退出登录",
    key: "exit",
    icon: renderIcon(ExitToAppRound)
  }
]

const isLoggedIn = computed(() => {
  return store.user.isLoggedIn
})

const userInfo = computed(() => {
  return store.user.userInfo
})

function userMenuSelect(key: string) {
  if (key === "exit") {
    exit()
  }
}

function login() {
  router.push({ name: "auth", params: { tab: "login" } })
}

function logoff() {
  AuthApi.logoff()
    .catch((err: ErrorMessage) => {
      message.warning(err.toString())
    })
    .finally(() => {
      store.user.clearToken()
      login()
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
