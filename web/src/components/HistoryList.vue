<template>
  <div class="content">
    <div style="width: 100%">
      <el-form style="margin-top: 15px" size="medium" :inline="true" @submit.native.prevent>
        <el-form-item>
          <el-input style="width: 350px" v-model="searchOption.value" placeholder="请选择搜索方式"
                    @keyup.enter.native="getHistories">
            <el-select style="width: 110px" slot="prepend" v-model="searchOption.type">
              <el-option label="题目 ID" value="problemId"></el-option>
              <el-option label="题目名称" value="title"></el-option>
            </el-select>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="el-icon-search" @click="getHistories">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
      <el-table :data="histories.data" stripe v-loading="loading">
        <el-table-column label="状态" width="140px">
          <template slot-scope="scope">
            <el-tag class="result-tag" size="small" effect="light"
                    :type="resultTag(scope.row).type" @click="resultClick(scope.row)">
              <i class="el-icon--left" :class="resultTag(scope.row).icon"/>
              <span>{{ resultTag(scope.row).text }}</span>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="题目" width="320px">
          <template slot-scope="scope">
            <el-link @click="titleClick(scope.row)">
              {{ scope.row.problemId }}&nbsp;<b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="语言">
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
        <el-table-column label="得分" prop="score" align="right">
        </el-table-column>
        <el-table-column width="120px" align="right">
          <template slot="header">
            <i class="el-icon-date el-icon--left"/>
            <span>提交时间</span>
          </template>
          <template slot-scope="scope">
            {{ scope.row['submitTime'] }}
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="histories.count"
                     :current-page.sync="currentPage"
                     @size-change="getHistories" @current-change="getHistories">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {Notice, prettyMemory, searchParams, toLoginPage, userInfo} from "@/util"
import {languages, resultTags, stateTags} from "@/util/data"
import {UserApi} from "@/service"

export default {
  name: "HistoryList",
  beforeMount() {
    sessionStorage.removeItem("code")
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
      pageSize: 15,
      languages,
      codeDialogVisible: false,
      code: "",
      prettyMemory,
      searchOption: {
        type: "title",
        value: "",
      }
    }
  },
  methods: {
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    getHistories() {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      const type = this.searchOption.type
      const value = this.searchOption.value
      const searchParam = {
        problemId: type === "problemId" && value !== "" ? value : null,
        title: type === "title" && value !== "" ? value : null
      }
      UserApi.getCommitHistory(this.currentPage, this.pageSize, searchParam, userInfo())
          .then((data) => {
            this.histories = data
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage(this)
            } else {
              Notice.notify.error(this, {
                title: "获取提交记录失败",
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.loading = false
          })
    },
    titleClick(solution) {
      window.sessionStorage.setItem("code", JSON.stringify({
        problemId: solution.problemId,
        language: solution.language,
        content: solution.code
      }))
      window.location.href = `/commit?problemId=${solution["problemId"]}`
    },
    resultClick(row) {
      const error = row["errorInfo"]
      if (error !== "") {
        const h = this.$createElement
        this.$msgbox({
          title: "错误信息",
          message: h("pre", {class: "result-error"}, error),
          customClass: "error-info"
        })
      }
    },
    resultTag(row) {
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
.content {
  width: 100%;
}

.language-icon {
  height: 22px;
  width: 22px;
  margin-right: 5px;
}
</style>