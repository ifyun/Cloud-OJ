<template>
  <div id="root">
    <div v-if="!loading && contests.count === 0">
      <el-empty description="什么都没有"/>
    </div>
    <el-card v-else class="borderless" style="width: 100%">
      <el-table :data="contests.data" v-loading="loading">
        <el-table-column label="竞赛" prop="contestName">
          <template slot-scope="scope">
            <el-tag v-if="scope.row['ended']" type="danger" effect="dark" size="small">已结束</el-tag>
            <el-tag v-else-if="scope.row['started']" type="success" effect="dark" size="small">进行中</el-tag>
            <el-tag v-else type="info" effect="dark" size="small">未开始</el-tag>
            <el-link style="margin-left: 6px" @click="contestClick(scope.row)"
                     :type="scope.row['ended']? 'info' : scope.row['started'] ? 'success' : 'info'"
                     :disabled="scope.row['ended'] ? false : !scope.row['started']">
              {{ scope.row.contestName }}
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
            <span>{{ formatDate(scope.row['startAt']) }}</span>
            <br>
            <i class="el-icon-time el-icon--left"></i>
            <span>{{ formatTime(scope.row['startAt']) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180px" align="right">
          <template slot-scope="scope">
            <div>
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
                <el-dropdown-item icon="el-icon-s-data" command="leaderboard">
                  查看排行榜
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px" layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="contests.count"
                     :current-page.sync="currentPage"
                     @size-change="getContests" @current-change="getContests">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import {languages} from "@/util/data"
import {Notice, userInfo} from "@/util"
import moment from "moment"
import {ContestApi} from "@/service"

export default {
  name: "Contests",
  beforeMount() {
    this.$siteSetting.setTitle("竞赛")
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
      const page = this.$route.query.page
      if (page != null) {
        this.currentPage = Number(page)
      }
    },
    pageChange(page) {
      this.$router.push({
        query: {page}
      })
    },
    getContests() {
      this.loading = true
      ContestApi.getAll(this.currentPage, this.pageSize)
          .then((data) => {
            this.contests = data
          })
          .catch((error) => {
            Notice.notify.error(this, {
              title: "获取竞赛失败",
              message: `${error.code} ${error.msg}`
            })
          })
          .finally(() => {
            this.loading = false
          })
    },
    contestClick(row) {
      if (userInfo() == null) {
        this.$bus.$emit("login")
      } else {
        this.$router.push({
          path: "/problems",
          query: {
            contestId: row.contestId
          }
        })
      }
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
      window.open(`/contest_leaderboard/${contest.contestId}`, "_blank")
    }
  }
}
</script>

<style scoped>
#root {
  margin: 0 auto;
  width: 1100px;
}

.languages {
  color: var(--color-text-normal);
  font-size: var(--text-small);
}

.el-dropdown-link {
  cursor: pointer;
  color: var(--color-text-primary);
}
</style>