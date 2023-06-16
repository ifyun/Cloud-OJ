<template>
  <n-avatar
    v-if="hasAvatar"
    round
    :src="url"
    :size="size"
    style="vertical-align: middle"
    @error="url = ''" />
  <n-avatar
    v-else
    round
    :size="size"
    style="vertical-align: middle; background: var(--primary-color)">
    {{ name.substring(0, 1).toUpperCase() }}
  </n-avatar>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { NAvatar } from "naive-ui"
import { ApiPath } from "@/api/request"

interface Props {
  userId: string
  name: string
  hasAvatar: boolean
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
