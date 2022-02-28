<template>
  <div id="heatmap" style="width: 100%; height: 210px">
  </div>
</template>

<script setup lang="ts">
import {computed, defineProps, nextTick, onMounted, watch} from "vue"
import {useStore} from "vuex"
import * as echarts from "echarts/core"
import {EChartsType} from "echarts/core"
import {SVGRenderer} from "echarts/renderers"
import {HeatmapChart, HeatmapSeriesOption} from "echarts/charts"
import {
  CalendarComponent,
  CalendarComponentOption,
  VisualMapComponent,
  VisualMapComponentOption
} from "echarts/components"
import {Activity} from "@/api/type"

type ECOption = echarts.ComposeOption<HeatmapSeriesOption | CalendarComponentOption | VisualMapComponentOption>

echarts.use([
  SVGRenderer,
  HeatmapChart,
  CalendarComponent,
  VisualMapComponent
])

const light = {
  calendar: {
    itemStyle: {
      color: "#EBEEF5",
      borderColor: "#FFFFFF"
    },
    splitLine: {
      lineStyle: {
        color: "#000000"
      }
    }
  },
  visualMap: {
    color: [
      "#2F6D39",
      "#58C363"
    ]
  }
}

const dark = {
  calendar: {
    itemStyle: {
      color: "#282930",
      borderColor: "#18181C"
    },
    dayLabel: {
      color: "#FFFFFF"
    },
    monthLabel: {
      color: "#FFFFFF"
    },
    yearLabel: {
      color: "#FFFFFF"
    },
    splitLine: {
      lineStyle: {
        color: "#FFFFFF"
      }
    }
  },
  visualMap: {
    color: [
      "#39D353",
      "#0E4429"
    ],
    textStyle: {
      color: "#FFFFFF"
    }
  }
}

const props = defineProps<{ data: Array<Activity>, year: string }>()
const store = useStore()
const theme = computed(() => store.state.theme)
const heatmapTheme = computed(() => {
  return theme.value == null ? light : dark
})

let heatmap: EChartsType | null = null

const option: ECOption = {
  calendar: {
    top: 24,
    left: 96,
    right: 48,
    range: props.year,
    cellSize: ["auto", 17],
    dayLabel: {
      firstDay: 1,
      nameMap: "EN"
    },
    yearLabel: {
      show: true
    },
    splitLine: {
      show: false,
      lineStyle: {
        width: 0.1
      }
    },
    itemStyle: {
      borderWidth: 4
    }
  },
  visualMap: {
    min: 0,
    max: 15,
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

watch(() => props.data, (newVal) => {
  console.log("watch", newVal)
  nextTick(() => {
    newVal.forEach((val) => {
      (option.series as HeatmapSeriesOption).data?.push([val.date, val.count])
    })
    heatmap?.setOption(option)
  })
})

onMounted(() => {
  heatmap = echarts.init(document.getElementById("heatmap")!, heatmapTheme.value)
  console.log(props.data)
  props.data.forEach((val) => {
    (option.series as HeatmapSeriesOption).data?.push([val.date, val.count])
  })
  heatmap.setOption(option)
})
</script>
