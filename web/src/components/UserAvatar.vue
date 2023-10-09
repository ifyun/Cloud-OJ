<template>
  <n-avatar
    v-if="hasAvatar"
    round
    :src="url"
    :size="size"
    style="vertical-align: middle"
    @error="url = ''" />
  <!-- 没有头像，使用昵称第一个字符作为头像 -->
  <n-avatar
    v-else
    round
    :size="size"
    style="vertical-align: middle; background: var(--primary-color)">
    {{ nickname.substring(0, 1).toUpperCase() }}
  </n-avatar>
</template>

<script setup lang="ts">
import { ApiPath } from "@/api"
import { NAvatar } from "naive-ui"
import { ref, watch } from "vue"

interface Props {
  uid: number
  nickname: string
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
    url.value = `${ApiPath.AVATAR}/${newValue.uid}.png?t=${newValue.timestamp}`
  },
  { immediate: true, deep: true }
)
</script>
