<template>
  <div id="root">
    <router-view :key="$route.fullPath"/>
  </div>
</template>

<script>
export default {
  name: "Manage"
}
</script>

<style scoped>
#root {
  width: 100%;
  min-width: 1000px;
}
</style>