<template>
  <div>
    <el-card>
      <span>系统设置</span>
      <el-divider></el-divider>
      <div>
        <el-row :gutter="20">
          <el-col :span="12">
            <h3>隐藏进行中的竞赛排行榜</h3>
            <el-alert show-icon type="info" :closable="false"
                      title="开启后，只有当竞赛/作业结束后可以查看排行榜（管理员不受此限制）">
            </el-alert>
          </el-col>
          <el-col :span="12">
            <el-switch class="switch" v-model="settings.showRankingAfterEnded" active-color="#67C23A">
            </el-switch>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <h3>显示未开始的竞赛</h3>
            <el-alert show-icon type="info" :closable="false"
                      title="开启后，未开始的竞赛/作业也会显示在列表中，但不可查看题目">
            </el-alert>
          </el-col>
          <el-col :span="12">
            <el-switch class="switch" v-model="settings.showNotStartedContest" active-color="#67C23A">
            </el-switch>
          </el-col>
        </el-row>
        <el-button style="margin-top: 45px" type="success" plain
                   @click="saveSettings">
          保存设置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import {apiPath, userInfo} from "@/script/util";

export default {
  name: "Settings",
  beforeMount() {
    document.title = `系统设置 · Cloud OJ`
    this.getSettings()
  },
  data() {
    return {
      settings: {
        showRankingAfterEnded: false,
        showNotStartedContest: false,
      }
    }
  },
  methods: {
    getSettings() {
      this.$axios.get(apiPath.settings, {
        headers: {
          'token': userInfo().token
        },
        params: {
          'userId': userInfo().userId
        }
      }).then((res) => {
        this.settings = res.data
      }).catch((error) => {
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: '获取系统设置失败',
          message: `${res.status} ${res.data.msg === undefined ? res.statusText : res.data.msg}`
        })
      })
    },
    saveSettings() {
      this.$axios({
        url: apiPath.settings,
        method: 'put',
        headers: {
          'token': userInfo().token,
          'Content-Type': 'application/json'
        },
        params: {
          'userId': userInfo().userId
        },
        data: JSON.stringify(this.settings)
      }).then((res) => {
        this.$notify.success({
          offset: 50,
          title: '已保存',
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: '保存系统设置失败',
          message: `${res.status} ${res.data.msg === undefined ? res.statusText : res.data.msg}`
        })
      })
    }
  }
}
</script>

<style scoped>
h3 {
  margin-top: 0;
  color: #606266;
}

.switch {
  margin-top: 5px;
  margin-left: 50px;
}
</style>