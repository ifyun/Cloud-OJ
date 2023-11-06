<template>
  <n-config-provider abstract :theme-overrides="themeOverrides">
    <n-global-style />
    <n-message-provider>
      <n-dialog-provider>
        <n-layout>
          <n-layout-header class="header" bordered>
            <top-navbar />
          </n-layout-header>
          <n-layout-content
            class="main"
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
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { useStore } from "@/store"
import { themeOverridesDark } from "@/theme"
import { BottomInfo, TopNavbar } from "@/views/layout"
import type { GlobalThemeOverrides } from "naive-ui"
import {
  NConfigProvider,
  NDialogProvider,
  NGlobalStyle,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  NMessageProvider
} from "naive-ui"
import { computed } from "vue"

const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.app.theme != null) {
    return themeOverridesDark
  }

  return {}
})
</script>
