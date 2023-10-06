<template>
  <div
    style="
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
    ">
    <n-icon size="180" depth="2" color="#409EFF">
      <upcoming-round />
    </n-icon>
    <n-text depth="3" style="margin-top: 12px; font-size: 16px">
      什么也找不到...
    </n-text>
  </div>
</template>

<script setup>
import { NText, NIcon } from "naive-ui"
import { UpcomingRound } from "@vicons/material"
</script>
