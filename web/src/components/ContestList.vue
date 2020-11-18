<template>
  <el-container class="container">
    <el-card style="width: 100%">
      <el-table :data="contests.data" v-loading="loading">
        <el-table-column label="竞赛/作业名称" prop="contestName">
          <template slot-scope="scope">
            <el-link style="font-size: 12pt"
                     :type="scope.row['ended']? 'info' : scope.row['started'] ? 'success' : 'info'"
                     :disabled="scope.row['ended'] ? false : !scope.row['started']"
                     :href="`.?contestId=${scope.row.contestId}&contestName=${scope.row.contestName}`">
              <b v-if="scope.row['ended']">[已结束]</b>
              <b v-else-if="scope.row['started']">[进行中]</b>
              <b v-else>[未开始]</b>
              <b>&nbsp;{{ scope.row.contestName }}</b>
            </el-link>
            <div style="margin-top: 10px">题目数量: {{ scope.row['problemCount'] }} 题
            </div>
          </template>
        </el-table-column>
        <el-table-column label="语言限制" align="left">
          <template slot-scope="scope">
            <b class="languages" style="word-break: break-word">{{ calcLanguages(scope.row.languages) }}</b>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180px" align="right">
          <template slot-scope="scope">
            <b>{{ formatDate(scope.row['startAt']) }}</b>
            <br>
            <b>{{ formatTime(scope.row['startAt']) }}</b>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180px" align="right">
          <template slot-scope="scope">
            <div style="color: #F56C6C">
              <b>{{ formatDate(scope.row['endAt']) }}</b>
              <br>
              <b>{{ formatTime(scope.row['endAt']) }}</b>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="130px" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row['started']" type="text"
                       icon="el-icon-s-data"
                       @click="seeRanking(scope.row)">
              排行榜
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background :hide-on-single-page="true"
                     layout="total, sizes, prev, pager, next"
                     :page-sizes="[10, 20, 30]"
                     :page-size.sync="pageSize"
                     :total="contests.count"
                     :current-page.sync="currentPage"
                     @size-change="getContests"
                     @current-change="getContests">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {apiPath} from "@/script/env"
import {Notice} from "@/script/util"
import moment from "moment"

const languages = ['C', 'C++', 'Java', 'Python', 'Bash', 'C#', 'JavaScript', 'Kotlin']

export default {
  name: "CompetitionList",
  mounted() {
    document.title = '竞赛/作业 · Cloud OJ'
    this.getContests()
  }
  ,
  data() {
    return {
      loading: true,
      contests: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 10
    }
  }
  ,
  methods: {
    getContests() {
      this.loading = true
      this.$axios({
        url: apiPath.contest,
        method: 'get',
        params: {
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.contests = res.status === 200 ? res.data : {data: [], count: 0}
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: '获取竞赛/作业失败',
          message: `${res.status} ${res.statusText}`
        })
      }).finally(() => {
        this.loading = false
      })
    }
    ,
    formatDate(time) {
      return moment(time).format('YYYY年 MM月DD日')
    }
    ,
    formatTime(time) {
      return moment(time).format('HH:mm:ss')
    },
    calcLanguages(lang) {
      let langArr = []
      languages.forEach((value, index) => {
        let t = 1 << index
        if ((lang & t) === t)
          langArr.push(value)
      })
      return langArr.join(' / ')
    }
    ,
    seeRanking(contest) {
      window.sessionStorage.setItem('contest', JSON.stringify({
        id: contest.contestId,
        name: contest.contestName
      }))
      window.location.href = `./ranking`
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

.languages {
  color: #606266;
}
</style>