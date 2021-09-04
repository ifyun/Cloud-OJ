<template>
  <div id="header">
    <div id="header-content" :class="headerClass">
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
              <el-menu-item index="/leaderboard">
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
            <el-link v-if="userInfo == null" :underline="false"
                     @click="loginDialog.visible = true">
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
    <!-- 登录/注册窗口 -->
    <el-dialog width="500px" :visible.sync="loginDialog.visible" :append-to-body="true">
      <Login/>
    </el-dialog>
  </div>
</template>

<script>
import {clearToken, userInfo} from "@/util"
import {ApiPath, AuthApi} from "@/service"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/sign-in-alt"
import Login from "@/components/Login"

export default {
  name: "TopNavigation",
  components: {
    Login,
    Icon
  },
  props: ["active", "type"],
  created() {
    this.verifyToken()
    this.siteSetting.setTitle()

    this.$bus.$on("login", () => {
      clearToken()
      this.loginDialog.visible = true
    })
    this.$bus.$on("logoff", () => {
      this.logoff()
    })
  },
  computed: {
    headerClass() {
      switch (this.type) {
        case "admin":
          return "header-admin"
        case "commit":
          return "header-commit"
        default:
          return "header-normal"
      }
    }
  },
  data() {
    return {
      logoUrl: `${ApiPath.IMAGE}/favicon.png`,
      siteSetting: this.$siteSetting,
      userInfo: userInfo(),
      loginDialog: {
        visible: false
      }
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
            this.$router.push("/manage/problems")
          }
          break
        case "exit":
          this.userInfo = null
          this.logoff()
      }
    },
    logoff() {
      AuthApi.logoff(this.userInfo).finally(() => {
        clearToken()
        this.loginDialog.visible = true
      })
    },
    verifyToken() {
      if (this.userInfo != null) {
        AuthApi.verify(this.userInfo).catch(() => {
          clearToken()
          this.loginDialog.visible = true
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
#header {
  width: 100%;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.08);

  #header-content {
    margin: 0 auto;
  }

  .header-normal {
    width: 1100px;
  }

  .header-commit {
    padding: 0 20px;
    min-width: 1100px;
    max-width: 1280px;
  }

  .header-admin {
    padding: 0 20px 0 10px;
    background-color: white;
  }
}

.nav {
  display: flex;
  flex-direction: row;

  .logo {
    margin-right: 10px;
    width: auto !important;
    background-color: transparent;
  }

  a {
    text-decoration: none;
  }

  .top-menu {
    border: none !important;
    margin-left: 15px;
    background-color: inherit;
  }
}

.account-area {
  float: right;
  display: flex;
  justify-content: center;
  align-items: center;

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
}
</style>