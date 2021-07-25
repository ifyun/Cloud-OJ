<template>
  <div v-if="userInfo != null || userId != null">
    <el-container class="container">
      <error v-if="error.code != null" :error="error"/>
      <el-tabs class="borderless" v-else type="border-card" style="width: 100%">
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
      <bottom-area class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import BottomArea from "@/components/common/BottomArea"
import UserProfile from "@/components/profile/UserProfile"
import Overview from "@/components/profile/Overview"
import HistoryList from "@/components/HistoryList"
import Error from "@/components/Error"
import {toLoginPage, userInfo} from "@/util"

export default {
  name: "Profile",
  beforeMount() {
    if (this.userInfo == null) {
      toLoginPage()
    }
    this.userId = this.$route.query.userId
  },
  components: {
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
      userId: ""
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