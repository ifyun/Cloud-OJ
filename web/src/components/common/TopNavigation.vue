<template>
  <el-header class="header">
    <div class="header-wrapper">
      <el-row type="flex" align="middle">
        <el-col :span="20">
          <div class="nav">
            <div class="logo-div">
              <a href="/" :style="{display: siteSetting.hideLogo === true ? 'none': ''}">
                <el-avatar class="logo" shape="square" :src="logoUrl" alt="logo">
                  <img :src="'/favicon.png'" alt="logo">
                </el-avatar>
              </a>
              <a class="app-name" type="success" href="/">{{ siteSetting.name }}</a>
            </div>
            <!-- 导航菜单 -->
            <el-menu class="top-menu" mode="horizontal" :default-active="$route.path" @select="onSelect">
              <el-menu-item index="/problems">
                <span>题库</span>
              </el-menu-item>
              <el-menu-item index="/contest">
                <span>竞赛</span>
              </el-menu-item>
              <el-menu-item index="/ranking">
                <span>排行榜</span>
              </el-menu-item>
              <el-menu-item index="/help">
                <span>帮助</span>
              </el-menu-item>
            </el-menu>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="account-area">
            <el-avatar v-if="userInfo != null" class="el-icon--left" size="medium"
                       :src="`/api/file/image/avatar/${userInfo.userId}.png`"
                       icon="el-icon-user-solid" alt="user">
            </el-avatar>
            <el-link v-if="userInfo == null" :underline="false" href="/login">
              <Icon class="el-icon--left" name="sign-in-alt" scale="0.85"/>
              <span>登录/注册</span>
            </el-link>
            <el-dropdown v-else @command="userMenuClick">
              <span class="el-dropdown-link" style="color: #303133">
                <span>{{ userInfo != null ? userInfo.name : '' }}</span>
                <i class="el-icon-arrow-down el-icon--right"/>
              </span>
              <!-- 用户菜单 -->
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item v-if="userInfo != null && userInfo['roleId'] > 0"
                                  command="manage">
                  <i class="el-icon-s-management"></i>
                  <span>系统管理</span>
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

export default {
  name: "TopNavigation",
  components: {
    Icon
  },
  props: ["active"],
  beforeMount() {
    siteSetting.setTitle()
  },
  data() {
    return {
      logoUrl: `${ApiPath.IMAGE}/favicon.png`,
      siteSetting,
      userInfo: userInfo()
    }
  },
  methods: {
    onSelect(key) {
      if (key === "/contest" || key === "/ranking") {
        window.sessionStorage.removeItem("contest")
      }
      this.$router.push(key)
    },
    userMenuClick(command) {
      switch (command) {
        case "profile":
          this.$router.push("/profile")
          break
        case "manage":
          // 检查权限
          if (userInfo()["roleId"] === 1) {
            this.$router.push("/manage/user")
          } else {
            this.$router.push("manage/problems")
          }
          break
        case "exit":
          this.userInfo = null
          this.logoff()
      }
    },
    logoff() {
      AuthApi.logoff(this.userInfo)
          .then(() => {
            toLoginPage(this, true)
          })
          .catch(() => {
            toLoginPage(this, true)
          })
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
}

.header {
  width: 100%;
}

.header-wrapper {
  padding: 0 20px;
  background-color: white;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.08)
}

.nav {
  display: flex;
  flex-direction: row;
}

.top-menu {
  border: none !important;
  margin-left: 15px;
  background-color: inherit;
}

.logo {
  margin-right: 6px;
  width: auto !important;
  background-color: transparent;
}

.account-area {
  float: right;
  display: flex;
  justify-content: center;
  align-items: center;
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