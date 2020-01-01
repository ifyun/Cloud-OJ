<template>
  <div>
    <div class="profile-div" v-if="editable">
      <span>上传新头像:</span>
      <el-upload class="avatar-uploader"
                 :show-file-list="false"
                 :action="uploadPath"
                 :headers="uploadHeaders"
                 :data="{'userId': userInfo.userId}"
                 :before-upload="beforeUpload"
                 :on-success="checkAvatar">
        <img v-if="avatarUrl" :src="avatarUrl"
             class="avatar-uploaded" alt="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
      <el-divider></el-divider>
      <el-form>
        <el-form-item>
          <el-input prefix-icon="el-icon-user" v-model="userProfile.name"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input prefix-icon="el-icon-message" v-model="userProfile.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input prefix-icon="el-icon-school" v-model="userProfile.section"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="success" @click="save">保存</el-button>
          <el-button size="small" @click="editable = false">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="profile-div" v-else>
      <img class="avatar" :src="avatarUrl" onerror="this.src='/icons/no_avatar.svg'"
           alt="avatar">
      <span style="font-size: 14pt;font-weight: bold;">{{ userInfo.name }}</span>
      <span style="font-weight: lighter">{{ userInfo.userId }}</span>
      <span v-if="userInfo.email !== undefined"
            style="margin-top: 15px;">
        <i class="el-icon-message"></i>
        <span>&nbsp;{{ userInfo.email }}</span>
      </span>
      <span v-if="userInfo.section !== undefined" style="margin-top: 5px;">
        <i class="el-icon-office-building"></i>
        <span>&nbsp;{{ userInfo.section }}</span>
      </span>
      <el-button size="small" style="margin-top: 15px"
                 @click="onEdit">
        <span>修改个人信息</span>
      </el-button>
    </div>
  </div>
</template>

<script>
import {apiPath, userInfo} from "@/js/util";

export default {
  name: "UserProfile",
  mounted() {
    this.checkAvatar()
  },
  data() {
    return {
      uploadPath: apiPath.avatar,
      uploadHeaders: {
        'token': userInfo().token
      },
      avatarUrl: '',
      userInfo: userInfo(),
      userProfile: {
        name: '',
        email: '',
        section: ''
      },
      editable: false
    }
  },
  methods: {
    checkAvatar() {
      this.avatarUrl = ''
      const url = `${apiPath.avatar}/${userInfo().userId}.png`
      this.$axios.head(url).then(() => {
        this.avatarUrl = url
      }).catch(() => {

      })
    },
    beforeUpload(file) {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPGorPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!');
      }

      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }

      return isJPGorPNG && isLt2M;
    },
    onEdit() {
      this.userProfile = {
        name: userInfo().name,
        email: userInfo().email,
        section: userInfo().section
      }
      this.editable = true
    },
    save() {
      // TODO 保存修改
      alert('还没有实现!')
    }
  }
}
</script>

<style scoped>
.profile-div {
  display: flex;
  height: 900px;
  border-right: 1px solid #e0e0e0;
  flex-direction: column;
  padding-left: 5px;
  padding-right: 15px;
}

.avatar {
  width: 80%;
  height: auto;
  display: block;
  border-radius: 200px;
  border: 3px solid #e0e0e0;
  margin: 0 auto 15px;
}
</style>

<style>
.avatar-uploader .el-upload {
  margin-top: 15px;
  border: 2px dashed #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar-uploaded {
  width: 178px;
  height: 178px;
  display: block;
  border-radius: 6px;
}
</style>