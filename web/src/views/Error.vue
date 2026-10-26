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
      <n-text>哦豁，出错了！</n-text>
      <div v-if="theme === 'light'" v-html="Error" />
      <div v-else v-html="ErrorDark" />
      <n-text depth="3">{{ errorText }}</n-text>
      <n-button size="small" round secondary @click="reload">
        <template #icon>
          <n-icon :component="RefreshRound" />
        </template>
        刷新
      </n-button>
    </n-flex>
    <n-layout-footer style="margin-top: auto">
      <bottom-info />
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { ErrorMessage } from "@/api/type"
import ErrorDark from "@/assets/error-dark.svg?raw"
import Error from "@/assets/error.svg?raw"
import { Logo } from "@/components"
import { RefreshRound } from "@vicons/material"
import {
  NButton,
  NFlex,
  NIcon,
  NLayout,
  NLayoutFooter,
  NLayoutHeader,
  NText
} from "naive-ui"
import { computed, inject } from "vue"
import { BottomInfo } from "./layout/components"

const props = defineProps<{
  error: ErrorMessage
}>()

const reload = inject("reload", () => {})
const theme = inject<string>("themeStr")

const errorText = computed(() => {
  return `${props.error.status}: ${props.error.message}`
})
</script>
