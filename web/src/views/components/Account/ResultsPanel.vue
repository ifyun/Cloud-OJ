<template>
  <div>
    <div id="pie" style="width: 100%; height: 210px" />
  </div>
</template>

<script setup lang="ts">
import { Statistics } from "@/api/type"
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
import { computed, nextTick, onMounted, watch } from "vue"
import { useStore } from "@/store"

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
  "#4eaa25",
  "#d14748",
  "#f9a825",
  "#0288d1",
  "#777777",
  "#673ab7"
]

const light = {
  color: chartColor
}

/* 饼图暗色主题 */
const dark = {
  color: chartColor,
  title: {
    textStyle: {
      color: "white"
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
  title: {
    text: "结果统计",
    textStyle: {
      fontSize: 18,
      fontWeight: 500
    }
  },
  legend: {
    left: "right",
    top: 20,
    orient: "vertical",
    data: keys
  },
  tooltip: {
    trigger: "item"
  },
  series: {
    type: "pie",
    itemStyle: {
      borderRadius: 4,
      borderWidth: 2
    },
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
      keys.forEach((key) => {
        ;(option.series as PieSeriesOption).data?.push({
          name: key,
          value: (val as any)[key]
        })
      })
      pieChart?.setOption(option)
      pieChart?.dispatchAction({
        type: "highlight",
        seriesIndex: 0,
        dataIndex: 0
      })
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
