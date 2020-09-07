<template>
  <div style="margin: 20px">
    <el-form ref="signupForm"
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
      <el-form-item>
        <el-input class="login-input" prefix-icon="el-icon-school"
                  v-model="signupForm.section"
                  placeholder="学校/班级/部门（选填）">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button class="login-button" type="success"
                   @click="signup('signupForm')">注册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "SignupTab",
  data() {
    return {
      signupForm: {
        name: '',
        userId: '',
        password: '',
        section: '',
        email: ''
      },
      rules: {
        name: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 16, message: '长度在 3 ~ 16 个字符', trigger: 'blur'}
        ],
        userId: [
          {required: true, message: '请输入ID', trigger: 'blur'},
          {min: 8, max: 16, message: '长度在 8 ~ 16 个字符', trigger: 'blur'}
        ],
        email: [
          {type: 'email', message: '请输入邮箱', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 16, message: '长度在 6 ~ 16 位字符', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    signup(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios({
            url: `api/manager/user`,
            method: 'post',
            headers: {
              'Content-Type': 'application/json'
            },
            data: JSON.stringify(this.signupForm)
          }).then((res) => {
            this.$notify({
              type: 'success',
              title: `用户${this.signupForm.name}注册成功`,
              message: `${res.status}`
            })
          }).catch((error) => {
            this.$notify.error({
              title: `注册失败`,
              message: `${error.response.status}`
            })
          })
        } else {
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