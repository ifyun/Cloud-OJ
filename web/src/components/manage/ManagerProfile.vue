<template>
  <el-card>
    <span>管理员信息</span>
    <el-divider></el-divider>
    <el-form label-width="140px" style="width: 500px">
      <el-form-item label="管理员ID：">
        <span>{{ manager.userId }}</span>
      </el-form-item>
      <el-form-item label="用户名：">
        <span>{{ manager.name }}</span>
        <span v-if="manager.section !== undefined">
          @{{ manager.section }}
        </span>
      </el-form-item>
      <el-form-item label="角色/权限：">
        <template>
          <el-tag effect="dark"
                  :type="roleTypes[manager['roleId']]">
            <span>{{ roleNames[manager['roleId']] }}</span>
          </el-tag>
        </template>
      </el-form-item>
      <el-form-item label="Token 过期时间：">
        <span>{{ manager.expire }}</span>
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