<template>
  <div class="text-logo">
    <router-link :to="{path: '/'}">
      <span>Cloud</span>
      <span class="colored">OJ</span>
    </router-link>
  </div>
</template>

<script>
export default {
  name: "AppLogo"
}
</script>

<style scoped lang="scss">
.text-logo {
  font-weight: bold;
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
