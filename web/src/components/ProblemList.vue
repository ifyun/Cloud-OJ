<template>
  <div style="width: 100%">
    <Error v-if="error.code != null" :error="error"/>
    <el-card v-else style="width: 100%">
      <h3 v-if="contest.contestId != null">{{ contest.contestName }}</h3>
      <div style="align-self: flex-start" v-if="contestId == null">
        <el-form :inline="true" @submit.native.prevent>
          <el-form-item>
            <el-input size="medium" placeholder="输入关键字" prefix-icon="el-icon-search"
                      v-model="keyword">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button size="medium" type="success" icon="el-icon-search"
                       @click="search(keyword)">搜索
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-tag type="success" size="medium"
                    v-if="keyword !== ''" closable @close="tagClose()">
              {{ keyword }}
            </el-tag>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="problems.data" stripe v-loading="loading">
        <el-table-column label="题目名称" width="280px">
          <template slot-scope="scope">
            <el-link
                :href="`/commit?problemId=${scope.row.problemId}${scope.row.contestId === undefined? '' : `&contestId=${scope.row.contestId}`}`">
              <span>[{{ scope.row.problemId }}]&nbsp;</span><b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column align="left" width="110px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.result !== undefined" size="small" effect="plain"
                    :type="resultTags[scope.row.result].type">
              <i :class="resultTags[scope.row.result].icon">
                {{ resultTags[scope.row.result].text }}
              </i>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="contestId == null && userInfo != null" width="100px" align="right">
          <template slot="header">
            <i class="el-icon-success el-icon--left"/>
            <span>通过人数</span>
          </template>
          <template slot-scope="scope">
          <span v-if="scope.row['passed'] !== undefined">
            {{ scope.row['passed'] }}
          </span>
          </template>
        </el-table-column>
        <el-table-column align="center">
          <template slot="header">
            <i class="el-icon-collection-tag el-icon--left"/>
            <span>分类</span>
          </template>
          <template slot-scope="scope">
            <div v-if="contest.contestId == null">
              <div v-if="scope.row.category !== ''">
                <span v-for="tag in scope.row.category.split(',')"
                      v-bind:key="tag.index"
                      @click="tagClick(tag)"
                      class="tag" :class="getTagColor(tag)">
                  {{ tag }}
                </span>
              </div>
            </div>
            <span v-else class="contest-tag tag-color-5">
              {{ contest.contestName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="分数" width="70px" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row.score }}</b>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="130px" align="right">
          <template slot-scope="scope">
            <i class="el-icon-date el-icon--left"></i>
            <span>{{ formatDate(scope.row["createAt"]) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" background layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="problems.count"
                     :current-page.sync="currentPage"
                     @size-change="getProblems" @current-change="getProblems">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import Error from "@/components/Error"
import {Notice, searchParams, tagColor, toLoginPage, userInfo} from "@/util"
import {resultTags} from "@/util/data"
import {ContestApi, ProblemApi} from "@/service"
import moment from "moment"

export default {
  name: "ProblemList",
  components: {
    Error
  },
  beforeMount() {
    sessionStorage.removeItem("code")
    this.loadPage()
    if (this.contestId != null) {
      this.getContest()
    } else {
      this.getProblems()
      document.title = "题库 - Cloud OJ"
    }

  },
  data() {
    return {
      loading: true,
      error: {
        code: null,
        msg: ""
      },
      contestId: searchParams().contestId,
      contest: {
        contestId: null,
        contestName: null
      },
      problems: {
        data: [],
        count: 0
      },
      userInfo: userInfo(),
      pageSize: 15,
      currentPage: 1,
      keyword: "",
      resultTags: resultTags
    }
  },
  methods: {
    getTagColor: tagColor,
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    formatDate(time) {
      return moment(time).format("YYYY/MM/DD")
    },
    getContest() {
      // Only logged-in users can view contest problems
      if (this.contestId != null && userInfo() == null) {
        toLoginPage()
      }
      ContestApi.get(this.contestId).then((data) => {
        document.title = `${data.contestName} - Cloud OJ`
        this.contest = data
        this.getProblems()
      }).catch((error) => {
        this.error = error
      })
    },
    getProblems() {
      let params = `?page=${this.currentPage}`
      if (this.contestId != null) {
        params += `&contestId=${this.contestId}`
      }
      history.pushState(null, "", params)
      this.loading = true
      let promise

      if (this.contest.contestId != null) {
        // Get problems from contest
        promise = ContestApi.getProblemsFromStarted(
            this.contestId,
            this.currentPage,
            this.pageSize,
            this.userInfo
        )
      } else {
        // Only get opened problems
        promise = ProblemApi.getAllOpened(
            this.currentPage,
            this.pageSize,
            this.keyword != null ? this.keyword : null,
            this.userInfo != null ? this.userInfo.userId : null
        )
      }

      promise.then((data) => {
        this.problems = data
      }).catch((error) => {
        if (error.code === 401) {
          toLoginPage()
        }
        Notice.notify.error(this, {
          title: "获取题目失败",
          message: `${error.code} ${error.msg}`
        })
      }).finally(() => {
        this.loading = false
      })
    },
    search() {
      this.currentPage = 1
      this.getProblems()
    },
    tagClose() {
      this.keyword = ""
      this.getProblems()
    },
    tagClick(tag) {
      this.keyword = tag
      this.search()
    }
  }
}
</script>

<style scoped>
h3 {
  color: #303133;
  margin-top: 0;
}
</style>