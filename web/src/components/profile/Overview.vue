<template>
  <div style="position:relative">
    <el-row>
      <el-col :span="16">
        <span class="title">语言偏好</span>
        <ECharts :options="pieOption"/>
        <span v-if="pieOption.series[0].data.length === 0" class="no-record">无做题记录</span>
      </el-col>
      <el-col :span="8">
        <span class="title">结果统计({{ resultStatistics.total }}次提交)</span>
        <div style="margin-top: 35px">
          <div class="result-bar" v-for="(key, index) in resultKeys" :key="index">
            <el-row>
              <el-col :span="4">
                <b :style="{color: resultColor.get(key)}">{{ key }}</b>
              </el-col>
              <el-col :span="20">
                <el-progress :stroke-width="10" :color="resultColor.get(key)" :percentage="calcPercentage(key)">
                </el-progress>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
    <span class="title">年度提交记录</span>
    <ECharts class="activities" theme="green" :options="activitiesOption"/>
  </div>
</template>

<script>
import ECharts from "vue-echarts"
import "echarts/lib/chart/pie"
import "echarts/lib/chart/heatmap"
import "echarts/lib/component/title"
import "echarts/lib/component/tooltip"
import "echarts/lib/component/legend"
import "echarts/lib/component/calendar"
import "echarts/lib/component/visualMap"
import green from "@/assets/theme/echarts-theme"
import {userInfo} from "@/util"
import {languages} from "@/util/data"
import {UserApi} from "@/service"
import moment from "moment"

const year = new Date().getFullYear()
const formatDate = (time) => {
  return moment(time).format("M月D日")
}

export default {
  name: "Overview",
  components: {
    ECharts
  },
  beforeMount() {
    ECharts.registerTheme("green", green)
    this.getOverview()
  },
  props: ["userId"],
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
      resultColor: new Map([
        ["AC", "#73C13B"],
        ["WA", "#EB6E6F"],
        ["TLE", "#F6925F"],
        ["MLE", "#F6925F"],
        ["RE", "#909399"],
        ["CE", "#909399"]
      ]),
      pieOption: {
        tooltip: {
          trigger: "item",
          formatter: "{b}<br>{c} ({d}%)"
        },
        legend: {
          left: "left",
          top: "15",
          orient: "vertical",
          data: []
        },
        series: [
          {
            name: "语言偏好",
            type: "pie",
            center: ["50%", "40%"],
            radius: [35, 125],
            itemStyle: {
              borderRadius: 6
            },
            roseType: "area",
            data: []
          }
        ]
      },
      activitiesOption: {
        tooltip: {
          formatter(params) {
            return `${formatDate(params.data[0])}<br>${params.data[1]} AC`
          }
        },
        visualMap: {
          min: 0,
          max: 15,
          maxOpen: true,
          type: "piecewise",
          orient: "horizontal",
          left: "center",
          bottom: 20
        },
        calendar: {
          top: 60,
          right: 0,
          cellSize: ["auto", "14"],
          range: year,
          dayLabel: {
            firstDay: 1,
            nameMap: "en"
          },
          monthLabel: {
            nameMap: "cn"
          },
          itemStyle: {
            color: "#ebeef5",
            borderColor: "#ffffff",
            borderWidth: 3,
          },
          splitLine: {
            show: false
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
      UserApi.getStatistics(this.userId == null ? userInfo().userId : this.userId, year)
          .then((data) => {
            this.setCharts(data)
          })
          .catch((error) => {
            this.$emit("error", error)
          })
    },
    setCharts(overview) {
      this.resultStatistics = overview["statistics"]
      let preference = overview["preference"]
      let activities = overview["activities"]

      let preferenceLegend = []
      let preferenceData = []
      let activitiesData = []

      for (let i = 0; i < preference.length; i++) {
        preferenceLegend.push(languages[preference[i].language].name)
        preferenceData.push({
          name: languages[preference[i].language].name,
          value: preference[i].count
        })
      }

      for (let i = 0; i < activities.length; i++) {
        activitiesData.push([
          activities[i].date,
          activities[i].count
        ])
      }

      this.pieOption.legend.data = preferenceLegend
      this.pieOption.series[0].data = preferenceData
      this.activitiesOption.series.data = activitiesData
    },
    calcPercentage(key) {
      if (this.resultStatistics.total === 0) {
        return 0
      }
      return Math.round((this.resultStatistics[key] / this.resultStatistics.total) * 100)
    }
  }
}
</script>

<style scoped>
.title {
  font-size: 13pt;
  font-weight: bold;
}

.no-record {
  position: absolute;
  top: 20%;
  left: 22%;
  font-size: 22pt;
}

.result-bar {
  margin-top: 20px;
}

.activities {
  width: 820px;
  height: 250px;
  margin: 0 auto;
}
</style>