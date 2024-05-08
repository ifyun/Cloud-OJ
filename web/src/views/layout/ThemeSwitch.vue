<template>
  <n-switch v-model:value="isDarkTheme" size="small" :rail-style="railStyle">
    <template #icon>
      <n-icon color="#ffc20e" :component="icon!" />
    </template>
  </n-switch>
</template>

<script setup lang="ts">
import { useStore } from "@/store"
import { DarkModeRound, LightModeRound } from "@vicons/material"
import { NIcon, NSwitch } from "naive-ui"
import { computed, type CSSProperties, type RendererNode } from "vue"

let icon: RendererNode | null = null

const store = useStore()
const isDarkTheme = computed<boolean>({
  get: () => {
    return store.app.theme != null
  },
  set: (value) => {
    store.app.setTheme(value ? "dark" : "light")
  }
})

function railStyle({ checked }: { checked: boolean }) {
  const style: CSSProperties = { boxShadow: "none" }

  if (checked) {
    // 暗色主题
    style.background = "#26292f"
    icon = DarkModeRound
  } else {
    // 亮色主题
    style.background = "#f6f6f9"
    icon = LightModeRound
  }

  return style
}
</script>
