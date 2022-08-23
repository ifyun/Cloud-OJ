<script setup lang="tsx">
import { RouterLink } from "vue-router"
import { NIcon, NMenu } from "naive-ui"
import {
  Book as BookIcon,
  Trophy as TrophyIcon,
  Users as UsersIcon
} from "@vicons/fa"
import { SettingsRound as SettingsIcon } from "@vicons/material"

const menuOptions = [
  {
    label: () => (
      <RouterLink to={{ name: "problem_admin" }}>题目管理</RouterLink>
    ),
    key: "problem_admin",
    icon: () => (
      <NIcon>
        <BookIcon />
      </NIcon>
    )
  },
  {
    label: () => (
      <RouterLink to={{ name: "contest_admin" }}>竞赛管理</RouterLink>
    ),
    key: "contest_admin",
    icon: () => (
      <NIcon>
        <TrophyIcon />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "user_admin" }}>用户管理</RouterLink>,
    key: "user_admin",
    icon: () => (
      <NIcon>
        <UsersIcon />
      </NIcon>
    )
  },
  {
    label: "系统设置",
    key: "settings",
    icon: () => (
      <NIcon>
        <SettingsIcon />
      </NIcon>
    )
  }
]
</script>

<template>
  <div class="side-navbar">
    <n-menu
      v-model:value="$route.name"
      :options="menuOptions"
      :collapsed-icon-size="20" />
  </div>
</template>
