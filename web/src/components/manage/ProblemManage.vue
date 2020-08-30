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
        <el-form-item style="float: right">
          <el-button type="primary" icon="el-icon-circle-plus">添加题目</el-button>
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
            <span v-for="tag in scope.row.category.split(',')"
                  v-bind:key="tag.index"
                  @click="onTagClick(tag)"
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
                       @command="onEditClick">
            <el-button size="mini"
                       icon="el-icon-edit-outline"
                       @click="selectedId=scope.row.problemId">
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="0">编辑题目</el-dropdown-item>
              <el-dropdown-item command="1">管理测试数据</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="danger" icon="el-icon-delete"
                     @click="onDeleteClick(scope.row)">
          </el-button>
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
                   :page-sizes="[10, 25, 50]"
                   :page-size="pageSize"
                   :total="problems.count"
                   :current-page.sync="currentPage"
                   @size-change="onSizeChange"
                   @current-change="onCurrentPageChange">
    </el-pagination>
    <el-dialog title="提示" :visible.sync="showDeleteDialog">
      <el-alert type="warning" show-icon
                :title="`你正在删除题目：${selectedTitle}`"
                description="与该题目相关的提交记录也会被删除!"
                :closable="false">
      </el-alert>
      <el-form>
        <el-form-item label="输入题目名称确认删除">
          <el-input v-model="checkTitle" :placeholder="selectedTitle">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="onDelete">删除题目</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
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
      selectedId: null,
      selectedTitle: '',
      checkTitle: '',
      problems: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 25,
      showDeleteDialog: false
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
    onTagClick(tag) {
      this.keyword = tag
      this.search()
    }
    ,
    onCurrentPageChange() {
      this.getProblems()
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getProblems()
    },
    onEditClick(command) {
      if (command === '0') {
        // TODO 编辑题目
      } else if (command === '1') {
        // TODO 管理测试数据
      }
    },
    onDeleteClick(row) {
      this.selectedId = row.problemId
      this.selectedTitle = row.title
      this.showDeleteDialog = true
    },
    onDelete() {
      // TODO 删除题目
    }
  }
}
</script>

<style scoped>

</style>