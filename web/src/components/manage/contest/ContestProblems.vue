<template>
  <div>
    <el-alert type="info" show-icon style="margin-bottom: 10px"
              title="只能添加未开放的题目"
              description="从竞赛/作业中移除题目并不会删除题目"
              :closable="false">
    </el-alert>
    <el-form>
      <el-form-item>
        <el-button type="primary" size="medium"
                   @click="showProblemsDialog = true">添加题目
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="problems.data" border>
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
              confirm-button-text="删除"
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
        :page-size="pageSize"
        :total="problems.count"
        :current-page.sync="currentPage"
        @size-change="onSizeChange"
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
import {apiPath, getUserInfo, handle401} from "@/js/util"
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
      userInfo: getUserInfo(),
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
        url: `${apiPath.contest}` +
            `/${this.contestId}?page=${this.currentPage}&limit=${this.pageSize}`,
        method: 'get'
      }).then((res) => {
        this.problems = res.data
      }).catch((error) => {
        this.$notify.error({
          title: `获取数据失败`,
          message: `${error.response.status}`
        })
      })
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getProblems()
    },
    deleteProblem(problemId, title) {
      this.$axios({
        url: `${apiPath.contestManage}/problem/`
            + `${this.contestId}/${problemId}?userId=${this.userInfo.userId}`,
        method: 'delete',
        headers: {
          'token': this.userInfo.token
        }
      }).then((res) => {
        this.$notify({
          title: `${title}移除成功`,
          type: 'success',
          message: `Status: ${res.status}`
        })
        this.getProblems()
      }).catch((error) => {
        if (error.response.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: `${title}移除失败`,
            message: `${error.response.status}`
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.desc {
  width: 600px;
  white-space: pre-wrap;
}
</style>