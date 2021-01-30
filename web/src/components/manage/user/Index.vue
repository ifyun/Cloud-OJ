<template>
  <el-card>
    <el-form :inline="true">
      <el-form-item>
        <el-button size="medium" icon="el-icon-circle-plus" type="primary"
                   @click="addClick">
          创建新用户
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" icon="el-icon-refresh" @click="refresh">
          刷新
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="users.data" stripe v-loading="loading">
      <el-table-column prop="userId" width="150px">
        <template slot="header">
          <Icon name="id-card"/>
          <span style="margin-left: 5px">ID</span>
        </template>
      </el-table-column>
      <el-table-column prop="name">
        <template slot="header">
          <i class="el-icon-user-solid el-icon--left"/>
          <span>用户名</span>
        </template>
        <template slot-scope="scope">
          <div style="display: inline-flex;align-items: center">
            <el-link :underline="false" :href="`/profile?userId=${scope.row.userId}`">
              {{ scope.row.name }}
            </el-link>
            <el-tag v-if="scope.row.userId === userInfo.userId" class="el-icon--right"
                    type="success" size="mini" effect="dark">
              你自己
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column width="200px" align="center">
        <template slot="header">
          <Icon name="user-shield"/>
          <span style="margin-left: 5px">角色/权限</span>
        </template>
        <template slot-scope="scope">
          <el-tag effect="light" style="width: 90px" size="small"
                  :type="roleTypes[scope.row['roleId']]">
            <span>{{ roleNames[scope.row["roleId"]] }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="220px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row["createAt"] }}</i>
        </template>
      </el-table-column>
      <el-table-column width="180px" align="center">
        <template slot="header">
          <i class="el-icon-menu el-icon--left"></i>
          <span>操作</span>
        </template>
        <template slot-scope="scope">
          <el-button-group>
            <el-button size="mini" icon="el-icon-edit"
                       @click="editClick(scope.row)">
              编辑
            </el-button>
            <el-button size="mini" icon="el-icon-delete" type="danger"
                       :disabled="scope.row.userId === 'root'"
                       @click="deleteClick(scope.row)">
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px" layout="total, prev, pager, next"
                   :page-size.sync="pageSize" :total="users.count"
                   :current-page.sync="currentPage"
                   @size-change="getUsers" @current-change="getUsers">
    </el-pagination>
    <!-- Editor Dialog -->
    <el-dialog :title="editorDialog.title"
               :visible.sync="editorDialog.visible">
      <UserEditor :user="selectedUser"
                  :create="editorDialog.create"
                  :dialog-visible.sync="editorDialog.visible"
                  @refresh="getUsers"/>
    </el-dialog>
    <!-- Delete Dialog -->
    <el-dialog title="提示"
               :visible.sync="deleteDialog.visible">
      <el-alert type="warning" show-icon
                :title="`你正在删除用户：${selectedUser.userId}`"
                description="如果该用户已有做题记录，只能通过操作数据库删除"
                :closable="false">
      </el-alert>
      <el-form ref="deleteForm"
               :model="deleteDialog.form"
               :rules="deleteDialog.rules"
               @submit.native.prevent>
        <el-form-item label="输入用户名确认删除" prop="checkUserId">
          <el-input v-model="deleteDialog.form.checkUserId" :placeholder="selectedUser.userId">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" @click="confirmDelete">删除用户</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import {copyObject, Notice, searchParams, toLoginPage, userInfo} from "@/util"
import UserEditor from "@/components/manage/user/UserEditor"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/id-card"
import "vue-awesome/icons/user-shield"
import {UserApi} from "@/service"

export default {
  name: "UserManage",
  components: {
    UserEditor,
    Icon
  },
  beforeMount() {
    this.siteSetting.setTitle("用户管理")
    this.loadPage()
    this.getUsers()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedUser.userId) {
        return callback(new Error("请确认输入正确"))
      }
      callback()
    }
    return {
      loading: Boolean,
      userInfo: userInfo(),
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
      selectedUser: {},
      users: {
        data: [],
        count: 0
      },
      deleteDialog: {
        visible: false,
        form: {
          checkUserId: ""
        },
        rules: {
          checkUserId: [
            {required: true, message: "请输入用户ID", trigger: "blur"},
            {validator: validateDelete, trigger: "blur"}
          ]
        }
      },
      editorDialog: {
        visible: false,
        title: "",
        create: false
      },
      currentPage: 1,
      pageSize: 15
    }
  },
  methods: {
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    refresh() {
      this.getUsers(true)
    },
    getUsers(refresh = false) {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      UserApi.getAll(this.currentPage, this.pageSize, userInfo())
          .then((data) => {
            this.users = data
            refresh === true && Notice.message.success(this, "用户列表已刷新")
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: "获取数据失败",
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.loading = false
          })
    },
    addClick() {
      this.selectedUser = {}
      this.editorDialog = {
        title: "创建新用户",
        visible: true,
        create: true
      }
    },
    editClick(row) {
      this.selectedUser = copyObject(row)
      this.editorDialog = {
        title: `${row.userId} - ${row.name}`,
        visible: true,
        create: false
      }
    },
    deleteClick(row) {
      this.deleteDialog.form.checkUserId = ""
      this.selectedUser = copyObject(row)
      this.deleteDialog.visible = true
    },
    confirmDelete() {
      this.$refs["deleteForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        if (this.selectedUser.userId === userInfo().userId) {
          Notice.notify.warning(this, {
            title: "无法删除",
            message: "你当前正在使用该用户"
          })
        }
        UserApi.delete(this.selectedUser.userId, userInfo())
            .then((res) => {
              this.deleteDialog.visible = false
              Notice.notify.info(this, {
                title: `用户 ${this.selectedUser.userId} 已删除`,
                message: `${res.status} ${res.statusText}`
              })
            })
            .catch((error) => {
              if (error.code === 401) {
                toLoginPage()
              } else {
                const msg = error.code === 409 ? "此用户存在做题记录，无法删除" : error.msg
                Notice.notify.warning(this, {
                  title: `无法删除用户 ${this.selectedUser.userId}`,
                  message: `${error.code} ${msg}`
                })
              }
            })
            .finally(() => {
              this.getUsers()
            })
      })
    }
  }
}
</script>

<style scoped>

</style>