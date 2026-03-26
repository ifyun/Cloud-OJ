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
    label: () => <RouterLink to={{ name: "admin_overview" }}>概览</RouterLink>,
    key: "admin_overview",
    icon: () => (
      <NIcon>
        <DashboardRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "problem_admin" }}>题目</RouterLink>,
    key: "problem_admin",
    icon: () => (
      <NIcon>
        <LocalLibraryRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "contest_admin" }}>竞赛</RouterLink>,
    key: "contest_admin",
    icon: () => (
      <NIcon>
        <SchoolRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "user_admin" }}>用户</RouterLink>,
    key: "user_admin",
    icon: () => (
      <NIcon>
        <PeopleAltRound />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "solution_admin" }}>提交</RouterLink>,
    key: "solution_admin",
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
