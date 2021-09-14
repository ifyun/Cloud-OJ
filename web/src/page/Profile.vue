<template>
  <div id="root">
      <error-info v-if="error.code != null" :error="error"/>
      <el-tabs v-else class="borderless" type="border-card" style="width: 100%">
        <el-tab-pane label="概览">
          <el-row :gutter="5">
            <el-col :span="6">
              <user-profile :user-id="userId" @error="onError"/>
            </el-col>
            <el-col :span="18">
              <overview style="margin-left: 5px" :user-id="userId" @error="onError"/>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane v-if="userId == null" label="提交记录">
          <history-list/>
        </el-tab-pane>
      </el-tabs>
  </div>
</template>

<script>
import UserProfile from "@/components/profile/UserProfile"
import Overview from "@/components/profile/Overview"
import HistoryList from "@/components/HistoryList"
import ErrorInfo from "@/components/ErrorInfo"
import {userInfo} from "@/util"

export default {
  name: "Profile",
  components: {
    UserProfile,
    Overview,
    HistoryList,
    ErrorInfo
  },
  data() {
    return {
      error: {
        code: null,
        msg: ""
      },
      userInfo: userInfo(),
      userId: null
    }
  },
  created() {
    this.userId = this.$route.query.userId
    if (typeof this.userId === "undefined" && this.userInfo == null) {
      this.error = {
        code: 401,
        msg: "请登录"
      }
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
#root {
  margin: 0 auto;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
  width: 1100px;
}
</style>