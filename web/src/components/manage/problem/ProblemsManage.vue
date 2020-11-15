<template>
  <el-card>
    <div style="align-self: flex-start">
      <el-form :inline="true" @submit.native.prevent>
        <el-form-item>
          <el-input placeholder="输入关键字" prefix-icon="el-icon-search"
                    v-model="keyword">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="el-icon-search"
                     @click="search(keyword)">
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-refresh" @click="getProblems(true)">
            刷新
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-tag type="success"
                  v-if="showKeyword"
                  closable
                  @close="onTagClose()">{{ keyword }}
          </el-tag>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button-group>
            <el-button type="primary" size="medium"
                       icon="el-icon-circle-plus"
                       @click="onAddProblemClick()">
              添加题目
            </el-button>
            <el-button icon="el-icon-upload2" size="medium"
                       @click="importDialogVisible = true">
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
    <el-table :data="problems.data" stripe border v-loading="loading">
      <el-table-column label="ID" prop="problemId" width="80px" align="center">
      </el-table-column>
      <el-table-column label="题目名称" width="220px">
        <template slot-scope="scope">
          <el-link :href="`./commit?problemId=${scope.row.problemId}`">
            <b>{{ scope.row.title }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center">
        <template slot-scope="scope">
          <div v-if="scope.row.category !== ''" style="">
            <span v-for="tag in scope.row.category.split(',')"
                  v-bind:key="tag.index"
                  @click="onTagClick(tag)"
                  class="tag" :class="getTagColor(tag)">{{ tag }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="是否开放" width="80px" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enable"
                     active-color="#67C23A"
                     @change="toggleOpen($event, scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="190px" align="center">
        <template slot-scope="scope">
          <el-button-group>
            <el-button size="mini" icon="el-icon-edit-outline"
                       @click="onEditClick(scope.row)">
            </el-button>
            <el-button size="mini" @click="manageTestData(scope.row)">管理数据</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete"
                       @click="onDeleteClick(scope.row)">
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="130px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.createAt }}</i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[15, 25, 35]"
                   :page-size.sync="pageSize"
                   :total="problems.count"
                   :current-page.sync="currentPage"
                   @size-change="getProblems"
                   @current-change="getProblems">
    </el-pagination>
    <!-- Editor Dialog -->
    <el-dialog :title="editorDialog.title" width="800px"
               :visible.sync="editorDialog.visible">
      <ProblemEditor :problem-id="selectedId"
                     :save-type="saveType"
                     :dialog-visible.sync="editorDialog.visible"
                     @refresh="getProblems"/>
    </el-dialog>
    <!-- Test Data Manage Dialog -->
    <el-dialog :title="`【${selectedTitle}】的测试数据`"
               :visible.sync="testDataDialogVisible"
               width="850px">
      <TestDataManage :problem-id="selectedId"
                      :dialog-visible.sync="testDataDialogVisible"/>
    </el-dialog>
    <!-- Delete Dialog -->
    <el-dialog title="删除提示"
               :visible.sync="deleteDialogVisible">
      <el-alert type="warning" show-icon
                :title="`你正在删除题目：${selectedTitle}`"
                description="与该题目相关的提交记录也会被删除!"
                :closable="false">
      </el-alert>
      <el-form ref="deleteForm"
               :model="deleteForm"
               :rules="deleteRules"
               @submit.native.prevent>
        <el-form-item label="输入题目名称确认删除" prop="checkTitle">
          <el-input v-model="deleteForm.checkTitle" :placeholder="selectedTitle">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="onDelete">删除题目</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog title="导入题目" :visible.sync="importDialogVisible">
      <el-upload drag action="./api/manager/backup"
                 :headers="{token: userInfo.token}"
                 :data="{userId: userInfo.userId}"
                 :on-success="importSuccess">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传json文件，导入的题目会自动生成新的ID</div>
      </el-upload>
    </el-dialog>
  </el-card>
</template>

<script>
import {userInfo, tagColor, toLoginPage, Notice} from "@/script/util"
import {apiPath} from "@/script/env"
import ProblemEditor from "@/components/manage/problem/ProblemEditor"
import TestDataManage from "@/components/manage/problem/TestDataManage"

export default {
  name: "ProblemManager",
  components: {
    TestDataManage,
    ProblemEditor
  },
  beforeMount() {
    document.title = `题库管理 · Cloud OJ`
    this.getProblems()
  },
  data() {
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedTitle)
        return callback(new Error('请确认输入正确'))
      callback()
    }
    return {
      userInfo: userInfo(),
      keyword: '',
      showKeyword: false,
      selectedId: null,
      selectedTitle: '',
      loading: true,
      saveType: '',
      problems: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 15,
      editorDialog: {
        title: '',
        visible: false
      },
      testDataDialogVisible: false,
      deleteDialogVisible: false,
      importDialogVisible: false,
      deleteForm: {
        checkTitle: ''
      },
      deleteRules: {
        checkTitle: [
          {required: true, message: '请输入题目名称', trigger: 'blur'},
          {validator: validateDelete, trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    getTagColor: tagColor,
    getProblems(refresh) {
      this.loading = true
      let params = {
        page: this.currentPage,
        limit: this.pageSize
      }
      if (this.keyword !== '')
        params.keyword = this.keyword
      this.$axios({
        url: apiPath.problemManage,
        method: 'get',
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId
        },
        params: params
      }).then((res) => {
        this.problems = res.status === 200 ? res.data : {data: [], count: 0}
        if (refresh === true) {
          Notice.message.success(this, '题目列表已刷新')
        }
      }).catch((error) => {
        if (error.response.status === 401) {
          toLoginPage()
        } else {
          let res = error.response
          Notice.notify.error(this, {
            title: `获取数据失败`,
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    search() {
      this.currentPage = 1
      this.getProblems()
      this.showKeyword = true
    },
    onTagClose() {
      this.keyword = ''
      this.showKeyword = false
      this.getProblems()
    },
    onTagClick(tag) {
      this.keyword = tag
      this.search()
    },
    toggleOpen(value, row) {
      let state = value === true ? '开放' : '关闭'
      this.$axios({
        url: `${apiPath.problemManage}/${row.problemId}`,
        method: 'put',
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId
        },
        params: {
          enable: value
        }
      }).then((res) => {
        Notice.notify.warning(this, {
          title: `【${row.title}】已${state}`,
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        switch (res.status) {
          case 401:
            toLoginPage()
            break
          case 400:
            Notice.notify.error(this, {
              title: `【${row.title}】${state}失败`,
              message: `${res.data.msg}`
            })
            break
          default:
            Notice.notify.error(this, {
              title: `【${row.title}】${state}失败`,
              message: `${res.status} ${res.statusText}`
            })
        }
      }).finally(() => {
        this.getProblems()
      })
    },
    onAddProblemClick() {
      this.selectedId = null
      this.editorTitle = '创建新题目'
      this.saveType = 'post'
      this.editorDialogVisible = true
    },
    onEditClick(row) {
      this.selectedId = row.problemId
      this.saveType = 'put'
      this.editorDialog.title = `编辑【${row.title}】`
      this.editorDialog.visible = true
    },
    manageTestData(row) {
      this.selectedId = row.problemId
      this.selectedTitle = row.title
      this.testDataDialogVisible = true
    },
    onDeleteClick(row) {
      this.selectedId = row.problemId
      this.selectedTitle = row.title
      this.deleteDialogVisible = true
    },
    onDelete() {
      this.$refs['deleteForm'].validate((valid) => {
        if (valid) {
          this.$axios({
            url: `${apiPath.problemManage}/${this.selectedId}`,
            method: 'delete',
            headers: {
              'token': userInfo().token,
              'userId': userInfo().userId
            }
          }).then((res) => {
            this.deleteDialogVisible = false
            Notice.notify.info(this, {
              title: `${this.selectedTitle} 已删除`,
              message: `${res.status} ${res.statusText}`
            })
          }).catch((error) => {
            let res = error.response
            switch (res.status) {
              case 401:
                toLoginPage()
                break
              case 400:
                Notice.notify.error(this, {
                  title: `【${this.selectedTitle}】删除失败`,
                  message: `${res.data.msg}`
                })
                break
              default:
                Notice.notify.error(this, {
                  title: `【${this.selectedTitle}】删除失败`,
                  message: `${res.status} ${res.statusText}`
                })
            }
          }).finally(() => {
            this.getProblems()
          })
        } else {
          return false
        }
      })
      this.deleteForm.checkTitle = ''
    },
    backupProblems() {
      let newWindow = window.open('_blank')
      newWindow.location.href =
          `${apiPath.backup}?userId=${userInfo().userId}&token=${userInfo().token}`
    },
    importSuccess() {
      this.importDialogVisible = false
      this.getProblems()
    }
  }
}
</script>

<style scoped>

</style>