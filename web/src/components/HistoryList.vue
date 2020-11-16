<template>
  <el-container class="container">
    <el-page-header style="align-self: flex-start; margin-top: 5px; margin-bottom: 25px" content="提交记录" @back="back">
    </el-page-header>
    <el-card style="width: 100%">
      <el-table :data="histories.data" v-loading="loading">
        <el-table-column label="题目" width="300px">
          <template slot-scope="scope">
            <el-link @click="titleClick(scope.row)">
              <b>[{{ scope.row.problemId }}]&nbsp;{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="结果" align="center">
          <template slot-scope="scope">
            <el-tooltip content="点击查看代码" placement="right">
              <el-tag style="cursor: pointer" effect="plain"
                      :type="resultTags[scope.row.result].type"
                      @click="showCode(scope.row.code)">
                <i :class="resultTags[scope.row.result].icon">
                  {{ resultTags[scope.row.result].text }}
                </i>
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="编程语言" align="center">
          <template slot-scope="scope">
            <img class="language-icon" :src="languages[scope.row['language']].icon"
                 align="center" alt="language">
          </template>
        </el-table-column>
        <el-table-column label="耗时" width="100px" align="right">
          <template slot-scope="scope">
            <span v-if="scope.row.result <= 4">{{ scope.row['time'] }}&nbsp;ms</span>
          </template>
        </el-table-column>
        <el-table-column label="内存" width="100px" align="right">
          <template slot-scope="scope">
            <span v-if="scope.row.result <= 4">{{ prettyMemory(scope.row['memory']) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="得分" width="100px" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row['score'] }}</b>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180px" align="right">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['submitTime'] }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background :hide-on-single-page="true"
                     layout="total, sizes, prev, pager, next"
                     :page-sizes="[10, 20, 30]"
                     :page-size.sync="pageSize"
                     :total="histories.count"
                     :current-page.sync="currentPage"
                     @size-change="getHistories"
                     @current-change="getHistories">
      </el-pagination>
    </el-card>
    <el-dialog title="代码预览" :visible.sync="codeDialogVisible">
      <pre class="sample">{{ code }}</pre>
    </el-dialog>
  </el-container>
</template>

<script>
import {Notice, toLoginPage, userInfo} from "@/script/util"
import {apiPath, resultTags} from "@/script/env"

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
      pageSize: 10,
      resultTags: resultTags,
      languages: {
        0: {name: 'C', icon: './icons/lang/c.svg'},
        1: {name: 'C++', icon: './icons/lang/cpp.svg'},
        2: {name: 'Java', icon: './icons/lang/java.svg'},
        3: {name: 'Python', icon: './icons/lang/python.svg'},
        4: {name: 'Bash', icon: './icons/lang/bash.svg'},
        5: {name: 'C#', icon: './icons/lang/csharp.svg'},
        6: {name: 'JavaScript', icon: './icons/lang/js.svg'},
        7: {name: 'Kotlin', icon: './icons/lang/kotlin.svg'}
      },
      codeDialogVisible: false,
      code: ''
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
          token: userInfo().token,
          userId: userInfo().userId
        },
        params: {
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.histories = res.status === 200 ? res.data : {data: [], count: 0}
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        } else {
          Notice.notify.error(this, {
            title: '获取提交记录失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    showCode(code) {
      this.code = code
      this.codeDialogVisible = true
    },
    titleClick(solution) {
      window.sessionStorage.setItem('code', JSON.stringify({
        language: solution['language'],
        code: solution['code']
      }))
      window.location.href = `/commit?problemId=${solution['problemId']}`
    },
    prettyMemory(mem) {
      if (mem <= 1024)
        return `${mem} KB`
      else
        return `${(mem / 1024).toFixed(2)} MB`
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}

.language-icon {
  height: 20px;
  margin-right: 5px;
}
</style>