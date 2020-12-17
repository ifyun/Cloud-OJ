<template>
  <el-container class="container">
    <el-page-header style="align-self: flex-start; margin-top: 5px; margin-bottom: 25px" content="提交记录" @back="back">
    </el-page-header>
    <el-card style="width: 100%">
      <el-table :data="histories.data" stripe v-loading="loading">
        <el-table-column label="题目名称" width="300px">
          <template slot-scope="scope">
            <el-link @click="titleClick(scope.row)">
              [{{ scope.row.problemId }}]&nbsp;<b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="结果" align="center">
          <template slot-scope="scope">
            <el-tag size="small" effect="plain" :type="getResultTag(scope.row).type">
              <i :class="getResultTag(scope.row).icon">
                {{ getResultTag(scope.row).text }}
              </i>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="语言" align="center">
          <template slot-scope="scope">
            <img class="language-icon" :src="languages[scope.row['language']].icon"
                 align="center" alt="language">
          </template>
        </el-table-column>
        <el-table-column align="right">
          <template slot="header">
            <i class="el-icon-time el-icon--left"/>
            <span>耗时</span>
          </template>
          <template slot-scope="scope">
            <span v-if="scope.row.result <= 4">{{ scope.row['time'] }}&nbsp;ms</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column align="right">
          <template slot="header">
            <i class="el-icon-cpu el-icon--left"/>
            <span>内存占用</span>
          </template>
          <template slot-scope="scope">
            <span v-if="scope.row.result <= 4">{{ prettyMemory(scope.row['memory']) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="得分" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row['score'] }}</b>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="150px" align="right">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['submitTime'] }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" background layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="histories.count"
                     :current-page.sync="currentPage"
                     @size-change="getHistories" @current-change="getHistories">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {clearCachedCode, Notice, searchParams, toLoginPage, userInfo} from "@/script/util"
import {apiPath, resultTags, stateTags} from "@/script/env"

export default {
  name: "HistoryList",
  beforeMount() {
    clearCachedCode()
    this.loadPage()
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
      languages: {
        0: {name: "C", icon: "./icons/lang/c.svg"},
        1: {name: "C++", icon: "./icons/lang/cpp.svg"},
        2: {name: "Java", icon: "./icons/lang/java.svg"},
        3: {name: "Python", icon: "./icons/lang/python.svg"},
        4: {name: "Bash", icon: "./icons/lang/bash.svg"},
        5: {name: "C#", icon: "./icons/lang/csharp.svg"},
        6: {name: "JavaScript", icon: "./icons/lang/js.svg"},
        7: {name: "Kotlin", icon: "./icons/lang/kotlin.svg"}
      },
      codeDialogVisible: false,
      code: ""
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    getHistories() {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      this.$axios({
        url: apiPath.history,
        method: "get",
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
            title: "获取提交记录失败",
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    titleClick(solution) {
      window.sessionStorage.setItem("code", JSON.stringify({
        language: solution["language"],
        code: solution["code"]
      }))
      window.location.href = `/commit?problemId=${solution["problemId"]}`
    },
    prettyMemory(mem) {
      if (mem <= 1024)
        return `${mem} KB`
      else
        return `${(mem / 1024).toFixed(2)} MB`
    },
    getResultTag(row) {
      if (row.state === 0) {
        return resultTags[row.result]
      } else {
        return stateTags[row.state]
      }
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