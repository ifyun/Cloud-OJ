<template>
  <el-container class="container">
    <el-page-header v-if="contestId != null" style="align-self: flex-start; margin-top: 5px; margin-bottom: 25px"
                    @back="back"
                    :content="contest.name">
    </el-page-header>
    <el-card style="width: 100%">
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
                    v-if="showKeyword" closable @close="onTagClose()">
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
              [{{ scope.row.problemId }}]&nbsp;<b>{{ scope.row.title }}</b>
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
            <div v-if="contestId == null">
              <div v-if="scope.row.category !== ''">
                <span v-for="tag in scope.row.category.split(',')"
                      v-bind:key="tag.index"
                      @click="onTagClick(tag)"
                      class="tag" :class="getTagColor(tag)">
                  {{ tag }}
                </span>
              </div>
            </div>
            <span v-else class="contest-tag tag-color-5">
              {{ contest.name }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="分数" width="90px" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row.score }}</b>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160px" align="right">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row.createAt }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" background layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="problems.count"
                     :current-page.sync="currentPage"
                     @size-change="getProblems" @current-change="getProblems">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {clearCachedCode, Notice, searchParams, tagColor, toLoginPage, userInfo} from "@/script/util"
import {resultTags} from "@/script/env"

export default {
  name: "ProblemList",
  props: ["contestId"],
  beforeMount() {
    this.loadPage()
    if (this.contestId != null) {
      this.getContest()
    }
    if (this.contest.id != null && userInfo() == null) {
      toLoginPage()
    }
    document.title = `${this.contest.id == null ? "题库" : this.contest.name} - Cloud OJ`
    clearCachedCode()
    this.getProblems()
  },
  data() {
    return {
      loading: true,
      contest: {
        id: null,
        name: null
      },
      problems: {
        data: [],
        count: 0
      },
      userInfo: userInfo(),
      pageSize: 15,
      currentPage: 1,
      keyword: "",
      showKeyword: false,
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
    getContest() {
      let contest = sessionStorage.getItem("contest")
      if (contest != null)
        this.contest = JSON.parse(contest)
    },
    getProblems() {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      let path = this.contest.id != null ? `contest/problem` : "problem"
      let headers = {}
      let params = {
        page: this.currentPage,
        limit: this.pageSize,
      }

      if (this.contestId != null) {
        params.contestId = this.contest.id
        headers = {
          userId: userInfo().userId,
          token: userInfo().token
        }
      }

      if (userInfo() != null) {
        params.userId = userInfo().userId
      }

      if (this.keyword !== "") {
        params.keyword = this.keyword
      }

      this.$axios({
        url: `api/manager/${path}`,
        method: "get",
        headers: headers,
        params: params
      }).then((res) => {
        this.problems = res.status === 200 ? res.data : {data: [], count: 0}
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          toLoginPage()
        }
        Notice.notify.error(this, {
          title: "获取题目失败",
          message: `${res.status} ${res.data === undefined ? res.statusText : res.data.msg}`
        })
      }).finally(() => {
        this.loading = false
      })
    },
    onTagClose() {
      this.keyword = ""
      this.showKeyword = false
      this.getProblems()
    },
    onTagClick(tag) {
      this.keyword = tag
      this.search()
    },
    search() {
      this.currentPage = 1
      this.showKeyword = true
      this.getProblems()
    },
    back() {
      window.history.back()
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
</style>