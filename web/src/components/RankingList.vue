<template>
  <el-container class="container" v-loading="loading">
    <Error v-if="error.code !== undefined"
           :error="error"/>
    <el-card v-else style="width: 100%">
      <div v-if="contest != null" class="head">
        <span style="font-size: 14pt;font-weight: bold;">
          {{ contest.name }}
        </span>
        <div class="refresh-div">
          <div>
            <span>自动刷新：</span>
            <el-switch v-model="autoRefresh" @change="toggleAutoRefresh">
            </el-switch>
          </div>
          <el-button icon="el-icon-refresh" size="medium" style="margin-left: 15px"
                     @click="getRankingList(true)">
            刷新
          </el-button>
        </div>
      </div>
      <el-table :data="ranking.data" v-loading="loading" :row-style="{height: '55px'}" @row-dblclick="getDetail">
        <el-table-column label="排名" width="150px" align="center">
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
            <img class="avatar" align="center"
                 :src="`./api/file/image/avatar/${scope.row.userId}.png`"
                 onerror="this.style.display='none'" alt="avatar">
          </template>
        </el-table-column>
        <el-table-column label="用户名">
          <template slot-scope="scope">
            <b>{{ scope.row.name }}</b>
          </template>
        </el-table-column>
        <el-table-column label="总提交次数" width="150px" align="right">
          <template slot-scope="scope">
            <span>{{ scope.row['committed'] }} 次提交</span>
          </template>
        </el-table-column>
        <el-table-column label="通过题目" width="150px" align="right">
          <template slot-scope="scope">
            <span>{{ scope.row['passed'] }} 题通过</span>
          </template>
        </el-table-column>
        <el-table-column label="分数" prop="totalScore" width="150px" align="right">
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background :hide-on-single-page="true"
                     layout="total, sizes, prev, pager, next"
                     :page-sizes="[15, 25, 35]"
                     :page-size.sync="pageSize"
                     :total="ranking.count"
                     :current-page.sync="currentPage"
                     @size-change="getRankingList"
                     @current-change="getRankingList">
      </el-pagination>
    </el-card>
    <el-dialog :title="detailDialog.title" :visible.sync="detailDialog.visible" width="700px">
      <el-table :data="detailDialog.details">
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
  </el-container>
</template>

<script>
import {Notice, userInfo} from "@/script/util"
import {apiPath} from "@/script/env"
import Error from "@/components/Error"

export default {
  name: "RankingList",
  components: {
    Error
  },
  mounted() {
    this.contest = JSON.parse(window.sessionStorage.getItem('contest'))
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
      contest: {},
      currentPage: 1,
      pageSize: 15,
      detailDialog: {
        visible: false,
        title: '',
        details: []
      },
      error: {
        code: undefined,
        text: ''
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
      let self = this
      return function () {
        self.getRankingList(true)
      }
    },
    getRankingList(refresh) {
      this.loading = true
      let url, headers = {}
      if (this.contest == null) {
        url = apiPath.ranking
      } else {
        if (userInfo() != null && userInfo()['roleId'] >= 2) {
          url = apiPath.adminContestRanking
          headers = {
            token: userInfo() == null ? null : userInfo().token,
            userId: userInfo() == null ? null : userInfo().userId
          }
        } else {
          url = apiPath.contestRanking
        }
        url += `/${this.contest.id}`
      }
      this.$axios({
        url: url,
        method: 'get',
        headers: headers,
        params: {
          page: this.currentPage,
          limit: this.pageSize,
        }
      }).then((res) => {
        this.ranking = res.status === 200 ? res.data : {data: [], count: 0}
        if (refresh === true) {
          Notice.message.success(this, '排行榜已刷新')
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 403) {
          this.error = {
            code: res.status,
            text: res.data.msg
          }
        } else {
          Notice.notify.error(this, {
            title: '获取排行榜失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
    },
    getDetail(row) {
      if (this.contest != null && userInfo() != null && userInfo()['roleId'] >= 2) {
        this.detailDialog.visible = true
        this.detailDialog.title = `${row.name} 每题得分`
        this.$axios.get(apiPath.contestDetail, {
          headers: {
            token: userInfo().token,
            userId: userInfo().userId
          },
          params: {
            contestId: row.contestId,
            userId: row.userId
          }
        }).then((res) => {
          this.detailDialog.details = res.data
        }).catch((error) => {
          let res = error.response
          Notice.notify.error(this, {
            title: '获取详细得分失败',
            message: `${res.status} ${res.statusText}`
          })
        })
      }
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