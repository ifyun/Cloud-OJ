<template>
  <div class="root">
    <div>
      <n-space vertical>
        <n-skeleton width="60%" height="30px" round />
        <n-skeleton width="200px" height="25px" round />
        <n-space size="small">
          <n-skeleton width="70px" height="25px" round />
          <n-skeleton width="100px" height="25px" round />
          <n-skeleton width="100px" height="25px" round />
          <n-skeleton width="100px" height="25px" round />
        </n-space>
        <div>
          <n-skeleton :repeat="3" text round />
          <n-skeleton width="50%" text round />
        </div>
        <n-skeleton width="150px" height="25px" round />
        <div>
          <n-skeleton :repeat="2" text round />
          <n-skeleton width="50%" text round />
        </div>
        <n-skeleton width="150px" height="25px" round />
        <n-skeleton width="100%" height="100px" />
        <n-skeleton width="150px" height="25px" round />
        <n-skeleton width="100%" height="120px" />
        <div>
          <n-skeleton :repeat="2" text round />
          <n-skeleton width="50%" text round />
        </div>
      </n-space>
    </div>
    <div>
      <div style="height: 100%; display: flex; flex-direction: column">
        <n-skeleton width="100%" height="30px" />
        <n-skeleton width="100%" height="100%" style="margin-top: 5px" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { NSkeleton, NSpace } from "naive-ui"
</script>

<style scoped lang="scss">
.root {
  height: 100%;
  display: flex;
  flex-direction: row;

  & > * {
    flex: 1;
    margin-left: 12px;

    &:first-child {
      margin-left: 0;
    }
  }
}
</style>
