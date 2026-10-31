<template>
  <div class="side-navbar">
    <n-menu
      :value="routeName"
      :options="menuOptions"
      :collapsed-icon-size="20" />
  </div>
</template>

<script setup lang="tsx">
import {
  CodeRound,
  DashboardRound,
  LocalLibraryRound,
  PeopleAltRound,
  SchoolRound,
  SettingsRound
} from "@vicons/material"
import { NIcon, NMenu } from "naive-ui"
import { computed } from "vue"
import { RouterLink, useRoute } from "vue-router"

const route = useRoute()
const routeName = computed(() => route.name?.toString())

const menuOptions = [
  {
    label: () => <RouterLink to={{ name: "dashboard" }}>仪表板</RouterLink>,
    key: "dashboard",
    icon: () => (
      <NIcon>
        <DashboardRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "admin-problems" }}>题目</RouterLink>,
    key: "admin-problems",
    icon: () => (
      <NIcon>
        <LocalLibraryRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "admin-contests" }}>竞赛</RouterLink>,
    key: "admin-contests",
    icon: () => (
      <NIcon>
        <SchoolRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "admin-users" }}>用户</RouterLink>,
    key: "admin-users",
    icon: () => (
      <NIcon>
        <PeopleAltRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "admin-solutions" }}>提交</RouterLink>,
    key: "admin-solutions",
    icon: () => (
      <NIcon>
        <CodeRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "settings" }}>设置</RouterLink>,
    key: "settings",
    icon: () => (
      <NIcon>
        <SettingsRound />
      </NIcon>
    )
  }
]
</script>
