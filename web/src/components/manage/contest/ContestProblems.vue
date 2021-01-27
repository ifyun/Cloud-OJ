<template>
  <div>
    <el-button type="primary" size="medium" icon="el-icon-circle-plus"
               @click="problemsDialog.visible = true">添加题目
    </el-button>
    <el-table style="margin-top: 15px" :data="problems.data" border v-loading="loading">
      <el-table-column label="题目名称">
        <template slot-scope="scope">
          <el-button size="small" @click="preview(scope.row)">
            {{ scope.row.problemId }} - {{ scope.row.title }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="分值" prop="score" width="70px" align="right">
      </el-table-column>
      <el-table-column label="操作" width="100px" align="center">
        <template slot-scope="scope">
          <el-popconfirm title="确定要移除吗？（此操作不会删除题目）" icon="el-icon-info"
                         confirm-button-type="danger" confirm-button-text="确定"
                         cancel-button-type="plain" cancel-button-text="取消"
                         @confirm="removeProblem(scope.row.problemId, scope.row.title)">
            <el-button type="danger" size="mini" icon="el-icon-delete" slot="reference">
              移除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        style="margin-top: 15px" small layout="total, prev, pager, next"
        :page-size.sync="pageSize" :total="problems.count"
        :current-page.sync="currentPage"
        @size-change="getProblems" @current-change="getProblems">
    </el-pagination>
    <el-dialog title="添加题目" append-to-body width="800px"
               :visible.sync="problemsDialog.visible"
               @close="getProblems">
      <AddProblems :contest-id="contestId" :visibility="problemsDialog.visible"/>
    </el-dialog>
    <el-dialog :title="previewDialog.title" :visible.sync="previewDialog.visible"
               append-to-body width="750px">
      <markdown-it-vue :options="mdOptions" :content="previewDialog.content"/>
    </el-dialog>
  </div>
</template>

<script>
import MarkdownItVue from "markdown-it-vue"
import "markdown-it-vue/dist/markdown-it-vue.css"
import {userInfo, toLoginPage, Notice} from "@/util"
import AddProblems from "@/components/manage/contest/AddProblems"
import {ContestApi} from "@/service"

export default {
  name: "ContestProblemsManage",
  components: {
    AddProblems,
    MarkdownItVue
  },
  props: {
    contestId: Number
  },
  watch: {
    contestId: {
      immediate: true,
      handler() {
        if (typeof this.contestId !== "undefined") {
          this.getProblems()
        }
      }
    }
  },
  data() {
    return {
      loading: true,
      problems: {
        data: [],
        count: 0
      },
      pageSize: 15,
      currentPage: 1,
      problemsDialog: {
        visible: false
      },
      previewDialog: {
        title: "",
        content: "",
        visible: false
      },
      mdOptions: {
        markdownIt: {
          html: true
        }
      }
    }
  },
  methods: {
    getProblems() {
      this.loading = true
      ContestApi.getProblems(this.contestId, this.currentPage, this.pageSize, userInfo())
          .then((data) => {
            this.problems = data
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "获取数据失败",
              message: `${error.code} ${error.msg}`
            })
          })
          .finally(() => {
            this.loading = false
          })
    },
    preview(row) {
      this.previewDialog = {
        title: `${row.problemId} - ${row.title}`,
        content: row.description,
        visible: true
      }
    },
    removeProblem(problemId, title) {
      ContestApi.removeProblem(this.contestId, problemId, userInfo())
          .then(() => {
            Notice.notify.warning(this, {
              title: `${title} 已移除`,
            })
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
            } else {
              Notice.notify.error(this, {
                title: `${title} 移除失败`,
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.getProblems()
          })
    }
  }
}
</script>

<style scoped>
</style>