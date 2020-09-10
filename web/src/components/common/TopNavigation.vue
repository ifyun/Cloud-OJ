<template>
  <el-header>
    <div class="header-wrapper">
      <el-row type="flex" align="middle">
        <el-col :span="6">
          <div>
            <a href="/"><img class="logo" src="@/assets/logo.png" alt="logo"/></a>
          </div>
        </el-col>
        <el-col :span="12">
          <!-- Nav Menu -->
          <el-menu class="top-nav" mode="horizontal"
                   text-color="#909399"
                   active-text-color="#409eff"
                   :default-active="active"
                   @select="handleSelect">
            <el-submenu index="1">
              <template slot="title">
                <i class="el-icon-s-order"></i>
                <span>题库</span>
              </template>
              <el-menu-item index="1-1">开放题库</el-menu-item>
              <el-menu-item index="1-2">竞赛/作业题库</el-menu-item>
            </el-submenu>
            <el-menu-item index="2"><i class="el-icon-s-data"></i>排行榜</el-menu-item>
            <el-menu-item index="3"
                          v-if="userInfo != null && userInfo['roleId'] > 0">
              <i class="el-icon-s-management"></i>
              <span>管理</span>
            </el-menu-item>
            <el-menu-item index="4">
              <i class="el-icon-question"></i>
              <span>帮助</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        <el-col :span="6">
          <div class="account-area">
            <el-avatar class="avatar"></el-avatar>
            <span class="el-dropdown-link" style="color: #909399"
                  v-if="userInfo == null"
                  @click="login">登录
            </span>
            <el-dropdown v-else @command="userMenuClick">
              <span class="el-dropdown-link" style="color: #909399">
                <span><b>{{ userInfo != null ? userInfo.name : '' }}</b></span>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="exit">退出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-header>
</template>

<script>
import {apiPath, clearToken, userInfo} from "@/js/util";

export default {
  name: "TopNavigation",
  props: ["active"],
  data() {
    return {
      userInfo: userInfo(),
      paths: {
        '1-1': '/',
        '1-2': '/contest',
        '2': '/ranking',
        '3': '/manage'
      }
    }
  },
  methods: {
    handleSelect(key) {
      window.location.href = this.paths[key]
    },
    login() {
      window.location.href = '/login'
    },
    userMenuClick(command) {
      if (command === 'exit') {
        this.logoff()
      }
    },
    logoff() {
      this.$axios({
        url: apiPath.logoff,
        method: 'delete',
        headers: {
          'token': userInfo().token
        },
        params: {
          userId: userInfo().userId
        }
      }).then(() => {
        clearToken()
        this.login()
      }).catch(() => {
        clearToken()
        this.login()
      })
    }
  }
}
</script>

<style scoped>
.logo {
  height: 35px;
  float: left;
  border-radius: 5px;
}

.top-nav {
  border: none !important;
}

.header-wrapper {
  border-bottom: solid 1px #e6e6e6;
  box-shadow: 0 5px 8px -5px #e6e6e6;
  padding: 0 20px;
}

.account-area {
  float: right;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  cursor: pointer;
  height: 35px;
  width: 35px;
}

.el-dropdown-link {
  margin-left: 10px;
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>