<template>
  <div ref="chartElem" style="height: 125px"></div>
</template>

<script lang="ts" setup>
import { LineChart, type LineSeriesOption } from "echarts/charts"
import { GridComponent, type GridComponentOption } from "echarts/components"
import type { EChartsType } from "echarts/core"
import * as echarts from "echarts/core"
import { UniversalTransition } from "echarts/features"
import { SVGRenderer } from "echarts/renderers"
import {
  type ComputedRef,
  inject,
  nextTick,
  onActivated,
  onDeactivated,
  ref,
  watch
} from "vue"

echarts.use([LineChart, SVGRenderer, UniversalTransition, GridComponent])
type EChartsOption = echarts.ComposeOption<
  LineSeriesOption | GridComponentOption
>

let chart: EChartsType | null = null
const chartElem = ref<HTMLElement | null>()
const option: EChartsOption = {
  textStyle: {
    fontFamily: "v-sans, sans-serif"
  },
  backgroundColor: "",
  grid: {
    top: 10,
    bottom: 20,
    left: 30,
    right: 30
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
  data: Array<{ sample: number; timestamp: number }>
}>()

const themeStr: ComputedRef<"light" | "dark"> | undefined = inject("themeStr")
const observer = new ResizeObserver(() => {
  requestAnimationFrame(() => {
    chart?.resize()
  })
})

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

onActivated(() => {
  initChart(themeStr!.value)
  observer.observe(chartElem.value!)
})

onDeactivated(() => {
  observer.disconnect()
  chart?.dispose()
})

function initChart(theme: string | null) {
  chart = echarts.init(chartElem.value, theme, {
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
