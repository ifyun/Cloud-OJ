<template>
  <div style="margin: 20px">
    <el-form label-position="right"
             :model="loginForm">
      <el-form-item>
        <el-input class="login-input" prefix-icon="el-icon-postcard"
                  placeholder="ID"
                  v-model="loginForm.username">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-input class="login-input" type="password" prefix-icon="el-icon-lock"
                  placeholder="密码"
                  v-model="loginForm.password">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="primary"
                   @click="onLogin">登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "LoginTab",
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    onLogin() {
      this.loginForm.password = this.$md5(this.loginForm.password)
      this.$axios({
        url: this.apiUrl + 'login',
        method: 'post',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        data: this.qs.stringify(this.loginForm)
      }).then((res) => {
        sessionStorage.setItem('cloud_oj_token', JSON.stringify(res.data))
        window.location.href = '../'
      }).catch((error) => {
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
.login-input {
  width: 320px;
}

.login-button {
  width: 100%;
}
</style>