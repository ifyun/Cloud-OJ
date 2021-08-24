<template>
  <div style="height: calc(100% - 61px)">
    <div style="margin-top: 75px" v-if="error.code != null">
      <error :error="error"/>
      <bottom-area class="bottom"/>
    </div>
    <el-container v-else class="container">
      <el-aside class="aside">
        <el-menu class="side-nav" mode="vertical" :default-active="$route.path"
                 :collapse="collapse" @select="onSelect">
          <el-menu-item v-on:click="toggleCollapse">
            <i v-bind:class="collapse ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'">
            </i>
            <span>折叠</span>
          </el-menu-item>
          <el-menu-item index="/manage/problems" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-grid"></i>
            <span slot="title">题库管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/contest" :disabled="[2, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-s-flag"></i>
            <span slot="title">竞赛管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/user" :disabled="[1, 3].indexOf(userInfo['roleId']) === -1">
            <i class="el-icon-user-solid"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/manage/settings" :disabled="userInfo['roleId'] !== 3">
            <i class="el-icon-s-tools"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <div class="main">
          <router-view :key="$route.fullPath"/>
          <bottom-area style="margin-top: 50px"/>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import Error from "@/components/Error"
import BottomArea from "@/components/common/BottomArea"
import {toLoginPage, userInfo} from "@/util"

export default {
  name: "Manager",
  components: {
    Error,
    BottomArea
  },
  beforeMount() {
    if (this.userInfo == null) {
      toLoginPage()
    } else if (this.userInfo["roleId"] === 0) {
      this.error = {
        code: 403,
        msg: "无权限"
      }
    }
  },
  data() {
    return {
      userInfo: userInfo(),
      collapse: false,
      error: {
        code: null,
        msg: ""
      }
    }
  },
  methods: {
    toggleCollapse() {
      this.collapse = !this.collapse
    },
    /**
     * 左侧菜单选择事件
     * @param key 路由
     */
    onSelect(key) {
      this.$router.push(key).catch(() => {
      })
    }
  }
}
</script>

<style scoped>
.side-nav {
  border: none;
  background-color: inherit;
}

.side-nav:not(.el-menu--collapse) {
  width: 180px;
}

.aside {
  height: 100%;
  width: auto !important;
  box-shadow: 2px 0 4px 0 rgba(0, 0, 0, 0.08);
}

.container {
  margin-top: 0 !important;
  padding: 0;
  min-width: 1100px !important;
  max-width: 100% !important;
  height: 100%;
}

.main {
  margin: 0 auto;
}
</style>