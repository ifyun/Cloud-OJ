<template>
  <n-flex vertical align="center" style="margin-top: 48px">
    <div v-if="theme === 'light'" v-html="Empty" />
    <div v-else v-html="EmptyDark" />
    <n-text depth="3">没有数据</n-text>
  </n-flex>
</template>

<script setup lang="ts">
import EmptyDark from "@/assets/empty-dark.svg?raw"
import Empty from "@/assets/empty.svg?raw"
import { NFlex, NText } from "naive-ui"
import { inject } from "vue"

const theme = inject("themeStr") as "light" | "dark"
</script>
