<template>
  <div style="margin: 25px 45px">
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
                   @click="signup('signupForm')">
          注册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {apiPath, copyObject} from "@/script/util"

const bcrypt = require('bcryptjs')

export default {
  name: "SignupTab",
  data() {
    let validatePassword = (rule, value, callback) => {
      if (value === '')
        callback(new Error('请再次输入密码'))
      else if (value !== this.signupForm.password)
        callback(new Error('两次输入密码不一致'))
      else
        callback()
    }
    return {
      loading: false,
      signupForm: {
        name: '',
        userId: '',
        password: '',
        checkPassword: '',
        section: '',
        email: ''
      },
      rules: {
        name: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, max: 16, message: '长度在 2 ~ 16 个字符', trigger: 'blur'}
        ],
        userId: [
          {required: true, message: '请输入ID', trigger: 'blur'},
          {min: 6, max: 16, message: '长度在 6 ~ 16 个字符', trigger: 'blur'}
        ],
        email: [
          {type: 'email', message: '请输入邮箱', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 16, message: '长度在 6 ~ 16 位字符', trigger: 'blur'}
        ],
        checkPassword: [
          {validator: validatePassword, trigger: 'blur'}
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
          data.checkPassword = ''
          data.password = bcrypt.hashSync(this.$md5(data.password), 10)
          this.$axios({
            url: apiPath.user,
            method: 'post',
            headers: {
              'Content-Type': 'application/json'
            },
            data: JSON.stringify(data)
          }).then((res) => {
            this.$notify({
              offset: 50,
              type: 'success',
              title: `用户 ${this.signupForm.name} 注册成功`,
              message: `${res.status}`
            })
            this.$refs[formName].resetFields()
            this.$refs['deleteForm'].clearValidate()
          }).catch((error) => {
            let res = error.response
            let msg
            if (res.status === 409)
              msg = '用户已存在'
            else
              msg = res.data.msg === undefined ? res.statusText : res.data.msg
            this.$notify.error({
              offset: 50,
              title: '注册失败',
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
  width: 320px;
}

.login-button {
  width: 100%;
}
</style>