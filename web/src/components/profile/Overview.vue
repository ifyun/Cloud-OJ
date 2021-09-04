<template>
  <div style="position:relative">
    <el-row>
      <el-col :span="12">
        <span class="title">语言偏好</span>
        <div class="pie-chart" ref="languages"/>
        <el-empty v-if="!loading && languagesOption.series[0].data.length === 0" class="no-record"/>
      </el-col>
      <el-col :span="12">
        <span class="title">结果统计({{ resultStatistics.total }}次提交)</span>
        <div class="pie-chart" ref="results"/>
      </el-col>
    </el-row>
    <span class="title">年度做题记录</span>
    <div style="display: flex; flex-direction: column; align-items: center">
      <el-date-picker style="margin-top: 10px" size="mini" type="year" format="yyyy 年"
                      placeholder="选择年" :editable="false" :clearable="false"
                      v-model="year" @change="changeYear">
      </el-date-picker>
      <div id="activities" ref="activities"/>
    </div>
  </div>
</template>

<script>
import green from "@/assets/theme/echarts-theme"
import * as echarts from "echarts/core"
import {
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  CalendarComponent,
  VisualMapComponent
} from "echarts/components"
import {
  PieChart,
  HeatmapChart
} from "echarts/charts"
import {
  CanvasRenderer
} from "echarts/renderers"
import {userInfo} from "@/util"
import {languages} from "@/util/data"
import {UserApi} from "@/service"
import moment from "moment"

const currentYear = new Date()
const formatDate = (time) => {
  return moment(time).format("M月D日")
}

const resultKeys = ["CE", "AC", "TLE", "WA", "RE", "MLE"]

echarts.use([
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  CalendarComponent,
  VisualMapComponent,
  PieChart,
  HeatmapChart,
  CanvasRenderer
])

echarts.registerTheme("green", green)

export default {
  name: "Overview",
  props: ["userId"],
  watch: {
    languagesOption: {
      handler(newVal) {
        this.languages.setOption(newVal)
      },
      deep: true
    },
    resultsOption: {
      handler(newVal) {
        this.results.setOption(newVal)
      },
      deep: true
    },
    activitiesOption: {
      handler(newVal) {
        this.activities.clear()
        this.activities.setOption(newVal)
      },
      deep: true
    }
  },
  beforeMount() {
    this.getOverview()
  },
  mounted() {
    this.languages = echarts.init(this.$refs.languages, null)
    this.results = echarts.init(this.$refs.results, null)
    this.activities = echarts.init(this.$refs.activities, "green")
  },
  data() {
    return {
      loading: true,
      languages: null,
      results: null,
      activities: null,
      year: currentYear,
      resultStatistics: {
        AC: 0,
        WA: 0,
        TLE: 0,
        MLE: 0,
        RE: 0,
        CE: 0,
        total: 0,
      },
      languagesOption: {
        tooltip: {
          trigger: "item"
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
            center: ["55%", "40%"],
            radius: ["25%", "45%"],
            itemStyle: {
              borderRadius: 6,
              borderColor: '#FFFFFF',
              borderWidth: 2
            },
            data: []
          }
        ]
      },
      resultsOption: {
        title: {
          show: true,
          text: "",
          left: "53%",
          top: "35%",
          textStyle: {
            fontSize: 16,
            lineHeight: 18
          },
          textAlign: "center"
        },
        tooltip: {
          trigger: "item"
        },
        legend: {
          left: "left",
          top: "15",
          orient: "vertical",
          data: resultKeys
        },
        series: [
          {
            name: "结果统计",
            type: "pie",
            center: ["55%", "40%"],
            radius: ["25%", "45%"],
            itemStyle: {
              borderRadius: 6,
              borderColor: '#FFFFFF',
              borderWidth: 2
            },
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
          max: 10,
          maxOpen: true,
          type: "piecewise",
          orient: "horizontal",
          left: "center",
          bottom: 30
        },
        calendar: {
          top: 50,
          right: 0,
          cellSize: ["auto", "14"],
          range: currentYear.getFullYear(),
          dayLabel: {
            firstDay: 1,
            nameMap: "en"
          },
          monthLabel: {
            nameMap: "cn"
          },
          itemStyle: {
            color: "#EBEEF5",
            borderColor: "#FFFFFF",
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
    changeYear(date) {
      if (date.getFullYear() > currentYear.getFullYear()) {
        this.year = currentYear
      }
      this.activitiesOption.calendar.range = this.year.getFullYear()
      this.getOverview()
    },
    getOverview() {
      this.loading = true
      UserApi.getStatistics(this.userId == null ? userInfo().userId : this.userId,
          this.year.getFullYear())
          .then((data) => {
            this.setCharts(data)
          })
          .catch((error) => {
            this.$emit("error", error)
          })
          .finally(() => {
            this.loading = false
          })
    },
    setCharts(overview) {
      this.resultStatistics = overview["statistics"]
      let preference = overview["preference"]
      let activities = overview["activities"]

      let preferenceLegend = []
      let preferenceData = []
      let resultsData = []
      let activitiesData = []

      for (let i = 0; i < preference.length; i++) {
        preferenceLegend.push(languages[preference[i].language].name)
        preferenceData.push({
          name: languages[preference[i].language].name,
          value: preference[i].count
        })
      }

      for (let i = 0; i < resultKeys.length; i++) {
        let key = resultKeys[i]
        resultsData.push({
          name: key,
          value: this.resultStatistics[key]
        })
      }

      for (let i = 0; i < activities.length; i++) {
        activitiesData.push([
          activities[i].date,
          activities[i].count
        ])
      }

      let passRate = this.resultStatistics.AC * 100 / this.resultStatistics.total

      if (isNaN(passRate)) {
        passRate = 0
      } else {
        passRate = passRate.toFixed(2)
      }

      this.languagesOption.legend.data = preferenceLegend
      this.languagesOption.series[0].data = preferenceData
      this.resultsOption.series[0].data = resultsData
      this.resultsOption.title.text = `通过率\n${passRate}%`
      this.activitiesOption.series.data = activitiesData
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
  top: 10%;
  left: 15%;
  font-size: 22pt;
}

.pie-chart {
  height: 400px;
  width: 420px;
}

#activities {
  width: 790px;
  height: 250px;
  margin: 0 auto;
}
</style>