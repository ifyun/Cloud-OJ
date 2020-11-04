<template>
  <div style="height: 100%">
    <el-header class="header">
      <div class="header-wrapper">
        <a href="./"><img class="logo" src="@/assets/logo.png" align="center" alt="logo"></a>
        <div style="font-size: 16pt; margin-left: 10px">登录/注册</div>
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
    </el-container>
    <BottomArea style="margin: 0 25px"/>
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
}

.header-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  z-index: 10;
}

.container {
  justify-content: center;
  align-items: flex-start;
  height: calc(100% - 150px) !important;
}

.login-tabs {
  margin-top: 180px;
}
</style>
