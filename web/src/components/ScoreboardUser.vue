<template>
  <n-flex size="small" align="center">
    <user-avatar
      size="large"
      :uid="data.uid"
      :nickname="data.nickname"
      :has-avatar="data.hasAvatar" />
    <n-flex vertical :size="0">
      <n-flex :size="4" align="center">
        <router-link :to="{ name: 'account', params: { uid: data.uid } }">
          <n-button text strong>{{ data.nickname }}</n-button>
        </router-link>
        <n-icon
          v-if="data.star"
          :size="24"
          color="#efca44"
          :component="StarRound" />
        <n-tag
          v-if="store.user.userInfo?.uid === data.uid"
          type="primary"
          size="small"
          round>
          你自己
        </n-tag>
      </n-flex>
      <n-text v-if="data.username" depth="3">
        {{ data.realName }}@{{ data.username }}
      </n-text>
    </n-flex>
  </n-flex>
</template>

<script setup lang="ts">
import type { Ranking } from "@/api/type"
import { useStore } from "@/store"
import { StarRound } from "@vicons/material"
import { NButton, NFlex, NIcon, NTag, NText } from "naive-ui"
import UserAvatar from "./UserAvatar.vue"

defineProps<{ data: Ranking }>()

const store = useStore()
</script>
