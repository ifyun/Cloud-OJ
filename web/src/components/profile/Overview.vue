<template>
  <div style="position:relative">
    <el-row>
      <el-col :span="16">
        <ECharts :options="pieOption"/>
        <span v-if="pieOption.series[0].data.length === 0" class="no-record">无做题记录</span>
      </el-col>
      <el-col :span="8">
        <b style="font-size: 13pt">结果统计({{ resultStatistics.total }}次提交)</b>
        <div style="margin-top: 70px">
          <div class="result-bar" v-for="(key, index) in resultKeys" :key="index">
            <el-row>
              <el-col :span="4">
                <b :style="{color: getColor(key)}">{{ key }}</b>
              </el-col>
              <el-col :span="20">
                <el-progress :stroke-width="18" :text-inside="true" :color="getColor(key)"
                             :percentage="calcPercentage(key)">
                </el-progress>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-divider></el-divider>
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
      pieOption: {
        title: {
          text: "语言偏好",
          textStyle: {
            color: "#303133",
            fontWeight: "bold"
          }
        },
        tooltip: {
          trigger: "item",
          formatter: '{b}<br>{c} ({d}%)'
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
          text: "年度做题记录",
          textStyle: {
            color: "#303133"
          }
        },
        tooltip: {
          formatter(params) {
            return `${formatDate(params.data[0])}<br>${params.data[1]} 次提交`
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
          top: 80,
          right: 10,
          cellSize: ["auto", 14],
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

      let preferenceData = []
      let activitiesData = []

      for (let i = 0; i < preference.length; i++) {
        preferenceData.push({
          name: languages[parseInt(preference[i]["language"])],
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
    getColor(key) {
      switch (key) {
        case "AC":
          return "#73C13B"
        case "WA":
          return "#EB6E6F"
        case "TLE":
        case "MLE":
          return "#F6925F"
        case "RE":
        case "CE":
          return "#909399"
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

.activities {
  width: 100%;
  height: 250px;
}
</style>