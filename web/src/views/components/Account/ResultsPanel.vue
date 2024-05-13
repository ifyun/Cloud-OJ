<template>
  <div>
    <n-h3 strong>结果统计</n-h3>
    <div style="display: flex">
      <div style="display: flex; flex-direction: column">
        <div
          v-for="(color, index) in colors"
          :key="index"
          style="display: flex; align-items: center; margin-right: auto">
          <div
            :style="{
              backgroundColor: color,
              borderRadius: '4px',
              width: '14px',
              height: '14px',
              marginRight: '4px'
            }" />
          <n-text style="font-size: small">
            {{ names[index] }}
          </n-text>
        </div>
      </div>
      <n-progress
        style="width: 150px; margin-left: 100px"
        type="multiple-circle"
        :stroke-width="4"
        :circle-gap="0.5"
        :color="colors"
        :percentage="percentage">
        <n-text style="font-size: small; text-align: center">
          {{ acRate }}%<br />正确率
        </n-text>
      </n-progress>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type Results } from "@/api/type"
import { NH3, NProgress, NText } from "naive-ui"
import { computed } from "vue"

const colors = [
  "#18A058", // AC
  "#F56C6C", // WA
  "#EBB563", // TLE
  "#409EFF", // MLE
  "#777770", // RE
  "#DDDDD0" // CE
]

const keys = ["AC", "WA", "TLE", "MLE", "RE", "CE"]
const names = ["正确", "错误", "超时", "超内存", "运行错误", "编译错误"]

const props = defineProps<{
  data: Results
}>()

const percentage = computed(() => {
  const arr: Array<number> = []

  keys.forEach((key) => {
    const p = (props.data[key] ?? 0) / props.data.total
    arr.push(isNaN(p) ? 0 : p * 100)
  })

  return arr
})

const acRate = computed(() => {
  if (props.data.total === 0) {
    return 0
  }

  return Math.round((props.data["AC"] / props.data.total) * 100)
})
</script>
