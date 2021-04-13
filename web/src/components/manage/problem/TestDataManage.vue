<template>
  <div>
    <el-alert v-if="sql === false" style="margin-bottom: 15px"
              :closable="false" type="warning" show-icon
              title="文件中的换行符应该为 LF，不可使用 CRLF">
    </el-alert>
    <el-upload ref="upload" :action="uploadUrl" :multiple="true" :auto-upload="false"
               :data="{'problemId': this.problemId}" :headers="headers"
               :on-success="uploadSuccess" :file-list="fileList" accept=".in,.out,.db">
      <el-button slot="trigger" size="small" type="primary"
                 icon="el-icon-folder-opened">选取文件
      </el-button>
      <el-button style="margin-left: 10px;" size="small" icon="el-icon-upload"
                 type="success" @click="uploadClick">上传到服务器
      </el-button>
      <el-button style="float: right" icon="el-icon-refresh" size="small" @click="getTestData">刷新</el-button>
    </el-upload>
    <el-alert v-if="sql === true" style="margin: 15px 0" :closable="false" type="info" show-icon
              title="此题为 SQL，请上传 SQLite 数据库文件（*.db）"
              description="仅支持一个文件，上传多个只有第一个有效">
    </el-alert>
    <el-alert v-else style="margin: 15px 0" :closable="false" type="info" show-icon
              title="若需要修改请先下载，编辑后再上传"
              description="每组数据的名称应该相同（例如1-1.in对应1-1.out），删除文件时，请确保成对删除">
    </el-alert>
    <el-table size="small" :data="testData" stripe max-height="600" v-loading="loading"
              :default-sort="{prop: 'fileName', order: 'ascending'}">
      <el-table-column label="文件名" prop="fileName">
      </el-table-column>
      <el-table-column label="文件大小" width="200px" align="right">
        <template slot-scope="scope">
          <span>{{ scope.row.size }} 字节</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="success" icon="el-icon-download"
                     @click="download(scope.row['fileName'])">下载
          </el-button>
          <el-popconfirm style="margin-left: 10px" title="确定要删除吗？" icon="el-icon-info"
                         confirm-button-type="danger" confirm-button-text="删除"
                         cancel-button-type="plain" cancel-button-text="取消"
                         @confirm="deleteFile(scope.row['fileName'])">
            <el-button type="danger" size="mini" icon="el-icon-delete" slot="reference">
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {Notice, toLoginPage, userInfo} from "@/util"
import {ApiPath, TestDataApi} from "@/service"

export default {
  name: "TestDataManage",
  props: {
    problemId: Number,
    sql: Boolean,
    dialogVisible: Boolean
  },
  watch: {
    problemId: {
      immediate: true,
      handler() {
        // 对话框显示的时候才发送请求
        if (this.dialogVisible) {
          this.testData = []
          this.fileList = []
          this.getTestData()
        }
      }
    }
  },
  data() {
    return {
      loading: Boolean,
      testData: [],
      fileList: [],
      uploadUrl: ApiPath.TEST_DATA,
      headers: {
        "token": userInfo().token,
        "userId": userInfo().userId
      }
    }
  },
  methods: {
    getTestData() {
      this.loading = true
      TestDataApi.get(this.problemId, userInfo())
          .then((data) => {
            this.testData = data
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage(this)
            } else {
              Notice.notify.error(this, {
                title: "获取测试数据失败",
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.loading = false
          })
    },
    download(fileName) {
      let link = `${ApiPath.TEST_DATA}/download/${this.problemId}`
          + `/${fileName}?userId=${userInfo().userId}&token=${userInfo().token}`
      window.open(link, "_blank")
    },
    uploadClick() {
      this.$refs.upload.submit();
    },
    uploadSuccess(_, file) {
      this.getTestData()
      Notice.notify.success(this, {
        title: `${file.name} 上传成功`
      })
    },
    deleteFile(fileName) {
      TestDataApi.delete(this.problemId, fileName, userInfo())
          .then(() => {
            this.getTestData()
            Notice.notify.warning(this, {
              title: `${fileName} 已删除`
            })
          })
          .catch((error) => {
            let res = error.response
            if (res.status === 401) {
              toLoginPage(this)
            } else {
              Notice.notify.error(this, {
                title: `${fileName} 删除失败`,
                message: `${res.status} ${res.statusText}`
              })
            }
          })
    }
  }
}
</script>

<style scoped>

</style>