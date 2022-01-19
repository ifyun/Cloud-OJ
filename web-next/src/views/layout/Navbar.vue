<template>
  <div class="top-nav">
    <div class="app-name">
      <logo :size="40"/>
    </div>
    <div style="margin-left: 50px; font-weight: bold">
      <n-menu mode="horizontal" v-model:value="$route.name" :options="navMenuOptions"/>
    </div>
    <div style="margin-left: auto">
      <n-space size="large" align="center">
        <theme-switch/>
        <user-menu/>
      </n-space>
    </div>
  </div>
</template>

<script lang="tsx">
import {Options, Vue} from "vue-class-component"
import {RouterLink} from "vue-router"
import {NMenu, NSpace} from "naive-ui"
import Logo from "@/components/Logo.vue"
import ThemeSwitch from "@/views/layout/ThemeSwitch.vue"
import UserMenu from "@/views/layout/UserMenu.vue"

@Options({
  name: "Navbar",
  components: {
    NSpace,
    NMenu,
    Logo,
    ThemeSwitch,
    UserMenu
  }
})
export default class Navbar extends Vue {
  private navMenuOptions = [
    {
      label: () => <RouterLink to={{name: "problems"}}>练习</RouterLink>,
      key: "problems"
    },
    {
      label: () => <RouterLink to={{name: "contests"}}>竞赛 & 作业</RouterLink>,
      key: "contests"
    },
    {
      label: () => <RouterLink to={{name: "leaderboard"}}>排名</RouterLink>,
      key: "leaderboard"
    },
    {
      label: () => <RouterLink to={{name: "help"}}>帮助</RouterLink>,
      key: "help"
    }
  ]
}
</script>

<style scoped lang="scss">
.top-nav {
  height: 60px;
  padding: 0 25px;
  display: flex;
  align-items: center;
  justify-content: flex-start;

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
