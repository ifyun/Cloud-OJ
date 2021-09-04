<template>
  <div>
    <error-info :error="error" v-if="!loading && error.code != null"/>
    <el-card v-else class="borderless">
      <div>
        <el-form :inline="true" @submit.native.prevent>
          <el-form-item>
            <el-input style="width: 300px" size="medium" placeholder="输入关键字"
                      prefix-icon="el-icon-search" v-model="keyword">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button size="medium" type="success" icon="el-icon-search" @click="search(keyword)">
              搜索
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button size="medium" icon="el-icon-refresh" @click="refresh">
              刷新
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-tag type="success" v-if="keywordTag !== ''" closable @close="keywordTagClose()">
              {{ keywordTag }}
            </el-tag>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button-group>
              <el-button type="primary" size="small" icon="el-icon-circle-plus"
                         @click="addProblemClick()">
                添加题目
              </el-button>
            </el-button-group>
          </el-form-item>
        </el-form>
      </div>
      <el-empty v-if="!loading && problems.count === 0"/>
      <el-table v-else :data="problems.data" stripe v-loading="loading" @row-dblclick="rowDbClick">
        <el-table-column label="#" align="center" type="index"
                         :index="(currentPage - 1) * pageSize + 1">
        </el-table-column>
        <el-table-column label="题目名称">
          <template slot-scope="scope">
            <el-link @click="titleClick(scope.row.problemId)">
              {{ scope.row.problemId }}&nbsp;{{ scope.row.title }}
            </el-link>
            <el-tag v-if="scope.row.type === 1" style="margin-left: 10px" type="info" size="small">
              SQL
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类">
          <template slot-scope="scope">
            <div v-if="typeof scope.row.category !== 'undefined' && scope.row.category !== ''">
            <span v-for="tag in scope.row.category.split(',')"
                  v-bind:key="tag.index" @click="tagClick(tag)"
                  class="tag" :class="getTagColor(tag)">
              {{ tag }}
            </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分数" prop="score" width="60px" align="right">
        </el-table-column>
        <el-table-column label="开放" width="90px" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enable" active-color="#67C23A"
                       @change="toggleOpen($event, scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column width="120px" align="center">
          <template slot="header">
            <i class="el-icon-date el-icon--left"/>
            <span>创建时间</span>
          </template>
          <template slot-scope="scope">
            {{ formatDate(scope.row["createAt"]) }}
          </template>
        </el-table-column>
        <el-table-column width="70px" align="center">
          <template slot="header">
            <i class="el-icon-menu el-icon--left"/>
            <span>操作</span>
          </template>
          <template slot-scope="scope">
            <el-dropdown trigger="click" @command="operation($event, scope.row)">
            <span class="el-dropdown-link">
              <i class="el-icon-s-unfold"/>
            </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-edit" command="edit">
                  编辑
                </el-dropdown-item>
                <el-dropdown-item icon="el-icon-document" command="manage-data">
                  管理测试数据
                </el-dropdown-item>
                <el-dropdown-item icon="el-icon-delete" command="delete" style="color: #F56C6C">
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" layout="total, prev, pager, next" background
                     :page-size.sync="pageSize" :total="problems.count"
                     :current-page.sync="currentPage"
                     @size-change="getProblems" @current-change="getProblems">
      </el-pagination>
      <!-- Editor Dialog -->
      <el-dialog :title="editorDialog.title" class="editor-dialog" fullscreen center
                 :close-on-press-escape="false" :close-on-click-modal="false"
                 :visible.sync="editorDialog.visible">
        <ProblemEditor class="editor" :problem-id="selected.problemId" :create="editorDialog.create"
                       :dialog-visible.sync="editorDialog.visible"
                       @refresh="getProblems"/>
      </el-dialog>
      <!-- Test Data Manage Dialog -->
      <el-dialog :title="`${selected.title} - 测试数据`"
                 :visible.sync="testDataDialog.visible"
                 width="850px">
        <TestDataManage :problem-id="selected.problemId" :sql="selected.type === 1"
                        :dialog-visible.sync="testDataDialog.visible"/>
      </el-dialog>
      <!-- Delete Dialog -->
      <el-dialog title="提示"
                 :visible.sync="deleteDialog.visible">
        <el-alert type="warning" show-icon
                  :title="`你正在删除题目：${selected.title}`"
                  description="与本题目相关的提交记录也会被删除!"
                  :closable="false">
        </el-alert>
        <el-form ref="deleteForm" @submit.native.prevent
                 :model="deleteDialog.form" :rules="deleteDialog.rules">
          <el-form-item label="输入题目名称确认删除" prop="checkTitle">
            <el-input v-model="deleteDialog.form.checkTitle" :placeholder="selected.title">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="danger" icon="el-icon-delete" size="medium" @click="confirmDelete">
              删除题目
            </el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import {userInfo, tagColor, Notice} from "@/util"
