<template>
  <div
    style="
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 50px;
    ">
    <n-button text tag="a" href="https://github.com/ifyun" target="_blank">
      © Cloud
    </n-button>
    <n-divider vertical />
    <n-button
      text
      tag="a"
      href="https://github.com/ifyun/Cloud-OJ"
      target="_blank">
      <template #icon>
        <n-icon size="16">
          <github-icon />
        </n-icon>
      </template>
      GitHub
    </n-button>
    <n-divider vertical />
    <n-button text>MIT License</n-button>
  </div>
</template>

<script setup lang="ts">
import { Github as GithubIcon } from "@vicons/fa"
import { NButton, NDivider, NIcon } from "naive-ui"
</script>
