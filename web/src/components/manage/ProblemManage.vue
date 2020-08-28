<template>
  <div>
    <span class="table-title">题库管理</span>
    <el-divider></el-divider>
    <div style="align-self: flex-start">
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
    <el-table :data="problems.data" stripe border>
      <el-table-column label="ID" prop="problemId" width="100px" align="center">
      </el-table-column>
      <el-table-column label="题目名称" width="300px">
        <template slot-scope="scope">
          <el-link type="primary">
            <b>{{ scope.row.title }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center">
        <template slot-scope="scope">
          <div v-if="scope.row.category !== ''" style="">
            <span v-for="tag in scope.row.category.split(',')" v-bind:key="tag.index"
                  class="tag" :class="getTagColor(tag)">{{ tag }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="是否开放" width="100px" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enable"
                     active-color="#67C23A">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-dropdown style="margin-right: 10px"
                       @command="onEdit">
            <el-button size="mini"
                       icon="el-icon-edit-outline"
                       @click="currentId=scope.row.problemId">编辑
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="edit">编辑题目</el-dropdown-item>
              <el-dropdown-item command="test-data">管理测试数据</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="danger" icon="el-icon-delete"
                     @click="onDelete(scope.row.problemId)">
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="150px" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span style="margin-left: 5px">
            {{ scope.row.createAt }}
          </span>
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
  </div>
</template>

<script>
import {apiPath, getUserInfo, getTagColor} from "@/js/util"

export default {
  name: "ProblemManager",
  beforeMount() {
    document.title = `题库管理 · Cloud OJ`
    this.getProblems()
  },
  data() {
    return {
      userInfo: getUserInfo(),
      keyword: '',
      showKeyword: false,
      currentId: null,
      problems: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 25
    }
  },
  methods: {
    getTagColor,
    getProblems() {
      this.$axios({
        url: `${this.apiUrl}${apiPath.problemManage}`
            + `?page=${this.currentPage}&limit=${this.pageSize}`
            + `&userId=${this.userInfo.userId}&keyword=${this.keyword}`,
        method: 'get',
        headers: {
          'token': this.userInfo.token
        }
      }).then((res) => {
        this.problems = res.data
      }).catch((error) => {
        console.log(error)
      })
    },
    search() {
      this.currentPage = 1
      this.getProblems()
      this.showKeyword = true
    },
    onTagClose() {
      this.keyword = ''
      this.showKeyword = false
      this.getProblems()
    },
    onCurrentPageChange() {
      this.getProblems()
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getProblems()
    },
    onEdit(command) {
      if (command === 'edit') {
        // TODO 编辑题目
      } else if (command === 'test-data') {
        // TODO 管理测试数据
      }
    },
    onDelete() {
      // TODO 删除题目
    }
  }
}
</script>

<style scoped>

</style>