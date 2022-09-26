<template>
  <div class="text-logo">
    <router-link :to="{ path: '/' }">
      <div v-if="collapsed">
        <span class="colored">C</span>
      </div>
      <div v-else>
        <span>Cloud</span>
        <span class="colored">OJ</span>
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})
</script>

<script lang="ts">
export default {
  name: "AppLogo"
}
</script>

<style scoped lang="scss">
.text-logo {
  font-weight: 500;
  font-size: 26px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  .colored {
    color: var(--primary-color);
  }
}
</style>
