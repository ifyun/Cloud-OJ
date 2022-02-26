<template>
  <div>
    <n-h4 strong style="margin-bottom: 12px">常用语言</n-h4>
    <div class="languages">
      <div class="language-bar" v-for="item in data" :key="item.name"
           :style="{flexGrow: item.percent, backgroundColor: item.color}"/>
    </div>
    <div class="language-detail">
      <div v-for="item in data" :key="item.name">
        <div class="item">
          <n-icon :color="item.color">
            <circle-round/>
          </n-icon>
          <n-text depth="1" strong>{{ item.name }}</n-text>
          <n-text depth="3">{{ item.percent.toFixed(2) * 100 }}%</n-text>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, defineProps} from "vue"
import {NH4, NIcon, NText} from "naive-ui"
import {CircleRound} from "@vicons/material"
import {Language} from "@/api/type"

const names = ["C", "C++", "Java", "Python", "Bash", "JavaScript", "Kotlin", "Go"]
const colors = ["#555555", "#F34B7D", "#B07219", "#3572A5", "#89E051", "#F1E05A", "#A97BFF", "#00ADD8"]

const props = defineProps<{ languages: Array<Language> }>()

type LanguageItem = {
  name: string
  count: number
  percent: number
  color: string
}

const data = computed<Array<LanguageItem>>(() => {
  const total = props.languages.length
  const items: Array<LanguageItem> = []

  props.languages.forEach(val => {
    items.push({
      name: names[val.language],
      count: val.count,
      percent: val.count / total,
      color: colors[val.language]
    })
  })

  return items
})
</script>

<style scoped lang="scss">
.languages {
  display: flex;
  flex-direction: row;
  height: 12px;

  .language-bar {
    margin-left: 1px;

    &:first-child {
      margin-left: 0;
      border-top-left-radius: 6px;
      border-bottom-left-radius: 6px;
    }

    &:last-child {
      border-top-right-radius: 6px;
      border-bottom-right-radius: 6px;
    }
  }
}

.language-detail {
  display: flex;
  flex-direction: column;
  margin-top: 6px;

  .item {
    display: flex;
    align-items: center;

    *:first-child {
      margin-left: 0;
    }

    *:nth-child(2) {
      margin-left: 10px;
    }

    *:nth-child(3) {
      margin-left: 6px;
    }
  }
}
</style>
