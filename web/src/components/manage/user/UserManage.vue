<template>
  <el-card>
    <span class="table-title">用户管理</span>
    <el-divider></el-divider>
    <el-form :inline="true">
      <el-form-item>
        <el-button icon="el-icon-circle-plus" type="success"
                   @click="onAddClick">创建新用户
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-refresh" @click="getUsers">
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="users.data" border stripe v-loading="loading">
      <el-table-column label="ID" prop="userId" width="150px">
      </el-table-column>
      <el-table-column label="用户名/昵称" prop="name">
      </el-table-column>
      <el-table-column label="角色/权限" width="200px" align="center">
        <template slot-scope="scope">
          <el-tag effect="dark"
                  :type="roleTypes[scope.row.roleId]">
            <span>{{ roleNames[scope.row.roleId] }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-button size="mini" icon="el-icon-edit"
                     @click="onEditClick(scope.row)">编辑
          </el-button>
          <el-button size="mini" icon="el-icon-delete" type="danger"
                     @click="onDeleteClick(scope.row)">
          </el-button>
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
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[10, 25, 50]"
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
                  @refresh="getUsers"/>
    </el-dialog>
    <!-- Delete Dialog -->
    <el-dialog title="删除提示"
               :visible.sync="deleteDialogVisible">
      <el-alert type="warning" show-icon
                :title="`你正在删除用户：${selectedUser.userId}`"
                description="与该题目相关的所有数据也会被删除!"
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
          <el-button type="danger" @click="onDelete">删除题目</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import {apiPath, copyObject, handle401, userInfo} from "@/js/util";
import UserEditor from "@/components/manage/user/UserEditor";

export default {
  name: "UserManage",
  components: {UserEditor},
  beforeMount() {
    document.title = `用户管理 · Cloud OJ`
    this.getUsers()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedUser.userId)
        return callback(new Error('请确认输入正确'))
      callback()
    }
    return {
      loading: Boolean,
      roleNames: [
        '用户',
        '用户管理',
        '题库管理',
        'ROOT'
      ],
      roleTypes: [
        'info',
        'success',
        'primary',
        'danger'
      ],
      editorTitle: '',
      selectedUser: {},
      users: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 25,
      deleteForm: {
        checkUserId: ''
      },
      deleteRules: {
        checkUserId: [
          {required: true, message: '请输入用户ID', trigger: 'blur'},
          {validator: validateDelete, trigger: 'blur'}
        ]
      },
      editorDialogVisible: false,
      deleteDialogVisible: false,
      saveType: '',
    }
  },
  methods: {
    getUsers() {
      this.loading = true
      this.$axios({
        url: apiPath.userManage,
        method: 'get',
        headers: {
          'token': userInfo().token
        },
        params: {
          page: this.currentPage,
          limit: this.pageSize,
          userId: userInfo().userId
        }
      }).then((res) => {
        this.loading = false
        this.users = res.data
      }).catch((error) => {
        this.loading = false
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: '获取数据失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      })
    },
    onAddClick() {
      this.selectedUser = {}
      this.saveType = 'post'
      this.editorTitle = '创建用户'
      this.editorDialogVisible = true
    },
    onEditClick(row) {
      this.selectedUser = copyObject(row)
      this.saveType = 'put'
      this.editorTitle = `修改用户【${row.userId}】的信息`
      this.editorDialogVisible = true
    },
    onDeleteClick(row) {
      this.selectedUser = copyObject(row)
      this.deleteDialogVisible = true
    },
    onDelete() {
      this.$refs['deleteForm'].validate((valid) => {
        if (valid) {
          this.$axios({
            url: `${apiPath.userManage}/${this.selectedUser.userId}?userId=${userInfo().userId}`,
            method: 'delete',
            headers: {
              'token': userInfo().token
            }
          }).then((res) => {
            this.getUsers()
            this.deleteDialogVisible = false
            this.$notify({
              type: 'info',
              title: `用户【${this.selectedUser.userId}】已删除`,
              message: `${res.status} ${res.statusText}`
            })
          }).catch((error) => {
            let res = error.response
            if (res.status === 401) {
              handle401()
            } else {
              this.$notify.error({
                title: `用户【${this.selectedUser.userId}】删除失败`,
                message: `${res.status} ${res.statusText}`
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