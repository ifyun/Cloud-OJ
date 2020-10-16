<template>
  <el-container class="container">
    <el-page-header style="align-self: flex-start; margin-top: 10px;"
                    content="提交记录"
                    @back="back">
    </el-page-header>
    <el-divider></el-divider>
    <el-card style="width: 100%; margin-bottom: 25px">
      <el-table :data="histories.data" v-loading="loading">
        <el-table-column label="题目ID" prop="problemId" width="120px" align="center">
        </el-table-column>
        <el-table-column label="题目名称">
          <template slot-scope="scope">
            <el-link type="primary"
                     @click="titleClick(scope.row)">
              <b>{{ scope.row.title }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="编程语言">
          <template slot-scope="scope">
            <div style="display: flex; align-items: center;">
              <img class="language-icon" :src="languageIcons[scope.row['language']]" alt="">
              {{ languages[scope.row['language']] }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="140px" align="center">
          <template slot-scope="scope">
            <el-tooltip content="点击查看代码" placement="right">
              <el-tag style="cursor: pointer" effect="plain"
                      :type="resultTags[scope.row.result].type"
                      @click="showCode(scope.row.code)">
                <i :class="resultTags[scope.row.result].icon">
                  {{ resultTags[scope.row.result].text }}
                </i>
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="耗时" width="100px" align="right">
          <template slot-scope="scope">
            <span v-if="scope.row.result < 4">{{ scope.row['time'] }}ms</span>
          </template>
        </el-table-column>
        <el-table-column label="得分" width="100px" align="right">
          <template slot-scope="scope">
            <b>{{ scope.row['score'] }}</b>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180px" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['submitTime'] }}</i>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background
                     layout="total, sizes, prev, pager, next, jumper"
                     :page-sizes="[10, 25, 50, 100]"
                     :page-size.sync="pageSize"
                     :total="histories.count"
                     :current-page.sync="currentPage"
                     @size-change="getHistories"
                     @current-change="getHistories">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {apiPath, handle401, userInfo} from "@/js/util";

export default {
  name: "HistoryList",
  mounted() {
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
      pageSize: 25,
      resultTags: [
        {text: '完全正确', type: 'success', icon: 'el-icon-success'},
        {text: '时间超限', type: 'warning', icon: 'el-icon-warning'},
        {text: '部分通过', type: 'warning', icon: 'el-icon-warning'},
        {text: '答案错误', type: 'danger', icon: 'el-icon-error'},
        {text: '编译错误', type: 'info', icon: 'el-icon-info'},
        {text: '运行错误', type: 'info', icon: 'el-icon-info'},
        {text: '判题异常', type: 'error', icon: 'el-icon-error'}
      ],
      languages: {
        0: 'C',
        1: 'C++',
        2: 'Java',
        3: 'Python',
        4: 'Bash Shell',
        5: 'C#'
      },
      languageIcons: {
        0: './icons/c.svg',
        1: './icons/cpp.svg',
        2: './icons/java.svg',
        3: './icons/python.svg',
        4: './icons/bash.svg',
        5: './icons/c-sharp.svg'
      }
    }
  },
  methods: {
    back() {
      window.history.back()
    },
    getHistories() {
      this.loading = true
      this.$axios({
        url: apiPath.history,
        method: 'get',
        headers: {
          token: userInfo().token
        },
        params: {
          userId: userInfo().userId,
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.histories = res.data
      }).catch((error) => {
        let res = error.response
        if (res.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            offset: 50,
            title: '获取提交记录失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    showCode(code) {
      this.$alert(`<pre class="sample">${code}</pre>`,
          '代码预览',
          {dangerouslyUseHTMLString: true});
    },
    titleClick(solution) {
      window.sessionStorage.setItem('code', JSON.stringify({
        language: solution['language'],
        code: solution['code']
      }))
      window.location.href = `/commit?problemId=${solution['problemId']}`
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

.language-icon {
  height: 24px;
  margin-right: 5px;
}
</style>