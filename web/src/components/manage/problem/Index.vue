<template>
  <el-card>
    <div style="align-self: flex-start">
      <el-form :inline="true" @submit.native.prevent>
        <el-form-item>
          <el-input size="medium" placeholder="输入关键字" prefix-icon="el-icon-search"
                    v-model="keyword">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" type="success" icon="el-icon-search"
                     @click="search(keyword)">
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" icon="el-icon-refresh" @click="refresh">
            刷新
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-tag type="success"
                  v-if="keyword !== ''"
                  closable
                  @close="tagClose()">{{ keyword }}
          </el-tag>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button-group>
            <el-button type="primary" size="medium"
                       icon="el-icon-circle-plus"
                       @click="addProblemClick()">
              添加题目
            </el-button>
            <el-button icon="el-icon-upload2" size="medium"
                       @click="importDialog.visible = true">
              导入题目
            </el-button>
            <el-button icon="el-icon-download" size="medium"
                       @click="backupProblems">
              导出题目
            </el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="problems.data" stripe v-loading="loading">
      <el-table-column label="题目名称" width="250px">
        <template slot-scope="scope">
          <el-link :href="`./commit?problemId=${scope.row.problemId}`">
            {{ scope.row.problemId }}&nbsp;<b>{{ scope.row.title }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center">
        <template slot="header">
          <i class="el-icon-collection-tag el-icon--left"></i>
          <span>分类</span>
        </template>
        <template slot-scope="scope">
          <div v-if="typeof scope.row.category !== 'undefined' && scope.row.category !== ''">
            <span v-for="tag in scope.row.category.split(',')"
                  v-bind:key="tag.index"
                  @click="tagClick(tag)"
                  class="tag" :class="getTagColor(tag)">
              {{ tag }}
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分值" prop="score" width="60px" align="right">
      </el-table-column>
      <el-table-column label="开放" width="90px" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enable"
                     active-color="#67C23A"
                     @change="toggleOpen($event, scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column width="130px" align="center">
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
          <i class="el-icon-menu"></i>
        </template>
        <template slot-scope="scope">
          <el-dropdown trigger="click" @command="operation($event, scope.row)">
            <span class="el-dropdown-link">
              <i class="el-icon-arrow-down"></i>
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
    <el-pagination style="margin-top: 10px" layout="total, prev, pager, next"
                   :page-size.sync="pageSize" :total="problems.count"
                   :current-page.sync="currentPage"
                   @size-change="getProblems" @current-change="getProblems">
    </el-pagination>
    <!-- Editor Dialog -->
    <el-dialog :title="editorDialog.title" class="editor-dialog" fullscreen center
               :close-on-press-escape="false" :close-on-click-modal="false"
               :visible.sync="editorDialog.visible">
      <ProblemEditor class="editor" :problem-id="selectedId" :create="editorDialog.create"
                     :dialog-visible.sync="editorDialog.visible"
                     @refresh="getProblems"/>
    </el-dialog>
    <!-- Test Data Manage Dialog -->
    <el-dialog :title="`${selectedTitle} - 测试数据`"
               :visible.sync="testDataDialog.visible"
               width="850px">
      <TestDataManage :problem-id="selectedId"
                      :dialog-visible.sync="testDataDialog.visible"/>
    </el-dialog>
    <!-- Delete Dialog -->
    <el-dialog title="提示"
               :visible.sync="deleteDialog.visible">
      <el-alert type="warning" show-icon
                :title="`你正在删除题目：${selectedTitle}`"
                description="与该题目相关的提交记录也会被删除!"
                :closable="false">
      </el-alert>
      <el-form ref="deleteForm"
               :model="deleteDialog.form"
               :rules="deleteDialog.rules"
               @submit.native.prevent>
        <el-form-item label="输入题目名称确认删除" prop="checkTitle">
          <el-input v-model="deleteDialog.form.checkTitle" :placeholder="selectedTitle">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" @click="confirmDelete">
            删除题目
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog title="导入题目" :visible.sync="importDialog.visible">
      <el-upload drag :action="importDialog.backupUrl" :headers="{token: userInfo.token}"
                 :data="{userId: userInfo.userId}" :on-success="importSuccess">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传json文件，导入的题目会自动生成新的ID</div>
      </el-upload>
    </el-dialog>
  </el-card>
</template>

<script>
import {userInfo, tagColor, toLoginPage, Notice, searchParams} from "@/util"
import ProblemEditor from "@/components/manage/problem/ProblemEditor"
import TestDataManage from "@/components/manage/problem/TestDataManage"
import {ApiPath, ProblemApi} from "@/service"
import moment from "moment";

export default {
  name: "ProblemManage",
  components: {
    TestDataManage,
    ProblemEditor
  },
  beforeMount() {
    this.siteSetting.setTitle("题库管理")
    this.loadPage()
    this.getProblems()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedTitle) {
        return callback(new Error("请确认输入正确"))
      }
      callback()
    }
    return {
      userInfo: userInfo(),
      keyword: "",
      selectedId: null,
      selectedTitle: "",
      loading: true,
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
      importDialog: {
        visible: false,
        backupUrl: ApiPath.BACKUP
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
  methods: {
    getTagColor: tagColor,
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    formatDate(time) {
      return moment(time).format("YYYY/MM/DD")
    },
    refresh() {
      this.getProblems(true)
    },
    getProblems(refresh = false) {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      ProblemApi.getAll(this.currentPage, this.pageSize, this.keyword, this.userInfo)
          .then((data) => {
            this.problems = data
            refresh === true && Notice.message.success(this, "题目列表已刷新")
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
    search() {
      this.currentPage = 1
      this.getProblems()
    },
    tagClose() {
      this.keyword = ""
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
                toLoginPage()
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
      this.selectedId = problem.problemId
      this.selectedTitle = problem.title

      switch (cmd) {
        case "edit":
          this.editorDialog = {
            visible: true,
            create: false,
            title: `${problem.problemId} - ${problem.title}`
          }
          break
        case "manage-data":
          this.testDataDialog.visible = true
          break
        case "delete":
          this.deleteDialog.visible = true
      }
    },
    addProblemClick() {
      this.selectedId = null
      this.editorDialog = {
        title: "创建新题目",
        create: true,
        visible: true
      }
    },
    confirmDelete() {
      this.$refs["deleteForm"].validate((valid) => {
        if (!valid) {
          return false
        }
        ProblemApi.delete(this.selectedId, this.userInfo)
            .then((res) => {
              this.deleteDialog.visible = false
              Notice.notify.info(this, {
                title: `${this.selectedTitle} 已删除`,
                message: `${res.status} ${res.statusText}`
              })
            })
            .catch((error) => {
              switch (error.code) {
                case 401:
                  toLoginPage()
                  break
                case 400:
                  Notice.notify.error(this, {
                    title: `${this.selectedTitle} 删除失败`,
                    message: `${error.code} ${error.msg}`
                  })
                  break
                default:
                  Notice.notify.error(this, {
                    title: `${this.selectedTitle} 删除失败`,
                    message: `${error.code} ${error.msg}`
                  })
              }
            })
            .finally(() => {
              this.getProblems()
            })
      })
      this.deleteDialog.form.checkTitle = ""
    },
    backupProblems() {
      let newWindow = window.open("_blank")
      newWindow.location.href =
          `${ApiPath.BACKUP}?userId=${userInfo().userId}&token=${userInfo().token}`
    },
    importSuccess() {
      this.importDialog.visible = false
      this.getProblems()
    }
  }
}
</script>

<style scoped>
.editor {
  max-width: 1450px;
  margin: 0 auto;
}

.el-dropdown-link {
  cursor: pointer;
  color: #303133;
}
</style>