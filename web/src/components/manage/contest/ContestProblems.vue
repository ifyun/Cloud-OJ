<template>
  <div>
    <el-alert type="info" show-icon style="margin-bottom: 20px"
              title="只能添加未开放的题目"
              description="从竞赛/作业中移除题目并不会删除题目"
              :closable="false">
    </el-alert>
    <el-button type="success" size="medium" icon="el-icon-circle-plus"
               @click="problemsDialogVisible = true">添加题目
    </el-button>
    <el-table style="margin-top: 10px" :data="problems.data" border v-loading="loading">
      <el-table-column label="ID" prop="problemId" width="100px" align="center">
      </el-table-column>
      <el-table-column label="题目名称">
        <template slot-scope="scope">
          <el-popover trigger="click" placement="right-end">
            <pre class="desc">{{ scope.row.description }}</pre>
            <el-button slot="reference" size="medium">{{ scope.row.title }}
            </el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="分值" prop="score" width="70px" align="right">
      </el-table-column>
      <el-table-column label="操作" width="100px" align="center">
        <template slot-scope="scope">
          <el-popconfirm
              title="确定要移除吗？"
              icon="el-icon-info"
              confirm-button-type="danger"
              confirm-button-text="移除"
              cancel-button-text="取消"
              @onConfirm="deleteProblem(scope.row.problemId, scope.row.title)">
            <el-button type="danger" size="mini"
                       icon="el-icon-delete"
                       slot="reference">
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        style="margin-top: 10px"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[15, 25, 35]"
        :page-size.sync="pageSize"
        :total="problems.count"
        :current-page.sync="currentPage"
        @size-change="getProblems"
        @current-change="getProblems">
    </el-pagination>
    <el-dialog title="添加题目" append-to-body width="800px"
               :visible.sync="problemsDialogVisible"
               @close="getProblems">
      <AddProblems :contest-id="contestId" :visibility="problemsDialogVisible"/>
    </el-dialog>
  </div>
</template>

<script>
import {userInfo, toLoginPage, Notice} from "@/script/util"
import {apiPath} from "@/script/env"
import AddProblems from "@/components/manage/contest/AddProblems"

export default {
  name: "CompetitionProblemsManager",
  components: {
    AddProblems
  },
  props: {
    contestId: Number
  },
  watch: {
    contestId: {
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
      problemsDialogVisible: false
    }
  },
  methods: {
    getProblems() {
      this.loading = true
      this.$axios({
        url: `${apiPath.contest}/pro/${this.contestId}/problem`,
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
        this.problems = res.status === 200 ? res.data : {data: [], count: 0}
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: `获取数据失败`,
          message: `${res.status} ${res.statusText}`
        })
      }).finally(() => {
        this.loading = false
      })
    },
    deleteProblem(problemId, title) {
      this.$axios({
        url: `${apiPath.contestManage}/problem/${this.contestId}/${problemId}`,
        method: 'delete',
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId
        },
      }).then((res) => {
        Notice.notify.success(this, {
          title: `【${title}】移除成功`,
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: `【${title}】移除失败`,
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.getProblems()
      })
    }
  }
}
</script>

<style scoped>
.desc {
  max-width: 500px;
  max-height: 500px;
  white-space: pre-wrap;
}
</style>