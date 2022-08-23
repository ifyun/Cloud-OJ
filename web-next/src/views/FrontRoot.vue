<template>
  <n-layout position="absolute">
    <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-layout-header class="header" position="absolute">
        <top-navbar />
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
import { computed } from "vue"
import { useStore } from "vuex"
import type { GlobalThemeOverrides } from "naive-ui"
import {
  darkTheme,
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader
} from "naive-ui"
import { BottomInfo, TopNavbar } from "@/views/layout"

const store = useStore()

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
</script>
