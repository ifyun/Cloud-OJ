<template>
  <n-flex vertical align="center" style="margin-top: 48px">
    <div v-if="theme === 'light'" v-html="Empty" />
    <div v-else v-html="EmptyDark" />
    <n-text depth="3">没有数据</n-text>
  </n-flex>
</template>

<script setup>
import { inject } from "vue"
import { NFlex, NText } from "naive-ui"
import Empty from "@/assets/empty.svg?raw"
import EmptyDark from "@/assets/empty-dark.svg?raw"

const theme = inject("themeStr")
</script>
