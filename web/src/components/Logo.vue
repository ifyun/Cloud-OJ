<template>
  <div
    style="
      font-weight: 500;
      font-size: 24px;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: center;
    ">
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
defineOptions({
  name: "AppLogo"
})

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.colored {
  color: var(--primary-color);
}
</style>
