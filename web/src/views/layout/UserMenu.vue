<template>
  <n-flex v-if="isLoggedIn" align="center" size="small">
    <user-avatar
      size="small"
      :uid="userInfo!.uid!"
      :nickname="userInfo!.nickname!"
      :has-avatar="userInfo!.hasAvatar" />
    <n-dropdown
      trigger="click"
      placement="bottom-end"
      :show-arrow="true"
      :options="menuOptions"
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
  </n-flex>
  <n-flex v-else size="small">
    <n-button secondary type="primary" round size="small" @click="login">
      注册/登录
    </n-button>
  </n-flex>
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
  NFlex,
  NIcon,
  useDialog,
  useMessage
} from "naive-ui"
import { computed } from "vue"
import { RouterLink, useRouter } from "vue-router"

const store = useStore()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const isLoggedIn = computed(() => {
  return store.user.isLoggedIn
})

const userInfo = computed(() => {
  return store.user.userInfo
})

const menuOptions = [
  {
    key: "account",
    label: () => <RouterLink to={{ name: "account" }}>个人中心</RouterLink>,
    icon: renderIcon(HouseUser)
  },
  {
    key: "admin",
    show: userInfo.value?.role === 0,
    label: () => <RouterLink to={{ name: "admin" }}>系统管理</RouterLink>,
    icon: renderIcon(DashboardRound)
  },
  {
    key: "exit",
    label: "退出登录",
    icon: renderIcon(ExitToAppRound)
  }
]

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
