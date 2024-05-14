<template>
  <n-flex vertical align="center" size="large" style="margin-top: 64px">
    <div v-if="theme === 'light'" v-html="Error" />
    <div v-else v-html="ErrorDark" />
    <n-text depth="3">{{ errorText }}</n-text>
    <n-button type="info" size="small" round secondary @click="backToPrevious">
      <template #icon>
        <n-icon :component="BackIcon" />
      </template>
      返回上页
    </n-button>
  </n-flex>
</template>

<script setup lang="ts">
import { computed, inject } from "vue"
import { useRouter } from "vue-router"
import { NButton, NFlex, NIcon, NText } from "naive-ui"
import { ArrowBackRound as BackIcon } from "@vicons/material"
import { ErrorMessage } from "@/api/type"
import Error from "@/assets/error.svg?raw"
import ErrorDark from "@/assets/error-dark.svg?raw"

const props = defineProps<{
  error: ErrorMessage
}>()

const theme = inject("themeStr")
const router = useRouter()
const errorText = computed(() => {
  return `${props.error.status}: ${props.error.message}`
})

function backToPrevious() {
  router.back()
}
</script>
