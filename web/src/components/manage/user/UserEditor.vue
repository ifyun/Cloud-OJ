<template>
  <div>
    <el-form :model="user" :rules="rules" ref="userForm"
             label-width="120px">
      <el-form-item label="ID" prop="userId">
        <el-input prefix-icon="el-icon-postcard"
                  :disabled="saveType === 'put'"
                  v-model="user.userId">
        </el-input>
      </el-form-item>
      <el-form-item label="用户名/昵称" prop="name">
        <el-input prefix-icon="el-icon-user"
                  v-model="user.name">
        </el-input>
      </el-form-item>
      <el-form-item v-if="saveType === 'put'"
                    label="角色/权限">
        <el-select v-model="user['roleId']">
          <el-option v-for="role in roles"
                     :key="role.value"
                     :label="role.label"
                     :value="role.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input type="email" prefix-icon="el-icon-message"
                  auto-complete="off" placeholder="选填"
                  v-model="user.email">
        </el-input>
      </el-form-item>
      <el-form-item v-if="saveType === 'post'" label="密码" prop="password">
        <el-input type="password" prefix-icon="el-icon-lock"
                  auto-complete="new-password"
                  v-model="user.password"
                  placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item v-else label="设置新密码" prop="newPassword">
        <el-input type="password" prefix-icon="el-icon-lock"
                  auto-complete="new-password"
                  v-model="user.newPassword"
                  placeholder="6 ~ 16位">
        </el-input>
      </el-form-item>
      <el-form-item label="学校/班级/部门">
        <el-input prefix-icon="el-icon-school" placeholder="选填"
                  v-model="user.section">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success"
                   :icon="saveType === 'post' ? 'el-icon-plus' : 'el-icon-check'"
                   @click="onSave(saveType)">
          <span>{{ saveType === 'post' ? '创建' : '保存' }}</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {Notice, toLoginPage, userInfo} from "@/script/util"
import {apiPath} from "@/script/env"

const bcrypt = require('bcryptjs')

export default {
  name: "UserEditor",
  props: {
    user: Object,
    saveType: String,
    dialogVisible: Boolean
  },
  data() {
    return {
      roles: [
        {value: 0, label: '用户'},
        {value: 1, label: '用户管理'},
        {value: 2, label: '题库管理'},
        {value: 3, label: 'ROOT'}
      ],
      rules: {
        name: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, max: 16, message: '长度在 2 ~ 16 个字符', trigger: 'blur'}
        ],
        userId: [
          {required: true, message: '请输入ID', trigger: 'blur'},
          {min: 4, max: 16, message: '长度在 4 ~ 16 个字符', trigger: 'blur'}
        ],
        email: [
          {type: 'email', message: '请输入邮箱', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 16, message: '长度在 6 ~ 16 位字符', trigger: 'blur'}
        ],
        newPassword: [
          {min: 6, max: 16, message: '长度在 6 ~ 16 位字符', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onSave(type) {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {
          if (type === 'put' && this.user.newPassword !== undefined) {
            this.user.password = bcrypt.hashSync(this.$md5(this.user.newPassword), 10)
          }
          let url = type === 'post' ? apiPath.user : apiPath.userManage
          this.$axios({
            url: url,
            method: type,
            headers: {
              'token': userInfo().token,
              'userId': userInfo().userId,
              'Content-Type': 'application/json'
            },
            data: JSON.stringify(this.user)
          }).then((res) => {
            this.$emit('refresh')
            this.$emit('update:dialogVisible', false)
            Notice.notify.success(this, {
              title: type === 'post' ? `用户【${this.user.userId}】已创建` : '已保存',
              message: `${res.status} ${res.statusText}`
            })
          }).catch((error) => {
            let res = error.response
            if (res.status === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: type === 'post' ? `用户【${this.user.userId}】创建失败` : '保存失败',
                message: `${res.status} ${res.statusText}`
              })
            }
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

</style>