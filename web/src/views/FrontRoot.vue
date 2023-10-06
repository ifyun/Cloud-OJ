<template>
  <n-config-provider abstract :theme-overrides="themeOverrides">
    <n-message-provider>
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
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { themeOverridesDark } from "@/theme"
import { BottomInfo, TopNavbar } from "@/views/layout"
import type { GlobalThemeOverrides } from "naive-ui"
import {
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  NMessageProvider
} from "naive-ui"
import { computed } from "vue"
import { useStore } from "vuex"

const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return themeOverridesDark
  }

  return {}
})
</script>
