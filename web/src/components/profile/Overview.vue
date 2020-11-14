<template>
  <div style="position:relative">
    <ECharts theme="macarons" :options="pieOption"/>
    <span v-if="pieOption.series[0].data.length === 0" class="no-record">无做题记录</span>
    <el-divider></el-divider>
    <ECharts theme="royal" :options="activitiesOption" style="width: 100%;height: 200px"/>
  </div>
</template>

<script>
import ECharts from 'vue-echarts'
import 'echarts/lib/chart/pie'
import 'echarts/theme/macarons'
import 'echarts/theme/royal'
import {Notice, userInfo} from "@/script/util"
import {apiPath} from "@/script/env"

const year = new Date().getFullYear()
const languages = ['C', 'C++', 'Java', 'Python', 'Bash', 'C#', 'JavaScript', 'Kotlin']

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
      pieOption: {
        title: {
          text: '语言偏好',
          textStyle: {
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'left',
          top: '50',
          orient: 'vertical',
          data: languages
        },
        series: [
          {
            name: '语言偏好',
            type: 'pie',
            center: ['55%', '50%'],
            radius: [35, 120],
            roseType: 'area',
            data: []
          }
        ]
      },
      activitiesOption: {
        title: {
          top: 0,
          text: '年度做题情况',
          textStyle: {
            color: '#333'
          }
        },
        tooltip: {},
        visualMap: {
          min: 0,
          max: 20,
          type: 'piecewise',
          orient: 'horizontal',
          left: 'center',
          top: 35
        },
        calendar: {
          top: 100,
          right: 10,
          cellSize: ['auto', 14],
          range: '2020',
          dayLabel: {
            firstDay: 1,
            nameMap: 'cn'
          },
          monthLabel: {
            nameMap: 'cn'
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
          type: 'heatmap',
          coordinateSystem: 'calendar',
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
          title: '获取数据失败',
          message: `${res.status} ${res.statusText}`
        })
      })
    },
    setChartsData(overview) {
      let preference = overview['preference']
      let activities = overview['activities']

      let preferenceData = []
      let activitiesData = []

      for (let i = 0; i < preference.length; i++) {
        preferenceData.push({
          name: languages[preference[i]['language']],
          value: preference[i]['count']
        })
      }

      for (let i = 0; i < activities.length; i++) {
        activitiesData.push([
          activities[i]['date'],
          activities[i]['count']
        ])
      }

      this.pieOption.series[0].data = preferenceData
      this.activitiesOption.series.data = activitiesData
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
</style>