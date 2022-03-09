<template>
  <div class="side-navbar">
    <n-menu :options="menuOptions" v-model:value="$route.name">
    </n-menu>
  </div>
</template>

<script setup lang="tsx">
import {RouterLink} from "vue-router"
import {NMenu} from "naive-ui"
import {Book as BookIcon, Trophy as TrophyIcon, Users as UsersIcon} from "@vicons/fa"
import {SettingsRound as SettingsIcon} from "@vicons/material"
import {renderIcon} from "@/utils"

const menuOptions = [
  {
    label: () => <RouterLink to={{name: "problem_admin"}}>题目管理</RouterLink>,
    key: "problem_admin",
    icon: renderIcon(BookIcon)
  },
  {
    label: () => <RouterLink to={{name: "contest_admin"}}>竞赛管理</RouterLink>,
    key: "contest_admin",
    icon: renderIcon(TrophyIcon)
  },
  {
    label: () => <RouterLink to={{name: "user_admin"}}>用户管理</RouterLink>,
    key: "user_admin",
    icon: renderIcon(UsersIcon)
  },
  {
    label: "系统设置",
    key: "settings",
    icon: renderIcon(SettingsIcon)
  }
]
</script>
