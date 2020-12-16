<template>
  <div>
    <el-card>
      <h3>系统信息</h3>
      <el-card style="margin-bottom: 35px">
        <div><b>RabbitMQ 消息队列：</b>
          <el-button style="float: right" icon="el-icon-refresh" size="mini"
                     @click="getQueueInfo(true)">
            刷新
          </el-button>
        </div>
        <div style="margin-top: 20px">
          <el-tag type="warning" effect="dark">
            {{ queueInfo.inCommitQueue }}&nbsp;个提交等待写入
          </el-tag>
        </div>
        <div style="margin-top: 10px">
          <el-tag effect="dark" type="success">
            {{ queueInfo.inJudgeQueue }}&nbsp;个提交等待判题
          </el-tag>
        </div>
      </el-card>
      <h3>系统设置</h3>
      <el-card style="margin-bottom: 35px">
        <el-row :gutter="20">
          <el-col :span="16">
            <h3>隐藏进行中的竞赛排行榜</h3>
            <el-alert show-icon type="info" :closable="false"
                      title="开启后，只有当竞赛/作业结束后可以查看排行榜（管理员不受此限制）">
            </el-alert>
          </el-col>
          <el-col :span="8">
            <el-switch class="switch" active-color="#67C23A"
                       v-model="settings.showRankingAfterEnded">
            </el-switch>
          </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row :gutter="20">
          <el-col :span="16">
            <h3>显示未开始的竞赛</h3>
            <el-alert show-icon type="info" :closable="false"
                      title="开启后，未开始的竞赛/作业也会显示在列表中，但不可查看题目">
            </el-alert>
          </el-col>
          <el-col :span="8">
            <el-switch class="switch" active-color="#67C23A"
                       v-model="settings.showNotStartedContest">
            </el-switch>
          </el-col>
        </el-row>
        <el-button style="margin-top: 35px" type="primary" size="medium" icon="el-icon-check"
                   @click="saveSettings">
          保存设置
        </el-button>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import {Notice, userInfo} from "@/script/util"
import {apiPath} from "@/script/env"

export default {
  name: "Settings",
  beforeMount() {
    document.title = "系统设置 - Cloud OJ"
    this.getQueueInfo()
    this.getSettings()
  },
  data() {
    return {
      queueInfo: {
        inCommitQueue: 0,
        inJudgeQueue: 0
      },
      settings: {
        showRankingAfterEnded: false,
        showNotStartedContest: false,
      }
    }
  },
  methods: {
    getQueueInfo(refresh) {
      this.$axios.get(apiPath.queueInfo, {
        headers: {
          "token": userInfo().token,
          "userId": userInfo().userId
        }
      }).then((res) => {
        this.queueInfo = res.data
        if (refresh === true) {
          Notice.message.success(this, "队列信息已刷新")
        }
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: "获取队列信息失败",
          message: `${res.status} ${res.data.msg === undefined ? res.statusText : res.data.msg}`
        })
      })
    },
    getSettings() {
      this.$axios.get(apiPath.settings, {
        headers: {
          "token": userInfo().token,
          "userId": userInfo().userId
        }
      }).then((res) => {
        this.settings = res.data
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: "获取系统设置失败",
          message: `${res.status} ${res.data.msg === undefined ? res.statusText : res.data.msg}`
        })
      })
    },
    saveSettings() {
      this.$axios({
        url: apiPath.settings,
        method: "put",
        headers: {
          "token": userInfo().token,
          "userId": userInfo().userId,
          "Content-Type": "application/json"
        },
        data: JSON.stringify(this.settings)
      }).then((res) => {
        Notice.notify.success(this, {
          title: "已保存",
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: "保存系统设置失败",
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