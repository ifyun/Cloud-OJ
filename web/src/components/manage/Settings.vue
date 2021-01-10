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
            <el-switch class="switch" active-color="#67C23A" :disabled="loading"
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
            <el-switch class="switch" active-color="#67C23A" :disabled="loading"
                       v-model="settings.showNotStartedContest">
            </el-switch>
          </el-col>
        </el-row>
        <el-button style="margin-top: 35px" type="primary" size="medium" icon="el-icon-check"
                   :disabled="loading" @click="saveSettings">
          保存设置
        </el-button>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import {Notice, userInfo} from "@/util"
import {SettingsApi} from "@/service"

export default {
  name: "Settings",
  beforeMount() {
    document.title = "系统设置 - Cloud OJ"
    this.getQueueInfo()
    this.getSettings()
  },
  data() {
    return {
      loading: true,
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
      SettingsApi.getQueueInfo(userInfo())
          .then((data) => {
            this.queueInfo = data
            refresh === true && Notice.message.success(this, "队列信息已刷新")
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "获取队列信息失败",
              message: `${error.code} ${error.msg}`
            })
          })
    },
    getSettings() {
      SettingsApi.get(userInfo())
          .then((data) => {
            this.settings = data
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "获取系统设置失败",
              message: `${error.code} ${error.msg}`
            })
          })
          .finally(() => {
            this.loading = false
          })
    },
    saveSettings() {
      SettingsApi.update(this.settings, userInfo())
          .then((res) => {
            Notice.notify.success(this, {
              title: "已保存",
              message: `${res.status} ${res.statusText}`
            })
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "保存系统设置失败",
              message: `${error.code} ${error.msg}`
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