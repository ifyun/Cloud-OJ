<template>
  <n-layout position="absolute">
    <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-layout-header class="header" position="absolute">
        <navbar />
      </n-layout-header>
    </n-config-provider>
    <n-layout-content
      class="main"
      position="absolute"
      :embedded="true"
      :native-scrollbar="false"
      content-style="display: flex; flex-direction: column">
      <router-view v-slot="{ Component }" :key="$route.fullPath">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
      <n-layout-footer class="footer">
        <bottom-info />
      </n-layout-footer>
    </n-layout-content>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue"
import { useStore } from "vuex"
import {
  darkTheme,
  GlobalThemeOverrides,
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  useMessage
} from "naive-ui"
import { BottomInfo, Navbar } from "@/views/layout"
import { AuthApi } from "@/api/request"
import { ErrorMsg } from "@/api/type"

const store = useStore()
const message = useMessage()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return {}
  }

  return {
    Layout: {
      headerColor: "#161B22FF",
      headerColorInverted: "#161B22FF"
    }
  }
})

const isLoggedIn = computed(() => {
  return store.getters.isLoggedIn
})

onMounted(() => {
  if (isLoggedIn.value) {
    AuthApi.verify(store.state.userInfo).catch((error: ErrorMsg) => {
      if (error.code === 401) {
        message.warning("登录已失效，请重新登录！")
      }
    })
  }
})
</script>
