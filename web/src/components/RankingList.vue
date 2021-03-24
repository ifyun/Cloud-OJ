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
      <el-table :data="ranking.data" v-loading="loading" :show-header="false" stripe
                :row-style="{height: '55px'}" @row-dblclick="getDetail">
        <el-table-column width="60px" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row['rank'] === 1" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.1.svg" alt="1">
            <img v-else-if="scope.row['rank'] === 2" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.2.svg" alt="2">
            <img v-else-if="scope.row['rank'] === 3" align="center" class="ranking-icon"
                 src="@/assets/icons/medal-no.3.svg" alt="3">
            <b v-else>{{ scope.row['rank'] }}</b>
          </template>
        </el-table-column>
        <el-table-column width="80px" align="right">
          <template slot-scope="scope">
            <img class="avatar" align="center" alt="Avatar" :src="`/api/file/image/avatar/${scope.row.userId}.png`"
                 onerror="this.src='/icons/no_avatar.png'">
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <el-link :href="`/profile?userId=${scope.row.userId}`">{{ scope.row.name }}</el-link>
            <el-tag v-if="userInfo != null && scope.row.userId === userInfo.userId" class="el-icon--right"
                    type="success" size="mini" effect="dark">D
              你自己
            </el-tag>
            <br>
            <span class="user-id">{{ scope.row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column width="120px" align="right">
          <template slot-scope="scope">
            <span>{{ scope.row['committed'] }} 次提交</span>
            <i class="el-icon-top el-icon--right"/>
          </template>
        </el-table-column>
        <el-table-column width="120px" align="right">
          <template slot-scope="scope">
            <span class="passed-tag" style="color: #67c23a" @click="getDetail(scope.row)">
              <span>{{ scope.row['passed'] }} 题通过</span>
              <i class="el-icon-success el-icon--right"/>
            </span>
          </template>
        </el-table-column>
        <el-table-column width="150px" align="right">
          <template slot-scope="scope">
            <b style="color: #303133">{{ scope.row["totalScore"] }}</b>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px" layout="total, prev, pager, next"
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
      this.siteSetting.setTitle("排行榜")
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
      userInfo: userInfo(),
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
        this.siteSetting.setTitle(`${data.contestName} - 排行榜`)
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
            toLoginPage(this)
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
                toLoginPage(this)
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
  height: 42px;
  width: 42px;
  border-radius: 22px;
}

.avatar:empty {
  background-color: #F0F0F0;
}

.ranking-icon {
  height: 28px;
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

.el-link--inner {
  color: #303133;
}

.user-id {
  font-size: 6px;
  color: #909399;
}

.passed-tag {
  cursor: pointer;
}
</style>