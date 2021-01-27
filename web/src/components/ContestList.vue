<template>
  <div class="content">
    <el-card style="width: 100%">
      <el-table :data="contests.data" v-loading="loading">
        <el-table-column label="竞赛/作业名称" prop="contestName">
          <template slot-scope="scope">
            <el-link :type="scope.row['ended']? 'info' : scope.row['started'] ? 'success' : 'info'"
                     :disabled="scope.row['ended'] ? false : !scope.row['started']"
                     @click="contestClick(scope.row)">
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
            <span class="languages" style="word-break: break-word">
              {{ calcLanguages(scope.row.languages) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180px" align="right">
          <template slot-scope="scope">
            <i class="el-icon-date el-icon--left"></i>
            <span>{{ formatDate(scope.row['startAt']) }}</span>
            <br>
            <i class="el-icon-time el-icon--left"></i>
            <span>{{ formatTime(scope.row['startAt']) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180px" align="right">
          <template slot-scope="scope">
            <div style="color: #F56C6C">
              <i class="el-icon-date el-icon--left"></i>
              <span>{{ formatDate(scope.row['endAt']) }}</span>
              <br>
              <i class="el-icon-time el-icon--left"></i>
              <span>{{ formatTime(scope.row['endAt']) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="50px" align="right">
          <template slot-scope="scope">
            <el-dropdown v-if="scope.row['started']" trigger="click" @command="seeRanking($event, scope.row)">
            <span class="el-dropdown-link">
              <i class="el-icon-arrow-down"></i>
            </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-s-data" command="ranking">
                  查看排行榜
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="contests.count"
                     :current-page.sync="currentPage"
                     @size-change="getContests" @current-change="getContests">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import {languages} from "@/util/data"
import {Notice, searchParams} from "@/util"
import moment from "moment"
import {ContestApi} from "@/service"

export default {
  name: "CompetitionList",
  beforeMount() {
    this.siteSetting.setTitle("竞赛/作业")
    this.loadPage()
    this.getContests()
  },
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
  },
  methods: {
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    getContests() {
      history.pushState(null, "", `?page=${this.currentPage}`)
      this.loading = true
      ContestApi.getAll(this.currentPage, this.pageSize)
          .then((data) => {
            this.contests = data
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "获取竞赛/作业失败",
              message: `${error.code} ${error.msg}`
            })
          })
          .finally(() => {
            this.loading = false
          })
    },
    contestClick(row) {
      window.location.href = `.?contestId=${row.contestId}`
    },
    formatDate(time) {
      return moment(time).format("YYYY年 MM月DD日")
    },
    formatTime(time) {
      return moment(time).format("HH:mm:ss")
    },
    calcLanguages(languageId) {
      let languageNames = []
      for (let i in languages) {
        languageNames.push(languages[i].name)
      }
      const max = (1 << languageNames.length) - 1
      if ((languageId & max) === max) {
        return "无限制"
      } else {
        let languageLimit = []
        languageNames.forEach((value, index) => {
          const lang = 1 << index
          if ((languageId & lang) === lang) {
            languageLimit.push(value)
          }
        })
        return languageLimit.join(" / ")
      }
    },
    seeRanking(_, contest) {
      window.location.href = `/ranking?contestId=${contest.contestId}`
    }
  }
}
</script>

<style scoped>
.content {
  width: 100%;
}

.languages {
  color: #606266;
}

.el-dropdown-link {
  cursor: pointer;
  color: #303133;
}
</style>