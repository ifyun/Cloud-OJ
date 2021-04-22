<template>
  <div style="width: 100%">
    <el-form class="toolbar" size="medium" :inline="true" @submit.native.prevent>
      <div v-if="!singleMode" style="display: inline-block">
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
      </div>
      <el-form-item v-else>
        <span style="font-size: 16px">{{ `${problemId}.${title}` }}</span>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button icon="el-icon-refresh" size="mini" @click="getHistories">
          刷新
        </el-button>
      </el-form-item>
    </el-form>
    <el-alert v-if="singleMode && histories.count > 0" show-icon type="info"
              style="margin-top: 10px" title="双击行可以加载代码到编辑框。"/>
    <el-table style="margin-top: 10px" stripe v-loading="loading" :data="histories.data"
              :size="singleMode ? 'small' : 'medium'" @row-dblclick="rowDbClick">
      <el-table-column label="状态" width="105px">
        <template slot-scope="scope">
          <el-tag class="result-tag" size="small" effect="light"
                  :type="resultTag(scope.row).type" @click="resultClick(scope.row)">
            <i class="el-icon--left" :class="resultTag(scope.row).icon"/>
            <span>{{ resultTag(scope.row).text }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="!singleMode" label="题目" width="320px">
        <template slot-scope="scope">
          <el-link @click="titleClick(scope.row)">
            {{ scope.row.problemId }}&nbsp;<b>{{ scope.row.title }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="语言" align="center">
        <template slot-scope="scope">
          <el-tooltip v-if="scope.row.type === 0" :content="languages[scope.row['language']].name" placement="right">
            <el-button type="text" size="mini">
              <img class="language-icon" align="center" :src="languages[scope.row['language']].icon"
                   :alt="languages[scope.row['language']].name">
            </el-button>
          </el-tooltip>
          <img v-else class="language-icon" align="center"
               :src="sqlTypes[scope.row['language']].icon" :alt=" sqlTypes[scope.row['language']].name">
        </template>
      </el-table-column>
      <el-table-column label="耗时" align="right">
        <template slot-scope="scope">
          <span v-if="scope.row.result <= 4">{{ scope.row['time'] }}&nbsp;ms</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="内存占用" align="right">
        <template slot-scope="scope">
          <span v-if="scope.row.result <= 4">{{ prettyMemory(scope.row['memory']) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="得分" prop="score" align="right">
      </el-table-column>
      <el-table-column label="提交时间" align="right">
        <template slot-scope="scope">
          <el-tooltip placement="left" :content="formatTime(scope.row['submitTime'])">
            <el-button type="text" :size="singleMode ? 'small' : 'medium'">{{
                formatDate(scope.row["submitTime"])
              }}
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px" layout="total, prev, pager, next"
                   :page-size.sync="pageSize" :total="histories.count"
                   :current-page.sync="currentPage"
                   @size-change="getHistories" @current-change="getHistories">
    </el-pagination>
  </div>
</template>

<script>
import {Notice, prettyMemory, toLoginPage, userInfo} from "@/util"
import {languages, sqlTypes, resultTags, stateTags} from "@/util/data"
import {UserApi} from "@/service"
import moment from "moment";

export default {
  name: "HistoryList",
  beforeMount() {
    sessionStorage.removeItem("code")
    if (this.singleMode) {
      this.searchOption = {
        type: "problemId",
        value: this.problemId
      }
    }
    this.getHistories()
  },
  props: {
    /* 单题目模式，仅显示某一题目的记录 */
    singleMode: Boolean,
    problemId: String,
    title: String
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
      sqlTypes,
      prettyMemory,
      searchOption: {
        type: "title",
        value: "",
      }
    }
  },
  methods: {
    rowDbClick(row) {
      this.$emit("changeCode", row.code, row.language)
    },
    getHistories() {
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
    },
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD")
    },
    formatTime(time) {
      return moment(time).format("HH:mm:ss")
    }
  }
}
</script>

<style scoped>
.language-icon {
  height: 20px;
  width: 20px;
}

.toolbar .el-form-item {
  margin-bottom: 0;
}

.toolbar *:last-child {
  margin-right: 0;
}
</style>