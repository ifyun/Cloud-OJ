<template>
  <div class="text-logo">
    <router-link :to="{path: '/'}">
      <span>cloud</span>
      <span class="colored">oj</span>
    </router-link>
  </div>
</template>

<script lang="ts">
import {Vue} from "vue-class-component"

export default class Logo extends Vue {
}
</script>

<style scoped lang="scss">
.text-logo {
  font-weight: bold;
  font-size: 28px;

  .colored {
    color: var(--primary-color);
  }
}
</style>