import ProblemEditor from "@/components/manage/problem/ProblemEditor"
import TestDataManage from "@/components/manage/problem/TestDataManage"
import ErrorInfo from "@/components/ErrorInfo"
import moment from "moment"
import {ProblemApi} from "@/service"

export default {
  name: "ProblemManage",
  components: {
    ErrorInfo,
    TestDataManage,
    ProblemEditor
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selected.title) {
        return callback(new Error("请确认输入正确"))
      }
      callback()
    }
    return {
      loading: true,
      error: {
        code: null,
        msg: null
      },
      userInfo: userInfo(),
      keyword: "",
      keywordTag: "",
      selected: {},
      problems: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 15,
      editorDialog: {
        title: "",
        create: false,
        visible: false
      },
      testDataDialog: {
        visible: false
      },
      deleteDialog: {
        visible: false,
        form: {
          checkTitle: ""
        },
        rules: {
          checkTitle: [
            {required: true, message: "请输入题目名称", trigger: "blur"},
            {validator: validateDelete, trigger: "blur"}
          ]
        }
      }
    }
  },
  created() {
    this.$siteSetting.setTitle("题库管理")

    if(this.userInfo != null) {
      this.getProblems()
    } else {
      this.loading = false
      this.error = {
        code: 401,
        msg: "未授权"
      }
    }
  },
  methods: {
    getTagColor: tagColor,
    formatDate(time) {
      return moment(time).format("YYYY/MM/DD")
    },
    refresh() {
      this.getProblems(true)
    },
    titleClick(problemId) {
      this.$router.push({path: "/commit", query: {problemId}})
    },
    getProblems(refresh = false) {
      this.loading = true
      ProblemApi.getAll(this.currentPage, this.pageSize, this.keyword, this.userInfo)
          .then((data) => {
            this.problems = data
            refresh === true && Notice.message.success(this, "题目列表已刷新")
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
    search() {
      this.currentPage = 1
      this.keywordTag = this.keyword
      this.getProblems()
    },
    keywordTagClose() {
      this.keyword = ""
      this.keywordTag = ""
      this.getProblems()
    },
    tagClick(tag) {
      this.keyword = tag
      this.search()
    },
    toggleOpen(value, row) {
      let state = value === true ? "开放" : "关闭"
      ProblemApi.changeState(row.problemId, value, this.userInfo)
          .then(() => {
            Notice.notify.warning(this, {
              title: `${row.title} 已${state}`
            })
          })
          .catch((error) => {
            switch (error.code) {
              case 401:
                this.$bus.$emit("login")
                break
              case 400:
                Notice.notify.error(this, {
                  title: `${row.title} ${state}失败`,
                  message: `${error.code} ${error.msg}`
                })
                break
              default:
                Notice.notify.error(this, {
                  title: `${row.title} ${state}失败`,
                  message: `${error.code} ${error.msg}`
                })
            }
          })
          .finally(() => {
            this.getProblems()
          })
    },
    operation(cmd, problem) {
      this.selected = problem

      switch (cmd) {
        case "edit":
          this.editorDialog = {
            visible: true,
            create: false,
            title: `${problem.problemId}. ${problem.title}`
          }
          break
        case "manage-data":
          this.testDataDialog.visible = true
          break
        case "delete":
          this.deleteDialog.visible = true
      }
    },
    rowDbClick(row) {
      this.operation("edit", row)
    },
    addProblemClick() {
      this.selected = {}
      this.editorDialog = {
        title: "创建题目",
        create: true,
        visible: true
      }
    },
    confirmDelete() {
      this.$refs["deleteForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        ProblemApi.delete(this.selected.problemId, this.userInfo)
            .then((res) => {
              this.deleteDialog.visible = false
              Notice.notify.info(this, {
                title: `${this.selected.title} 已删除`,
                message: `${res.status} ${res.statusText}`
              })
            })
            .catch((error) => {
              switch (error.code) {
                case 401:
                  this.$bus.$emit("login")
                  break
                case 400:
                  Notice.notify.error(this, {
                    title: `${this.selected.title} 删除失败`,
                    message: `${error.code} ${error.msg}`
                  })
                  break
                default:
                  Notice.notify.error(this, {
                    title: `${this.selected.title} 删除失败`,
                    message: `${error.code} ${error.msg}`
                  })
              }
            })
            .finally(() => {
              this.getProblems()
            })
      })
      this.deleteDialog.form.checkTitle = ""
    }
  }
}
</script>

<style scoped>
.editor {
  max-width: 1450px;
  min-width: 1150px;
  margin: 0 auto;
}

.el-dropdown-link {
  cursor: pointer;
  color: #303133;
}
</style>