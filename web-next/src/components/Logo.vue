<template>
  <div class="text-logo">
    <router-link :to="{path: '/'}">
      <span>Cloud</span>
      <span class="colored">OJ</span>
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
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  .colored {
    color: var(--primary-color);
  }
}
</style>