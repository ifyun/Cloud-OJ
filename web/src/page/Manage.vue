<template>
  <div style="height: 100%">
    <TopNavigation active=""/>
    <div style="margin-top: 75px" v-if="error.code != null">
      <Error :error="error"/>
      <BottomArea class="bottom"/>
    </div>
    <el-container v-else class="container">
      <el-aside class="aside">
        <el-menu class="side-nav" mode="vertical" :default-active="active"
                 :collapse="collapse" @select="onSelect">
          <el-menu-item v-on:click="toggleCollapse">
            <i v-bind:class="collapse ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'">
            </i>
            <span>折叠</span>
          </el-menu-item>
          <el-menu-item index="1" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-grid"></i>
            <span slot="title">题库管理</span>
          </el-menu-item>
          <el-menu-item index="2" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-flag"></i>
            <span slot="title">竞赛管理</span>
          </el-menu-item>
          <el-menu-item index="3" :disabled="[1, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-user-solid"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="4" :disabled="userInfo['roleId'] !== 3">
            <i class="el-icon-s-tools"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="main">
          <component :is="currentView"/>
          <BottomArea style="margin-top: 50px"/>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ProblemsManage from "@/components/manage/problem/Index"
import ContestManage from "@/components/manage/contest/Index"
import UserManage from "@/components/manage/user/Index"
import Settings from "@/components/manage/Settings"
import Error from "@/components/Error"
import BottomArea from "@/components/common/BottomArea"
import {searchParams, toLoginPage, userInfo} from "@/util"

let pages = new Map([
  ["1", "ProblemsManage"],
  ["2", "ContestManage"],
  ["3", "UserManage"],
  ["4", "Settings"]
])

export default {
  name: "Manager",
  components: {
    TopNavigation,
    ProblemsManage,
    ContestManage,
    UserManage,
    Settings,
    Error,
    BottomArea
  },
  beforeMount() {
    if (this.userInfo == null) {
      toLoginPage()
    } else if (this.userInfo["roleId"] === 0) {
      this.error = {
        code: 403,
        msg: "你没有权限访问此页面"
      }
    }
  },
  mounted() {
    const active = searchParams().active
    if (this.userInfo["roleId"] === 1) {
      this.active = "3"
    } else {
      active == null ? this.active = "1" : this.active = active
    }
    this.onSelect(this.active)
  },
  data() {
    return {
      userInfo: userInfo(),
      collapse: false,
      active: "",
      currentView: "",
      error: {
        code: null,
        msg: ""
      }
    }
  },
  methods: {
    toggleCollapse() {
      this.collapse = !this.collapse
    },
    onSelect(key) {
      if (key != null) {
        history.pushState(null, "", "?page=1")
        this.currentView = pages.get(key)
      }
    }
  }
}
</script>

<style scoped>
.side-nav {
  border: none;
  background-color: inherit;
}

.side-nav:not(.el-menu--collapse) {
  width: 180px;
}

.aside {
  margin-top: 1px;
  height: 100%;
  width: auto !important;
  border-right: 1px solid #EBEEF5;
  background-color: white;
}

.container {
  margin-top: 60px;
  padding: 0;
  min-width: 1150px !important;
  max-width: 100% !important;
  height: calc(100% + 59px);
}

.main {
  margin: 0 auto;
}

.el-menu-item.is-active {
  color: #409EFF;
  background-color: #F5F5F5;
  box-shadow: inset -2px 0 0 #409EFF;
}
</style>