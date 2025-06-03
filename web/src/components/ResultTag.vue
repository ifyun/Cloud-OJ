<template>
  <n-tag size="small" :type="type" :bordered="false">
    <template #icon>
      <n-icon :component="icon" />
    </template>
    {{ text }}
  </n-tag>
</template>

<script setup lang="ts">
import { NIcon, NTag } from "naive-ui"
import { type Component, onBeforeMount, ref, shallowRef } from "vue"
import { CheckCircleFilled, ErrorRound, TimelapseRound } from "@vicons/material"
import { JudgeResult } from "@/api/type"
import { ResultTypes } from "@/type"

const props = defineProps<{
  data: JudgeResult
}>()

const type = ref<any>("default")
const text = ref<string>()
const icon = shallowRef<Component>()

onBeforeMount(() => {
  const { state, result, stateText, resultText } = props.data

  if (state === 0) {
    type.value = ResultTypes[result!]
    text.value = resultText
  } else {
    type.value = "info"
    text.value = stateText
  }

  if (state !== 0) {
    icon.value = TimelapseRound
  } else if (result === 0) {
    icon.value = CheckCircleFilled
  } else {
    icon.value = ErrorRound
  }
})
</script>
