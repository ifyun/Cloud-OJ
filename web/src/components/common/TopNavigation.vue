<template>
  <el-header>
    <div class="header-wrapper">
      <el-row type="flex" align="middle">
        <el-col :span="4">
          <div>
            <a href="/">
              <img class="logo" src="@/assets/logo.png" alt="logo"/>
            </a>
          </div>
        </el-col>
        <el-col :span="16" style="display: flex; justify-content: center">
          <!-- Nav Menu -->
          <el-menu class="top-nav" mode="horizontal"
                   text-color="#909399"
                   active-text-color="#409eff"
                   :default-active="active"
                   @select="onSelect">
            <el-menu-item index="1">
              <i class="el-icon-s-order"></i>
              <span>题库</span>
            </el-menu-item>
            <el-menu-item index="2">
              <i class="el-icon-s-flag"></i>
              <span>竞赛/作业</span>
            </el-menu-item>
            <el-menu-item index="3">
              <i class="el-icon-s-data"></i>
              <span>排行榜</span>
            </el-menu-item>
            <el-menu-item index="4"
                          v-if="userInfo != null && userInfo['roleId'] > 0">
              <i class="el-icon-s-management"></i>
              <span>管理</span>
            </el-menu-item>
            <el-menu-item index="5">
              <i class="el-icon-question"></i>
              <span>帮助</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        <el-col :span="4">
          <div class="account-area">
            <img class="avatar"
                 :src="userInfo != null ? `./api/file/image/avatar/${userInfo.userId}.png` : ''"
                 onerror="this.src='/icons/no_avatar.svg'" alt="avatar">
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
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="history">
                  <i class="el-icon-time"></i>
                  <span>提交记录</span>
                </el-dropdown-item>
                <el-dropdown-item command="exit">
                  <i class="el-icon-switch-button"></i>
                  <span>退出</span>
                </el-dropdown-item>
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
        '1': '/',
        '2': '/contest',
        '3': '/ranking',
        '4': '/manage',
        '5': '/help'
      }
    }
  },
  methods: {
    onSelect(key) {
      window.location.href = this.paths[key]
    },
    login() {
      window.location.href = '/login'
    },
    userMenuClick(command) {
      switch (command) {
        case 'history':
          window.location.href = '/history'
          break
        case 'profile':
          window.location.href = '/profile'
          break
        case 'exit':
          this.logoff()
          break

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
  border: 1px solid #e0e0e0;
  border-radius: 20px;
}

.el-dropdown-link {
  margin-left: 5px;
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>