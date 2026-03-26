<template>
  <n-layout position="absolute">
    <n-layout-header
      position="static"
      bordered
      style="height: var(--header-height)">
      <nav-menu />
    </n-layout-header>
    <n-layout position="absolute" style="top: var(--header-height)">
      <n-scrollbar class="global">
        <router-layout />
        <n-layout-footer style="margin-top: auto">
          <bottom-info />
        </n-layout-footer>
      </n-scrollbar>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { BottomInfo, RouterLayout, NavMenu } from "@/views/layout"
import { NLayout, NLayoutFooter, NLayoutHeader, NScrollbar } from "naive-ui"
</script>

<style scoped lang="scss">
:deep(.wrap) {
  width: calc(100% - var(--layout-padding) * 4);
  max-width: 1080px;
  padding-top: calc(var(--layout-padding) * 2);
}
</style>
