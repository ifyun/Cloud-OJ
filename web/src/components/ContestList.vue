<template>
  <el-container class="container">
    <el-table :data="contests.data" stripe>
      <el-table-column label="ID" prop="contestId" width="120px" align="center">
      </el-table-column>
      <el-table-column label="竞赛/作业" prop="contestName">
        <template slot-scope="scope">
          <el-link type="primary"
                   :href="`.?contestId=${scope.row.contestId}&contestName=${scope.row.contestName}`">
            <b>{{ scope.row.contestName }}</b>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" prop="startAt" width="300px">
      </el-table-column>
      <el-table-column label="结束时间" prop="endAt" width="300px">
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[10, 25, 50, 100]"
                   :page-size.sync="pageSize"
                   :total="contests.count"
                   :current-page.sync="currentPage"
                   @size-change="getContests"
                   @current-change="getContests">
    </el-pagination>
  </el-container>
</template>

<script>
import {apiPath} from "@/js/util";

export default {
  name: "CompetitionList",
  beforeMount() {
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
      pageSize: 25
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
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
.container {
  margin-top: 20px;
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>