<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-global-style />
    <router-view v-if="show" v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </n-config-provider>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { ErrorMessage } from "@/api/type"
import { themeOverrides } from "@/theme"
import { NConfigProvider, NGlobalStyle, dateZhCN, zhCN } from "naive-ui"
import { computed, nextTick, onMounted, provide, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "@/store"

const store = useStore()
const route = useRoute()
const router = useRouter()

const show = ref(true)
const theme = computed(() => store.app.theme)
const isLoggedIn = computed(() => store.user.isLoggedIn)

provide("reload", reload)

function reload() {
  show.value = false
  nextTick(() => (show.value = true))
}

watch(
  theme,
  (val) => {
    document.documentElement.setAttribute(
      "data-color-scheme",
      val == null ? "light" : "dark"
    )
  },
  { immediate: true }
)

router.beforeEach((to, from) => {
  if (to.name !== "error") {
    // 进入正常页面，清空错误信息
    store.app.setError(null)
  } else if (store.app.error == null) {
    // 意外进入错误页面却没有错误信息，返回上一页
    router.replace({ name: from.name! })
  }

  if (isLoggedIn.value) {
    // 已登录，检查是否有效
    AuthApi.verify().catch((error: ErrorMessage) => {
      if (error.status === 401) {
        store.user.clearToken()
        router.push({ name: "auth", params: { tab: "login" } })
      }
    })
  }
})

// 根据路由生成管理页面导航
router.afterEach(() => {
  const routes: Array<string> = []

  route.matched.forEach((r) => {
    if (r.meta.title) {
      routes.push(r.meta.title as string)
    }
  })

  store.app.setBreadcrumb(routes)
})

onMounted(() => {
  console.log("Timezone:", Intl.DateTimeFormat().resolvedOptions().timeZone)
})
</script>
