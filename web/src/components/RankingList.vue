<template>
  <div class="content">
    <Error v-if="error.code != null" :error="error"/>
    <el-card v-else style="width: 100%">
      <div v-if="contest.contestId != null" class="head">
        <span style="font-size: 14pt;font-weight: bold;">{{ contest.contestName }}</span>
        <div class="refresh-div">
          <div>
            <span>自动刷新：</span>
            <el-switch v-model="autoRefresh" @change="toggleAutoRefresh">
            </el-switch>
          </div>
          <el-button style="margin-left: 20px" icon="el-icon-refresh"
                     size="mini" round @click="getRankingList(true)">
            刷新
          </el-button>
        </div>
      </div>
      <el-table :data="ranking.data" v-loading="loading" :row-style="{height: '55px'}" @row-dblclick="getDetail">
        <el-table-column width="100px" align="center">
          <template slot="header">
            <i class="el-icon-s-data el-icon--left"/>
            <span>排名</span>
          </template>
          <template slot-scope="scope">
            <img v-if="scope.row['rank'] === 1" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.1.svg" alt="1">
            <img v-else-if="scope.row['rank'] === 2" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.2.svg" alt="2">
            <img v-else-if="scope.row['rank'] === 3" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.3.svg" alt="3">
            <span v-else><b>{{ scope.row['rank'] }}</b></span>
          </template>
        </el-table-column>
        <el-table-column width="55px" align="right">
          <template slot-scope="scope">
            <img class="avatar" align="center" alt="avatar" style="visibility: hidden"
                 :src="`./api/file/image/avatar/${scope.row.userId}.png`"
                 onload="this.style.visibility='visible'"
                 onerror="this.src='/icons/no_avatar.png'">
          </template>
        </el-table-column>
        <el-table-column>
          <template slot="header">
            <i class="el-icon-user-solid el-icon--left"/>
            <span>用户名</span>
          </template>
          <template slot-scope="scope">
            <el-link :href="`/profile?userId=${scope.row.userId}`"><b>{{ scope.row.name }}</b></el-link>
          </template>
        </el-table-column>
        <el-table-column label="总提交次数" width="150px" align="right">
          <template slot-scope="scope">
            <span>{{ scope.row['committed'] }} 次提交</span>
          </template>
        </el-table-column>
        <el-table-column width="150px" align="right">
          <template slot="header">
            <i class="el-icon-success el-icon--left"></i>
            <span>通过题目</span>
          </template>
          <template slot-scope="scope">
            <span style="cursor: pointer" @click="getDetail(scope.row)">
              {{ scope.row['passed'] }} 题通过
            </span>
          </template>
        </el-table-column>
        <el-table-column label="分数" prop="totalScore" width="150px" align="right">
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px" background layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="ranking.count"
                     :current-page.sync="currentPage"
                     @size-change="getRankingList" @current-change="getRankingList">
      </el-pagination>
    </el-card>
    <el-dialog :title="detailDialog.title" :visible.sync="detailDialog.visible" width="700px">
      <el-table :data="detailDialog.details" v-loading="detailDialog.loading">
        <el-table-column label="题目">
          <template slot-scope="scope">
            <b>[{{ scope.row.problemId }}]&nbsp;{{ scope.row.title }}</b>
          </template>
        </el-table-column>
        <el-table-column label="通过率" width="100px" align="right">
          <template slot-scope="scope">
            <span>{{ scope.row['passRate'] * 100 }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="得分" prop="score" width="100px" align="right">
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {Notice, searchParams, toLoginPage, userInfo} from "@/util"
import Error from "@/components/Error"
import {ContestApi, RankingApi} from "@/service"

export default {
  name: "RankingList",
  components: {
    Error
  },
  beforeMount() {
    this.loadPage()
    if (this.contestId != null) {
      this.getContest()
    } else {
      document.title = "排行榜 - Cloud OJ"
    }
    this.getRankingList()
  },
  data() {
    return {
      loading: true,
      autoRefresh: false,
      timer: null,
      ranking: {
        data: [],
        count: 0
      },
      contestId: searchParams()["contestId"],
      contest: {
        contestId: null,
        contestName: ""
      },
      currentPage: 1,
      pageSize: 15,
      detailDialog: {
        visible: false,
        title: "",
        loading: true,
        details: []
      },
      error: {
        code: null,
        msg: ""
      }
    }
  },
  methods: {
    toggleAutoRefresh(autoRefresh) {
      if (autoRefresh) {
        this.timer = setInterval(this.autoGetRankingList(), 10000)
      } else {
        clearInterval(this.timer)
      }
    },
    autoGetRankingList() {
      let ctx = this
      return function () {
        ctx.getRankingList(true)
      }
    },
    loadPage() {
      const page = searchParams()["page"]
      if (page != null) {
        this.currentPage = parseInt(page)
      }
    },
    getContest() {
      if (this.contestId == null) {
        return
      }
      ContestApi.get(this.contestId).then((data) => {
        document.title = `${data.contestName} - 排行榜 - Cloud OJ`
        this.contest = data
        this.getRankingList()
      }).catch((error) => {
        this.error = error
      })
    },
    getRankingList(refresh = false) {
      this.loading = true
      let params = `?page=${this.currentPage}`
      if (this.contestId != null) {
        params += `&contestId=${this.contestId}`
      }

      history.pushState(null, "", params)

      const promise = this.contest.contestId == null ?
          RankingApi.getRanking(this.currentPage, this.pageSize) :
          RankingApi.getContestRanking(this.contest.contestId, this.currentPage, this.pageSize, userInfo())

      promise.then((data) => {
        this.ranking = data
        refresh === true && Notice.message.success(this, "排行榜已刷新")
      }).catch((error) => {
        switch (error.code) {
          case 401:
            toLoginPage()
            break
          default:
            this.error = error
        }
      }).finally(() => {
        this.loading = false
      })
    },
    getDetail(row) {
      if (this.contest.contestId != null && userInfo() != null && userInfo()["roleId"] >= 2) {
        this.detailDialog.loading = true
        this.detailDialog.visible = true
        this.detailDialog.title = `${row.name} - 每题得分`

        RankingApi.getDetail(row.contestId, row.userId, userInfo())
            .then((data) => {
              this.detailDialog.details = data
            })
            .catch((error) => {
              if (error.code === 401) {
                toLoginPage()
              }
              Notice.notify.error(this, {
                title: "获取详细得分失败",
                message: `${error.code} ${error.msg}`
              })
            })
            .finally(() => {
              this.detailDialog.loading = false
            })
      }
    }
  }
}
</script>

<style scoped>
.content {
  width: 100%;
}

.avatar {
  height: 32px;
  border-radius: 15px;
}

.ranking-icon {
  height: 26px;
}

.head {
  margin-bottom: 25px;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.refresh-div {
  margin-left: auto;
  display: flex;
  align-items: center;
}
</style>