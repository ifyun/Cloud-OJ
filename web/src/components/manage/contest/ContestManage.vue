<template>
  <el-card>
    <el-form :inline="true">
      <el-form-item>
        <el-button type="primary" icon="el-icon-circle-plus"
                   @click="onAddContestClick">
          创建新竞赛/作业
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-refresh" @click="getContests(true)">
          刷新
        </el-button>
      </el-form-item>
    </el-form>
    <!-- Contest Table -->
    <el-table :data="contests.data" stripe border v-loading="loading">
      <el-table-column label="ID" prop="contestId" width="100px" align="center">
      </el-table-column>
      <el-table-column label="竞赛/作业名称">
        <template slot-scope="scope">
          <b>{{ scope.row.contestName }}</b>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" width="200px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.startAt }}</i>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" width="200px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.endAt }}</i>
        </template>
      </el-table-column>
      <!-- Operation -->
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-button-group>
            <el-button size="mini" icon="el-icon-edit-outline"
                       @click="onEditClick(scope.$index)">
            </el-button>
            <el-button size="mini" @click="manageProblems(scope.$index)">题目管理</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete"
                       @click="onDeleteClick(scope.$index)">
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[15, 25, 35]"
                   :page-size.sync="pageSize"
                   :total="contests.count"
                   :current-page.sync="currentPage"
                   @size-change="getContests"
                   @current-change="getContests">
    </el-pagination>
    <!-- Edit Contest Dialog -->
    <el-dialog :title="contestDialogTitle"
               :visible.sync="editorDialogVisible">
      <ContestEditor :contest="selectedContest"
                     :save-type="saveType"
                     :dialog-visible.sync="editorDialogVisible"
                     @refresh="getContests"/>
    </el-dialog>
    <!-- Delete Confirm Dialog -->
    <el-dialog title="删除提示" :visible.sync="deleteDialogVisible">
      <el-alert type="warning" show-icon
                :title="`你正在删除：${this.selectedContest.contestName}`"
                description="相关提交记录会消失（该竞赛/作业包含的题目不会被删除）"
                :closable="false">
      </el-alert>
      <el-form :model="deleteForm" ref="deleteForm"
               :rules="deleteRules"
               @submit.native.prevent>
        <el-form-item label="输入名称确认删除" prop="checkName">
          <el-input v-model="deleteForm.checkName"
                    :placeholder="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger"
                     @click="onDelete('deleteForm')">删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- Problems Manage Dialog -->
    <el-dialog :title="selectedContest.contestName" width="1000px"
               :visible.sync="problemsDialogVisible">
      <CompetitionProblemsManage :contest-id="selectedContest.contestId"/>
    </el-dialog>
  </el-card>
</template>

<script>
import {copyObject, userInfo, toLoginPage, Notice} from "@/script/util"
import {apiPath} from "@/script/env"
import CompetitionProblemsManage from "@/components/manage/contest/ContestProblems"
import ContestEditor from "@/components/manage/contest/ContestEditor"

export default {
  name: "CompetitionManage",
  components: {
    ContestEditor,
    CompetitionProblemsManage
  },
  beforeMount() {
    document.title = `竞赛/作业管理 · Cloud OJ`
    this.getContests()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedContest.contestName)
        return callback(new Error('请确认输入正确'))
      callback()
    }
    return {
      loading: true,
      contests: {
        data: [],
        count: 0
      },
      selectedIndex: null,
      selectedContest: {
        contestId: null,
        contestName: '',
        startAt: '',
        endAt: '',
        languages: ''
      },
      contestDialogTitle: '',
      saveType: '',
      pageSize: 15,
      currentPage: 1,
      editorDialogVisible: false,
      deleteDialogVisible: false,
      problemsDialogVisible: false,
      deleteForm: {
        checkName: ''
      },
      deleteRules: {
        checkName: [
          {required: true, message: '请输入名称', trigger: 'blur'},
          {validator: validateDelete, trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    getContests(refresh) {
      this.loading = true
      this.$axios({
        url: apiPath.contestManage,
        method: 'get',
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId
        },
        params: {
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.contests = res.status === 200 ? res.data : {data: [], count: 0}
        if (refresh === true) {
          Notice.message.success(this, '竞赛/作业列表已刷新')
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: `获取数据失败`,
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    onEditClick(index) {
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
      this.saveType = 'put'
      this.contestDialogTitle = '编辑'
      this.editorDialogVisible = true
    },
    manageProblems(index) {
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
      this.problemsDialogVisible = true
    },
    onAddContestClick() {
      this.saveType = 'post'
      this.contestDialogTitle = '创建竞赛/作业'
      this.selectedContest = {}
      this.editorDialogVisible = true
    },
    onDeleteClick(index) {
      this.deleteDialogVisible = true
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
    },
    onDelete(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios({
            url: `${apiPath.contestManage}/${this.selectedContest.contestId}`,
            method: 'delete',
            headers: {
              'token': userInfo().token,
              'userId': userInfo().userId
            },
          }).then((res) => {
            this.deleteDialogVisible = false
            this.getContests()
            Notice.notify.info(this, {
              title: `【${this.selectedContest.contestName}】已删除`,
              message: `${res.status} ${res.statusText}`
            })
          }).catch((error) => {
            let res = error.response
            if (res.status === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: `【${this.selectedContest.contestName}】删除失败`,
                message: `${res.status} ${res.statusText}`
              })
            }
          })
        }
      })
      this.deleteForm.checkName = ''
    }
  }
}
</script>

<style scoped>
.el-checkbox + .el-checkbox,
.el-checkbox.is-bordered + .el-checkbox.is-bordered {
  margin-left: 0;
}
</style>