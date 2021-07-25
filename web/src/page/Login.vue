<template>
  <div v-if="display" class="container">
    <div class="left">
      <div id="text">
        <h1>
          <span style="color: #499CD5">SELECT</span>
          <span style="color: #FFC560">&nbsp;*</span>
          <br>
          <span style="color: #499CD5">FROM</span>
          <span>&nbsp;WORLD</span>
          <br>
          <span style="color: #499CD5">WHERE</span>
          <span>&nbsp;SOMEONE</span>
          <span style="color: #499CD5">&nbsp;LIKE</span>
          <span style="color: #FA536F">&nbsp;'YOU'</span>
        </h1>
      </div>
    </div>
    <div class=right>
      <div id="login-content">
        <div class="logo-div">
          <a href="/" :style="{display: siteSetting['hideLogo'] === true ? 'none': ''}">
            <el-avatar class="logo" shape="square" size="large" :src="logoUrl" alt="logo">
              <img class="logo" :src="'/favicon.png'" alt="logo">
            </el-avatar>
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
        <transition name="fade" mode="out-in">
          <component class="login-tab" :is="currentView"/>
        </transition>
        <el-divider/>
        <bottom-area/>
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
import {siteSetting, userInfo} from "@/util"

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
      display: false,
      from: "/",
      currentView: "LoginTab",
      logoUrl: `${ApiPath.IMAGE}/favicon.png`,
      siteSetting
    }
  },
  beforeMount() {
    if (userInfo() != null) {
      this.$router.replace("/")
    } else {
      this.display = true
    }
  },
  methods: {
    login() {
      this.currentView = "LoginTab"
    },
    signup() {
      this.currentView = "SignupTab"
    }
  }
}
</script>

<style scoped>
.container {
  height: calc(100% + 60px);
  min-width: 1100px;
  display: flex;
  flex-direction: row;
}

.left {
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100% + 52px);
  min-height: 700px;
  /* background by SVGBackgrounds.com */
  background-color: #3A3A3A;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100%25' height='100%25' viewBox='0 0 1600 800'%3E%3Cg stroke='%23000' stroke-width='66.7' stroke-opacity='0.05' %3E%3Ccircle fill='%23383838' cx='0' cy='0' r='1800'/%3E%3Ccircle fill='%23363636' cx='0' cy='0' r='1700'/%3E%3Ccircle fill='%23353535' cx='0' cy='0' r='1600'/%3E%3Ccircle fill='%23333333' cx='0' cy='0' r='1500'/%3E%3Ccircle fill='%23313131' cx='0' cy='0' r='1400'/%3E%3Ccircle fill='%232f2f2f' cx='0' cy='0' r='1300'/%3E%3Ccircle fill='%232e2e2e' cx='0' cy='0' r='1200'/%3E%3Ccircle fill='%232c2c2c' cx='0' cy='0' r='1100'/%3E%3Ccircle fill='%232a2a2a' cx='0' cy='0' r='1000'/%3E%3Ccircle fill='%23292929' cx='0' cy='0' r='900'/%3E%3Ccircle fill='%23272727' cx='0' cy='0' r='800'/%3E%3Ccircle fill='%23262626' cx='0' cy='0' r='700'/%3E%3Ccircle fill='%23242424' cx='0' cy='0' r='600'/%3E%3Ccircle fill='%23222222' cx='0' cy='0' r='500'/%3E%3Ccircle fill='%23212121' cx='0' cy='0' r='400'/%3E%3Ccircle fill='%231f1f1f' cx='0' cy='0' r='300'/%3E%3Ccircle fill='%231e1e1e' cx='0' cy='0' r='200'/%3E%3Ccircle fill='%231c1c1c' cx='0' cy='0' r='100'/%3E%3C/g%3E%3C/svg%3E");
  background-attachment: fixed;
  background-size: cover;
  border-radius: 8px;
  margin: 4px;
}

.right {
  min-width: 520px;
  height: calc(100% + 60px);
  min-height: 700px;
  overflow: hidden;
}

#text {
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
}

#text h1 {
  font-size: 26pt;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
  font-family: Courier, serif;
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
  background-color: transparent;
  margin-right: 5px;
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
