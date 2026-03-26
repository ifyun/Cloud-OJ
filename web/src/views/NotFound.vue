<template>
  <n-layout
    position="absolute"
    content-style="display: flex; flex-direction: column">
    <n-layout-header bordered style="height: var(--header-height)">
      <n-flex align="center" justify="center" style="height: 100%">
        <logo />
      </n-flex>
    </n-layout-header>
    <n-flex vertical align="center" style="margin-top: 24px">
      <div v-html="NotFoundIcon" />
      <n-text depth="2">找不到页面了...</n-text>
    </n-flex>
    <n-layout-footer style="margin-top: auto">
      <bottom-info />
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import NotFoundIcon from "@/assets/404.svg?raw"
import { Logo } from "@/components"
import { NFlex, NLayout, NLayoutFooter, NLayoutHeader, NText } from "naive-ui"
import { BottomInfo } from "./layout"
</script>
