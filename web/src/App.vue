<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeBase"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-config-provider abstract :theme-overrides="themeOverrides">
      <n-global-style />
      <router-view v-if="show" v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </n-config-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { useStore } from "@/store"
import { themeBase, themeDark } from "@/theme"
import {
  dateZhCN,
  type GlobalThemeOverrides,
  NConfigProvider,
  NGlobalStyle,
  zhCN
} from "naive-ui"
import { computed, nextTick, onMounted, provide, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { setTitle } from "@/utils"

const store = useStore()
const route = useRoute()
const router = useRouter()

const show = ref(true)
const theme = computed(() => store.app.theme)
const themeStr = computed(() => (store.app.theme === null ? "light" : "dark"))
const isLoggedIn = computed(() => store.user.isLoggedIn)

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
    await checkToken()
  } else {
    if (to.name === "edit_account" || to.path.includes("/admin")) {
      await router.replace("/")
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

  if (route.meta.title) {
    setTitle(route.meta.title as string)
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

    if (
      (r.name === "edit_problem" || r.name === "edit_contest") &&
      route.params.id === "new"
    ) {
      routes.push(r.meta._title as string)
    } else {
      routes.push(r.meta.title as string)
    }
  })

  store.app.setBreadcrumb(routes)
})

onMounted(() => {
  console.log("Timezone:", Intl.DateTimeFormat().resolvedOptions().timeZone)
})

async function checkToken() {
  try {
    await AuthApi.verify()
  } catch (error: any) {
    if (error.status === 401) {
      store.user.clearToken()
      await router.push({ name: "auth", params: { tab: "login" } })
    }
  }
}
</script>
