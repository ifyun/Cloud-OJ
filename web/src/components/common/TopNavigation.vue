<template>
  <el-header class="header">
    <div class="header-wrapper">
      <el-row type="flex" align="middle">
        <el-col :span="20">
          <div class="nav">
            <div class="logo-div">
              <a href="/">
                <img class="logo" :src="logoUrl" alt="logo">
              </a>
              <a class="app-name" type="success" href="/">{{ siteSetting.name }}</a>
            </div>
            <!-- Nav Menu -->
            <el-menu class="top-menu" mode="horizontal" :default-active="active" @select="onSelect">
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
            <img v-if="userInfo != null" class="avatar el-icon--left"
                 :src="`/api/file/image/avatar/${userInfo.userId}.png`"
                 onerror="this.src='/icons/no_avatar.png'" alt="avatar">
            <el-link v-if="userInfo == null" :underline="false" href="/login">
              <Icon class="el-icon--left" name="sign-in-alt" scale="0.85"/>
              <span>登录/注册</span>
            </el-link>
            <el-dropdown v-else @command="userMenuClick">
              <span class="el-dropdown-link" style="color: #303133">
                <span>{{ userInfo != null ? userInfo.name : '' }}</span>
                <i class="el-icon-arrow-down el-icon--right"/>
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
import {siteSetting, toLoginPage, userInfo} from "@/util"
import {ApiPath, AuthApi} from "@/service"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/sign-in-alt"
import axios from "axios"

export default {
  name: "TopNavigation",
  components: {
    Icon
  },
  props: ["active"],
  beforeMount() {
    siteSetting.setTitle()
    this.checkLogo()
  },
  data() {
    return {
      logoUrl: "",
      siteSetting,
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
      if (key === "2" || key === "3") {
        window.sessionStorage.removeItem("contest")
      }
      window.location.href = this.paths[parseInt(key)]
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
    },
    checkLogo() {
      const url = `${ApiPath.IMAGE}/favicon.png`
      axios.head(url).then(() => {
        this.logoUrl = url
        this.siteSetting.setFavicon(url)
      }).catch(() => {
        this.logoUrl = "/favicon.png"
      })
    }
  }
}
</script>

<style scoped>
.header {
  position: absolute;
  top: 0;
  width: 100%;
}

.header-wrapper {
  border-bottom: solid 1px #e6e6e6;
  padding: 0 20px;
  background-color: white;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.08);
}

.nav {
  display: flex;
  flex-direction: row;
}

.top-menu {
  border: none !important;
  margin-left: 35px;
  background-color: inherit;
}

.logo {
  height: 40px;
}

.account-area {
  float: right;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  height: 36px;
  width: 36px;
  border-radius: 18px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.el-menu-item.is-active i {
  color: inherit;
}
</style>