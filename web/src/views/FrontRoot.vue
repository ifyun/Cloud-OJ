<template>
  <n-message-provider>
    <n-dialog-provider>
      <n-layout position="absolute" style="min-width: 900px">
        <n-layout-header class="header" position="static" bordered>
          <top-navbar />
        </n-layout-header>
        <n-layout
          class="main"
          position="absolute"
          style="top: var(--header-height)">
          <n-scrollbar class="global">
            <router-view v-slot="{ Component }">
              <keep-alive :key="$route.path">
                <component :is="Component" />
              </keep-alive>
            </router-view>
            <n-layout-footer style="margin-top: auto">
              <bottom-info />
            </n-layout-footer>
          </n-scrollbar>
        </n-layout>
      </n-layout>
    </n-dialog-provider>
  </n-message-provider>
</template>

<script setup lang="ts">
import { BottomInfo, TopNavbar } from "@/views/layout"
import {
  NDialogProvider,
  NLayout,
  NLayoutFooter,
  NLayoutHeader,
  NMessageProvider,
  NScrollbar
} from "naive-ui"
</script>
