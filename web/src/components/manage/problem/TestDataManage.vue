<template>
  <div>
    <el-upload ref="upload"
               :action="uploadUrl"
               :auto-upload="false"
               :data="{'problemId': problemId}"
               :headers="uploadHeaders"
               :on-preview="onPreview"
               :on-success="onUploadSuccess"
               :file-list="fileList"
               accept=".in,.out">
      <el-button slot="trigger" size="small" type="primary"
                 icon="el-icon-folder-opened">选取文件
      </el-button>
      <el-button style="margin-left: 10px;" size="small" icon="el-icon-upload"
                 type="success" @click="onUpload">上传到服务器
      </el-button>
    </el-upload>
    <el-divider></el-divider>
    <el-alert style="margin-bottom: 15px"
              :closable="false" type="info" show-icon
              title="点击文件名可查看内容，若需要修改请先下载，编辑完成再上传"
              description="每组数据的名称应该相同（例如1-1.in对应1-1.out），删除文件时，请确保成对删除">
    </el-alert>
    <el-table :data="testData" border max-height="600" v-loading="loading"
              :default-sort="{prop: 'fileName', order: 'ascending'}">
      <el-table-column label="文件名">
        <template slot-scope="scope">
          <el-popover trigger="click" placement="right-end">
            <pre class="test-data">{{ scope.row.content }}</pre>
            <el-button slot="reference" size="mini">{{ scope.row.fileName }}
            </el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="文件大小" width="200px" align="right">
        <template slot-scope="scope">
          <span>{{ scope.row.size }} 字节</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="success" icon="el-icon-download"
                     @click="download(scope.row.fileName)">下载
          </el-button>
          <el-popconfirm style="margin-left: 10px"
                         title="确定要删除吗？"
                         icon="el-icon-info"
                         confirm-button-type="danger"
                         confirm-button-text="删除"
                         cancel-button-text="取消"
                         @onConfirm="deleteFile(scope.row.fileName)">
            <el-button type="danger" size="mini"
                       icon="el-icon-delete"
                       slot="reference">
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {apiPath, handle401, userInfo} from "@/js/util";

export default {
  name: "TestDataManage",
  props: {
    problemId: Number
  },
  watch: {
    problemId: {
      immediate: true,
      handler() {
        this.testData = []
        this.fileList = []
        this.getTestData()
      }
    },
    fileList: {
      immediate: true,
      handler() {
        console.log(this.fileList)
      }
    }
  },
  data() {
    return {
      loading: Boolean,
      testData: [],
      fileList: [],
      uploadUrl: `${apiPath.testDataManage}?userId=${userInfo().userId}`,
      uploadHeaders: {
        'token': userInfo().token
      }
    }
  },
  computed: {
    disableUpload: vm => vm.fileList.length === 0
  },
  methods: {
    getTestData() {
      this.loading = true
      this.$axios({
        url: `${apiPath.testDataManage}/${this.problemId}?userId=${userInfo().userId}`,
        method: 'get',
        headers: {
          'token': userInfo().token
        }
      }).then((res) => {
        this.testData = res.data
        this.loading = false
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: '获取数据失败',
            message: `${res.status} ${res.statusText}`
          })
        }
        this.loading = false
      })
    },
    download(fileName) {
      // NOTE Dev link
      let link = `http://cloudoj.204.group${apiPath.testDataManage}/file/${this.problemId}`
          + `/${fileName}?userId=${userInfo().userId}&token=${userInfo().token}`
      window.open(link, '_blank')
    },
    onPreview(file) {
      console.log(file)
    },
    onUpload() {
      this.$refs.upload.submit();
    },
    onUploadSuccess(res, file) {
      this.getTestData()
      this.$notify({
        type: 'success',
        title: `${file.name}上传成功`,
        message: ``
      })
    },
    deleteFile(fileName) {
      this.$axios({
        url: `${apiPath.testDataManage}/${this.problemId}`
            + `?name=${fileName}&userId=${userInfo().userId}`,
        method: 'delete',
        headers: {
          'token': userInfo().token
        }
      }).then((res) => {
        this.getTestData()
        this.$notify({
          type: 'info',
          title: `${fileName}已删除`,
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: `${fileName}删除失败`,
            message: `${res.status} ${res.statusText}`
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.test-data {
  max-width: 500px;
  max-height: 500px;
  white-space: pre-wrap;
}
</style>