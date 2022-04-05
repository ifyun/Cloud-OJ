<template>
  <n-switch v-model:value="isDarkTheme">
    <template #checked>
      <n-icon color="#FCE100">
        <dark-icon />
      </n-icon>
    </template>
    <template #unchecked>
      <n-icon>
        <dark-icon />
      </n-icon>
    </template>
  </n-switch>
</template>

<script setup lang="ts">
import { computed } from "vue"
import { useStore } from "vuex"
import { NIcon, NSwitch } from "naive-ui"
import { DarkModeRound as DarkIcon } from "@vicons/material"
import { Mutations } from "@/store"

const store = useStore()

const isDarkTheme = computed<boolean>({
  get: () => {
    return store.state.theme != null
  },
  set: (value) => {
    store.commit(Mutations.CHANGE_THEME, value ? "dark" : "light")
  }
})
</script>
