<template>
  <div>
    <div class="profile-div" v-if="profileEditor.display">
      <span style="font-size: 13px">上传新头像:</span>
      <el-upload class="avatar-uploader" :show-file-list="false" :action="uploadUrl"
                 :headers="uploadHeaders" :before-upload="beforeUpload"
                 :on-success="uploadSuccess" :on-error="uploadFailed">
        <el-avatar :src="avatarUrl" class="avatar-uploaded" alt="upload avatar">
          <img src="@/assets/icons/no_avatar.png" alt="user">
        </el-avatar>
      </el-upload>
      <el-divider></el-divider>
      <el-form ref="profileForm" size="small" :model="userProfile" :rules="rules">
        <el-form-item prop="name">
          <el-input prefix-icon="el-icon-user" v-model="userProfile.name"></el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input prefix-icon="el-icon-message" v-model="userProfile.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input prefix-icon="el-icon-school" v-model="userProfile.section"></el-input>
        </el-form-item>
        <el-collapse>
          <el-collapse-item title="修改密码">
            <el-form-item prop="password">
              <el-input type="password" prefix-icon="el-icon-lock" v-model="userProfile.password"
                        auto-complete="new-password" onkeyup="this.value=this.value.replace(/\s+/g,'')"
                        placeholder="输入新密码">
              </el-input>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
        <el-form-item style="margin-top: 15px">
          <el-button size="small" type="success" icon="el-icon-check"
                     :loading="profileEditor.loading" @click="saveProfile">保存
          </el-button>
          <el-button size="small" icon="el-icon-close" :disabled="profileEditor.loading"
                     @click="cancelEdit">取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-else class="profile-div">
      <el-avatar class="avatar" :src="avatarUrl" :size="180" alt="avatar">
        <img src="@/assets/icons/no_avatar.png" alt="user">
      </el-avatar>
      <span class="name">{{ userProfile.name }}</span>
      <span class="user-id">{{ userProfile.userId }}</span>
      <el-divider/>
      <span>
        <i class="el-icon-message el-icon--left"/>
        <span v-if="userProfile.email === undefined || userProfile.email === ''"
              style="color: #606266">此用户未留下邮箱</span>
        <span v-else>{{ userProfile.email }}</span>
      </span>
      <span style="margin-top: 5px">
        <i class="el-icon-office-building el-icon--left"/>
        <span v-if="userProfile.section === undefined || userProfile.section === ''"
              style="color: #606266">未填写</span>
        <span v-else>{{ userProfile.section }}</span>
      </span>
      <el-button v-if="userId == null" size="small" style="margin-top: 15px"
                 icon="el-icon-edit-outline" @click="editClick">
        修改个人信息
      </el-button>
    </div>
  </div>
</template>

<script>
import {Notice, saveToken, toLoginPage, userInfo} from "@/util"
import {ApiPath, UserApi} from "@/service"
import md5 from "crypto-js"

const bcrypt = require("bcryptjs")

export default {
  name: "UserProfile",
  beforeMount() {
    if (this.userId != null) {
      this.getProfile()
      this.avatarUrl = `${ApiPath.AVATAR}/${this.userId}.png`
    } else {
      this.resetProfileData()
      this.$siteSetting.setTitle("个人中心")
      this.avatarUrl = `${ApiPath.AVATAR}/${this.userInfo.userId}.png`
    }
  },
  props: ["userId"],
  data() {
    return {
      uploadUrl: ApiPath.AVATAR,
      uploadHeaders: {
        "token": userInfo().token,
        "userId": userInfo().userId
      },
      avatarUrl: "",
      userInfo: userInfo(),
      userProfile: {
        userId: "",
        name: "",
        email: "",
        section: "",
        password: ""
      },
      rules: {
        name: [
          {required: true, message: "请输入用户名", trigger: "blur"},
          {min: 2, max: 16, message: "长度在 2 ~ 16 个字符", trigger: "blur"}
        ],
        email: [
          {type: "email", message: "请输入邮箱", trigger: "blur"}
        ],
        password: [
          {min: 6, max: 16, message: "长度在 6 ~ 16 个字符", trigger: "blur"}
        ],
      },
      profileEditor: {
        display: false,
        loading: false
      }
    }
  },
  methods: {
    getProfile() {
      UserApi.getProfile(this.userId)
          .then((data) => {
            this.$siteSetting.setTitle(`${data.name}`)
            this.userProfile = data
          })
          .catch((error) => {
            this.$emit("error", error)
          })
    },
    beforeUpload(file) {
      const isTypeOk = ["image/jpeg", "image/png"].indexOf(file.type) !== -1
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isTypeOk) {
        this.$message.error("上传头像图片只能是 JPG/PNG 格式!")
      }

      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!")
      }

      return isTypeOk && isLt2M
    },
    uploadSuccess() {
      this.avatarUrl = `${ApiPath.AVATAR}/${this.userProfile.userId}.png?t=${Math.random()}`
      Notice.message.success(this, "头像已更新")
    },
    uploadFailed(err) {
      Notice.notify.error(this, {
        title: "上传头像失败",
        message: `${err.status} ${err.error}`
      })
    },
    editClick() {
      this.profileEditor.display = true
    },
    saveProfile() {
      this.$refs["profileForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        this.profileEditor.loading = true
        let password = this.userProfile.password
        let passwordChanged = false

        if (typeof password === "undefined" || password === "") {
          delete this.userProfile.password
        } else {
          this.userProfile.password = bcrypt.hashSync(md5(password).toString(), 10)
          passwordChanged = true
        }

        UserApi.updateProfile(this.userProfile, userInfo())
            .then(() => {
              this.profileEditor.display = false
              Notice.notify.success(this, {
                title: "已保存"
              })
              passwordChanged && toLoginPage()
              this.updateLocalUserInfo()
            })
            .catch((error) => {
              Notice.notify.error(this, {
                title: "保存失败",
                message: `${error.code} ${error.msg}`
              })
            })
            .finally(() => {
              this.userProfile.password = ""
              this.profileEditor.loading = false
            })
      })
    },
    cancelEdit() {
      this.profileEditor.display = false
      this.resetProfileData()
    },
    resetProfileData() {
      this.userProfile = {
        userId: userInfo().userId,
        name: userInfo().name,
        email: userInfo().email,
        section: userInfo().section
      }
    },
    updateLocalUserInfo() {
      this.userInfo.name = this.userProfile.name
      this.userInfo.email = this.userProfile.email
      this.userInfo.section = this.userProfile.section
      saveToken(JSON.stringify(this.userInfo))
    }
  }
}
</script>

<style scoped lang="scss">
.profile-div {
  display: flex;
  height: 800px;
  border-right: 1px solid #e0e0e0;
  flex-direction: column;
  margin-top: 5px;
  padding-right: 20px;
  padding-left: 5px;

  .avatar {
    border: 2px solid #F5F7FA;
    margin-bottom: 15px;
    align-self: center;
  }

  .name {
    font-size: 13pt;
    font-weight: bold;
    color: #303133;
    align-self: center;
  }

  .user-id {
    font-weight: lighter;
    color: #606266;
    align-self: center;
  }
}
</style>

<style lang="scss">
.avatar-uploader {
  display: flex;

  .el-upload {
    margin: 15px auto 0;
    border: 2px dashed #e0e0e0;
    border-radius: 90px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .el-upload:hover {
    border-color: #409EFF;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 170px;
    height: 170px;
    line-height: 170px;
    text-align: center;
  }
}

.avatar-uploaded {
  width: 170px;
  height: 170px;
  display: block;
  border-radius: 85px;
}
</style>