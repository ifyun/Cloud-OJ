<template>
  <el-card>
    <el-form :inline="true">
      <el-form-item>
        <el-button size="medium" icon="el-icon-circle-plus" type="primary"
                   @click="onAddClick">
          创建新用户
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" icon="el-icon-refresh" @click="getUsers(true)">
          刷新
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="users.data" border stripe v-loading="loading">
      <el-table-column label="ID" prop="userId" width="150px">
      </el-table-column>
      <el-table-column label="用户名" prop="name">
        <template slot-scope="scope">
          <b>{{ scope.row.name }}</b>
        </template>
      </el-table-column>
      <el-table-column label="角色/权限" width="200px" align="center">
        <template slot-scope="scope">
          <el-tag effect="plain" style="width: 90px" size="small"
                  :type="roleTypes[scope.row['roleId']]">
            <span>{{ roleNames[scope.row['roleId']] }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-button-group>
            <el-button size="mini" icon="el-icon-edit"
                       @click="onEditClick(scope.row)">编辑
            </el-button>
            <el-button size="mini" icon="el-icon-delete" type="danger"
                       :disabled="scope.row.userId === 'root'"
                       @click="onDeleteClick(scope.row)">
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="220px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.createAt }}</i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, prev, pager, next"
                   :page-size.sync="pageSize"
                   :total="users.count"
                   :current-page.sync="currentPage"
                   @size-change="getUsers"
                   @current-change="getUsers">
    </el-pagination>
    <!-- Editor Dialog -->
    <el-dialog :title="editorTitle"
               :visible.sync="editorDialogVisible">
      <UserEditor :user="selectedUser"
                  :save-type="saveType"
                  :dialog-visible.sync="editorDialogVisible"
                  @refresh="getUsers"/>
    </el-dialog>
    <!-- Delete Dialog -->
    <el-dialog title="删除提示"
               :visible.sync="deleteDialogVisible">
      <el-alert type="warning" show-icon
                :title="`你正在删除用户：${selectedUser.userId}`"
                description="如果该用户已有做题记录，只能通过操作数据库删除"
                :closable="false">
      </el-alert>
      <el-form ref="deleteForm"
               :model="deleteForm"
               :rules="deleteRules"
               @submit.native.prevent>
        <el-form-item label="输入用户名确认删除" prop="checkUserId">
          <el-input v-model="deleteForm.checkUserId" :placeholder="selectedUser.userId">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" @click="onDelete">删除用户</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import {copyObject, Notice, toLoginPage, userInfo} from "@/script/util"
import {apiPath} from "@/script/env"
import UserEditor from "@/components/manage/user/UserEditor"

export default {
  name: "UserManage",
  components: {UserEditor},
  beforeMount() {
    document.title = "用户管理 - Cloud OJ"
    this.getUsers()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedUser.userId)
        return callback(new Error("请确认输入正确"))
      callback()
    }
    return {
      loading: Boolean,
      roleNames: [
        "用户",
        "用户管理员",
        "题库管理员",
        "ROOT"
      ],
      roleTypes: [
        "info",
        "success",
        "primary",
        "danger"
      ],
      editorTitle: "",
      selectedUser: {},
      users: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 15,
      deleteForm: {
        checkUserId: ""
      },
      deleteRules: {
        checkUserId: [
          {required: true, message: "请输入用户ID", trigger: "blur"},
          {validator: validateDelete, trigger: "blur"}
        ]
      },
      editorDialogVisible: false,
      deleteDialogVisible: false,
      saveType: "",
    }
  },
  methods: {
    getUsers(refresh) {
      this.loading = true
      this.$axios({
        url: apiPath.userManage,
        method: "get",
        headers: {
          "token": userInfo().token,
          "userId": userInfo().userId
        },
        params: {
          page: this.currentPage,
          limit: this.pageSize,
        }
      }).then((res) => {
        this.users = res.status === 200 ? res.data : {data: [], count: 0}
        if (refresh === true) {
          Notice.message.success(this, "用户列表已刷新")
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: "获取数据失败",
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    onAddClick() {
      this.selectedUser = {}
      this.saveType = "post"
      this.editorTitle = "创建用户"
      this.editorDialogVisible = true
    },
    onEditClick(row) {
      this.selectedUser = copyObject(row)
      this.saveType = "put"
      this.editorTitle = `编辑用户【${row.userId}】`
      this.editorDialogVisible = true
    },
    onDeleteClick(row) {
      this.deleteForm.checkUserId = ""
      this.selectedUser = copyObject(row)
      this.deleteDialogVisible = true
    },
    onDelete() {
      this.$refs["deleteForm"].validate((valid) => {
        if (valid) {
          if (this.selectedUser.userId === userInfo().userId) {
            Notice.notify.warning(this, {
              title: "无法删除",
              message: "你当前正在使用该用户"
            })
          }
          this.$axios({
            url: `${apiPath.userManage}/${this.selectedUser.userId}`,
            method: "delete",
            headers: {
              "token": userInfo().token,
              "userId": userInfo().userId
            }
          }).then((res) => {
            this.getUsers()
            this.deleteDialogVisible = false
            Notice.notify.info(this, {
              title: `用户【${this.selectedUser.userId}】已删除`,
              message: `${res.status} ${res.statusText}`
            })
          }).catch((error) => {
            let res = error.response
            if (res.status === 401) {
              toLoginPage()
            } else {
              let msg
              if (res.status === 409)
                msg = "此用户存在做题记录，无法删除"
              else
                msg = res.data.msg === undefined ? res.statusText : res.data.msg
              Notice.notify.warning(this, {
                title: `无法删除用户【${this.selectedUser.userId}】`,
                message: `${res.status} ${msg}`
              })
            }
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>

</style>