<template>
  <div>
    <el-form ref="signupForm" status-icon
             :model="signupForm"
             :rules="rules">
      <el-form-item prop="name">
        <el-input class="login-input" prefix-icon="el-icon-user"
                  v-model="signupForm.name"
                  placeholder="用户名/昵称">
        </el-input>
      </el-form-item>
      <el-form-item prop="userId">
        <el-input class="login-input" prefix-icon="el-icon-postcard"
                  v-model="signupForm.userId"
                  placeholder="ID（用于登录，不可重复）">
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input class="login-input" type="email"
                  prefix-icon="el-icon-message"
                  auto-complete="off"
                  v-model="signupForm.email"
                  placeholder="邮箱（选填）">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input class="login-input" type="password" prefix-icon="el-icon-lock"
                  v-model="signupForm.password" auto-complete="new-password"
                  placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item prop="checkPassword">
        <el-input class="login-input" type="password" prefix-icon="el-icon-lock"
                  v-model="signupForm.checkPassword" auto-complete="new-password"
                  placeholder="重复密码">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-input class="login-input" prefix-icon="el-icon-school"
                  v-model="signupForm.section"
                  placeholder="学校/班级/部门（选填）">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="success" round
                   :loading="loading"
                   @click="signup('signupForm')">注 册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {copyObject, Notice} from "@/script/util"
import {apiPath} from "@/script/env"

const bcrypt = require('bcryptjs')

export default {
  name: "SignupTab",
  beforeMount() {
    document.title = "注册 - Cloud OJ"
  },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === "")
        callback(new Error("请再次填写密码"))
      else if (value !== this.signupForm.password)
        callback(new Error("两次密码不一致"))
      else
        callback()
    }
    const validateUserId = (rule, value, callback) => {
      const regx = /^[A-Za-z0-9]+$/
      if (!regx.test(value)) {
        callback(new Error("只能是字母和数字"))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      signupForm: {
        name: "",
        userId: "",
        password: "",
        checkPassword: "",
        section: "",
        email: ""
      },
      rules: {
        name: [
          {required: true, message: "请填写用户名", trigger: "blur"},
          {min: 2, max: 16, message: "长度在 2 ~ 16 个字符", trigger: "blur"}
        ],
        userId: [
          {required: true, message: "请填写ID", trigger: "blur"},
          {validator: validateUserId, trigger: "blur"},
          {min: 6, max: 16, message: "长度在 6 ~ 16 个字符", trigger: "blur"}
        ],
        email: [
          {type: "email", message: "请填写正确的邮箱地址", trigger: "blur"}
        ],
        password: [
          {required: true, message: "请填写密码", trigger: "blur"},
          {min: 6, max: 16, message: "长度在 6 ~ 16 位字符", trigger: "blur"}
        ],
        checkPassword: [
          {validator: validatePassword, trigger: "blur"}
        ]
      }
    }
  },
  methods: {
    signup(formName) {
      this.loading = true
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let data = copyObject(this.signupForm)
          data.checkPassword = ""
          data.password = bcrypt.hashSync(this.$md5(data.password), 10)
          this.$axios({
            url: apiPath.user,
            method: "post",
            headers: {
              "Content-Type": "application/json"
            },
            data: JSON.stringify(data)
          }).then((res) => {
            Notice.notify.success(this, {
              offset: 0,
              title: `用户 ${this.signupForm.name} 注册成功`,
              message: `${res.status} ${res.statusText}`
            })
            this.$refs[formName].resetFields()
            this.$refs["deleteForm"].clearValidate()
          }).catch((error) => {
            let res = error.response
            let msg
            if (res.status === 409)
              msg = "用户已存在"
            else
              msg = res.data.msg === undefined ? res.statusText : res.data.msg
            Notice.notify.error(this, {
              offset: 0,
              title: "注册失败",
              message: `${res.status} ${msg}`
            })
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