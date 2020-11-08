<template>
  <el-container class="container" v-loading="loading">
    <Error style="margin-top: 35px"
           v-if="error.code !== undefined"
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
                     @click="getRankingList">
            刷新
          </el-button>
        </div>
      </div>
      <el-table :data="ranking.data" v-loading="loading">
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
                 onerror="this.src='/icons/no_avatar.svg'" alt="avatar">
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
                     :page-sizes="[10, 20, 30, 50]"
                     :page-size.sync="pageSize"
                     :total="ranking.count"
                     :current-page.sync="currentPage"
                     @size-change="getRankingList"
                     @current-change="getRankingList">
      </el-pagination>
    </el-card>
  </el-container>
</template>

<script>
import {apiPath, userInfo} from "@/script/util"
import Error from "@/components/Error"

export default {
  name: "RankingList",
  components: {
    Error
  },
  mounted() {
    this.contest = JSON.parse(window.sessionStorage.getItem('contest'))
    window.sessionStorage.removeItem('contest')
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
      pageSize: 10,
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
      let url
      if (this.contest == null)
        url = apiPath.ranking
      else {
        if (userInfo()['roleId'] >= 2) {
          url = apiPath.adminContestRanking
        } else {
          url = apiPath.contestRanking
        }
        url += `/${this.contest.id}`
      }
      this.$axios({
        url: url,
        method: 'get',
        headers: {
          'token': userInfo().token
        },
        params: {
          page: this.currentPage,
          limit: this.pageSize,
          userId: userInfo().userId
        }
      }).then((res) => {
        this.ranking = res.status === 200 ? res.data : {data: [], count: 0}
        if (refresh === true) {
          console.log('Refresh ranking.')
          this.$message({
            offset: 75,
            message: '排行榜已刷新',
            type: 'success',
            duration: 1500
          })
        }
      }).catch((error) => {
        let res = error.response
        if (res.status === 403) {
          this.error = {
            code: res.status,
            text: res.data.msg
          }
        } else {
          this.$notify.error({
            offset: 50,
            title: '获取排行榜失败',
            message: `${res.status} ${res.statusText}`
          })
        }
      }).finally(() => {
        this.loading = false
      })
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

.avatar {
  height: 30px;
  border-radius: 15px;
  border: 1px solid #e0e0e0;
}

.ranking-icon {
  height: 36px;
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