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
                   @click="signup">注 册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {copyObject, Notice} from "@/util"
import {UserApi} from "@/service"
import md5 from "crypto-js/md5"

const bcrypt = require("bcryptjs")

export default {
  name: "SignupTab",
  beforeMount() {
    this.siteSetting.setTitle("注册")
  },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次填写密码"))
      } else if (value !== this.signupForm.password) {
        callback(new Error("两次密码不一致"))
      } else {
        callback()
      }
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
    signup() {
      this.$refs["signupForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        this.loading = true
        let user = copyObject(this.signupForm)
        user.checkPassword = ""
        user.password = bcrypt.hashSync(md5(user.password).toString(), 10)
        UserApi.save(user, null, true).then(() => {
          Notice.notify.success(this, {
            offset: 0,
            title: "注册成功"
          })
          this.$refs["signupForm"].resetFields()
          this.$refs["signupForm"].clearValidate()
        }).catch((error) => {
          const msg = error.code === 409 ? "ID 已被使用" : `${error.msg}`
          Notice.notify.error(this, {
            offset: 0,
            title: "注册失败",
            message: `${error.code} ${msg}`
          })
        }).finally(() => {
          this.loading = false
        })
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