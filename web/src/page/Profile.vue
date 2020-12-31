<template>
  <div v-if="userInfo != null || userId != null">
    <TopNavigation active=""/>
    <Error v-if="error.code != null" :error="error"/>
    <el-container v-else class="container">
      <el-card style="width: 100%">
        <el-row :gutter="10">
          <el-col :span="6">
            <UserProfile :user-id="userId" @error="onError"/>
          </el-col>
          <el-col :span="18">
            <Overview :user-id="userId" @error="onError"/>
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
  padding: 0 10px;
  flex-direction: column;
  align-items: center;
  max-width: 1200px;
}
</style>