<template>
  <n-config-provider abstract :theme-overrides="themeOverrides">
    <n-layout position="absolute" :native-scrollbar="false">
      <n-layout-header class="header" bordered>
        <top-navbar />
      </n-layout-header>
      <n-layout-content
        class="main"
        :native-scrollbar="false"
        content-style="display: flex; flex-direction: column">
        <router-view v-slot="{ Component }">
          <keep-alive :key="$route.path">
            <component :is="Component" />
          </keep-alive>
        </router-view>
        <n-layout-footer class="footer">
          <bottom-info />
        </n-layout-footer>
      </n-layout-content>
    </n-layout>
  </n-config-provider>
</template>

<script setup lang="ts">
import { computed } from "vue"
import { useStore } from "vuex"
import type { GlobalThemeOverrides } from "naive-ui"
import {
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader
} from "naive-ui"
import { themeOverridesDark } from "@/theme"
import { BottomInfo, TopNavbar } from "@/views/layout"

const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return themeOverridesDark
  }

  return {}
})
</script>
