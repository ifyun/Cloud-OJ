<template>
  <n-switch v-model:value="isDarkTheme" size="small" :rail-style="railStyle">
    <template #icon>
      <n-icon color="#ffc20e" :component="icon!" />
    </template>
  </n-switch>
</template>

<script setup lang="ts">
import { computed, CSSProperties, ref, RendererNode } from "vue"
import { useStore } from "vuex"
import { NIcon, NSwitch } from "naive-ui"
import { DarkModeRound, LightModeRound } from "@vicons/material"
import { Mutations } from "@/store"

const store = useStore()
const icon = ref<RendererNode | null>(null)
const isDarkTheme = computed<boolean>({
  get: () => {
    return store.state.theme != null
  },
  set: (value) => {
    store.commit(Mutations.CHANGE_THEME, value ? "dark" : "light")
  }
})

function railStyle({ checked }: { checked: boolean }) {
  const style: CSSProperties = { boxShadow: "none" }

  if (checked) {
    // 暗色主题
    style.background = "#26292f"
    icon.value = DarkModeRound
  } else {
    // 亮色主题
    style.background = "#f6f6f9"
    icon.value = LightModeRound
  }

  return style
}
</script>
