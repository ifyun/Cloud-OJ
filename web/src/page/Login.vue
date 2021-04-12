<template>
  <div class="container">
    <div class="left">
      <div id="words">
        <h2>
          <span style="color: #DB6D63">Errors</span>
          = (<span style="color: #5EAAE8">More Code</span>)<sup>2</sup>
        </h2>
        <pre class="egg">{{ egg }}</pre>
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
      siteSetting,
      egg: "0xE7AE97 0xE6B395 0xE4B88D\n" +
          "0xE4BC9A 0xE982A3 0xE698AF\n" +
          "0xE79C9F 0xE4B88D 0xE4BC9A"
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
  /* background by SVGBackgrounds.com */
  background-color: #3A3A3A;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100%25' height='100%25' viewBox='0 0 1600 800'%3E%3Cg stroke='%23000' stroke-width='66.7' stroke-opacity='0.05' %3E%3Ccircle fill='%23383838' cx='0' cy='0' r='1800'/%3E%3Ccircle fill='%23363636' cx='0' cy='0' r='1700'/%3E%3Ccircle fill='%23353535' cx='0' cy='0' r='1600'/%3E%3Ccircle fill='%23333333' cx='0' cy='0' r='1500'/%3E%3Ccircle fill='%23313131' cx='0' cy='0' r='1400'/%3E%3Ccircle fill='%232f2f2f' cx='0' cy='0' r='1300'/%3E%3Ccircle fill='%232e2e2e' cx='0' cy='0' r='1200'/%3E%3Ccircle fill='%232c2c2c' cx='0' cy='0' r='1100'/%3E%3Ccircle fill='%232a2a2a' cx='0' cy='0' r='1000'/%3E%3Ccircle fill='%23292929' cx='0' cy='0' r='900'/%3E%3Ccircle fill='%23272727' cx='0' cy='0' r='800'/%3E%3Ccircle fill='%23262626' cx='0' cy='0' r='700'/%3E%3Ccircle fill='%23242424' cx='0' cy='0' r='600'/%3E%3Ccircle fill='%23222222' cx='0' cy='0' r='500'/%3E%3Ccircle fill='%23212121' cx='0' cy='0' r='400'/%3E%3Ccircle fill='%231f1f1f' cx='0' cy='0' r='300'/%3E%3Ccircle fill='%231e1e1e' cx='0' cy='0' r='200'/%3E%3Ccircle fill='%231c1c1c' cx='0' cy='0' r='100'/%3E%3C/g%3E%3C/svg%3E");
  background-attachment: fixed;
  background-size: cover;
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
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

#words h2 {
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

.egg {
  color: #333333;
  font-family: serif;
  font-size: 12px;
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
  margin-right: 6px;
}

.app-name {
  font-size: 20pt !important;
}

#login-content h2 {
  font-weight: normal;
  margin: 0 10px 0 0;
}

.recommend {
  margin-top: 10px;
  font-size: 14px;
  display: flex;
  justify-content: center;
}
</style>
