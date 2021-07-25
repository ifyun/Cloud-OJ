<template>
  <div>
    <el-alert type="info" show-icon style="margin-bottom: 15px"
              title="只能添加未开放的题目，点击名称可预览题目内容"
              :closable="false">
    </el-alert>
    <el-table :data="problems.data" size="small" stripe v-loading="loading">
      <el-table-column label="题目名称">
        <template slot-scope="scope">
          <el-button size="small" type="text" @click="preview(scope.row)">
            {{ scope.row.problemId }} - {{ scope.row.title }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="分数" prop="score" width="70px" align="right-end">
      </el-table-column>
      <el-table-column label="操作" width="100px" align="center">
        <template slot-scope="scope">
          <el-button type="success" size="mini" icon="el-icon-circle-plus"
                     @click="addProblemToContest(scope.row.problemId, scope.row.title)">添加
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 15px" small layout="total, prev, pager, next"
                   :page-size.sync="pageSize" :total="problems.count"
                   :current-page.sync="currentPage"
                   @size-change="getProblems" @current-change="getProblems">
    </el-pagination>
    <el-dialog :title="previewDialog.title" :visible.sync="previewDialog.visible"
               append-to-body width="750px">
      <markdown-it :content="previewDialog.content"/>
    </el-dialog>
  </div>
</template>

<script>
import MarkdownIt from "@/components/MarkdownIt"
import {userInfo, toLoginPage, Notice} from "@/util"
import {ContestApi} from "@/service"

export default {
  name: "AddProblems",
  components: {
    MarkdownIt
  },
  props: {
    contestId: Number,
    visibility: Boolean
  },
  watch: {
    visibility: {
      immediate: true,
      handler() {
        this.getProblems()
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
      previewDialog: {
        title: "",
        content: "",
        visible: false
      }
    }
  },
  methods: {
    getProblems() {
      ContestApi.getProblemsNotInContest(this.contestId, this.currentPage, this.pageSize, userInfo())
          .then((data) => {
            this.problems = data
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage(this)
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
    preview(row) {
      this.previewDialog = {
        title: `${row.problemId} - ${row.title}`,
        content: row.description,
        visible: true
      }
    },
    addProblemToContest(problemId, title) {
      ContestApi.addProblem(this.contestId, problemId, userInfo())
          .then(() => {
            Notice.notify.success(this, {
              title: `${title} 已添加`
            })
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage(this)
            } else {
              Notice.notify.error(this, {
                title: `${title} 添加失败`,
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