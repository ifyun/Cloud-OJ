<template>
  <div v-if="showChart" ref="chartElem" v-bind="$attrs"></div>
  <n-empty v-else description="没有记录" />
</template>

<script setup lang="ts">
import type { LanguageUserUsage } from "@/api/type"
import { LanguageColors, LanguageNames } from "@/type"
import { PieChart, type PieSeriesOption } from "echarts/charts"
import {
  AriaComponent,
  TooltipComponent,
  type LegendComponentOption,
  type TooltipComponentOption
} from "echarts/components"
import type { EChartsType } from "echarts/core"
import * as echarts from "echarts/core"
import { LabelLayout } from "echarts/features"
import { SVGRenderer } from "echarts/renderers"
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
const props = defineProps<{ data: LanguageUserUsage[] }>()
const themeStr = inject<ComputedRef<"light" | "dark">>("themeStr")

type EChartsOption = echarts.ComposeOption<
  TooltipComponentOption | LegendComponentOption | PieSeriesOption
>

echarts.use([
  AriaComponent,
  LabelLayout,
  PieChart,
  SVGRenderer,
  TooltipComponent
])

let chart: EChartsType | null = null
const chartElem = ref<HTMLElement | null>()
const option: EChartsOption = {
  backgroundColor: "transparent",
  tooltip: {
    trigger: "item",
    formatter: (p: any) => `${p.marker} ${p.name} ${p.value} 个用户`
  },
  aria: {
    enabled: true,
    decal: {
      show: true
    }
  },
  series: [
    {
      name: "用户语言统计",
      type: "pie",
      padAngle: 4,
      itemStyle: {
        borderRadius: 4
      },
      radius: ["45%", "80%"],
      data: []
    }
  ]
}

const showChart = computed(() => props.data.length > 0)
const chartData = computed(() =>
  props.data.map((item) => ({
    name: LanguageNames[item.language],
    value: item.users,
    itemStyle: {
      color: LanguageColors[item.language]
    }
  }))
)

const observer = new ResizeObserver(() => {
  requestAnimationFrame(() => {
    chart?.resize()
  })
})

watch(chartData, (val) => {
  nextTick(() => {
    ;(option.series as any)[0].data = val
    chart?.setOption(option)
  })
})

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
  observer.disconnect()
  chart?.dispose()
})

function initChart(theme: string | null) {
  chart = echarts.init(chartElem.value, theme, {
    renderer: "svg"
  })
  ;(option.series as any)[0].data = chartData.value
  chart.setOption(option)
}
</script>
