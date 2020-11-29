<template>
  <div class="container">
    <div class="left">
      <div id="words">
        <h1>
          <span style="color: #DB6D63">Errors</span>
          = (<span style="color: #5EAAE8">More Code</span>)<sup>2</sup></h1>
      </div>
    </div>
    <div class=right>
      <div id="login-content">
        <div class="logo-div">
          <a href="/"><img class="logo" :src="'/favicon.svg'" alt="logo"></a>
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
      </div>
    </div>
  </div>
</template>

<script>
import LoginTab from "@/components/login/LoginTab"
import SignupTab from "@/components/login/SignupTab"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Login",
  components: {
    LoginTab,
    SignupTab,
    BottomArea
  },
  data() {
    return {
      currentView: "LoginTab"
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
  margin: 125px auto 0;
  width: 350px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  width: 350px;
  margin-top: 25px;
  display: flex;
  align-items: flex-end;
}

.logo-div {
  width: 350px;
}

.logo {
  height: 50px;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

h1 {
  font-size: 28pt;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

h2 {
  font-weight: normal;
  margin: 0 10px 0 0;
}

.login-tab {
  margin: 25px 0 25px;
}
</style>
