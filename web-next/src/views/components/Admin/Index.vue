<template>
  <router-view v-slot="{Component}" :key="$route.fullPath">
    <keep-alive>
      <component :is="Component"/>
    </keep-alive>
  </router-view>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"

@Options({
  name: "Admin"
})
export default class Admin extends Vue {
}
</script>

<style scoped>

</style>