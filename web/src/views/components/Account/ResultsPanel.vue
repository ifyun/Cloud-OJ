<template>
  <div>
    <n-h3 strong style="margin-bottom: 12px">结果统计</n-h3>
    <n-empty v-if="total === 0" description="没有记录" />
    <n-flex v-else vertical :size="0">
      <n-flex v-for="(v, index) in data" :key="index" align="center">
        <n-text>{{ v.name }}</n-text>
        <n-progress
          type="line"
          :show-indicator="false"
          :color="v.color"
          :percentage="v.percentage"
          style="flex: auto; width: 0" />
      </n-flex>
      <n-text strong>正确率：{{ acRate }}%</n-text>
    </n-flex>
  </div>
</template>

<script setup lang="ts">
import { type Results } from "@/api/type"
import { ResultColors, ResultNames } from "@/type"
import { NEmpty, NFlex, NH3, NProgress, NText } from "naive-ui"
import { computed } from "vue"

const props = defineProps<{
  data: Results
}>()

const total = computed(() =>
  Object.values(props.data).reduce((acc, cur) => acc + cur, 0)
)

const data = computed(() => {
  return Object.entries(props.data).map((v) => {
    const p = v[1] / total.value
    return {
      name: ResultNames[v[0]],
      color: ResultColors[v[0]],
      percentage: isFinite(p) ? p * 100 : 0
    }
  })
})

const acRate = computed(() => {
  if (total.value === 0) {
    return 0
  }

  return Math.round((props.data["AC"] / total.value) * 100)
})
</script>
