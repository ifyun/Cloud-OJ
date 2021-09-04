<template>
  <div id="root">
    <error-info v-if="error.code != null" :error="error"/>
    <div v-else-if="!loading && ranking.count === 0">
      <el-empty description="什么都没有"/>
    </div>
    <el-card v-else class="borderless" style="width: 100%">
      <el-table :data="ranking.data" v-loading="loading" :show-header="false" stripe
                :row-style="{height: '55px'}">
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
            <el-avatar icon="el-icon-user-solid" size="medium" alt="user"
                       :src="`/api/file/image/avatar/${scope.row.userId}.png`">
            </el-avatar>
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
            <span>{{ scope.row.committed }} 次提交</span>
            <i class="el-icon-top el-icon--right"/>
          </template>
        </el-table-column>
        <el-table-column width="120px" align="right">
          <template slot-scope="scope">
            <span style="color: #67c23a">
              <span>{{ scope.row.passed }} 题通过</span>
              <i class="el-icon-success el-icon--right"/>
            </span>
          </template>
        </el-table-column>
        <el-table-column width="150px" align="right">
          <template slot-scope="scope">
            <b style="color: #303133">{{ scope.row.score }}</b>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 15px" layout="total, prev, pager, next"
                     :page-size.sync="pageSize" :total="ranking.count"
                     :current-page.sync="currentPage"
                     @size-change="getRankingList" @current-change="pageChange">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import {userInfo} from "@/util"
import ErrorInfo from "@/components/ErrorInfo"
import {RankingApi} from "@/service"

export default {
  name: "Leaderboard",
  components: {
    ErrorInfo
  },
  beforeMount() {
    this.loadPage()
    this.$siteSetting.setTitle("排行榜")
    this.getRankingList()
  },
  data() {
    return {
      loading: true,
      ranking: {
        data: [
          {
            committed: 0,
            passed: 0,
            score: 0
          }
        ],
        count: 0
      },
      userInfo: userInfo(),
      currentPage: 1,
      pageSize: 15,
      error: {
        code: null,
        msg: ""
      }
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
        query: {
          page
        }
      })
    },
    getRankingList() {
      this.loading = true

      RankingApi.getRanking(this.currentPage, this.pageSize)
          .then((data) => {
            this.ranking = data
          })
          .catch((error) => {
            this.error = error
          })
          .finally(() => {
            this.loading = false
          })
    }
  }
}
</script>

<style scoped>
#root {
  margin: 0 auto;
  width: 1100px;
}

.ranking-icon {
  height: 28px;
}

.el-link--inner {
  color: #303133;
}

.user-id {
  font-size: 6px;
  color: #909399;
}
</style>