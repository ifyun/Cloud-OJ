<template>
  <div>
    <el-form :model="user" :rules="rules" ref="userForm" label-width="120px">
      <el-form-item v-if="!create">
        <el-avatar :size="160" :src="`/api/file/image/avatar/${userData.userId}.png`" alt="user">
          <img src="@/assets/icons/no_avatar.png" alt="user">
        </el-avatar>
      </el-form-item>
      <el-form-item label="User ID" prop="userId">
        <el-input prefix-icon="el-icon-postcard"
                  :disabled="!create"
                  v-model="user.userId">
        </el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="name">
        <el-input prefix-icon="el-icon-user"
                  v-model="user.name">
        </el-input>
      </el-form-item>
      <el-form-item v-if="!create" label="角色/权限">
        <el-select v-model="user['roleId']">
          <el-option v-for="role in roles"
                     :key="role.value"
                     :label="role.label"
                     :value="role.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input type="email" prefix-icon="el-icon-message" auto-complete="off"
                  placeholder="选填" v-model="user.email">
        </el-input>
      </el-form-item>
      <el-form-item v-if="create" label="密码" prop="password">
        <el-input type="password" prefix-icon="el-icon-lock" auto-complete="new-password"
                  v-model="user.password" placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item v-else label="设置新密码" prop="newPassword">
        <el-input type="password" prefix-icon="el-icon-lock" auto-complete="new-password"
                  v-model="user.newPassword" placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item label="学校/班级/部门">
        <el-input prefix-icon="el-icon-school" placeholder="选填" v-model="user.section">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" size="medium" @click="onSave"
                   :icon="create ? 'el-icon-plus' : 'el-icon-check'">
          <span>{{ create ? "创建" : "保存" }}</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {Notice, userInfo} from "@/util"
import {UserApi} from "@/service"
import md5 from "crypto-js/md5"

const bcrypt = require("bcryptjs")

export default {
  name: "UserEditor",
  props: {
    userData: Object,
    create: Boolean,
    dialogVisible: Boolean
  },
  watch: {
    userData: {
      handler(val) {
        this.user = val
      },
      immediate: true
    },
    user() {
      this.$refs.userForm.clearValidate()
    }
  },
  data() {
    return {
      user: {},
      roles: [
        {value: 0, label: "用户"},
        {value: 1, label: "用户管理员"},
        {value: 2, label: "题库管理员"},
        {value: 3, label: "ROOT"}
      ],
      rules: {
        name: [
          {required: true, message: "请输入用户名", trigger: "blur"},
          {min: 2, max: 16, message: "长度在 2 ~ 16 个字符", trigger: "blur"}
        ],
        userId: [
          {required: true, message: "请输入ID", trigger: "blur"},
          {min: 4, max: 16, message: "长度在 4 ~ 16 个字符", trigger: "blur"}
        ],
        email: [
          {type: "email", message: "请输入邮箱", trigger: "blur"}
        ],
        password: [
          {required: true, message: "请输入密码", trigger: "blur"},
          {min: 6, max: 16, message: "长度在 6 ~ 16 位字符", trigger: "blur"}
        ],
        newPassword: [
          {min: 6, max: 16, message: "长度在 6 ~ 16 位字符", trigger: "blur"}
        ]
      }
    }
  },
  methods: {
    onSave() {
      this.$refs.userForm.validate((valid) => {
        if (!valid) {
          return false
        }

        if (this.create) {
          this.user.password = bcrypt.hashSync(md5(this.user.password).toString(), 10)
        } else if (typeof this.user.newPassword !== "undefined") {
          this.user.password = bcrypt.hashSync(md5(this.user.newPassword).toString(), 10)
        }

        UserApi.save(this.user, userInfo(), this.create)
            .then(() => {
              this.$emit("refresh")
              Notice.notify.success(this, {
                title: this.create ? "已创建" : "已保存",
              })
            })
            .catch((error) => {
              if (error.code === 401) {
                this.$bus.$emit("login")
              } else {
                Notice.notify.error(this, {
                  title: this.create ? "创建失败" : "保存失败",
                  message: `${error.code} ${error.msg}`
                })
              }
            })
      })
    }
  }
}
</script>

<style scoped>
</style>