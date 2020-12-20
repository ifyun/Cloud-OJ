<template>
  <div>
    <el-form ref="loginForm"
             :rules="loginRules"
             :model="loginForm">
      <el-form-item prop="username">
        <el-input class="login-input" auto-complete="off"
                  prefix-icon="el-icon-postcard"
                  placeholder="ID"
                  v-model="loginForm.username">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input class="login-input" type="password" auto-complete="new-password"
                  prefix-icon="el-icon-lock"
                  placeholder="密码"
                  v-model="loginForm.password">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="primary" round
                   :loading="loading"
                   @click="onLogin">登 录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {copyObject, Notice, saveToken} from "@/script/util"
import {apiPath} from "@/script/env"

export default {
  name: "LoginTab",
  mounted() {
    document.title = "登录 - Cloud OJ"
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
    onLogin() {
      this.loading = true
      this.$refs["loginForm"].validate((valid) => {
        if (valid) {
          let data = copyObject(this.loginForm)
          data.password = this.$md5(data.password)
          this.$axios({
            url: apiPath.login,
            method: "post",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded"
            },
            data: this.qs.stringify(data)
          }).then((res) => {
            saveToken(JSON.stringify(res.data))
            window.location.href = "/"
          }).catch((error) => {
            let res = error.response
            if (res.status === 400) {
              Notice.notify.warning(this, {
                offset: 0,
                title: "登录失败",
                message: "用户名或密码错误!"
              })
            } else {
              Notice.notify.error(this, {
                offset: 0,
                title: "登录失败",
                message: `${res.status} ${res.data === undefined ? res.statusText : res.data.msg}`
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
  width: 350px;
}

.login-button {
  width: 350px;
}
</style>