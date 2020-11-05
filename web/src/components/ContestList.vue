<template>
  <el-container class="container">
    <el-card style="width: 100%">
      <el-table :data="contests.data" stripe>
        <el-table-column label="竞赛/作业" prop="contestName">
          <template slot-scope="scope">
            <el-link :type="scope.row['ended']? 'info' : scope.row['started'] ? 'success' : 'info'"
                     :disabled="scope.row['ended'] ? false : !scope.row['started']"
                     :href="`.?contestId=${scope.row.contestId}&contestName=${scope.row.contestName}`">
              <b v-if="scope.row['ended']">[已结束]</b>
              <b v-else-if="scope.row['started']">[进行中]</b>
              <b v-else>[未开始]</b>
              <b>&nbsp;{{ scope.row.contestName }}</b>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="240px" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['startAt'] }}</i>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="240px" align="center">
          <template slot-scope="scope">
            <i class="el-icon-time"> {{ scope.row['endAt'] }}</i>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200px" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row['started']" type="success" size="mini" plain
                       @click="seeRanking(scope.row)">
              查看排行榜
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 10px"
                     background
                     layout="total, sizes, prev, pager, next, jumper"
                     :page-sizes="[10, 20, 30, 50]"
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
import {apiPath} from "@/script/util";

export default {
  name: "CompetitionList",
  mounted() {
    document.title = '竞赛/作业 · Cloud OJ'
    this.getContests()
  },
  data() {
    return {
      contests: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 10
    }
  },
  methods: {
    getContests() {
      this.$axios({
        url: apiPath.contest,
        method: 'get',
        params: {
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.contests = res.data
      }).catch((error) => {
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: '获取竞赛/作业失败',
          message: `${res.status} ${res.statusText}`
        })
      })
    },
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
  margin-top: 25px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>