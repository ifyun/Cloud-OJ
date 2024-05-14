<template>
  <n-flex vertical align="center" size="large" style="margin-top: 64px">
    <div v-if="theme === 'light'" v-html="Empty" />
    <div v-else v-html="EmptyDark" />
    <n-text depth="3">此页面没有数据</n-text>
    <n-button type="info" size="small" round secondary @click="reload">
      <template #icon>
        <n-icon :component="RefreshIcon" />
      </template>
      重新加载
    </n-button>
  </n-flex>
</template>

<script setup>
import { inject } from "vue"
import { NButton, NFlex, NIcon, NText } from "naive-ui"
import { RefreshRound as RefreshIcon } from "@vicons/material"
import Empty from "@/assets/empty.svg?raw"
import EmptyDark from "@/assets/empty-dark.svg?raw"

const theme = inject("themeStr")
const reload = inject("reload", () => {})
</script>
