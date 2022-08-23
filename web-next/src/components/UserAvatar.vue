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

const props = defineProps<{
  userId: string
  size: "small" | "medium" | "large" | number
}>()

const url = ref<string>("")

watch(
  props,
  async (newValue) => {
    url.value = `/api/file/image/avatar/${newValue.userId}.png`
  },
  { immediate: true, deep: true }
)
</script>
