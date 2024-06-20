<template>
  <div style="display: flex; flex-direction: column; align-items: center">
    <div class="active-chart">
      <ul class="weeks">
        <li class="week-day"></li>
        <li class="week-day">二</li>
        <li class="week-day"></li>
        <li class="week-day">四</li>
        <li class="week-day"></li>
        <li class="week-day">六</li>
        <li class="week-day"></li>
      </ul>
      <!-- Month + Grid -->
      <div>
        <ul class="months">
          <li
            v-for="(month, index) in months"
            :key="index"
            class="li-month"
            :style="
              month != null
                ? { gridColumn: `${index + 1} / span 3` }
                : { display: 'none' }
            ">
            {{ month }}
          </li>
        </ul>
        <ul class="graph" :data-theme="theme">
          <n-popover
            v-for="(info, index) in infoList"
            :key="index"
            :duration="0"
            :show-arrow="false">
            <template #trigger>
              <li class="day" :data-level="info.level" />
            </template>
            <n-text>
              {{ `${info.month}月${info.date}日: ${info.count} AC` }}
            </n-text>
          </n-popover>
        </ul>
      </div>
    </div>
    <n-text depth="3" style="font-size: small">
      (不同语言AC同一题算作多次)
    </n-text>
  </div>
</template>

<script setup lang="ts">
import { inject, nextTick, ref, watch } from "vue"
import { NPopover, NText } from "naive-ui"
import dayjs from "dayjs"

type Info = {
  year: number
  month: number
  date: number
  count: number
  level: number
}

type Data = {
  [date: string]: number
}

const props = withDefaults(defineProps<{ data: Data }>(), {
  data: () => {
    return {}
  }
})

const months = ref<Array<string | null>>(new Array<string>(52))
const infoList = ref<Array<Info>>([])
const theme = inject("themeStr")

watch(
  () => props.data,
  (val) => {
    nextTick(() => createChartData(val))
  }
)

function createChartData(data: Data) {
  months.value = new Array<string>(52)
  infoList.value = []

  let d = new Date()
  const today = new Date()
  const day = today.getDay()
  let prevMonth: number = -1

  for (let i = 0; i < 364; i++) {
    // jump to days ago
    const days = 363 - (7 - day) - i
    d = new Date(today.getTime() - days * 24 * 3600 * 1000)

    if (prevMonth == -1) {
      prevMonth = d.getMonth()
    }

    if (days === -1) {
      break
    }

    const dateStr = dayjs(d).format("YYYY-MM-DD")
    const count = data[dateStr] ?? 0
    const level = calcLevel(count)
    const info: Info = {
      year: d.getFullYear(),
      month: d.getMonth() + 1,
      date: d.getDate(),
      count,
      level
    }

    infoList.value.push(info)

    if (d.getDay() == 1 && d.getMonth() !== prevMonth) {
      // current day is monday
      const month = d.getMonth() + 1
      const weekOfMonth = Math.floor((i + 1) / 7)
      months.value[weekOfMonth] = month + "月"
      prevMonth = d.getMonth()
    }
  }
}

function calcLevel(count: number) {
  if (count === 0) {
    return 0
  }

  if (count < 3) {
    return 1
  }

  if (count < 6) {
    return 2
  }

  if (count < 9) {
    return 3
  }

  return 4
}
</script>

<style lang="scss" scoped>
.active-chart {
  display: flex;
  flex-direction: row;
  margin-bottom: 12px;
}

.months {
  display: grid;
  grid-template-columns: repeat(52, 12px);
  font-size: small;
  padding-inline-start: 0;
  margin: 0;

  .li-month {
    list-style: none;
    letter-spacing: 2px;
  }
}

.weeks {
  display: grid;
  grid-template-rows: repeat(7, 12px);
  grid-auto-flow: column;
  margin: 19px 4px 0 0;

  .week-day {
    list-style: none;
    font-size: smaller;
  }
}

.graph {
  display: grid;
  grid-template-columns: repeat(52, 12px);
  grid-template-rows: repeat(7, 12px);
  padding-inline-start: 0;
  grid-auto-flow: column;
  margin: 0;

  .day {
    list-style: none;
    margin: 1px;
    border-radius: 3px;
    cursor: pointer;
  }

  &[data-theme="dark"] {
    .day {
      background: #161b22;

      &[data-level="1"] {
        background: #0e4429;
      }

      &[data-level="2"] {
        background: #006d32;
      }

      &[data-level="3"] {
        background: #26a641;
      }

      &[data-level="4"] {
        background: #39d353;
      }
    }
  }

  &[data-theme="light"] {
    .day {
      background: #ebedf0;
      outline: 1px solid #dfe1e4;
      outline-offset: -1px;

      &[data-level="1"] {
        background: #9be9a8;
      }

      &[data-level="2"] {
        background: #40c463;
      }

      &[data-level="3"] {
        background: #30a14e;
      }

      &[data-level="3"] {
        background: #216e39;
      }
    }
  }
}
</style>
