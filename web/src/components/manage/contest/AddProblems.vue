<template>
  <div>
    <el-alert type="info" show-icon style="margin-bottom: 10px"
              title="点击名称可预览题目内容"
              :closable="false">
    </el-alert>
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
      <el-table-column label="分值" prop="score" width="70px" align="right-end">
      </el-table-column>
      <el-table-column label="操作" width="100px" align="center">
        <template slot-scope="scope">
          <el-button type="success" size="mini"
                     icon="el-icon-circle-plus"
                     @click="addProblemToContest(scope.row.problemId, scope.row.title)">添加
          </el-button>
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
  </div>
</template>

<script>
import {apiPath, userInfo, handle401} from "@/js/util";

export default {
  name: "AddProblems",
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
      currentPage: 1
    }
  },
  methods: {
    getProblems() {
      this.$axios({
        url: `${apiPath.contestManage}`
            + `/${this.contestId}?page=${this.currentPage}&limit=${this.pageSize}`
            + `&userId=${userInfo().userId}`,
        method: 'get',
        headers: {
          'token': userInfo().token
        }
      }).then((res) => {
        this.problems = res.data
      }).catch((error) => {
        if (error.response.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: `获取数据失败`,
            message: `${error.response.status}`
          })
        }
      })
    },
    addProblemToContest(problemId, title) {
      this.$axios({
        url: `${apiPath.contestManage}/problem/`
            + `${this.contestId}/${problemId}?userId=${userInfo().userId}`,
        method: 'post',
        headers: {
          'token': userInfo().token
        }
      }).then((res) => {
        this.$notify({
          type: 'success',
          title: `${title}已添加`,
          message: `${res.status}`
        })
        this.getProblems()
      }).catch((error) => {
        if (error.response.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: `${title}添加失败`,
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
  max-width: 500px;
  max-height: 500px;
  white-space: pre-wrap;
}
</style>