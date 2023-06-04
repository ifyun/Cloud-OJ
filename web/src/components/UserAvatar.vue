<template>
  <n-avatar
    round
    :src="url"
    :size="size"
    style="vertical-align: middle"
    @error="url = ''" />
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { NAvatar } from "naive-ui"
import { ApiPath } from "@/api/request"

interface Props {
  userId: string
  size: "small" | "medium" | "large" | number
  timestamp?: number
}

const props = withDefaults(defineProps<Props>(), {
  timestamp: Date.now()
})

const url = ref<string>("")

watch(
  props,
  async (newValue) => {
    url.value = `${ApiPath.AVATAR}/${newValue.userId}.png?t=${newValue.timestamp}`
  },
  { immediate: true, deep: true }
)
</script>
