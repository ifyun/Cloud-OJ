<template>
  <el-header>
    <div class="header-wrapper">
      <el-row type="flex" align="middle">
        <el-col :span="20">
          <div class="flex-nav">
            <div class="logo-div">
              <img class="logo" :src="'/favicon.svg'" alt="logo">
              <a class="app-name" type="success" href="/">Cloud OJ</a>
            </div>
            <!-- Nav Menu -->
            <el-menu class="top-menu" mode="horizontal" :default-active="active" @select="onSelect"
                     background-color="#3B3B3B" text-color="#F0F0F0" active-text-color="#409EFF">
              <el-menu-item index="1">
                <i class="el-icon-s-grid"></i>
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
              <el-menu-item index="4">
                <i class="el-icon-question"></i>
                <span>帮助</span>
              </el-menu-item>
            </el-menu>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="account-area">
            <img class="avatar"
                 :src="userInfo != null ? `./api/file/image/avatar/${userInfo.userId}.png` : '/icons/user.svg'"
                 onerror="this.src='/icons/no_avatar.png'" alt="avatar">
            <span class="el-dropdown-link" style="color: #E0E0E0"
                  v-if="userInfo == null"
                  @click="login">登录
            </span>
            <el-dropdown v-else @command="userMenuClick">
              <span class="el-dropdown-link" style="color: #E0E0E0">
                <span><b>{{ userInfo != null ? userInfo.name : '' }}</b></span>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item v-if="userInfo != null && userInfo['roleId'] > 0"
                                  command="manage">
                  <i class="el-icon-s-management"></i>
                  <span>后台管理</span>
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
import {toLoginPage, userInfo} from "@/util"
import {AuthApi} from "@/service"

export default {
  name: "TopNavigation",
  props: ["active"],
  data() {
    return {
      userInfo: userInfo(),
      paths: {
        1: "/",
        2: "/contest",
        3: "/ranking",
        4: "/help"
      }
    }
  },
  methods: {
    onSelect(key) {
      if (key === "2" || key === "3")
        window.sessionStorage.removeItem("contest")
      window.location.href = this.paths[parseInt(key)]
    },
    login() {
      window.location.href = "/login"
    },
    userMenuClick(command) {
      switch (command) {
        case "profile":
          window.location.href = "/profile"
          break
        case "manage":
          window.location.href = "/manage"
          break
        case "exit":
          this.logoff()
      }
    },
    logoff() {
      AuthApi.logoff(this.userInfo)
          .then(() => {
            toLoginPage()
          })
          .catch(() => {
            toLoginPage()
          })
    }
  }
}
</script>

<style scoped>
.flex-nav {
  display: flex;
  flex-direction: row;
}

.top-menu {
  border: none !important;
  margin-left: 35px;
}

.header-wrapper {
  border-bottom: solid 1px #e6e6e6;
  padding: 0 20px;
  background-color: #3B3B3B;
}

.account-area {
  float: right;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  height: 32px;
  width: 32px;
  border-radius: 16px;
  margin-right: 2px;
}

.el-dropdown-link {
  margin-left: 10px;
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.el-menu-item i {
  color: #F0F0F0;
}

.el-menu-item.is-active i {
  color: inherit;
}
</style>