<template>
  <div style="width: 100%">
    <Error v-if="error.code != null" :error="error"/>
    <el-card v-else style="width: 100%">
      <h3 v-if="contest.contestId != null">{{ contest.contestName }}</h3>
      <div style="align-self: flex-start" v-if="contestId == null">
        <el-form size="medium" :inline="true" @submit.native.prevent>
          <el-form-item>
            <el-input style="width: 250px" placeholder="输入关键字" prefix-icon="el-icon-search"
                      v-model="keyword">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" icon="el-icon-search" @click="search(keyword)">
              搜索
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-tag v-if="keyword !== ''" type="success" size="medium" closable @close="tagClose()">
              {{ keyword }}
            </el-tag>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="problems.data" stripe v-loading="loading">
        <el-table-column label="#" align="center" type="index" :index="(currentPage - 1) * pageSize + 1">
        </el-table-column>
        <el-table-column label="题目名称">
          <template slot-scope="scope">
            <el-link :href="generateLink(scope.row)">
              {{ scope.row.problemId }}&nbsp;<b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column v-if="userInfo != null" label="状态" align="center" width="105px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.result !== undefined" size="small" effect="light"
                    :type="resultTags[scope.row.result].type">
              <i class="el-icon--left" :class="resultTags[scope.row.result].icon"/>
              <span>{{ resultTags[scope.row.result].text }}</span>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="contestId == null && userInfo != null"
                         label="通过人数" width="120px" align="right">
          <template slot-scope="scope">
          <span v-if="scope.row['passed'] !== undefined" style="color: #67c23a; font-size: 6px">
            <span>{{ scope.row['passed'] }}</span>
            <i class="el-icon-success el-icon--right"/>
          </span>
          </template>
        </el-table-column>
        <el-table-column width="50px"/>
        <el-table-column label="分类" v-if="contest.contestId == null">
          <template slot-scope="scope">
            <div v-if="scope.row.category !== ''">
              <span v-for="tag in scope.row.category.split(',')" v-bind:key="tag.index"
                    class="tag" :class="getTagColor(tag)" @click="tagClick(tag)">
                  {{ tag }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分数" align="right">
          <template slot-scope="scope">
            {{ scope.row.score }} 分
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px" layout="total, prev, pager, next"
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
      this.siteSetting.setTitle("题库")
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
      resultTags
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
    generateLink(row) {
      const contestId = typeof row.contestId === "undefined" ?
          "" : `&contestId=${row.contestId}`
      return `/commit?problemId=${row.problemId}${contestId}`
    },
    getContest() {
      // Only logged-in users can view contest problems
      if (this.contestId != null && userInfo() == null) {
        toLoginPage(this)
      }
      ContestApi.get(this.contestId).then((data) => {
        this.siteSetting.setTitle(data.contestName)
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
        // Get all opened problems
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
          toLoginPage(this)
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