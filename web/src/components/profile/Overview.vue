<template>
  <div style="position:relative">
    <el-row>
      <el-col :span="16">
        <ECharts theme="macarons" :options="pieOption"/>
        <span v-if="pieOption.series[0].data.length === 0" class="no-record">无做题记录</span>
      </el-col>
      <el-col :span="8">
        <span style="font-size: 13pt">结果统计({{ resultStatistics.total }}次提交)</span>
        <div style="margin-top: 75px">
          <div class="result-bar" v-for="(key, index) in resultKeys" :key="index">
            <el-row>
              <el-col :span="4">
                <b>{{ key }}</b>
              </el-col>
              <el-col :span="20">
                <el-progress :stroke-width="18" :text-inside="true" :status="getStatus(key)"
                             :percentage="calcPercentage(key)">
                </el-progress>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-divider></el-divider>
    <ECharts theme="royal" :options="activitiesOption" style="width: 100%;height: 200px"/>
  </div>
</template>

<script>
import ECharts from "vue-echarts"
import "echarts/lib/chart/pie"
import "echarts/theme/macarons"
import "echarts/theme/royal"
import {Notice, userInfo} from "@/script/util"
import {apiPath, languages} from "@/script/env"

const year = new Date().getFullYear()

export default {
  name: "Overview",
  components: {
    ECharts
  },
  mounted() {
    this.getOverview()
  },
  data() {
    return {
      resultKeys: [
        "AC",
        "WA",
        "TLE",
        "MLE",
        "RE",
        "CE"
      ],
      resultStatistics: {
        AC: 0,
        WA: 0,
        TLE: 0,
        MLE: 0,
        RE: 0,
        CE: 0,
        total: 0,
      },
      pieOption: {
        title: {
          text: "语言偏好",
          textStyle: {
            color: "#303133"
          }
        },
        tooltip: {
          trigger: "item",
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: "left",
          top: "50",
          orient: "vertical",
          data: languages
        },
        series: [
          {
            name: "语言偏好",
            type: "pie",
            center: ["50%", "50%"],
            radius: [50, 120],
            roseType: "area",
            data: []
          }
        ]
      },
      activitiesOption: {
        title: {
          top: 0,
          text: "年度做题情况",
          textStyle: {
            color: "#303133"
          }
        },
        tooltip: {},
        visualMap: {
          min: 0,
          max: 20,
          type: "piecewise",
          orient: "horizontal",
          left: "center",
          top: 35
        },
        calendar: {
          top: 100,
          right: 10,
          cellSize: ["auto", 14],
          range: "2020",
          dayLabel: {
            firstDay: 1,
            nameMap: "cn"
          },
          monthLabel: {
            nameMap: "cn"
          },
          itemStyle: {
            borderWidth: 0.2
          },
          splitLine: {
            lineStyle: {
              width: 1,
              opacity: 0.25
            }
          },
          yearLabel: {show: true}
        },
        series: {
          type: "heatmap",
          coordinateSystem: "calendar",
          data: []
        }
      }
    }
  },
  methods: {
    getOverview() {
      this.$axios({
        url: apiPath.overview,
        params: {
          userId: userInfo().userId,
          year: year
        }
      }).then((res) => {
        this.setChartsData(res.data)
      }).catch((error) => {
        let res = error.response
        Notice.notify.error(this, {
          title: "获取数据失败",
          message: `${res.status} ${res.statusText}`
        })
      })
    },
    setChartsData(overview) {
      let statistics = overview["statistics"]
      let total = 0

      this.resultKeys.forEach((key) => {
        total += statistics[key]
      })

      this.resultStatistics = statistics
      this.resultStatistics.total = total

      let preference = overview["preference"]
      let activities = overview["activities"]

      let preferenceData = []
      let activitiesData = []

      for (let i = 0; i < preference.length; i++) {
        preferenceData.push({
          name: languages[preference[i]["language"]],
          value: preference[i]["count"]
        })
      }

      for (let i = 0; i < activities.length; i++) {
        activitiesData.push([
          activities[i]["date"],
          activities[i]["count"]
        ])
      }

      this.pieOption.series[0].data = preferenceData
      this.activitiesOption.series.data = activitiesData
    },
    calcPercentage(key) {
      if (this.resultStatistics.total === 0)
        return 0
      return Math.round((this.resultStatistics[key] / this.resultStatistics.total) * 100)
    },
    getStatus(key) {
      switch (key) {
        case "AC":
          return "success"
        case "WA":
          return "exception"
        case "TLE":
        case "MLE":
        case "RE":
          return "warning"
        case "CE":
          return null
      }
    }
  }
}
</script>

<style scoped>
.no-record {
  position: absolute;
  top: 20%;
  left: 22%;
  font-size: 22pt;
}

.result-bar {
  margin-top: 20px;
}
</style>