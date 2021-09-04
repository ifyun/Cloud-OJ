<template>
  <div id="root">
    <div id="main">
      <div id="contest">
        <h2 id="title">{{ contest.contestName }}</h2>
        <div id="time-range">
          {{ timeRange }}
        </div>
      </div>
      <div id="ranking-wrapper">
        <transition-group v-if="ranking.count > 0" name="ranking-list"
                          id="ranking-list" tag="div">
          <div class="ranking-list-item"
               v-for="item in ranking.data"
               v-bind:key="item.userId" @click="getDetail(item)">
            <div class="user-info">
              <div class="rank">
                <img v-if="item.rank === 1" class="rank-icon"
                     src="@/assets/icons/medal-no.1.svg" alt="1">
                <img v-else-if="item.rank === 2" class="rank-icon"
                     src="@/assets/icons/medal-no.2.svg" alt="2">
                <img v-else-if="item.rank === 3" class="rank-icon"
                     src="@/assets/icons/medal-no.3.svg" alt="3">
                <span v-else>
                {{ item.rank }}
              </span>
              </div>
              <el-avatar class="avatar" icon="el-icon-user-solid"
                         :src="`/api/file/image/avatar/${item.userId}.png`">
              </el-avatar>
              <div class="username">
                {{ item.name }}
              </div>
            </div>
            <div class="data">
              <div class="data-item">
                <span class="prop">提交</span>
                <span>{{ item.committed }}</span>
              </div>
              <div class="data-item">
                <span class="prop">通过</span>
                <span>{{ item.passed }}</span>
              </div>
              <div class="data-item">
                <span class="prop">分数</span>
                <span>{{ item.score }}</span>
              </div>
            </div>
          </div>
        </transition-group>
        <el-empty v-else/>
      </div>
      <el-drawer :visible.sync="openDetail" :with-header="false" size="600px" direction="rtl">
        <div class="score-detail-title">
          <span>{{ scoreDetail.title }}</span>
        </div>
        <el-table :data="scoreDetail.data" stripe>
          <el-table-column label="题目">
            <template slot-scope="scope">
              {{ scope.row.problemId }}&nbsp;{{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column label="通过率" width="150px" align="right">
            <template slot-scope="scope">
              {{ scope.row['passRate'] * 100 }}%
            </template>
          </el-table-column>
          <el-table-column label="得分" prop="score" width="150px" align="right">
          </el-table-column>
        </el-table>
      </el-drawer>
    </div>
  </div>
</template>

<script>
import {ContestApi, RankingApi} from "@/service"
import {Notice, toLoginPage, userInfo} from "@/util"
import moment from "moment"

export default {
  name: "ContestLeaderboard",
  data() {
    return {
      contestId: null,
      contest: {
        contestId: null,
        contestName: "",
        ended: false,
      },
      ranking: {
        data: [
          {
            rank: 0,
            committed: 0,
            passed: 0,
            score: 0
          }
        ],
        count: 0
      },
      openDetail: false,
      scoreDetail: {
        title: "",
        data: []
      },
      timer: null,
      userInfo: userInfo()
    }
  },
  beforeMount() {
    const contestId = this.$route.params.id

    if (contestId != null) {
      this.contestId = contestId
      this.getContest()
      // 每隔 30 秒自动获取排名数据
      this.timer = setInterval(() => {
        this.getContest()
      }, 30000)
    }
  },
  computed: {
    timeRange: function () {
      if (this.contest.contestId != null) {
        const startAt = moment(this.contest.startAt)
        const endAt = moment(this.contest.endAt)

        const start = startAt.format("YYYY 年 MM 月 DD 日 HH:mm:ss")
        let end

        if (startAt.isSame(endAt, "day")) {
          end = endAt.format("HH:mm:ss")
        } else {
          end = endAt.format("YYYY 年 MM月DD日 HH:mm:ss")
        }

        return `${start} ~ ${end}`
      } else {
        return ""
      }
    }
  },
  methods: {
    getContest() {
      ContestApi.get(this.contestId)
          .then((data) => {
            this.$siteSetting.setTitle(`${data.contestName} - 排行榜`)
            this.contest = data
            this.getRanking()

            if (this.contest.ended) {
              clearInterval(this.timer)
            }
          })
          .catch((error) => {
            let msg = error.msg

            if (error.code === 401) {
              msg = "请重新登录"
            }

            Notice.message.error(this, `${error.code} ${msg}`)
          })
    },
    /**
     * 获取排名数据
     */
    getRanking() {
      RankingApi.getContestRanking(this.contest.contestId, 1, 10, userInfo())
          .then((data) => {
            this.ranking = data
          })
          .catch((error) => {
            let msg = error.msg

            if (error.code === 401) {
              msg = "请重新登录"
            }

            Notice.message.error(this, `${error.code} ${msg}`)
          })
    },
    /**
     * 获取详细得分
     */
    getDetail(user) {
      if (this.userInfo == null || this.userInfo["roleId"] === 0) {
        return
      }
      RankingApi.getDetail(this.contest.contestId, user.userId, this.userInfo)
          .then((data) => {
            console.log(data)
            this.scoreDetail.title = `${user.name}`
            this.scoreDetail.data = data
            this.openDetail = true
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
    }
  }
}
</script>

<style scoped lang="scss">
#root {
  height: 100%;
  width: 100%;
  overflow: auto;
  background-color: #111111;
}

#main {
  max-width: 1080px;
  min-width: 900px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

#contest {
  display: flex;
  flex-direction: column;
  align-items: center;

  #title {
    color: white;
    margin-top: 30px;
    font-size: 26pt;
    font-weight: bold;
    letter-spacing: 6px;
    text-shadow: 0 0 .1em white, 0 0 .1em white;
  }

  #time-range {
    color: white;
    font-size: 12pt;
    font-weight: bold;
    text-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
  }
}

