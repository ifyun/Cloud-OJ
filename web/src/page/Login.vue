<template>
  <div style="height: 100%">
    <el-header class="header">
      <div class="header-wrapper">
        <el-link class="app-name" type="success" href="/">Cloud OJ</el-link>
      </div>
    </el-header>
    <el-container class="container">
      <el-tabs class="login-tabs" v-model="activeTab" type="border-card">
        <el-tab-pane label="登录" name="login">
          <LoginTab/>
        </el-tab-pane>
        <el-tab-pane label="注册" name="sign-up">
          <SignupTab/>
        </el-tab-pane>
      </el-tabs>
      <BottomArea style="margin-top: 120px"/>
    </el-container>
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
      activeTab: 'login'
    }
  }
}
</script>

<style scoped>
.header {
  position: fixed;
  margin: 0 auto;
  left: 0;
  right: 0;
  z-index: 10;
  background-color: #545c64;
}

.header-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  z-index: 10;
  background-color: inherit;
}

.container {
  flex-direction: column;
  align-items: center;
  height: calc(100% - 150px) !important;
}

.login-tabs {
  margin-top: 160px;
}
</style>
