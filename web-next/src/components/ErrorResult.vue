<template>
  <n-result
    style="margin-top: 48px"
    size="large"
    status="info"
    :title="title"
    :description="desc">
    <template #footer>要不刷新一下试试？</template>
  </n-result>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { NResult } from "naive-ui"
import { ErrorMessage } from "@/api/type"

const props = defineProps<{
  error: ErrorMessage
}>()

const title = ref<string>("")
const desc = ref<string>("")

watch(
  () => props.error,
  (value) => {
    if (typeof value === "undefined") {
      return
    }

    title.value = `${value.status} ${value.error!}`
    desc.value = value.message
  },
  { immediate: true, deep: true }
)
</script>
