<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeBase"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-config-provider abstract :theme-overrides="themeOverrides">
      <n-modal-provider>
        <n-dialog-provider>
          <n-message-provider>
            <n-notification-provider>
              <n-global-style />
              <router-view v-if="show" v-slot="{ Component }">
                <component
                  :is="isForbidden ? Forbidden : Component"
                  style="min-width: 900px" />
              </router-view>
            </n-notification-provider>
          </n-message-provider>
        </n-dialog-provider>
      </n-modal-provider>
    </n-config-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { useStore } from "@/store"
import { themeBase, themeDark } from "@/theme"
import { setTitle } from "@/utils"
import {
  dateZhCN,
  type GlobalThemeOverrides,
  NConfigProvider,
  NDialogProvider,
  NGlobalStyle,
  NMessageProvider,
  NModalProvider,
  NNotificationProvider,
  zhCN
} from "naive-ui"
import { computed, nextTick, provide, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import Forbidden from "./views/Forbidden.vue"

const store = useStore()
const route = useRoute()
const router = useRouter()

const show = ref(true)
const theme = computed(() => store.app.theme)
const themeStr = computed(() => (store.app.theme === null ? "light" : "dark"))
const isLoggedIn = computed(() => store.user.isLoggedIn)
const isForbidden = ref<boolean>(false)

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.app.theme != null) {
    return themeDark
  }

  return {}
})

provide("reload", reload)
provide("themeStr", themeStr)

// 刷新当前路由
function reload() {
  show.value = false
  nextTick(() => {
    checkToken()
    show.value = true
  })
}

router.beforeEach(async (to, from) => {
  if (isLoggedIn.value) {
    // 已登录，检查是否有效
    if (
      (await checkToken()) &&
      to.matched.some((r) => r.meta.requiresAdmin) &&
      store.user.userInfo?.role !== 0
    ) {
      // 无权限，取消导航
      isForbidden.value = true
      return false
    } else {
      isForbidden.value = false
    }
  } else {
    isForbidden.value = false
    if (to.matched.some((r) => r.meta.requiresAuth)) {
      await router.push({ name: "auth", params: { tab: "login" } })
    }
  }

  if (to.name !== "error") {
    // 进入正常页面，清空错误信息
    store.app.setError(null)
  } else if (store.app.error === null) {
    // 意外进入错误页面却没有错误信息，返回上一页
    await router.replace({ path: from.fullPath })
  }
})

router.afterEach((to, from) => {
  const routes: Array<string> = []
  // 设置页面标题
  if (route.meta.title) {
    const t =
      typeof route.meta.title === "function"
        ? route.meta.title(route)
        : route.meta.title
    setTitle(t)
  }
  // 跳转时清除当前页面的查询条件
  if (from.name && to.name !== from.name) {
    sessionStorage.removeItem("query")
  }
  // 为 Admin 页面生成面包屑
  route.matched.forEach((r) => {
    if (!r.meta.inBreadcrumb) {
      return
    }

    const title = r.meta.title

    if (title) {
      routes.push(typeof title === "function" ? title(route) : title)
    }
  })

  store.app.setBreadcrumb(routes)
})

async function checkToken() {
  try {
    await AuthApi.verify()
    return true
  } catch (error: any) {
    if (error.status === 401) {
      store.user.clearToken()
      await router.push({ name: "auth", params: { tab: "login" } })
    }

    return false
  }
}
</script>

<style lang="scss">
.n-scrollbar.global {
  > .n-scrollbar-container {
    > .n-scrollbar-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      min-height: calc(100vh - var(--header-height));
    }
  }
}
</style>
