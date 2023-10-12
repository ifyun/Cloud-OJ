<template>
  <div>
    <n-h3 strong>结果统计</n-h3>
    <div
      id="pie"
      style="margin-top: -36px; width: 100%; height: 210px; z-index: 10" />
  </div>
</template>

<script setup lang="ts">
import { Statistics } from "@/api/type"
import { useStore } from "@/store"
import { PieChart, PieSeriesOption } from "echarts/charts"
import {
  LegendComponent,
  LegendComponentOption,
  TitleComponent,
  TitleComponentOption,
  TooltipComponent,
  TooltipComponentOption
} from "echarts/components"
import * as echarts from "echarts/core"
import { EChartsType } from "echarts/core"
import { SVGRenderer } from "echarts/renderers"
import { NH3 } from "naive-ui"
import { computed, nextTick, onMounted, watch } from "vue"

type ECOption = echarts.ComposeOption<
  | PieSeriesOption
  | LegendComponentOption
  | TooltipComponentOption
  | TitleComponentOption
>

echarts.use([
  SVGRenderer,
  PieChart,
  LegendComponent,
  TooltipComponent,
  TitleComponent
])

/* 饼图实例 */
let pieChart: EChartsType | null = null

const chartColor = [
  "#18A058",
  "#F56C6C",
  "#EBB563",
  "#409EFF",
  "#777770",
  "#DDDDD0"
]

const light = {
  color: chartColor
}

/* 饼图暗色主题 */
const dark = {
  color: chartColor,
  title: {
    textStyle: {
      color: "#FFFFFFE6"
    }
  },
  legend: {
    textStyle: {
      color: "white"
    }
  },
  label: {
    color: "white"
  }
}

const keys = ["AC", "WA", "TLE", "MLE", "RE", "CE"]

const store = useStore()

const props = defineProps<{
  data: Statistics
}>()

const chartTheme = computed(() => {
  return store.app.theme == null ? light : dark
})

const option: ECOption = {
  legend: {
    left: "right",
    top: 32,
    orient: "vertical",
    data: keys
  },
  tooltip: {
    trigger: "item"
  },
  series: {
    type: "pie",
    center: ["50%", "50%"],
    radius: ["40%", "60%"],
    data: []
  }
}

watch(chartTheme, (val) => {
  echarts.dispose(pieChart!)
  pieChart = echarts.init(document.getElementById("pie")!, val)
  pieChart.setOption(option)
})

watch(
  () => props.data,
  (val: Statistics) => {
    nextTick(() => {
      ;(option.series as PieSeriesOption).data = []

      if (val.total === 0) {
        ;(option.legend as LegendComponentOption).data = []
      } else {
        ;(option.legend as LegendComponentOption).data = keys
        keys.forEach((key) => {
          ;(option.series as PieSeriesOption).data?.push({
            name: key,
            value: (val as any)[key]
          })
        })
      }

      pieChart?.setOption(option)
    })
  }
)

onMounted(() => {
  keys.forEach((key) => {
    ;(option.series as PieSeriesOption).data?.push({
      name: key,
      value: (props.data as any)[key]
    })
  })

  pieChart = echarts.init(document.getElementById("pie")!, chartTheme.value)
  pieChart.setOption(option)
})
</script>
