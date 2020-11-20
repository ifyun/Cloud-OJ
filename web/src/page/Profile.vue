<template>
  <div v-if="display">
    <TopNavigation active=""/>
    <el-container class="container">
      <el-card style="width: 100%">
        <el-row :gutter="10">
          <el-col :span="6">
            <UserProfile/>
          </el-col>
          <el-col :span="18">
            <Overview/>
          </el-col>
        </el-row>
      </el-card>
      <BottomArea class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import UserProfile from "@/components/profile/UserProfile"
import Overview from "@/components/profile/Overview"
import {toLoginPage, userInfo} from "@/script/util"

export default {
  name: "Profile",
  beforeMount() {
    if (!this.display) {
      toLoginPage()
    }
    document.title = "个人中心 · Cloud OJ"
  },
  components: {
    TopNavigation,
    BottomArea,
    UserProfile,
    Overview
  },
  data() {
    return {
      display: userInfo() != null
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 10px;
  flex-direction: column;
  align-items: center;
  max-width: 1200px;
}
</style>