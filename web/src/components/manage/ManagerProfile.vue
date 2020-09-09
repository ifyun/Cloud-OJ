<template>
  <el-card>
    <span>管理员信息</span>
    <el-divider></el-divider>
    <el-form label-width="100px" style="width: 500px">
      <el-form-item label="管理员ID">
        <el-input v-model="manager.userId" disabled>
        </el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="manager.name" disabled>
          <template slot="append">{{ manager.section }}</template>
        </el-input>
      </el-form-item>
      <el-form-item label="角色/权限">
        <template>
          <el-tag effect="dark"
                  :type="roleTypes[manager['roleId']]">
            <span>{{ roleNames[manager['roleId']] }}</span>
          </el-tag>
        </template>
      </el-form-item>
      <el-form-item label="登录失效于">
        <el-input v-model="manager.expire" disabled>
        </el-input>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import {userInfo} from "@/js/util"

export default {
  name: "ManagerProfile",
  data() {
    return {
      manager: userInfo(),
      roleNames: [
        '用户',
        '用户管理',
        '题库管理',
        'ROOT'
      ],
      roleTypes: [
        'info',
        'success',
        'primary',
        'danger'
      ]
    }
  }
}
</script>

<style scoped>

</style>