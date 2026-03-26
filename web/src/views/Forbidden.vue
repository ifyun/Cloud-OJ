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
      <n-icon :component="DoNotDisturbRound" :size="100" color="#F56C6C" />
      <n-text
        strong
        depth="3"
        style="font-size: 32px; font-family: var(--code-font)">
        403
      </n-text>
      <n-text depth="2">你没有权限访问此页面</n-text>
    </n-flex>
    <n-layout-footer style="margin-top: auto">
      <bottom-info />
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { Logo } from "@/components"
import { DoNotDisturbRound } from "@vicons/material"
import {
  NFlex,
  NIcon,
  NLayout,
  NLayoutFooter,
  NLayoutHeader,
  NText
} from "naive-ui"
import { BottomInfo } from "./layout"
</script>
