<template>
  <el-container class="container">
    <el-page-header style="align-self: flex-start; margin-top: 10px;"
                    content="提交记录"
                    @back="back">
    </el-page-header>
    <el-divider></el-divider>
    <el-card style="width: 100%">
      <el-table :data="histories.data" v-loading="loading">
        <el-table-column label="题目ID" prop="problemId" width="120px" align="center">
        </el-table-column>
        <el-table-column label="题目名称">
          <template slot-scope="scope">
            <el-link type="primary"
                     :href="`/commit?problemId=${scope.row.problemId}`">
              <b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="语言">
          <template slot-scope="scope">
            {{ languages[scope.row.language] }}
          </template>
        </el-table-column>
        <el-table-column label="结果" width="120px" align="center">
          <template slot-scope="scope">
            <el-tooltip content="点击查看代码" placement="right">
              <el-tag style="cursor: pointer" effect="plain"
                      :type="resultTags[scope.row.result].type">
                <i :class="resultTags[scope.row.result].icon">
                  {{ resultTags[scope.row.result].text }}
                </i>
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="得分" width="100px" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row.score }}</b>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180px" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['submitTime'] }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background
                     layout="total, sizes, prev, pager, next, jumper"
                     :page-sizes="[10, 25, 50, 100]"
                     :page-size.sync="pageSize"
                     :total="histories.count"
                     :current-page.sync="currentPage"
                     @size-change="getHistories"
                     @current-change="getHistories">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {apiPath, handle401, userInfo} from "@/js/util";

export default {
  name: "HistoryList",
  mounted() {
    this.getHistories()
  },
  data() {
    return {
      loading: true,
      histories: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 25,
      resultTags: [
        {text: '完全正确', type: 'success', icon: 'el-icon-success'},
        {text: '时间超限', type: 'warning', icon: 'el-icon-warning'},
        {text: '部分通过', type: 'warning', icon: 'el-icon-warning'},
        {text: '答案错误', type: 'danger', icon: 'el-icon-error'},
        {text: '编译错误', type: 'info', icon: 'el-icon-info'}
      ],
      languages: {
        0: 'C',
        1: 'C++',
        2: 'Java',
        3: 'Python',
        4: 'Bash Shell',
        5: 'C#'
      }
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    getHistories() {
      this.loading = true
      this.$axios({
        url: apiPath.history,
        method: 'get',
        headers: {
          token: userInfo().token
        },
        params: {
          userId: userInfo().userId,
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.histories = res.data
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: '获取提交记录失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 25px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>