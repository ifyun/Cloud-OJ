<template>
  <div style="display: flex; justify-content: center">
    <div id="heatmap" style="width: 800px; height: 180px" />
  </div>
</template>

<script setup lang="ts">
import { type Activity } from "@/api/type"
import { useStore } from "@/store"
import { HeatmapChart, type HeatmapSeriesOption } from "echarts/charts"
import {
  CalendarComponent,
  type CalendarComponentOption,
  VisualMapComponent,
  type VisualMapComponentOption
} from "echarts/components"
import * as echarts from "echarts/core"
import { type EChartsType } from "echarts/core"
import { SVGRenderer } from "echarts/renderers"
import { computed, nextTick, onMounted, watch } from "vue"

type ECOption = echarts.ComposeOption<
  HeatmapSeriesOption | CalendarComponentOption | VisualMapComponentOption
>

echarts.use([SVGRenderer, HeatmapChart, CalendarComponent, VisualMapComponent])

const light = {
  calendar: {
    itemStyle: {
      color: "#EBEEF5",
      borderColor: "#FFFFFF"
    }
  },
  visualMap: {
    color: ["#2F6D39", "#58C363"]
  }
}

const dark = {
  calendar: {
    itemStyle: {
      color: "#161B22",
      borderColor: "#0D1117"
    },
    dayLabel: {
      color: "#FFFFFF"
    },
    monthLabel: {
      color: "#FFFFFF"
    },
    yearLabel: {
      color: "#FFFFFF"
    }
  },
  visualMap: {
    color: ["#39D353", "#0E4429"],
    textStyle: {
      color: "#FFFFFF"
    }
  }
}

const store = useStore()
const props = defineProps<{ data: Array<Activity>; year: string }>()

const heatmapTheme = computed(() => {
  return store.app.theme == null ? light : dark
})

let heatmap: EChartsType | null = null

const option: ECOption = {
  calendar: {
    top: 24,
    left: 100,
    right: 100,
    range: props.year,
    cellSize: ["auto", 12],
    dayLabel: {
      firstDay: 1,
      nameMap: "EN"
    },
    yearLabel: {
      show: true
    },
    splitLine: {
      show: false
    },
    itemStyle: {
      borderWidth: 2
    }
  },
  visualMap: {
    min: 0,
    max: 10,
    type: "piecewise",
    orient: "horizontal",
    left: "center"
  },
  series: {
    type: "heatmap",
    coordinateSystem: "calendar",
    data: []
  }
}

watch(heatmapTheme, (newVal) => {
  echarts.dispose(heatmap!)
  heatmap = echarts.init(document.getElementById("heatmap")!, newVal)
  heatmap.setOption(option)
})

watch(
  () => props.data,
  (newVal) => {
    nextTick(() => {
      newVal.forEach((val) => {
        ;(option.series as HeatmapSeriesOption).data?.push([
          val.date,
          val.count
        ])
      })
      heatmap?.setOption(option)
    })
  }
)

onMounted(() => {
  heatmap = echarts.init(
    document.getElementById("heatmap")!,
    heatmapTheme.value
  )
  props.data.forEach((val) => {
    ;(option.series as HeatmapSeriesOption).data?.push([val.date, val.count])
  })
  heatmap.setOption(option)
})
</script>
