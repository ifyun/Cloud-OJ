<template>
  <div v-if="userInfo != null || userId != null">
    <TopNavigation active=""/>
    <el-container class="container">
      <Error v-if="error.code != null" :error="error"/>
      <el-tabs v-else type="border-card" style="width: 100%">
        <el-tab-pane label="概览">
          <el-row :gutter="5">
            <el-col :span="6">
              <UserProfile :user-id="userId" @error="onError"/>
            </el-col>
            <el-col :span="18">
              <Overview :user-id="userId" @error="onError"/>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane v-if="userId == null" label="提交记录">
          <HistoryList/>
        </el-tab-pane>
      </el-tabs>
      <BottomArea class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import UserProfile from "@/components/profile/UserProfile"
import Overview from "@/components/profile/Overview"
import HistoryList from "@/components/HistoryList"
import Error from "@/components/Error"
import {searchParams, toLoginPage, userInfo} from "@/util"

export default {
  name: "Profile",
  beforeMount() {
    if (this.userInfo == null) {
      toLoginPage()
    }
  },
  components: {
    TopNavigation,
    BottomArea,
    UserProfile,
    Overview,
    HistoryList,
    Error
  },
  data() {
    return {
      error: {
        code: null,
        msg: ""
      },
      userInfo: userInfo(),
      userId: searchParams()["userId"]
    }
  },
  methods: {
    onError(data) {
      this.error = data
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>