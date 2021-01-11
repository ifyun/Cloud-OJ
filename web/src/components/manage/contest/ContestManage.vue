<template>
  <el-card>
    <el-form :inline="true">
      <el-form-item>
        <el-button size="medium" type="primary" icon="el-icon-circle-plus"
                   @click="addContestClick">
          创建新竞赛/作业
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" icon="el-icon-refresh" @click="getContests(true)">
          刷新
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="contests.data" stripe v-loading="loading">
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
      <el-table-column width="220px" align="center">
        <template slot="header">
          <i class="el-icon-menu el-icon--left"></i>
          <span>操作</span>
        </template>
        <template slot-scope="scope">
          <el-button-group>
            <el-button size="mini" icon="el-icon-edit"
                       @click="editClick(scope.$index)">
            </el-button>
            <el-button size="mini" icon="el-icon-s-grid" @click="manageProblemsClick(scope.$index)">
              管理题目
            </el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete"
                       @click="deleteClick(scope.$index)">
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px" background layout="total, prev, pager, next"
                   :page-size="pageSize" :total="contests.count"
                   :current-page.sync="currentPage"
                   @size-change="getContests" @current-change="getContests">
    </el-pagination>
    <!-- Edit Contest Dialog -->
    <el-dialog :title="editorDialog.title" width="650px"
               :visible.sync="editorDialog.visible">
      <ContestEditor :contest="selectedContest"
                     :create="editorDialog.create"
                     :dialog-visible.sync="editorDialog.visible"
                     @refresh="getContests"/>
    </el-dialog>
    <!-- Delete Confirm Dialog -->
    <el-dialog title="删除提示" width="650px"
               :visible.sync="deleteDialog.visible">
      <el-alert type="warning" show-icon
                :title="`你正在删除：${this.selectedContest.contestName}`"
                description="相关提交记录会消失（该竞赛/作业包含的题目不会被删除）"
                :closable="false">
      </el-alert>
      <el-form :model="deleteDialog.form" ref="deleteForm"
               :rules="deleteDialog.rules"
               @submit.native.prevent>
        <el-form-item label="输入名称确认删除" prop="checkName">
          <el-input v-model="deleteDialog.form.checkName"
                    :placeholder="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" @click="confirmDelete">删除</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- Problems Manage Dialog -->
    <el-dialog :title="selectedContest.contestName" width="1000px"
               :visible.sync="problemsDialog.visible">
      <ContestProblemsManage :contest-id="selectedContest.contestId"/>
    </el-dialog>
  </el-card>
</template>

<script>
import {copyObject, userInfo, toLoginPage, Notice, searchParams} from "@/util"
import ContestProblemsManage from "@/components/manage/contest/ContestProblems"
import ContestEditor from "@/components/manage/contest/ContestEditor"
import {ContestApi} from "@/service"

export default {
  name: "CompetitionManage",
  components: {
    ContestEditor,
    ContestProblemsManage
  },
  beforeMount() {
    document.title = "竞赛/作业管理 - Cloud OJ"
    this.loadPage()
    this.getContests()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedContest.contestName) {
        return callback(new Error("请确认输入正确"))
      }
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
        contestName: "",
        startAt: "",
        endAt: "",
        languages: ""
      },
      pageSize: 15,
      currentPage: 1,
      editorDialog: {
        title: "",
        visible: false,
        create: false
      },
      problemsDialog: {
        visible: false
      },
      deleteDialog: {
        visible: false,
        form: {
          checkName: ""
        },
        rules: {
          checkName: [
            {required: true, message: "请填写竞赛/作业名称", trigger: "blur"},
            {validator: validateDelete, trigger: "blur"}
          ]
        }
      }
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
      this.getContests(true)
    },
    getContests(refresh = false) {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      ContestApi.getAll(this.currentPage, this.pageSize, userInfo())
          .then((data) => {
            this.contests = data
            refresh === true && Notice.message.success(this, "竞赛/作业列表已刷新")
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: "获取竞赛/作业失败",
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.loading = false
          })
    },
    editClick(index) {
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
      this.editorDialog = {
        create: false,
        title: this.selectedContest.contestName,
        visible: true
      }
    },
    manageProblemsClick(index) {
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
      this.problemsDialog.visible = true
    },
    addContestClick() {
      this.selectedContest = {}
      this.editorDialog = {
        title: "创建竞赛/作业",
        create: true,
        visible: true
      }
    },
    deleteClick(index) {
      this.deleteDialog.visible = true
      let i = this.pageSize * (this.currentPage - 1) + index
      this.selectedContest = copyObject(this.contests.data[i])
    },
    confirmDelete() {
      this.$refs["deleteForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        this.deleteDialog.form.checkName = ""
        ContestApi.delete(this.selectedContest.contestId, userInfo())
            .then(() => {
              this.deleteDialog.visible = false
              Notice.notify.warning(this, {
                title: `${this.selectedContest.contestName} 已删除`
              })
            })
            .catch((error) => {
              if (error.code === 401) {
                toLoginPage()
              } else {
                Notice.notify.error(this, {
                  title: `${this.selectedContest.contestName} 删除失败`,
                  message: `${error.code} ${error.msg}`
                })
              }
            })
            .finally(() => {
              this.getContests()
            })
      })
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