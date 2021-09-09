<template>
  <div id="root">
    <div id="main">
      <div class="ranking-wrapper">
        <div class="header" :style="{background: headerColor}">
          <div class="contest">
            <span class="title">{{ contest.contestName }}</span>
            <span class="time-range">
              {{ timeRange }}
            </span>
          </div>
        </div>
        <transition-group name="ranking-list" tag="div" class="ranking-list"
                          v-if="ranking.count > 0">
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
                <div class="prop">
                  <i class="el-icon-upload2 el-icon--left"></i>
                  <span>提交</span>
                </div>
                <span class="value">{{ item.committed }}</span>
              </div>
              <div class="data-item">
                <div class="prop">
                  <i class="el-icon-success el-icon--left"></i>
                  <span>通过</span>
                </div>
                <div class="value passed">
                  <span>{{ item.passed }}</span>
                </div>
              </div>
              <div class="data-item">
                <span class="prop">总分</span>
                <span class="value score">{{ item.score }}</span>
              </div>
            </div>
          </div>
        </transition-group>
        <el-empty v-else/>
      </div>
      <!-- 分数详情 -->
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
      headerColor: "#73C13B",
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
  destroyed() {
    clearInterval(this.timer);
    this.timer = null;
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
              this.timer = null
              this.headerColor = "#EB6E6F";
            }
          })
          .catch((error) => {
            Notice.message.error(this, `${error.code} ${error.msg}`)
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
            if (error.code === 401) {
              Notice.message.error(this, `${error.code} ${error.msg}`)
            } else if (error.code === 403) {
              clearInterval(this.timer)
              this.timer = null
            }
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
  background-image: linear-gradient(180deg, #F5F7FA 10%, #F9FBFF 100%);
}

#main {
  max-width: 1000px;
  min-width: 900px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;

  .ranking-wrapper {
    margin-top: 50px;
    width: 95%;
    flex: 1;
    transition: all 0.5s ease;
    box-shadow: 0 0 18px 6px rgba(0, 0, 0, 0.05);
    background-color: white;

    .header {
      transition: all 2s ease;
      padding: 30px;

      .contest {
        display: flex;
        flex-direction: column;
        align-items: flex-start;

        * {
          text-shadow: 0 0 5px rgba(0, 0, 0, 0.06);
        }

        .title {
          color: white;
          font-size: 30px;
          font-weight: bold;
          letter-spacing: 4px;
        }

        .time-range {
          margin-top: 10px;
          color: white;
          font-size: var(--text-small-title);
        }
      }
    }

    .ranking-list {
      display: flex;
      flex-direction: column;
      flex-basis: 100%;
      overflow: hidden;
      transition: all 1s ease;

      .ranking-list-item {
        display: flex;
        align-items: center;
        padding: 20px;
        height: 50px;
        background-image: linear-gradient(180deg, #FAFAFA88 10%, #FFFFFF00 100%);;

        * {
          text-shadow: 0 0 4px rgba(0, 0, 0, 0.06);
        }

        &:hover {
          cursor: pointer;
        }

        &:nth-child(1) {
          .avatar {
            border-color: #F8A31D !important;
          }
        }

        .user-info {
          display: flex;
          align-items: center;
          flex: 0 0 65%;

          .rank {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 45px;
            color: var(--color-text-secondary);
            font-weight: bold;
            font-size: var(--text-large-title);
            margin-right: 30px;

            .rank-icon {
              height: 35px;
            }
          }

          .avatar {
            width: 50px;
            height: 50px;
            border: 4px solid #F5F7FA;
          }

          .username {
            color: var(--color-text-normal);
            font-size: var(--text-large-title);
            font-weight: bold;
            margin-left: 20px;
          }
        }

        .data {
          flex: 1;
          width: 200px;
          height: 100%;
          display: flex;
          justify-content: space-around;

          .data-item {
            display: flex;
            flex-direction: column;
            justify-content: space-around;
            align-items: center;

            * {
              text-shadow: 0 0 4px rgba(0, 0, 0, 0.06);
            }

            .prop {
              font-weight: normal;
              font-size: var(--text-base);
              color: var(--color-text-secondary);
            }

            .value {
              color: var(--color-success);
              font-size: var(--text-title);
              font-weight: bold;

              &.passed {
                color: var(--color-success);
              }

              &.score {
                color: var(--color-success);
              }
            }
          }
        }
      }
    }
  }
}

@media screen and (max-width: 720px) {
  #main {
    min-width: 100%;

    .ranking-wrapper {
      margin-top: 0;
      width: 100%;

      .header {
        padding: 1rem .8rem;

        .contest {

          .title {
            font-size: 1.25rem;
          }

          .time-range {
            font-size: .8rem;
          }
        }
      }

      .ranking-list {
        .ranking-list-item {
          padding: .3rem;

          .user-info {
            flex: 0 0 60%;

            .rank {
              width: 2rem;
              font-size: 1rem;
              margin-right: .2rem;

              .rank-icon {
                height: 1.3rem;
              }
            }

            .avatar {
              width: 2.15rem;
              height: 2.15rem;
              border-width: .15rem;
            }

            .username {
              font-size: .8rem;
              margin-left: .5rem;
            }
          }

          .data {
            .data-item {
              .el-icon--left {
                margin-right: .2rem;
              }

              .prop {
                font-size: .6rem;
              }

              .value {
                font-size: .8rem;
              }
            }
          }
        }
      }
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