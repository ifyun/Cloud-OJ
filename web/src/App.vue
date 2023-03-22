<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-global-style />
    <n-dialog-provider>
      <n-message-provider>
        <router-view
          v-if="!reload"
          v-slot="{ Component }"
          :key="$route.fullPath">
          <component :is="Component" />
        </router-view>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import {
  dateZhCN,
  NConfigProvider,
  NDialogProvider,
  NGlobalStyle,
  NMessageProvider,
  zhCN
} from "naive-ui"
import { themeOverrides } from "@/theme"
import { AuthApi } from "@/api/request"
import { ErrorMessage } from "@/api/type"
import { Mutations } from "@/store"

const store = useStore()
const router = useRouter()

const theme = computed(() => {
  return store.state.theme
})

const reload = computed(() => {
  return store.state.reload
})

const isLoggedIn = computed(() => {
  return store.getters.isLoggedIn
})

onMounted(() => {
  console.log("Timezone:", store.state.timezone)

  if (isLoggedIn.value) {
    // 已登录，检查是否有效
    AuthApi.verify(store.state.userInfo).catch((error: ErrorMessage) => {
      console.log(error)
      if (error.status === 401) {
        store.commit(Mutations.CLEAR_TOKEN)
        router.push({ name: "auth", params: { tab: "login" } })
      }
    })
  }
})
</script>
