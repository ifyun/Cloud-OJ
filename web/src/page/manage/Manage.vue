<template>
  <div style="height: 100%">
    <TopNavigation active="3"/>
    <el-container class="container">
      <el-aside class="aside">
        <el-menu class="side-nav" mode="vertical"
                 :collapse="collapse" :default-active="active" @select="onSelect">
          <el-menu-item v-on:click="toggleCollapse">
            <i v-bind:class="collapse? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'">
            </i>
            <span>折叠</span>
          </el-menu-item>
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
      <el-main>
        <component :is="currentView"></component>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ProblemManage from "@/components/manage/ProblemManage"
import CompetitionManage from "@/components/manage/CompetitionManage";

let page = new Map([
  ['1', 'ProblemManage'],
  ['2', 'CompetitionManage']
])


export default {
  name: "Manager",
  mounted() {

  },
  components: {
    TopNavigation,
    ProblemManage,
    CompetitionManage
  },
  data() {
    return {
      collapse: true,
      active: '1',
      currentView: page.get('1')
    }
  },
  methods: {
    toggleCollapse() {
      this.collapse = !this.collapse
    },
    onSelect(key) {
      if (key != null) {
        this.currentView = page.get(key)
      }
    }
  }
}
</script>

<style scoped>
.side-nav {
  height: 100%;
}

.side-nav:not(.el-menu--collapse) {
  width: 180px;
}

.aside {
  margin-top: 1px;
  height: 100%;
  width: auto !important;
}

.container {
  padding: 0 20px;
  height: calc(100% - 61px);
}
</style>