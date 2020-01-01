<template>
  <div>
    <ECharts theme="macarons" :options="pieOption" style=""/>
    <el-divider></el-divider>
    <ECharts theme="royal" :options="activitiesOption" style="width: 100%;"/>
  </div>
</template>

<script>
import ECharts from 'vue-echarts'
import 'echarts/lib/chart/pie'
import 'echarts/theme/macarons'
import 'echarts/theme/royal'
import {apiPath, userInfo} from "@/js/util";

const year = new Date().getFullYear()
const languages = ['C', 'C++', 'Java', 'Python', 'Bash', 'C#']

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
            center: ['45%', '50%'],
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
        this.$notify.error({
          offset: 50,
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

</style>