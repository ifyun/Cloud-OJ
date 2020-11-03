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
      <el-container class="main-container">
        <el-main>
          <component :is="currentView"></component>
        </el-main>
        <el-footer>
          <BottomArea/>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ProblemsManage from "@/components/manage/problem/ProblemsManage"
import ContestManage from "@/components/manage/contest/ContestManage"
import UserManage from "@/components/manage/user/UserManage"
import BottomArea from "@/components/common/BottomArea"
import {userInfo} from "@/script/util"

let page = new Map([
  ['1', 'ProblemsManage'],
  ['2', 'ContestManage'],
  ['3', 'UserManage']
])

export default {
  name: "Manager",
  components: {
    TopNavigation,
    ProblemsManage,
    ContestManage,
    UserManage,
    BottomArea
  },
  mounted() {
    if (this.userInfo['roleId'] === 1) {
      this.active = 3
      this.onSelect('3')
    } else {
      this.active = 1
      this.onSelect('1')
    }
  },
  data() {
    return {
      userInfo: userInfo(),
      collapse: true,
      active: '',
      currentView: ''
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

.main-container {
  min-width: initial !important;
  overflow-x: auto;
  padding-bottom: 50px;
  height: 100%;
}
</style>