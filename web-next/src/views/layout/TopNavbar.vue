<template>
  <div class="top-nav" :class="route.name">
    <div class="app-name">
      <logo :size="40" />
    </div>
    <div style="margin-left: 50px">
      <n-menu
        v-model:value="$route.name"
        mode="horizontal"
        :options="navMenuOptions" />
    </div>
    <div style="margin-left: auto">
      <n-space size="large" align="center">
        <theme-switch />
        <user-menu />
      </n-space>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { RouterLink, useRoute } from "vue-router"
import { NMenu, NSpace } from "naive-ui"
import { Logo } from "@/components"
import ThemeSwitch from "./ThemeSwitch.vue"
import UserMenu from "./UserMenu.vue"

const route = useRoute()

const navMenuOptions = [
  {
    label: () => <RouterLink to={{ name: "problems" }}>练习</RouterLink>,
    key: "problems"
  },
  {
    label: () => <RouterLink to={{ name: "contests" }}>竞赛 & 作业</RouterLink>,
    key: "contests"
  },
  {
    label: () => <RouterLink to={{ name: "leaderboard" }}>排名</RouterLink>,
    key: "leaderboard"
  },
  {
    label: () => <RouterLink to={{ name: "help" }}>帮助</RouterLink>,
    key: "help"
  }
]
</script>

<style scoped lang="scss">
.top-nav {
  width: 1100px;
  height: 60px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: flex-start;

  &.submission {
    width: auto;
    padding: 0 calc(var(--layout-padding) * 2);
  }

  .app-name {
    display: flex;
    align-items: center;
    font-size: 22px;
    font-weight: 500;

    .logo {
      margin-right: 12px;
    }
  }
}
</style>
