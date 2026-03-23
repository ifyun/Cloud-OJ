<template>
  <n-flex size="large" align="center" class="top-nav">
    <logo :size="40" />
    <div>
      <n-menu :value="routeName" mode="horizontal" :options="navMenuOptions" />
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

const navMenuOptions = [
  {
    label: () => <RouterLink to={{ name: "problems" }}>📘 题目</RouterLink>,
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
    key: "leaderboard"
  },
  {
    label: () => <RouterLink to={{ name: "help" }}>🛟 帮助</RouterLink>,
    key: "help"
  }
]
</script>

<style scoped lang="scss">
.top-nav {
  height: calc(var(--header-height) - 1px);
  padding: 0 calc(var(--layout-padding) * 2);
}
</style>
