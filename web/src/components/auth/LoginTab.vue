<template>
  <div>
    <el-form ref="loginForm"
             :rules="loginRules"
             :model="loginForm">
      <el-form-item prop="username">
        <el-input class="login-input" auto-complete="off"
                  prefix-icon="el-icon-postcard" placeholder="ID"
                  v-model="loginForm.username">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input class="login-input" type="password" auto-complete="off"
                  prefix-icon="el-icon-lock" placeholder="密码" v-model="loginForm.password"
                  @keyup.enter.native="login">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="primary" round
                   :loading="loading" @click="login">登 录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {Notice, saveToken} from "@/util"
import {AuthApi} from "@/service"
import md5 from "crypto-js/md5"

export default {
  name: "LoginTab",
  beforeMount() {
    this.siteSetting.setTitle("登录")
  },
  data() {
    return {
      loading: false,
      loginForm: {
        username: "",
        password: ""
      },
      loginRules: {
        username: [
          {required: true, message: "请输入用户名", trigger: "blur"}
        ],
        password: [
          {required: true, message: "请输入密码", trigger: "blur"}
        ]
      }
    }
  },
  methods: {
    login() {
      this.loading = true
      this.$refs["loginForm"].validate((valid) => {
        if (valid) {
          AuthApi.login({
            username: this.loginForm.username,
            password: md5(this.loginForm.password).toString()
          }).then((data) => {
            saveToken(JSON.stringify(data))
            window.location.href = "/"
          }).catch((error) => {
            if (error.code === 400) {
              Notice.notify.warning(this, {
                offset: 0,
                title: "登录失败",
                message: "用户名或密码错误!"
              })
            } else {
              Notice.notify.error(this, {
                offset: 0,
                title: "登录失败",
                message: `${error.code} ${error.msg}`
              })
            }
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.loading = false
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-input {
  min-width: 350px;
}

.login-button {
  min-width: 350px;
  width: 100%;
}
</style>