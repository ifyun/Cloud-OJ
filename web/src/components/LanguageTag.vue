<template>
  <n-flex align="center" :size="4">
    <n-icon size="12" :color="LanguageColors[language]">
      <circle-round />
    </n-icon>
    <n-text
      strong
      depth="2"
      :style="{ width: autoWidth ? 'auto' : '80px', textAlign: 'start' }">
      {{ LanguageNames[language] }}
    </n-text>
  </n-flex>
</template>

<script setup lang="ts">
import { LanguageColors, LanguageNames } from "@/type"
import { CircleRound } from "@vicons/material"
import { NFlex, NIcon, NText } from "naive-ui"

defineProps<{ language: any; autoWidth?: boolean }>()
</script>
