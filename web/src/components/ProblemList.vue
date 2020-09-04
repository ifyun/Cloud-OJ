<template>
  <el-container class="container">
    <span class="table-title" v-if="contestId == null">开放题库</span>
    <el-page-header v-else
                    style="align-self: flex-start; margin-top: 10px;"
                    @back="back"
                    :content="contestName">
    </el-page-header>
    <el-divider></el-divider>
    <div style="align-self: flex-start" v-if="contestId == null">
      <el-form :inline="true" @submit.native.prevent>
        <el-form-item>
          <el-input placeholder="输入关键字" prefix-icon="el-icon-search"
                    v-model="keyword">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search"
                     @click="search(keyword)">搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-tag type="success"
                  v-if="showKeyword"
                  closable
                  @close="onTagClose()">{{ keyword }}
          </el-tag>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="problems.data" stripe>
      <el-table-column label="ID" prop="problemId" width="120px" align="center">
      </el-table-column>
      <el-table-column label="题目名称" width="300px">
        <template slot-scope="scope">
          <el-link type="primary"
                   :href="`/commit?problemId=${scope.row.problemId}${scope.row.contestId === undefined? '' : `&contestId=${scope.row.contestId}`}`">
            <b>{{ scope.row.title }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="通过人数" prop="passed" width="100px" align="center">
      </el-table-column>
      <el-table-column label="分类" align="center">
        <template slot-scope="scope">
          <div v-if="scope.row.category !== ''">
            <span v-for="tag in scope.row.category.split(',')"
                  v-bind:key="tag.index"
                  @click="onTagClick(tag)"
                  class="tag" :class="getTagColor(tag)">{{ tag }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="150px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.createAt }}</i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[10, 25, 50, 100]"
                   :page-size="pageSize"
                   :total="problems.count"
                   :current-page.sync="currentPage"
                   @size-change="onSizeChange"
                   @current-change="onCurrentPageChange">
    </el-pagination>
  </el-container>
</template>

<script>
import {getTagColor, userInfo} from '@/js/util'

export default {
  name: "ProblemList",
  props: ['contestId', 'contestName', 'tableTitle', 'apiPath'],
  beforeMount() {
    document.title = `${this.contestId == null ? '开放题库' : this.contestName} · Cloud OJ`
    this.getProblems()
  },
  data() {
    return {
      userInfo: userInfo(),
      problems: {
        data: [],
        count: 0
      },
      pageSize: 25,
      currentPage: 1,
      keyword: '',
      showKeyword: false
    }
  },
  methods: {
    getTagColor,
    getProblems() {
      let userId = this.userInfo != null ? this.userInfo.userId : ''
      let path = this.contestId != null ? `contest/${this.contestId}` : 'problem'
      this.$axios({
        url: `api/manager/${path}?userId=${userId}` +
            `&page=${this.currentPage}&limit=${this.pageSize}&keyword=${this.keyword}`,
        method: 'get'
      }).then((res) => {
        this.problems = res.data
      }).catch((error) => {
        console.log(error)
      })
    },
    onTagClose() {
      this.keyword = ''
      this.showKeyword = false
      this.getProblems()
    },
    onTagClick(tag) {
      this.keyword = tag
      this.search()
    },
    onCurrentPageChange(page) {
      this.getProblems(page)
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getProblems()
    },
    search() {
      this.currentPage = 1
      this.getProblems()
      this.showKeyword = true
    },
    back() {
      window.history.back()
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 20px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}

.table-title {
  align-self: flex-start;
  font-size: 14pt;
}
</style>