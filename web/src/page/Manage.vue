<template>
  <div style="height: 100%">
    <TopNavigation active="4"/>
    <div v-if="error.code !== undefined">
      <Error style="margin-top: 35px" :error="error"/>
      <BottomArea style="margin-top: 35px"/>
    </div>
    <el-container v-else class="container">
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
      <el-main style="margin-top: 15px">
        <component :is="currentView"/>
        <BottomArea style="margin-top: 50px"/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ProblemsManage from "@/components/manage/problem/ProblemsManage"
import ContestManage from "@/components/manage/contest/ContestManage"
import UserManage from "@/components/manage/user/UserManage"
import Settings from "@/components/manage/Settings"
import Error from "@/components/Error"
import BottomArea from "@/components/common/BottomArea"
import {toLoginPage, userInfo} from "@/script/util"

let page = new Map([
  ['1', 'ProblemsManage'],
  ['2', 'ContestManage'],
  ['3', 'UserManage'],
  ['4', 'Settings']
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
    } else if (this.userInfo['roleId'] === 0) {
      this.error = {
        code: 403,
        text: '你没有权限访问此页面'
      }
    }
  },
  mounted() {
    if (this.userInfo['roleId'] === 1) {
      this.active = '3'
      this.onSelect('3')
    } else {
      this.active = '1'
      this.onSelect('1')
    }
  },
  data() {
    return {
      userInfo: userInfo(),
      collapse: false,
      active: '',
      currentView: '',
      error: {
        code: undefined,
        text: ''
      }
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
  border: none;
}

.side-nav:not(.el-menu--collapse) {
  width: 180px;
}

.aside {
  margin-top: 1px;
  height: 100%;
  width: auto !important;
  border-right: 1px solid #EBEEF5;
}

.container {
  padding: 0 20px;
  min-width: 1150px !important;
  height: calc(100% - 61px);
}
</style>