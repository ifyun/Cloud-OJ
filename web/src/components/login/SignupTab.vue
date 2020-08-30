<template>
  <div style="margin: 20px">
    <el-form label-position="right"
             :model="signupForm" :rules="rules" ref="signupForm">
      <el-form-item prop="name">
        <el-input class="login-input" prefix-icon="el-icon-user"
                  v-model="signupForm.name"
                  placeholder="用户名/昵称">
        </el-input>
      </el-form-item>
      <el-form-item prop="userId">
        <el-input class="login-input" prefix-icon="el-icon-postcard"
                  v-model="signupForm.userId"
                  placeholder="ID是唯一的">
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input class="login-input" prefix-icon="el-icon-message"
                  v-model="signupForm.email"
                  placeholder="邮箱（非必填）">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input class="login-input" type="password" prefix-icon="el-icon-lock"
                  v-model="signupForm.password"
                  placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-input class="login-input" prefix-icon="el-icon-school"
                  v-model="signupForm.section"
                  placeholder="学校/班级（非必填）">
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
          {required: true, type: 'number', message: '请输入ID', trigger: 'blur'},
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
      console.log(this.$refs)
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // TODO 注册
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