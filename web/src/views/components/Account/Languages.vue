<template>
  <div>
    <n-h3 strong style="margin-bottom: 12px">常用语言</n-h3>
    <n-empty
      v-if="data.length === 0"
      description="没有记录"
      style="margin-top: 42px" />
    <div class="languages">
      <div
        v-for="item in data"
        :key="item.name"
        class="language-bar"
        :style="{ flexGrow: item.percent, backgroundColor: item.color }" />
    </div>
    <div class="language-detail">
      <div v-for="item in data" :key="item.name" class="item">
        <n-icon :color="item.color" size="12">
          <circle-round />
        </n-icon>
        <n-text depth="1" strong style="margin-left: 12px"
          >{{ item.name }}
        </n-text>
        <n-text depth="3" style="margin-left: 6px">{{ item.percent }}% </n-text>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type Language } from "@/api/type"
import { LanguageColors, LanguageNames } from "@/type"
import { CircleRound } from "@vicons/material"
import { NEmpty, NH3, NIcon, NText } from "naive-ui"
import { computed } from "vue"

const props = defineProps<{ languages: Array<Language> }>()

type LanguageItem = {
  name: string
  count: number
  percent: number
  color: string
}

const data = computed<Array<LanguageItem>>(() => {
  const total = props.languages.reduce((acr, cur) => acr + cur.count, 0)
  const items: Array<LanguageItem> = []

  props.languages.forEach((val) => {
    items.push({
      name: LanguageNames[val.language],
      count: val.count,
      percent: Number(((val.count / total) * 100).toFixed(1)),
      color: LanguageColors[val.language]
    })
  })

  items.sort((a, b) => {
    return b.count - a.count
  })

  return items
})
</script>

<style scoped lang="scss">
.languages {
  display: flex;
  flex-direction: row;
  height: 10px;

  .language-bar {
    margin-left: 1px;

    &:first-child {
      margin-left: 0;
      border-top-left-radius: 5px;
      border-bottom-left-radius: 5px;
    }

    &:last-child {
      border-top-right-radius: 5px;
      border-bottom-right-radius: 5px;
    }
  }
}

.language-detail {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  width: 350px;
  margin-top: 10px;

  *:nth-child(odd) {
    margin-left: 0;
  }

  *:nth-child(even) {
    margin-left: 24px;
  }

  .item {
    display: flex;
    align-items: center;
    margin-top: 4px;
    width: 135px;
  }
}
</style>
