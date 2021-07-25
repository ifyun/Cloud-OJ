<template>
  <div id="app">
    <top-navigation v-if="displayNav"/>
    <router-view :key="$route.fullPath">
    </router-view>
  </div>
</template>

<script>
import "@/assets/css/theme.css"
import TopNavigation from "@/components/common/TopNavigation"

export default {
  name: "App",
  components: {
    TopNavigation
  },
  data() {
    return {
      active: "",
      displayNav: false
    }
  },
  watch: {
    $route(to) {
      this.displayNav = to.path !== "/login"
    }
  }
}
</script>

<style scoped>
#app {
  height: 100%;
}
</style>