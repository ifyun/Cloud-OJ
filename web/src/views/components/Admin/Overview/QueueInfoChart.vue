<template>
  <div :id="chartName" class="chart" style="height: 125px; width: 700px" />
</template>

<script lang="ts" setup>
import { type ComputedRef, inject, nextTick, onMounted, watch } from "vue"
import type { EChartsType } from "echarts/core"
import * as echarts from "echarts/core"
import { GridComponent, type GridComponentOption } from "echarts/components"
import { LineChart, type LineSeriesOption } from "echarts/charts"
import { UniversalTransition } from "echarts/features"
import { SVGRenderer } from "echarts/renderers"

echarts.use([LineChart, SVGRenderer, UniversalTransition, GridComponent])
type EChartsOption = echarts.ComposeOption<
  LineSeriesOption | GridComponentOption
>

let chart: EChartsType | null = null
const option: EChartsOption = {
  textStyle: {
    fontFamily: "v-sans, sans-serif"
  },
  backgroundColor: "",
  grid: {
    top: 10,
    bottom: 20,
    left: 50,
    right: 50
  },
  axisPointer: {
    animation: false
  },
  xAxis: {
    type: "time",
    splitNumber: 4,
    splitLine: {
      show: true
    }
  },
  yAxis: {
    type: "value",
    axisLine: {
      show: false
    },
    splitNumber: 1,
    splitLine: {
      show: false
    },
    min: 0,
    max: 1
  },
  series: {
    type: "line",
    showSymbol: false,
    lineStyle: {
      color: "#18a058"
    },
    data: []
  }
}

const props = defineProps<{
  chartName: string
  data: Array<{ sample: number; timestamp: number }>
}>()

const themeStr: ComputedRef<"light" | "dark"> | undefined = inject("themeStr")

watch(themeStr!, (val) => {
  nextTick(() => {
    chart?.dispose()
    initChart(val)
  })
})

watch(
  () => props.data,
  (val) => {
    nextTick(() => {
      if (val.length > 0) {
        ;(option.yAxis as any).max = null
      }
      ;(option.series as any).data = resolveData()
      chart?.setOption(option)
    })
  }
)

onMounted(() => {
  initChart(themeStr!.value)
})

function initChart(theme: string | null) {
  chart = echarts.init(document.getElementById(props.chartName), theme, {
    renderer: "svg"
  })
  ;(option.series as any).data = resolveData()
  chart.setOption(option)
}

function resolveData() {
  const chartData = []

  for (let i = props.data.length - 1; i >= 0; i--) {
    chartData.push({
      name: props.data[i].timestamp,
      value: [props.data[i].timestamp, props.data[i].sample]
    })
  }

  return chartData
}
</script>
