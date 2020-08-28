<template>
  <el-aside style="margin-top: 20px">
    <el-menu class="side-nav" mode="vertical">
      <el-menu-item index="1">
        <i class="el-icon-s-order"></i>
        <span>题库管理</span>
      </el-menu-item>
      <el-menu-item index="2">
        <i class="el-icon-s-flag"></i>
        <span>竞赛/作业管理</span>
      </el-menu-item>
      <el-menu-item index="3">
        <i class="el-icon-user-solid"></i>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="4">
        <i class="el-icon-s-tools"></i>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script>
export default {
  name: "SideNavigation"
}
</script>

<style scoped>
.side-nav {
  width: 200px;
  height: 100%;
}
</style>