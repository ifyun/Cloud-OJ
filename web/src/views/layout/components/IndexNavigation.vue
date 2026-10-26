<template>
  <n-flex size="large" align="center">
    <logo :size="40" />
    <div>
      <n-menu :value="routeName" mode="horizontal" :options="options" />
    </div>
    <n-flex size="large" align="center" style="margin-left: auto">
      <theme-switch />
      <user-menu />
    </n-flex>
  </n-flex>
</template>

<script setup lang="tsx">
import { Logo } from "@/components"
import { NFlex, NMenu } from "naive-ui"
import { computed } from "vue"
import { RouterLink, useRoute } from "vue-router"
import ThemeSwitch from "./ThemeSwitch.vue"
import UserMenu from "./UserMenu.vue"

const route = useRoute()

const routeName = computed(() => route.name?.toString())

const options = [
  {
    label: () => <RouterLink to={{ name: "problems" }}>📒 题目</RouterLink>,
    key: "problems"
  },
  {
    label: () => (
      <RouterLink to={{ name: "contests" }}>🕹️ 竞赛 & 练习</RouterLink>
    ),
    key: "contests"
  },
  {
    label: () => <RouterLink to={{ name: "scoreboard" }}>🎈 排名</RouterLink>,
    key: "scoreboard"
  },
  {
    label: () => <RouterLink to={{ name: "help" }}>🛟 帮助</RouterLink>,
    key: "help"
  }
]
</script>
