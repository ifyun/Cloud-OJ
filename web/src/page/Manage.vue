<template>
  <div style="height: 100%">
    <TopNavigation active="4"/>
    <el-container class="container">
      <el-aside class="aside">
        <el-menu class="side-nav" mode="vertical"
                 :collapse="collapse"
                 :default-active="active"
                 @select="onSelect">
          <el-menu-item v-on:click="toggleCollapse">
            <i v-bind:class="collapse? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'">
            </i>
            <span>折叠</span>
          </el-menu-item>
          <el-menu-item index="1" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-order"></i>
            <span slot="title">题库管理</span>
          </el-menu-item>
          <el-menu-item index="2" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-flag"></i>
            <span slot="title">竞赛/作业管理</span>
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
        <component :is="currentView"></component>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ManagerProfile from "@/components/manage/ManagerProfile"
import ProblemsManage from "@/components/manage/problem/ProblemsManage"
import ContestManage from "@/components/manage/contest/ContestManage"
import UserManage from "@/components/manage/user/UserManage"
import {userInfo} from "@/js/util"

let page = new Map([
  ['1', 'ProblemsManage'],
  ['2', 'ContestManage'],
  ['3', 'UserManage']
])

export default {
  name: "Manager",
  components: {
    TopNavigation,
    ManagerProfile,
    ProblemsManage,
    ContestManage,
    UserManage
  },
  data() {
    return {
      userInfo: userInfo(),
      collapse: false,
      active: '',
      currentView: 'ManagerProfile'
    }
  },
  methods: {
    toggleCollapse() {
      this.collapse = !this.collapse
    },
    onSelect(key) {
      if (key != null) {
        this.currentView = page.get(key)
      }
    }
  }
}
</script>

<style scoped>
.side-nav {
  height: 100%;
  border: none;
}

.side-nav:not(.el-menu--collapse) {
  width: 180px;
}

.aside {
  margin-top: 15px;
  height: calc(100% - 30px);
  width: auto !important;
  box-shadow: 0 0 10px #e0e0e0;
}

.container {
  padding: 0 20px;
  height: calc(100% - 61px);
}
</style>