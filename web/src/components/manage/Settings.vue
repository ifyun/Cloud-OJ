<template>
  <div>
    <el-card v-loading="loading">
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
        <el-row type="flex" justify="space-between">
          <el-col :span="20">
            <h3>隐藏进行中的竞赛排行榜</h3>
            <span class="info">开启后，只有当竞赛/作业结束后可以查看排行榜（管理员不受此限制）</span>
          </el-col>
          <el-col :span="4">
            <el-switch class="switch" active-color="#67C23A" :disabled="loading"
                       v-model="settings.showRankingAfterEnded">
            </el-switch>
          </el-col>
        </el-row>
        <el-divider/>
        <el-row type="flex" justify="space-between">
          <el-col :span="20">
            <h3>显示未开始的竞赛</h3>
            <span class="info">开启后，未开始的竞赛/作业也会显示在列表中，但不可查看题目</span>
          </el-col>
          <el-col :span="4">
            <el-switch class="switch" active-color="#67C23A" :disabled="loading"
                       v-model="settings.showNotStartedContest">
            </el-switch>
          </el-col>
        </el-row>
        <el-divider/>
        <el-row>
          <el-col :span="10">
            <h3>网站设置</h3>
            <el-form label-width="80px" label-position="left" size="medium">
              <el-form-item label="网站名称">
                <el-input v-model="settings.siteName" placeholder="Cloud OJ"/>
              </el-form-item>
              <el-form-item label="备案号">
                <el-input v-model="settings.icp" placeholder="例如: 京ICP备00000000号"/>
              </el-form-item>
              <el-form-item label="备案链接">
                <el-input v-model="settings.icpUrl" placeholder="填写备案网站的链接"/>
              </el-form-item>
              <el-form-item label="网站图标">
                <el-upload class="logo-uploader" :show-file-list="false" :action="logo.uploadUrl"
                           :headers="logo.uploadHeaders" :before-upload="beforeUpload"
                           :on-success="uploadSuccess" :on-error="uploadFailed">
                  <img v-if="logo.url" :src="logo.url" class="logo-uploaded" alt="logo">
                  <i v-else class="el-icon-plus logo-uploader-icon"/>
                </el-upload>
                <el-button type="danger" plain size="mini" icon="el-icon-refresh-left"
                           @click="deleteLogo">
                  恢复默认图标
                </el-button>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
        <el-button style="margin-top: 35px" type="success" size="medium" icon="el-icon-check"
                   :disabled="loading" @click="saveSettings">
          保存设置
        </el-button>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import {Notice, toLoginPage, userInfo} from "@/util"
import {ApiPath, SettingsApi} from "@/service"
import axios from "axios"

const title = "系统设置"

export default {
  name: "Settings",
  beforeMount() {
    history.pushState(null, "", "?active=4")
    this.siteSetting.setTitle(title)
    this.getQueueInfo()
    this.getSettings()
    this.checkLogo()
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
        siteName: "",
        icp: "",
        icpUrl: ""
      },
      logo: {
        url: "/favicon.svg",
        uploadUrl: ApiPath.IMAGE + "/logo",
        uploadHeaders: {
          "token": userInfo().token,
          "userId": userInfo().userId
        }
      }
    }
  },
  methods: {
    getQueueInfo(refresh) {
      SettingsApi.getQueueInfo(userInfo()).then((data) => {
        this.queueInfo = data
        refresh === true && Notice.message.success(this, "队列信息已刷新")
      }).catch((error) => {
        if (error.code === 401) {
          toLoginPage(this)
        } else {
          Notice.notify.error(this, {
            title: "获取队列信息失败",
            message: `${error.code} ${error.msg}`
          })
        }
      })
    },
    getSettings() {
      SettingsApi.get().then((data) => {
        this.settings = data
      }).catch((error) => {
        Notice.notify.error(this, {
          title: "获取系统设置失败",
          message: `${error.code} ${error.msg}`
        })
      }).finally(() => {
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
            this.siteSetting.reload(title)
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "保存系统设置失败",
              message: `${error.code} ${error.msg}`
            })
          })
    },
    beforeUpload(file) {
      const isTypeOk = ["image/jpeg", "image/png"].indexOf(file.type) !== -1
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isTypeOk) {
        this.$message.error("图标只能是 JPG/PNG 格式!")
      }

      if (!isLt2M) {
        this.$message.error("图标大小不能超过 2MB!")
      }

      return isTypeOk && isLt2M
    },
    uploadSuccess() {
      this.checkLogo()
      Notice.message.success(this, "网站图标已更新")
    },
    uploadFailed(err) {
      Notice.notify.error(this, {
        title: "上传图标失败",
        message: `${err.status} ${err.error}`
      })
    },
    checkLogo() {
      this.logo.url = ""
      const url = `${ApiPath.IMAGE}/favicon.png`
      axios.head(url).then(() => {
        this.logo.url = url
        this.siteSetting.setFavicon(url)
      }).catch(() => {
        this.logo.url = "/favicon.png"
      })
    },
    deleteLogo() {
      SettingsApi.deleteLogo(userInfo()).then(() => {
        Notice.message.success(this, "网站图标已恢复默认")
        this.checkLogo()
      }).catch(() => {
        Notice.message.error(this, "恢复默认图标失败")
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

.info {
  color: #909399;
  font-size: 14px;
}

.switch {
  margin-top: 5px;
  margin-left: 50px;
}
</style>

<style>
.logo-uploader .el-upload {
  border: 1px dashed #e0e0e0;
  border-radius: 5px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.logo-uploader .el-upload:hover {
  border-color: #409EFF;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.logo-uploaded {
  width: 120px;
  height: 120px;
  padding: 5px;
  display: block;
  border-radius: 5px;
}
</style>