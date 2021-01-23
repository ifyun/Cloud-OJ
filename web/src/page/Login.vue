<template>
  <div class="container">
    <div class="left">
      <div id="words">
        <h1>
          <span style="color: #DB6D63">Errors</span>
          = (<span style="color: #5EAAE8">More Code</span>)<sup>2</sup>
        </h1>
      </div>
    </div>
    <div class=right>
      <div id="login-content">
        <div class="logo-div">
          <a href="/">
            <img class="logo" :src="logoUrl" alt="logo">
          </a>
          <a class="app-name" type="success" href="/">{{ siteSetting.name }}</a>
        </div>
        <div v-if="currentView === 'LoginTab'" class="title">
          <h2>登录</h2>
          <span>没有账号？</span>
          <el-link type="primary" @click="signup">注册</el-link>
        </div>
        <div v-else class="title">
          <h2>注册</h2>
          <span>已有账号？</span>
          <el-link type="primary" @click="login">登录</el-link>
        </div>
        <component class="login-tab" :is="currentView"/>
        <el-divider/>
        <BottomArea/>
        <div class="recommend">
          <span style="margin-right: 5px">推荐使用</span>
          <el-link href="https://www.microsoft.com/zh-cn/edge" target="_blank">
            <Icon class="icon" name="brands/edge"/>
            新版 Edge
          </el-link>
          、
          <el-link href="https://www.google.cn/chrome/" target="_blank">
            <Icon class="icon" name="brands/chrome"/>
            Chrome
          </el-link>
          <span style="margin-left: 5px">访问本站</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LoginTab from "@/components/auth/LoginTab"
import SignupTab from "@/components/auth/SignupTab"
import BottomArea from "@/components/common/BottomArea"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/brands/chrome"
import "vue-awesome/icons/brands/edge"
import {ApiPath} from "@/service"
import axios from "axios"
import {siteSetting} from "@/util"

export default {
  name: "Login",
  components: {
    LoginTab,
    SignupTab,
    BottomArea,
    Icon
  },
  data() {
    return {
      currentView: "LoginTab",
      logoUrl: "",
      siteSetting
    }
  },
  beforeMount() {
    this.checkLogo()
  },
  methods: {
    login() {
      this.currentView = "LoginTab"
    },
    signup() {
      this.currentView = "SignupTab"
    },
    checkLogo() {
      const url = `${ApiPath.IMAGE}/favicon.png`
      axios.head(url).then(() => {
        this.logoUrl = url
        this.siteSetting.setFavicon(url)
      }).catch(() => {
        console.warn("use default favicon.")
        this.logoUrl = "/favicon.png"
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: calc(100% + 60px);
  min-width: 1100px;
}

.left {
  width: 60%;
  height: calc(100% + 60px);
  min-height: 700px;
  display: inline-block;
  vertical-align: top;
  background-color: #21252B;
}

.right {
  width: 40%;
  height: calc(100% + 60px);
  min-height: 700px;
  display: inline-block;
  vertical-align: top;
}

#words {
  color: white;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

#login-content {
  margin: 0 auto;
  width: 70%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.login-tab {
  margin: 25px 0 25px;
  width: 100%;
}

.title {
  width: 100%;
  margin-top: 25px;
  display: flex;
  align-items: flex-end;
}

.logo-div {
  width: 100%;
}

.logo {
  height: 45px;
}

.app-name {
  font-size: 24pt !important;
}

h1 {
  font-size: 28pt;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

h2 {
  font-weight: normal;
  margin: 0 10px 0 0;
}

.recommend {
  font-size: 14px;
  display: flex;
  justify-content: center;
}
</style>
