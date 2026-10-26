<template>
  <n-layout position="absolute">
    <n-layout-header
      bordered
      position="static"
      style="
        height: var(--header-height);
        padding: 0 calc(var(--layout-padding) * 2);
      ">
      <index-navigation style="height: 100%" />
    </n-layout-header>
    <n-layout position="absolute" style="top: var(--header-height)">
      <n-scrollbar class="global">
        <router-wrapper />
        <n-layout-footer style="margin-top: auto">
          <bottom-info />
        </n-layout-footer>
      </n-scrollbar>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { RouterWrapper } from "@/components"
import { BottomInfo, IndexNavigation } from "@/views/layout/components"
import { NLayout, NLayoutFooter, NLayoutHeader, NScrollbar } from "naive-ui"
</script>

<style scoped lang="scss">
:deep(.wrap) {
  width: calc(100% - var(--layout-padding) * 4);
  max-width: 1080px;
  padding-top: calc(var(--layout-padding) * 2);
}
</style>
