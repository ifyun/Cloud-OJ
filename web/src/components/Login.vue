<template>
  <div id="root">
    <div class="logo-div">
      <a href="/" :style="{display: siteSetting.hideLogo === true ? 'none': ''}">
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
  </div>
</template>

<script>
import LoginTab from "@/components/auth/LoginTab"
import SignupTab from "@/components/auth/SignupTab"
import {ApiPath} from "@/service"

export default {
  name: "Login",
  components: {
    LoginTab,
    SignupTab
  },
  data() {
    return {
      siteSetting: this.$siteSetting,
      currentView: "LoginTab",
      logoUrl: `${ApiPath.IMAGE}/favicon.png`
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

<style scoped lang="scss">
#root {
  padding: 0 35px;
  overflow: hidden;
}

.logo-div {
  .logo {
    background-color: transparent;
    margin-right: 10px;
  }

  .app-name {
    font-size: 20pt !important;
  }
}

.title {
  width: 100%;
  margin-top: 20px;
  display: flex;
  align-items: baseline;

  h2 {
    font-weight: normal;
    margin: 0 10px 0 0;
  }
}

.login-tab {
  margin: 25px 0 25px;
}
</style>