#ranking-wrapper {
  margin-top: 20px;
  width: 90%;
  flex: 1;
  transition: all 0.5s ease;
  background-color: whitesmoke;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
}

#ranking-list {
  display: flex;
  flex-direction: column;
  flex-basis: 100%;
  margin: 10px;
  padding: 15px;
  overflow: hidden;
  transition: all 1s ease;

  .ranking-list-item {
    display: flex;
    align-items: center;
    margin-top: 5px;
    padding: 15px;
    height: 50px;

    * {
      text-shadow: 0 0 4px rgba(0, 0, 0, 0.08);
    }

    &:hover {
      cursor: pointer;
    }

    &:first-child {
      margin-top: 0;
    }

    &:nth-child(1) {
      .avatar {
        border-color: #F8A31D !important;
      }
    }

    &:nth-child(2) {
      .avatar {
        border-color: #6A7F94 !important;
      }
    }

    &:nth-child(3) {
      .avatar {
        border-color: #95734F !important;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      flex: 0 0 60%;

      .avatar {
        width: 45px;
        height: 45px;
        border: 3px solid white;
      }

      .username {
        color: var(--color-text-normal);
        font-size: 13pt;
        font-weight: bold;
        margin-left: 25px;
      }
    }
  }
}

.rank {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  color: var(--color-text-normal);
  font-weight: bold;
  font-size: 16pt;
  margin-right: 30px;

  .rank-icon {
    height: 35px;
  }
}

.data {
  width: 200px;
  flex: 1;
  display: flex;
  justify-content: space-around;

  .data-item {
    color: var(--color-text-primary);
    text-align: center;
    font-size: 12pt;
    font-weight: bold;
    width: 120px;
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;

    .prop {
      font-weight: normal;
      color: var(--color-text-normal);
    }
  }
}

.score-detail-title {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 25px;

  span {
    font-size: 14pt;
    font-weight: bold;
    color: var(--color-text-normal);
  }
}

.ranking-list-leave-active {
  width: 100%;
  position: absolute;
}

.ranking-list-move {
  transition: all 1s ease;
}

.ranking-list-leave {
  transition: opacity 0.5s ease;
}

.ranking-list-enter,
.ranking-list-leave-to {
  opacity: 0;
}
</style>