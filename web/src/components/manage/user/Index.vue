<template>
  <div>
    <error-info :error="error" v-if="!loading && error.code != null"/>
    <el-card v-else class="borderless">
      <el-form size="medium" :inline="true" @submit.native.prevent>
        <el-form-item>
          <el-input style="width: 320px" v-model="searchOption.value" placeholder="请选择搜索方式"
                    @keyup.enter.native="getUsers">
            <el-select style="width: 100px" slot="prepend" v-model="searchOption.type">
              <el-option label="用户 ID" value="userId"></el-option>
              <el-option label="用户名" value="name"></el-option>
            </el-select>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="el-icon-search" @click="getUsers">
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-refresh" @click="refresh">
            刷新
          </el-button>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button size="small" icon="el-icon-circle-plus" type="primary"
                     @click="addUser">
            创建新用户
          </el-button>
        </el-form-item>
      </el-form>
      <el-empty v-if="!loading && users.count === 0"/>
      <el-table v-else :data="users.data" stripe v-loading="loading" @row-dblclick="rowDbClick">
        <el-table-column prop="userId" width="140px">
          <template slot="header">
            <Icon class="el-icon--left" name="id-card"/>
            <span>ID</span>
          </template>
        </el-table-column>
        <el-table-column prop="name">
          <template slot="header">
            <i class="el-icon-user-solid el-icon--left"/>
            <span>用户名</span>
          </template>
          <template slot-scope="scope">
            <div style="display: inline-flex; align-items: center">
              <el-avatar class="el-icon--left" size="small" icon="el-icon-user-solid" alt="user"
                         :src="`/api/file/image/avatar/${scope.row.userId}.png`">
              </el-avatar>
              <el-link :underline="false" @click="usernameClick(scope.row.userId)">
                <b>{{ scope.row.name }}</b>
              </el-link>
              <el-tag v-if="scope.row.userId === userInfo.userId" class="el-icon--right"
                      type="success" size="mini" effect="dark">
                你自己
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center">
          <template slot="header">
            <Icon name="user-shield"/>
            <span style="margin-left: 5px">角色/权限</span>
          </template>
          <template slot-scope="scope">
            <el-tag style="width: 80px" size="small" :type="roleTypes[scope.row['roleId']]"
                    :effect="scope.row['roleId'] > 0 ? 'dark' : 'light'">
              <span>{{ roleNames[scope.row["roleId"]] }}</span>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column width="160px" align="center">
          <template slot="header">
            <i class="el-icon-date el-icon--left"/>
            <span>注册时间</span>
          </template>
          <template slot-scope="scope">
            {{ formatDate(scope.row["createAt"]) }}
          </template>
        </el-table-column>
        <el-table-column width="120px" align="center">
          <template slot="header">
            <i class="el-icon-menu el-icon--left"/>
            <span>操作</span>
          </template>
          <template slot-scope="scope">
            <el-button-group>
              <el-button size="mini" icon="el-icon-edit" @click="editUser(scope.row)">
              </el-button>
              <el-button size="mini" icon="el-icon-delete" :disabled="scope.row.userId === 'root'"
                         @click="deleteUser(scope.row)">
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px" layout="total, prev, pager, next" background
                     :page-size.sync="pageSize" :total="users.count"
                     :current-page.sync="currentPage"
                     @size-change="getUsers" @current-change="getUsers">
      </el-pagination>
      <!-- Editor Dialog -->
      <el-dialog center :title="editorDialog.title" :visible.sync="editorDialog.visible">
        <UserEditor :user-data="selectedUser" :create="editorDialog.create"
                    :dialog-visible.sync="editorDialog.visible" @refresh="getUsers"/>
      </el-dialog>
      <!-- Delete Dialog -->
      <el-dialog title="提示" :visible.sync="deleteDialog.visible">
        <el-alert type="warning" show-icon :closable="false"
                  :title="`你正在删除用户：${selectedUser.userId}`"
                  description="如果该用户已有做题记录，只能通过操作数据库删除">
        </el-alert>
        <el-form ref="deleteForm" :model="deleteDialog.form" :rules="deleteDialog.rules"
                 @submit.native.prevent>
          <el-form-item label="输入用户ID确认删除" prop="checkUserId">
            <el-input v-model="deleteDialog.form.checkUserId" :placeholder="selectedUser.userId">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="danger" icon="el-icon-delete" size="medium" @click="confirmDelete">
              删除
            </el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import {copyObject, Notice, userInfo} from "@/util"
import UserEditor from "@/components/manage/user/UserEditor"
import ErrorInfo from "@/components/ErrorInfo"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/id-card"
import "vue-awesome/icons/user-shield"
import {UserApi} from "@/service"
import moment from "moment"

export default {
  name: "UserManage",
  components: {
    UserEditor,
    ErrorInfo,
    Icon
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
      error: {
        code: null,
        msg: ""
      },
      userInfo: userInfo(),
      roleNames: [
        "用户",
        "用户管理员",
        "题目管理员",
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
      searchOption: {
        type: "userId",
        value: ""
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
  created() {
    this.$siteSetting.setTitle("用户管理")

    if (this.userInfo != null) {
      this.getUsers()
    } else {
      this.loading = false
      this.error = {
        code: 401,
        msg: "未授权"
      }
    }
  },
  methods: {
    refresh() {
      this.getUsers(true)
    },
    usernameClick(userId) {
      this.$router.push({path: "/profile", query: {userId}})
    },
    getUsers(refresh = false) {
      this.loading = true
      const type = this.searchOption.type
      const value = this.searchOption.value
      const searchParam = {
        userId: type === "userId" && value !== "" ? value : null,
        name: type === "name" && value !== "" ? value : null
      }
      UserApi.getAll(this.currentPage, this.pageSize, searchParam, userInfo())
          .then((data) => {
            this.users = data
            refresh === true && Notice.message.success(this, "用户列表已刷新")
          })
          .catch((error) => {
            if (error.code === 401) {
              this.$bus.$emit("login")
            } else if (error.code === 403) {
              this.error = {
                code: 403,
                msg: "无权限访问此页面"
              }
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
    formatDate(time) {
      return moment(time).format("YYYY/MM/DD")
    },
    addUser() {
      this.selectedUser = {}
      this.editorDialog = {
        title: "创建新用户",
        visible: true,
        create: true
      }
    },
    editUser(row) {
      this.selectedUser = copyObject(row)
      this.editorDialog = {
        title: `${row.name}(${row.userId})`,
        visible: true,
        create: false
      }
    },
    rowDbClick(row) {
      this.editUser(row)
    },
    deleteUser(row) {
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
                this.$bus.$emit("login")
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