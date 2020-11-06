<template>
  <div>
    <el-alert type="info" show-icon style="margin-bottom: 20px"
              title="只能添加未开放的题目"
              description="从竞赛/作业中移除题目并不会删除题目"
              :closable="false">
    </el-alert>
    <el-button type="success" size="medium" icon="el-icon-circle-plus"
               @click="showProblemsDialog = true">添加题目
    </el-button>
    <el-table :data="problems.data" border style="margin-top: 10px">
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
        :page-sizes="[10, 20, 30]"
        :page-size.sync="pageSize"
        :total="problems.count"
        :current-page.sync="currentPage"
        @size-change="getProblems"
        @current-change="getProblems">
    </el-pagination>
    <el-dialog title="添加题目" append-to-body width="800px"
               :visible.sync="showProblemsDialog"
               @close="getProblems">
      <AddProblems :contest-id="contestId"/>
    </el-dialog>
  </div>
</template>

<script>
import {apiPath, userInfo, toLoginPage} from "@/script/util"
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
      problems: {
        data: [],
        count: 0
      },
      pageSize: 10,
      currentPage: 1,
      showProblemsDialog: false
    }
  },
  methods: {
    getProblems() {
      this.$axios({
        url: `${apiPath.contest}/pro/${this.contestId}/problem`,
        method: 'get',
        headers: {
          'token': userInfo().token
        },
        params: {
          userId: userInfo().userId,
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.problems = res.data
      }).catch((error) => {
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: `获取数据失败`,
          message: `${res.status} ${res.statusText}`
        })
      })
    },
    deleteProblem(problemId, title) {
      this.$axios({
        url: `${apiPath.contestManage}/problem/${this.contestId}/${problemId}`,
        method: 'delete',
        headers: {
          'token': userInfo().token
        },
        params: {
          userId: userInfo().userId
        }
      }).then((res) => {
        this.$notify({
          offset: 50,
          title: `【${title}】移除成功`,
          type: 'success',
          message: `Status: ${res.status}`
        })
      }).catch((error) => {
        if (error.response.status === 401) {
          toLoginPage()
        } else {
          this.$notify.error({
            offset: 50,
            title: `【${title}】移除失败`,
            message: `${error.response.status}`
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