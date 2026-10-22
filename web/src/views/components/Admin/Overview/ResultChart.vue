<template>
  <div v-if="showChart" ref="chartElem" v-bind="$attrs"></div>
  <n-empty v-else description="没有记录" />
</template>

<script setup lang="ts">
import type { Results } from "@/api/type"
import { ResultColors } from "@/type"
import { BarChart, type BarSeriesOption } from "echarts/charts"
import {
  AriaComponent,
  GridComponent,
  type GridComponentOption
} from "echarts/components"
import type { EChartsType } from "echarts/core"
import * as echarts from "echarts/core"
import { CanvasRenderer } from "echarts/renderers"
import { NEmpty } from "naive-ui"
import {
  computed,
  inject,
  nextTick,
  onActivated,
  onDeactivated,
  ref,
  watch,
  type ComputedRef
} from "vue"

defineOptions({
  inheritAttrs: false
})
const props = defineProps<{ data: Results }>()
const themeStr = inject<ComputedRef<"light" | "dark">>("themeStr")

type EChartsOption = echarts.ComposeOption<
  GridComponentOption | BarSeriesOption
>

echarts.use([AriaComponent, BarChart, CanvasRenderer, GridComponent])

let chart: EChartsType | null = null
const chartElem = ref<HTMLElement | null>(null)
const option: EChartsOption = {
  backgroundColor: "transparent",
  aria: {
    enabled: true,
    decal: {
      show: true
    }
  },
  tooltip: {
    trigger: "item"
  },
  grid: {
    top: 8,
    bottom: 0,
    left: 0,
    right: 0,
    containLabel: true
  },
  xAxis: {
    type: "category",
    data: []
  },
  yAxis: {
    type: "value"
  },
  series: [
    {
      type: "bar",
      barMinWidth: 8,
      barMaxWidth: 32,
      data: []
    }
  ]
}

const chartDataX = computed(() => Object.keys(props.data))
const chartDataY = computed(() =>
  Object.entries(props.data).map((v) => ({
    value: v[1],
    itemStyle: { color: ResultColors[v[0]] }
  }))
)

const showChart = computed(
  () => Object.values(props.data).reduce((acc, cur) => acc + cur, 0) > 0
)

const observer = new ResizeObserver(() => {
  requestAnimationFrame(() => {
    chart?.resize()
  })
})

watch(
  () => props.data,
  () => {
    nextTick(() => {
      ;(option.xAxis as any).data = chartDataX.value
      ;(option.series as any)[0].data = chartDataY.value
      chart?.setOption(option)
    })
  }
)

watch(themeStr!, (val) => {
  if (showChart.value) {
    nextTick(() => {
      chart?.dispose()
      initChart(val)
    })
  }
})

watch(showChart, (val) => {
  if (val) {
    nextTick(() => {
      initChart(themeStr!.value)
      observer.observe(chartElem.value!)
    })
  }
})

onActivated(() => {
  if (showChart.value) {
    initChart(themeStr!.value)
    observer.observe(chartElem.value!)
  }
})

onDeactivated(() => {
  chart?.dispose()
  observer.disconnect()
})

function initChart(theme: string | null) {
  chart = echarts.init(chartElem.value, theme, {
    renderer: "svg"
  })
  ;(option.xAxis as any).data = chartDataX.value
  ;(option.series as any)[0].data = chartDataY.value
  chart.setOption(option)
}
</script>
