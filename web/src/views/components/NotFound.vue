<template>
  <n-flex vertical align="center" size="large" style="margin-top: 64px">
    <div v-html="NotFoundIcon" />
    <n-text depth="3">找不到页面了...</n-text>
    <n-button type="info" size="small" round secondary @click="backToPrevious">
      <template #icon>
        <n-icon :component="BackIcon" />
      </template>
      返回上页
    </n-button>
  </n-flex>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router"
import { NButton, NFlex, NIcon, NText } from "naive-ui"
import { ArrowBackRound as BackIcon } from "@vicons/material"
import NotFoundIcon from "@/assets/404.svg?raw"

const router = useRouter()

function backToPrevious() {
  router.back()
}
</script>
