<template>
  <div class="side-navbar">
    <n-menu
      :value="routeName"
      :options="menuOptions"
      :collapsed-icon-size="20" />
  </div>
</template>

<script setup lang="tsx">
import { computed } from "vue"
import { RouterLink, useRoute } from "vue-router"
import { NIcon, NMenu } from "naive-ui"
import { Library, Options, People, School } from "@vicons/ionicons5"

const route = useRoute()
const routeName = computed(() => route.name?.toString())

const menuOptions = [
  {
    label: () => (
      <RouterLink to={{ name: "problem_admin" }}>题目管理</RouterLink>
    ),
    key: "problem_admin",
    icon: () => (
      <NIcon>
        <Library />
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
        <School />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "user_admin" }}>用户管理</RouterLink>,
    key: "user_admin",
    icon: () => (
      <NIcon>
        <People />
      </NIcon>
    )
  },
  {
    label: () => <RouterLink to={{ name: "settings" }}>系统设置</RouterLink>,
    key: "settings",
    icon: () => (
      <NIcon>
        <Options />
      </NIcon>
    )
  }
]
</script>
