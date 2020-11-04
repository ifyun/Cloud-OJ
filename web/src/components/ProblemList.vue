<template>
  <el-container class="container">
    <el-page-header v-if="contestId != null" style="align-self: flex-start; margin-top: 5px"
                    @back="back"
                    :content="contestName">
    </el-page-header>
    <el-card style="width: 100%; margin-top: 25px">
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
      <el-table :data="problems.data" stripe v-loading="loading">
        <el-table-column label="题目名称" width="280px">
          <template slot-scope="scope">
            <el-link
                :href="`/commit?problemId=${scope.row.problemId}${scope.row.contestId === undefined? '' : `&contestId=${scope.row.contestId}`}`">
              <b>[{{ scope.row.problemId }}]&nbsp;{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column align="left" width="110px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.result !== undefined"
                    style="cursor: pointer" effect="plain"
                    :type="resultTags[scope.row.result].type">
              <i :class="resultTags[scope.row.result].icon">
                {{ resultTags[scope.row.result].text }}
              </i>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="contestId == null" label="通过人数" width="90px" align="right">
          <template slot-scope="scope">
          <span v-if="scope.row['passed'] !== undefined">
            {{ scope.row['passed'] }} 人通过
          </span>
          </template>
        </el-table-column>
        <el-table-column label="分类" align="center">
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
            <el-tag v-else effect="dark" type="success">
              {{ contestName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分数" prop="score" width="90px" align="right">
        </el-table-column>
        <el-table-column label="创建时间" width="160px" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row.createAt }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background
                     layout="total, sizes, prev, pager, next, jumper"
                     :page-sizes="[10, 20, 30, 50]"
                     :page-size.sync="pageSize"
                     :total="problems.count"
                     :current-page.sync="currentPage"
                     @size-change="getProblems"
                     @current-change="getProblems">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {getTagColor, userInfo} from '@/script/util'

export default {
  name: "ProblemList",
  props: ['contestId', 'contestName'],
  mounted() {
    document.title = `${this.contestId == null ? '题库' : this.contestName} · Cloud OJ`
    this.getProblems()
  },
  data() {
    return {
      loading: true,
      problems: {
        data: [],
        count: 0
      },
      pageSize: 10,
      currentPage: 1,
      keyword: '',
      showKeyword: false,
      resultTags: [
        {text: '完全正确', type: 'success', icon: 'el-icon-success'},
        {text: '时间超限', type: 'warning', icon: 'el-icon-warning'},
        {text: '部分通过', type: 'warning', icon: 'el-icon-warning'},
        {text: '答案错误', type: 'danger', icon: 'el-icon-error'},
        {text: '编译错误', type: 'info', icon: 'el-icon-info'},
        {text: '运行错误', type: 'info', icon: 'el-icon-info'},
        {text: '判题异常', type: 'error', icon: 'el-icon-error'}
      ]
    }
  },
  methods: {
    getTagColor,
    getProblems() {
      this.loading = true
      let userId = userInfo() != null ? userInfo().userId : ''
      let path = this.contestId != null ? `contest/${this.contestId}/problem` : 'problem'
      let params = {
        page: this.currentPage,
        limit: this.pageSize,
        userId: userId
      }
      if (this.keyword !== '')
        params.keyword = this.keyword
      this.$axios({
        url: `api/manager/${path}`,
        method: 'get',
        params: params
      }).then((res) => {
        this.loading = false
        this.problems = res.data
      }).catch((error) => {
        this.loading = false
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: '获取题目失败',
          message: `${res.status} ${res.data === undefined ? res.statusText : res.data.msg}`
        })
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
  margin-top: 25px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>