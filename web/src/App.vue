<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-global-style />
    <router-view v-if="!reload" v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </n-config-provider>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { ErrorMessage } from "@/api/type"
import { themeOverrides } from "@/theme"
import { NConfigProvider, NGlobalStyle, dateZhCN, zhCN } from "naive-ui"
import { computed, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useStore } from "@/store"

const store = useStore()
const route = useRoute()
const router = useRouter()

const theme = computed(() => {
  return store.app.theme
})

const reload = computed(() => {
  return store.app.reload
})

const isLoggedIn = computed(() => {
  return store.user.isLoggedIn
})

router.beforeEach(() => {
  const routes: Array<string> = []

  route.matched.forEach((r) => {
    if (r.meta.title) {
      routes.push(r.meta.title as string)
    }
  })

  store.app.setBreadcrumb(routes)

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

onMounted(() => {
  console.log("Timezone:", Intl.DateTimeFormat().resolvedOptions().timeZone)
})
</script>
