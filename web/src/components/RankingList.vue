<template>
  <el-container class="container" v-loading="loading">
    <el-card style="width: 100%">
      <el-table :data="ranking.data" stripe>
        <el-table-column label="排名" width="150px" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row['rank'] === 1" align="center"
                 src="@/assets/icons/medal-no.1.svg" height="36px" alt="1">
            <img v-else-if="scope.row['rank'] === 2" align="center"
                 src="@/assets/icons/medal-no.2.svg" alt="2">
            <img v-else-if="scope.row['rank'] === 3" align="center"
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
        <el-table-column label="用户名" prop="name">
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
                     background
                     layout="total, sizes, prev, pager, next, jumper"
                     :page-sizes="[10, 25, 50, 100]"
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
import {apiPath} from "@/js/util";

export default {
  name: "RankingList",
  mounted() {
    this.getRankingList()
  },
  data() {
    return {
      loading: true,
      ranking: {
        data: [],
        count: 0
      },
      currentPage: 1,
      pageSize: 25
    }
  },
  methods: {
    getRankingList() {
      this.loading = true
      this.$axios({
        url: apiPath.ranking,
        method: 'get',
        params: {
          page: this.currentPage,
          limit: this.pageSize
        }
      }).then((res) => {
        this.ranking = res.data
      }).catch((error) => {
        let res = error.response
        this.$notify.error({
          offset: 50,
          title: '获取排行榜失败',
          message: `${res.status} ${res.statusText}`
        })
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
</style>