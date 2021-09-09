<template>
  <el-container id="app" direction="vertical">
    <el-header v-if="ui.topNav">
      <top-navigation :type="headerType"/>
    </el-header>
    <el-container>
      <el-aside v-if="ui.sideNav && userInfo != null" class="aside">
        <el-menu class="side-nav" mode="vertical" :default-active="$route.path"
                 :collapse="ui.collapse" @select="onSelect">
          <el-menu-item v-on:click="toggleCollapse">
            <i v-bind:class="ui.collapse ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'">
            </i>
            <span>折叠</span>
          </el-menu-item>
          <el-menu-item index="/manage/problems" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-grid"></i>
            <span slot="title">题库管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/contest" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-flag"></i>
            <span slot="title">竞赛管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/user" :disabled="[1, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-user-solid"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/settings" :disabled="userInfo['roleId'] !== 3">
            <i class="el-icon-s-tools"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main :class="{'no-padding': ui.noPadding}">
          <router-view :key="$route.fullPath">
          </router-view>
        </el-main>
        <el-footer v-show="ui.bottom">
          <bottom/>
        </el-footer>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
import "@/assets/css/theme.css"
import TopNavigation from "@/components/common/TopNavigation"
import Bottom from "@/components/common/Bottom"
import {userInfo} from "@/util"
import axios from "axios"
import {ApiPath} from "@/service"

export default {
  name: "App",
  components: {
    TopNavigation,
    Bottom
  },
  data() {
    return {
      active: "",
      headerType: null,
      ui: {
        topNav: false,
        sideNav: false,
        bottom: false,
        collapse: false,
        noPadding: false,
      },
      userInfo: userInfo()
    }
  },
  created() {
    axios.head(`${ApiPath.IMAGE}/favicon.png`)
        .then(() => {
          this.$siteSetting.setFavicon(`${ApiPath.IMAGE}/favicon.png`)
        })
        .catch(() => {
        })
  },
  watch: {
    $route(to) {
      if (to.path.startsWith("/contest_leaderboard")) {
        this.ui.topNav = false
        this.ui.bottom = false
        this.ui.noPadding = true
      } else {
        this.ui.topNav = true
        this.ui.bottom = true
        this.ui.noPadding = false
      }

      if (to.path.startsWith("/manage")) {
        this.ui.sideNav = true
        this.headerType = "admin"
      } else if (to.path.startsWith("/commit")) {
        this.ui.sideNav = false
        this.headerType = "commit"
      } else {
        this.ui.sideNav = false
        this.headerType = null
      }
    }
  },
  methods: {
    toggleCollapse() {
      this.ui.collapse = !this.ui.collapse
    },
    onSelect(key) {
      this.$router.push(key).catch(() => {
      })
    }
  }
}
</script>

<style scoped lang="scss">
#app {
  height: 100%;
  margin-top: 0;
}

.no-padding {
  padding: 0;
}

.aside {
  height: 100%;
  width: auto !important;
  box-shadow: 2px 0 4px 0 rgba(0, 0, 0, 0.08);

  .side-nav {
    border: none;
    background-color: inherit;

    &:not(.el-menu--collapse) {
      width: 180px;
    }
  }
}
</style>